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
 *     The copyright to this program is held by its authors. 
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

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.Versification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Similar to a Passage, but that stores a ranking for each of the Verses that
 * it contains.
 * 
 * <p>
 * Currently there is no well defined spec for what the rank of a verse means -
 * it is just an int. Since this number is exposed in 2 places
 * (getNameAndTally() and getTallyFor()) we should specify what the numbers
 * mean. Trouble is most tallies come from searches where the numbers only have
 * relative meaning.
 * </p>
 * 
 * <p>
 * This class exactly implements the Passage interface when the ordering is set
 * to Order.BIBLICAL, however an additional setting of Order.TALLY sorts the
 * verses by the rank in this tally.
 * 
 * <p>
 * Calling <code>tally.add(Gen 1:1); tally.add(Gen 1:1);</code> is redundant for
 * a Passage however a PassageTally will increase the rank of Gen 1:1, there are
 * additional methods <code>unAdd()</code> and <code>unAddAll()</code> that do
 * the reverse, of decreasing the rank of the specified verse(s).
 * </p>
 * 
 * <p>
 * The point is to allow a search for
 * "God loves us, and gave Jesus to die to save us" to correctly identify John
 * 3:16. So we are using fuzzy matching big style, but I think this will be very
 * useful.
 * </p>
 * 
 * <p>
 * How should we rank VerseRanges? We could use a sum of the ranks of the verses
 * in a range or the maximum value of a range. The former would seem to be more
 * mathematically correct, but I think that the latter is better because: the
 * concept of max value is preserved, because a wide blurred match is generally
 * not as good as a sharply defined one.
 * </p>
 * 
 * <p>
 * Should we be going for a PassageTallyFactory type approach? Of the 3
 * implementations of Passage, The RangedPassage does not make sense here, and a
 * PassageTally will not have the range of uses that a Passage has, so I think
 * there is more likely to be a correct answer. So right now the answer is no.
 * </p>
 * 
 * <p>
 * Memory considerations: The BitSet approach will always use a
 * <code>int[31000]</code> = 128k of memory.<br>
 * The Distinct approach will be n * int[4] where n is the number of verses
 * stored. I expect most searches to have at least n=1000. Also 128k<br>
 * Given this, (A Distinct style PassageTally will usually use more memory than
 * a BitSet style PassageTally) And the intuitive result that the BitSet will be
 * faster, I'm going to start by implementing the latter only.
 * </p>
 * 
 * <p>
 * To think about - I've upped the MAX_TALLY to 20000 to help the new mapper
 * program. I'm not sure why it was originally 100?
 * 
 * <p>
 * LATER(joe): Specify how passage ranks work.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class PassageTally extends AbstractPassage {
    /**
     * Create an empty PassageTally
     * 
     * @param v11n
     *            The Versification to which this Passage belongs.
     */
    public PassageTally(Versification v11n) {
        super(v11n);
        board = new int[v11n.maximumOrdinal() + 1];
    }

    /**
     * Create a Verse from a human readable string. The opposite of toString()
     * 
     * @param v11n
     *            The Versification to which this Passage belongs.
     * @param refs
     *            The text to interpret
     * @param basis
     *           The basis by which to interpret refs
     * @throws NoSuchVerseException
     *             If refs is invalid
     */
    protected PassageTally(Versification v11n, String refs, Key basis) throws NoSuchVerseException {
        super(v11n, refs);
        board = new int[v11n.maximumOrdinal() + 1];
        addVerses(refs, basis);
    }

    protected PassageTally(Versification v11n, String refs) throws NoSuchVerseException {
        this(v11n, refs, null);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int countVerses() {
        return size;
    }

    /**
     * Set how we sort the verses we output. The options are:
     * <ul>
     * <li>Order.BIBLICAL: Natural Biblical order</li>
     * <li>Order.TALLY: In an order specified by this class</li>
     * </ul>
     * 
     * @param order
     *            the sort order
     */
    public void setOrdering(Order order) {
        this.order = order;
    }

    /**
     * Get how we sort the verses we output.
     * 
     * @return the sort order
     */
    public Order getOrdering() {
        return order;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total
     *            the total to set
     */
    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public PassageTally clone() {
        // This gets us a shallow copy
        PassageTally copy = (PassageTally) super.clone();

        copy.board = board.clone();

        return copy;
    }

    @Override
    public String toString() {
        return getName(0);
    }

    @Override
    public String getName() {
        return getName(0);
    }

    /**
     * A Human readable version of the verse list. Uses short books names, and
     * the shortest possible rendering eg "Mat 3:1-4, 6"
     * 
     * @param cnt
     *            The number of matches to return, 0 gives all matches
     * @return a String containing a description of the verses
     */
    public String getName(int cnt) {
        int maxCount = cnt;
        if (PassageUtil.isPersistentNaming() && originalName != null) {
            return originalName;
        }

        StringBuilder retcode = new StringBuilder();

        if (order == Order.BIBLICAL) {
            Iterator<VerseRange> it = rangeIterator(RestrictionType.NONE);
            Verse current = null;
            while (it.hasNext()) {
                VerseRange range = it.next();
                retcode.append(range.getName(current));

                if (it.hasNext()) {
                    retcode.append(AbstractPassage.REF_PREF_DELIM);
                }

                current = range.getStart();
            }
        } else {
            if (maxCount == 0) {
                maxCount = Integer.MAX_VALUE;
            }

            Iterator<Key> it = new OrderedVerseIterator(getVersification(), board);
            Key current = null;
            int count = 0;

            while (it.hasNext() && count < maxCount) {
                Key verse = it.next();
                retcode.append(verse.getName(current));

                current = verse;
                count++;

                if (it.hasNext() && count < maxCount) {
                    retcode.append(AbstractPassage.REF_PREF_DELIM);
                }
            }
        }

        return retcode.toString();
    }

    /**
     * A Human readable version of the PassageTally. Uses short books names, and
     * the shortest possible rendering eg "Mat 3:1-4"
     * 
     * @return a String containing a description of the verses
     */
    public String getNameAndTally() {
        return getNameAndTally(0);
    }

    /**
     * A Human readable version of the PassageTally. Uses short books names, and
     * the shortest possible rendering eg "Mat 3:1-4"
     * 
     * @param cnt
     *            The number of matches to return, 0 gives all matches
     * @return a String containing a description of the verses
     */
    public String getNameAndTally(int cnt) {
        int maxCount = cnt;
        StringBuilder retcode = new StringBuilder();
        if (maxCount == 0) {
            maxCount = Integer.MAX_VALUE;
        }

        OrderedVerseIterator it = new OrderedVerseIterator(getVersification(), board);
        int count = 0;

        while (it.hasNext() && count < maxCount) {
            Key verse = it.next();
            retcode.append(verse.getName());
            retcode.append(" (");
            retcode.append(100 * it.lastRank() / max);
            retcode.append("%)");

            count++;

            if (it.hasNext() && count < maxCount) {
                retcode.append(AbstractPassage.REF_PREF_DELIM);
            }
        }

        return retcode.toString();
    }

    /**
     * Iterate through the verse elements in the current sort order
     * 
     * @return A verse Iterator
     */
    public Iterator<Key> iterator() {
        if (order == Order.BIBLICAL) {
            return new VerseIterator();
        }
        return new OrderedVerseIterator(getVersification(), board);
    }

    @Override
    public Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
        if (order == Order.BIBLICAL) {
            return new VerseRangeIterator(getVersification(), iterator(), restrict);
        }
        return new OrderedVerseRangeIterator(getVersification(), iterator(), board);
    }

    /**
     * Does this tally contain all the specified verses?
     * 
     * @param that
     *            The verses to test for
     * @return true if all the verses exist in this tally
     */
    @Override
    public boolean contains(Key that) {
        for (Key aKey : that) {
            Verse verse = (Verse) aKey;
            if (board[verse.getOrdinal()] == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * The ranking given to a specific verse
     * 
     * @param verse
     *            The verse to get the ranking of
     * @return The rank of the verse in question
     */
    public int getTallyOf(Verse verse) {
        return board[verse.getOrdinal()];
    }

    /**
     * What is the index of the give verse in the current ordering scheme
     * 
     * @param verse
     *            The verse to get the index of
     * @return The index of the verse or -1 if the verse was not found
     */
    public int getIndexOf(Verse verse) {
        int pos = verse.getOrdinal();
        int tally = board[pos];
        return tally > 0 ? pos : -1;
    }

    /**
     * Add/Increment this verses in the rankings
     * 
     * @param that
     *            The verses to add/increment
     */
    public void add(Key that) {
        optimizeWrites();

        alterVerseBase(that, 1);
        fireIntervalAdded(this, null, null);
    }

    /**
     * DONT USE THIS. It makes public something of the ratings scheme which is
     * not generally recommended. This method is likely to be removed at a
     * moments notice, and it only here to keep Mapper happy. Add/Increment this
     * verses in the rankings
     * 
     * @param that
     *            The verses to add/increment
     * @param count
     *            The amount to increment by
     */
    public void add(Key that, int count) {
        optimizeWrites();

        alterVerseBase(that, count);
        fireIntervalAdded(this, null, null);
    }

    /**
     * Remove/Decrement this verses in the rankings
     * 
     * @param that
     *            The verses to remove/decrement
     */
    public void unAdd(Key that) {
        optimizeWrites();

        alterVerseBase(that, -1);
        fireIntervalRemoved(this, null, null);
    }

    /**
     * Remove these verses from the rankings, ie, set their rank to zero.
     * 
     * @param that
     *            The verses to remove/decrement
     */
    public void remove(Key that) {
        optimizeWrites();

        for (Key aKey : that) {
            Verse verse = (Verse) aKey;
            kill(verse.getOrdinal());
        }

        fireIntervalRemoved(this, null, null);
    }

    @Override
    public void addAll(Key that) {
        optimizeWrites();

        if (that instanceof PassageTally) {
            PassageTally tally = (PassageTally) that;

            int vib = getVersification().maximumOrdinal();
            for (int i = 0; i <= vib; i++) {
                increment(i, tally.board[i]);
            }

            incrementMax(tally.max);
        } else {
            for (Key aKey : that) {
                Verse verse = (Verse) aKey;
                increment(verse.getOrdinal(), 1);
            }

            incrementMax(1);
        }

        fireIntervalAdded(this, null, null);
    }

    /**
     * Remove/Decrement these verses in the rankings
     * 
     * @param that
     *            The verses to remove/decrement
     */
    public void unAddAll(Passage that) {
        optimizeWrites();

        if (that instanceof PassageTally) {
            PassageTally tally = (PassageTally) that;

            int vib = getVersification().maximumOrdinal();
            for (int i = 0; i <= vib; i++) {
                increment(i, -tally.board[i]);
            }
        } else {
            for (Key aKey : that) {
                Verse verse = (Verse) aKey;
                increment(verse.getOrdinal(), -1);
            }
        }

        fireIntervalRemoved(this, null, null);

        // Just because we've decremented some doesn't
        // change the max. So we don't need to do:
        // incrementMax(-1);
    }

    @Override
    public void removeAll(Key key) {
        optimizeWrites();

        if (key instanceof PassageTally) {
            PassageTally tally = (PassageTally) key;

            int vib = getVersification().maximumOrdinal();
            for (int i = 0; i <= vib; i++) {
                if (tally.board[i] != 0) {
                    kill(i);
                }
            }
        } else {
            for (Key aKey : key) {
                Verse verse = (Verse) aKey;
                kill(verse.getOrdinal());
            }
        }

        fireIntervalRemoved(this, null, null);

        // Just because we've decremented some doesn't
        // change the max. So we don't need to do:
        // incrementMax(-1);
    }

    @Override
    public void clear() {
        optimizeWrites();

        for (int i = 0; i < board.length; i++) {
            board[i] = 0;
        }

        size = 0;

        fireIntervalRemoved(this, null, null);
    }

    /**
     * Ensures that there are a maximum of <code>count</code> Verses in this
     * Passage. If there were more than <code>count</code> Verses then a new
     * Passage is created containing the Verses from <code>count</code> + 1
     * onwards. If there was not greater than <code>count</code> in the Passage,
     * then the passage remains unchanged, and null is returned.
     * 
     * @param count
     *            The maximum number of Verses to allow in this collection
     * @return A new Passage containing the remaining verses or null
     * @see Verse
     */
    @Override
    public Passage trimVerses(int count) {
        optimizeWrites();

        int i = 0;
        boolean overflow = false;

        Passage remainder = this.clone();

        for (Key verse : this) {
            i++;
            if (i > count) {
                remove(verse);
                overflow = true;
            } else {
                remainder.remove(verse);
            }

        }

        if (overflow) {
            return remainder;
        }
        return null;
    }

    /**
     * Take the verses in the tally and give them all and equal rank of 1. After
     * this method has executed then both sorting methods for a.
     */
    public void flatten() {
        optimizeWrites();

        for (int i = 0; i < board.length; i++) {
            if (board[i] != 0) {
                board[i] = 1;
            }
        }

        max = 1;
    }

    @Override
    public void blur(int verses, RestrictionType restrict) {
        assert verses >= 0;

        optimizeWrites();
        raiseEventSuppresion();
        raiseNormalizeProtection();

        if (!restrict.equals(RestrictionType.NONE)) {
            log.warn("Restrict={} is not properly supported.", restrict);

            // This is a bit of a cheat, but there is no way I'm going
            // to do the math to speed up the restricted version
            PassageTally temp = this.clone();
            Iterator<VerseRange> it = temp.rangeIterator(RestrictionType.NONE);

            while (it.hasNext()) {
                VerseRange range = it.next();
                for (int i = 0; i <= verses; i++) {
                    add(restrict.blur(getVersification(), range, i, i));
                }
            }
        } else {
            int[] newBoard = new int[board.length];

            for (int i = 0; i < board.length; i++) {
                if (board[i] != 0) {
                    // This could be re-written more simply:
                    // for (int j = -verses; j <= verses; j++) {
                    //     int k = i + j;
                    //     if (k >= 0 && k <= BibleInfo.maximumOrdinal()) {
                    //         new_board[k] += board[i] + verses - mod(j);
                    //     }
                    // }
                    // However splitting the loop in 2 will speed it up quite a bit.

                    for (int j = -verses; j < 0; j++) {
                        int k = i + j;
                        if (k >= 0) {
                            newBoard[k] += board[i] + verses + j;
                        }
                    }

                    newBoard[i] += board[i] + verses;

                    for (int j = 1; j <= verses; j++) {
                        int k = i + j;
                        if (k < board.length - 1) {
                            newBoard[k] += board[i] + verses - j;
                        }
                    }
                }
            }

            board = newBoard;
        }

        resetMax();

        lowerNormalizeProtection();
        if (lowerEventSuppressionAndTest()) {
            fireIntervalAdded(this, null, null);
        }
    }

    /**
     * Sometimes we end up not knowing what the max is - this makes sure we know
     * accurately. Same with size.
     */
    private void resetMax() {
        optimizeWrites();

        max = 0;
        size = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] > 0) {
                size++;
            }
            if (board[i] > max) {
                max = board[i];
            }
        }
    }

    /**
     * Increment/Decrement this verses in the rankings
     * 
     * @param that
     *            The verses to add/increment
     * @param tally
     *            The amount to increment/decrement by
     */
    private void alterVerseBase(Key that, int tally) {
        for (Key aKey : that) {
            Verse verse = (Verse) aKey;
            increment(verse.getOrdinal(), tally);
        }

        if (tally > 0) {
            incrementMax(tally);
        }
    }

    /**
     * Increment a verse by an amount
     * 
     * @param ord
     *            The verse to increment
     * @param tally
     *            The amount to increase by
     */
    private void increment(int ord, int tally) {
        boolean exists = board[ord] > 0;
        board[ord] += tally;
        if (board[ord] > MAX_TALLY) {
            board[ord] = MAX_TALLY;
        }
        if (board[ord] < 0) {
            board[ord] = 0;
        }

        // Recompute the size
        if (exists && board[ord] == 0) {
            size--;
        } else if (!exists && board[ord] > 0) {
            size++;
        }
    }

    /**
     * Increment a verse by an amount
     * 
     * @param tally
     *            The amount to increase by
     */
    private void incrementMax(int tally) {
        max += tally;
        if (max > MAX_TALLY) {
            max = MAX_TALLY;
        }
        if (max < 0) {
            max = 0;
        }
    }

    /**
     * Wipe the rank of the given verse to zero
     * 
     * @param ord
     *            The verse to increment
     */
    private void kill(int ord) {
        if (board[ord] > 0) {
            size--;
        }

        board[ord] = 0;
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

        readObjectSupport(in);
    }

    /**
     * Indicates how this PassageTally is to order it's Verses.
     */
    public enum Order {
        /**
         * Sort in Biblical order
         */
        BIBLICAL,

        /**
         * Sort in tally rank order
         */
        TALLY
    }

    /**
     * The highest tally possible
     */
    public static final int MAX_TALLY = 20000;

    /*
     * The number of verses in the tally.
     */
    private int size;

    /*
     * The total number of verses that this tally represents.
     */
    private int total;

    /**
     * The tally board itself
     */
    protected int[] board;

    /**
     * The maximum tally possible
     */
    private int max;

    /**
     * The maximum tally possible
     */
    private Order order = Order.BIBLICAL;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(PassageTally.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3761128240928274229L;

    /**
     * Iterate over the Verses in normal verse order
     * 
     * @author Joe Walker
     */
    private final class VerseIterator implements Iterator<Key> {
        /**
         * Find the first unused verse
         */
        protected VerseIterator() {
            calculateNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return next <= board.length - 1;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Key next() throws NoSuchElementException {
            if (next >= board.length) {
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
            throw new UnsupportedOperationException();
        }

        /**
         * Find the next bit
         */
        private void calculateNext() {
            do {
                next++;
            } while (next < board.length && board[next] == 0);
        }

        /** What is the next Verse to be considered */
        private int next;
    }

    /**
     * Iterate over the Verses in order of their rank in the tally
     * 
     * @author Joe Walker
     */
    private static final class OrderedVerseIterator implements Iterator<Key> {
        /**
         * Find the first unused verse
         */
        protected OrderedVerseIterator(Versification v11n, int[] board) {
            referenceSystem = v11n;
            TreeSet<TalliedVerse> output = new TreeSet<TalliedVerse>();

            int vib = board.length - 1;
            for (int i = 0; i <= vib; i++) {
                if (board[i] != 0) {
                    output.add(new TalliedVerse(i, board[i]));
                }
            }

            it = output.iterator();
            last = null;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return it.hasNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Key next() throws NoSuchElementException {
            last = it.next();
            return referenceSystem.decodeOrdinal(last.ord);
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * @return the next Verse in the iteration
         * @throws NoSuchElementException
         *             if hasNext() == false
         */
        public int lastRank() throws NoSuchElementException {
            if (last != null) {
                return last.tally;
            }
            throw new NoSuchElementException(JSOtherMsg.lookupText("nextElement() has not been called yet."));
        }

        /**
         * The Versification is needed to decode board positions.
         */
        private Versification referenceSystem;
        /**
         * So that we can get at the ranking of the given verse
         */
        private TalliedVerse last;

        /**
         * The Iterator we are converting
         */
        private Iterator<TalliedVerse> it;
    }

    /**
     * Hack to make this work with J2SE 1.1 as well as J2SE 1.2 This compared 2
     * Integers
     */
    private static class TalliedVerse implements Comparable<TalliedVerse> {
        /**
         * Convenience ctor to set the public variables
         * 
         * @param ord
         *            the verse id
         * @param tally
         *            the rank of the verse
         */
        protected TalliedVerse(int ord, int tally) {
            this.ord = ord;
            this.tally = tally;
        }

        @Override
        public int hashCode() {
            int result = 31 + ord;
            return 31 * result + tally;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TalliedVerse other = (TalliedVerse) obj;
            if (tally != other.tally) {
                return false;
            }
            if (ord != other.ord) {
                return false;
            }
            return true;
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(TalliedVerse that) {
            if (that.tally == this.tally) {
                return this.ord - that.ord;
            }

            return that.tally - this.tally;
        }

        /**
         * The verse id
         */
        protected int ord;

        /**
         * The rank of the verse
         */
        protected int tally;
    }

    /**
     * Iterate over the Ranges in order of their rank in the tally
     * 
     * @author Joe Walker
     */
    private static final class OrderedVerseRangeIterator implements Iterator<VerseRange> {
        /**
         * Find the first unused verse
         * 
         * @param v11n
         *            the versification to which this reference pertains
         * @param vit 
         * @param board 
         */
        protected OrderedVerseRangeIterator(Versification v11n, Iterator<Key> vit, int[] board) {
            Set<TalliedVerseRange> output = new TreeSet<TalliedVerseRange>();

            Iterator<VerseRange> rit = new VerseRangeIterator(v11n, vit, RestrictionType.NONE);
            while (rit.hasNext()) {
                VerseRange range = rit.next();

                // Calculate the maximum rank for a verse
                int rank = 0;
                Iterator<Key> iter = range.iterator();
                while (iter.hasNext()) {
                    Verse verse = (Verse) iter.next();
                    int temp = board[verse.getOrdinal()];
                    if (temp > rank) {
                        rank = temp;
                    }
                }

                output.add(new TalliedVerseRange(range, rank));
            }

            this.it = output.iterator();
            last = null;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return it.hasNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public VerseRange next() throws NoSuchElementException {
            last = it.next();
            return last.range;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * So that we can get at the ranking of the given verse
         */
        private TalliedVerseRange last;

        /**
         * The Iterator we are converting
         */
        private Iterator<TalliedVerseRange> it;
    }

    /**
     * Hack to make this work with JDK1.1 as well as JDK1.2 This compared 2
     * Integers
     */
    private static class TalliedVerseRange implements Comparable<TalliedVerseRange> {
        /**
         * Convenience ctor to set the public variables
         * 
         * @param range
         *            The verse range
         * @param tally
         *            The rank of the verse
         */
        protected TalliedVerseRange(VerseRange range, int tally) {
            this.range = range;
            this.tally = tally;
        }

        @Override
        public int hashCode() {
            int result = 31 + tally;
            return 31 * result + ((range == null) ? 0 : range.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TalliedVerseRange other = (TalliedVerseRange) obj;
            if (tally != other.tally) {
                return false;
            }
            if (range == null) {
                if (other.range != null) {
                    return false;
                }
            } else if (!range.equals(other.range)) {
                return false;
            }
            return true;
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(TalliedVerseRange that) {
            if (that.tally == this.tally) {
                return this.range.compareTo(that.range);
            }

            return that.tally - this.tally;
        }

        /**
         * The verse range
         */
        protected VerseRange range;

        /**
         * The rank of the verse
         */
        protected int tally;
    }
}
