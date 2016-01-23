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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * A Passage that is implemented using a BitSet - one for each verse. The
 * attributes of the style are:
 * <ul>
 * <li>Fairly fast manipulation
 * <li>Fairly getName()
 * <li>Static size, poor for small Passages, good for large Passages
 * </ul>
 * 
 * <p>
 * The BitSet has one more bit than the number of verses in the Bible. This
 * would waste 1 bit per BitSet but since this doesn't cause BitSet to need an
 * extra long it doesn't, and it saves us some maths.
 * </p>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class BitwisePassage extends AbstractPassage {
    /**
     * Create an empty BitwisePassage. There are no ctors from either Verse or
     * VerseRange so you need to do new <code>DistinctPassage().add(...);</code>
     * 
     * @param v11n
     *            The Versification to which this Passage belongs.
     */
    public BitwisePassage(Versification v11n) {
        super(v11n);
        store = new BitSet(v11n.maximumOrdinal() + 1);
    }

    /**
     * Create a Verse from a human readable string. The opposite of toString(),
     * Given any BitwisePassage v1, and the following
     * <code>DistinctPassage v2 = new BitwisePassage(v1.toString());</code> Then
     * <code>v1.equals(v2);</code> Theoretically, since there are many ways of
     * representing a BitwisePassage as text string comparison along the lines
     * of: <code>v1.toString().equals(v2.toString())</code> could be false.
     * Practically since toString() is standardized this will be true however.
     * We don't need to worry about thread safety in a ctor since we don't exist
     * yet.
     * 
     * @param v11n
     *            The Versification to which this Passage belongs.
     * @param refs
     *            A String containing the text of the BitwisePassage
     * @param basis
     *           The basis by which to interpret refs
     * @throws NoSuchVerseException
     *             If the string is not parsable
     */
    protected BitwisePassage(Versification v11n, String refs, Key basis) throws NoSuchVerseException {
        super(v11n, refs);
        store = new BitSet(v11n.maximumOrdinal() + 1);
        addVerses(refs, basis);
    }

    /**
     * Create a Verse from a human readable string. The opposite of toString(),
     * Given any BitwisePassage v1, and the following
     * <code>DistinctPassage v2 = new BitwisePassage(v1.toString());</code> Then
     * <code>v1.equals(v2);</code> Theoretically, since there are many ways of
     * representing a BitwisePassage as text string comparison along the lines
     * of: <code>v1.toString().equals(v2.toString())</code> could be false.
     * Practically since toString() is standardized this will be true however.
     * We don't need to worry about thread safety in a ctor since we don't exist
     * yet.
     * 
     * @param v11n
     *            The Versification to which this Passage belongs.
     * @param refs
     *            A String containing the text of the BitwisePassage
     * @throws NoSuchVerseException
     *             If the string is not parsable
     */
    protected BitwisePassage(Versification v11n, String refs) throws NoSuchVerseException {
        this(v11n, refs, null);
    }

    @Override
    public BitwisePassage clone() {
        // This gets us a shallow copy
        BitwisePassage copy = (BitwisePassage) super.clone();

        copy.store = (BitSet) store.clone();

        return copy;
    }

    @Override
    public int countVerses() {
        return store.cardinality();
    }

    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Key> iterator() {
        return new VerseIterator();
    }

    @Override
    public boolean contains(Key obj) {
        for (Key aKey : obj) {
            Verse verse = (Verse) aKey;
            if (!store.get(verse.getOrdinal())) {
                return false;
            }
        }

        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#add(org.crosswire.jsword.passage.Key)
     */
    public void add(Key obj) {
        optimizeWrites();

        Verse firstVerse = null;
        Verse lastVerse = null;
        for (Key aKey : obj) {
            lastVerse = (Verse) aKey;
            if (firstVerse == null) {
                firstVerse = lastVerse;
            }
            store.set(lastVerse.getOrdinal());
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero and may be wasted
        if (suppressEvents == 0) {
            fireIntervalAdded(this, firstVerse, lastVerse);
        }
    }

    /**
     * A shortcut to adding a key, by ordinal. The ordinal needs to be taken
     * from the same versification as the passage being created.
     * 
     * @param ordinal
     *            the ordinal
     */
    public void addVersifiedOrdinal(int ordinal) {
        optimizeWrites();
        store.set(ordinal);

        // we do an extra check here because the cost of calculating the
        // params is non-zero and may be wasted
        if (suppressEvents == 0) {
            Verse verse = getVersification().decodeOrdinal(ordinal);
            fireIntervalAdded(this, verse, verse);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#remove(org.crosswire.jsword.passage.Key)
     */
    public void remove(Key obj) {
        optimizeWrites();

        Verse firstVerse = null;
        Verse lastVerse = null;
        for (Key aKey : obj) {
            lastVerse = (Verse) aKey;
            if (firstVerse == null) {
                firstVerse = lastVerse;
            }
            store.clear(lastVerse.getOrdinal());
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero and may be wasted
        if (suppressEvents == 0) {
            fireIntervalAdded(this, firstVerse, lastVerse);
        }
    }

    @Override
    public void addAll(Key key) {
        //check for key empty. This avoids the AIOBounds with that.getVerseAt, during event firing
        if (key.isEmpty()) {
            //nothing to add
            return;
        }

        optimizeWrites();

        if (key instanceof BitwisePassage) {
            BitwisePassage thatRef = (BitwisePassage) key;
            store.or(thatRef.store);
        } else {
            super.addAll(key);
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero and may be wasted
        if (suppressEvents == 0 && !key.isEmpty()) {
            if (key instanceof Passage) {
                Passage that = (Passage) key;
                fireIntervalAdded(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
            } else if (key instanceof VerseRange) {
                VerseRange that = (VerseRange) key;
                fireIntervalAdded(this, that.getStart(), that.getEnd());
            } else if (key instanceof Verse) {
                Verse that = (Verse) key;
                fireIntervalAdded(this, that, that);
            }
        }
    }

    @Override
    public void removeAll(Key key) {
        optimizeWrites();

        if (key instanceof BitwisePassage) {
            BitwisePassage thatRef = (BitwisePassage) key;

            store.andNot(thatRef.store);
        } else {
            super.removeAll(key);
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero and may be wasted
        if (suppressEvents == 0 && !key.isEmpty()) {
            if (key instanceof Passage) {
                Passage that = (Passage) key;
                fireIntervalRemoved(this, that.getVerseAt(0), that.getVerseAt(that.countVerses() - 1));
            } else if (key instanceof VerseRange) {
                VerseRange that = (VerseRange) key;
                fireIntervalRemoved(this, that.getStart(), that.getEnd());
            } else if (key instanceof Verse) {
                Verse that = (Verse) key;
                fireIntervalRemoved(this, that, that);
            }
        }
    }

    @Override
    public void retainAll(Key key) {
        optimizeWrites();

        BitSet thatStore = null;
        if (key instanceof BitwisePassage) {
            thatStore = ((BitwisePassage) key).store;
        } else {
            Versification v11n = getVersification();
            thatStore = new BitSet(v11n.maximumOrdinal() + 1);

            for (Key aKey : key) {
                int ord = ((Verse) aKey).getOrdinal();
                if (store.get(ord)) {
                    thatStore.set(ord);
                }
            }
        }
        store.and(thatStore);

        fireIntervalRemoved(this, null, null);
    }

    @Override
    public void clear() {
        optimizeWrites();

        store.clear();

        fireIntervalRemoved(this, null, null);
    }

    @Override
    public void blur(int verses, RestrictionType restrict) {
        assert verses >= 0;
        optimizeWrites();
        raiseNormalizeProtection();

        if (!restrict.equals(RestrictionType.NONE)) {
            super.blur(verses, restrict);
        } else {
            optimizeWrites();
            raiseEventSuppresion();
            raiseNormalizeProtection();

            int maximumOrdinal = getVersification().maximumOrdinal();
            BitSet newStore = new BitSet(maximumOrdinal + 1);

            for (int i = store.nextSetBit(0); i >= 0; i = store.nextSetBit(i + 1)) {
                int start = Math.max(1, i - verses);
                int end = Math.min(maximumOrdinal, i + verses);

                for (int j = start; j <= end; j++) {
                    newStore.set(j);
                }
            }

            store = newStore;

            lowerNormalizeProtection();
            if (lowerEventSuppressionAndTest()) {
                fireIntervalAdded(this, null, null);
            }
        }
    }

    /**
     * Iterate over the Verses
     * 
     * @author Joe Walker
     * @author DM Smith
     */
    private final class VerseIterator implements Iterator<Key> {
        /**
         * Find the first unused verse
         */
        VerseIterator() {
            next = -1;
            calculateNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return next >= 0;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Key next() throws NoSuchElementException {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Key retcode = getVersification().decodeOrdinal(next);
            calculateNext();

            return retcode;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() throws UnsupportedOperationException {
            store.clear(next);
        }

        /**
         * Find the next bit
         */
        private void calculateNext() {
            next = store.nextSetBit(next + 1);
        }

        /**
         * What is the next Verse to be considered
         */
        private int next;
    }

    /**
     * Call the support mechanism in AbstractPassage
     * 
     * @param out
     *            The stream to write our state to
     * @serialData Write the ordinal number of this verse
     * @see AbstractPassage#writeObjectSupport(ObjectOutputStream)
     * @throws IOException
     *             if the read fails
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        // Save off the versification by name
        out.writeUTF(getVersification().getName());

        writeObjectSupport(out);
    }

    /**
     * Call the support mechanism in AbstractPassage
     * 
     * @param in
     *            The stream to read our state from
     * @throws IOException
     *             if the read fails
     * @throws ClassNotFoundException
     *             If the read data is incorrect
     * @serialData Write the ordinal number of this verse
     * @see AbstractPassage#readObjectSupport(ObjectInputStream)
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        optimizeWrites();

        in.defaultReadObject();

        // Read the versification by name
        String v11nName = in.readUTF();
        Versification v11n = Versifications.instance().getVersification(v11nName);

        store = new BitSet(v11n.maximumOrdinal() + 1);

        readObjectSupport(in);
    }

    /**
     * To make serialization work across new versions
     */
    private static final long serialVersionUID = -5931560451407396276L;

    /**
     * The place the real data is stored
     */
    protected transient BitSet store;
}
