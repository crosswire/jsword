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

import org.crosswire.common.icu.NumberShaper;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;

/**
 * Types of Accuracy for verse references. For example:
 * <ul>
 * <li>Gen == BOOK_ONLY;</li>
 * <li>Gen 1 == BOOK_CHAPTER;</li>
 * <li>Gen 1:1 == BOOK_VERSE;</li>
 * <li>Jude 1 == BOOK_VERSE;</li>
 * <li>Jude 1:1 == BOOK_VERSE;</li>
 * <li>1:1 == CHAPTER_VERSE;</li>
 * <li>10 == BOOK_ONLY, CHAPTER_ONLY, or VERSE_ONLY</li>
 * </ul>
 * 
 * <p>
 * With the last one, you will note that there is a choice. By itself there is
 * not enough information to determine which one it is. There has to be a
 * context in which it is used.
 * </p><p>
 * It may be found in a verse range like: Gen 1:2 - 10. In this case the context
 * of 10 is Gen 1:2, which is BOOK_VERSE. So in this case, 10 is VERSE_ONLY.
 * </p><p>
 * If it is at the beginning of a range like 10 - 22:3, it has to have more
 * context. If the context is a prior entry like Gen 2:5, 10 - 22:3, then its
 * context is Gen 2:5, which is BOOK_VERSE and 10 is VERSE_ONLY.
 * </p><p>
 * However if it is Gen 2, 10 - 22:3 then the context is Gen 2, BOOK_CHAPTER so
 * 10 is understood as BOOK_CHAPTER.
 * </p><p>
 * As a special case, if the preceding range is an entire chapter or book then
 * 10 would understood as CHAPTER_ONLY or BOOK_ONLY (respectively)
 * </p><p>
 * If the number has no preceding context, then it is understood as being
 * BOOK_ONLY.
 * </p><p>
 * In all of these examples, the start verse was being interpreted. In the case
 * of a verse that is the end of a range, it is interpreted in the context of
 * the range's start.
 * </p>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public enum AccuracyType {
    /**
     * The verse was specified as book, chapter and verse. For example, Gen 1:1,
     * Jude 3 (which only has one chapter)
     */
    BOOK_VERSE {
        @Override
        public boolean isVerse() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            BibleBook book = v11n.getBook(parts[0]);
            int chapter = 1;
            int verse = 1;
            final String subIdentifier = getSubIdentifier(parts);
            final boolean hasSub = subIdentifier != null;

            //can be of form, BCV, BCV!sub, BV, BV!a
            //we only have a chapter and verse number if
            // a- BCV (3 parts) or b- BCV!sub (4 parts)
            // however, we have 3 parts if BV!a
            if (hasSub && parts.length == 4 || !hasSub && parts.length == 3) {
                chapter = getChapter(v11n, book, parts[1]);
                verse = getVerse(v11n, book, chapter, parts[2]);
            } else {
                // Some books only have 1 chapter
                verse = getVerse(v11n, book, chapter, parts[1]);
            }
            return new Verse(v11n, book, chapter, verse, subIdentifier);
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            // A fully specified verse is the same regardless of whether it is a
            // start or an end to a range.
            return createStartVerse(v11n, null, endParts);
        }
    },

    /**
     * The passage was specified to a book and chapter (no verse). For example,
     * Gen 1
     */
    BOOK_CHAPTER {
        @Override
        public boolean isChapter() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            BibleBook book = v11n.getBook(parts[0]);
            int chapter = getChapter(v11n, book, parts[1]);
            int verse = 0; // chapter > 0 ? 1 : 0; // 0 ?
            return new Verse(v11n, book, chapter, verse);
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            // Very similar to the start verse but we want the end of the chapter
            BibleBook book = v11n.getBook(endParts[0]);
            int chapter = getChapter(v11n, book, endParts[1]);
            int verse = v11n.getLastVerse(book, chapter);
            return new Verse(v11n, book, chapter, verse);
        }
    },

    /**
     * The passage was specified to a book only (no chapter or verse). For
     * example, Gen
     */
    BOOK_ONLY {
        @Override
        public boolean isBook() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            BibleBook book = v11n.getBook(parts[0]);
            int chapter = 0; // v11n.getLastChapter(book) > 0 ? 1 : 0; // 0 ?
            int verse = 0; // v11n.getLastVerse(book, chapter) > 0 ? 1 : 0; // 0 ?
            return new Verse(v11n, book, chapter, verse);
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            BibleBook book = v11n.getBook(endParts[0]);
            int chapter = v11n.getLastChapter(book);
            int verse = v11n.getLastVerse(book, chapter);
            return new Verse(v11n, book, chapter, verse);
        }
    },

    /**
     * The passage was specified to a chapter and verse (no book). For example,
     * 1:1
     */
    CHAPTER_VERSE {
        @Override
        public boolean isVerse() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            if (verseRangeBasis == null) {
                // TRANSLATOR: The user supplied a verse reference but did not give the book of the Bible.
                throw new NoSuchVerseException(JSMsg.gettext("Book is missing"));
            }
            BibleBook book = verseRangeBasis.getEnd().getBook();
            int chapter = getChapter(v11n, book, parts[0]);
            int verse = getVerse(v11n, book, chapter, parts[1]);

            return new Verse(v11n, book, chapter, verse, getSubIdentifier(parts));
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            // Very similar to the start verse but use the verse as a basis
            BibleBook book = verseBasis.getBook();
            int chapter = getChapter(v11n, book, endParts[0]);
            int verse = getVerse(v11n, book, chapter, endParts[1]);
            return new Verse(v11n, book, chapter, verse, getSubIdentifier(endParts));
        }
    },

    /**
     * There was only a chapter number
     */
    CHAPTER_ONLY {
        @Override
        public boolean isChapter() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            if (verseRangeBasis == null) {
                // TRANSLATOR: The user supplied a verse reference but did not give the book of the Bible.
                throw new NoSuchVerseException(JSMsg.gettext("Book is missing"));
            }
            BibleBook book = verseRangeBasis.getEnd().getBook();
            int chapter = getChapter(v11n, book, parts[0]);
            int verse = 0; // chapter > 0 ? 1 : 0; // 0 ?
            return new Verse(v11n, book, chapter, verse);
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            // Very similar to the start verse but use the verse as a basis
            // and it gets the end of the chapter
            BibleBook book = verseBasis.getBook();
            int chapter = getChapter(v11n, book, endParts[0]);
            return new Verse(v11n, book, chapter, v11n.getLastVerse(book, chapter));
        }
    },

    /**
     * There was only a verse number
     */
    VERSE_ONLY {
        @Override
        public boolean isVerse() {
            return true;
        }

        @Override
        public Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException {
            if (verseRangeBasis == null) {
                // TRANSLATOR: The user supplied a verse reference but did not give the book or chapter of the Bible.
                throw new NoSuchVerseException(JSMsg.gettext("Book and chapter are missing"));
            }
            BibleBook book = verseRangeBasis.getEnd().getBook();
            int chapter = verseRangeBasis.getEnd().getChapter();
            int verse = getVerse(v11n, book, chapter, parts[0]);
            return new Verse(v11n, book, chapter, verse, getSubIdentifier(parts));
        }

        @Override
        public Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException {
            // Very similar to the start verse but use the verse as a basis
            // and it gets the end of the chapter
            BibleBook book = verseBasis.getBook();
            int chapter = verseBasis.getChapter();
            int verse = getVerse(v11n, book, chapter, endParts[0]);
            return new Verse(v11n, book, chapter, verse, getSubIdentifier(endParts));
        }
    };

    /**
     * @param v11n
     *            the versification to which this reference pertains
     * @param verseRangeBasis
     *            the range that stood before the string reference
     * @param parts
     *            a tokenized version of the original
     * @return a <code>Verse</code> for the original
     * @throws NoSuchVerseException
     */
    public abstract Verse createStartVerse(Versification v11n, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException;

    /**
     * @param v11n
     *            the versification to which this reference pertains
     * @param verseBasis
     *            the verse at the beginning of the range
     * @param endParts
     *            a tokenized version of the original
     * @return a <code>Verse</code> for the original
     * @throws NoSuchVerseException
     */
    public abstract Verse createEndVerse(Versification v11n, Verse verseBasis, String[] endParts) throws NoSuchVerseException;

    /**
     * @return true if this AccuracyType specifies down to the book but not
     *         chapter or verse.
     */
    public boolean isBook() {
        return false;
    }

    /**
     * @return true if this AccuracyType specifies down to the chapter but not
     *         the verse.
     */
    public boolean isChapter() {
        return false;
    }

    /**
     * @return true if this AccuracyType specifies down to the verse.
     */
    public boolean isVerse() {
        return false;
    }

    /**
     * Interprets the chapter value, which is either a number or "ff" or "$"
     * (meaning "what follows")
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @param lbook
     *            the book
     * @param chapter
     *            a string representation of the chapter. May be "ff" or "$" for
     *            "what follows".
     * @return the number of the chapter
     * @throws NoSuchVerseException
     */
    public static final int getChapter(Versification v11n, BibleBook lbook, String chapter) throws NoSuchVerseException {
        if (isEndMarker(chapter)) {
            return v11n.getLastChapter(lbook);
        }
        return parseInt(chapter);
    }

    /**
     * Interprets the verse value, which is either a number or "ff" or "$"
     * (meaning "what follows")
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @param lbook
     *            the integer representation of the book
     * @param lchapter
     *            the integer representation of the chapter
     * @param verse
     *            the string representation of the verse
     * @return the number of the verse
     * @throws NoSuchVerseException
     */
    public static final int getVerse(Versification v11n, BibleBook lbook, int lchapter, String verse) throws NoSuchVerseException {
        if (isEndMarker(verse)) {
            return v11n.getLastVerse(lbook, lchapter);
        }
        return parseInt(verse);
    }

    /**
     * Get an integer representation for this AccuracyType
     * 
     * @return the ordinal representation
     */
    public int toInteger() {
        return ordinal();
    }

    /**
     * Determine how closely the string defines a verse.
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @param original
     * @param parts
     *            is a reference split into parts
     * @return what is the kind of accuracy of the string reference.
     * @throws NoSuchVerseException
     */
    public static AccuracyType fromText(Versification v11n, String original, String[] parts) throws NoSuchVerseException {
        return fromText(v11n, original, parts, null, null);
    }

    /**
     * @param v11n
     *            the versification to which this reference pertains
     * @param original
     * @param parts
     * @param verseAccuracy
     * @return the accuracy of the parts
     * @throws NoSuchVerseException
     */
    public static AccuracyType fromText(Versification v11n, String original, String[] parts, AccuracyType verseAccuracy) throws NoSuchVerseException {
        return fromText(v11n, original, parts, verseAccuracy, null);
    }

    /**
     * @param v11n
     *            the versification to which this reference pertains
     * @param original
     * @param parts
     * @param basis
     * @return the accuracy of the parts
     * @throws NoSuchVerseException
     */
    public static AccuracyType fromText(Versification v11n, String original, String[] parts, VerseRange basis) throws NoSuchVerseException {
        return fromText(v11n, original, parts, null, basis);
    }

    /**
     * Does this string exactly define a Verse. For example:
     * <ul>
     * <li>fromText("Gen") == AccuracyType.BOOK_ONLY;</li>
     * <li>fromText("Gen 1:1") == AccuracyType.BOOK_VERSE;</li>
     * <li>fromText("Gen 1") == AccuracyType.BOOK_CHAPTER;</li>
     * <li>fromText("Jude 1") == AccuracyType.BOOK_VERSE;</li>
     * <li>fromText("Jude 1:1") == AccuracyType.BOOK_VERSE;</li>
     * <li>fromText("1:1") == AccuracyType.CHAPTER_VERSE;</li>
     * <li>fromText("1") == AccuracyType.VERSE_ONLY;</li>
     * </ul>
     * 
     * @param v11n
     *            the versification to which this reference pertains
     * @param original 
     * @param parts
     * @param verseAccuracy
     * @param basis
     * @return the accuracy of the parts
     * @throws NoSuchVerseException
     */
    public static AccuracyType fromText(Versification v11n, String original, String[] parts, AccuracyType verseAccuracy, VerseRange basis) throws NoSuchVerseException {
        int partsLength = parts.length;
        String lastPart = parts[partsLength - 1];
        if (lastPart.length() > 0 && lastPart.charAt(0) == '!') {
            --partsLength;
        }
        switch (partsLength) {
        case 1:
            if (v11n.isBook(parts[0])) {
                return BOOK_ONLY;
            }

            // At this point we should have a number.
            // But double check it
            checkValidChapterOrVerse(parts[0]);

            // What it is depends upon what stands before it.
            if (verseAccuracy != null) {
                if (verseAccuracy.isVerse()) {
                    return VERSE_ONLY;
                }

                if (verseAccuracy.isChapter()) {
                    return CHAPTER_ONLY;
                }
            }

            if (basis != null) {
                if (basis.isWholeChapter()) {
                    return CHAPTER_ONLY;
                }
                return VERSE_ONLY;
            }

            throw buildVersePartsException(original, parts);

        case 2:
            // Does it start with a book?
            BibleBook pbook = v11n.getBook(parts[0]);
            if (pbook == null) {
                checkValidChapterOrVerse(parts[0]);
                checkValidChapterOrVerse(parts[1]);
                return CHAPTER_VERSE;
            }

            if (v11n.getLastChapter(pbook) == 1) {
                return BOOK_VERSE;
            }

            return BOOK_CHAPTER;

        case 3:
            if (v11n.getBook(parts[0]) != null) {
                checkValidChapterOrVerse(parts[1]);
                checkValidChapterOrVerse(parts[2]);
                return BOOK_VERSE;
            }

            throw buildVersePartsException(original, parts);

        default:
            throw buildVersePartsException(original, parts);
        }
    }

    private static NoSuchVerseException buildVersePartsException(String original, String[] parts) {
        StringBuilder buffer = new StringBuilder(original);
        for (int i = 0; i < parts.length; i++) {
            buffer.append(", ").append(parts[i]);
        }
        // TRANSLATOR: The user specified a verse with too many separators. {0} is a placeholder for the allowable separators.
        return new NoSuchVerseException(JSMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})", buffer.toString()));
    }

    /**
     * Is this text valid in a chapter/verse context
     * 
     * @param text
     *            The string to test for validity
     * @throws NoSuchVerseException
     *             If the text is invalid
     */
    private static void checkValidChapterOrVerse(String text) throws NoSuchVerseException {
        if (!isEndMarker(text)) {
            parseInt(text);
        }
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
    private static int parseInt(String text) throws NoSuchVerseException {
        try {
            return Integer.parseInt(new NumberShaper().unshape(text));
        } catch (NumberFormatException ex) {
            // TRANSLATOR: The chapter or verse number is actually not a number, but something else.
            // {0} is a placeholder for what the user supplied.
            throw new NoSuchVerseException(JSMsg.gettext("Cannot understand {0} as a chapter or verse.", text));
        }
    }

    /**
     * Is this string a legal marker for 'to the end of the chapter'
     * 
     * @param text
     *            The string to be checked
     * @return true if this is a legal marker
     */
    private static boolean isEndMarker(String text) {
        if (text.equals(AccuracyType.VERSE_END_MARK1)) {
            return true;
        }

        if (text.equals(AccuracyType.VERSE_END_MARK2)) {
            return true;
        }

        return false;
    }

    private static boolean hasSubIdentifier(String[] parts) {
        String subIdentifier = parts[parts.length - 1];
        return subIdentifier != null && subIdentifier.length() > 0 && subIdentifier.charAt(0) == '!';
    }

    protected static String getSubIdentifier(String[] parts) {
        String subIdentifier = null;
        if (hasSubIdentifier(parts)) {
            subIdentifier = parts[parts.length - 1].substring(1);
        }
        return subIdentifier;
    }

    /**
     * Take a string representation of a verse and parse it into an Array of
     * Strings where each part is likely to be a verse part. The goal is to
     * allow the greatest possible variations in user input.
     * <p>
     * Parts can be separated by pretty much anything. No distinction is made
     * between them. While chapter and verse need to be separated, a separator
     * is assumed between digits and non-digits. Adjacent words, (i.e. sequences
     * of non-digits) are understood to be a book reference. If a number runs up
     * against a book name, it is considered to be either part of the book name
     * (i.e. it is before it) or a chapter number (i.e. it stands after it.)
     * </p>
     * <p>
     * Note: ff and $ are considered to be digits.
     * </p>
     * <p>
     * Note: it is not necessary for this to be a BCV (book, chapter, verse), it
     * may just be BC, B, C, V or CV. No distinction is needed here for a number
     * that stands alone.
     * </p>
     * 
     * @param input
     *            The string to parse.
     * @return The string array
     * @throws NoSuchVerseException
     */
    public static String[] tokenize(String input) throws NoSuchVerseException {
        // The results are expected to be no more than 3 parts
        String[] args = {
                null, null, null, null, null, null, null, null
        };

        // Normalize the input by replacing non-digits and non-letters with
        // spaces.
        int length = input.length();
        // Create an output array big enough to add ' ' where needed
        char[] normalized = new char[length * 2];
        char lastChar = '0'; // start with a digit so normalized won't start
                             // with a space
        char curChar = ' '; // can be anything
        int tokenCount = 0;
        int normalizedLength = 0;
        int startIndex = 0;
        String token = null;
        boolean foundBoundary = false;
        boolean foundSubIdentifier = false;
        for (int i = 0; i < length; i++) {
            curChar = input.charAt(i);
            if (curChar == '!') {
                foundSubIdentifier = true;
                token = new String(normalized, startIndex, normalizedLength - startIndex);
                args[tokenCount++] = token;
                normalizedLength = 0;
            }
            if (foundSubIdentifier) {
                normalized[normalizedLength++] = curChar;
                continue;
            }
            boolean charIsDigit = curChar == '$' || Character.isDigit(curChar)
                    || (curChar == 'f' && (i + 1 < length ? input.charAt(i + 1) : ' ') == 'f' && !Character.isLetter(lastChar));
            if (charIsDigit || Character.isLetter(curChar)) {
                foundBoundary = true;
                boolean charWasDigit = lastChar == '$' || Character.isDigit(lastChar) || (lastChar == 'f' && (i > 2 ? input.charAt(i - 2) : '0') == 'f');
                if (charWasDigit || Character.isLetter(lastChar)) {
                    foundBoundary = false;
                    // Replace transitions between digits and letters with
                    // spaces.
                    if (normalizedLength > 0 && charWasDigit != charIsDigit) {
                        foundBoundary = true;
                    }
                }
                if (foundBoundary) {
                    // On a boundary:
                    // Digits always start a new token
                    // Letters always continue a previous token
                    if (charIsDigit) {
                        if (tokenCount >= args.length) {
                            // TRANSLATOR: The user specified a verse with too many separators. {0} is a placeholder for the allowable separators.
                            throw new NoSuchVerseException(JSMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})", input));
                        }

                        token = new String(normalized, startIndex, normalizedLength - startIndex);
                        args[tokenCount++] = token;
                        normalizedLength = 0;
                    } else {
                        normalized[normalizedLength++] = ' ';
                    }
                }
                normalized[normalizedLength++] = curChar;
            }

            // Until the first character is copied, there is no last char
            if (normalizedLength > 0) {
                lastChar = curChar;
            }
        }

        if (tokenCount >= args.length) {
            // TRANSLATOR: The user specified a verse with too many separators. {0} is a placeholder for the allowable separators.
            throw new NoSuchVerseException(JSMsg.gettext("Too many parts to the Verse. (Parts are separated by any of {0})", input));
        }

        token = new String(normalized, startIndex, normalizedLength - startIndex);
        args[tokenCount++] = token;

        String[] results = new String[tokenCount];
        System.arraycopy(args, 0, results, 0, tokenCount);
        return results;
    }

    /**
     * What characters can we use to separate parts to a verse
     */
    public static final String VERSE_ALLOWED_DELIMS = " :.";

    /**
     * Characters that are used to indicate end of verse/chapter, part 1
     */
    public static final String VERSE_END_MARK1 = "$";

    /**
     * Characters that are used to indicate end of verse/chapter, part 2
     */
    public static final String VERSE_END_MARK2 = "ff";
}
