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
 * ID: $Id$
 */
package org.crosswire.jsword.book.search.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.crosswire.common.activate.Activatable;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.IndexStatus;
import org.crosswire.jsword.book.search.SearchModifier;
import org.crosswire.jsword.book.search.basic.AbstractIndex;
import org.crosswire.jsword.passage.AbstractPassage;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;
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
    public LuceneIndex(Book book, URL storage) throws BookException
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
    public LuceneIndex(Book book, URL storage, boolean create) throws BookException
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

        Job job = JobManager.createJob(Msg.INDEX_START.toString(), Thread.currentThread(), false);

        IndexStatus finalStatus = IndexStatus.UNDONE;
        try
        {
            synchronized (CREATING)
            {
                book.setIndexStatus(IndexStatus.CREATING);
                File tempPath = new File(path + '.' + IndexStatus.CREATING.toString());

                // An index is created by opening an IndexWriter with the
                // create argument set to true.
                IndexWriter writer = new IndexWriter(tempPath.getCanonicalPath(), new SimpleAnalyzer(), true);

                List errors = new ArrayList();
                generateSearchIndexImpl(job, errors, writer, book.getGlobalKeyList());

                job.setProgress(95, Msg.OPTIMIZING.toString());

                writer.optimize();
                writer.close();

                job.setInterruptable(false);
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
        catch (Exception ex)
        {
            job.ignoreTimings();
            throw new BookException(Msg.LUCENE_INIT, ex);
        }
        finally
        {
            book.setIndexStatus(finalStatus);
            job.done();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#findWord(java.lang.String)
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

                Analyzer analyzer = new SimpleAnalyzer();
                Query query = QueryParser.parse(search, LuceneIndex.FIELD_BODY, analyzer);
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
                        Verse verse = VerseFactory.fromString(hits.doc(i).get(LuceneIndex.FIELD_NAME));
                        // PassageTally understands a score of 0 as the verse not participating
                        int score = (int) (hits.score(i) * 100 + 1);
                        tally.add(verse, score);
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
                        Verse verse = VerseFactory.fromString(hits.doc(i).get(LuceneIndex.FIELD_NAME));
                        results.addAll(verse);
                    }
                    if (passage != null)
                    {
                        passage.lowerNormalizeProtection();
                        passage.lowerEventSuppresionAndTest();
                    }
                }
            }
            catch (Exception ex)
            {
                throw new BookException(Msg.SEARCH_FAILED, ex);
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
     * @see org.crosswire.jsword.book.search.Index#getKey(java.lang.String)
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
    private void generateSearchIndexImpl(Job job, List errors, IndexWriter writer, Key key) throws BookException, IOException
    {
        int bookNum = 0;
        int oldBookNum = -1;
        int percent = 0;
        String name = ""; //$NON-NLS-1$
        String text = ""; //$NON-NLS-1$
        BookData data = null;
        Key subkey = null;
        Verse verse = null;
        Document doc = null;
        for (Iterator it = key.iterator(); it.hasNext(); )
        {
            subkey = (Key) it.next();
            if (subkey.canHaveChildren())
            {
                generateSearchIndexImpl(job, errors, writer, subkey);
            }
            else
            {
                data = null;
                try
                {
                    data = book.getData(subkey);
                }
                catch (BookException e)
                {
                    errors.add(subkey);
                    continue;
                }

                text = data.getVerseText();

                // Do the actual indexing
                if (text != null && text.length() > 0)
                {
                    doc = new Document();
                    doc.add(Field.UnIndexed(FIELD_NAME, subkey.getOSISName()));
                    doc.add(Field.Text(FIELD_BODY, new StringReader(text)));
                    writer.addDocument(doc);
                }

                // report progress
                verse = KeyUtil.getVerse(subkey);

                try
                {
                    percent = 95 * verse.getOrdinal() / BibleInfo.versesInBible();
                    bookNum = verse.getBook();
                    if (oldBookNum != bookNum)
                    {
                        name = BibleInfo.getBookName(bookNum);
                        oldBookNum = bookNum;
                    }
                }
                catch (NoSuchVerseException ex)
                {
                    log.error("Failed to get book name from verse: " + verse, ex); //$NON-NLS-1$
                    assert false;
                    name = subkey.getName();
                }

                job.setProgress(percent, Msg.INDEXING.toString(name));

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
     * The Lucene field for the verse name
     */
    protected static final String FIELD_NAME = "key"; //$NON-NLS-1$

    /**
     * The Lucene field for the verse contents
     */
    protected static final String FIELD_BODY = "content"; //$NON-NLS-1$

    /**
     * The Lucene field for the strong numbers
     */
    protected static final String FIELD_STRONG = "strong"; //$NON-NLS-1$

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
