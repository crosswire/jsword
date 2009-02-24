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
package org.crosswire.jsword.book.sword;

import java.io.Serializable;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.KeyType;

/**
 * Data about book types.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class BookType implements Serializable
{
    /**
     * Uncompressed Bibles
     */
    public static final BookType RAW_TEXT = new BookType("RawText", BookCategory.BIBLE) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            return new RawBackend(sbmd, 2);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.VERSE;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3544385920414529336L;
    };

    /**
     * Compressed Bibles
     */
    public static final BookType Z_TEXT = new BookType("zText", BookCategory.BIBLE) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            BlockType blockType = BlockType.fromString((String) sbmd.getProperty(ConfigEntryType.BLOCK_TYPE));
            return new ZVerseBackend(sbmd, blockType);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.VERSE;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257846571620906039L;
    };

    /**
     * Uncompressed Commentaries
     */
    public static final BookType RAW_COM = new BookType("RawCom", BookCategory.COMMENTARY) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            return new RawBackend(sbmd, 2);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.VERSE;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258129141898294837L;
    };

    public static final BookType RAW_COM4 = new BookType("RawCom4", BookCategory.COMMENTARY) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            return new RawBackend(sbmd, 4);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.VERSE;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258129141898294838L;
    };
    /**
     * Compressed Commentaries
     */
    public static final BookType Z_COM = new BookType("zCom", BookCategory.COMMENTARY) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            BlockType blockType = BlockType.fromString((String) sbmd.getProperty(ConfigEntryType.BLOCK_TYPE));
            return new ZVerseBackend(sbmd, blockType);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.VERSE;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257569516166002487L;
    };

    /**
     * Uncompresses HREF Commentaries
     */
    public static final BookType HREF_COM = new BookType("HREFCom", BookCategory.COMMENTARY) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            return new RawBackend(sbmd, 2);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.VERSE;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256439209706338354L;
    };

    /**
     * Uncompressed Commentaries
     */
    public static final BookType RAW_FILES = new BookType("RawFiles", BookCategory.COMMENTARY) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            return new RawBackend(sbmd, 2);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.VERSE;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256446901875325236L;
    };

    /**
     * 2-Byte Index Uncompressed Dictionaries
     */
    public static final BookType RAW_LD = new BookType("RawLD", BookCategory.DICTIONARY) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS))
            {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            return new RawLDBackend(sbmd, 2);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.LIST;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257290240195442745L;
    };

    /**
     * 4-Byte Index Uncompressed Dictionaries
     */
    public static final BookType RAW_LD4 = new BookType("RawLD4", BookCategory.DICTIONARY) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS))
            {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            return new RawLDBackend(sbmd, 4);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.LIST;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3689067356945331762L;
    };

    /**
     * Compressed Dictionaries
     */
    public static final BookType Z_LD = new BookType("zLD", BookCategory.DICTIONARY) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS))
            {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            return new ZLDBackend(sbmd);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.LIST;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3691037673259414067L;
    };

    /**
     * Generic Books
     */
    public static final BookType RAW_GEN_BOOK = new BookType("RawGenBook", BookCategory.GENERAL_BOOK) //$NON-NLS-1$
    {
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordGenBook(sbmd, backend);
        }

        protected AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException
        {
            return new GenBookBackend(sbmd);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.BookType#getKeyType()
         */
        public KeyType getKeyType()
        {
            return KeyType.TREE;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257290218703892528L;
    };

    /**
     * Simple ctor
     */
    public BookType(String name, BookCategory type)
    {
        this.name = name;
        this.type = type;
    }

    /**
     * Find a BookType from a name.
     * @param name The name of the BookType to look up
     * @return The found BookType or null if the name is not found
     */
    public static BookType getBookType(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            BookType mod = VALUES[i];
            if (mod.name.equalsIgnoreCase(name))
            {
                return mod;
            }
        }

        throw new IllegalArgumentException(Msg.UNDEFINED_BOOK_TYPE.toString(name));
    }

    /**
     * The category of this book
     */
    public BookCategory getBookCategory()
    {
        return type;
    }

    /**
     * Given a SwordBookMetaData determine whether this BookType
     * will work for it.
     * @param sbmd the BookMetaData that this BookType works upon
     * @return true if this is a usable BookType
     */
    public boolean isSupported(SwordBookMetaData sbmd)
    {
        return type != null && sbmd != null;
    }

    /**
     * Create a Book appropriate for the BookMetaData
     * @throws BookException
     */
    public Book createBook(SwordBookMetaData sbmd) throws BookException
    {
        return getBook(sbmd, getBackend(sbmd));
    }

    /**
     * Create a Book with the given backend
     */
    protected abstract Book getBook(SwordBookMetaData sbmd, AbstractBackend backend);

    /**
     * Create a the appropriate backend for this type of book
     */
    protected abstract AbstractBackend getBackend(SwordBookMetaData sbmd) throws BookException;

    /**
     * Get the way this type of Book organizes it's keys.
     * @return the organization of keys for this book
     */
    public abstract KeyType getKeyType();

    /**
     * The name of the BookType
     */
    private String name;

    /**
     * What category is this book
     */
    private BookCategory type;

    /**
     * Lookup method to convert from a String
     */
    public static BookType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            BookType mod = VALUES[i];
            if (mod.name.equalsIgnoreCase(name))
            {
                return mod;
            }
        }

        throw new ClassCastException(Msg.UNDEFINED_DATATYPE.toString(name));
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final BookType[] VALUES =
    {
        RAW_TEXT,
        Z_TEXT,
        RAW_COM,
        RAW_COM4,
        Z_COM,
        RAW_COM,
        HREF_COM,
        RAW_FILES,
        RAW_LD,
        RAW_LD4,
        Z_LD,
        RAW_GEN_BOOK,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 5597156322295331692L;
}
