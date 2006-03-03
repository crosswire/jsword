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

import java.io.File;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;

/**
 * Data about book types.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum BookType
{
    /** Uncompressed Bibles */
    RAW_TEXT ("RawText", BookCategory.BIBLE) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return new RawBackend(sbmd, rootPath);
        }
    },

    /** Compressed Bibles */
    Z_TEXT ("zText", BookCategory.BIBLE) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return getCompressedBackend(sbmd, rootPath);
        }

        @Override
        protected boolean isBackendSupported(SwordBookMetaData sbmd)
        {
            return isCompressedBackendSupported(sbmd);
        }
    },

    /** Uncompressed Commentaries */
    RAW_COM ("RawCom", BookCategory.COMMENTARY) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return new RawBackend(sbmd, rootPath);
        }
    },

    /** Compressed Commentaries */
    Z_COM ("zCom", BookCategory.COMMENTARY) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return getCompressedBackend(sbmd, rootPath);
        }

        @Override
        protected boolean isBackendSupported(SwordBookMetaData sbmd)
        {
            return isCompressedBackendSupported(sbmd);
        }
    },

    /** Uncompresses HREF Commentaries */
    HREF_COM ("HREFCom", BookCategory.COMMENTARY) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return new RawBackend(sbmd, rootPath);
        }
    },

    /** Uncompressed Commentaries */
    RAW_FILES ("RawFiles", BookCategory.COMMENTARY) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return new RawBackend(sbmd, rootPath);
        }
    },

    /** 2-Byte Index Uncompressed Dictionaries */
    RAW_LD ("RawLD", BookCategory.DICTIONARY) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS))
            {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return new RawLDBackend(sbmd, rootPath, 2);
        }
    },

    /** 4-Byte Index Uncompressed Dictionaries */
    RAW_LD4 ("RawLD4", BookCategory.DICTIONARY) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS))
            {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return new RawLDBackend(sbmd, rootPath, 4);
        }
    },

    /** Compressed Dictionaries */
    Z_LD ("zLD", BookCategory.DICTIONARY) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            if (sbmd.getBookCategory().equals(BookCategory.DAILY_DEVOTIONS))
            {
                return new SwordDailyDevotion(sbmd, backend);
            }
            return new SwordDictionary(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return new ZLDBackend(sbmd, rootPath);
        }

        @Override
        protected boolean isBackendSupported(SwordBookMetaData sbmd)
        {
            return false;
        }
    },

    /** Generic Books */
    RAW_GEN_BOOK ("RawGenBook", BookCategory.OTHER) //$NON-NLS-1$
    {
        @Override
        protected Book getBook(SwordBookMetaData sbmd, AbstractBackend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        @Override
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return new GenBookBackend(sbmd, rootPath);
        }

        @Override
        protected boolean isBackendSupported(SwordBookMetaData sbmd)
        {
            return false;
        }
    };

    /**
     * Simple ctor
     */
    private BookType(String name, BookCategory type)
    {
        this.name = name;
        this.type = type;
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
     * @return true if this is a useable BookType
     */
    public boolean isSupported(SwordBookMetaData sbmd)
    {
        return type != null && isBackendSupported(sbmd);
    }

    /**
     * By default the backend is supported if the BookMetaData is not null.
     * @return true if this is a useable BackEnd
     */
    protected boolean isBackendSupported(SwordBookMetaData sbmd)
    {
        return sbmd != null;
    }

    /**
     * Create a Book appropriate for the BookMetaData
     * @throws BookException
     */
    public Book createBook(SwordBookMetaData sbmd, File progdir) throws BookException
    {
        AbstractBackend backend = getBackend(sbmd, progdir);
        Book book = getBook(sbmd, backend);
        return book;
    }

    /**
     * Create a Book with the given backend
     */
    protected abstract Book getBook(SwordBookMetaData sbmd, AbstractBackend backend);

    /**
     * Create a the appropriate backend for this type of book
     */
    protected abstract AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException;

    /**
     * 
     */
    protected static AbstractBackend getCompressedBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
    {
        String cStr = sbmd.getProperty(ConfigEntryType.COMPRESS_TYPE);
        if (cStr != null)
        {
            return Enum.valueOf(CompressionType.class, cStr).getBackend(sbmd, rootPath);
        }
        throw new BookException(Msg.COMPRESSION_UNSUPPORTED, new Object[] { cStr });
    }

    /**
     * Determine whether the SwordBookMetaData supports compression
     */
    protected static boolean isCompressedBackendSupported(SwordBookMetaData sbmd)
    {
        String cStr = sbmd.getProperty(ConfigEntryType.COMPRESS_TYPE);
        if (cStr != null)
        {
            return Enum.valueOf(CompressionType.class, cStr).isSupported();
        }
        return false;
    }

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
        for (BookType t : BookType.values())
        {
            if (t.name.equalsIgnoreCase(name))
            {
                return t;
            }
        }

        throw new ClassCastException(Msg.UNDEFINED_DATATYPE.toString(name));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return name;
    }
}
