
package org.crosswire.jsword.passage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.crosswire.common.util.LogicError;
import org.apache.log4j.Logger;

/**
 * This is a base class to help with some of the common implementation
 * details of being a Passage.
 * <p>Importantly, this class takes care of Serialization in a general yet
 * optimized way. I think I am going to have a look at replacement here.
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
public abstract class AbstractPassage implements Passage
{
    /**
     * Setup that leaves original name being null
     */
    protected AbstractPassage()
    {
    }

    /**
     * Setup the original name of this reference
     * @param original_name The text originally used to create this Passage.
     */
    protected AbstractPassage(String original_name)
    {
        this.original_name = original_name;
    }

    /**
     * Create a copy of ourselves
     * @return A complete copy of ourselves
     * @exception CloneNotSupportedException We don't do this but our kids might
     */
    public Object clone() throws CloneNotSupportedException
    {
        // This gets us a shallow copy
        AbstractPassage copy = (AbstractPassage) super.clone();

        copy.listeners = (Vector) listeners.clone();
        copy.original_name  = original_name;

        return copy;
    }

    /**
     * Is this Object equal to us. Points to note:<ul>
     * <li>If you override equals(), you must override hashCode() too.
     * <li>If you are doing this it is a good idea to be immutable.
     * </ul>
     * @param obj The thing to test against
     * @return True/False is we are or are not equal to obj
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null) return false;

        // This is cheating beacuse I am supposed to say:
        // <code>!obj.getClass().equals(this.getClass())</code>
        // However I think it is entirely valid for a RangedPassage
        // to equal a DistinctPassage since the point of the Factory
        // is that the user does not need to know the actual type of the
        // Object he is using.
        if (!(obj instanceof Passage)) return false;

        Passage ref = (Passage) obj;
        // The real test
        if (!ref.getName().equals(getName())) return false;

        return true;
    }

    /**
     * @return The hashing number
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /**
     * A Human readable version of the verse list. Uses short books names,
     * and the shortest possible rendering eg "Mat 3:1-4, 6"
     * @returns a String containing a description of the verses
     */
    public String getName()
    {
        if (PassageUtil.isPersistentNaming() && original_name != null)
        {
            return original_name;
        }

        StringBuffer retcode = new StringBuffer();

        Iterator it = rangeIterator();
        Verse current = null;
        while (it.hasNext())
        {
            VerseRange range = (VerseRange) it.next();
            retcode.append(range.getName(current));

            if (it.hasNext())
                retcode.append(REF_PREF_DELIM);

            current = range.getStart();
        }

        return retcode.toString();
    }

    /**
     * Simply bounce to getName() to help String concatenation.
     * @returns a String containing a description of the verses
     */
    public String toString()
    {
        return getName();
    }

    /**
     * A summary of the verses in this Passage
     * For example "Search (10 matches in 4 books)"
     * @returns a String containing an overview of the verses
     */
    public String getOverview()
    {
        int verse_count = countVerses();
        int book_count = booksInPassage();

        String verses = (verse_count == 1)
                      ? PassageUtil.getResource("abstract_verse_singular")
                      : PassageUtil.getResource("abstract_verse_plural");

        String books = (book_count == 1)
                     ? PassageUtil.getResource("abstract_book_singular")
                     : PassageUtil.getResource("abstract_book_plural");

        return verse_count+" "+verses+" "+book_count+" "+books;
    }

    /**
     * Does this Passage have 0 members
     * @return true if the Passage is empty
     */
    public boolean isEmpty()
    {
        return countVerses() == 0;
    }

    /**
     * Returns the number of verses in this collection. Like Collection.size()
     * This does not mean the Passage needs to use Verses, just that it understands the concept.
     * @return the number of Verses in this collection
     * @see Verse
     */
    public int countVerses()
    {
        int count = 0;

        Iterator it = verseIterator();
        while (it.hasNext())
        {
            it.next();
            count++;
        }

        return count;
    }

    /**
     * Like countVerses() that counts VerseRanges instead of Verses
     * Returns the number of fragments in this collection.
     * This does not mean the Passage needs to use VerseRanges, just that it understands the concept.
     * @return the number of VerseRanges in this collection
     * @see VerseRange
     */
    public int countRanges()
    {
        int count = 0;

        Iterator it = rangeIterator();
        while (it.hasNext())
        {
            it.next();
            count++;
        }

        return count;
    }

    /**
     * How many books are there in this Passage
     * @return The number of distinct books
     */
    public int booksInPassage()
    {
        int current_book = 0;
        int book_count = 0;

        Iterator it = verseIterator();
        while (it.hasNext())
        {
            Verse verse = (Verse) it.next();
            if (current_book != verse.getBook())
            {
                current_book = verse.getBook();
                book_count++;
            }
        }

        return book_count;
    }

    /**
     * How many chapters are there in a particular book in this Passage
     * @param book The book to be checking (0 for distinct chapters in all books)
     * @return The number of distinct chapters
     * @throws NoSuchVerseException if the book is invalid
     */
    public int chaptersInPassage(int book) throws NoSuchVerseException
    {
        if (book != 0)  Books.validate(book, 1, 1);

        int current_chapter = 0;
        int chapter_count = 0;

        Iterator it = verseIterator();
        while (it.hasNext())
        {
            Verse verse = (Verse) it.next();

            if ((book == 0 || verse.getBook() == book)
                && current_chapter != verse.getChapter())
            {
                current_chapter = verse.getChapter();
                chapter_count++;
            }
        }

        return chapter_count;
    }

    /**
     * How many chapters are there in a particular book in this Passage.
     * Note that <code>versesInPassage(ref, 0, 0) == ref.countVerses()</code>
     * for all ref.
     * @param book The book to be checking (0 for distinct chapters in all books)
     * @param chapter The chapter to be checking (0 for distinct verses in all chapters)
     * @return The number of distinct chapters
     * @throws NoSuchVerseException if the book/chapter is invalid
     */
    public int versesInPassage(int book, int chapter) throws NoSuchVerseException
    {
        Books.validate((book == 0 ? 1 : book), (chapter == 0 ? 1 : chapter), 1);

        int verse_count = 0;

        Iterator it = verseIterator();
        while (it.hasNext())
        {
            Verse verse = (Verse) it.next();

            if ((book == 0 || verse.getBook() == book)
                && (chapter == 0 || verse.getChapter() == chapter))
            {
                verse_count++;
            }
        }

        return verse_count;
    }

    /**
     * Get a specific Verse from this collection
     * @param offset The verse offset (legal values are 0 to countVerses()-1)
     * @return The Verse
     * @throws ArrayIndexOutOfBoundsException If the offset is out of range
     */
    public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException
    {
        Iterator it = verseIterator();
        Object retcode = null;

        for (int i=0; i<=offset; i++)
        {
            if (!it.hasNext())
            {
                String message = PassageUtil.getResource("passg_error_index", new Object[] { new Integer(offset) });
                throw new ArrayIndexOutOfBoundsException(message);
            }

            retcode = it.next();
        }

        return (Verse) retcode;
    }

    /**
     * Get a specific VerseRange from this collection
     * @param offset The verse range offset (legal values are 0 to countRanges()-1)
     * @return The Verse Range
     * @throws ArrayIndexOutOfBoundsException If the offset is out of range
     */
    public VerseRange getVerseRangeAt(int offset) throws ArrayIndexOutOfBoundsException
    {
        Iterator it = rangeIterator();
        Object retcode = null;

        for (int i=0; i<=offset; i++)
        {
            if (!it.hasNext())
            {
                String message = PassageUtil.getResource("passg_error_index", new Object[] { new Integer(offset) });
                throw new ArrayIndexOutOfBoundsException(message);
            }

            retcode = it.next();
        }

        return (VerseRange) retcode;
    }

    /**
     * Enumerate over the VerseRanges
     * @return A list enumerator
     */
    public Iterator rangeIterator()
    {
        return new VerseRangeIterator();
    }

    /**
     * Returns true if this Passage contains all of the Verses
     * in the that Passage.
     * @param that Passage to be checked for containment in this Passage
     * @return true if this reference contains all of the Verses in that Passage
     */
    public boolean containsAll(Passage that)
    {
        Iterator that_it = null;

        if (that instanceof RangedPassage) that_it = ((RangedPassage) that).rangeIterator();
        else                               that_it = that.verseIterator();

        while (that_it.hasNext())
        {
            if (!contains((VerseBase) that_it.next()))
                return false;
        }

        return true;
    }

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
    public Passage trimVerses(int count)
    {
        optimizeWrites();
        raiseNormalizeProtection();

        Passage remainder = null;
        int i = 0;
        boolean overflow = false;

        try
        {
            remainder = (Passage) this.clone();

            Iterator it = verseIterator();
            while (it.hasNext())
            {
                i++;
                Verse verse = (Verse) it.next();

                if (i > count)
                {
                    remove(verse);
                    overflow = true;
                }
                else
                {
                    remainder.remove(verse);
                }
            }
        }
        catch (CloneNotSupportedException ex)
        {
            throw new LogicError(ex);
        }

        lowerNormalizeProtection();
        // The event notification is done by the remove above

        if (overflow)   return remainder;
        else            return null;
    }

    /**
     * Ensures that there are a maximum of <code>count</code> VerseRanges
     * in this Passage. If there were more than <code>count</code>
     * VerseRanges then a new Passage is created containing the
     * VerseRanges from <code>count</code>+1 onwards. If there was not
     * greater than <code>count</code> in the Passage, then the passage
     * remains unchanged, and null is returned.
     * @param count The maximum number of VerseRanges to allow in this collection
     * @return A new Passage conatining the remaining verses or null
     * @see VerseRange
     */
    public Passage trimRanges(int count)
    {
        optimizeWrites();
        raiseNormalizeProtection();

        Passage remainder = null;
        int i = 0;
        boolean overflow = false;

        try
        {
            remainder = (Passage) this.clone();

            Iterator it = rangeIterator();
            while (it.hasNext())
            {
                i++;
                VerseRange range = (VerseRange) it.next();

                if (i > count)
                {
                    remove(range);
                    overflow = true;
                }
                else
                {
                    remainder.remove(range);
                }
            }
        }
        catch (CloneNotSupportedException ex)
        {
            throw new LogicError(ex);
        }

        lowerNormalizeProtection();
        // The event notification is done by the remove above

        if (overflow)   return remainder;
        else            return null;
    }

    /**
     * Adds all of the elements in that Passage to this Passage.
     * The behavior of this operation is undefined if that
     * Passage is modified while the operation is in progress
     * @param that elements to be inserted into this Passage
     */
    public void addAll(Passage that)
    {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        Iterator that_it = null;

        if (that instanceof Passage)  that_it = ((Passage) that).rangeIterator();
        else                          that_it = that.verseIterator();

        while (that_it.hasNext())
        {
            // Avoid touching store to make thread safety easier.
            add((VerseBase) that_it.next());
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest())
            fireIntervalAdded(this, that.getVerseAt(0), that.getVerseAt(that.countVerses()-1));
    }

    /**
     * Removes all this Passage's Verses that are also contained in the
     * that Passage.  After this call returns, this Passage
     * will contain no Verses in common with the that Passage
     * @param that Verses to be removed from this Passage
     */
    public void removeAll(Passage that)
    {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        Iterator that_it = null;

        if (that instanceof Passage)  that_it = ((Passage) that).rangeIterator();
        else                          that_it = that.verseIterator();

        while (that_it.hasNext())
        {
            // Avoid touching store to make thread safety easier.
            remove((VerseBase) that_it.next());
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest())
            fireIntervalRemoved(this, that.getVerseAt(0), that.getVerseAt(that.countVerses()-1));
    }

    /**
     * Retains only the Verses in this Passage that are contained in that
     * Passage. In other words, removes from this Passage all of its
     * Verses that are not contained in that Passage
     * @param that Verses to be retained in this Passage
     */
    public void retainAll(Passage that)
    {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        try
        {
            Passage temp = (Passage) this.clone();
            Iterator it = temp.verseIterator();

            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();
                if (!that.contains(verse))
                {
                    remove(verse);
                }
            }
        }
        catch (CloneNotSupportedException ex)
        {
            throw new LogicError(ex);
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest())
            fireIntervalRemoved(this, null, null);
    }

    /**
     * Removes all of the Verses from this Passage.
     * This implementation is ripe for optimization
     */
    public void clear()
    {
        optimizeWrites();
        raiseNormalizeProtection();

        remove(VerseRange.getWholeBibleVerseRange());

        if (lowerEventSuppresionAndTest())
            fireIntervalRemoved(this, null, null);
    }

    /**
     * Widen the range of the verses in this list. This is primarily for
     * "find x within n verses of y" type applications.
     * @param verses The number of verses to widen by
     * @param restrict How should we restrict the blurring?
     * @see Passage
     */
    public void blur(int verses, int restrict)
    {
        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        try
        {
            Passage temp = (Passage) this.clone();
            Iterator it = temp.rangeIterator();

            while (it.hasNext())
            {
                VerseRange range = new VerseRange((VerseRange) it.next(), verses, verses, restrict);
                add(range);
            }
        }
        catch (CloneNotSupportedException ex)
        {
            throw new LogicError(ex);
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest())
            fireIntervalAdded(this, null, null);
    }

    /**
     * To be compatible with humans we read/write ourselves to a file that
     * a human can read and even edit. OLB verse.lst integration is a good
     * goal here.
     * @param out The stream to write to
     * @exception java.io.IOException If the file/network etc breaks
     */
    public void writeDescription(Writer out) throws IOException
    {
        BufferedWriter bout = new BufferedWriter(out);

        Iterator it = rangeIterator();

        while (it.hasNext())
        {
            VerseRange range = (VerseRange) it.next();
            bout.write(range.getName());
            bout.newLine();
        }

        bout.flush();
    }

    /**
     * To be compatible with humans we read/write ourselves to a file that
     * a human can read and even edit. OLB verse.lst integration is a good
     * goal here. This method does not clear before it starts reading.
     * @param in The stream to read from
     * @throws NoSuchVerseException if the description is invalid
     * @exception java.io.IOException If the file/network etc breaks
     */
    public void readDescription(Reader in) throws IOException, NoSuchVerseException
    {
        raiseEventSuppresion();
        raiseNormalizeProtection();

        BufferedReader bin = new BufferedReader(in);
        while (true)
        {
            String line = bin.readLine();
            if (line == null) break;
            addVerses(line);
        }

        lowerNormalizeProtection();
        if (lowerEventSuppresionAndTest())
            fireIntervalAdded(this, getVerseAt(0), getVerseAt(countVerses()-1));
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
    }

    /**
     * Simple method to instruct children to stop caching results
     */
    protected void optimizeWrites()
    {
    }

    /**
     * Event Listeners - Add Listener
     * @param li The listener to add
     */
    public void addPassageListener(PassageListener li)
    {
        synchronized (listeners)
        {
            listeners.addElement(li);
        }
    }

    /**
     * Event Listeners - Remove Listener
     * @param li The listener to remove
     */
    public void removePassageListener(PassageListener li)
    {
        synchronized (listeners)
        {
            listeners.removeElement(li);
        }
    }

    /**
     * AbstractPassage subclasses must call this method <b>after</b> one
     * or more elements of the list are added.  The changed elements are
     * specified by a closed interval from start to end.
     * @param source The thing that changed, typically "this".
     * @param start One end of the new interval.
     * @param end The other end of the new interval.
     * @see PassageListener
     */
    protected void fireIntervalAdded(Object source, Verse start, Verse end)
    {
        if (suppress_events != 0) return;

        Vector temp;

        // Create Event
        PassageEvent ev = new PassageEvent(source, PassageEvent.VERSES_ADDED, start, end);

        // Copy listener vector so it won't change while firing
        synchronized (listeners)
        {
            temp = (Vector) listeners.clone();
        }

        // And run throught the list shouting
        for (int i=0; i<temp.size(); i++)
        {
            PassageListener rl = (PassageListener) temp.elementAt(i);
            rl.versesAdded(ev);
        }
    }

    /**
     * AbstractPassage subclasses must call this method <b>before</b> one
     * or more elements of the list are added.  The changed elements are
     * specified by a closed interval from start to end.
     * @param source The thing that changed, typically "this".
     * @param start One end of the new interval.
     * @param end The other end of the new interval.
     * @see PassageListener
     */
    protected void fireIntervalRemoved(Object source, Verse start, Verse end)
    {
        if (suppress_events != 0) return;

        Vector temp;

        // Create Event
        PassageEvent ev = new PassageEvent(source, PassageEvent.VERSES_REMOVED, start, end);

        // Copy listener vector so it won't change while firing
        synchronized (listeners)
        {
            temp = (Vector) listeners.clone();
        }

        // And run throught the list shouting
        for (int i=0; i<temp.size(); i++)
        {
            PassageListener rl = (PassageListener) temp.elementAt(i);
            rl.versesRemoved(ev);
        }
    }

    /**
     * AbstractPassage subclasses must call this method <b>before</b> one
     * or more elements of the list are added.  The changed elements are
     * specified by a closed interval from start to end.
     * @param source The thing that changed, typically "this".
     * @param start One end of the new interval.
     * @param end The other end of the new interval.
     * @see PassageListener
     */
    protected void fireContentsChanged(Object source, Verse start, Verse end)
    {
        if (suppress_events != 0) return;

        Vector temp;

        // Create Event
        PassageEvent ev = new PassageEvent(source, PassageEvent.VERSES_CHANGED, start, end);

        // Copy listener vector so it won't change while firing
        synchronized (listeners)
        {
            temp = (Vector) listeners.clone();
        }

        // And run throught the list shouting
        for (int i=0; i<temp.size(); i++)
        {
            PassageListener rl = (PassageListener) temp.elementAt(i);
            rl.versesChanged(ev);
        }
    }

    /**
     * Create a Passage from a human readable string. The opposite of
     * <code>toString()</code>. Since this method is not public it
     * leaves control of <code>suppress_events<code> up to the people
     * that call it.
     * @param refs A String containing the text of the RangedPassage
     * @throws NoSuchVerseException if the string is invalid
     */
    protected void addVerses(String refs) throws NoSuchVerseException
    {
        optimizeWrites();

        String[] parts = PassageUtil.tokenize(refs, REF_ALLOWED_DELIMS);
        if (parts.length == 0) return;

        // We treat the first as a special case because there is
        // nothing to sensibly base this reference on
        VerseRange basis = new VerseRange(parts[0].trim());
        add(basis);

        // Loop for the other verses, interpreting each on the
        // basis of the one before.
        for (int i=1; i<parts.length; i++)
        {
            VerseRange next = new VerseRange(parts[i].trim(), basis);
            add(next);
            basis = next;
        }
    }

    /**
     * We sometimes need to sort ourselves out ...
     * I don't think we need to be synchronised since we are private
     * and we could check that all public calling of normalize() are
     * synchronised, however this is safe, and I don't think there is
     * a cost associated with a double synchronize. (?)
     */
    protected void normalize()
    {
        // before doing any normalization we should be checking that
        // skip_normalization == 0, and just returning if so.
    }

    /**
     * If things want to prevent normalization because they are doing
     * a set of changes that should be normalized in one go, this is
     * what to call. Be sure to call lowerNormalizeProtection() when
     * you are done.
     */
    protected void raiseNormalizeProtection()
    {
        skip_normalization++;

        if (skip_normalization > 10)
        {
            // This is a bit drastic and does not give us much
            // chance to fix the error
            //   throw new LogicError();

            log.warn("skip_normalization="+skip_normalization, new Exception());
        }
    }

    /**
     * If things want to prevent normalization because they are doing
     * a set of changes that should be normalized in one go, they should
     * call raiseNormalizeProtection() and when done call this. This also
     * calls normalize() if the count reaches zero.
     */
    protected void lowerNormalizeProtection()
    {
        skip_normalization--;

        if (skip_normalization == 0)
            normalize();

        if (skip_normalization < 0)
            throw new LogicError();
    }

    /**
     * If things want to prevent event firing because they are doing
     * a set of changes that should be notified in one go, this is
     * what to call. Be sure to call lowerEventSuppression() when
     * you are done.
     */
    protected void raiseEventSuppresion()
    {
        suppress_events++;

        if (suppress_events > 10)
        {
            // This is a bit drastic and does not give us much
            // chance to fix the error
            //   throw new LogicError();

            log.warn("suppress_events="+suppress_events, new Exception());
        }
    }

    /**
     * If things want to prevent event firing because they are doing
     * a set of changes that should be notified in one go, they should
     * call raiseEventSuppression() and when done call this.
     * @return true if it is then safe to fire an event.
     */
    protected boolean lowerEventSuppresionAndTest()
    {
        suppress_events--;

        if (suppress_events < 0)
            throw new LogicError();

        return (suppress_events == 0);
    }

    /**
     * Convert the Object to a VerseRange. If base is a Verse then return a
     * VerseRange of zero length.
     * @param base The object to be cast
     * @return The VerseRange
     * @exception java.lang.ClassCastException If this is not a Verse or a VerseRange
     */
    protected static VerseRange toVerseRange(Object base) throws ClassCastException
    {
        if (base == null)
            throw new NullPointerException();

        if (base instanceof VerseRange)
        {
            return (VerseRange) base;
        }
        else if (base instanceof Verse)
        {
            return new VerseRange((Verse) base);
        }

        throw new ClassCastException(PassageUtil.getResource("abstract_error_cast"));
    }

    /**
     * Convert the Object to an array of Verses. If base is a VerseRange then return a
     * Verse array of the VersesRanges Verses.
     * @param base The Object to be cast
     * @return The Verse array
     * @exception java.lang.ClassCastException If this is not a Verse or a VerseRange
     */
    protected static Verse[] toVerseArray(Object base) throws ClassCastException
    {
        if (base == null)
            throw new NullPointerException();

        if (base instanceof VerseRange)
        {
            VerseRange range = (VerseRange) base;
            return range.toVerseArray();
        }
        else if (base instanceof Verse)
        {
            return new Verse[] { (Verse) base };
        }

        throw new ClassCastException(PassageUtil.getResource("abstract_error_cast"));
    }

    /**
     * Skip over verses that are part of a range
     */
    protected final class VerseRangeIterator implements Iterator
    {
        /**
         * iterate, amalgumating Verses into VerseRanges
         */
        public VerseRangeIterator()
        {
            it = verseIterator();
            if (it.hasNext()) next_verse = (Verse) it.next();
            calculateNext();
        }

        /**
         * @return true if the iteration has more element
         */
        public final boolean hasNext()
        {
            return next_range != null;
        }

        /**
         * @return the next element in the interation
         * @throws NoSuchElementException if next() is called too often
         */
        public final Object next() throws NoSuchElementException
        {
            Object retcode = next_range;
            calculateNext();
            return retcode;
        }

        /**
         * Not supported
         * @throws UnsupportedOperationException Every time ...
         */
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Find the next VerseRange
         */
        private void calculateNext()
        {
            if (next_verse == null)
            {
                next_range = null;
                return;
            }

            Verse start = next_verse;
            Verse end = next_verse;

            while (true)
            {
                if (!it.hasNext())
                {
                    next_verse = null;
                    break;
                }

                next_verse = (Verse) it.next();
                if (!end.adjacentTo(next_verse)) break;
                end = next_verse;
            }

            next_range = new VerseRange(start, end);
        }

        /** The Iterator that we are proxying to */
        private Iterator it;

        /** What is the next VerseRange to be considered */
        private VerseRange next_range = null;

        /** What is the next Verse to be considered */
        private Verse next_verse = null;
    }

    /**
     * Write out the object to the given ObjectOutputStream. There are 3
     * ways of doing this - according to the 3 implementations of
     * Passage.<ul>
     * <li>Distinct: If we write out a list if verse ordinals then the
     *     space used is 4 bytes per verse.
     * <li>Bitwise: If we write out a bitmap then the space used is
     *     something like 31104/8 = 4k bytes.
     * <li>Ranged: The we write a list of start/end pairs then the space
     *     used is 8 bytes per range.
     * </ul>
     * Since we can take our time about this section, we calculate the
     * optimal storage method before we do the saving. If some methods
     * come out equal first then bitwise is preferred, then distinct,
     * then ranged, because I imagine that for speed of de-serialization
     * this is the sensible order. I've not tested it though.
     * @throws IOException if the read fails
     * @param out The stream to write our state to
     */
    protected void writeObjectSupport(ObjectOutputStream out) throws IOException
    {
        // This allows our children to have default serializable fields
        // even though we have none.
        out.defaultWriteObject();

        // the size in bits of teach storage method
        int bitwise_size = Books.versesInBible();
        int ranged_size =  8 * countRanges();
        int distinct_size = 4 * countVerses();

        // if bitwise is equal smallest
        if (bitwise_size <= ranged_size && bitwise_size <= distinct_size)
        {
            out.writeInt(BITWISE);

            BitSet store = new BitSet(Books.versesInBible());
            Iterator it = verseIterator();
            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();
                store.set(verse.getOrdinal()-1);
            }

            out.writeObject(store);
        }

        // if distinct is not bigger than ranged
        else if (distinct_size <= ranged_size)
        {
            // write the Passage type and the number of verses
            out.writeInt(DISTINCT);
            out.writeInt(countVerses());

            // write the verse ordinals in a loop
            Iterator it = verseIterator();
            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();
                out.writeInt(verse.getOrdinal());
            }
        }

        // otherwise use ranges
        else
        {
            // write the Passage type and the number of ranges
            out.writeInt(RANGED);
            out.writeInt(countRanges());

            // write the verse ordinals in a loop
            Iterator it = rangeIterator();
            while (it.hasNext())
            {
                VerseRange range = (VerseRange) it.next();
                out.writeInt(range.getStart().getOrdinal());
                out.writeInt(range.getVerseCount());
            }
        }

        // Ignore the original name. Is this wise?
        // I am expecting that people are not that fussed about it and
        // it could make everything far more verbose
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * @throws IOException if the read fails
     * @throws ClassNotFoundException If the read data is incorrect
     * @param in The stream to read our state from
     */
    protected void readObjectSupport(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        raiseEventSuppresion();
        raiseNormalizeProtection();

        // This allows our children to have default serializable fields
        // even though we have none.
        in.defaultReadObject();

        // Setup
        listeners = new Vector();

        try
        {
            int type = in.readInt();
            switch (type)
            {
            case BITWISE:
                BitSet store = (BitSet) in.readObject();
                for (int i=0; i<Books.versesInBible(); i++)
                {
                    if (store.get(i))
                        add(new Verse(i+1));
                }
                break;

            case DISTINCT:
                int verses = in.readInt();
                for (int i=0; i<verses; i++)
                {
                    int ord = in.readInt();
                    add(new Verse(ord));
                }
                break;

            case RANGED:
                int ranges = in.readInt();
                for (int i=0; i<ranges; i++)
                {
                    int ord = in.readInt();
                    int count = in.readInt();
                    add(new VerseRange(new Verse(ord), count));
                }
                break;

            default:
                throw new ClassCastException(PassageUtil.getResource("abstract_error_cast"));
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new IOException(ex.getMessage());
        }

        // We are ignoring the original_name. It was set to null in the
        // default ctor so I will ignore it here.

        // We don't bother to call fireContentsChanged(...) because
        // nothing can have registered at this point
        lowerEventSuppresionAndTest();
        lowerNormalizeProtection();
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger(AbstractPassage.class);

    /** Serialization type constant for a BitWise layout */
    protected static final int BITWISE = 0;

    /** Serialization type constant for a Distinct layout */
    protected static final int DISTINCT = 1;

    /** Serialization type constant for a Ranged layout */
    protected static final int RANGED = 2;

    /** Count of serializations methods */
    protected static final int METHOD_COUNT = 3;

    /** Support for change notification */
    protected transient Vector listeners = new Vector();

    /** The original string for picky users */
    protected transient String original_name = null;

    /**
     * If we have several changes to make then we increment this and then
     * decrement it when done (and fire an event off). If the cost of
     * calculating the parameters to the fire is high then we can check that
     * this is 0 before doing the calculation.
     */
    protected transient int suppress_events = 0;

    /**
     * Do we skip normalization for now - if we want to skip then we increment
     * this, and the decrement it when done.
     */
    protected transient int skip_normalization = 0;
}
