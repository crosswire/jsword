
package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.crosswire.common.util.LogicError;

/**
 * A Passage is a pointer to a single verse. Externally its unique
 * identifier is a String of the form "Gen 1:1" Internally we use
 * <code>int[] { book, chapter, verse }</code>
 *
 * <p>A Verse is designed to be immutable. This is a necessary from a
 * collections point of view. A Verse should always be valid, although
 * some versions may not return any text for verses that they consider to
 * be mis-translated in some way.</p>
 *
 * <p>Optimization information: I spent some time optimizing this class
 * because it is at the heart of things. My benchmark started st 11.25s.
 * By taking the int[] and turning it into 3 ints and it took 10.8s.<br />
 * Cacheing the ordinal number just took the time from 12s to 12s! I guess
 * that the time and extra memory taken up by the extra int overrode the
 * time it saved by repeated queries to the same verse. I guess this would
 * change if we were using a [Ranged|Distinct]Passage instead of a Bitwise
 * Passage (as in the test). Maybe it would be a good idea to have an
 * extra class OrdCacheVerse (or something) that gave us the best of both
 * worlds?<br />
 * Removing the default initialization of the ints took the time down by
 * about 0.25s also.
 * </p>
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version $Id$
 */
public class Verse implements VerseBase
{
    /**
     * The default Verse is Genesis 1:1. I didn't want to provide this
     * constructor however, you are supposed to provide a default ctor
     * for all beans. For this reason I suggest you don't use it.
     */
    public Verse()
    {
        original_name = null;

        book = DEFAULT.book;
        chapter = DEFAULT.chapter;
        verse = DEFAULT.verse;
    }

    /**
     * Construct a Verse from a String - something like "Gen 1:1".
     * in case the user does not want to have their typing 'fixed' by a
     * meddling patronizing computer. The following initial letters can
     * not be matched at all - 'bfquvwx'.
     * @param desc The text string to be validated
     * @exception NoSuchVerseException If the text can not be understood
     */
    public Verse(String desc) throws NoSuchVerseException
    {
        this(desc, DEFAULT);
    }

    /**
     * Construct a Ref from a String and a Verse. For example given "2:2"
     * and a basis of Gen 1:1 the result would be Gen 2:2
     * @param desc The string describing the verse e.g "2:2"
     * @param basis The basis by which to understand the desc.
     * @exception NoSuchVerseException If the reference is illegal
     */
    public Verse(String desc, Verse basis) throws NoSuchVerseException
    {
        String[] parts = tokenize(desc, VERSE_ALLOWED_DELIMS);
        original_name = desc;

        switch (getAccuracy(parts))
        {
        case ACCURACY_BOOK_VERSE:
            if (parts.length == 3) set(parts[0], parts[1], parts[2]);
            else                   set(parts[0], 1, parts[1]);
            break;

        case ACCURACY_BOOK_CHAPTER:
            set(parts[0], parts[1], 1);
            break;

        case ACCURACY_BOOK_ONLY:
            set(parts[0], 1, 1);
            break;

        case ACCURACY_CHAPTER_VERSE:
            set(basis.getBook(), parts[0], parts[1]);
            break;

        case ACCURACY_VERSE_ONLY:
            set(basis.getBook(), basis.getChapter(), parts[0]);
            break;

        case ACCURACY_NONE:
            set(basis.getBook(), basis.getChapter(), basis.getVerse());
            break;

        default:
            throw new LogicError();
        }
    }

    /**
     * Create a Verse from book, chapter and verse numbers, throwing up
     * if the specified Verse does not exist
     * @param book The book number (Genesis = 1)
     * @param chapter The chapter number
     * @param verse The verse number
     * @exception NoSuchVerseException If the reference is illegal
     */
    public Verse(int book, int chapter, int verse) throws NoSuchVerseException
    {
        original_name = null;
        set(book, chapter, verse);
    }

    /**
     * Create a Verse from book, chapter and verse numbers, patching up if
     * the specified verse does not exist.
     * <p>The actual value of the boolean is ignored. However for future
     * proofing you should only use 'true'. Do not use patch_up=false, use
     * <code>Verse(int, int, int)</code> This so that we can declare this
     * constructor to not throw an exception. Is there a better way of
     * doing this?
     * @param book The book number (Genesis = 1)
     * @param chapter The chapter number
     * @param verse The verse number
     * @param patch_up True to trigger reference fixing
     */
    public Verse(int book, int chapter, int verse, boolean patch_up)
    {
        if (!patch_up)
            throw new IllegalArgumentException(PassageUtil.getResource("verse_error_patch"));

        original_name = null;
        setAndPatch(book, chapter, verse);
    }

    /**
     * Set a Verse using a Verse Ordinal number - WARNING Do not use this
     * method unless you really know the dangers of doing so. Ordinals are
     * not always going to be the same. So you should use a Verse or an
     * int[3] in preference to an int ordinal whenever possible. Ordinal
     * numbers are 1 based and not 0 based.
     * @param ordinal The verse id
     * @exception NoSuchVerseException If the reference is illegal
     */
    public Verse(int ordinal) throws NoSuchVerseException
    {
        original_name = null;
        set(ordinal);
    }

    /**
     * Translate the Passage into a human readable string. This is
     * simply an alias for getName();
     * @return The string representation
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Translate the Passage into a human readable string
     * @return The string representation
     */
    public String getName()
    {
        try
        {
            if (PassageUtil.isPersistentNaming() && original_name != null)
            {
                return original_name;
            }

            // To cope with thing like Jude 2...
            if (Books.chaptersInBook(book) == 1)
            {
                return Books.getShortBookName(book) + VERSE_PREF_DELIM1 + verse;
            }
            else
            {
                return Books.getShortBookName(book) + VERSE_PREF_DELIM1 + chapter + VERSE_PREF_DELIM2 + verse;
            }
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Translate the Passage into a human readable string
     * @param base The verse to use to cut down unnecessary output.
     * @return The string representation
     */
    public String getName(Verse base)
    {
        if (base == null) return getName();

        try
        {
            if (PassageUtil.isPersistentNaming() && original_name != null)
            {
                return original_name;
            }

            // To cope with thing like Jude 2...
            if (Books.chaptersInBook(book) == 1)
            {
                if (base.book != book)
                    return Books.getShortBookName(book) + VERSE_PREF_DELIM1 + verse;

                return ""+verse;
            }
            else
            {
                if (base.book != book)
                    return Books.getShortBookName(book) + VERSE_PREF_DELIM1 + chapter + VERSE_PREF_DELIM2 + verse;

                if (base.chapter != chapter)
                    return chapter + VERSE_PREF_DELIM2 + verse;

                return ""+verse;
            }
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Get a copy of ourselves. Points to note:
     *   Call clone() not new() on member Objects, and on us.
     *   Do not use Copy Constructors! - they do not inherit well.
     *   Think about this needing to be synchronized
     *   If this is not cloneable then writing cloneable children is harder
     * @return A complete copy of ourselves
     * @exception CloneNotSupportedException We don't do this but our kids might
     */
    public Object clone() throws CloneNotSupportedException
    {
        Verse copy = (Verse) super.clone();

        copy.book = book;
        copy.chapter = chapter;
        copy.verse = verse;
        //copy.ord = ord;
        copy.original_name = original_name;

        return copy;
    }

    /**
     * Is this Object equal to us. Points to note:
     *   If you override equals(), you must override hashCode() too.
     *   If you are doing this it is a good idea to be immutable.
     * @param obj The thing to test against
     * @return True/False is we are or are not equal to obj
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null) return false;

        // Check that that is the same as this
        // Don't use instanceOf since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) return false;

        Verse v = (Verse) obj;

        // The real tests
        if (v.getBook() != getBook()) return false;
        if (v.getChapter() != getChapter()) return false;
        if (v.getVerse() != getVerse()) return false;

        return true;
    }

    /**
     * This returns the ordinal number of the verse
     * so <code>new Verse("Rev 22:21").hashCode() = 31104</code>.
     * <p><b>However should should not reply on this being true</b>
     * @return The hashing number
     */
    public int hashCode()
    {
        return getOrdinal();
    }

    /**
     * Compare this to a given object
     * @param obj The thing to compare against
     * @return 1 means he is earlier than me, -1 means he is later ...
     */
    public int compareTo(Object obj)
    {
        Verse that = null;
        if (obj instanceof Verse)   that = (Verse) obj;
        else                        that = ((VerseRange) obj).getStart();

        int that_start = that.getOrdinal();
        int this_start = this.getOrdinal();

        if (that_start > this_start) return -1;
        if (that_start < this_start) return 1;
        return 0;
    }

    /**
     * Is this verse adjacent to another verse
     * @param that The thing to compare against
     * @return 1 means he is earlier than me, -1 means he is later ...
     */
    public boolean adjacentTo(Verse that)
    {
        return Math.abs(that.getOrdinal() - getOrdinal()) == 1;
    }

    /**
     * How many verses are there in between the 2 Verses.
     * The answer is -ve if that is bigger than this.
     * The answer is inclusive of that and exclusive of this, so that
     * <code>gen11.difference(gen12) == 1</code>
     * @param that The Verse to compare this to
     * @return The count of verses between this and that.
     */
    public int subtract(Verse that)
    {
        return getOrdinal() - that.getOrdinal();
    }

    /**
     * Get the verse n down from here this Verse.
     * @param n The number to count down by
     * @return The new Verse
     */
    public Verse subtract(int n)
    {
        try
        {
            int new_ordinal = Math.max(getOrdinal() - n, 1);
            return new Verse(new_ordinal);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Get the verse that is a few verses on from the one
     * we've got.
     * @param extra the number of verses later than the one we're one
     * @return The new verse
     */
    public Verse add(int extra)
    {
        try
        {
            int new_ordinal = Math.min(getOrdinal() + extra, Books.versesInBible());
            return new Verse(new_ordinal);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Return the book that we refer to
     * @return The book number (Genesis = 1)
     */
    public int getBook()
    {
        return book;
    }

    /**
     * Return the chapter that we refer to
     * @return The chapter number
     */
    public int getChapter()
    {
        return chapter;
    }

    /**
     * Return the verse that we refer to
     * @return The verse number
     */
    public int getVerse()
    {
        return verse;
    }

    /**
     * Is this verse the first in a chapter
     * @return true or false ...
     */
    public boolean isStartOfChapter()
    {
        return verse == 1;
    }

    /**
     * Is this verse the first in a chapter
     * @return true or false ...
     */
    public boolean isEndOfChapter()
    {
        try
        {
            return verse == Books.versesInChapter(book, chapter);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Is this verse the first in a chapter
     * @return true or false ...
     */
    public boolean isStartOfBook()
    {
        return verse == 1 && chapter == 1;
    }

    /**
     * Is this verse the first in a chapter
     * @return true or false ...
     */
    public boolean isEndOfBook()
    {
        try
        {
            return verse == Books.versesInChapter(book, chapter)
                   && chapter == Books.chaptersInBook(book);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Is this verse in the same chapter as that one
     * @param that The verse to compate to
     * @return true or false ...
     */
    public boolean isSameChapter(Verse that)
    {
        return book == that.book && chapter == that.chapter;
    }

    /**
     * Is this verse in the same book as that one
     * @param that The verse to compate to
     * @return true or false ...
     */
    public boolean isSameBook(Verse that)
    {
        return book == that.book;
    }

    /**
     * Return the verse that we refer to
     * @return An array of 3 ints 0=book, 1=chapter, 2=verse
     */
    public int[] getRefArray()
    {
        return new int[] { book, chapter, verse };
    }

    /**
     * Return the verse id that we refer to, where Gen 1:1 = 1, and
     * Rev 22:21 = 31104
     * @return The verse number
     */
    public int getOrdinal()
    {
        //if (ord == -1)
        {
            try
            {
                return /*ord =*/ Books.verseOrdinal(book, chapter, verse);
            }
            catch (NoSuchVerseException ex)
            {
                // A verse should never be illegal so
                log.warn("ref="+book+", "+chapter+", "+verse);
                throw new LogicError(ex);
            }
        }

        //return ord;
    }

    /**
     * Does this string exactly define a Verse. For example:<ul>
     * <li>getAccuracy("Gen") == ACCURACY_BOOK_ONLY;
     * <li>getAccuracy("Gen 1:1") == ACCURACY_BOOK_VERSE;
     * <li>getAccuracy("Gen 1") == ACCURACY_BOOK_CHAPTER;
     * <li>getAccuracy("Jude 1") == ACCURACY_BOOK_VERSE;
     * <li>getAccuracy("Jude 1:1") == ACCURACY_BOOK_VERSE;
     * <li>getAccuracy("1:1") == ACCURACY_CHAPTER_VERSE;
     * <li>getAccuracy("1") == ACCURACY_VERSE_ONLY;
     * <li>getAccuracy("") == ACCURACY_NONE;
     * <ul>
     * @param desc The string to be tested for Rangeness
     * @return A constant specifing how precise the Verse is.
     * @exception NoSuchVerseException If the text can not be understood
     * @see Passage
     */
    public static int getAccuracy(String desc) throws NoSuchVerseException
    {
        String[] parts = tokenize(desc, VERSE_ALLOWED_DELIMS);

        return getAccuracy(parts);
    }

    /**
     * Does this string exactly define a Verse. For example:<ul>
     * <li>getAccuracy("Gen") == ACCURACY_BOOK_ONLY;
     * <li>getAccuracy("Gen 1:1") == ACCURACY_BOOK_VERSE;
     * <li>getAccuracy("Gen 1") == ACCURACY_BOOK_CHAPTER;
     * <li>getAccuracy("Jude 1") == ACCURACY_BOOK_VERSE;
     * <li>getAccuracy("Jude 1:1") == ACCURACY_BOOK_VERSE;
     * <li>getAccuracy("1:1") == ACCURACY_CHAPTER_VERSE;
     * <li>getAccuracy("1") == ACCURACY_VERSE_ONLY;
     * <li>getAccuracy("") == ACCURACY_NONE;
     * <ul>
     * @param parts The string array to be tested for Rangeness
     * @return A constant specifing how precise the Verse is.
     * @exception NoSuchVerseException If the text can not be understood
     * @see Passage
     */
    private static int getAccuracy(String[] parts) throws NoSuchVerseException
    {
        switch (parts.length)
        {
        case 0:
            return ACCURACY_NONE;

        case 1:
            if (Books.isBookName(parts[0])) return ACCURACY_BOOK_ONLY;
            checkValidChapterOrVerse(parts[0]);
            return ACCURACY_VERSE_ONLY;

        case 2:
            try
            {
                // Does it start with a book?
                int book = Books.getBookNumber(parts[0]);
                if (Books.chaptersInBook(book) == 1)    return ACCURACY_BOOK_VERSE;
                else                                    return ACCURACY_BOOK_CHAPTER;
            }
            catch (NoSuchVerseException ex) { }
            checkValidChapterOrVerse(parts[0]);
            checkValidChapterOrVerse(parts[1]);
            return ACCURACY_CHAPTER_VERSE;

        case 3:
            Books.getBookNumber(parts[0]);
            checkValidChapterOrVerse(parts[1]);
            checkValidChapterOrVerse(parts[2]);
            return ACCURACY_BOOK_VERSE;
        }

        throw new NoSuchVerseException("passg_verse_parts", new Object[] { VERSE_ALLOWED_DELIMS });
    }

    /**
     * Is this text valid in a chapter/verse context
     * @param text The string to test for validity
     * @throws NoSuchVerseException If the text is invalid
     */
    private final static void checkValidChapterOrVerse(String text) throws NoSuchVerseException
    {
        if (!isEndMarker(text))
            parseInt(text);
    }

    /**
     * Return the bigger of the 2 verses. If the verses are equal()
     * then return Verse a
     * @param a The first verse to compare
     * @param b The second verse to compare
     * @return The bigger of the 2 verses
     */
    public final static Verse max(Verse a, Verse b)
    {
        if (a.compareTo(b) == -1) return b;
        else                      return a;
    }

    /**
     * Return the smaller of the 2 verses. If the verses are equal()
     * then return Verse a
     * @param a The first verse to compare
     * @param b The second verse to compare
     * @return The smaller of the 2 verses
     */
    public final static Verse min(Verse a, Verse b)
    {
        if (a.compareTo(b) == 1) return b;
        else                     return a;
    }

    /**
     * Is this string a legal marker for 'to the end of the chapter'
     * @param text The string to be checked
     * @return true if this is a legal marker
     */
    public static boolean isEndMarker(String text)
    {
        if (text.equals(VERSE_END_MARK1)) return true;
        if (text.equals(VERSE_END_MARK2)) return true;

        return false;
    }


    /**
     * Create an array of Verses
     * @return The array of verses that this makes up
     */
    public Verse[] toVerseArray()
    {
        return new Verse[] { this };
    }

    /**
     * Enumerate over the verse in this verse!.
     * This may seem silly, however is is very useful to be able to treat
     * Verses and Ranges the same (VerseBase) and this is a common accessor.
     * @return A verse iterator
     */
    public Iterator verseIterator()
    {
        return new Iterator()
        {
            private boolean done = false;

            public boolean hasNext()
            {
                return !done;
            }

            public Object next()
            {
                done = true;
                return this;
            }

            public void remove() throws UnsupportedOperationException
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Take a string and parse it into an Array of Strings where each
     * part is likely to be a verse part (book, chapter, verse, ...)
     * @param command The string to parse.
     * @param delim A string containing the spacing characters.
     * @return The string array
     */
    private static String[] tokenize(String command, String delim)
    {
        // The substrings "ch" and "v" are really a book/chapter or
        // chapter/verse separators we should swap them for normal delims
        // I recon it is safe to assume that the is no more than one of
        // each
        int idx = command.lastIndexOf("v");
        if (idx != -1)
        {
            // Check that the "v" is surrounded my non letters - i.e.
            // it is not part of "prov"
            if (!Character.isLetter(command.charAt(idx-1)) &&
                !Character.isLetter(command.charAt(idx+1)))
            {
                command = command.substring(0, idx) + VERSE_PREF_DELIM2 + command.substring(idx+1);
            }
        }
        idx = command.lastIndexOf("ch");
        if (idx != -1)
        {
            // Check that the "ch" is surrounded my non letters - i.e.
            // it is not part of "chronicles"
            if (!Character.isLetter(command.charAt(idx-1)) &&
                !Character.isLetter(command.charAt(idx+2)))
            {
                command = command.substring(0, idx) + VERSE_PREF_DELIM1 + command.substring(idx+2);
            }
        }

        // Create the original string array
        StringTokenizer tokenize = new StringTokenizer(command, delim);
        String[] args = new String[tokenize.countTokens()];
        int argc = 0;
        while (tokenize.hasMoreTokens())
        {
            args[argc++] = tokenize.nextToken();
        }

        // If the first word is a number, and the second a word, but not an
        // EndMarker then this must be something like "2 Ki ...", so join
        // them together to get "2Ki ..."
        if (args.length > 1)
        {
            if (Character.isDigit(args[0].charAt(0))
                && Character.isLetter(args[1].charAt(0))
                && !isEndMarker(args[1]))
            {
                String[] oldargs = args;
                args = new String[oldargs.length - 1];

                args[0] = oldargs[0] + oldargs[1];
                for (int i=1; i<args.length; i++)
                {
                    args[i] = oldargs[i+1];
                }
            }
        }

        // If the first word contains letters, but ends with a number
        // then this must be something like "Gen1" to split them up
        // to get "Gen 1"
        if (args.length > 0
            && Character.isDigit(args[0].charAt(args[0].length()-1))
            && PassageUtil.containsLetter(args[0]))
        {
            // This might make the code quicker (less array subscripting)
            // It certainly makes for more readable code.
            String word = args[0];

            // The caveat here is that - We should not split if the bit
            // before the number is one of the numeric book identifiers,
            // in that case #2 means Exo and not the book of # chapter 2
            boolean is_numeric_book = false;
            for (int i=0; i<VERSE_NUMERIC_BOOK.length && is_numeric_book==false; i++)
            {
                // so if we start with a book number id mark
                if (word.startsWith(VERSE_NUMERIC_BOOK[i]))
                    is_numeric_book = true;
            }

            if (!is_numeric_book)
            {
                boolean found_letters = false;
                int i = 0;

                // Find the split
                for (i=0; i<word.length(); i++)
                {
                    if (!found_letters)
                    {
                        if (Character.isLetter(word.charAt(i))) found_letters = true;
                    }
                    else
                    {
                        if (!Character.isLetter(word.charAt(i))) break;
                    }
                }

                String[] oldargs = args;
                args = new String[oldargs.length + 1];

                args[0] = oldargs[0].substring(0, i);
                args[1] = oldargs[0].substring(i);
                for (int j=2; j<args.length; j++)
                {
                    args[j] = oldargs[j-1];
                }
            }
        }

        // The last 2 sections join and split up parts of the array, should
        // I combine them to make for less array manipulation?
        // This would only speed things up in the rare case where someone
        // enters "2 Tim3:16" or something. The above method will still work
        // it will just be slower, and it is a heck of a lot easier to
        // understand and debug. Optimize when you need to not before.

        return args;
    }

    /**
     * This is simply a convenience function to wrap Integer.parseInt()
     * and give us a reasonable exception on failure. It is called by
     * VerseRange hence protected, however I would prefer private
     * @param text The string to be parsed
     * @return The correctly parsed chapter or verse
     * @exception NoSuchVerseException If the reference is illegal
     */
    protected static int parseInt(String text) throws NoSuchVerseException
    {
        try
        {
            return Integer.parseInt(text);
        }
        catch (NumberFormatException ex)
        {
            throw new NoSuchVerseException("passg_verse_parse", new Object[] { text });
        }
    }

    /**
     * Mutate into this reference and fix the reference if needed.
     * This nust only be called from a ctor to maintain immutability
     * @param book The book to set (Genesis = 1)
     * @param chapter The chapter to set
     * @param verse The verse to set
     */
    private final void setAndPatch(int book, int chapter, int verse)
    {
        int[] ref = { book, chapter, verse };

        Books.patch(ref);

        this.book = ref[BOOK];
        this.chapter = ref[CHAPTER];
        this.verse = ref[VERSE];
    }

    /**
     * Mutate into this reference and fix the reference if needed.
     * This must only be called from a ctor to maintain immutability
     * @param ref An array of the book, chapter and verse to set
     */
    private final void setAndPatch(int[] ref)
    {
        setAndPatch(ref[BOOK], ref[CHAPTER], ref[VERSE]);
    }

    /**
     * Verify and set the references.
     * This must only be called from a ctor to maintain immutability
     * @param book_str The book to set in String form (Genesis = 1)
     * @param chapter The chapter to set
     * @param verse The verse to set
     * @exception NoSuchVerseException If the verse can not be understood
     */
    private final void set(String book_str, int chapter, int verse) throws NoSuchVerseException
    {
        int book = Books.getBookNumber(book_str);

        set(book, chapter, verse);
    }

    /**
     * Verify and set the references.
     * This must only be called from a ctor to maintain immutability
     * @param book_str The book to set in String form (Genesis = 1)
     * @param chapter_str The chapter to set in String form
     * @param verse The verse to set
     * @exception NoSuchVerseException If the verse can not be understood
     */
    private final void set(String book_str, String chapter_str, int verse) throws NoSuchVerseException
    {
        int book = Books.getBookNumber(book_str);

        int chapter = 0;
        if (isEndMarker(chapter_str))   chapter = Books.chaptersInBook(book);
        else                            chapter = parseInt(chapter_str);

        set(book, chapter, verse);
    }

    /**
     * Verify and set the references.
     * This must only be called from a ctor to maintain immutability
     * @param book_str The book to set in String form (Genesis = 1)
     * @param chapter_str The chapter to set in String form
     * @param verse_str The verse to set in String form
     * @exception NoSuchVerseException If the verse can not be understood
     */
    private final void set(String book_str, String chapter_str, String verse_str) throws NoSuchVerseException
    {
        int book = Books.getBookNumber(book_str);

        int chapter = 0;
        if (isEndMarker(chapter_str))   chapter = Books.chaptersInBook(book);
        else                            chapter = parseInt(chapter_str);

        int verse = 0;
        if (isEndMarker(verse_str))     verse = Books.versesInChapter(book, chapter);
        else                            verse = parseInt(verse_str);

        set(book, chapter, verse);
    }

    /**
     * Verify and set the references.
     * This must only be called from a ctor to maintain immutability
     * @param book_str The book to set in String form (Genesis = 1)
     * @param chapter The chapter to set
     * @param verse_str The verse to set in String form
     * @exception NoSuchVerseException If the verse can not be understood
     */
    private final void set(String book_str, int chapter, String verse_str) throws NoSuchVerseException
    {
        int book = Books.getBookNumber(book_str);

        int verse = 0;
        if (isEndMarker(verse_str))     verse = Books.versesInChapter(book, chapter);
        else                            verse = parseInt(verse_str);

        set(book, chapter, verse);
    }

    /**
     * Verify and set the references.
     * This must only be called from a ctor to maintain immutability
     * @param book The book to set (Genesis = 1)
     * @param chapter_str The chapter to set in String form
     * @param verse_str The verse to set in String form
     * @exception NoSuchVerseException If the verse can not be understood
     */
    private final void set(int book, String chapter_str, String verse_str) throws NoSuchVerseException
    {
        int chapter = 0;
        if (isEndMarker(chapter_str))   chapter = Books.chaptersInBook(book);
        else                            chapter = parseInt(chapter_str);

        int verse = 0;
        if (isEndMarker(verse_str))     verse = Books.versesInChapter(book, chapter);
        else                            verse = parseInt(verse_str);

        set(book, chapter, verse);
    }

    /**
     * Verify and set the references.
     * This must only be called from a ctor to maintain immutability
     * @param book The book to set (Genesis = 1)
     * @param chapter The chapter to set
     * @param verse_str The verse to set in String form
     * @exception NoSuchVerseException If the verse can not be understood
     */
    private final void set(int book, int chapter, String verse_str) throws NoSuchVerseException
    {
        int verse = 0;
        if (isEndMarker(verse_str))     verse = Books.versesInChapter(book, chapter);
        else                            verse = parseInt(verse_str);

        set(book, chapter, verse);
    }

    /**
     * Verify and set the references.
     * This must only be called from a ctor to maintain immutability
     * @param book The book to set (Genesis = 1)
     * @param chapter The chapter to set
     * @param verse The verse to set
     * @exception NoSuchVerseException If the verse can not be understood
     */
    private final void set(int book, int chapter, int verse) throws NoSuchVerseException
    {
        Books.validate(book, chapter, verse);

        this.book = book;
        this.chapter = chapter;
        this.verse = verse;
    }

    /**
     * Set the references.
     * This must only be called from a ctor to maintain immutability
     * @param ref An array of the book, chapter and verse to set
     * @exception NoSuchVerseException If the verse can not be understood
     */
    private final void set(int[] ref) throws NoSuchVerseException
    {
        Books.validate(ref[BOOK], ref[CHAPTER], ref[VERSE]);

        book = ref[BOOK];
        chapter = ref[CHAPTER];
        verse = ref[VERSE];
    }

    /**
     * Set the references.
     * This must only be called from a ctor to maintain immutability
     * @param ordinal The ordinal of the verse
     * @exception NoSuchVerseException If the verse can not be understood
     */
    private final void set(int ordinal) throws NoSuchVerseException
    {
        int[] ref = Books.decodeOrdinal(ordinal);

        book = ref[BOOK];
        chapter = ref[CHAPTER];
        verse = ref[VERSE];
    }

    /**
     * Write out the object to the given ObjectOutputStream
     * @param out The stream to write our state to
     * @throws IOException if the read fails
     * @serialData Write the ordinal number of this verse
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
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
     * @param in The stream to read our state from
     * @throws IOException if the read fails
     * @throws ClassNotFoundException If the read data is incorrect
     * @serialData Write the ordinal number of this verse
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        // Call even if there is no default serializable fields.
        in.defaultReadObject();

        try
        {
            set(in.readInt());
        }
        catch (NoSuchVerseException ex)
        {
            throw new IOException(ex.getMessage());
        }

        // We are ignoring the original_name. It was set to null in the
        // default ctor so I will ignore it here.
    }

    /**
     * To make serialization work across new versions
     */
    static final long serialVersionUID = -4033921076023185171L;

    /**
     * To make the code more readible, the book is the first part of a int[]
     */
    private static final int BOOK = 0;

    /**
     * To make the code more readible, the chapter is the second part of a int[]
     */
    private static final int CHAPTER = 1;

    /**
     * To make the code more readible, the verse is the third part of a int[]
     */
    private static final int VERSE = 2;

    /**
     * The default verse
     * @label default verse
     */
    protected static final Verse DEFAULT = new Verse(1, 1, 1, true);

    /**
     * The book number. Genesis=1
     */
    private transient int book;

    /**
     * The chapter number
     */
    private transient int chapter;

    /**
     * The verse number
     */
    private transient int verse;

    /**
     * The ordinal number. Cache only.
     */
    // private transient int ord = -1;

    /**
     * The original string for picky users
     */
    private transient String original_name;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger("bible.passage");
}
