
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookUtil;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.Filters;
import org.crosswire.jsword.book.data.JAXBUtil;
import org.crosswire.jsword.book.local.LocalURLBible;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.Parser;
import org.crosswire.jsword.book.search.ParserFactory;
import org.crosswire.jsword.book.search.SearchEngine;
import org.crosswire.jsword.book.search.SearchEngineFactory;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Header;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisText;
import org.crosswire.jsword.osis.Work;
import org.crosswire.jsword.passage.BibleInfo;
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
 *       If iTemp <> 0 And iTemp &lt; iNext Then
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
public class RawBible extends LocalURLBible implements Index
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBible#init(org.crosswire.jsword.book.Bible, org.crosswire.jsword.book.WorkListener)
     */
    public void generateText(Bible source) throws BookException
    {
        init(true);

        super.generateText(source);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBible#init()
     */
    public void init() throws BookException
    {
        init(false);

        try
        {
            URL url = getLocalURLBibleMetaData().getURL();
            searcher = SearchEngineFactory.createSearchEngine(this, url);
        }
        catch (BookException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.INIT_FAIL, ex);
        }
    }

    /**
     * Lazy initialization
     */
    public void init(boolean create)
    {
        if (create)
        {
            memory = false;
        }
        else
        {
            memory = defaultmemory;
        }

        try
        {
            // Without these we can't go on
            word_items = new WordItemsMem(this, create);
            
            if (memory)
            {
                word_insts = new WordInstsMem(this, create);
            } 
            else
            {
                word_insts = new WordInstsDisk(this, create);
            }
            
            // We can still produce text without these though so they
            // should not except if the load fails.
            StringBuffer messages = new StringBuffer();
            
            if (memory)
            {
                punc_insts = new PuncInstsMem(this, create, messages);
            }
            else
            {
                punc_insts = new PuncInstsDisk(this, create, messages);
            }
            
            punc_items = new PuncItemsMem(this, create, messages);
            case_insts = new CaseInstsMem(this, create, messages);
            para_insts = new ParaInstsMem(this, create, messages);
            
            // So if any of them have failed to load we have a record of it.
            // We can carry on work fine, but shouldn't we be telling someone?
            
            /* should have this configurable? */
            //createSearchCache();
        }
        catch (IOException ex)
        {
            log.error("Failed to load indexes.", ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBible#activate()
     */
    public void activate()
    {
        searcher.activate();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBible#deactivate()
     */
    public void deactivate()
    {
        searcher.deactivate();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#findPassage(org.crosswire.jsword.book.Search)
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
     * @see org.crosswire.jsword.book.Bible#getData(org.crosswire.jsword.passage.Passage)
     */
    public BookData getData(Passage ref) throws BookException
    {
        try
        {
            String osisid = getBibleMetaData().getInitials();
            Osis osis = JAXBUtil.factory().createOsis();
            
            Work work = JAXBUtil.factory().createWork();
            work.setOsisWork(osisid);
            
            Header header = JAXBUtil.factory().createHeader();
            header.getWork().add(work);
            
            OsisText text = JAXBUtil.factory().createOsisText();
            text.setOsisIDWork("Bible."+osisid);
            text.setHeader(header);
            
            osis.setOsisText(text);

            Iterator it = ref.rangeIterator();
            while (it.hasNext())
            {
                VerseRange range = (VerseRange) it.next();
                Div div = JAXBUtil.factory().createDiv();
                div.setDivTitle(range.getName());
                
                text.getDiv().add(div);
                
                Verse[] array = range.toVerseArray();
                for (int i=0; i<array.length; i++)
                {
                    Verse verse = array[i];

                    org.crosswire.jsword.osis.Verse everse = JAXBUtil.factory().createVerse();
                    everse.setOsisID(verse.getBook()+"."+verse.getChapter()+"."+verse.getVerse());

                    div.getContent().add(everse);

                    String txt = getText(new VerseRange(verse));
                    Filters.PLAIN_TEXT.toOSIS(everse, txt);                
                }
            }

            BookData bdata = new BookData(osis);
            return bdata;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#findWord(java.lang.String)
     */
    public Passage findWord(String word) throws BookException
    {
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
            int total = BibleInfo.versesInBible();

            for (int ord=1; ord<=total; ord++)
            {
                int[] word_item_ids = word_insts.getIndexes(ord);
                for (int i=0; i<word_item_ids.length; i++)
                {
                    if (word_item_ids[i] == word_idx)
                    {
                        ref.add(new Verse(ord));
                    }
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }

        return ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#getStartsWith(java.lang.String)
     */
    public Iterator getStartsWith(String word) throws BookException
    {
        return ((WordItemsMem) word_items).getStartsWith(word);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBible#setDocument(org.crosswire.jsword.passage.Verse, org.crosswire.jsword.book.data.BookData)
     */
    public void setDocument(Verse verse, BookData bdata) throws BookException
    {
        // For all of the sections
        Iterator sit = bdata.getOsis().getOsisText().getDiv().iterator();
        while (sit.hasNext())
        {
            Div div = (Div) sit.next();

            // For all of the Verses in the section
            for (Iterator vit=div.getContent().iterator(); vit.hasNext(); )
            {
                Object data = vit.next();
                if (data instanceof org.crosswire.jsword.osis.Verse)
                {
                    org.crosswire.jsword.osis.Verse overse = (org.crosswire.jsword.osis.Verse) data;

                    String text = JAXBUtil.getPlainText(overse);

                    // Is this verse part of a new paragraph? Since the move to OSIS
                    // the concept of new para is not what it was. I don't intend to
                    // fix it properly since Raw does not fit well with marked-up
                    // text.
                    para_insts.setPara(false, verse);
    
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
                else
                {
                    log.error("Ignoring non OSIS/Verse content of DIV.");
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBible#flush()
     */
    public void flush() throws BookException
    {
        try
        {
            word_items.save();
            word_insts.save();

            punc_items.save();
            punc_insts.save();

            case_insts.save();
            para_insts.save();

            // generateSearchCache();
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.FLUSH_FAIL, ex);
        }

        super.flush();
    }

    /**
     * Create an String for the specified Verses
     * @param range The verses to search for
     * @return The Bible text
     */
    private String getText(VerseRange range) throws BookException
    {
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
     * Create a cache to speed up searches.
     *
    private void createSearchCache() throws BookException
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
            for (int ord=1; ord<=BibleInfo.versesInBible(); ord++)
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
            throw new BookException(Msg.FIND_FAIL, ex);
        }
    }

    /**
     * Create a cache to speed up searches.
     *
    private void deleteSearchCache() throws BookException
    {
        cache = null;
    }

    /**
     * Do the Bibles we create cache everything in memory or leave it on
     * disk and then read it at query time.
     * @return True if we are cacheing data in memory
     */
    public static boolean isDefaultCacheData()
    {
        return defaultmemory;
    }

    /**
     * Do the Bibles we create cache everything in memory or leave it on
     * disk and then read it at query time.
     * @param memory True if we are cacheing data in memory
     */
    public static void setDefaultCacheData(boolean memory)
    {
        RawBible.defaultmemory = memory;
    }

    /**
     * Do we instruct new RawBibles to cache data in memory?
     */
    private static boolean defaultmemory = true;

    /**
     * Constant for read-only, data in memory mode
     */
    public static final int MODE_READ_MEMORY = 0;

    /**
     * Constant for read-only, data on disk mode
     */
    public static final int MODE_READ_DISK = 1;

    /**
     * Constant for create mode
     */
    public static final int MODE_WRITE = 2;

    /**
     * The Source of Words
     */
    private Items word_items;

    /**
     * The Source of Word Instances
     */
    private Insts word_insts;

    /**
     * The source of Punctuation
     */
    private Items punc_items;

    /**
     * The source of Punctuation Instances
     */
    private Insts punc_insts;

    /**
     * The source of Case Instances
     */
    private Insts case_insts;

    /**
     * The source of Para Instances
     */
    private ParaInstsMem para_insts;

    /**
     * The cache of word searches
     */
    private Passage[] cache;

    /**
     * The search implementation
     */
    protected SearchEngine searcher;

    /**
     * Are we cacheing or in on disk mode?.
     * Does this Bible cache everything in
     * memory or leave it on disk and then read it at query time.
     */
    private boolean memory = true;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RawBible.class);
}
