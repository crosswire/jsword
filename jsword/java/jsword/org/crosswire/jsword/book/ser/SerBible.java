
package org.crosswire.jsword.book.ser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import org.crosswire.common.util.ArrayEnumeration;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertiesUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.BasicBookMetaData;
import org.crosswire.jsword.book.basic.VersewiseBible;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.DefaultBibleData;
import org.crosswire.jsword.book.data.RefData;
import org.crosswire.jsword.book.data.SectionData;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A Biblical source that comes from files on the local file system.
 *
 * <p>This format is designed to be fast. At any cost. So disk space does
 * not matter, which is good because early versions used about 100Mb!</p>
 *
 * <p>This is a history of some of the design desisions that this class has
 * been through.</p>
 *
 * <h4>Searching</h4>
 * <p>I think that a Bible ought not to store anything other than Bible
 * text. I have experimented with a saerch mechanism that cached searches
 * in a very effective manner, however it took up a lot of disk space,
 * and only worked for one version. It might be good to have it work in a
 * more generic way, and an in-memory cache would also be a good idea. So
 * I am going to move the natty search bit into a caching class.
 *
 * <h4>Text Storage</h4>
 * It would be good to get a handle on the way the OLB and Sword and so on
 * work:<ul>
 * <li><b>OLB:</b> 2 core files: an index file that starts with text like:
 *     "AaronitesbaddonAbagthanarimabasedingtedAbbadaeelielonednegolbet"
 *     which is a strange sort of index. Possibly strings with start pos
 *     and length. Then data files, and plenty of other indexes.
 * <li><b>Theopholos:</b> Single data file that begins- "aaron aaronites
 *     aarons abaddon abagtha abana abarim abase abased abasing abated"
 *     This is again in index type affair.
 * <li><b>Sword:</b> All this VerseKey stuff ...
 * </ul>
 * I think the answer is that an word index is good. (Like this is news)
 * So we can map all the words to numbers and then encode the biblical
 * text as a series of numbers.
 *
 * <h4>Priorities</h4>
 * What factors affect our design the most?<ul>
 * <li><b>Search Speed:</b> Proably the biggest reason people will have to
 *     use this program initially will be the powerful search engine. This
 *     can be very demanding though, and every effort should be taken to
 *     make best match searches fast.
 * <li><b>Size:</b> Size is not a huge problem from a disk space point of
 *     view - the average hard disk is now about 10Gb. Looking at the
 *     various installations that I have, the average is a little short of
 *     20Mb each. Generally each version takes up 3-5Mb If we were to be
 *     over double this size and take up 50Mb total, I don't think there
 *     would be a huge problem.<br>
 *     However many people will first come to use this program from a net
 *     download - now size is a huge problem. Maybe we should have a
 *     very very compact download that on installation indexed itself.
 * <li><b>Text Retrieval Speed:</b> I do not see this as being a huge
 *     issue. The text generation time from reverse-engineering my
 *     concordance was acceptable if slow, so this should not be a big
 *     deal, and I guess it is very easily cacheable too.
 * </ul>
 *
 * <h4>Strategies</h4>
 * For a single verse we have 2 basic strategies. Have a single block of
 * data that specifies the words, punctuation, and markup, or for each set
 * of data we could have a separate source. Clearly there are also hybrid
 * versions. The pros and cons:<ul>
 * <li>Searches only have to read one file, and the information is more
 *     dense in that (less disk reads for wanted data) This also applies
 *     to the ability to ignore certain types of mark-up.
 * <li>It is easier to add/alter a single source of information - or even
 *     to share a source amongst versions. Maybe things like red lettering
 *     could benefit from this.
 * <li>Text display is slower because the information is spread over
 *     several files. But as mentioned above - who cares?
 * </ul>
 * So how far do we take this? The parts that we can split off from the
 * words are these:<ul>
 * <li>Markup: Most markup is tied to a particular word, so we would need
 *     some way of attaching markup to words.
 * <li>Inter-Word Punctuation: We could do for punctuation exactly what we
 *     do for the words. List the options in a dictionary, and then write
 *     out an index. I guess less than 255 different types of inter-word
 *     punctuation (1 byte per inter-word). (as opposed to 18360 different
 *     words 2 bytes per word)<br>
 *     There are 32k words in the Bible - this would make the central data
 *     file about 64k in size!
 * <li>Case: To get down to 18k words you need to make "Foo" the same as
 *     "foo" and "FOO", however I guess that even making words case
 *     sensative we would be under 65k words.
 *     Splitting case would not decrease file sizes (but may make it
 *     compress better) however it would introduce a new case file. Since
 *     there are only 4 cases (See PassageUtil) that is 0.25 bytes per
 *     word. (8k for the whole Bible)
 * <li>Intra-Word Punctuation: Examples "-',". Examples of words that use
 *     these punctuations: Maher-Shalal-Hash-Baz, Aaron's, 144,000. Watch
 *     out for --. The NIV uses it to join sentances together--Something
 *     like this. However there is no space between these words. This is
 *     closely linked to-
 * <li>Word Semantics: We could make the words "job", "jobs", and "job's"
 *     the same. Also "run", "runs", "running", "runned" and so on. Even
 *     "am", "are", "is". This would dramatically reduce the size of the
 *     dictionary, make the text re-generation quite complex and the data
 *     generation nigh on impossible. But it would make for some really
 *     powerful searches (although possibly nothing that a thesaurus would
 *     not help)
 * </ul>
 * I think the last 2 are hard to sus. However I am keen to work on them
 * next. So it looks like I sort out the first 3. Time to reasurect that
 * VB code. Now is it a port or a re-write?
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version $Id$
 */
public class SerBible extends VersewiseBible
{
    /**
     * Basic constructor for a SerBible
     */
    public SerBible(String name, URL url, boolean create) throws BookException
    {
        this.name = name;
        this.url = url;

        if (!url.getProtocol().equals("file"))
            throw new BookException("ser_url");

        try
        {
            String file_mode;

            if (create)
            {
                // We leave the Version unknown until we have data
                version = null;

                // Create blank indexes
                // we used to use new StringComparator() here because
                // jdk1.1 String did not implement Comparable. I assume that
                // the rules I used  in writing StringComparator are the same
                // as the rules in jdk1.2 String Comparable ...
                // The same comments are valid for the else code below.
                ref_map = new TreeMap();
                xml_arr = new long[Books.versesInBible()];

                // Open the random access files read write
                file_mode = "rw";
            }
            else
            {
                // The version information
                URL prop_url = NetUtil.lengthenURL(url, "bible.properties");
                InputStream prop_in = prop_url.openStream();
                Properties prop = new Properties();
                PropertiesUtil.load(prop, prop_in);
                version = new BasicBookMetaData(prop);

                // Load the ascii Passage index
                // See above notes on StringComparator()
                URL ref_idy_url = NetUtil.lengthenURL(url, "ref.index");
                BufferedReader ref_idy_bin = new BufferedReader(new InputStreamReader(ref_idy_url.openStream()));
                ref_map = new TreeMap();
                while (true)
                {
                    String line = ref_idy_bin.readLine();
                    if (line == null) break;
                    int colon1 = line.indexOf(":");
                    int colon2 = line.lastIndexOf(":");
                    String word = line.substring(0, colon1);
                    long offset = Long.parseLong(line.substring(colon1 + 1, colon2));
                    int length = Integer.parseInt(line.substring(colon2 + 1));
                    Section section = new Section(offset, length);
                    ref_map.put(word, section);
                }
                ref_idy_bin.close();

                // Load the ascii XML index
                URL xml_idy_url = NetUtil.lengthenURL(url, "xml.index");
                BufferedReader xml_idy_bin = new BufferedReader(new InputStreamReader(xml_idy_url.openStream()));
                xml_arr = new long[Books.versesInBible()];
                for (int i = 0; i < Books.versesInBible(); i++)
                {
                    String line = xml_idy_bin.readLine();
                    xml_arr[i] = Integer.parseInt(line);
                }
                xml_idy_bin.close();

                // Open the random access files read only
                file_mode = "r";
            }

            // Open the Passage RAF
            URL ref_dat_url = NetUtil.lengthenURL(url, "ref.data");
            ref_dat = new RandomAccessFile(ref_dat_url.getFile(), file_mode);

            // Open the XML RAF
            URL xml_dat_url = NetUtil.lengthenURL(url, "xml.data");
            xml_dat = new RandomAccessFile(xml_dat_url.getFile(), file_mode);
        }
        catch (Exception ex)
        {
            throw new BookException("ser_init", ex);
        }

        log.fine("Started SerBible url=" + url + " name=" + name + " create=" + create);
    }

    /**
     * What driver is controlling this Bible?
     * @return A BibleDriver relevant to this Bible
     */
    public BibleDriver getDriver()
    {
        return SerBibleDriver.driver;
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

                    // Seek to the correct point
                    xml_dat.seek(xml_arr[verse.getOrdinal() - 1]);

                    // Read the XML text
                    String text = xml_dat.readUTF();

                    RefData vref = section.createRefData(verse, false);
                    vref.setPlainText(text);
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
            log.warning("Search failed on:");
            log.warning("  word=" + word);
            log.warning("  ref_ptr=" + section.offset);
            log.warning("  ref_length=" + section.length);
            Reporter.informUser(this, ex);

            return PassageFactory.createPassage();
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
    public Iterator listWords() throws BookException
    {
        return ref_map.keySet().iterator();
    }

    /**
     * Write the XML to disk
     * @param verse The verse to write
     * @param doc The data to write
     */
    public void setDocument(BibleData doc) throws BookException
    {
        try
        {
            // For all of the sections
            for (Enumeration sen = doc.getSectionDatas(); sen.hasMoreElements(); )
            {
                SectionData section = (SectionData) sen.nextElement();

                // For all of the Verses in the section
                for (Enumeration ven = section.getRefDatas(); ven.hasMoreElements(); )
                {
                    RefData vel = (RefData) ven.nextElement();

                    Verse verse = vel.getVerse();
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
            throw new BookException("ser_write", ex);
        }
    }

    /**
     * Write the references for a Word
     * @param word The word to write
     * @param ref The references to the word
     */
    public void foundPassage(String word, Passage ref) throws BookException
    {
        if (word == null) return;

        try
        {
            log.fine("s " + word + " " + System.currentTimeMillis());
            byte[] buffer = PassageUtil.toBinaryRepresentation(ref);
            log.fine("e " + word + " " + System.currentTimeMillis());

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
     * Flush the data written to disk
     */
    public void flush() throws BookException
    {
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

            // Save the ascii XML index
            URL xml_idy_url = NetUtil.lengthenURL(url, "xml.index");
            PrintWriter xml_idy_out = new PrintWriter(NetUtil.getOutputStream(xml_idy_url));
            for (int i = 0; i < xml_arr.length; i++)
            {
                xml_idy_out.println(xml_arr[i]);
            }
            xml_idy_out.close();

            // The Bible config info
            Properties prop = new Properties();
            prop.put("Version", getMetaData().getFullName());
            URL prop_url = NetUtil.lengthenURL(url, "bible.properties");
            OutputStream prop_out = NetUtil.getOutputStream(prop_url);
            PropertiesUtil.save(prop, prop_out, "RawBible Config");
        }
        catch (IOException ex)
        {
            throw new BookException("ser_index", ex);
        }
    }

    /**
     * The directory that holds the RawBible files
     * @return The index file directory
     */
    public URL getBaseURL()
    {
        return url;
    }

    /**
     * The SAX parser to use
     */
    private static final String PARSER = "com.ibm.xml.parsers.SAXParser";

    /**
     * The base url
     */
    private URL url;

    /**
     * The name of this version
     */
    private String name;

    /**
     * The passages random access file
     */
    private RandomAccessFile ref_dat;

    /**
     * The hash of indexes into the passages file
     */
    private SortedMap ref_map;

    /**
     * The text random access file
     */
    private RandomAccessFile xml_dat;

    /**
     * The hash of indexes into the text file, one per verse. Note that the
     * index in use is NOT the ordinal number of the verse since ordinal nos are
     * 1 based. The index into xml_arr is verse.getOrdinal() - 1
     */
    private long[] xml_arr;

    /**
     * Some shortcuts into the list of names to help startsWith
     */
    private long[] letters = new long[26];

    /**
     * The Version of the Bible that this produces
     */
    private BookMetaData version;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger("bible.book");

    /**
     * A simple class to hold an offset and length into the passages random
     * access file
     */
    static class Section
    {
        public Section(long offset, int length)
        {
            this.offset = offset;
            this.length = length;
        }

        public long offset;
        public int length;
    }

    /**
     * This customization just clips of the .ser from the array members
     */
    static class CustomArrayEnumeration extends ArrayEnumeration
    {
        /**
         * This is the only of the ArrayEnumeration ctors that we need
         */
        CustomArrayEnumeration(Object[] array)
        {
            super(array);
        }

        /**
         * Get the next item from the database
         * @return The next object in the list
         */
        public Object nextElement()
        {
            String file = (String) array[pos++];
            return file.substring(0, file.length() - 4);
        }
    }

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    static class CustomFilenameFilter implements FilenameFilter
    {
        /**
         * Create a CustomFilenameFilter with a word to match
         * the start of
         */
        public CustomFilenameFilter(String word)
        {
            this.word = word;
        }

        /**
         * Match word
         */
        public boolean accept(File parent, String name)
        {
            return name.startsWith(word);
        }

        /**
         * The word to match
         */
        private String word;
    }
}
