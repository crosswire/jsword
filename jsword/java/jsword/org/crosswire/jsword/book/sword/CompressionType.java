package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.Serializable;

import org.crosswire.jsword.book.BookException;

/**
 * Data about Compression types.
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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public abstract class CompressionType implements Serializable
{
    /**
     * The level of compression is the Book
     */
    public static final CompressionType COMPRESSION_ZIP = new CompressionType("ZIP") //$NON-NLS-1$
    {
        public boolean isSupported()
        {
            return true;
        }
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            BlockType blockType = BlockType.fromString(sbmd.getProperty(ConfigEntryType.BLOCK_TYPE));
            return new GZIPBackend(sbmd, rootPath, blockType);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3977014063492642096L;
    };

    /**
     * The level of compression is the Book
     */
    public static final CompressionType COMPRESSION_LZSS = new CompressionType("LZSS") //$NON-NLS-1$
    {
        public boolean isSupported()
        {
            return false;
        }
        protected AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException
        {
            return new LZSSBackend(sbmd, rootPath);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257847692691517494L;
    };

    /**
     * Simple ctor
     */
    public CompressionType(String name)
    {
        this.name = name;
    }

    /**
     * Returns whether this compression is implemented at this time.
     *
     * @return true if it is supported.
     */
    abstract boolean isSupported();

    abstract AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException;

    /**
     * Lookup method to convert from a String
     */
    public static CompressionType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            CompressionType obj = VALUES[i];
            if (obj.name.equalsIgnoreCase(name))
            {
                return obj;
            }
        }

        throw new ClassCastException(Msg.UNDEFINED_DATATYPE.toString(name));
    }

    /**
     * Lookup method to convert from an integer
     */
    public static CompressionType fromInteger(int i)
    {
        return VALUES[i];
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

    /**
     * The name of the CompressionType
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final CompressionType[] VALUES =
    {
        COMPRESSION_ZIP,
        COMPRESSION_LZSS
    };
}
