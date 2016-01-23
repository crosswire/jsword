/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

/**
 * A Passage is a specialized Collection of Verses. The additions are:
 * <ul>
 * <li>List blurring
 * <li>Range Counting and iteration (in addition to Verse counting etc)
 * <li>List change notification, so you can register to update yourself, and
 * this goes hand in hand with a added thread-safe contract.
 * <li>getName() to be more VerseBase like.
 * <li>Human readable serialization. So we can read and write to and from OLB
 * style Passage files.
 * </ul>
 * 
 * <p>
 * Passage no longer extends the Collection interface to avoid J2SE 1.1/1.2
 * portability problems, and because many of the things that a Passage does rely
 * on consecutive Verses which are an alien concept to Collections. So users
 * would have to use the Passage interface anyway.
 * 
 * <p>
 * Other arguments for and against.
 * <ul>
 * <li>The generic version will postpone some type errors to runtime. Is this a
 * huge problem? Are there many syntax errors that would be lost? Probably not.
 * <li>The specific version would stop enhancements like add("Gen 1:1"); (But
 * this is just syntactical sugar anyway).
 * <li>The specific version allows functionality by is-a as well as has-a. But a
 * Passage is fundamentally different so this is not that much use.
 * <li>At the end of the day I expect people to use getName() instead of
 * toString() and blur(), both of which are Passage things not Collection
 * things. So the general use of these classes is via a Passage interface not a
 * Collections one.
 * <li>Note that the implementations of Passage could not adhere strictly to the
 * Collections interface in returning false from add(), remove() etc, to specify
 * if the Collection was changed. Given ranges and the like this can get very
 * time consuming and complex.
 * </ul>
 * 
 * <p>
 * The upshot of all this is that I am removing the Collections interface from
 * Passage.
 * 
 * <p>
 * I considered giving Passages names to allow for a CLI that could use named
 * RangedPassages, however that is perhaps better left to another class.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface Passage extends VerseKey<Passage> {
    /**
     * A summary of the verses in this Passage For example
     * "10 verses in 4 books"
     * 
     * @return a String containing an overview of the verses
     */
    String getOverview();

    /**
     * Returns the number of verses in this collection. Like Collection.size()
     * This does not mean the Passage needs to use Verses, just that it
     * understands the concept.
     * 
     * @return the number of Verses in this collection
     * @see Verse
     */
    int countVerses();

    /**
     * Determine whether there are two or more ranges.
     * 
     * @param restrict
     *            Do we break ranges at chapter/book boundaries
     * @return whether there are two or more ranges
     * @see VerseRange
     */
    boolean hasRanges(RestrictionType restrict);

    /**
     * Like countVerses() that counts VerseRanges instead of Verses Returns the
     * number of fragments in this collection. This does not mean the Passage
     * needs to use VerseRanges, just that it understands the concept.
     * 
     * @param restrict
     *            Do we break ranges at chapter/book boundaries
     * @return the number of VerseRanges in this collection
     * @see VerseRange
     */
    int countRanges(RestrictionType restrict);

    /**
     * Ensures that there are a maximum of <code>count</code> Verses in this
     * Passage. If there were more than <code>count</code> Verses then a new
     * Passage is created containing the Verses from <code>count</code>+1
     * onwards. If there was not greater than <code>count</code> in the Passage,
     * then the passage remains unchanged, and null is returned.
     * 
     * @param count
     *            The maximum number of Verses to allow in this collection
     * @return A new Passage containing the remaining verses or null
     * @see Verse
     */
    Passage trimVerses(int count);

    /**
     * Ensures that there are a maximum of <code>count</code> VerseRanges in
     * this Passage. If there were more than <code>count</code> VerseRanges then
     * a new Passage is created containing the VerseRanges from
     * <code>count</code>+1 onwards. If there was not greater than
     * <code>count</code> in the Passage, then the passage remains unchanged,
     * and null is returned.
     * 
     * @param count
     *            The maximum number of VerseRanges to allow in this collection
     * @param restrict
     *            Do we break ranges at chapter/book boundaries
     * @return A new Passage containing the remaining verses or null
     * @see VerseRange
     */
    Passage trimRanges(int count, RestrictionType restrict);

    /**
     * How many books are there in this Passage
     * 
     * @return The number of distinct books
     */
    int booksInPassage();

    /**
     * Get a specific Verse from this collection
     * 
     * @param offset
     *            The verse offset (legal values are 0 to countVerses()-1)
     * @return The Verse
     * @throws ArrayIndexOutOfBoundsException
     *             If the offset is out of range
     */
    Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException;

    /**
     * Get a specific VerseRange from this collection
     * 
     * @param offset
     *            The verse range offset (legal values are 0 to countRanges()-1)
     * @param restrict
     *            Do we break ranges at chapter/book boundaries
     * @return The Verse Range
     * @throws ArrayIndexOutOfBoundsException
     *             If the offset is out of range
     */
    VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException;

    /**
     * Like iterator() that iterates over VerseRanges instead of Verses.
     * Exactly the same data will be traversed, however using rangeIterator()
     * will usually give fewer iterations (and never more)
     * 
     * @param restrict
     *            Do we break ranges over chapters
     * @return A list enumerator
     */
    Iterator<VerseRange> rangeIterator(RestrictionType restrict);

    /**
     * Returns true if this collection contains all the specified Verse
     * 
     * @param that
     *            Verse or VerseRange that may exist in this Passage
     * @return true if this collection contains that
     */
    boolean contains(Key that);

    /**
     * Add this Verse/VerseRange to this Passage
     * 
     * @param that
     *            The Verses to be added from this Passage
     */
    void add(Key that);

    /**
     * Remove this Verse/VerseRange from this Passage
     * 
     * @param that
     *            The Verses to be removed from this Passage
     */
    void remove(Key that);

    /**
     * Returns true if this Passage contains all of the verses in that Passage
     * 
     * @param that
     *            Passage to be checked for containment in this collection.
     * @return true if this reference contains all of the Verses in that Passage
     */
    boolean containsAll(Passage that);

    /**
     * To be compatible with humans we read/write ourselves to a file that a
     * human can read and even edit. OLB verse.lst integration is a good goal
     * here.
     * 
     * @param in
     *            The stream to read from
     * @exception java.io.IOException
     *                If the file/network etc breaks
     * @exception NoSuchVerseException
     *                If the file was invalid
     */
    void readDescription(Reader in) throws IOException, NoSuchVerseException;

    /**
     * To be compatible with humans we read/write ourselves to a file that a
     * human can read and even edit. OLB verse.lst integration is a good goal
     * here.
     * 
     * @param out
     *            The stream to write to
     * @exception java.io.IOException
     *                If the file/network etc breaks
     */
    void writeDescription(Writer out) throws IOException;

    /**
     * For performance reasons we may well want to hint to the Passage that we
     * have done editing it for now and that it is safe to cache certain values
     * to speed up future reads. Any action taken by this method will be undone
     * simply by making a future edit, and the only loss in calling
     * optimizeReads() is a loss of time if you then persist in writing to the
     * Passage.
     */
    void optimizeReads();

    /**
     * Event Listeners - Add Listener
     * 
     * @param li
     *            The listener to add
     */
    void addPassageListener(PassageListener li);

    /**
     * Event Listeners - Remove Listener
     * 
     * @param li
     *            The listener to remove
     */
    void removePassageListener(PassageListener li);
}
