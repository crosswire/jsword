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
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * A Verse is a pointer to a single verse. Externally its unique identifier is
 * a String of the form "Gen 1:1" Internally we use
 * <code>( book, chapter, verse )</code>
 * 
 * <p>
 * A Verse is designed to be immutable. This is a necessary from a collections
 * point of view. A Verse should always be valid, although some versions may not
 * return any text for verses that they consider to be mis-translated in some
 * way.
 * </p>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Verse implements Key {
    /**
     * The default Verse is Genesis 1:1. I didn't want to provide this
     * constructor however, you are supposed to provide a default ctor for all
     * beans. For this reason I suggest you don't use it.
     */
    public Verse() {
        originalName = null;

        book = DEFAULT.book;
        chapter = DEFAULT.chapter;
        verse = DEFAULT.verse;
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
     * @exception NoSuchVerseException
     *                If the reference is illegal
     */
    /* package */Verse(String original, BibleBook book, int chapter, int verse) throws NoSuchVerseException {
        originalName = original;
        set(book, chapter, verse);
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
     * @exception NoSuchVerseException
     *                If the reference is illegal
     */
    public Verse(BibleBook book, int chapter, int verse) throws NoSuchVerseException {
        this(null, book, chapter, verse);
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
     */
    public Verse(BibleBook book, int chapter, int verse, boolean patch_up) {
        if (!patch_up) {
            throw new IllegalArgumentException(JSOtherMsg.lookupText("Use patch=true."));
        }

        originalName = null;
        setAndPatch(book, chapter, verse);
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
     * @exception NoSuchVerseException
     *                If the reference is illegal
     */
    public Verse(int ordinal) throws NoSuchVerseException {
        originalName = null;
        set(ordinal);
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

        try {
            if (PassageUtil.isPersistentNaming() && originalName != null) {
                return originalName;
            }

            String verseName = doGetName((Verse) base);
            // Only shape it if it can be unshaped.
            if (shaper.canUnshape()) {
                return shaper.shape(verseName);
            }

            return verseName;
        } catch (NoSuchKeyException ex) {
            assert false : ex;
            return "!Error!";
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getRootName()
     */
    public String getRootName() {
        try {
            return book.getShortName();
        } catch (NoSuchKeyException ex) {
            assert false : ex;
            return "!Error!";
        }
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
            copy.book = book;
            copy.chapter = chapter;
            copy.verse = verse;
            copy.originalName = originalName;
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

        Verse v = (Verse) obj;

        // The real tests
        if (v.getBook() != getBook()) {
            return false;
        }

        if (v.getChapter() != getChapter()) {
            return false;
        }

        if (v.getVerse() != getVerse()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return getOrdinal();
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

        int thatStart = that.getOrdinal();
        int thisStart = this.getOrdinal();

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
     * @return 1 means he is earlier than me, -1 means he is later ...
     */
    public boolean adjacentTo(Verse that) {
        return Math.abs(that.getOrdinal() - getOrdinal()) == 1;
    }

    /**
     * How many verses are there in between the 2 Verses. The answer is -ve if
     * that is bigger than this. The answer is inclusive of that and exclusive
     * of this, so that <code>gen11.subtract(gen12) == 1</code>
     * 
     * @param that
     *            The Verse to compare this to
     * @return The count of verses between this and that.
     */
    public int subtract(Verse that) {
        return getOrdinal() - that.getOrdinal();
    }

    /**
     * Get the verse n down from here this Verse.
     * 
     * @param n
     *            The number to count down by
     * @return The new Verse
     */
    public Verse subtract(int n) {
        try {
            int new_ordinal = Math.max(getOrdinal() - n, 1);
            return new Verse(new_ordinal);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return Verse.DEFAULT;
        }
    }

    /**
     * Get the verse that is a few verses on from the one we've got.
     * 
     * @param extra
     *            the number of verses later than the one we're one
     * @return The new verse
     */
    public Verse add(int extra) {
        try {
            int new_ordinal = Math.min(getOrdinal() + extra, BibleInfo.versesInBible());
            return new Verse(new_ordinal);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return Verse.DEFAULT;
        }
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
     */
    public boolean isStartOfChapter() {
        return verse == 1;
    }

    /**
     * Is this verse the first in a chapter
     * 
     * @return true or false ...
     */
    public boolean isEndOfChapter() {
        try {
            return verse == BibleInfo.versesInChapter(book, chapter);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return true;
        }
    }

    /**
     * Is this verse the first in a chapter
     * 
     * @return true or false ...
     */
    public boolean isStartOfBook() {
        return verse == 1 && chapter == 1;
    }

    /**
     * Is this verse the first in a chapter
     * 
     * @return true or false ...
     */
    public boolean isEndOfBook() {
        try {
            return verse == BibleInfo.versesInChapter(book, chapter) && chapter == BibleInfo.chaptersInBook(book);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return true;
        }
    }

    /**
     * Is this verse in the same chapter as that one
     * 
     * @param that
     *            The verse to compare to
     * @return true or false ...
     */
    public boolean isSameChapter(Verse that) {
        return book == that.book && chapter == that.chapter;
    }

    /**
     * Is this verse in the same book as that one
     * 
     * @param that
     *            The verse to compare to
     * @return true or false ...
     */
    public boolean isSameBook(Verse that) {
        return book == that.book;
    }

    /**
     * Return the verse id that we refer to, where Gen 1:1 = 1, and Rev 22:21 =
     * 31104
     * 
     * @return The verse number
     */
    public int getOrdinal() {
        try {
            return BibleInfo.verseOrdinal(this);
        } catch (NoSuchVerseException ex) {
            // A verse should never be illegal so
            log.error("ref=" + book + ", " + chapter + ", " + verse);
            assert false : ex;
            return 1;
        }
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
     */
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
     */
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

    /**
     * Create a new Verse being the last verse in the current book
     * 
     * @return The last verse in this book
     */
    public Verse getLastVerseInBook() {
        try {
            int lastchap = BibleInfo.chaptersInBook(book);
            int lastverse = BibleInfo.versesInChapter(book, lastchap);

            return new Verse(book, lastchap, lastverse);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return Verse.DEFAULT;
        }
    }

    /**
     * Create a new Verse being the last verse in the current book
     * 
     * @return The last verse in this book
     */
    public Verse getLastVerseInChapter() {
        try {
            int lastverse = BibleInfo.versesInChapter(book, chapter);

            return new Verse(book, chapter, lastverse);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return Verse.DEFAULT;
        }
    }

    /**
     * Create a new Verse being the first verse in the current book
     * 
     * @return The first verse in this book
     */
    public Verse getFirstVerseInBook() {
        try {
            return new Verse(book, 1, 1);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return Verse.DEFAULT;
        }
    }

    /**
     * Create a new Verse being the first verse in the current book
     * 
     * @return The first verse in this book
     */
    public Verse getFirstVerseInChapter() {
        try {
            return new Verse(book, chapter, 1);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return Verse.DEFAULT;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent() {
        return parent;
    }

    /**
     * Set a parent Key. This allows us to follow the Key interface more
     * closely, although the concept of a parent for a verse is fairly alien.
     * 
     * @param parent
     *            The parent Key for this verse
     */
    public void setParent(Key parent) {
        this.parent = parent;
    }

    /**
     * Compute the verse representation given the context.
     * 
     * @param verseBase
     *            the context or null if there is none
     * @return the verse representation
     * @throws NoSuchVerseException
     */
    private String doGetName(Verse verseBase) throws NoSuchVerseException {
        // To cope with thing like Jude 2...
        if (BibleInfo.chaptersInBook(book) == 1) {
            if (verseBase == null || verseBase.book != book) {
                return book.getPreferredName() + Verse.VERSE_PREF_DELIM1 + verse;
            }

            return String.valueOf(verse);
        }

        if (verseBase == null || verseBase.book != book) {
            return book.getPreferredName() + Verse.VERSE_PREF_DELIM1 + chapter + Verse.VERSE_PREF_DELIM2 + verse;
        }

        if (verseBase.chapter != chapter) {
            return chapter + Verse.VERSE_PREF_DELIM2 + verse;
        }

        return String.valueOf(verse);
    }

    /**
     * This is simply a convenience function to wrap Integer.parseInt() and give
     * us a reasonable exception on failure. It is called by VerseRange hence
     * protected, however I would prefer private
     * 
     * @param text
     *            The string to be parsed
     * @return The correctly parsed chapter or verse
     * @exception NoSuchVerseException
     *                If the reference is illegal
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
     * Mutate into this reference and fix the reference if needed. This must
     * only be called from a ctor to maintain immutability
     * 
     * @param book
     *            The book to set (Genesis = 1)
     * @param chapter
     *            The chapter to set
     * @param verse
     *            The verse to set
     */
    private void setAndPatch(BibleBook book, int chapter, int verse) {
        Verse patched = BibleInfo.patch(book, chapter, verse);

        this.book = patched.book;
        this.chapter = patched.chapter;
        this.verse = patched.verse;
    }

    /**
     * Verify and set the references. This must only be called from a ctor to
     * maintain immutability
     * 
     * @param book
     *            The book to set (Genesis = 1)
     * @param chapter
     *            The chapter to set
     * @param verse
     *            The verse to set
     * @exception NoSuchVerseException
     *                If the verse can not be understood
     */
    private void set(BibleBook book, int chapter, int verse) throws NoSuchVerseException {
        BibleInfo.validate(book, chapter, verse);

        this.book = book;
        this.chapter = chapter;
        this.verse = verse;
    }

    /**
     * Set the references. This must only be called from a ctor to maintain immutability
     * 
     * @param ordinal
     *            The ordinal of the verse
     * @exception NoSuchVerseException
     *                If the verse can not be understood
     */
    private void set(int ordinal) throws NoSuchVerseException {
        Verse v = BibleInfo.decodeOrdinal(ordinal);

        book = v.book;
        chapter = v.chapter;
        verse = v.verse;
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
        // Call even if there is no default serializable fields.
        out.defaultWriteObject();

        // save the ordinal of the verse
        out.writeInt(getOrdinal());

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
        // Call even if there is no default serializable fields.
        in.defaultReadObject();

        try {
            set(in.readInt());
        } catch (NoSuchVerseException ex) {
            throw new IOException(ex.getMessage());
        }

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
    public static final Verse DEFAULT = new Verse(BibleBook.GENESIS, 1, 1, true);

    /**
     * Allow the conversion to and from other number representations.
     */
    private static NumberShaper shaper = new NumberShaper();

    /**
     * The parent key. See the key interface for more information.
     * 
     * NOTE(joe): These keys are not serialized, should we?
     * 
     * @see Key
     */
    private transient Key parent;

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

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Verse.class);
}
