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
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.search.SearchEngine;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;

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
public class LuceneSearchEngine implements SearchEngine
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#init(org.crosswire.jsword.book.Bible, java.net.URL)
     */
    public void init(Bible newbible, URL newurl) throws BookException
    {
        try
        {
            url = NetUtil.lengthenURL(newurl, "lucene");
            bible = newbible;

            isindexed = isIndexed();
            if (isindexed)
            {
                // Opening Lucene indexes is quite quick I think, so we can try
                // it to see if it works to report errors that we want to drop
                // later
                loadIndexes();
            }
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.LUCENE_INIT, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#activate()
     */
    public void activate()
    {
        // Load the ascii Passage index
        if (isindexed)
        {
            try
            {
                loadIndexes();
            }
            catch (IOException ex)
            {
                log.warn("second load failure", ex);
            }
        }
        else
        {
            Reporter.informUser(this, Msg.TYPE_INDEXGEN.getName());

            // The index is usable but incomplete, so kick off a generation
            // thread if there is not one running already.
            if (job == null)
            {
                Thread work = new Thread(new IndexerRunnable());
                work.start();
            }
            else
            {
                log.warn("activate() called while job in progress", new Exception());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#deactivate()
     */
    public void deactivate()
    {
        try
        {
            unloadIndexes();
        }
        catch (IOException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#findPassage(org.crosswire.jsword.book.Search)
     */
    public Passage findPassage(Search search) throws BookException
    {
        // TODO: think about splitting out the parser.
        /*
        Parser parser = ParserFactory.createParser(this);
        return parser.search(search);
        */

        try
        {
            Analyzer analyzer = new StandardAnalyzer();
            Query query = QueryParser.parse(search.getMatch(), FIELD_BODY, analyzer);
            Hits hits = searcher.search(query);

            PassageTally tally = new PassageTally();
            for (int i = 0; i < hits.length(); i++)
            {
                Verse verse = new Verse(hits.doc(i).get(FIELD_NAME));
                int score = (int) (hits.score(i) * 100);
                tally.add(verse, score);
            }

            return tally;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.SEARCH_FAILED, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#getStartsWith(java.lang.String)
     *
    public Iterator getStartsWith(String word) throws BookException
    {
        // TODO: we could probably implement this, but only if we can split the parser out
    }
    */

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#findWord(java.lang.String)
     *
    public Passage findWord(String word) throws BookException
    {
        // TODO: we could probably implement this, but only if we can split the parser out
    }
    */

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#delete()
     */
    public void delete() throws BookException
    {
        // TODO: write this
        /*
        Directory directory = FSDirectory.getDirectory("demo index", false);
        IndexReader reader = IndexReader.open(directory);

        //       Term term = new Term("path", "pizza");
        //       int deleted = reader.delete(term);

        //       System.out.println("deleted " + deleted +
        //           " documents containing " + term);

        for (int i = 0; i < reader.maxDoc(); i++)
            reader.delete(i);

        reader.close();
        directory.close();
        */
    }

    /**
     * Detects if index data has been stored for this Bible already
     */
    private boolean isIndexed()
    {
        URL index = NetUtil.lengthenURL(url, "segments");
        return NetUtil.isFile(index);
    }

    /**
     * Create an index
     */
    protected void generateSearchIndex() throws IOException, BookException
    {
        // An index is created by opening an IndexWriter with the
        // create argument set to true.
        IndexWriter writer = new IndexWriter(NetUtil.getAsFile(url), new StandardAnalyzer(), true);

        int percent = -1;
        for (Iterator it = WHOLE.verseIterator(); it.hasNext();)
        {
            Verse verse = (Verse) it.next();
            Key key = bible.getKey(verse.getName());
            BookData data = bible.getData(key);
            Reader reader = new StringReader(data.getPlainText());

            Document doc = new Document();
            doc.add(Field.Text(FIELD_NAME, verse.getName()));
            doc.add(Field.Text(FIELD_BODY, reader));

            writer.addDocument(doc);

            // report progress
            int newpercent = 95 * verse.getOrdinal() / BibleInfo.versesInBible();
            if (percent != newpercent)
            {
                percent = newpercent;
                job.setProgress(percent, "Indexing verse: "+verse.getName());
            }
        }

        job.setProgress(percent, "Optimizing");

        writer.optimize();
        writer.close();
    }

    /**
     * Load a previously generated index
     */
    protected void loadIndexes() throws IOException
    {
        searcher = new IndexSearcher(NetUtil.getAsFile(url).getCanonicalPath());
    } 

    /**
     * Frees the memory in a previously loaded index
     */
    private void unloadIndexes() throws IOException
    {
        searcher.close();
        searcher = null;
    }

    /**
     * The Lucene field for the verse name
     */
    private static final String FIELD_NAME = "name";

    /**
     * The Lucene field for the verse contents
     */
    private static final String FIELD_BODY = "body";

    /**
     * Has the index been created
     */
    protected boolean isindexed;

    /**
     * The Bible that we are indexing
     */
    protected Bible bible;

    /**
     * The location of this index
     */
    private URL url;

    /**
     * The Lucene search engine
     */
    private Searcher searcher;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LuceneSearchEngine.class);

    /**
     * The job used when generating a search index
     */
    protected Job job = null;

    /**
     * The Whole Bible
     */
    private static final Passage WHOLE = PassageFactory.getWholeBiblePassage();

    /**
     * The index creation thread
     */
    private class IndexerRunnable implements Runnable
    {
        public void run()
        {
            try
            {
                job = JobManager.createJob("Indexing "+bible.getBibleMetaData().getName(), Thread.currentThread(), false);
                generateSearchIndex();
                job.done();
                job = null;

                isindexed = true;
                loadIndexes();
            }
            catch (Exception ex)
            {
                Reporter.informUser(LuceneSearchEngine.this, ex);
            }
        }
    }
}
