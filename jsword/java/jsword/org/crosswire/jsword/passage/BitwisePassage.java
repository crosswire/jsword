package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A Passage that is implemented using a BitSet - one for each verse.
 * The attributes of the style are:<ul>
 * <li>Fairly fast manipulation
 * <li>Fairly getName()
 * <li>Static size, poor for small Passages, good for large Passages
 * </ul>
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
 * @author DM Smith [dmsmith555 at yahoo dot com]
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
     */
    public Object clone()
    {
        // This gets us a shallow copy
        BitwisePassage copy = (BitwisePassage) super.clone();

        copy.store = (BitSet) store.clone();

        return copy;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#countVerses()
     */
    public int countVerses()
    {
        return store.cardinality();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#isEmpty()
     */
    public boolean isEmpty()
    {
        return store.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#verseIterator()
     */
    public Iterator iterator()
    {
        return new VerseIterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.AbstractPassage#rangeIterator()
     */
    public Iterator rangeIterator(int restrict)
    {
        return new VerseRangeIterator(iterator(), restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#contains(org.crosswire.jsword.passage.VerseBase)
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#add(org.crosswire.jsword.passage.VerseBase)
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
        if (suppressEvents == 0)
        {
            fireIntervalAdded(this, verses[0], verses[verses.length - 1]);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#remove(org.crosswire.jsword.passage.VerseBase)
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
        if (suppressEvents == 0)
        {
            fireIntervalRemoved(this, verses[0], verses[verses.length - 1]);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#addAll(org.crosswire.jsword.passage.Passage)
     */
    public void addAll(Key key)
    {
        Passage that = KeyUtil.getPassage(key);

        optimizeWrites();

        if (that instanceof BitwisePassage)
        {
            BitwisePassage thatRef = (BitwisePassage) that;
            store.or(thatRef.store);
        }
        else
        {
            super.addAll(that);
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppressEvents == 0 && !that.isEmpty())
        {
            fireIntervalAdded(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#removeAll(org.crosswire.jsword.passage.Passage)
     */
    public void removeAll(Key key)
    {
        Passage that = KeyUtil.getPassage(key);

        optimizeWrites();

        if (that instanceof BitwisePassage)
        {
            BitwisePassage thatRef = (BitwisePassage) that;

            store.andNot(thatRef.store);
        }
        else
        {
            super.removeAll(key);
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppressEvents == 0 && !that.isEmpty())
        {
            fireIntervalRemoved(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#retainAll(org.crosswire.jsword.passage.Passage)
     */
    public void retainAll(Key key)
    {
        Passage that = KeyUtil.getPassage(key);

        optimizeWrites();

        BitSet thatStore = null;
        if (that instanceof BitwisePassage)
        {
            thatStore = ((BitwisePassage) that).store;
        }
        else
        {
            thatStore = new BitSet(BibleInfo.versesInBible() + 1);

            Iterator it = that.iterator();
            while (it.hasNext())
            {
                int ord = ((Verse) it.next()).getOrdinal();
                if (store.get(ord))
                {
                    thatStore.set(ord);
                }
            }
        }
        store.and(thatStore);

        fireIntervalRemoved(this, null, null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#clear()
     */
    public void clear()
    {
        optimizeWrites();

        store.clear();

        fireIntervalRemoved(this, null, null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#blur(int, int)
     */
    public void blur(int verses, int restrict)
    {
        optimizeWrites();
        raiseNormalizeProtection();

        if (verses < 0)
        {
            throw new IllegalArgumentException(Msg.ERROR_BLUR.toString());
        }

        if (restrict != PassageConstants.RESTRICT_NONE)
        {
            BitwisePassage temp = (BitwisePassage) this.clone();
            Iterator it = temp.rangeIterator(PassageConstants.RESTRICT_NONE);

            while (it.hasNext())
            {
                VerseRange range = new VerseRange((VerseRange) it.next(), verses, verses, restrict);
                add(range);
            }
        }
        else
        {
            int versesInBible = BibleInfo.versesInBible();
            BitSet newStore = new BitSet(versesInBible + 1);

            for (int i=store.nextSetBit(0); i>=0; i=store.nextSetBit(i+1))
            {
                int start = Math.max(1, i-verses);
                int end = Math.min(versesInBible, i+verses);

                for (int j=start; j<=end; j++)
                {
                    newStore.set(j);
                }
            }

            store = newStore;
        }

        lowerNormalizeProtection();
        fireIntervalAdded(this, null, null);
    }

    /**
     * Iterate over the Verses
     * @author Joe Walker
     * @author DM Smith
     */
    private final class VerseIterator implements Iterator
    {
        /**
         * Find the first unused verse
         */
        public VerseIterator()
        {
            next = -1;
            calculateNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
            return next >= 0;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() throws NoSuchElementException
        {
            try
            {
                if (!hasNext())
                {
                    throw new NoSuchElementException();
                }

                Object retcode = new Verse(next);
                calculateNext();

                return retcode;
            }
            catch (NoSuchVerseException ex)
            {
                assert false;
                return null;
            }
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
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
            next = store.nextSetBit(next+1);
        }

        /**
         * What is the next Verse to be considered
         */
        private int next;
    }

    /**
     * Call the support mechanism in AbstractPassage
     * @param out The stream to write our state to
     * @serialData Write the ordinal number of this verse
     * @see AbstractPassage#writeObjectSupport(ObjectOutputStream)
     * @throws IOException if the read fails
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

    /**
     * To make serialization work across new versions
     */
    static final long serialVersionUID = -5931560451407396276L;

    /**
     * The place the real data is stored
     */
    protected transient BitSet store = new BitSet(BibleInfo.versesInBible()+1);
}
