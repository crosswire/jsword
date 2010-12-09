/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

/**
 * This is a simple proxy to a real Passage object that denies all attempts to
 * write to it.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
final class ReadOnlyPassage implements Passage {
    /**
     * Construct a ReadOnlyPassage from a real Passage to which we proxy.
     * 
     * @param ref
     *            The real Passage
     * @param ignore
     *            Do we throw up if someone tries to change us
     */
    protected ReadOnlyPassage(Passage ref, boolean ignore) {
        this.ref = ref;
        this.ignore = ignore;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#add(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#remove(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#retain(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage
     * .Key)
     */
    public boolean contains(Key key) {
        return ref.contains(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren() {
        return ref.canHaveChildren();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount() {
        return ref.getChildCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        return ref.getCardinality();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator<Key> iterator() {
        return ref.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        return ref.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage
     * .Key)
     */
    public int indexOf(Key that) {
        return ref.indexOf(that);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent() {
        return ref.getParent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getName()
     */
    public String getName() {
        return ref.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage
     * .Key)
     */
    public String getName(Key base) {
        return ref.getName(base);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getRootName()
     */
    public String getRootName() {
        return ref.getRootName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getOSISName()
     */
    public String getOsisRef() {
        return ref.getOsisRef();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getOSISId()
     */
    public String getOsisID() {
        return ref.getOsisID();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getOverview()
     */
    public String getOverview() {
        return ref.getOverview();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#isEmpty()
     */
    public boolean isEmpty() {
        return ref.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#countVerses()
     */
    public int countVerses() {
        return ref.countVerses();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#hasRanges(org.crosswire.jsword.passage
     * .RestrictionType)
     */
    public boolean hasRanges(RestrictionType restrict) {
        return ref.hasRanges(restrict);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#countRanges(org.crosswire.jsword
     * .passage.RestrictionType)
     */
    public int countRanges(RestrictionType restrict) {
        return ref.countRanges(restrict);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#trimVerses(int)
     */
    public Passage trimVerses(int count) {
        return ref.trimVerses(count);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#trimRanges(int,
     * org.crosswire.jsword.passage.RestrictionType)
     */
    public Passage trimRanges(int count, RestrictionType restrict) {
        return ref.trimRanges(count, restrict);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#booksInPassage()
     */
    public int booksInPassage() {
        return ref.booksInPassage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#chaptersInPassage(int)
     */
    public int chaptersInPassage(int book) throws NoSuchVerseException {
        return ref.chaptersInPassage(book);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#versesInPassage(int, int)
     */
    public int versesInPassage(int book, int chapter) throws NoSuchVerseException {
        return ref.versesInPassage(book, chapter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getVerseAt(int)
     */
    public Verse getVerseAt(int offset) throws ArrayIndexOutOfBoundsException {
        return ref.getVerseAt(offset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#getVerseRangeAt(int, int)
     */
    public VerseRange getRangeAt(int offset, RestrictionType restrict) throws ArrayIndexOutOfBoundsException {
        return ref.getRangeAt(offset, restrict);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#rangeIterator(int)
     */
    public Iterator<Key> rangeIterator(RestrictionType restrict) {
        return ref.rangeIterator(restrict);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#add(org.crosswire.jsword.passage
     * .VerseBase)
     */
    public void add(Key that) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#remove(org.crosswire.jsword.passage
     * .VerseBase)
     */
    public void remove(Key that) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#containsAll(org.crosswire.jsword
     * .passage.Passage)
     */
    public boolean containsAll(Passage that) {
        return ref.containsAll(that);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#clear()
     */
    public void clear() {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#blur(int)
     */
    public void blur(int by, RestrictionType restrict) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#readDescription(java.io.Reader)
     */
    public void readDescription(Reader in) {
        if (ignore) {
            return;
        }

        throw new IllegalStateException(Msg.PASSAGE_READONLY.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#writeDescription(java.io.Writer)
     */
    public void writeDescription(Writer out) throws IOException {
        ref.writeDescription(out);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Passage#optimizeReads()
     */
    public void optimizeReads() {
        ref.optimizeReads();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#addPassageListener(org.crosswire
     * .jsword.passage.PassageListener)
     */
    public void addPassageListener(PassageListener li) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.passage.Passage#removePassageListener(org.crosswire
     * .jsword.passage.PassageListener)
     */
    public void removePassageListener(PassageListener li) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ref.equals(obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ref.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ref.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        ReadOnlyPassage clone = null;
        try {
            clone = (ReadOnlyPassage) super.clone();
            clone.ref = this.ref;
            clone.ignore = this.ignore;
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }

        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Key o) {
        return ref.compareTo(o);
    }

    /**
     * The object we are proxying to
     */
    private Passage ref;

    /**
     * Do we just silently ignore change attempts or throw up
     */
    private boolean ignore;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257853173036102193L;

}
