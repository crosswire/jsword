
package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Iterator;

/**
 * A Passage is a specialized Collection of Verses.
 * The additions are:<ul>
 * <li>List blurring
 * <li>Range Counting and iteration (in addition to Verse counting etc)
 * <li>List change notification, so you can register to update yourself, and
 *     this goes hand in hand with a added thread-safe contract.
 * <li>getName() to be more VerseBase like.
 * <li>Human readable serialization. So we can read and write to and from
 *     OLB style Passage files.
 * </ul>
 *
 * <p>Passage no longer extends the Collection interface to avoid JDK 1.1/1.2
 * portability problems, and because many of the things that a Passage does
 * rely on consecutive Verses which are an alien concept to Collections.
 * So users would have to use the Passage interface anyway.
 *
 * <p>Other arguments for and against.<ul>
 * <li>The generic version will postpone some type errors to runtime. Is this a
 *     huge problem? Are there many syntax errors that would be lost? Probably not.
 * <li>The specific version would stop enhancements like add("Gen 1:1");
 *     (But this is just syntactical sugar anyway).
 * <li>The specific version allows funcionality by is-a as well as has-a.
 *     But a Passage is fundamentally different so this is not that much use.
 * <li>At the end of the day I expect people to use getName() instead of toString()
 *     and blur(), both of which are Passage things not Collection things. So
 *     the general use of these classes is via a Passage interface not a
 *     Collections one.
 * <li>Note that the implementations of Passage could not adhere strictly to
 *     the Collections interface in returning false from add(), remove() etc,
 *     to specify if the Collection was changed. Given ranges and the like this
 *     can get very time consuming and complex.
 * </ul>
 *
 * <p>The upshot of all this is that I am removing the Collections interface from
 * Passage, but writing a PassageCollection as a proxy.
 *
 * <p>I considered giving Passages names to allow for a CLI that could
 * use named RangedPassages, however that is perhaps better left to another class.
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
public interface Passage extends Serializable, Cloneable, PassageConstants
{
    /**
     * A Human readable version of the verse list.
     * Uses short books names, and the shortest sensible rendering eg "Mat 3:1-4"
     * and "Mar 1:1, 3, 5" and "3Jo, Jude"
     * @return a String containing a description of the verses
     */
    public String getName();

    /**
     * The OSIS defined specification for this Passage.
     * Uses short books names, with "." as a verse part separator.
     * @return a String containing the OSIS description of the verses
     */
    public String getOSISName();

    /**
     * A summary of the verses in this Passage
     * For example "10 verses in 4 books"
     * @return a String containing an overview of the verses
     */
    public String getOverview();

    /**
     * Does this Passage have 0 members
     * @return true if the Passage is empty
     */
    public boolean isEmpty();

    /**
     * Returns the number of verses in this collection. Like Collection.size()
     * This does not mean the Passage needs to use Verses, just that it
     * understands the concept.
     * @return the number of Verses in this collection
     * @see Verse
     */
    public int countVerses();

    /**
     * Like countVerses() that counts VerseRanges instead of Verses
     * Returns the number of fragments in this collection.
     * This does not mean the Passage needs to use VerseRanges, just that it
     * understands the concept.
     * @param restrict Do we break ranges at chapter/book boundries
     * @return the number of VerseRanges in this collection
     * @see VerseRange
     */
    public int countRanges(int restrict);

    /**
     * Ensures that there are a maximum of <code>count</code> Verses in
     * this Passage. If there were more than <code>count</code> Verses
     * then a new Passage is created containing the Verses from
     * <code>count</code>+1 onwards. If there was not greater than
     * <code>count</code> in the Passage, then the passage remains
     * unchanged, and null is returned.
     * @param count The maximum number of Verses to allow in this collection
     * @return A new Passage conatining the remaining verses or null
     * @see Verse
     */
    public Passage trimVerses(int count);

    /**
     * Ensures that there are a maximum of <code>count</code> VerseRanges
     * in this Passage. If there were more than <code>count</code>
     * VerseRanges then a new Passage is created containing the
     * VerseRanges from <code>count</code>+1 onwards. If there was not
     * greater than <code>count</code> in the Passage, then the passage
     * remains unchanged, and null is returned.
     * @param count The maximum number of VerseRanges to allow in this collection
     * @param restrict Do we break ranges at chapter/book boundries
     * @return A new Passage conatining the remaining verses or null
     * @see VerseRange
     */
    public Passage trimRanges(int count, int restrict);

    /**
     * How many books are there in this Passage
     * @return The number of distinct books
     */
    public int booksInPassage();

    /**
     * How many chapters are there in a particular book in this Passage
     * @param book The book to check (0 for distinct chapters in all books)
     * @return The number of distinct chapters
     * @throws NoSuchVerseException if the book is invalid
     */
    public int chaptersInPassage(int book) throws NoSuchVerseException;

    /**
     * How many chapters are there in a particular book in this Passage
     * Note that <code>versesInPassage(ref, 0, 0) == ref.countVerses()</code>
     * @param book The book to check (0 for distinct chapters in all books)
     * @param chapter The chapter to check (0 for distinct verses in all chapters)
     * @return The number of distinct chapters
     * @throws NoSuchVerseException if the book/chapter is invalid
     */
    public int versesInPassage(int book, int chapter) throws NoSuchVerseException;

    /**
     * Get a specific Verse from this collection
     * @param offset The verse offset (legal values are 0 to countVerses()-1)
     * @return The Verse
     * @throws ArrayIndexOutOfBoundsException If the offset is out of range
     */
    public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException;

    /**
     * Get a specific VerseRange from this collection
     * @param offset The verse range offset (legal values are 0 to countRanges()-1)
     * @param restrict Do we break ranges at chapter/book boundries
     * @return The Verse Range
     * @throws ArrayIndexOutOfBoundsException If the offset is out of range
     */
    public VerseRange getVerseRangeAt(int offset, int restrict) throws ArrayIndexOutOfBoundsException;

    /**
     * Iterate over the Verses in this collection
     * @return A list enumerator
     */
    public Iterator verseIterator();

    /**
     * Like verseElements() that iterates over VerseRanges instead of Verses.
     * Exactly the same data will be traversed, however using rangeIterator()
     * will usually give less iterations (and never more)
     * @param restrict Do we break ranges over chapters
     * @return A list enumerator
     */
    public Iterator rangeIterator(int restrict);

    /**
     * Returns true if this collection contains all the specified Verse
     * @param that Verse or VerseRange that may exist in this Passage
     * @return true if this collection contains that
     */
    public boolean contains(VerseBase that);

    /**
     * Add this Verse/VerseRange to this Passage
     * @param that The Verses to be removed from this Passage
     */
    public void add(VerseBase that);

    /**
     * Remove this Verse/VerseRange from this Passage
     * @param that The Verses to be removed from this Passage
     */
    public void remove(VerseBase that);

    /**
     * Returns true if this Passage contains all of the verses in that Passage
     * @param that Passage to be checked for containment in this collection.
     * @return true if this reference contains all of the Verses in that Passage
     */
    public boolean containsAll(Passage that);

    /**
     * Adds all that Passage's verses to this Passage
     * @param that The Verses to be removed from this Passage
     */
    public void addAll(Passage that);

    /**
     * Removes all this collection's elements that are also contained in the
     * specified collection.
     * @param that The Verses to be removed from this Passage
     */
    public void removeAll(Passage that);

    /**
     * Removes all the Verses from this reference that are not in that reference
     * specified collection.
     * @param that The Verses to be removed from this Passage
     */
    public void retainAll(Passage that);

    /**
     * Removes all the verses is this Passage
     */
    public void clear();

    /**
     * Widen the range of the verses in this list. This is primarily for
     * "find x within n verses of y" type applications.
     * @param verses The number of verses to widen by
     * @param restrict How should we restrict the blurring?
     * @see Passage
     */
    public void blur(int verses, int restrict);

    /**
     * To be compatible with humans we read/write ourselves to a file that a human can
     * read and even edit. OLB verse.lst integration is a good goal here.
     * @param in The stream to read from
     * @exception java.io.IOException If the file/network etc breaks
     * @exception NoSuchVerseException If the file was invalid
     */
    public void readDescription(Reader in) throws IOException, NoSuchVerseException;

    /**
     * To be compatible with humans we read/write ourselves to a file that a human can
     * read and even edit. OLB verse.lst integration is a good goal here.
     * @param out The stream to write to
     * @exception java.io.IOException If the file/network etc breaks
     */
    public void writeDescription(Writer out) throws IOException;

    /**
     * For preformance reasons we may well want to hint to the Passage that we
     * have done editing it for now and that it is safe to cache certain
     * values to speed up future reads. Any action taken by this method will be
     * undone simply by making a future edit, and the only loss in calling
     * optimizeReads() is a loss of time if you then persist in writing to the
     * Passage.
     */
    public void optimizeReads();

    /**
     * Event Listeners - Add Listener
     * @param li The listener to add
     */
    public void addPassageListener(PassageListener li);

    /**
     * Event Listeners - Remove Listener
     * @param li The listener to remove
     */
    public void removePassageListener(PassageListener li);

    /**
     * I'm not sure why we have to keep on redeclaring this. But If I don't
     * Then I get accused of attempting to reduce access level of clone();
     * Maybe this is a bug in MS JVM. It has been behaving strangely here.
     * @return A complete copy of ourselves
     * @exception java.lang.CloneNotSupportedException We don't do this but our kids might
     */
    public Object clone() throws CloneNotSupportedException;
}
