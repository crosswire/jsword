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
package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.KeyType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Data about book types.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public abstract class BookType {
    /**
     * Uncompressed Bibles
     */
    static BookType rawText = new BookType("RawText", BookCategory.BIBLE, KeyType.VERSE) {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new RawBackend(sbmd, 2);
        }
    };

    /**
     * Compressed Bibles
     */
    static BookType zText = new BookType("zText", BookCategory.BIBLE, KeyType.VERSE) {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            BlockType blockType = BlockType.fromString(sbmd.getProperty(SwordBookMetaData.KEY_BLOCK_TYPE));
            return new ZVerseBackend(sbmd, blockType, 2);
        }
    };

    /**
     * Compressed Bibles
     */
    static BookType zText4 = new BookType("zText4", BookCategory.BIBLE, KeyType.VERSE) {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            BlockType blockType = BlockType.fromString(sbmd.getProperty(SwordBookMetaData.KEY_BLOCK_TYPE));
            return new ZVerseBackend(sbmd, blockType, 4);
        }
    };

    /**
     * Uncompressed Commentaries
     */
    static BookType rawCom = new BookType("RawCom", BookCategory.COMMENTARY, KeyType.VERSE) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new RawBackend(sbmd, 2);
        }
    };

    static BookType rawCom4 = new BookType("RawCom4", BookCategory.COMMENTARY, KeyType.VERSE) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new RawBackend(sbmd, 4);
        }
    };

    /**
     * Compressed Commentaries
     */
    static BookType zCom = new BookType("zCom", BookCategory.COMMENTARY, KeyType.VERSE) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            BlockType blockType = BlockType.fromString(sbmd.getProperty(SwordBookMetaData.KEY_BLOCK_TYPE));
            return new ZVerseBackend(sbmd, blockType, 2);
        }
    };

    /**
     * Compressed Commentaries
     */
    static BookType zCom4 = new BookType("zCom4", BookCategory.COMMENTARY, KeyType.VERSE) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            BlockType blockType = BlockType.fromString(sbmd.getProperty(SwordBookMetaData.KEY_BLOCK_TYPE));
            return new ZVerseBackend(sbmd, blockType, 4);
        }
    };

    /**
     * Uncompresses HREF Commentaries
     */
    static BookType hrefCom = new BookType("HREFCom", BookCategory.COMMENTARY, KeyType.VERSE) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new RawBackend(sbmd, 2);
        }
    };

    /**
     * Uncompressed Commentaries
     */
    static BookType rawFiles = new BookType("RawFiles", BookCategory.COMMENTARY, KeyType.VERSE) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new RawFileBackend(sbmd, 2);
        }
    };

    /**
     * 2-Byte Index Uncompressed Dictionaries
     */
    static BookType rawLd = new BookType("RawLD", BookCategory.DICTIONARY, KeyType.LIST) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS)) {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new RawLDBackend(sbmd, 2);
        }
    };

    /**
     * 4-Byte Index Uncompressed Dictionaries
     */
    static BookType rawLd4 = new BookType("RawLD4", BookCategory.DICTIONARY, KeyType.LIST) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS)) {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new RawLDBackend(sbmd, 4);
        }
    };

    /**
     * Compressed Dictionaries
     */
    static BookType zLd = new BookType("zLD", BookCategory.DICTIONARY, KeyType.LIST) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS)) {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new ZLDBackend(sbmd);
        }
    };

    /**
     * Generic Books
     */
    static BookType rawGenBook = new BookType("RawGenBook", BookCategory.GENERAL_BOOK, KeyType.TREE) {

        @Override
        protected Book getBook(SwordBookMetaData sbmd, Backend backend) {
            return new SwordGenBook(sbmd, backend);
        }

        @Override
        protected Backend getBackend(SwordBookMetaData sbmd) throws BookException {
            return new GenBookBackend(sbmd);
        }
    };

    private static final ArrayList<BookType> supportedBookTypes = new ArrayList<>(Arrays.asList(
        rawText, zText, zText4, rawCom, rawCom4, zCom, zCom4, hrefCom, rawFiles, rawLd,
        rawLd4, zLd, rawGenBook)
    );

    public static void addSupportedBookType(BookType b) {
        supportedBookTypes.add(b);
    }

    /**
     * Simple ctor
     */
    public BookType(String name, BookCategory category, KeyType type) {
        this.name = name;
        this.category = category;
        this.keyType = type;
    }

    /**
     * Find a BookType from a name.
     * 
     * @param name
     *            The name of the BookType to look up
     * @return The found BookType or null if the name is not found
     */
    public static BookType getBookType(String name) {
        for (BookType v : supportedBookTypes) {
            if (v.name.equalsIgnoreCase(name)) {
                return v;
            }
        }

        throw new IllegalArgumentException(JSOtherMsg.lookupText("BookType {0} is not defined!", name));
    }

    /**
     * The category of this book
     * 
     * @return the category of this book
     */
    public BookCategory getBookCategory() {
        return category;
    }

    /**
     * Get the way this type of Book organizes it's keys.
     * 
     * @return the organization of keys for this book
     */
    public KeyType getKeyType() {
        return keyType;
    }

    /**
     * Given a SwordBookMetaData determine whether this BookType will work for
     * it.
     * 
     * @param sbmd
     *            the BookMetaData that this BookType works upon
     * @return true if this is a usable BookType
     */
    public boolean isSupported(SwordBookMetaData sbmd) {
        return category != null && sbmd != null;
    }

    /**
     * Create a Book appropriate for the BookMetaData
     * 
     * @param sbmd the book metadata 
     * @return a book for that metadata
     * @throws BookException
     */
    public Book createBook(SwordBookMetaData sbmd) throws BookException {
        return getBook(sbmd, getBackend(sbmd));
    }

    /**
     * Create a Book with the given backend
     */
    protected abstract Book getBook(SwordBookMetaData sbmd, Backend backend);

    /**
     * Create a the appropriate backend for this type of book
     */
    protected abstract Backend getBackend(SwordBookMetaData sbmd) throws BookException;

    /**
     * The name of the BookType
     */
    private String name;

    /**
     * What category is this book
     */
    private BookCategory category;

    /**
     * What category is this book
     */
    private KeyType keyType;

    /**
     * Lookup method to convert from a String
     * 
     * @param name the string representation of a book type
     * @return the matching book type
     */
    public static BookType fromString(String name) {
        for (BookType v : supportedBookTypes) {
            if (v.name.equalsIgnoreCase(name)) {
                return v;
            }
        }

        throw new ClassCastException(JSOtherMsg.lookupText("DataType {0} is not defined!", name));
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}
