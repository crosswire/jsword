
package org.crosswire.jsword.book.stub;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Commentary;
import org.crosswire.jsword.book.CommentaryMetaData;
import org.crosswire.jsword.book.Dictionary;
import org.crosswire.jsword.book.DictionaryMetaData;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.DefaultBibleData;
import org.crosswire.jsword.book.data.RefData;
import org.crosswire.jsword.book.data.SectionData;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * StubBook is a simple stub implementation of Bible that is pretty much
 * always going to work because it has no dependancies on external files.
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
public class StubBook extends AbstractBible implements Bible, Dictionary, Commentary
{
    /**
     * Basic constructor for a StubBook
     */
    public StubBook(StubBookMetaData bmd)
    {
        this.bmd = bmd;
    }

    /**
     * @see org.crosswire.jsword.book.Bible#getBibleMetaData()
     */
    public BibleMetaData getBibleMetaData()
    {
        return bmd;
    }

    /**
     * @see org.crosswire.jsword.book.Dictionary#getDictionaryMetaData()
     */
    public DictionaryMetaData getDictionaryMetaData()
    {
        return bmd;
    }

    /**
     * @see org.crosswire.jsword.book.Commentary#getCommentaryMetaData()
     */
    public CommentaryMetaData getCommentaryMetaData()
    {
        return bmd;
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
                vref.setPlainText("stub implementation");
            }
        }

        return doc;
    }

    /**
     * @see org.crosswire.jsword.book.Commentary#getComments(org.crosswire.jsword.passage.Passage)
     */
    public BibleData getComments(Passage ref) throws BookException
    {
        return getData(ref);
    }

    /**
     * @see org.crosswire.jsword.book.Commentary#hasComments(org.crosswire.jsword.passage.Verse)
     */
    public boolean hasComments(Verse verse) throws BookException
    {
        return true;
    }

    /**
     * @see org.crosswire.jsword.book.Dictionary#getData(java.lang.String)
     */
    public BibleData getData(String word) throws BookException
    {
        try
        {
            Verse verse = new Verse("Gen 1:1");
            VerseRange range = new VerseRange(verse);
            
            BibleData doc = new DefaultBibleData();
            SectionData section = doc.createSectionData(range.toString());
            
            RefData vref = section.createRefData(verse, false);
            vref.setPlainText("stub implementation");
            return doc;
        }
        catch (NoSuchVerseException ex)
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
        try
        {
            return PassageFactory.createPassage("Gen 1:1-Rev22:21");
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }
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
        return Arrays.asList(new String[] { "stub", "implementation", }).iterator();
    }

    /**
     * Retrieval: Return an array of words that are used by this Bible
     * that start with the given string. For example calling:
     * <code>getStartsWith("love")</code> will return something like:
     * { "love", "loves", "lover", "lovely", ... }
     * @param base The word to base your word array on
     * @return An array of words starting with the base
     */
    public Iterator getStartsWith(String base) throws BookException
    {
        base = base.toLowerCase();

        if (base.equals(""))
            return Arrays.asList(new String[] { "stub", "implementation", }).iterator();

        if ("stub".startsWith(base))
            return Arrays.asList(new String[] { "stub" }).iterator();

        if ("implementation".startsWith(base))
            return Arrays.asList(new String[] { "implementation" }).iterator();

        return Collections.EMPTY_LIST.iterator();
    }

    /**
     * @see org.crosswire.jsword.book.Dictionary#getIndex(java.lang.String)
     */
    public List getIndex(String base)
    {
        base = base.toLowerCase();

        if (base.equals(""))
            return Arrays.asList(new String[] { "stub", "implementation", });

        if ("stub".startsWith(base))
            return Arrays.asList(new String[] { "stub" });

        if ("implementation".startsWith(base))
            return Arrays.asList(new String[] { "implementation" });

        return Collections.EMPTY_LIST;
    }

    /**
     * The name of this version
     */
    private StubBookMetaData bmd;
}
