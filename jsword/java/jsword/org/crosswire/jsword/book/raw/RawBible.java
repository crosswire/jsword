
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertiesUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookUtil;
import org.crosswire.jsword.book.basic.BasicBookMetaData;
import org.crosswire.jsword.book.basic.VersewiseBible;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.DataUtil;
import org.crosswire.jsword.book.data.DefaultBibleData;
import org.crosswire.jsword.book.data.RefData;
import org.crosswire.jsword.book.data.SectionData;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * RawBible is a custom Bible. It is designed to be:<ul>
 * <li>Compact: So that the download time is as small as possible
 * <li>Divisible: So that a download can be partial, and some text
 *     can be read whilst missing content like styles, notes, or
 *     even word case.
 * </ul>
 * <p>As a result of this is can be very slow, or very memory hungry.
 * I guess that the technology developed here could be useful as a
 * delivery format, but the timings I am getting from my benchmarks
 * say "start again".</p>
 *
 * <p>There is a question mark over how this format will handle rich
 * text. The dictionary lookup scheme can be very space efficient
 * but I'm not sure how to embed strongs numbers with the same
 * efficiency.</p>
 *
 * <p>The algorithm I have implemented here is not perfect. To get a list
 * of the verses it gets 'wrong' see generate.log.
 * There are 2 reasons for problems. The RawBible does not take note of
 * double spaces. And we incorrectly capitalize hyphenated words at the
 * beginning of sentances.</p>
 *
 * <p>This is in part converted from the VB code that I wrote ages ago
 * that does asimilar job.</p>
 * <pre>
 * Public Sub WritePassage(sText As String, lPassageID As Long, bLang As Byte, lBibleID As Long)
 *
 *   Static bItalic As Boolean
 *
 *   Dim mWordInsts As Collection
 *
 *   Dim iNext As Long
 *   Dim iTemp As Long
 *   Dim iLast As Long
 *   Dim bDash As Boolean
 *   Dim sWord As String
 *   Dim bThisItalic As Boolean
 *   Dim iStart As Long
 *   Dim iEnd As Long
 *   Dim sNote As String
 *   Dim mNotes As Collection
 *   Dim vNoteStr As Variant
 *   Dim iNumNotes As Long
 *   Dim lWordInstID As Long
 *
 *   Set mWordInsts = New Collection
 *   iNext = 1
 *   iTemp = 1
 *   iLast = 1
 *   bDash = False
 *   iNumNotes = 1
 *
 *   ' For each real word in the verse
 *   Do
 *
 *     ' If this word contains a "{" then it is part of a comment
 *     ' and not a word. We need to strip out sets of comments
 *     Set mNotes = New Collection
 *     Do
 *       ' Decide how long this word is
 *       iNext = InStr(iLast, sText, " ")
 *       iTemp = InStr(iLast, sText, "--")
 *       If iTemp = iLast Then iTemp = 0
 *       If iTemp <> 0 And iTemp < iNext Then
 *         iNext = iTemp
 *         bDash = True
 *       Else
 *         bDash = False
 *       End If
 *
 *       ' If this is the end add in the rest otherwise just add in this word
 *       If iNext = 0 Then
 *         sWord = Mid$(sText, iLast, Len(sText) - iLast + 1)
 *       Else
 *         sWord = Mid$(sText, iLast, iNext - iLast)
 *       End If
 *
 *
 *       ' Strip out the notes
 *       ' If this word is not a comment
 *       iStart = InStr(iLast, sText, "{")
 *       If iStart = 0 Then Exit Do
 *       If iStart > iLast Then Exit Do
 *
 *       ' Check we have a start and an end
 *       iEnd = InStr(iLast, sText, "}")
 *
 *       ' Add the note in
 *       sNote = Mid$(sText, iStart + 1, iEnd - iStart - 1)
 *       mNotes.Add sNote
 *
 *       ' Adjust where we are looking for words
 *       iLast = iEnd + 2
 *       If iLast > Len(sText) Then
 *         iNext = 0
 *         sWord = ""
 *         Exit Do
 *       End If
 *     Loop
 *
 *     ' Are there any notes to add?
 *     If mNotes.Count <> 0 Then
 *       ' If there is no previous word to add to then create one
 *       If mWordInsts.Count = 0 Then
 *         lWordInstID = WriteWordInst(lPassageID, 1, lBibleID)
 *         SetWordInstItalic lWordInstID, bItalic
 *         mWordInsts.Add lWordInstID
 *       End If
 *
 *       ' So add the notes to the previous word
 *       For Each vNoteStr In mNotes
 *         sNote = vNoteStr
 *         WriteNote mWordInsts.Item(mWordInsts.Count), iNumNotes, sNote
 *         iNumNotes = iNumNotes + 1
 *       Next
 *     End If
 *     Set mNotes = Nothing
 *
 *
 *     ' Italics
 *     ' Do we have a start italic char
 *     If InStr(sWord, "[") Then
 *       bItalic = True
 *       sWord = RemoveChar(sWord, "[")
 *     End If
 *
 *     ' Remember the state for this letter
 *     bThisItalic = bItalic
 *
 *     ' do we have an end italic char
 *     If InStr(sWord, "]") Then
 *       bItalic = False
 *       sWord = RemoveChar(sWord, "]")
 *     End If
 *
 *
 *     ' Actually add the word in
 *     If sWord <> "" Then
 *       AddWord mWordInsts, sWord, lPassageID, bLang, lBibleID, bThisItalic
 *     End If
 *
 *
 *     ' Add one an extra one to the last used only for a Space split
 *     If bDash Then
 *       iLast = iNext
 *     Else
 *       iLast = iNext + 1
 *     End If
 *
 *   Loop Until iNext = 0
 *   Set mWordInsts = Nothing
 *
 * End Sub
 * </pre>
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
public class RawBible extends VersewiseBible
{
    /**
     * Create a new set of resources based on a URL.
     */
    public RawBible(String name, URL url, int mode) throws BookException
    {
        this.name = name;
        this.dir = url;

        setMode(mode);

        if (mode != MODE_WRITE)
        {
            try
            {
                // The version information
                URL prop_url = NetUtil.lengthenURL(url, "bible.properties");
                InputStream prop_in = prop_url.openStream();
                Properties prop = new Properties();
                PropertiesUtil.load(prop, prop_in);
                version = new BasicBookMetaData(prop);
            }
            catch (Exception ex)
            {
                throw new BookException("raw_init", ex);
            }
        }

        log.debug("Started RawBible url="+url+ " name="+name+" mode="+mode);
    }

    /**
     * Does this Bible cache everything in memory or leave it on disk and
     * then read it at query time.
     * @return True if we are cacheing data in memory
     */
    public int getMode()
    {
        if (create)
        {
            return MODE_WRITE;
        }
        else
        {
            if (memory) return MODE_READ_MEMORY;
            else        return MODE_READ_DISK;
        }
    }

    /**
     * Does this Bible cache everything in memory or leave it on disk and
     * then read it at query time. I wonder if this is an over complex and
     * redundant function? Maybe it is something to simplify at some point.
     * @param memory True if we are cacheing data in memory
     */
    public void setMode(int mode) throws BookException
    {
        // Do we need to patch-up if we have started reading.
        if (started)
        {
            if (mode == MODE_WRITE)
                throw new IllegalStateException("Can't change to write mode once reading has started.");

            try
            {
                if (this.memory && mode != MODE_READ_MEMORY)
                {
                    word_insts = new WordInstsDisk(this, create);
                }
                else if (!this.memory && mode == MODE_READ_MEMORY)
                {
                    word_insts = new WordInstsMem(this, create);
                }
            }
            catch (BookException ex)
            {
                throw ex;
            }
            catch (Exception ex)
            {
                throw new BookException("raw_bible_mode", ex);
            }
        }

        // Change the state
        switch (mode)
        {
        case MODE_READ_DISK:
            create = false;
            memory = false;
            break;

        case MODE_READ_MEMORY:
            create = false;
            memory = true;
            break;

        case MODE_WRITE:
            create = true;
            memory = false;
            break;

        default:
            throw new IllegalArgumentException("Mode must be one of MODE_[WRITE|READ_[DISK|MEMORY]]");
        }
    }

    /**
     * Lazy initialization
     */
    public void init() throws BookException
    {
        if (started) return;
        started = true;

        // Without these we can't go on
        try
        {
            word_items = new WordItemsMem(this, create);

            if (memory) word_insts = new WordInstsMem(this, create);
            else        word_insts = new WordInstsDisk(this, create);
        }
        catch (Exception ex)
        {
            if (ex instanceof BookException) throw (BookException) ex;
            throw new BookException("Error initializing resource. System error: "+ex);
        }

        // We can still produce text without these though so they
        // should not except if the load fails.
        StringBuffer messages = new StringBuffer();

        if (memory) punc_insts = new PuncInstsMem(this, create, messages);
        else        punc_insts = new PuncInstsDisk(this, create, messages);

        punc_items = new PuncItemsMem(this, create, messages);
        case_insts = new CaseInstsMem(this, create, messages);
        para_insts = new ParaInstsMem(this, create, messages);

        // So if any of them have failed to load we have a record of it.
        // We can carry on work fine, but shouldn't we be telling someone?

        /** @todo work out if we should have this configurable */
        //createSearchCache();
    }

    /**
     * Shut the Bible down.
     */
    public void destroy() throws BookException
    {
    }

    /**
     * What driver is controlling this Bible?
     * @return A BibleDriver relevant to this Bible
     */
    public BibleDriver getDriver()
    {
        return RawBibleDriver.driver;
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
     * Create an String for the specified Verses
     * @param range The verses to search for
     * @return The Bible text
     */
    public String getText(VerseRange range) throws BookException
    {
        if (!started) init();

        StringBuffer retcode = new StringBuffer();

        Verse[] verses = range.toVerseArray();
        for (int i=0; i<verses.length; i++)
        {
            int[] word_idxs = word_insts.getIndexes(verses[i]);
            int[] case_idxs = case_insts.getIndexes(verses[i]);
            int[] punc_idxs = punc_insts.getIndexes(verses[i]);

            for (int j=0; j<word_idxs.length; j++)
            {
                String punc = null;
                String word = null;

                try
                {
                    int punc_idx = punc_idxs[j];
                    int word_idx = word_idxs[j];
                    int case_idx = case_idxs[j];

                    punc = punc_items.getItem(punc_idx);
                    word = PassageUtil.setCase(word_items.getItem(word_idx), case_idx);
                }
                catch (Exception ex)
                {
                    Reporter.informUser(this, ex);
                }

                retcode.append(punc);
                retcode.append(word);
            }

            try
            {
                if (punc_idxs.length != 0)
                    retcode.append(punc_items.getItem(punc_idxs[punc_idxs.length-1]));
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }
        }

        return retcode.toString().trim();
    }

    /**
     * Create an XML document for the specified Verses
     * @param doc The XML document
     * @param ref The verses to search for
     */
    public BibleData getData(Passage ref) throws BookException
    {
        BibleData doc = new DefaultBibleData();

        if (!started) init();

        Iterator it = ref.rangeIterator();
        while (it.hasNext())
        {
            VerseRange range = (VerseRange) it.next();
            append(doc, range);
        }

        return doc;
    }

    /**
     * For a given word find a list of references to it
     * @param word The text to search for
     * @return The references to the word
     */
    public Passage findPassage(String word) throws BookException
    {
        if (!started) init();
        if (word == null)
            return PassageFactory.createPassage();

        int word_idx = word_items.getIndex(word);

        // Are we caching searches?
        if (cache != null && cache[word_idx] != null)
        {
            return cache[word_idx];
        }

        // Do the real seacrh
        Passage ref = PassageFactory.createPassage();
        try
        {
            int total = Books.versesInBible();

            for (int ord=1; ord<=total; ord++)
            {
                int[] word_items = word_insts.getIndexes(ord);
                for (int i=0; i<word_items.length; i++)
                {
                    if (word_items[i] == word_idx)
                        ref.add(new Verse(ord));
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new BookException("raw_bible_find", ex);
        }

        return ref;
    }

    /**
     * Find a list of words that start with the given word
     * @param word The word to search for
     * @return An array of matches
     */
    public Iterator getStartsWith(String word) throws BookException
    {
        return ((WordItemsMem) word_items).getStartsWith(word);
    }

    /**
     * Retrieval: Get a list of the words used by this Version. This is
     * not vital for normal display, however it is very useful for various
     * things, not least of which is new Version generation. However if
     * you are only looking to <i>display</i> from this Bible then you
     * could skip this one.
     * @return The references to the word
     */
    public Enumeration listWords() throws BookException
    {
        if (!started) init();
        return word_items.getEnumeration();
    }

    /**
     * Write the XML to disk. Now this code limits us to only having para
     * marks at the start of verses, and in the NIV there are marks in the
     * middle of verses. However all the data sources that I have (ex-OLB)
     * have the same limitation so I'm not to bothered just yet.
     * @param verse The verse to write
     * @param text The data to write
     */
    public void setDocument(BibleData doc) throws BookException
    {
        if (!started) init();

        // For all of the sections
        for (Enumeration sen=doc.getSectionDatas(); sen.hasMoreElements(); )
        {
            SectionData section = (SectionData) sen.nextElement();

            // For all of the Verses in the section
            for (Enumeration ven=section.getRefDatas(); ven.hasMoreElements(); )
            {
                RefData vel = (RefData) ven.nextElement();

                Verse verse = vel.getVerse();
                String text = vel.getPlainText();

                // Is this verse part of a new paragraph?
                boolean para = DataUtil.isNewPara(doc);
                para_insts.setPara(para, verse);

                // Chop the sentance into words.
                String[] text_array = BookUtil.tokenize(text);

                // The word index
                String[] word_array = BookUtil.stripPunctuation(text_array);
                int[] word_indexes = word_items.getIndex(word_array);
                word_insts.setIndexes(word_indexes, verse);

                // The punctuation index
                String[] punc_array = BookUtil.stripWords(text_array);
                int[] punc_indexes = punc_items.getIndex(punc_array);
                punc_insts.setIndexes(punc_indexes, verse);

                // The case index
                int[] case_indexes = BookUtil.getCases(word_array);
                case_insts.setIndexes(case_indexes, verse);
            }
        }
    }

    /**
     * Save a list of found words. This has been dome already
     * @param word The word to write
     * @param ref The data to write
     */
    public void foundPassage(String word, Passage ref) throws BookException
    {
    }

    /**
     * Flush the data written to disk
     */
    public void flush() throws BookException
    {
        if (!started) init();

        try
        {
            word_items.save();
            word_insts.save();

            punc_items.save();
            punc_insts.save();

            case_insts.save();
            para_insts.save();

            // generateSearchCache();

            Properties prop = new Properties();
            prop.put("Version", getMetaData().getFullName());
            URL prop_url = NetUtil.lengthenURL(dir, "bible.properties");
            OutputStream prop_out = NetUtil.getOutputStream(prop_url);
            PropertiesUtil.save(prop, prop_out, "RawBible Config");
        }
        catch (IOException ex)
        {
            throw new BookException("raw_bible_flush", ex);
        }
    }

    /**
     * The directory that holds the RawBible files
     * @return The index file directory
     */
    public URL getBaseURL()
    {
        return dir;
    }

    /**
     * Accessor for the list of Words. For testing only
     */
    protected WordItemsMem getWords()
    {
        return (WordItemsMem) word_items;
    }

    /**
     * Accessor for the Verse/Words arrays. For testing only
     */
    protected WordInstsMem getWordData()
    {
        return (WordInstsMem) word_insts;
    }

    /**
     * Part of the Bible interface - Get the text for this reference.
     * Fetch the Bible text for a single reference from a PassageID and a Bible
     */
    protected void append(BibleData doc, VerseRange range) throws BookException
    {
        SectionData section = doc.createSectionData(range.getName(), "AV");

        Verse[] array = range.toVerseArray();
        for (int i=0; i<array.length; i++)
        {
            Verse verse = array[i];
            String text = getText(new VerseRange(verse));
            boolean para = para_insts.getPara(verse);

            RefData ref = section.createRefData(verse, para);
            ref.setPlainText(text);
        }
    }

    /**
     * Create a cache to speed up searches.
     */
    protected void createSearchCache() throws BookException
    {
        try
        {
            // Create a passage for each word
            cache = new Passage[word_items.size()];
            for (int i=0; i<word_items.size(); i++)
            {
                cache[i] = PassageFactory.createPassage();
            }

            // For each verse in the Bible
            for (int ord=1; ord<=Books.versesInBible(); ord++)
            {
                // and each word in the verse
                int[] word_items = word_insts.getIndexes(ord);
                for (int i=0; i<word_items.length; i++)
                {
                    // add the word to that words passage
                    cache[word_items[i]].add(new Verse(ord));
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new BookException("raw_bible_find", ex);
        }
    }

    /**
     * Create a cache to speed up searches.
     */
    protected void deleteSearchCache() throws BookException
    {
        cache = null;
    }

    /** Constant for read-only, data in memory mode */
    public static final int MODE_READ_MEMORY = 0;

    /** Constant for read-only, data on disk mode */
    public static final int MODE_READ_DISK = 1;

    /** Constant for create mode */
    public static final int MODE_WRITE = 2;

    /** The directory that the data files are stored in */
    private URL dir;

    /** Are we in create mode? */
    private boolean create;

    /** Has init() been called? */
    private boolean started = false;

    /** The Source of Words */
    private Items word_items;

    /** The Source of Word Instances */
    private Insts word_insts;

    /** The source of Punctuation */
    private Items punc_items;

    /** The source of Punctuation Instances */
    private Insts punc_insts;

    /** The source of Case Instances */
    private Insts case_insts;

    /** The source of Para Instances */
    private ParaInstsMem para_insts;

    /** The name of this version */
    private String name;

    /** The cache of word searches */
    private Passage[] cache;

    /** Are we cacheing or in on disk mode */
    private boolean memory = true;

    /** The Version of the Bible that this produces */
    private BookMetaData version;

    /** The log stream */
    protected static Logger log = Logger.getLogger("bible.book");
}
