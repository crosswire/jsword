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

import org.crosswire.common.icu.NumberShaper;
import org.crosswire.common.util.ItemIterator;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BibleNames;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public final class Verse implements VerseKey<Verse> {
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
        this(v11n, book, chapter, verse, null);
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
     * @param subIdentifier
     *            The optional sub identifier
     */
    public Verse(Versification v11n, BibleBook book, int chapter, int verse, String subIdentifier) {
        this.v11n = v11n;
        this.book = book;
        this.chapter = chapter;
        this.verse = verse;
        this.subIdentifier = subIdentifier;
        this.ordinal = v11n.getOrdinal(this);
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
     * @param patchUp
     *            True to trigger reference fixing
     */
    public Verse(Versification v11n, BibleBook book, int chapter, int verse, boolean patchUp) {
        if (!patchUp) {
            throw new IllegalArgumentException(JSOtherMsg.lookupText("Use patchUp=true."));
        }

        this.v11n = v11n;
        Verse patched = this.v11n.patch(book, chapter, verse);
        this.book = patched.book;
        this.chapter = patched.chapter;
        this.verse = patched.verse;
        this.ordinal = patched.ordinal;
    }

    /**
     * Set a Verse using a verse ordinal number - WARNING Do not use this method
     * unless you really know the dangers of doing so. Ordinals are not always
     * going to be the same. So you should use Versification, Book, Chapter and Verse
     * in preference to an int ordinal whenever possible. Ordinal numbers are 1
     * based and not 0 based.
     * 
     * @param v11n
     *            The versification to which this verse belongs
     * @param ordinal
     *            The verse id
     */
    public Verse(Versification v11n, int ordinal) {
        Verse decoded = v11n.decodeOrdinal(ordinal);
        this.v11n = v11n;
        this.book = decoded.book;
        this.chapter = decoded.chapter;
        this.verse = decoded.verse;
        this.ordinal = decoded.ordinal;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.VerseKey#isWhole()
     */
    public boolean isWhole() {
        return subIdentifier == null || subIdentifier.length() == 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.VerseKey#getWhole()
     */
    public Verse getWhole() {
        if (isWhole()) {
            return this;
        }
        return new Verse(v11n, book, chapter, verse);
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
        return BibleNames.instance().getShortName(book);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisRef()
     */
    public String getOsisRef() {
        return getOsisID();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisID()
     */
    public String getOsisID() {
        final StringBuilder buf = getVerseIdentifier();
        if (subIdentifier != null && subIdentifier.length() > 0) {
            buf.append(VERSE_OSIS_SUB_PREFIX);
            buf.append(subIdentifier);
        }
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisID()
     */
    public String getOsisIDNoSubIdentifier() {
        return getVerseIdentifier().toString();
    }

    /**
     * Gets the common name of the verse, excluding any !abc sub-identifier
     * @return the verse OSIS-ID, excluding the sub-identifier
     */
    private StringBuilder getVerseIdentifier() {
        StringBuilder buf = new StringBuilder();
        buf.append(book.getOSIS());
        buf.append(Verse.VERSE_OSIS_DELIM);
        buf.append(chapter);
        buf.append(Verse.VERSE_OSIS_DELIM);
        buf.append(verse);
        return buf;
    }

    @Override
    public Verse clone() {
        Verse copy = null;
        try {
            copy = (Verse) super.clone();
            copy.v11n = this.v11n;
            copy.book = this.book;
            copy.chapter = this.chapter;
            copy.verse = this.verse;
            copy.ordinal = this.ordinal;
            copy.subIdentifier = this.subIdentifier;
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }

        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        // Since this can not be null
        if (!(obj instanceof Verse)) {
            return false;
        }

        Verse that = (Verse) obj;

        // The real tests
        return this.ordinal == that.ordinal
                && this.v11n.equals(that.v11n)
                && bothNullOrEqual(this.subIdentifier, that.subIdentifier);
    }

    @Override
    public int hashCode() {
        int result = 31 + ordinal;
        result = 31 * result + ((v11n == null) ? 0 : v11n.hashCode());
        return 31 * result + ((subIdentifier == null) ? 0 : subIdentifier.hashCode());
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Key obj) {
        return this.ordinal - ((Verse) obj).ordinal;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.VerseKey#getVersification()
     */
    public Versification getVersification() {
        return v11n;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Passage#reversify(org.crosswire.jsword.versification.Versification)
     */
    public Verse reversify(Versification newVersification) {
        if (v11n.equals(newVersification)) {
            return this;
        }

        try {
            //check the v11n supports this key, otherwise this leads to all sorts of issues
            if (newVersification.validate(book, chapter, verse, true)) {
                return new Verse(newVersification, book, chapter, verse);
            }
        } catch (NoSuchVerseException ex) {
            // will never happen
            log.error("Contract for validate was changed to thrown an exception when silent mode is true", ex);
        }
        return null;
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
     * Return the sub identifier if any
     * @return The optional OSIS sub identifier
     */
    public String getSubIdentifier() {
        return subIdentifier;
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
     * Determine whether two objects are equal, allowing nulls
     * @param x
     * @param y
     * @return true if both are null or the two are equal
     */
    public static boolean bothNullOrEqual(Object x, Object y) {
        return x == y || (x != null && x.equals(y));
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
        if (book.isShortBook()) {
            if (verseBase == null || verseBase.book != book) {
                buf.append(BibleNames.instance().getPreferredName(book));
                buf.append(Verse.VERSE_PREF_DELIM1);
                buf.append(verse);
                return buf.toString();
            }

            return Integer.toString(verse);
        }

        if (verseBase == null || verseBase.book != book) {
            buf.append(BibleNames.instance().getPreferredName(book));
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
     * What characters should we use to separate parts of an OSIS verse
     * reference
     */
    public static final char VERSE_OSIS_DELIM = '.';

    /**
     * What characters should we use to start an OSIS sub identifier
     */
    public static final char VERSE_OSIS_SUB_PREFIX = '!';

    /**
     * What characters should we use to separate the book from the chapter
     */
    public static final char VERSE_PREF_DELIM1 = ' ';

    /**
     * What characters should we use to separate the chapter from the verse
     */
    public static final char VERSE_PREF_DELIM2 = ':';

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
     * The OSIS Sub-identifier if present.
     * This should be a string that allows for the likes of:
     * a.xy.asdf.qr
     */
    private String subIdentifier;

    private static final Logger log = LoggerFactory.getLogger(Verse.class);

    /**
     * To make serialization work across new versions
     */
    private static final long serialVersionUID = -4033921076023185171L;
}
