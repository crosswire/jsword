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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import org.crosswire.common.icu.NumberShaper;
import org.crosswire.common.util.ItemIterator;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * A Verse is a pointer to a single verse. Externally its unique identifier is
 * a String of the form "Gen 1:1" Internally we use
 * <code>( v11n, book, chapter, verse )</code>
 * 
 * <p>
 * A Verse is designed to be immutable. This is a necessary from a collections
 * point of view. A Verse should always be valid, although some versions may not
 * return any text for verses that they consider to be untranslated in some
 * way.
 * </p>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith
 */
public final class Verse implements Key {
    /**
     * The default Verse is Genesis 1:1. I didn't want to provide this
     * constructor however, you are supposed to provide a default ctor for all
     * beans. For this reason I suggest you don't use it.
     * @deprecated no replacement
     */
    @Deprecated
    public Verse() {
        originalName = null;

        book = DEFAULT.book;
        chapter = DEFAULT.chapter;
        verse = DEFAULT.verse;
        v11n = DEFAULT.v11n;
    }

    /**
     * Create a Verse from book, chapter and verse numbers, throwing up if the
     * specified Verse does not exist. This constructor is deliberately package
     * protected so that is used only by VerseFactory.
     * 
     * @param original
     *            The original verse reference
     * @param book
     *            The book number (Genesis = 1)
     * @param chapter
     *            The chapter number
     * @param verse
     *            The verse number
     * @deprecated Use {@link #Verse(String, Versification, BibleBook, int, int)} instead
     */
    @Deprecated
    /* package */Verse(String original, BibleBook book, int chapter, int verse) {
        this(original, Versifications.instance().getDefaultVersification(), book, chapter, verse);
    }

    /**
     * Create a Verse from book, chapter and verse numbers, throwing up if the
     * specified Verse does not exist. This constructor is deliberately package
     * protected so that is used only by VerseFactory.
     * 
     * @param original
     *            The original verse reference
     * @param v11n
     *            The versification to which this verse belongs
     * @param book
     *            The book number (Genesis = 1)
     * @param chapter
     *            The chapter number
     * @param verse
     *            The verse number
     */
    /* package */Verse(String original, Versification v11n, BibleBook book, int chapter, int verse) {
        this.originalName = original;
        this.v11n = v11n;
        this.book = book;
        this.chapter = chapter;
        this.verse = verse;
        this.ordinal = v11n.getOrdinal(this);
    }

    /**
     * Create a Verse from book, chapter and verse numbers, throwing up if the
     * specified Verse does not exist.
     * 
     * @param book
     *            The book number (Genesis = 1)
     * @param chapter
     *            The chapter number
     * @param verse
     *            The verse number
     * @deprecated Use {@link #Verse(Versification, BibleBook, int, int)} instead
     */
    @Deprecated
    public Verse(BibleBook book, int chapter, int verse) {
        this(null, Versifications.instance().getDefaultVersification(), book, chapter, verse);
    }

    /**
     * Create a Verse from book, chapter and verse numbers, throwing up if the
     * specified Verse does not exist.
     * 
     * @param v11n
     *            The versification to which this verse belongs
     * @param book
     *            The book number (Genesis = 1)
     * @param chapter
     *            The chapter number
     * @param verse
     *            The verse number
     */
    public Verse(Versification v11n, BibleBook book, int chapter, int verse) {
        this(null, v11n, book, chapter, verse);
    }

    /**
     * Create a Verse from book, chapter and verse numbers, patching up if the
     * specified verse does not exist.
     * <p>
     * The actual value of the boolean is ignored. However for future proofing
     * you should only use 'true'. Do not use patch_up=false, use
     * <code>Verse(int, int, int)</code> This so that we can declare this
     * constructor to not throw an exception. Is there a better way of doing
     * this?
     * 
     * @param book
     *            The book number (Genesis = 1)
     * @param chapter
     *            The chapter number
     * @param verse
     *            The verse number
     * @param patch_up
     *            True to trigger reference fixing
     * @deprecated Use {@link #Verse(Versification, BibleBook, int, int, boolean)} instead.
     */
    @Deprecated
    public Verse(BibleBook book, int chapter, int verse, boolean patch_up) {
        this(Versifications.instance().getDefaultVersification(), book, chapter, verse, patch_up);
    }

    /**
     * Create a Verse from book, chapter and verse numbers, patching up if the
     * specified verse does not exist.
     * <p>
     * The actual value of the boolean is ignored. However for future proofing
     * you should only use 'true'. Do not use patch_up=false, use
     * <code>Verse(int, int, int)</code> This so that we can declare this
     * constructor to not throw an exception. Is there a better way of doing
     * this?
     * 
     * @param v11n
     *            The versification to which this verse belongs
     * @param book
     *            The book number (Genesis = 1)
     * @param chapter
     *            The chapter number
     * @param verse
     *            The verse number
     * @param patch_up
     *            True to trigger reference fixing
     */
    public Verse(Versification v11n, BibleBook book, int chapter, int verse, boolean patch_up) {
        if (!patch_up) {
            throw new IllegalArgumentException(JSOtherMsg.lookupText("Use patch=true."));
        }

        this.v11n = v11n;
        Verse patched = this.v11n.patch(book, chapter, verse);
        this.originalName = null;
        this.book = patched.book;
        this.chapter = patched.chapter;
        this.verse = patched.verse;
        this.ordinal = patched.ordinal;
    }

    /**
     * Set a Verse using a Verse Ordinal number - WARNING Do not use this method
     * unless you really know the dangers of doing so. Ordinals are not always
     * going to be the same. So you should use a Verse or an int[3] in
     * preference to an int ordinal whenever possible. Ordinal numbers are 1
     * based and not 0 based.
     * 
     * @param ordinal
     *            The verse id
     * @deprecated Use {@link #Verse(Versification, int)} instead.
     */
    @Deprecated
    public Verse(int ordinal) {
        this(Versifications.instance().getDefaultVersification(), ordinal);
    }

    /**
     * Set a Verse using a Verse Ordinal number - WARNING Do not use this method
     * unless you really know the dangers of doing so. Ordinals are not always
     * going to be the same. So you should use a Verse or an int[3] in
     * preference to an int ordinal whenever possible. Ordinal numbers are 1
     * based and not 0 based.
     * 
     * @param v11n
     *            The versification to which this verse belongs
     * @param ordinal
     *            The verse id
     */
    public Verse(Versification v11n, int ordinal) {
        Verse decoded = v11n.decodeOrdinal(ordinal);
        this.originalName = null;
        this.book = decoded.book;
        this.chapter = decoded.chapter;
        this.verse = decoded.verse;
        this.ordinal = decoded.ordinal;
    }

    @Override
    public String toString() {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName()
     */
    public String getName() {
        return getName(null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage.Key)
     */
    public String getName(Key base) {
        if (base != null && !(base instanceof Verse)) {
            return getName();
        }

        if (PassageUtil.isPersistentNaming() && originalName != null) {
            return originalName;
        }

        String verseName = doGetName((Verse) base);
        // Only shape it if it can be unshaped.
        if (shaper.canUnshape()) {
            return shaper.shape(verseName);
        }

        return verseName;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getRootName()
     */
    public String getRootName() {
        return v11n.getShortName(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisRef()
     */
    public String getOsisRef() {
        return book.getOSIS() + Verse.VERSE_OSIS_DELIM + chapter + Verse.VERSE_OSIS_DELIM + verse;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisID()
     */
    public String getOsisID() {
        return getOsisRef();
    }

    @Override
    public Verse clone() {
        Verse copy = null;
        try {
            copy = (Verse) super.clone();
            copy.originalName = this.originalName;
            copy.v11n = this.v11n;
            copy.book = this.book;
            copy.chapter = this.chapter;
            copy.verse = this.verse;
            copy.ordinal = this.ordinal;
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }

        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        // Since this can not be null
        if (obj == null) {
            return false;
        }

        // Check that that is the same as this
        // Don't use instanceOf since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }

        Verse that = (Verse) obj;

        // The real tests
        return this.ordinal == that.ordinal && this.v11n.equals(that.v11n);
    }

    @Override
    public int hashCode() {
        return ordinal;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Key obj) {
        Verse that = null;
        if (obj instanceof Verse) {
            that = (Verse) obj;
        } else {
            that = ((VerseRange) obj).getStart();
        }

        int thatStart = that.ordinal;
        int thisStart = this.ordinal;

        if (thatStart > thisStart) {
            return -1;
        }

        if (thatStart < thisStart) {
            return 1;
        }

        return 0;
    }

    /**
     * Is this verse adjacent to another verse
     * 
     * @param that
     *            The thing to compare against
     * @return true if that verse is next to this verse
     * @deprecated Use {@link Versification#isAdjacentVerse(Verse, Verse)}
     */
    @Deprecated
    public boolean adjacentTo(Verse that) {
        return v11n.isAdjacentVerse(this, that);
    }

    /**
     * How many verses are there in between the 2 Verses. The answer is -ve if
     * start is bigger than this (the end). The answer is inclusive of start and exclusive
     * of this, so that <code>gen12.subtract(gen11) == 1</code>
     * 
     * @param start
     *            The Verse to compare this to
     * @return The count of verses between this and that.
     * @deprecated Use {@link Versification#distance(Verse, Verse)}
     */
    @Deprecated
    public int subtract(Verse start) {
        return v11n.distance(this, start);
    }

    /**
     * Get the verse n down from here this Verse.
     * 
     * @param n
     *            The number to count down by
     * @return The new Verse
     * @deprecated Use {@link Versification#subtract(Verse, int)}
     */
    @Deprecated
    public Verse subtract(int n) {
        return v11n.subtract(this, n);
    }

    /**
     * Get the verse that is a few verses on from the one we've got.
     * 
     * @param n
     *            the number of verses later than the one we're one
     * @return The new verse
     * @deprecated Use {@link Versification#add(Verse, int)}
     */
    @Deprecated
    public Verse add(int n) {
        return v11n.add(this, n);
    }

    /**
     * Return the versification to which this verse belongs.
     * 
     * @return the versification of this verse
     */
    public Versification getVersification() {
        return v11n;
    }

    /**
     * Return the book that we refer to
     * 
     * @return The book of the Bible
     */
    public BibleBook getBook() {
        return book;
    }

    /**
     * Return the chapter that we refer to
     * 
     * @return The chapter number
     */
    public int getChapter() {
        return chapter;
    }

    /**
     * Return the verse that we refer to
     * 
     * @return The verse number
     */
    public int getVerse() {
        return verse;
    }

    /**
     * Is this verse the first in a chapter
     * 
     * @return true or false ...
     * @deprecated Use {@link Versification#isStartOfChapter(Verse)}
     */
    @Deprecated
    public boolean isStartOfChapter() {
        return v11n.isStartOfChapter(this);
    }

    /**
     * Is this verse the last in a chapter
     * 
     * @return true or false ...
     * @deprecated Use {@link Versification#isEndOfChapter(Verse)}
     */
    @Deprecated
     public boolean isEndOfChapter() {
        return v11n.isEndOfChapter(this);
    }

    /**
     * Is this verse the first in a chapter
     * 
     * @return true or false ...
     * @deprecated Use {@link Versification#isStartOfBook(Verse)}
     */
    @Deprecated
    public boolean isStartOfBook() {
        return v11n.isStartOfBook(this);
    }

    /**
     * Is this verse the last in a chapter
     * 
     * @return true or false ...
     * @deprecated Use {@link Versification#isEndOfBook(Verse)}
     */
    @Deprecated
    public boolean isEndOfBook() {
        return v11n.isEndOfBook(this);
    }

    /**
     * Is this verse in the same chapter as that one
     * 
     * @param that
     *            The verse to compare to
     * @return true or false ...
     * @deprecated Use {@link Versification#isSameChapter(Verse,Verse)}
     */
    @Deprecated
    public boolean isSameChapter(Verse that) {
        return book == that.book && chapter == that.chapter;
    }

    /**
     * Is this verse in the same book as that one
     * 
     * @param that
     *            The verse to compare to
     * @return true or false ...
     * @deprecated Use {@link Versification#isSameBook(Verse,Verse)}
     */
    @Deprecated
    public boolean isSameBook(Verse that) {
        return book == that.book;
    }

    /**
     * Return the ordinal value of the verse in its versification.
     * 
     * @return The verse number
     */
    public int getOrdinal() {
        return ordinal;
    }

    /**
     * Return the bigger of the 2 verses. If the verses are equal() then return
     * Verse a
     * 
     * @param a
     *            The first verse to compare
     * @param b
     *            The second verse to compare
     * @return The bigger of the 2 verses
     * @deprecated Use {@link Versification#max(Verse,Verse)}
     */
    @Deprecated
    public static Verse max(Verse a, Verse b) {
        if (a.compareTo(b) == -1) {
            return b;
        }
        return a;
    }

    /**
     * Return the smaller of the 2 verses. If the verses are equal() then return
     * Verse a
     * 
     * @param a
     *            The first verse to compare
     * @param b
     *            The second verse to compare
     * @return The smaller of the 2 verses
     * @deprecated Use {@link Versification#min(Verse,Verse)}
     */
    @Deprecated
    public static Verse min(Verse a, Verse b) {
        if (a.compareTo(b) == 1) {
            return b;
        }
        return a;
    }

    /**
     * Create an array of Verses
     * 
     * @return The array of verses that this makes up
     */
    public Verse[] toVerseArray() {
        return new Verse[] {
            this
        };
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent() {
        return null;
    }

    /**
     * Compute the verse representation given the context.
     * 
     * @param verseBase
     *            the context or null if there is none
     * @return the verse representation
     */
    private String doGetName(Verse verseBase) {
        StringBuilder buf = new StringBuilder();
        // To cope with thing like Jude 2...
        if (v11n.getLastChapter(book) == 1) {
            if (verseBase == null || verseBase.book != book) {
                buf.append(v11n.getPreferredName(book));
                buf.append(Verse.VERSE_PREF_DELIM1);
                buf.append(verse);
                return buf.toString();
            }

            return Integer.toString(verse);
        }

        if (verseBase == null || verseBase.book != book) {
            buf.append(v11n.getPreferredName(book));
            buf.append(Verse.VERSE_PREF_DELIM1);
            buf.append(chapter);
            buf.append(Verse.VERSE_PREF_DELIM2);
            buf.append(verse);
            return buf.toString();
        }

        if (verseBase.chapter != chapter) {
            buf.append(chapter);
            buf.append(Verse.VERSE_PREF_DELIM2);
            buf.append(verse);
            return buf.toString();
        }

        return Integer.toString(verse);
    }

    /**
     * This is simply a convenience function to wrap Integer.parseInt() and give
     * us a reasonable exception on failure. It is called by VerseRange hence
     * protected, however I would prefer private
     * 
     * @param text
     *            The string to be parsed
     * @return The correctly parsed chapter or verse
     */
    protected static int parseInt(String text) throws NoSuchVerseException {
        try {
            return Integer.parseInt(shaper.unshape(text));
        } catch (NumberFormatException ex) {
            // TRANSLATOR: The chapter or verse number is actually not a number, but something else.
            // {0} is a placeholder for what the user supplied.
            throw new NoSuchVerseException(JSMsg.gettext("Cannot understand {0} as a chapter or verse.", text));
        }
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * 
     * @param out
     *            The stream to write our state to
     * @throws IOException
     *             if the read fails
     * @serialData Write the ordinal number of this verse
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(v11n.getName());

        // Ignore the original name. Is this wise?
        // I am expecting that people are not that fussed about it and
        // it could make everything far more verbose
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * 
     * @param in
     *            The stream to read our state from
     * @throws IOException
     *             if the read fails
     * @throws ClassNotFoundException
     *             If the read data is incorrect
     * @serialData Write the ordinal number of this verse
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String v11nName = in.readUTF();
        v11n = Versifications.instance().getVersification(v11nName);
        Verse decoded = v11n.decodeOrdinal(ordinal);

        this.book = decoded.book;
        this.chapter = decoded.chapter;
        this.verse = decoded.verse;

        // We are ignoring the originalName. It was set to null in the
        // default ctor so I will ignore it here.
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount() {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality() {
        return 1;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        return this.equals(key);
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Key> iterator() {
        return new ItemIterator<Key>(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#addAll(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#removeAll(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retainAll(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key) {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear() {
        // do nothing
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index) {
        if (index == 0) {
            return this;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that) {
        if (this.equals(that)) {
            return 0;
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int, org.crosswire.jsword.passage.RestrictionType)
     */
    public void blur(int by, RestrictionType restrict) {
        throw new UnsupportedOperationException();
    }

    /**
     * To make serialization work across new versions
     */
    static final long serialVersionUID = -4033921076023185171L;

    /**
     * What characters should we use to separate parts of an OSIS verse
     * reference
     */
    public static final String VERSE_OSIS_DELIM = ".";

    /**
     * What characters should we use to separate the book from the chapter
     */
    public static final String VERSE_PREF_DELIM1 = " ";

    /**
     * What characters should we use to separate the chapter from the verse
     */
    public static final String VERSE_PREF_DELIM2 = ":";

    /**
     * The default verse
     */
    public static final Verse DEFAULT = new Verse(Versifications.instance().getVersification("KJV"), BibleBook.GEN, 1, 1);

    /**
     * Allow the conversion to and from other number representations.
     */
    private static NumberShaper shaper = new NumberShaper();

    /**
     * The versification for this verse.
     */
    private transient Versification v11n;

    /**
     * The ordinal value for this verse within its versification.
     */
    private int ordinal;

    /**
     * The book of the Bible.
     */
    private transient BibleBook book;

    /**
     * The chapter number
     */
    private transient int chapter;

    /**
     * The verse number
     */
    private transient int verse;

   /**
     * The original string for picky users
     */
    private transient String originalName;

}
