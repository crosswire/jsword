
package org.crosswire.jsword.book.ser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.BookDataListener;
import org.crosswire.jsword.book.data.DataFactory;
import org.crosswire.jsword.book.data.Filters;
import org.crosswire.jsword.book.data.SectionData;
import org.crosswire.jsword.book.data.VerseData;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A cache of BibleData that can be shared amongst Bibles.
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
public class BibleDataCache
{
    /**
     * Constructor for BibleDataCache.
     */
    public BibleDataCache(URL url, BibleMetaData bmd) throws BookException, FileNotFoundException, IOException
    {
        this.url = url;
        this.bmd = bmd;

        if (!url.getProtocol().equals("file"))
        {
            throw new MalformedURLException("not a file url");
        }

        // Create blank indexes
        xml_arr = new long[BibleInfo.versesInBible()];
        
        // Open the XML RAF
        URL xml_dat_url = NetUtil.lengthenURL(url, "xml.data");
        xml_dat = new RandomAccessFile(NetUtil.getAsFile(xml_dat_url), "r");

        // Open the index file
        URL xml_idy_url = NetUtil.lengthenURL(url, "xml.index");
        xml_idy_bin = new BufferedReader(new InputStreamReader(xml_idy_url.openStream()));
    }

    /**
     * Load the indexes from disk
     */
    public void activate()
    {
        // Load the ascii XML index
        for (int i = 0; i < BibleInfo.versesInBible(); i++)
        {
            String line = null;

            try
            {
                line = xml_idy_bin.readLine();
            }
            catch (IOException ex)
            {
                log.error("Error reading xml_idy_bin", ex);
                break;
            }

            if (line == null)
            {
                break;
            }

            try
            {
                xml_arr[i] = Integer.parseInt(line);
            }
            catch (NumberFormatException ex)
            {
                xml_arr[i] = -1;
                log.error("Error parsing line: "+line, ex);
            }
        }

        // xml_idy_bin.close();
    }

    /**
     * Conserve memory
     */
    public void deactivate()
    {
        // NOTE(joe): is there anything we can do to conserve memory?
    }

    /**
     * Create an XML document for the specified Verses
     * @param ref The verses to search for
     */
    public BookData getData(Passage ref) throws BookException
    {
        try
        {
            BookDataListener li = DataFactory.getInstance().createBookDataListnener();
            li.startDocument(bmd.getInitials());

            // For all the ranges in this Passage
            Iterator rit = ref.rangeIterator();
            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();
                li.startSection(range.toString());

                // For all the verses in this range
                Iterator vit = range.verseIterator();
                while (vit.hasNext())
                {
                    Verse verse = (Verse) vit.next();

                    // Seek to the correct point
                    long location = xml_arr[verse.getOrdinal() - 1];
                    if (location == -1)
                    {
                        throw new BookException(Msg.READ_ERROR);
                    }

                    xml_dat.seek(location);

                    // Read the XML text
                    String text = xml_dat.readUTF();

                    li.startVerse(verse);
                    Filters.PLAIN_TEXT.toOSIS(li, text);
                    li.endVerse();
                }

                li.endSection();
            }

            return li.endDocument();
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.READ_ERROR, ex);
        }
    }

    /**
     * Write the XML to disk
     * @param doc The data to write
     */
    public void setDocument(Verse verse, BookData bdata) throws BookException
    {
        try
        {
            // For all of the sections
            for (Iterator sit = bdata.getSectionDatas(); sit.hasNext(); )
            {
                SectionData section = (SectionData) sit.next();

                // For all of the Verses in the section
                for (Iterator vit = section.getRefDatas(); vit.hasNext(); )
                {
                    VerseData vel = (VerseData) vit.next();
                    String text = vel.getPlainText();

                    // Remember where we were so we can read it back later
                    xml_arr[verse.getOrdinal() - 1] = xml_dat.getFilePointer();

                    // And write the entry
                    xml_dat.writeUTF(text);
                }
            }
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }
    }

    /**
     * Flush the data written to disk
     */
    public void flush() throws BookException
    {
        try
        {
            // re-open the RAF read-write
            URL xml_dat_url = NetUtil.lengthenURL(url, "xml.data");
            xml_dat = new RandomAccessFile(NetUtil.getAsFile(xml_dat_url), "rw");

            // Save the ascii XML index
            URL xml_idy_url = NetUtil.lengthenURL(url, "xml.index");
            PrintWriter xml_idy_out = new PrintWriter(NetUtil.getOutputStream(xml_idy_url));
            for (int i = 0; i < xml_arr.length; i++)
            {
                xml_idy_out.println(xml_arr[i]);
            }
            xml_idy_out.close();
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }
    }

    /**
     * About the data that we are caching
     */
    private BibleMetaData bmd;

    /**
     * The directory in which the cache is stored
     */
    private URL url;

    /**
     * The text random access file
     */
    private RandomAccessFile xml_dat;

    /**
     * The index file reader
     */
    private BufferedReader xml_idy_bin;

    /**
     * The hash of indexes into the text file, one per verse. Note that the
     * index in use is NOT the ordinal number of the verse since ordinal nos are
     * 1 based. The index into xml_arr is verse.getOrdinal() - 1
     */
    private long[] xml_arr;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BibleDataCache.class);
}
