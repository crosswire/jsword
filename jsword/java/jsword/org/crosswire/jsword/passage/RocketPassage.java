
package org.crosswire.jsword.passage;

import java.util.Iterator;

/**
 * A RocketPassage is a bit and heavy implementation of Passage that goes
 * fairly quickly once let of the leash. It manages its speed by creating
 * contained instances of DistinctPassage and RangedPassage and selects
 * the fastest implementation for each fo its methods from the 3 available.
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
public class RocketPassage extends BitwisePassage
{
    /**
     * Create a new RocketPassage
     */
    public RocketPassage()
    {
    }

    /**
     * Create a Verse from a human readable string. The opposite
     * of getName(), Given any RangedPassage v1, and the following
     * <code>RangedPassage v2 = new RangedPassage(v1.getName());</code>
     * Then <code>v1.equals(v2);</code>
     * Theoretically, since there are many ways of representing a RangedPassage as text
     * string comparision along the lines of:
     * <code>v1.getName().equals(v2.getName())</code> could be false.
     * However since getName() is standardized this will be true.
     * We don't need to worry about thread safety in a ctor since we don't exist yet.
     * @param refs A String containing the text of the RangedPassage
     * @throws NoSuchVerseException if refs is invalid
     */
    protected RocketPassage(String refs) throws NoSuchVerseException
    {
        super(refs);
    }

    /**
     * For preformance reasons we may well want to hint to the Passage that we
     * have done editing it for now and that it is safe to cache certain
     * values to speed up future reads. Any action taken by this method will be
     * undone simply by making a future edit, and the only loss in calling
     * optimizeReads() is a loss of time if you then persist in writing to the
     * Passage.
     */
    public void optimizeReads()
    {
        raiseEventSuppresion();

        // We have to create the cached versions of these separately
        // so that the calculations made by addAll(this) can
        // safely call methods like countVerses() without any
        // danger of them being optimized before the optimizations
        // are ready for use.

        DistinctPassage dtemp = new DistinctPassage();
        dtemp.raiseEventSuppresion();
        dtemp.addAll(this);
        dtemp.lowerEventSuppresionAndTest();

        RangedPassage rtemp = new RangedPassage();
        rtemp.raiseEventSuppresion();
        rtemp.addAll(this);
        rtemp.lowerEventSuppresionAndTest();

        distinct = dtemp;
        ranged = rtemp;

        // This is just an optimization so we dont need to fire any events
        lowerEventSuppresionAndTest();
    }

    /**
     * Simple method to instruct children to stop caching results
     */
    protected void optimizeWrites()
    {
        distinct = null;
        ranged = null;
    }

    /**
     * @return the number of VerseRanges in this Passage
     */
    public int countRanges()
    {
        if (ranged != null)
            return ranged.countRanges();

        return super.countRanges();
    }

    /**
     * @return the number of Verses in this Passage
     */
    public int countVerses()
    {
        if (distinct != null)
            return distinct.countVerses();

        return super.countVerses();
    }

    /**
     * Iterate over the Verses
     * @return A list enumerator
     */
    public Iterator verseIterator()
    {
        if (distinct != null)
            return distinct.verseIterator();

        return super.verseIterator();
    }

    /**
     * Iterate over the VerseRanges
     * @return A list enumerator
     */
    public Iterator rangeIterator()
    {
        if (ranged != null)
            return ranged.rangeIterator();

        return super.rangeIterator();
    }

    /**
     * @returns true if this Passage contains no Verses
     */
    public boolean isEmpty()
    {
        if (distinct != null)
            return distinct.isEmpty();

        return super.isEmpty();
    }

    /**
     * Get a specific Verse from this collection
     * @param offset The verse offset (legal values are 0 to countVerses()-1)
     * @return The Verse
     * @throws ArrayIndexOutOfBoundsException If the offset is out of range
     */
    public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException
    {
        if (distinct != null)
            return distinct.getVerseAt(offset);

        return super.getVerseAt(offset);
    }

    /**
     * Get a specific VerseRange from this collection
     * @param offset The verse range offset (legal values are 0 to countRanges()-1)
     * @return The Verse Range
     * @throws ArrayIndexOutOfBoundsException If the offset is out of range
     */
    public VerseRange getVerseRangeAt(int offset) throws ArrayIndexOutOfBoundsException
    {
        if (ranged != null)
            return ranged.getVerseRangeAt(offset);

        return super.getVerseRangeAt(offset);
    }

    /**
     * How many books are there in this Passage
     * @return The number of distinct books
     */
    public int booksInPassage()
    {
        if (distinct != null)
            return distinct.booksInPassage();

        return super.booksInPassage();
    }

    /**
     * How many chapters are there in a particular book in this Passage
     * @param book The book to check (0 for distinct chapters in all books)
     * @return The number of distinct chapters
     * @throws NoSuchVerseException if book is invalid
     */
    public int chaptersInPassage(int book) throws NoSuchVerseException
    {
        if (distinct != null)
            return distinct.chaptersInPassage(book);

        return super.chaptersInPassage(book);
    }

    /**
     * How many chapters are there in a particular book in this Passage
     * Note that <code>versesInPassage(ref, 0, 0) == ref.countVerses()</code>
     * @param book The book to check (0 for distinct chapters in all books)
     * @param chapter The chapter to check (0 for distinct verses in all chapters)
     * @return The number of distinct chapters
     * @throws NoSuchVerseException if book/chapter is invalid
     */
    public int versesInPassage(int book, int chapter) throws NoSuchVerseException
    {
        if (distinct != null)
            return distinct.versesInPassage(book, chapter);

        return super.versesInPassage(book, chapter);
    }

    /**
     * Returns true if this Passage contains all of the verses in that Passage
     * @param that Passage to be checked for containment in this collection.
     * @return true if this reference contains all of the Verses in that Passage
     */
    public boolean containsAll(Passage that)
    {
        if (ranged != null)
            return ranged.containsAll(that);

        return super.containsAll(that);
    }

    /**
     * The contained DistinctPassage
     * @label cache
     */
    private transient DistinctPassage distinct = null;

    /**
     * The contained RangedPassage
     * @label cache
     */
    private transient RangedPassage ranged = null;
}
