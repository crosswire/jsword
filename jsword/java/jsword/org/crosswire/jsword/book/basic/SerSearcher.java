
package org.crosswire.jsword.book.basic;

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

import org.apache.log4j.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookUtil;
import org.crosswire.jsword.book.events.ProgressEvent;
import org.crosswire.jsword.book.events.ProgressListener;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.util.Project;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class SerSearcher implements Searcher
{
    /**
     * Constructor for SerSearcher.
     */
    public SerSearcher(Bible bible, ProgressListener li) throws BookException
    {
        try
        {
            this.bible = bible;
            this.url = Project.resource().getTempScratchSpace(bible.getBibleMetaData().getFullName());
    
            // Load the ascii Passage index
            if (isIndexed(url))
            {
                loadIndexes(url);
            }
            else
            {
                createEmptyIndex(url);
                generateSearchIndex(li);
                saveIndexes();
            }
        }
        catch (IOException ex)
        {
            throw new BookException("ser_init", ex);
        }
    }

    /**
     * Retrieval: Return an array of words that are used by this Bible
     * that start with the given string. For example calling:
     * <code>getStartsWith("love")</code> will return something like:
     * { "love", "loves", "lover", "lovely", ... }
     * @param base The word to base your word array on
     * @return An array of words starting with the base
     */
    public Iterator getStartsWith(String word) throws BookException
    {
        word = word.toLowerCase();
        SortedMap sub_map = ref_map.subMap(word, word + "\u9999");
        return sub_map.keySet().iterator();
    }

    /**
     * Retrieval: Get a list of the words used by this Version. This is
     * not vital for normal display, however it is very useful for various
     * things, not least of which is new Version generation. However if
     * you are only looking to <i>display</i> from this Bible then you
     * could skip this one.
     * @return The references to the word
     */
    protected Iterator listWords() throws BookException
    {
        return ref_map.keySet().iterator();
    }

    /**
     * Detects if index data has been stored for this Bible already
     */
    protected boolean isIndexed(URL url) throws IOException
    {
        URL ref_idy_url = NetUtil.lengthenURL(url, "ref.index");
        return NetUtil.isFile(ref_idy_url);
    }

    /**
     * Loads the index files from disk ready for searching
     */
    protected void loadIndexes(URL url) throws IOException, NumberFormatException
    {
        URL ref_idy_url = NetUtil.lengthenURL(url, "ref.index");

        // We don't need to create any indexes, they just need loading
        BufferedReader ref_idy_bin = new BufferedReader(new InputStreamReader(ref_idy_url.openStream()));
        ref_map = new TreeMap();
        while (true)
        {
            String line = ref_idy_bin.readLine();
            if (line == null)
                break;
            int colon1 = line.indexOf(":");
            int colon2 = line.lastIndexOf(":");
            String word = line.substring(0, colon1);
            long offset = Long.parseLong(line.substring(colon1 + 1, colon2));
            int length = Integer.parseInt(line.substring(colon2 + 1));
            Section section = new Section(offset, length);
            ref_map.put(word, section);
        }
        ref_idy_bin.close();

        // Open the Passage RAF
        URL ref_dat_url = NetUtil.lengthenURL(url, "ref.data");
        ref_dat = new RandomAccessFile(NetUtil.getAsFile(ref_dat_url), "r");
    }

    /**
     * Generate the Objects with no contents waiting to be filled
     * @param url
     * @throws IOException
     */
    protected void createEmptyIndex(URL url) throws IOException
    {
        // Create blank indexes
        ref_map = new TreeMap();

        // Open the Passage RAF
        URL ref_dat_url = NetUtil.lengthenURL(url, "ref.data");
        ref_dat = new RandomAccessFile(NetUtil.getAsFile(ref_dat_url), "rw");
    }

    /**
     * Read from the given source version to generate ourselves
     * @param version The source
     */
    protected void generateSearchIndex(ProgressListener li) throws BookException
    {
        // create a word/passage hashmap
        Map matchmap = new HashMap();

        // loop through all the verses
        for (Iterator it = WHOLE.verseIterator(); it.hasNext();)
        {
            Verse verse = (Verse) it.next();

            if (li != null)
                li.progressMade(new ProgressEvent(bible, "Finding Words:", 90 * verse.getOrdinal() / Books.versesInBible()));

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

                // This could take a long time ...
                Thread.yield();
                if (Thread.currentThread().isInterrupted())
                    break;
            }
        }

        int count = 0;
        int words = matchmap.size();

        // Now we need to write the words into our index
        for (Iterator it = matchmap.keySet().iterator(); it.hasNext();)
        {
            String word = (String) it.next();
            Passage match = (Passage) matchmap.get(word);
            foundPassage(word, match);

            // Fire a progress event?
            if (li != null)
                li.progressMade(new ProgressEvent(bible, "Writing Words:", 90 + (10 * count++ / words)));

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
                break;
        }
    }

    /**
     * Write the references for a Word
     * @param word The word to write
     * @param ref The references to the word
     */
    public void foundPassage(String word, Passage ref) throws BookException
    {
        if (word == null)
            return;

        try
        {
            log.debug("s " + word + " " + System.currentTimeMillis());
            byte[] buffer = PassageUtil.toBinaryRepresentation(ref);
            log.debug("e " + word + " " + System.currentTimeMillis());

            Section section = new Section(ref_dat.getFilePointer(), buffer.length);

            ref_dat.write(buffer);
            ref_map.put(word.toLowerCase(), section);

            // log.debug(this, "Written:");
            // log.debug(this, "  word="+word);
            // log.debug(this, "  ref_ptr="+ref_ptr);
            // log.debug(this, "  ref_length="+ref_blob.length);
            // log.debug(this, "  ref_blob="+new String(ref_blob));
        }
        catch (Exception ex)
        {
            throw new BookException("ser_write", ex);
        }
    }

    /**
     * For a given word find a list of references to it
     * @param word The text to search for
     * @return The references to the word
     */
    public Passage findPassage(String word) throws BookException
    {
        if (word == null)
            return PassageFactory.createPassage();

        Section section = (Section) ref_map.get(word.toLowerCase());

        if (section == null)
            return PassageFactory.createPassage();

        try
        {
            // Read blob
            byte[] blob = new byte[section.length];
            ref_dat.seek(section.offset);
            ref_dat.read(blob);

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
     * Write the indexes to disk
     */
    public void saveIndexes() throws BookException
    {
        // Store the indexes on disk
        try
        {
            // Save the ascii Passage index
            URL ref_idy_url = NetUtil.lengthenURL(url, "ref.index");
            PrintWriter ref_idy_out = new PrintWriter(NetUtil.getOutputStream(ref_idy_url));
            Iterator it = ref_map.keySet().iterator();
            while (it.hasNext())
            {
                String word = (String) it.next();
                Section section = (Section) ref_map.get(word);
                ref_idy_out.println(word + ":" + section.offset + ":" + section.length);
            }
            ref_idy_out.close();
        }
        catch (IOException ex)
        {
            throw new BookException("ser_index", ex);
        }
    }

    /**
     * Remove all the files that make up this index.
     */
    public void delete() throws BookException
    {
        // PENDING(joe): write delete()
    }

    /**
     * The Bible we are indexing
     */
    private Bible bible;

    /**
     * The directory to which to write the index
     */
    private URL url;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(SerSearcher.class);

    /**
     * The passages random access file
     */
    private RandomAccessFile ref_dat;

    /**
     * The hash of indexes into the passages file
     */
    private SortedMap ref_map;

    /**
     * The Whole Bible
     */
    private static final Passage WHOLE = PassageFactory.getWholeBiblePassage();

    /**
     * A simple class to hold an offset and length into the passages random
     * access file
     */
    public static class Section
    {
        public Section(long offset, int length)
        {
            this.offset = offset;
            this.length = length;
        }

        public long offset;
        public int length;
    }
}
