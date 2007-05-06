/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id:LuceneIndex.java 984 2006-01-23 14:18:33 -0500 (Mon, 23 Jan 2006) dmsmith $
 */
package org.crosswire.jsword.index.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.crosswire.common.activate.Activatable;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.index.AbstractIndex;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.search.SearchModifier;
import org.crosswire.jsword.passage.AbstractPassage;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.VerseFactory;

/**
 * Implement the SearchEngine using Lucene as the search engine.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]

 */
public class LuceneIndex extends AbstractIndex implements Activatable
{
    /**
     * Read an existing index and use it.
     * @throws BookException If we fail to read the index files
     */
    public LuceneIndex(Book book, URI storage) throws BookException
    {
        this.book = book;

        try
        {
            this.path = NetUtil.getAsFile(storage).getCanonicalPath();
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.LUCENE_INIT, ex);
        }
    }

    /**
     * Generate an index to use, telling the job about progress as you go.
     * @throws BookException If we fail to read the index files
     */
    public LuceneIndex(Book book, URI storage, boolean create) throws BookException
    {
        assert create;

        this.book = book;
        File finalPath = null;
        try
        {
            finalPath = NetUtil.getAsFile(storage);
            this.path = finalPath.getCanonicalPath();
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.LUCENE_INIT, ex);
        }

        Progress job = JobManager.createJob(Msg.INDEX_START.toString(), Thread.currentThread(), false);

        IndexStatus finalStatus = IndexStatus.UNDONE;
        Analyzer analyzer = new LuceneAnalyzer();
        List errors = new ArrayList();
        File tempPath = new File(path + '.' + IndexStatus.CREATING.toString());

        try
        {
            synchronized (CREATING)
            {

                book.setIndexStatus(IndexStatus.CREATING);

                // An index is created by opening an IndexWriter with the
                // create argument set to true.
                //IndexWriter writer = new IndexWriter(tempPath.getCanonicalPath(), analyzer, true);

                // Create the index in core.
                RAMDirectory ramDir = new RAMDirectory();
                IndexWriter  writer = new IndexWriter(ramDir, analyzer, true);

                generateSearchIndexImpl(job, errors, writer, book.getGlobalKeyList(), 0);

                job.setSectionName(Msg.OPTIMIZING.toString());
                job.setWork(95);

                // Consolidate the index into the minimum number of files.
                // writer.optimize(); /* Optimize is done by addIndexes */
                writer.close();

                // Write the core index to disk.
                IndexWriter fsWriter = new IndexWriter(tempPath.getCanonicalPath(), analyzer, true);
                fsWriter.addIndexes(new Directory[] { ramDir });
                fsWriter.close();

                // Free up the space used by the ram directory
                ramDir.close();

                job.setCancelable(false);
                if (!job.isFinished())
                {
                    tempPath.renameTo(finalPath);
                }

                if (finalPath.exists())
                {
                    finalStatus = IndexStatus.DONE;
                }

                if (errors.size() > 0)
                {
                    StringBuffer buf = new StringBuffer();
                    Iterator iter = errors.iterator();
                    while (iter.hasNext())
                    {
                        buf.append(iter.next());
                        buf.append('\n');
                    }
                    Reporter.informUser(this, Msg.BAD_VERSE, buf);
                }

            }
        }
        catch (IOException ex)
        {
            job.cancel();
            throw new BookException(Msg.LUCENE_INIT, ex);
        }
        finally
        {
            book.setIndexStatus(finalStatus);
            job.done();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.search.Index#findWord(java.lang.String)
     */
    public Key find(String search) throws BookException
    {
        checkActive();

        SearchModifier modifier = getSearchModifier();
        Key results = null;

        if (search != null)
        {
            try
            {

                Analyzer analyzer = new LuceneAnalyzer();
                QueryParser parser = new QueryParser(LuceneIndex.FIELD_BODY, analyzer);
                Query query = parser.parse(search);
                Hits hits = searcher.search(query);

                // For ranking we use a PassageTally
                if (modifier != null && modifier.isRanked())
                {
                    PassageTally tally = new PassageTally();
                    tally.raiseEventSuppresion();
                    tally.raiseNormalizeProtection();
                    results = tally;
                    for (int i = 0; i < hits.length(); i++)
                    {
                        Key key = VerseFactory.fromString(hits.doc(i).get(LuceneIndex.FIELD_KEY));
                        // PassageTally understands a score of 0 as the verse not participating
                        int score = (int) (hits.score(i) * 100 + 1);
                        tally.add(key, score);
                    }
                    tally.lowerNormalizeProtection();
                    tally.lowerEventSuppresionAndTest();
                }
                else
                {
                    results = book.createEmptyKeyList();
                    // If we have an abstract passage,
                    // make sure it does not try to fire change events.
                    AbstractPassage passage = null;
                    if (results instanceof AbstractPassage)
                    {
                        passage = (AbstractPassage) results;
                        passage.raiseEventSuppresion();
                        passage.raiseNormalizeProtection();
                    }
                    for (int i = 0; i < hits.length(); i++)
                    {
                        Key key = VerseFactory.fromString(hits.doc(i).get(LuceneIndex.FIELD_KEY));
                        results.addAll(key);
                    }
                    if (passage != null)
                    {
                        passage.lowerNormalizeProtection();
                        passage.lowerEventSuppresionAndTest();
                    }
                }
            }
            catch (IOException e)
            {
                throw new BookException(Msg.SEARCH_FAILED, e);
            }
            catch (NoSuchVerseException e)
            {
                throw new BookException(Msg.SEARCH_FAILED, e);
            }
            catch (ParseException e)
            {
                throw new BookException(Msg.SEARCH_FAILED, e);
            }
            finally
            {
                Activator.deactivate(this);
            }
        }

        if (results == null)
        {
            if (modifier != null && modifier.isRanked())
            {
                results = new PassageTally();
            }
            else
            {
                results = book.createEmptyKeyList();
            }
        }
        return results;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.search.Index#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException
    {
        return book.getKey(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        try
        {
            searcher = new IndexSearcher(path);
        }
        catch (IOException ex)
        {
            log.warn("second load failure", ex); //$NON-NLS-1$
        }

        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
        try
        {
            searcher.close();
            searcher = null;
        }
        catch (IOException ex)
        {
            Reporter.informUser(this, ex);
        }

        active = false;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive()
    {
        if (!active)
        {
            Activator.activate(this);
        }
    }

    /**
     * Dig down into a Key indexing as we go.
     */
    private void generateSearchIndexImpl(Progress job, List errors, IndexWriter writer, Key key, int count) throws BookException, IOException
    {
        boolean hasStrongs = book.getBookMetaData().hasFeature(FeatureType.STRONGS_NUMBERS);
        boolean hasXRefs = book.getBookMetaData().hasFeature(FeatureType.SCRIPTURE_REFERENCES);

        String oldRootName = ""; //$NON-NLS-1$
        int percent = 0;
        String rootName = ""; //$NON-NLS-1$
        String text = ""; //$NON-NLS-1$
        String strongs = ""; //$NON-NLS-1$
        String xrefs = ""; //$NON-NLS-1$
        BookData data = null;
        Key subkey = null;
        Document doc = null;
        int size = key.getCardinality();
        int subCount = count;
        for (Iterator it = key.iterator(); it.hasNext(); )
        {
            subkey = (Key) it.next();
            if (subkey.canHaveChildren())
            {
                generateSearchIndexImpl(job, errors, writer, subkey, subCount);
            }
            else
            {
                data = null;
                try
                {
                    data = book.getText(subkey);
                }
                catch (BookException e)
                {
                    errors.add(subkey);
                    continue;
                }

                text = data.getCanonicalText();
                if (hasStrongs)
                {
                    strongs = data.getStrongsNumbers();
                }

                // Do the actual indexing
                if (text != null && text.length() > 0)
                {
                    doc = new Document();
                    doc.add(new Field(FIELD_KEY, subkey.getOsisRef(), Field.Store.YES, Field.Index.UN_TOKENIZED));
                    doc.add(new Field(FIELD_BODY, new StringReader(text)));

                    if (hasStrongs)
                    {
                        strongs = data.getStrongsNumbers();
                        if (strongs != null && strongs.length() > 0)
                        {
                            doc.add(new Field(FIELD_STRONG, strongs, Field.Store.NO, Field.Index.TOKENIZED));
                        }
                    }

                    if (hasXRefs)
                    {
                        xrefs = data.getReferences();
                        if (xrefs != null && xrefs.length() > 0)
                        {
                            doc.add(new Field(FIELD_XREF, xrefs, Field.Store.NO, Field.Index.TOKENIZED));
                        }
                    }

                    writer.addDocument(doc);
                }

                // report progress
                rootName = subkey.getRootName();
                if (!rootName.equals(oldRootName))
                {
                    oldRootName = rootName;
                    job.setSectionName(Msg.INDEXING.toString(rootName));
                }

                subCount++;
                percent = 95 * subCount / size;

                job.setWork(percent);

                // This could take a long time ...
                Thread.yield();
                if (Thread.currentThread().isInterrupted())
                {
                    break;
                }
            }
        }
    }

    /**
     * A synchronization lock point to prevent us from doing 2 index runs at a time.
     */
    private static final Object CREATING = new Object();

    /**
     * Are we active
     */
    private boolean active;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LuceneIndex.class);

    /* The following fields are named the same as Sword in the hopes of
     * sharing indexes.
     */
    /**
     * The Lucene field for the osisID
     */
    protected static final String FIELD_KEY = "key"; //$NON-NLS-1$

    /**
     * The Lucene field for the text contents
     */
    protected static final String FIELD_BODY = "content"; //$NON-NLS-1$

    /**
     * The Lucene field for the strong numbers
     */
    protected static final String FIELD_STRONG = "strong"; //$NON-NLS-1$

    /**
     * The Lucene field for cross references
     */
    protected static final String FIELD_XREF = "xref"; //$NON-NLS-1$

    /**
     * The Lucene field for notes
     */
    protected static final String FIELD_NOTES = "note"; //$NON-NLS-1$

    /**
     * The Book that we are indexing
     */
    protected Book book;

    /**
     * The location of this index
     */
    private String path;

    /**
     * The Lucene search engine
     */
    protected Searcher searcher;
}
