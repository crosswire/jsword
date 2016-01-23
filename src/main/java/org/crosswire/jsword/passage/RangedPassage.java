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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import org.crosswire.jsword.versification.Versification;

/**
 * A Passage that is implemented using a TreeSet of VerseRanges. The attributes
 * of the style are:
 * <ul>
 * <li>Compact storage of large amounts of data
 * <li>Fast getName()
 * <li>Slow manipulation
 * </ul>
 * 
 * <p>
 * When to normalize()? This is a slow process, but one that is perhaps done
 * bit-by-bit instead of killing everything just to do getName(). The options
 * are:
 * <ul>
 * <li>Before every read</li>
 * <li>Before reads with a background thread</li>
 * <li>After every change</li>
 * <li>After every change with a caching scheme</li>
 * </ul>
 * I'm not sure which will be best. So I'm starting with 1 and optimizing later
 * ... Maybe the best is to allow the user to choose?
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class RangedPassage extends AbstractPassage {
    /**
     * Create an empty RangedPassage. There are no ctors from either Verse or
     * VerseRange so you need to do new <code>RangedPassage().add(...);</code>
     * 
     * @param refSystem
     *            The Versification to which this Passage belongs.
     */
    public RangedPassage(Versification refSystem) {
        super(refSystem);
        store = new TreeSet<VerseRange>();
    }

    /**
     * Create a Verse from a human readable string. The opposite of getName(),
     * Given any RangedPassage v1, and the following
     * <code>RangedPassage v2 = new RangedPassage(v1.getName());</code> Then
     * <code>v1.equals(v2);</code> Theoretically, since there are many ways of
     * representing a RangedPassage as text string comparison along the lines
     * of: <code>v1.getName().equals(v2.getName())</code> could be false.
     * However since getName() is standardized this will be true. We don't need
     * to worry about thread safety in a ctor since we don't exist yet.
     * 
     * @param v11n
     *            The Versification to which this Passage belongs.
     * @param refs
     *            A String containing the text of the RangedPassage
     * @param basis
     *           The basis by which to interpret refs
     * @throws NoSuchVerseException
     *             if refs is invalid
     */
    protected RangedPassage(Versification v11n, String refs, Key basis) throws NoSuchVerseException {
        super(v11n, refs);

        store = new TreeSet<VerseRange>();
        addVerses(refs, basis);
        normalize();
    }

    protected RangedPassage(Versification v11n, String refs) throws NoSuchVerseException {
        this(v11n, refs, null);
    }

    @Override
    public RangedPassage clone() {
        // This gets us a shallow copy
        RangedPassage copy = (RangedPassage) super.clone();

        // I want to just do the following
        // copy.store = (SortedSet) store.clone();
        // However SortedSet is not Cloneable so I can't
        // Watch out for this, I'm not sure if it breaks anything.
        copy.store = new TreeSet<VerseRange>();
        copy.store.addAll(store);

        return copy;
    }

    @Override
    public int countRanges(RestrictionType restrict) {
        if (restrict.equals(RestrictionType.NONE)) {
            return store.size();
        }

        return super.countRanges(restrict);
    }

    @Override
    public int countVerses() {
        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        int count = 0;

        while (it.hasNext()) {
            VerseRange range = it.next();
            count += range.getCardinality();
        }

        return count;
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Key> iterator() {
        return new VerseIterator(getVersification(), rangeIterator(RestrictionType.NONE));
    }

    @Override
    public final Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
        if (restrict.equals(RestrictionType.NONE)) {
            return store.iterator();
        }

        return new VerseRangeIterator(store.iterator(), restrict);
    }

    @Override
    public boolean isEmpty() {
        return store.isEmpty();
    }

    @Override
    public boolean contains(Key obj) {
        // Even for the contains(VerseRange) case, the simple
        // 'return store.contains(that);' will not work because
        // VerseRanges can contain others but not be equal to them.

        VerseRange thatRange = toVerseRange(getVersification(), obj);

        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        while (it.hasNext()) {
            VerseRange thisRange = it.next();
            if (thisRange.contains(thatRange)) {
                return true;
            }
        }

        // If it is not a Verse or a VerseRange then it's not here,
        // this also copes with the searches failing.
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#add(org.crosswire.jsword.passage.Key)
     */
    public void add(Key obj) {
        optimizeWrites();

        VerseRange thatRange = toVerseRange(getVersification(), obj);
        store.add(thatRange);

        normalize();

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppressEvents == 0) {
            fireIntervalAdded(this, thatRange.getStart(), thatRange.getEnd());
        }
    }

    @Override
    public void clear() {
        optimizeWrites();

        store.clear();
        fireIntervalRemoved(this, null, null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#remove(org.crosswire.jsword.passage.Key)
     */
    public void remove(Key obj) {
        optimizeWrites();

        VerseRange thatRange = toVerseRange(getVersification(), obj);
        boolean removed = false;

        // This allows us to modify store which iterating through a copy
        Set<Key> newStore = new TreeSet<Key>();
        newStore.addAll(store);

        // go through all the VerseRanges
        for (Key aKey : newStore) {
            // if this range touches the range to be removed ...
            VerseRange thisRange = (VerseRange) aKey;
            if (thisRange.overlaps(thatRange)) {
                // ... remove it and add the remainder
                store.remove(thisRange);
                VerseRange[] vra = VerseRange.remainder(thisRange, thatRange);

                for (int i = 0; i < vra.length; i++) {
                    store.add(vra[i]);
                }

                removed = true;
            }
        }

        if (removed) {
            normalize();
        }

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppressEvents == 0) {
            fireIntervalRemoved(this, thatRange.getStart(), thatRange.getEnd());
        }
    }

    @Override
    public void retainAll(Key key) {
        optimizeWrites();

        Set<VerseRange> newStore = new TreeSet<VerseRange>();

        if (key instanceof RangedPassage) {
            Iterator<VerseRange> thatIter = ((RangedPassage) key).rangeIterator(RestrictionType.CHAPTER);
            while (thatIter.hasNext()) {
                VerseRange thatRange = thatIter.next();

                // go through all the VerseRanges
                Iterator<VerseRange> thisIter = rangeIterator(RestrictionType.NONE);
                while (thisIter.hasNext()) {
                    // if this range touches the range to be removed ...
                    VerseRange thisRange = thisIter.next();
                    if (thisRange.overlaps(thatRange)) {
                        // ... remove it and add the remainder
                        VerseRange interstect = VerseRange.intersection(thisRange, thatRange);
                        if (interstect != null) {
                            newStore.add(interstect);
                        }
                    }
                }
            }
        } else {
            Iterator<Key> thatIter = key.iterator();
            while (thatIter.hasNext()) {
                VerseRange thatRange = toVerseRange(getVersification(), thatIter.next());

                // go through all the VerseRanges
                Iterator<VerseRange> thisIter = rangeIterator(RestrictionType.NONE);
                while (thisIter.hasNext()) {
                    // if this range touches the range to be removed ...
                    VerseRange thisRange = thisIter.next();
                    if (thisRange.overlaps(thatRange)) {
                        // ... remove it and add the remainder
                        VerseRange interstect = VerseRange.intersection(thisRange, thatRange);
                        if (interstect != null) {
                            newStore.add(interstect);
                        }
                    }
                }
            }
        }


        store = newStore;
        normalize();

        fireIntervalRemoved(this, null, null);
    }

    /**
     * We sometimes need to sort ourselves out ...
     * <p>
     * I don't think we need to be synchronized since we are private and we
     * could check that all public calling of normalize() are synchronized,
     * however this is safe, and I don't think there is a cost associated with a
     * double synchronize. (?)
     */
    @Override
    /* protected */final void normalize() {
        if (skipNormalization != 0) {
            return;
        }

        VerseRange last = null;
        VerseRange next = null;
        Set<VerseRange> newStore = new TreeSet<VerseRange>();

        Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
        while (it.hasNext()) {
            next = it.next();

            if (last != null && next.adjacentTo(last)) {
                VerseRange merge = new VerseRange(last, next);

                newStore.remove(last);
                newStore.add(merge);

                last = merge;
            } else {
                newStore.add(next);
                last = next;
            }
        }

        store = newStore;
    }

    /**
     * This class is here to prevent users of RangedPassage.iterator() from
     * altering the underlying store and getting us out of sync. Right now there
     * are no issues with someone else removing a RangedPassage without telling
     * us, however there may be some day, and I'm not sure that we need the
     * functionality right now. Also buy using this we get to ensure
     * synchronization. Everything is final so to save the proxying performace
     * hit.
     */
    private static final class VerseIterator implements Iterator<Key> {
        /**
         * Create a basic iterator that is a proxy for the RangedPassage
         * Passages iterator, with remove() overridden.
         * @param v11n
         *            the versification to which this reference pertains
         * @param it 
         */
        protected VerseIterator(Versification v11n, Iterator<VerseRange> it) {
            Set<Key> temp = new TreeSet<Key>();

            while (it.hasNext()) {
                VerseRange range = it.next();
                int start = range.getStart().getOrdinal();
                int end = range.getCardinality();

                for (int i = 0; i < end; i++) {
                    temp.add(v11n.decodeOrdinal(start + i));
                }
            }

            real = temp.iterator();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return real.hasNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Key next() throws NoSuchElementException {
            return real.next();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * The Iterator that we are proxying to
         */
        private Iterator<Key> real;
    }

    /**
     * Loop over the VerseRanges and check that they do not require digging into
     */
    private static final class VerseRangeIterator implements Iterator<VerseRange> {
        /**
         * Simple ctor
         * @param it 
         * @param restrict 
         */
        protected VerseRangeIterator(Iterator<VerseRange> it, RestrictionType restrict) {
            this.restrict = restrict;
            this.real = it;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return next != null || real.hasNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public VerseRange next() {
            if (next == null) {
                next = real.next();
            }

            if (next == null) {
                throw new NoSuchElementException();
            }

            // So we know what is broadly next, however the range might need
            // splitting according to restrict
            if (restrict.isSameScope(next.getVersification(), next.getStart(), next.getEnd())) {
                return replyNext();
            }
            return splitNext();
        }

        /**
         * The next object is correct, use that one
         */
        private VerseRange replyNext() {
            VerseRange reply = next;
            next = null;
            return reply;
        }

        /**
         * The next object is too big, so cut it up
         */
        private VerseRange splitNext() {
            Iterator<VerseRange> chop = next.rangeIterator(restrict);
            VerseRange first = chop.next();
            VerseRange[] ranges = VerseRange.remainder(next, first);

            assert ranges.length == 1;
            next = ranges[0];

            return first;
        }

        /**
         * What are we going to reply with next?
         */
        private VerseRange next;

        /**
         * Where do we break ranges
         */
        private RestrictionType restrict;

        /**
         * Where we read our base ranges from
         */
        private Iterator<VerseRange> real;
    }

    /**
     * Call the support mechanism in AbstractPassage
     * 
     * @param out
     *            The stream to write our state to
     * @serialData Write the ordinal number of this verse
     * @throws IOException
     *             if the read fails
     * @see AbstractPassage#writeObjectSupport(ObjectOutputStream)
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        writeObjectSupport(out);
    }

    /**
     * Call the support mechanism in AbstractPassage
     * 
     * @param in
     *            The stream to read our state from
     * @serialData Write the ordinal number of this verse
     * @throws IOException
     *             if the read fails
     * @throws ClassNotFoundException
     *             If the read data is incorrect
     * @see AbstractPassage#readObjectSupport(ObjectInputStream)
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        optimizeWrites();

        store = new TreeSet<VerseRange>();

        in.defaultReadObject();

        readObjectSupport(in);
    }

    /**
     * To make serialization work across new versions
     */
    private static final long serialVersionUID = 955115811339960826L;

    /**
     * The place the real data is stored
     */
    private transient Set<VerseRange> store;
}
