
package org.crosswire.jsword.book.sword;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.DefaultBibleData;
import org.crosswire.jsword.book.data.RefData;
import org.crosswire.jsword.book.data.SectionData;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A BibleDriver to read Sword format data.
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @version $Id$
 */
public class SwordBible extends AbstractBible
{
    /**
     * Open the sword data files.
     * @param path - path of the directory where data and index files are located.
     */
    public SwordBible(String name, URL url) throws BookException
    {
        if (mat11_ord == -1)
        {
            try
            {
                Verse mat11 = new Verse(Books.Names.Matthew, 1, 1);
                mat11_ord = mat11.getOrdinal();
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
        }

        this.name = name;

        if (!url.getProtocol().equals("file"))
            throw new BookException("sword_file_only", new Object[] { url.getProtocol() });

        String path = url.getFile();

        try
        {
            idx_raf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(path + "/ot.vss", "r");
            txt_raf[SwordConstants.TESTAMENT_OLD] = new RandomAccessFile(path + "/ot", "r");
        }
        catch (FileNotFoundException ex)
        {
        }

        try
        {
            idx_raf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(path + "/nt.vss", "r");
            txt_raf[SwordConstants.TESTAMENT_NEW] = new RandomAccessFile(path + "/nt", "r");
        }
        catch (FileNotFoundException ex)
        {
        }

        if (txt_raf[SwordConstants.TESTAMENT_OLD] == null && txt_raf[SwordConstants.TESTAMENT_NEW] == null)
            throw new BookException("sword_missing_file", new Object[] { url.getFile() });
    }

    /**
     * What driver is controlling this Bible?
     * @return A BibleDriver relevant to this Bible
     */
    public BibleDriver getDriver()
    {
        return SwordBibleDriver.driver;
    }

    /**
     * Meta-Information: What name can I use to get this Bible in a call
     * to Bibles.getBible(name);
     * @return The name of this Bible
     */
    public String getName()
    {
        return name;
    }

    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
     */
    public BookMetaData getMetaData()
    {
        return version;
    }

    /**
     * Setup the Version information
     * @param version The version that this Bible is becoming
     */
    public void setVersion(BookMetaData version)
    {
        this.version = version;
    }

    /**
     * Create a String for the specified Verses
     * @param range The verses to search for
     * @return The Bible text
     */
    public String getText(VerseRange range) throws BookException
    {
        try
        {
            StringBuffer reply = new StringBuffer();

            Iterator it = range.verseIterator();
            while(it.hasNext())
            {
                Verse verse = (Verse) it.next();
                int verse_ord = verse.getOrdinal();
                int bookNo = verse.getBook();
                int chapNo = verse.getChapter();
                int verseNo = verse.getVerse();

                // If this is an NT verse
                if (verse_ord >= mat11_ord)
                {
                    reply.append(getText(SwordConstants.TESTAMENT_NEW, bookNo-39, chapNo, verseNo));
                }
                else
                {
                    reply.append(getText(SwordConstants.TESTAMENT_OLD, bookNo, chapNo, verseNo));
                }
                

                if (it.hasNext())
                    reply.append(' ');
            }

            return reply.toString();
        }
        catch (IOException ex)
        {
            throw new BookException("sword_read_failed", ex);
        }
    }

    /**
     * Finds the offset of the key verse from the indexes
     * @param testament testament to find (0 - Bible/module introduction)
     * @param book number (within testament) of book
     * @param chapter.
     * @param verse
     */
        private String getText(int testament, int book, int chapter, int verse) throws IOException{
        long start;
        int size;

		// work out the offset
		int bookOffset = SwordConstants.bks[testament][book];
		long chapOffset = SwordConstants.cps[testament][bookOffset+chapter];
		
		long offset=6*(chapOffset+verse);
		
        // Read the next 6 byes.
        idx_raf[testament].seek(offset);
        byte[] read = new byte[6];
        idx_raf[testament].readFully(read);

        // Un-2s-complement them
        int[] temp = new int[6];
        for (int i=0; i<temp.length; i++)
        {
            temp[i] = read[i] >= 0 ? read[i] : 256 + read[i];
        }

        // The data is little endian - extract the start and size
        start = (temp[3] << 24) | (temp[2] << 16) | (temp[1] << 8) | temp[0];
        size = (temp[5] << 8) | temp[4];

        // Read from the data file.
        // I wonder if it would be safe to do a readLine() from here.
        // Probably be safer not to risk it since we know how long it is.
        byte[] buffer = new byte[size];
        txt_raf[testament].seek(start);
        txt_raf[testament].read(buffer);

        // We should probably think about encodings here?
        return new String(buffer);
    }

    /**
     * Create an XML document for the specified Verses
     * @param doc The XML document
     * @param ele The elemenet to append to
     * @param ref The verses to search for
     */
    public BibleData getData(Passage ref) throws BookException
    {
        BibleData doc = new DefaultBibleData();

        try
        {
            // For all the ranges in this Passage
            Iterator rit = ref.rangeIterator();
            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();
                SectionData section = doc.createSectionData(range.toString());

                // For all the verses in this range
                Iterator vit = range.verseIterator();
                while (vit.hasNext())
                {
                    Verse verse = (Verse) vit.next();

                    RefData vref = section.createRefData(verse, false);
                    vref.setPlainText(getText(new VerseRange(verse)));
                }
            }

            return doc;
        }
        catch (Exception ex)
        {
            throw new BookException("ser_read", ex);
        }
    }

    /**
     * For a given word find a list of references to it
     * @param word The text to search for
     * @return The references to the word
     */
    public Passage findPassage(String word) throws BookException
    {
        return PassageFactory.createPassage();
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
        return Collections.EMPTY_LIST.iterator();
    }

    /**
     * Retrieval: Get a list of the words used by this Version. This is
     * not vital for normal display, however it is very useful for various
     * things, not least of which is new Version generation. However if
     * you are only looking to <i>display</i> from this Bible then you
     * could skip this one.
     * @return The references to the word
     */
    public Iterator listWords() throws BookException
    {
        return Arrays.asList(new String[0]).iterator();
    }

    /**
     * Quick test program
     */
    public static void main(String[] args)
    {
        try
        {
            // To start with I'm going to hard code the path
            URL test = new URL("file:/usr/share/sword/modules/texts/rawtext/kjv/");

            SwordBible data = new SwordBible("kjv", test);
            
            String text = data.getText(SwordConstants.TESTAMENT_OLD,1,1,1);

            log.debug("text="+text);
            
            text = data.getText(SwordConstants.TESTAMENT_NEW,1,1,1);

            log.debug("text="+text);
        }
        catch (Exception ex)
        {
            log.info("Failure", ex);
        }
    }

    /** The array of index files */
    private RandomAccessFile[] idx_raf = new RandomAccessFile[3];

    /** The array of data files */
    private RandomAccessFile[] txt_raf = new RandomAccessFile[3];

    /** The name of this version */
    private String name;

    /** The Version of the Bible that this produces */
    private BookMetaData version;

    /** The start of the new testament */
    private static int mat11_ord = -1;

    /** The log stream */
    protected static Logger log = Logger.getLogger(SwordBible.class);
}
