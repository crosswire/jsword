
package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.crosswire.common.util.LogicError;

/**
 * A Passage that is implemented using a BitSet - one for each verse.
 * The attributes of the style are:<ul>
 * <li>Fairly fast manipulation
 * <li>Fairly getName()
 * <li>Static size, poor for small Passages, good for large Passages
 * </ul>
 *
 * <p>There is some optimization we could do here: The benchmark I have
 * been using spends a lot of time in VerseEnumeration. There is some
 * inefficiency here due to having to examine the bits of the BitSet
 * one by one, rather than being able to compare the underlying longs
 * with zero (clearing 64 bits in one shot). This would speed up the
 * (usual) case where there are relatively few matches in the BitSet,
 * but be a slowdown for fuller Passages.<br />
 * The bad news is that this would mean re-writing BitSet which I am
 * not all that keen to do right now.</p>
 *
 * <p>The BitSet has one more bit than the number of verses in the
 * Bible. This would waste 1 bit per BitSet but since this doesn't
 * cause BitSet to need an extra long it doesn't, and it saves us some
 * maths.</p>
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
public class BitwisePassage extends AbstractPassage
{
    /**
     * Create an empty BitwisePassage. There are no ctors from either Verse
     * or VerseRange so you need to do new <code>DistinctPassage().add(...);</code>
     */
    protected BitwisePassage()
    {
    }

    /**
     * Create a Verse from a human readable string. The opposite
     * of toString(), Given any BitwisePassage v1, and the following
     * <code>DistinctPassage v2 = new BitwisePassage(v1.toString());</code>
     * Then <code>v1.equals(v2);</code>
     * Theoretically, since there are many ways of representing a BitwisePassage as text
     * string comparision along the lines of:
     * <code>v1.toString().equals(v2.toString())</code> could be false.
     * Practically since toString() is standardized this will be true however.
     * We don't need to worry about thread safety in a ctor since we don't exist yet.
     * @param refs A String containing the text of the BitwisePassage
     * @throws NoSuchVerseException If the string is not parsable
     */
    protected BitwisePassage(String refs) throws NoSuchVerseException
    {
        super(refs);
        addVerses(refs);
    }

    /**
     * Get a copy of ourselves. Points to note:
     *   Call clone() not new() on member Objects, and on us.
     *   Do not use Copy Constructors! - they do not inherit well.
     *   Think about this needing to be synchronized
     *   If this is not cloneable then writing cloneable children is harder
     * @return A complete copy of ourselves
     * @exception java.lang.CloneNotSupportedException We don't do this but our kids might
     */
    public Object clone() throws CloneNotSupportedException
    {
        // This gets us a shallow copy
        BitwisePassage copy = (BitwisePassage) super.clone();

        copy.store = (BitSet) store.clone();

        return copy;
    }

    /**
     * @return the number of Verses in this Passage
     */
    public int countVerses()
    {
        int count = 0;

        int vib = BibleInfo.versesInBible();
        for (int i=1; i<=vib; i++)
        {
            if (store.get(i))
            {
                count++;
            }
        }

        return count;
    }

    /**
     * @return true if this Passage contains no Verses
     */
    public boolean isEmpty()
    {
        int vib = BibleInfo.versesInBible();
        for (int i=1; i<=vib; i++)
        {
            if (store.get(i))
            {
                return false;
            }
        }

        return true;
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
     * Enumerate over the VerseRanges
     * @return A list enumerator
     */
    public Iterator rangeIterator()
    {
        return new VerseRangeIterator(verseIterator());
    }

    /**
     * Returns true if this Passage contains the specified Verse
     * @param obj Verse whose presence in this Passage is to be tested
     * @return true if this Passage contains the specified Verse
     */
    public boolean contains(VerseBase obj)
    {
        Verse[] verses = toVerseArray(obj);

        for (int i=0; i<verses.length; i++)
        {
            if (!store.get(verses[i].getOrdinal()))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Ensures that this Passage contains the specified Verse
     * @param obj Verse whose presence in this Passage is to be ensured
     */
    public void add(VerseBase obj)
    {
        optimizeWrites();

        Verse[] verses = toVerseArray(obj);

        for (int i=0; i<verses.length; i++)
        {
            store.set(verses[i].getOrdinal());
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppress_events == 0)
        {
            fireIntervalAdded(this, verses[0], verses[verses.length - 1]);
        }
    }

    /**
     * Removes a single instance of the specified Verse from this Passage
     * @param obj Verse to be removed from this Passage, if present
     */
    public void remove(VerseBase obj)
    {
        optimizeWrites();

        Verse[] verses = toVerseArray(obj);

        for (int i=0; i<verses.length; i++)
        {
            store.clear(verses[i].getOrdinal());
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppress_events == 0)
        {
            fireIntervalRemoved(this, verses[0], verses[verses.length - 1]);
        }
    }

    /**
     * Adds the Verses in that Passage to this Passage
     * @param that Verses to be added to this Passage
     */
    public void addAll(Passage that)
    {
        optimizeWrites();

        if (that instanceof BitwisePassage)
        {
            BitwisePassage that_ref = (BitwisePassage) that;
            store.or(that_ref.store);
        }
        else
        {
            super.addAll(that);
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppress_events == 0 && !that.isEmpty())
        {
            fireIntervalAdded(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
        }
    }

    /**
     * Removes the Verses in this Passage that are contained in the
     * specified Passage.  In other words, removes from this Passage
     * all of its Verses that are not contained in the specified Passage
     * @param that Verses to be removed from this Passage
     */
    public void removeAll(Passage that)
    {
        optimizeWrites();

        if (that instanceof BitwisePassage)
        {
            BitwisePassage that_ref = (BitwisePassage) that;

            store.andNot(that_ref.store);
        }
        else
        {
            super.removeAll(that);
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppress_events == 0 && !that.isEmpty())
        {
            fireIntervalRemoved(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
        }
    }

    /**
     * Retains only the Verses in this Passage that are contained in the
     * specified Passage. In other words, removes from this Passage
     * all of its Verses that are not contained in the specified Passage
     * @param that Verses to be retained in this Passage.
     */
    public void retainAll(Passage that)
    {
        optimizeWrites();

        if (that instanceof BitwisePassage)
        {
            BitSet that_store = ((BitwisePassage) that).store;
            store.and(that_store);
        }
        else
        {
            BitSet new_store = new BitSet(BibleInfo.versesInBible());

            Iterator it = that.verseIterator();
            while (it.hasNext())
            {
                int ord = ((Verse) it.next()).getOrdinal();
                if (store.get(ord))
                {
                    new_store.set(ord);
                }
            }

            store = new_store;
        }

        fireIntervalRemoved(this, null, null);
    }

    /**
     * Removes all of the Verses from this Passage
     */
    public void clear()
    {
        optimizeWrites();

        int vib = BibleInfo.versesInBible();
        for (int i=1; i<=vib; i++)
        {
            store.clear(i);
        }

        fireIntervalRemoved(this, null, null);
    }

    /**
     * Widen the range of the verses in this list. This is primarily for
     * "find x within n verses of y" type applications.
     * @param verses The number of verses to widen by
     * @param restrict How should we restrict the blurring?
     * @exception IllegalArgumentException If a blurring is negative or the restrict mode is illegal
     * @see Passage
     */
    public void blur(int verses, int restrict)
    {
        optimizeWrites();
        raiseNormalizeProtection();

        if (verses < 0)
        {
            throw new IllegalArgumentException(Msg.ERROR_BLUR.getName());
        }

        if (restrict != RESTRICT_NONE)
        {
            // This is a bit of a cheat, but there is no way I'm going
            // to do the maths to speed up the restricted version
            try
            {
                BitwisePassage temp = (BitwisePassage) this.clone();
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
        }
        else
        {
            BitSet new_store = new BitSet(BibleInfo.versesInBible());

            int vib = BibleInfo.versesInBible();
            for (int i=1; i<=vib; i++)
            {
                if (store.get(i))
                {
                    int start = Math.max(0, i-verses);
                    int end = Math.min(BibleInfo.versesInBible(), i+verses);

                    for (int j=start; j<=end; j++)
                    {
                        new_store.set(j);
                    }
                }
            }

            store = new_store;
        }

        lowerNormalizeProtection();
        fireIntervalAdded(this, null, null);
    }

    /**
     * Iterate over the Verses
     * @author Joe Walker
     */
    private final class VerseIterator implements Iterator
    {
        /**
         * Find the first unused verse
         */
        public VerseIterator()
        {
            calculateNext();
        }

        /**
         * @return true if the iteration has more Verses
         */
        public boolean hasNext()
        {
            return next <= BibleInfo.versesInBible();
        }

        /**
         * @return the next Verse in the interation
         * @throws NoSuchElementException if hasNext() == false
         */
        public Object next() throws NoSuchElementException
        {
            try
            {
                if (next > BibleInfo.versesInBible())
                {
                    throw new NoSuchElementException();
                }

                Object retcode = new Verse(next);
                calculateNext();

                return retcode;
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
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
         * Find the next bit
         */
        private void calculateNext()
        {
            while (next <= BibleInfo.versesInBible())
            {
                next++;
                if (store.get(next))
                {
                    break;
                }
            }
        }

        /** What is the next Verse to be considered */
        private int next = 0;
    }

    /**
     * Call the support mechanism in AbstractPassage
     * @param out The stream to write our state to
     * @serialData Write the ordinal number of this verse
     * @see AbstractPassage#writeObjectSupport(ObjectOutputStream)
     * @throws IOException if the read fails
     * @throws ClassNotFoundException If the read data is incorrect
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        writeObjectSupport(out);
    }

    /**
     * Call the support mechanism in AbstractPassage
     * @param in The stream to read our state from
     * @throws IOException if the read fails
     * @throws ClassNotFoundException If the read data is incorrect
     * @serialData Write the ordinal number of this verse
     * @see AbstractPassage#readObjectSupport(ObjectInputStream)
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        optimizeWrites();

        store = new BitSet(BibleInfo.versesInBible()+1);
        readObjectSupport(in);
    }

    /** To make serialization work across new versions */
    static final long serialVersionUID = -5931560451407396276L;

    /** The place the real data is stored */
    protected transient BitSet store = new BitSet(BibleInfo.versesInBible()+1);
}
