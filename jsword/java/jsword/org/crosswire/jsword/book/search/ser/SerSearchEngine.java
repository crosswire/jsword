
package org.crosswire.jsword.book.search.ser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookUtil;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.Parser;
import org.crosswire.jsword.book.search.ParserFactory;
import org.crosswire.jsword.book.search.SearchEngine;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.Verse;

/**
 * A search engine - This is a stepping stone on the way to allowing use of
 * Lucene in place of our search engine.
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
public class SerSearchEngine implements SearchEngine, Index
{
    /**
     * Constructor for SerSearchEngine.
     */
    public void init(Bible newbible, URL newurl) throws BookException
    {
        try
        {
            this.bible = newbible;
            this.url = newurl;

            isindexed = isIndexed();
            
            URL dataurl = NetUtil.lengthenURL(newurl, "ref.data");

            if (isindexed)
            {
                URL indexurl = NetUtil.lengthenURL(newurl, "ref.index");

                // We don't need to create any indexes, they just need loading
                indexin = new BufferedReader(new InputStreamReader(indexurl.openStream()));

                // Open the Passage RAF
                dataraf = new RandomAccessFile(NetUtil.getAsFile(dataurl), "r");
            }
            else
            {
                // Open the Passage RAF
                dataraf = new RandomAccessFile(NetUtil.getAsFile(dataurl), "rw");
            }
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.INITIALIZE, ex);
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
            loadIndexes();
            // indexin.close();
        }
        else
        {
            Reporter.informUser(this, Msg.TYPE_INDEXGEN.getName());

            // The index is usable but incomplete, so kick off a generation
            // thread if there is not one running already.
            if (job != null)
            {
                Thread work = new Thread(new IndexerRunnable());
                work.start();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#deactivate()
     */
    public void deactivate()
    {
        datamap.clear();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#findPassage(org.crosswire.jsword.book.Search)
     */
    public Passage findPassage(Search search) throws BookException
    {
        try
        {
            Parser parser = ParserFactory.createParser(this);
            return parser.search(search);
        }
        catch (InstantiationException ex)
        {
            throw new BookException(Msg.SEARCH_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#delete()
     */
    public void delete() throws BookException
    {
        // NOTE(joe): write delete()
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#getStartsWith(java.lang.String)
     */
    public Iterator getStartsWith(String word) throws BookException
    {
        word = word.toLowerCase();
        SortedMap sub_map = datamap.subMap(word, word + "\u9999");
        return sub_map.keySet().iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#findWord(java.lang.String)
     */
    public Passage findWord(String word) throws BookException
    {
        if (word == null)
        {
            return PassageFactory.createPassage();
        }

        Section section = (Section) datamap.get(word.toLowerCase());

        if (section == null)
        {
            return PassageFactory.createPassage();
        }

        try
        {
            // Read blob
            byte[] blob = new byte[section.length];
            dataraf.seek(section.offset);
            int read = dataraf.read(blob);

            // Probably a bit harsh, but it would be wrong to just drop it.
            if (read != blob.length)
            {
                throw new IOException();
            }

            // De-serialize
            return PassageUtil.fromBinaryRepresentation(blob);
        }
        catch (Exception ex)
        {
            log.warn("Search failed on:");
            log.warn("  word=" + word);
            log.warn("  ref_ptr=" + section.offset);
            log.warn("  ref_length=" + section.length);
            Reporter.informUser(this, ex);

            return PassageFactory.createPassage();
        }
    }

    /**
     * Detects if index data has been stored for this Bible already
     */
    private boolean isIndexed() throws IOException
    {
        URL ref_idy_url = NetUtil.lengthenURL(url, "ref.index");
        return NetUtil.isFile(ref_idy_url);
    }

    /**
     * Loads the index files from disk ready for searching
     */
    private void loadIndexes()
    {
        while (true)
        {
            String line = null;

            try
            {
                line = indexin.readLine();
            }
            catch (IOException ex)
            {
                log.error("Read failed on indexin", ex);
                break;
            }

            if (line == null)
            {
                break;
            }

            try
            {
                int colon1 = line.indexOf(":");
                int colon2 = line.lastIndexOf(":");
                String word = line.substring(0, colon1);

                long offset = Long.parseLong(line.substring(colon1 + 1, colon2));
                int length = Integer.parseInt(line.substring(colon2 + 1));

                Section section = new Section(offset, length);
                datamap.put(word, section);
            }
            catch (NumberFormatException ex)
            {
                log.error("NumberFormatException reading line: "+line, ex);
            }
        }
    }

    /**
     * Read from the given source version to generate ourselves
     */
    protected void generateSearchIndex(Job ajob) throws BookException
    {
        // create a word/passage hashmap
        Map matchmap = new HashMap();

        // If we get an error reading a verse dont give up straight away
        int errors = 0;

        // loop through all the verses
        int percent = -1;
        for (Iterator it = WHOLE.verseIterator(); it.hasNext();)
        {
            Verse verse = (Verse) it.next();
            try
            {
                int newpercent = PERCENT_READ * verse.getOrdinal() / BibleInfo.versesInBible();
                if (percent != newpercent)
                {
                    percent = newpercent;
                    ajob.setProgress(percent, "Finding Words ("+verse.getName()+")");
                }

                // loop through all the words in this verse
                Passage current = PassageFactory.createPassage();
                current.add(verse);
                String text = bible.getData(current).getPlainText();
                String[] words = BookUtil.getWords(text);
                for (int i = 0; i < words.length; i++)
                {
                    // ensure there is a Passage for this word in the word/passage hashmap
                    Passage matches = (Passage) matchmap.get(words[i]);
                    if (matches == null)
                    {
                        matches = PassageFactory.createPassage();
                        matchmap.put(words[i], matches);
                    }

                    // add this verse to this words passage
                    matches.add(verse);
                }

                // This could take a long time ...
                Thread.yield();
                if (Thread.currentThread().isInterrupted())
                {
                    break;
                }
            }
            catch (Exception ex)
            {
                errors++;
                log.error("Error reading "+verse.getName()+" in "+bible.getBibleMetaData().getFullName()+": errors="+errors, ex);
                if (errors > MAX_ERRORS)
                {
                    if (ex instanceof BookException)
                    {
                        throw (BookException) ex;
                    }
                    else
                    {
                        throw new BookException(Msg.READ_ERROR, ex);
                    }
                }
            }
        }

        // For the progress listener
        int count = 0;
        int words = matchmap.size();

        // Now we need to write the words into our index
        for (Iterator it = matchmap.keySet().iterator(); it.hasNext();)
        {
            String word = (String) it.next();
            Passage match = (Passage) matchmap.get(word);
            recordFoundPassage(word, match);

            // Fire a progress event?
            int newpercent = PERCENT_READ + (PERCENT_WRITE * count++ / words) / BibleInfo.versesInBible();
            if (percent != newpercent)
            {
                percent = newpercent;
                ajob.setProgress(percent, "Writing Words ("+word+")");
            }

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
            {
                break;
            }
        }
    }

    /**
     * Add to the main index data the references against this word
     * @param word The word to write
     * @param ref The references to the word
     */
    private void recordFoundPassage(String word, Passage ref) throws BookException
    {
        if (word == null)
        {
            return;
        }

        try
        {
            byte[] buffer = PassageUtil.toBinaryRepresentation(ref);

            Section section = new Section(dataraf.getFilePointer(), buffer.length);

            dataraf.write(buffer);
            datamap.put(word.toLowerCase(), section);
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }
    }

    /**
     * Write the indexes to disk
     */
    protected void saveIndexes(Job ajob) throws BookException
    {
        // Store the indexes on disk
        try
        {
            ajob.setProgress(PERCENT_READ + PERCENT_WRITE, "Saving Index");

            // Save the ascii Passage index
            URL indexurl = NetUtil.lengthenURL(url, "ref.index");
            PrintWriter indexout = new PrintWriter(NetUtil.getOutputStream(indexurl));
            Iterator it = datamap.keySet().iterator();
            while (it.hasNext())
            {
                String word = (String) it.next();
                Section section = (Section) datamap.get(word);
                indexout.println(word + ":" + section.offset + ":" + section.length);
            }
            indexout.close();
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }
    }

    /**
     * Have we generated/read the search index
     */
    private boolean isindexed;

    /**
     * The Bible we are indexing
     */
    protected Bible bible;

    /**
     * The directory to which to write the index
     */
    private URL url;

    /**
     * The passages random access file
     */
    private RandomAccessFile dataraf;

    /**
     * The index to the random access file
     */
    private BufferedReader indexin;

    /**
     * The hash of indexes into the passages file
     */
    private SortedMap datamap = new TreeMap();

    /**
     * The job used when generating a search index
     */
    protected Job job = null;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SerSearchEngine.class);

    /**
     * The percentages taken but by different parts
     */
    private static final int PERCENT_READ = 60;
    private static final int PERCENT_WRITE = 39;
    // private static final int PERCENT_INDEX = 1;

    /**
     * When generating the index, how many tries before we give up?
     */
    private static final int MAX_ERRORS = 256;

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
                generateSearchIndex(job);
                saveIndexes(job);
                job.done();
                job = null;
            }
            catch (BookException ex)
            {
                Reporter.informUser(SerSearchEngine.this, ex);
            }
        }
    }

    /**
     * A simple class to hold an offset and length into the passages random
     * access file
     */
    public static class Section
    {
        protected Section(long offset, int length)
        {
            this.offset = offset;
            this.length = length;
        }

        protected long offset;
        protected int length;
    }
}
