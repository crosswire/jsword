package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.Serializable;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookType;

/**
 * Data about module types.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public abstract class ModuleType implements Serializable
{
    /**
     * Uncompressed Bibles
     */
    public static final ModuleType RAW_TEXT = new ModuleType("RawText", "texts/rawtext", BookType.BIBLE)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }
    };

    /**
     * Compressed Bibles
     */
    public static final ModuleType Z_TEXT = new ModuleType("zText", "texts/ztext", BookType.BIBLE)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return getCompressedBackend(sbmd, path);
        }
    };

    /**
     * Uncompressed Commentaries
     */
    public static final ModuleType RAW_COM = new ModuleType("RawCom", "comments/rawcom", BookType.COMMENTARY)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }
    };

    /**
     * Compressed Commentaries
     */
    public static final ModuleType Z_COM = new ModuleType("zCom", "comments/zcom", BookType.COMMENTARY)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return getCompressedBackend(sbmd, path);
        }
    };

    /**
     * Uncompresses HREF Commentaries
     */
    public static final ModuleType HREF_COM = new ModuleType("HREFCom", "comments/hrefcom", BookType.COMMENTARY)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }
    };

    /**
     * Uncompressed Commentaries
     */
    public static final ModuleType RAW_FILES = new ModuleType("RawFiles", "comments/rawfiles", BookType.COMMENTARY)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }
    };

    /**
     * 2-Byte Index Uncompressed Dictionaries
     */
    public static final ModuleType RAW_LD = new ModuleType("RawLD", "lexdict/rawld", BookType.DICTIONARY)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordDictionary(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawLDBackend(sbmd, path, 2);
        }
    };

    /**
     * 4-Byte Index Uncompressed Dictionaries
     */
    public static final ModuleType RAW_LD4 = new ModuleType("RawLD4", "lexdict/rawld4", BookType.DICTIONARY)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordDictionary(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawLDBackend(sbmd, path, 4);
        }
    };

    /**
     * Compressed Dictionaries
     */
    public static final ModuleType Z_LD = new ModuleType("zLD", "lexdict/zld", BookType.DICTIONARY)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordDictionary(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new ZLDBackend(sbmd);
        }
    };

    /**
     * Generic Books
     */
    public static final ModuleType RAW_GEN_BOOK = new ModuleType("RawGenBook", "genbook/rawgenbook", null)
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }
    };

    /**
     * Simple ctor
     */
    public ModuleType(String name, String install, BookType type)
    {
        this.name = name;
        this.install = install;
        this.type = type;
    }

    /**
     * Find a ModuleType from a name.
     * @param name The name of the ModuleType to look up
     * @return The found ModuleType or null if the name is not found
     */
    public static ModuleType getModuleType(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            ModuleType obj = VALUES[i];
            if (obj.name.equalsIgnoreCase(name))
            {
                return obj;
            }
        }

        throw new IllegalArgumentException("ModuleType " + name + " is not defined!");
    }

    /**
     * Strings for where the modules are installed.
     * @return Returns the install directory for this module type.
     */
    public String getInstallDirectory()
    {
        return install;
    }

    /**
     * The book type of this module
     */
    public BookType getBookType()
    {
        return type;
    }

    /**
     * Create a Book appropriate for the BookMetaData
     */
    public Book createBook(SwordBookMetaData sbmd, File progdir) throws BookException
    {
        String dataPath = sbmd.getFirstValue(ConfigEntry.DATA_PATH);
        File baseurl = new File(progdir, dataPath);
        String path = baseurl.getAbsolutePath();
        Backend backend = getBackend(sbmd, path);
        Book book = getBook(sbmd, backend);
        sbmd.setBook(book);
        return book;
    }

    /**
     * Create a Book with the given backend
     */
    protected abstract Book getBook(SwordBookMetaData sbmd, Backend backend);

    /**
     * Create a the appropriate backend for this type of book
     */
    protected abstract Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException;

    /**
     * 
     */
    protected static Backend getCompressedBackend(SwordBookMetaData sbmd, String path) throws BookException
    {
        switch (sbmd.matchingIndex(SwordConstants.COMPRESSION_STRINGS, ConfigEntry.COMPRESS_TYPE))
        {
        case SwordConstants.COMPRESSION_ZIP:
            // The default blocktype (when we used fields) was SwordConstants.BLOCK_CHAPTER (2);
            // but the specified default here is BLOCK_BOOK (0)
            int blocktype = sbmd.matchingIndex(SwordConstants.BLOCK_STRINGS, ConfigEntry.BLOCK_TYPE, SwordConstants.BLOCK_BOOK);
            return new GZIPBackend(path, blocktype);

        case SwordConstants.COMPRESSION_LZSS:
            return new LZSSBackend(sbmd);

        default:
            throw new BookException(Msg.COMPRESSION_UNSUPPORTED, new Object[]
            {
                sbmd.getFirstValue(ConfigEntry.COMPRESS_TYPE)
            });
        }
    }

    /**
     * The name of the ModuleType
     */
    private String name;

    /**
     * Where are modules of this type installed relative to the sword module
     * directory?
     */
    private String install;

    /**
     * What booktype is this module
     */
    private BookType type;

    /**
     * Lookup method to convert from a String
     */
    public static ModuleType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            ModuleType obj = VALUES[i];
            if (obj.name.equalsIgnoreCase(name))
            {
                return obj;
            }
        }

        throw new ClassCastException("DataType " + name + " is not defined!");
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object obj)
    {
        return super.equals(obj);
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
    static final long serialVersionUID = 1417463751329673026L;
    private static int nextObj_ = 0;
    private final int obj_ = nextObj_++;

    Object readResolve()
    {
        return VALUES[obj_];
    }

    private static final ModuleType[] VALUES =
    {
        RAW_TEXT,
        Z_TEXT,
        RAW_COM,
        Z_COM,
        RAW_COM,
        HREF_COM,
        RAW_FILES,
        RAW_LD,
        RAW_LD4,
        Z_LD,
        RAW_GEN_BOOK,
    };
}
