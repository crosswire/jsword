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

import org.crosswire.jsword.versification.Versification;

/**
 * This is a simple proxy to a real Passage object that makes all accesses
 * synchronized. It is final to give the VM as much hope as possible at being
 * able to inline stuff.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
final class SynchronizedPassage implements Passage {
    /**
     * Construct a SynchronizedPassage from a real Passage to which we proxy.
     * 
     * @param ref
     *            The real Passage
     */
    SynchronizedPassage(Passage ref) {
        this.ref = ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getVersification()
     */
    public Versification getVersification() {
        return ref.getVersification();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#reversify(org.crosswire.jsword.versification.Versification)
     */
    public Passage reversify(Versification newVersification) {
        return ref.reversify(newVersification);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.VerseKey#isWhole()
     */
    public boolean isWhole() {
        return ref.isWhole();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.VerseKey#getWhole()
     */
    public Passage getWhole() {
        return ref.getWhole();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#addAll(org.crosswire.jsword.passage.Key)
     */
    public synchronized void addAll(Key key) {
        ref.addAll(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#removeAll(org.crosswire.jsword.passage.Key)
     */
    public synchronized void removeAll(Key key) {
        ref.removeAll(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retainAll(org.crosswire.jsword.passage.Key)
     */
    public synchronized void retainAll(Key key) {
        ref.retainAll(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#contains(org.crosswire.jsword.passage.Key)
     */
    public synchronized boolean contains(Key key) {
        return ref.contains(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public synchronized int getChildCount() {
        return ref.getChildCount();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public synchronized int getCardinality() {
        return ref.getCardinality();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public synchronized boolean canHaveChildren() {
        return ref.canHaveChildren();
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public synchronized Iterator<Key> iterator() {
        return ref.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public synchronized Key get(int index) {
        return ref.get(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public synchronized int indexOf(Key that) {
        return ref.indexOf(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public synchronized Key getParent() {
        return ref.getParent();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName()
     */
    public synchronized String getName() {
        return ref.getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage.Key)
     */
    public synchronized String getName(Key base) {
        return ref.getName(base);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getRootName()
     */
    public synchronized String getRootName() {
        return ref.getRootName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisRef()
     */
    public synchronized String getOsisRef() {
        return ref.getOsisRef();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisID()
     */
    public synchronized String getOsisID() {
        return ref.getOsisID();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getOverview()
     */
    public synchronized String getOverview() {
        return ref.getOverview();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    public synchronized boolean isEmpty() {
        return ref.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#countVerses()
     */
    public synchronized int countVerses() {
        return ref.countVerses();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#hasRanges(org.crosswire.jsword.passage.RestrictionType)
     */
    public synchronized boolean hasRanges(RestrictionType restrict) {
        return ref.hasRanges(restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#countRanges(org.crosswire.jsword.passage.RestrictionType)
     */
    public synchronized int countRanges(RestrictionType restrict) {
        return ref.countRanges(restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#trimVerses(int)
     */
    public synchronized Passage trimVerses(int count) {
        return ref.trimVerses(count);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#trimRanges(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public synchronized Passage trimRanges(int count, RestrictionType restrict) {
        return ref.trimRanges(count, restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#booksInPassage()
     */
    public synchronized int booksInPassage() {
        return ref.booksInPassage();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getVerseAt(int)
     */
    public synchronized Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException {
        return ref.getVerseAt(offset);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#getRangeAt(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public synchronized VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException {
        return ref.getRangeAt(offset, restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#rangeIterator(org.crosswire.jsword.passage.RestrictionType)
     */
    public synchronized Iterator<VerseRange> rangeIterator(RestrictionType restrict) {
        return ref.rangeIterator(restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#add(org.crosswire.jsword.passage.Key)
     */
    public synchronized void add(Key that) {
        ref.add(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#remove(org.crosswire.jsword.passage.Key)
     */
    public synchronized void remove(Key that) {
        ref.remove(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#containsAll(org.crosswire.jsword.passage.Passage)
     */
    public synchronized boolean containsAll(Passage that) {
        return ref.containsAll(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public synchronized void clear() {
        ref.clear();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public synchronized void blur(int by, RestrictionType restrict) {
        ref.blur(by, restrict);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#readDescription(java.io.Reader)
     */
    public synchronized void readDescription(Reader in) throws IOException, NoSuchVerseException {
        ref.readDescription(in);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#writeDescription(java.io.Writer)
     */
    public synchronized void writeDescription(Writer out) throws IOException {
        ref.writeDescription(out);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#optimizeReads()
     */
    public synchronized void optimizeReads() {
        ref.optimizeReads();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#addPassageListener(org.crosswire.jsword.passage.PassageListener)
     */
    public synchronized void addPassageListener(PassageListener li) {
        ref.addPassageListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#removePassageListener(org.crosswire.jsword.passage.PassageListener)
     */
    public synchronized void removePassageListener(PassageListener li) {
        ref.removePassageListener(li);
    }

    @Override
    public synchronized SynchronizedPassage clone() {
        SynchronizedPassage clone = null;
        try {
            clone = (SynchronizedPassage) super.clone();
            synchronized (clone) {
                clone.ref = (Passage) ref.clone();
            }
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    @Override
    public synchronized int hashCode() {
        return ref.hashCode();
    }

    @Override
    public synchronized boolean equals(Object obj) {
        return ref.equals(obj);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public synchronized int compareTo(Key o) {
        return ref.compareTo(o);
    }

    /**
     * The object we are proxying to
     */
    private Passage ref;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3833181441264531251L;
}
