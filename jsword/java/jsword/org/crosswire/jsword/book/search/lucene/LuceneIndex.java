package org.crosswire.jsword.book.search.lucene;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.IndexManager;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.crosswire.jsword.util.Project;

/**
 * Implement the SearchEngine using Lucene as the search engine.
 *
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class LuceneIndex implements Index, Activatable
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#init(org.crosswire.jsword.book.Bible, java.net.URL)
     */
    public void init(Book newBook) throws BookException
    {
        try
        {
            book = newBook;

            String driverName = book.getBookMetaData().getDriverName();
            String bookName = book.getBookMetaData().getInitials();

            assert driverName != null;
            assert bookName != null;

            URL base = Project.instance().getTempScratchSpace(DIR_LUCENE, false);
            URL driver = NetUtil.lengthenURL(base, driverName);
            url = NetUtil.lengthenURL(driver, bookName);

            if (isIndexed())
            {
                // Opening Lucene indexes is quite quick I think, so we can try
                // it to see if it works to report errors that we want to drop
                // later
                searcher = new IndexSearcher(NetUtil.getAsFile(url).getCanonicalPath());
            }
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.LUCENE_INIT, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#findKeyList(org.crosswire.jsword.book.Search)
     */
    public Key findWord(String search) throws BookException
    {
        checkActive();

        PassageTally tally = new PassageTally();

        if (search != null)
        {
            if (searcher != null)
            {
                try
                {
                    Analyzer analyzer = new StandardAnalyzer();
                    Query query = QueryParser.parse(search, LuceneIndex.FIELD_BODY, analyzer);
                    Hits hits = searcher.search(query);
    
                    for (int i = 0; i < hits.length(); i++)
                    {
                        Verse verse = VerseFactory.fromString(hits.doc(i).get(LuceneIndex.FIELD_NAME));
                        int score = (int) (hits.score(i) * 100);
                        tally.add(verse, score);
                    }
                }
                catch (Exception ex)
                {
                    throw new BookException(Msg.SEARCH_FAILED, ex);
                }
            }
            else
            {
                log.warn("Missing searcher, skipping search for: "+search); //$NON-NLS-1$
            }
        }

        return tally;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException
    {
        return book.getKey(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#delete()
     */
    public void delete() throws BookException
    {
        checkActive();

        try
        {
            NetUtil.delete(url);
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.DELETE_FAILED, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractIndex#isIndexed()
     */
    public boolean isIndexed()
    {
        if (generating)
        {
            return false;
        }

        URL longer = NetUtil.lengthenURL(url, DIR_SEGMENTS);
        return NetUtil.isFile(longer);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractIndex#generateSearchIndex(org.crosswire.common.progress.Job)
     */
    public void generateSearchIndex(Job job) throws IOException, BookException
    {
        // An index is created by opening an IndexWriter with the
        // create argument set to true.
        IndexWriter writer = new IndexWriter(NetUtil.getAsFile(url), new StandardAnalyzer(), true);

        generateSearchIndexImpl(job, writer, book.getGlobalKeyList());

        job.setProgress(95, Msg.OPTIMIZING.toString());

        writer.optimize();
        writer.close();

        searcher = new IndexSearcher(NetUtil.getAsFile(url).getCanonicalPath());
    }

    /**
     * Dig down into a Key indexing as we go.
     */
    private void generateSearchIndexImpl(Job job, IndexWriter writer, Key key) throws BookException, IOException
    {
        int percent = 0;
        for (Iterator it = key.iterator(); it.hasNext(); )
        {
            Key subkey = (Key) it.next();
            if (subkey.canHaveChildren())
            {
                generateSearchIndexImpl(job, writer, subkey);
            }
            else
            {
                BookData data = book.getData(subkey);
                Reader reader = new StringReader(data.getPlainText());

                Document doc = new Document();
                doc.add(Field.Text(FIELD_NAME, subkey.getName()));
                doc.add(Field.Text(FIELD_BODY, reader));

                writer.addDocument(doc);

                // report progress
                String name = ""; //$NON-NLS-1$
                Verse verse = KeyUtil.getVerse(subkey);

                try
                {
                    percent = 95 * verse.getOrdinal() / BibleInfo.versesInBible();
                    name = BibleInfo.getLongBookName(verse.getBook());
                }
                catch (NoSuchVerseException ex)
                {
                    log.error("Failed to get book name from verse: "+verse, ex); //$NON-NLS-1$
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#activate()
     */
    public final void activate(Lock lock)
    {
        // Load the ascii Passage index
        if (isIndexed())
        {
            try
            {
                searcher = new IndexSearcher(NetUtil.getAsFile(url).getCanonicalPath());
            }
            catch (IOException ex)
            {
                log.warn("second load failure", ex); //$NON-NLS-1$
            }
        }
        else
        {
            IndexManager.instance().createIndex(this);
        }

        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#deactivate()
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
     * Are we active
     */
    private boolean active = false;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LuceneIndex.class);

    /**
     * Are we in the middle of generating an index?
     */
    private boolean generating = false;

    /**
     * The lucene search index directory
     */
    protected static final String DIR_LUCENE = "lucene"; //$NON-NLS-1$

    /**
     * The segments directory
     */
    protected static final String DIR_SEGMENTS = "segments"; //$NON-NLS-1$

    /**
     * The Lucene field for the verse name
     */
    protected static final String FIELD_NAME = "name"; //$NON-NLS-1$

    /**
     * The Lucene field for the verse contents
     */
    protected static final String FIELD_BODY = "body"; //$NON-NLS-1$

    /**
     * The Book that we are indexing
     */
    protected Book book;

    /**
     * The location of this index
     */
    private URL url;

    /**
     * The Lucene search engine
     */
    protected Searcher searcher;
}
