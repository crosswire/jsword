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
import java.util.Iterator;

import org.crosswire.jsword.versification.Versification;

/**
 * A RocketPassage is a bit and heavy implementation of Passage that goes fairly
 * quickly once let of the leash. It manages its speed by creating contained
 * instances of DistinctPassage and RangedPassage and selects the fastest
 * implementation for each of its methods from the 3 available.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class RocketPassage extends BitwisePassage {
    /**
     * Create a new RocketPassage
     * 
     * @param v11n
     *            The Versification to which this Passage belongs.
     */
    public RocketPassage(Versification v11n) {
        super(v11n);
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
     * 
     * @param v11n
     *            The Versification to which this Passage belongs.
     * @param refs
     *            A String containing the text of the RangedPassage
     * @param basis
     *           The basis by which to interpret refs
     * @throws NoSuchVerseException
     *           if refs is invalid
     */
    protected RocketPassage(Versification v11n, String refs, Key basis) throws NoSuchVerseException {
        super(v11n, refs, basis);
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
     * 
     * @param v11n
     *            The Versification to which this Passage belongs.
     * @param refs
     *            A String containing the text of the RangedPassage
     * @throws NoSuchVerseException
     *            if refs is invalid
     */
    protected RocketPassage(Versification v11n, String refs) throws NoSuchVerseException {
        this(v11n, refs, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.AbstractPassage#optimizeReads()
     */
    @Override
    public void optimizeReads() {
        raiseEventSuppresion();

        // We have to create the cached versions of these separately
        // so that the calculations made by addAll(this) can
        // safely call methods like countVerses() without any
        // danger of them being optimized before the optimizations
        // are ready for use.

        DistinctPassage dtemp = new DistinctPassage(getVersification());
        dtemp.raiseEventSuppresion();
        dtemp.addAll(this);
        dtemp.lowerEventSuppressionAndTest();

        RangedPassage rtemp = new RangedPassage(getVersification());
        rtemp.raiseEventSuppresion();
        rtemp.addAll(this);
        rtemp.lowerEventSuppressionAndTest();

        distinct = dtemp;
        ranged = rtemp;

        // This is just an optimization so we dont need to fire any events
        lowerEventSuppressionAndTest();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.AbstractPassage#optimizeWrites()
     */
    @Override
    protected void optimizeWrites() {
        distinct = null;
        ranged = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#countRanges(int)
     */
    @Override
    public int countRanges(RestrictionType restrict) {
        if (ranged != null) {
            return ranged.countRanges(restrict);
        }

        return super.countRanges(restrict);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#countVerses()
     */
    @Override
    public int countVerses() {
        if (distinct != null) {
            return distinct.countVerses();
        }

        return super.countVerses();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Key> iterator() {
        if (distinct != null) {
            return distinct.iterator();
        }

        return super.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#rangeIterator(int)
     */
    @Override
    public Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
        if (ranged != null) {
            return ranged.rangeIterator(restrict);
        }

        return super.rangeIterator(restrict);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        if (distinct != null) {
            return distinct.isEmpty();
        }

        return super.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getVerseAt(int)
     */
    @Override
    public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException {
        if (distinct != null) {
            return distinct.getVerseAt(offset);
        }

        return super.getVerseAt(offset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getVerseRangeAt(int, int)
     */
    @Override
    public VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException {
        if (ranged != null) {
            return ranged.getRangeAt(offset, restrict);
        }

        return super.getRangeAt(offset, restrict);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#booksInPassage()
     */
    @Override
    public int booksInPassage() {
        if (distinct != null) {
            return distinct.booksInPassage();
        }

        return super.booksInPassage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#containsAll(org.crosswire.jsword
     * .passage.Passage)
     */
    @Override
    public boolean containsAll(Passage that) {
        if (ranged != null) {
            return ranged.containsAll(that);
        }

        return super.containsAll(that);
    }

    /**
     * Serialization support
     * 
     * @param is
     *            The stream to read our state from
     * @throws IOException
     *             if the read fails
     * @throws ClassNotFoundException
     *             If the read data is incorrect
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        optimizeWrites();
        is.defaultReadObject();
    }

    /**
     * The contained DistinctPassage
     */
    private transient DistinctPassage distinct;

    /**
     * The contained RangedPassage
     */
    private transient RangedPassage ranged;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258125864771401268L;
}
