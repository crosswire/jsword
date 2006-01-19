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
package org.crosswire.jsword.book.search.ser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.crosswire.common.activate.Activatable;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.SentenceUtil;
import org.crosswire.jsword.book.search.Grammar;
import org.crosswire.jsword.book.search.Thesaurus;
import org.crosswire.jsword.book.search.basic.AbstractIndex;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.Verse;

/**
 * A search engine - This is a stepping stone on the way to allowing use of
 * Lucene in place of our search engine.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SerIndex extends AbstractIndex implements Activatable, Thesaurus
{
    /**
     * Default ctor
     */
    public SerIndex(Book newbook, URL storage)
    {
        this.book = newbook;
        this.url = storage;
    }

    /**
     * Generate an index to use, telling the job about progress as you go.
     * @throws BookException If we fail to read the index files
     */
    public SerIndex(Book book, URL storage, boolean create) throws BookException
    {
        assert create;

        this.book = book;
        this.url = storage;

        Job job = JobManager.createJob(Msg.INDEX_START.toString(), Thread.currentThread(), false);

        try
        {
            synchronized (creating)
            {
                generateSearchIndex(job);
            }
        }
        catch (Exception ex)
        {
            job.ignoreTimings();
            throw new BookException(Msg.SER_INIT, ex);
        }
        finally
        {
            job.done();
        }                
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException
    {
        return book.getKey(name);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#getStartsWith(java.lang.String)
     */
    public Collection getSynonyms(String word)
    {
        checkActive();

        // log.fine("considering="+words[i]);
        String root = Grammar.getRoot(word);

        // Check that the root is still a word. If not then we
        // use the full version. This catches misses like se is
        // the root of seed, and matches sea and so on ...
        Key ref = find(root);
        if (ref.isEmpty())
        {
            root = word;
        }

        word = word.toLowerCase();
        SortedMap submap = datamap.subMap(word, word + "\u9999"); //$NON-NLS-1$
        return submap.keySet();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#findWord(java.lang.String)
     */
    public Key find(String word)
    {
        checkActive();

        if (word == null)
        {
            return book.createEmptyKeyList();
        }

        Section section = (Section) datamap.get(word.toLowerCase());
        if (section == null)
        {
            return book.createEmptyKeyList();
        }

        try
        {
            // Read blob
            byte[] blob = new byte[section.length];
            dataRaf.seek(section.offset);
            int read = dataRaf.read(blob);

            // Probably a bit harsh, but it would be wrong to just drop it.
            if (read != blob.length)
            {
                throw new IOException();
            }

            // De-serialize
            return PassageKeyFactory.fromBinaryRepresentation(blob);
        }
        catch (Exception ex)
        {
            log.warn("Search failed on:"); //$NON-NLS-1$
            log.warn("  word=" + word); //$NON-NLS-1$
            log.warn("  offset=" + section.offset); //$NON-NLS-1$
            log.warn("  length=" + section.length); //$NON-NLS-1$
            Reporter.informUser(this, ex);

            return book.createEmptyKeyList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractIndex#generateSearchIndex(org.crosswire.common.progress.Job)
     */
    public void generateSearchIndex(Job job) throws BookException
    {
        // create a word/passage hashmap
        Map matchmap = new HashMap();
        generateSearchIndexImpl(job, book.getGlobalKeyList(), matchmap);

        // For the progress listener
        int count = 0;
        int words = matchmap.size();

        // Now we need to write the words into our index
        try
        {
            NetUtil.makeDirectory(url);
            URL dataUrl = NetUtil.lengthenURL(url, FILE_DATA);
            dataRaf = new RandomAccessFile(NetUtil.getAsFile(dataUrl), FileUtil.MODE_WRITE);
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }

        for (Iterator it = matchmap.keySet().iterator(); it.hasNext(); )
        {
            String word = (String) it.next();
            Key match = (Key) matchmap.get(word);
            recordFoundPassage(word, match);

            // Fire a progress event?
            int percent = PERCENT_READ + (PERCENT_WRITE * count++ / words) / BibleInfo.versesInBible();
            job.setProgress(percent, Msg.WRITING_WORDS.toString(word));

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
            {
                break;
            }
        }

        // Store the indexes on disk
        try
        {
            job.setProgress(PERCENT_READ + PERCENT_WRITE, Msg.SAVING.toString());

            // Save the ascii Passage index
            URL indexurl = NetUtil.lengthenURL(url, FILE_INDEX);
            PrintWriter indexout = new PrintWriter(NetUtil.getOutputStream(indexurl));
            Iterator it = datamap.keySet().iterator();
            while (it.hasNext())
            {
                String word = (String) it.next();
                Section section = (Section) datamap.get(word);
                indexout.println(word + ":" + section.offset + ":" + section.length); //$NON-NLS-1$ //$NON-NLS-2$
            }
            indexout.close();
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }
    }

    /**
     * Dig down into a Key indexing as we go.
     */
    private void generateSearchIndexImpl(Job job, Key key, Map matchmap) throws BookException
    {
        // loop through all the verses

        int percent = 0;
        for (Iterator it = key.iterator(); it.hasNext(); )
        {
            Key sublist = (Key) it.next();
            if (sublist.canHaveChildren())
            {
                generateSearchIndexImpl(job, sublist, matchmap);
            }
            else
            {
                BookData data = book.getData(sublist);
                String text = data.getPlainText();

                String[] words = SentenceUtil.getWords(text);
                for (int i = 0; i < words.length; i++)
                {
                    // ensure there is a Passage for this word in the word/passage hashmap
                    Key matches = (Key) matchmap.get(words[i]);
                    if (matches == null)
                    {
                        matches = book.createEmptyKeyList();
                        matchmap.put(words[i], matches);
                    }

                    // add this verse to this words passage
                    matches.addAll(sublist);
                }

                // report progress
                if (sublist instanceof Passage)
                {
                    Verse verse = KeyUtil.getVerse(sublist);
                    percent = PERCENT_READ * verse.getOrdinal() / BibleInfo.versesInBible();
                }

                job.setProgress(percent, Msg.FINDING_WORDS.toString(sublist.getName()));

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
     * Add to the main index data the references against this word
     * @param word The word to write
     * @param key The references to the word
     */
    private void recordFoundPassage(String word, Key key) throws BookException
    {
        if (word == null)
        {
            return;
        }

        try
        {
            Passage ref = KeyUtil.getPassage(key);
            byte[] buffer = PassageKeyFactory.toBinaryRepresentation(ref);

            Section section = new Section(dataRaf.getFilePointer(), buffer.length);

            dataRaf.write(buffer);
            datamap.put(word.toLowerCase(), section);
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#activate()
     */
    public final void activate(Lock lock)
    {
        try
        {
            URL dataUrl = NetUtil.lengthenURL(url, FILE_DATA);
            dataRaf = new RandomAccessFile(NetUtil.getAsFile(dataUrl), FileUtil.MODE_READ);
        
            URL indexUrl = NetUtil.lengthenURL(url, FILE_INDEX);
            BufferedReader indexIn = new BufferedReader(new InputStreamReader(indexUrl.openStream()));
        
            while (true)
            {
                String line = indexIn.readLine();
                if (line == null)
                {
                    break;
                }
        
                try
                {
                    int colon1 = line.indexOf(":"); //$NON-NLS-1$
                    int colon2 = line.lastIndexOf(":"); //$NON-NLS-1$
                    String word = line.substring(0, colon1);
        
                    long offset = Long.parseLong(line.substring(colon1 + 1, colon2));
                    int length = Integer.parseInt(line.substring(colon2 + 1));
        
                    Section section = new Section(offset, length);
                    datamap.put(word, section);
                }
                catch (NumberFormatException ex)
                {
                    log.error("NumberFormatException reading line: " + line, ex); //$NON-NLS-1$
                }
            }
        }
        catch (IOException ex)
        {
            log.error("Read failed on indexin", ex); //$NON-NLS-1$
        }

        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#deactivate()
     */
    public final void deactivate(Lock lock)
    {
        datamap.clear();
        dataRaf = null;

        active = false;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    private final void checkActive()
    {
        if (!active)
        {
            Activator.activate(this);
        }
    }

    /**
     * A synchronization lock point to prevent us from doing 2 index runs at a time.
     */
    private static final Object creating = new Object();

    /**
     * Are we active
     */
    private boolean active = false;

    /**
     * The name of the data file
     */
    private static final String FILE_DATA = "ref.data"; //$NON-NLS-1$

    /**
     * The name of the index file
     */
    protected static final String FILE_INDEX = "ref.index"; //$NON-NLS-1$

    /**
     * The Bible we are indexing
     */
    protected Book book;

    /**
     * The directory to which to write the index
     */
    private URL url;

    /**
     * The passages random access file
     */
    private RandomAccessFile dataRaf;

    /**
     * The hash of indexes into the passages file
     */
    private SortedMap datamap = new TreeMap();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SerIndex.class);

    /**
     * The percentages taken but by different parts
     */
    private static final int PERCENT_READ = 60;
    private static final int PERCENT_WRITE = 39;
    // private static final int PERCENT_INDEX = 1;

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
