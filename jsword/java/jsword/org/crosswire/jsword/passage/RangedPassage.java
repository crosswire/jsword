
package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.crosswire.common.util.LogicError;

/**
 * A Passage that is implemented using a TreeSet of VerseRanges.
 * The attributes of the style are:<ul>
 * <li>Compact storage of large amounts of data
 * <li>Fast getName()
 * <li>Slow manipulation
 * </ul>
 *
 * <p>When to normalize()? This is a slow process, but one that is perhaps
 * done bit-by-bit instead of killing everything just to do getName().
 * The options are:<ul>
 * <li>Before every read</li>
 * <li>Before reads with a background thread</li>
 * <li>After every change</li>
 * <li>After every change with a cacheing scheme</li>
 * </ul>
 * I'm not sure which will be best. So I'm starting with 1 and
 * optimizing later ... Maybe the best is to allow the user to choose?
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
public class RangedPassage extends AbstractPassage
{
    /**
     * Create an empty RangedPassage. There are no ctors from either Verse
     * or VerseRange so you need to do new <code>RangedPassage().add(...);</code>
     */
    protected RangedPassage()
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
    protected RangedPassage(String refs) throws NoSuchVerseException
    {
        super(refs);

        store = Collections.synchronizedSortedSet(new TreeSet());
        addVerses(refs);
        normalize();
    }

    /**
     * Get a copy of ourselves. Points to note:
     *   Call clone() not new() on member Objects, and on us.
     *   Do not use Copy Constructors! - they do not inherit well.
     *   Think about this needing to be synchronized
     *   If this is not cloneable then writing cloneable children is harder
     * @return A complete copy of ourselves
     * @exception CloneNotSupportedException We don't do this but our kids might
     */
    public Object clone() throws CloneNotSupportedException
    {
        // This gets us a shallow copy
        RangedPassage copy = (RangedPassage) super.clone();

        // I want to just do the following
        //   copy.store = (SortedSet) store.clone();
        // However SortedSet is not Clonable so I can't
        // Watch out for this, I'm not sure if it breaks anything.
        copy.store = Collections.synchronizedSortedSet(new TreeSet());
        copy.store.addAll(store);

        return copy;
    }

    /**
     * @return the number of VerseRanges in this Passage
     */
    public int countRanges()
    {
        return store.size();
    }

    /**
     * @return the number of Verses in this Passage
     */
    public int countVerses()
    {
        Iterator it = rangeIterator();
        int count = 0;

        while (it.hasNext())
        {
            VerseRange range = (VerseRange) it.next();
            count += range.getVerseCount();
        }

        return count;
    }

    /**
     * Iterate over the Verses
     * @return A list enumerator
     */
    public Iterator verseIterator()
    {
        return new VerseIterator();
    }

    /**
     * Iterate over the VerseRanges
     * @return A list enumerator
     */
    public Iterator rangeIterator()
    {
        return store.iterator();
    }

    /**
     * @return true if this Passage contains no Verses
     */
    public boolean isEmpty()
    {
        return store.isEmpty();
    }

    /**
     * Returns true if this Passage contains the specified Verse.
     * @param obj Verse whose presence in this Passage is to be tested.
     * @return true if this Passage contains the specified Verse
     */
    public boolean contains(VerseBase obj)
    {
        // Even for the conatins(VerseRange) case, the simple
        // 'return store.contains(that);' will not work because
        // VerseRanges can contain others but not be equal to them.

        VerseRange that_range = toVerseRange(obj);

        Iterator it = rangeIterator();
        while (it.hasNext())
        {
            VerseRange this_range = (VerseRange) it.next();
            if (this_range.contains(that_range)) return true;
        }

        // If it is not a Verse or a VerseRange then it's not here,
        // this also copes with the searches failing.
        return false;
    }

    /**
     * Ensures that this Passage contains the specified Verse
     * @param obj Verse whose presence in this Passage is to be ensured.
     */
    public void add(VerseBase obj)
    {
        optimizeWrites();

        VerseRange that_range = toVerseRange(obj);
        store.add(that_range);

        normalize();

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppress_events == 0)
            fireIntervalAdded(this, that_range.getStart(), that_range.getEnd());
    }

    /**
     * Removes all of the Verses from this Passage.
     */
    public void clear()
    {
        optimizeWrites();

        store.clear();
        fireIntervalRemoved(this, null, null);
    }

    /**
     * Removes a single instance of the specified Verse from this Passage
     * @param obj Verse to be removed from this Passage, if present.
     */
    public void remove(VerseBase obj)
    {
        optimizeWrites();

        VerseRange that_range = toVerseRange(obj);
        boolean removed = false;

        // This allows us to modify store which iterating through a copy
        SortedSet new_store = Collections.synchronizedSortedSet(new TreeSet());
        new_store.addAll(store);
        Iterator it = new_store.iterator();

        // go through all the VerseRanges
        while (it.hasNext())
        {
            // if this range touches the range to be removed ...
            VerseRange this_range = (VerseRange) it.next();
            if (this_range.overlaps(that_range))
            {
                // ... remove it and add the remainder
                store.remove(this_range);
                VerseRange[] vra = VerseRange.remainder(this_range, that_range);

                for (int i=0; i<vra.length; i++)
                {
                    store.add(vra[i]);
                }

                removed = true;
            }
        }

        if (removed) normalize();

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppress_events == 0)
            fireIntervalRemoved(this, that_range.getStart(), that_range.getEnd());
    }

    /**
     * Retains only the Verses in this Passage that are contained in the
     * specified Passage
     * @param that Verses to be retained in this Passage.
     */
    public void retainAll(Passage that)
    {
        optimizeWrites();

        SortedSet new_store = Collections.synchronizedSortedSet(new TreeSet());

        Iterator that_it = null;
        if (that instanceof Passage)  that_it = ((Passage) that).rangeIterator();
        else                          that_it = that.verseIterator();

        while (that_it.hasNext())
        {
            VerseRange that_range = toVerseRange(that_it.next());

            // go through all the VerseRanges
            Iterator this_it = rangeIterator();
            while (this_it.hasNext())
            {
                // if this range touches the range to be removed ...
                VerseRange this_range = (VerseRange) this_it.next();
                if (this_range.overlaps(that_range))
                {
                    // ... remove it and add the remainder
                    VerseRange interstect = VerseRange.intersection(this_range, that_range);
                    if (interstect != null) new_store.add(interstect);
                }
            }
        }

        store = new_store;
        normalize();

        fireIntervalRemoved(this, null, null);
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
        if (skip_normalization != 0) return;

        VerseRange last = null;
        VerseRange next = null;
        SortedSet new_store = Collections.synchronizedSortedSet(new TreeSet());

        Iterator it = rangeIterator();
        while (it.hasNext())
        {
            next = (VerseRange) it.next();

            if (last != null && next.adjacentTo(last))
            {
                VerseRange merge = new VerseRange(last, next);

                new_store.remove(last);
                new_store.add(merge);

                last = merge;
            }
            else
            {
                new_store.add(next);
                last = next;
            }
        }

        store = new_store;
    }

    /**
     * This class is here to prevent users of RangedPassage.iterator() from
     * altering the underlying store and getting us out of sync. Right
     * now there are no issues with someone else removing a RangedPassage
     * without telling us, however there may be some day, and I'm not
     * sure that we need the functionality right now.
     * Also buy using this we get to ensure synchronization.
     * Everything is final so to save the proxying performace hit.
     * @author Joe Walker
     */
    private final class VerseIterator implements Iterator
    {
        /**
         * Create a basic iterator that is a proxy for the RangedPassage Passages
         * iterator, with remove() overridden.
         */
        public VerseIterator()
        {
            try
            {
                SortedSet temp = Collections.synchronizedSortedSet(new TreeSet());
                Iterator it = rangeIterator();

                while (it.hasNext())
                {
                    VerseRange range = (VerseRange) it.next();

                    for (int i=0; i<range.getVerseCount(); i++)
                    {
                        temp.add(new Verse(range.getStart().getOrdinal()+i));
                    }
                }

                real = temp.iterator();
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
        }

        /**
         * Pass the request on to the real iterator.
         * @return true if the iteration has more Verses.
         */
        public final boolean hasNext()
        {
            return real.hasNext();
        }

        /**
         * Pass the request on to the real iterator.
         * @return the next Verse in the interation.
         * @throws NoSuchElementException if hasNext() == false
         */
        public final Object next() throws NoSuchElementException
        {
            return real.next();
        }

        /**
         * Not supported
         * @throws UnsupportedOperationException Every time ...
         */
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

        /** The Iterator that we are proxying to */
        private Iterator real;
    }

    /**
     * Call the support mechanism in AbstractPassage
     * @param out The stream to write our state to
     * @serialData Write the ordinal number of this verse
     * @throws IOException if the read fails
     * @see AbstractPassage#writeObjectSupport(ObjectOutputStream)
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        writeObjectSupport(out);
    }

    /**
     * Call the support mechanism in AbstractPassage
     * @param in The stream to read our state from
     * @serialData Write the ordinal number of this verse
     * @throws IOException if the read fails
     * @throws ClassNotFoundException If the read data is incorrect
     * @see AbstractPassage#readObjectSupport(ObjectInputStream)
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        optimizeWrites();

        store = Collections.synchronizedSortedSet(new TreeSet());
        readObjectSupport(in);
    }

    /** To make serialization work across new versions */
    static final long serialVersionUID = 955115811339960826L;

    /** The place the real data is stored */
    private transient SortedSet store = Collections.synchronizedSortedSet(new TreeSet());
}
