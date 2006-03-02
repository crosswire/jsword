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
import java.io.Serializable;

import org.crosswire.jsword.book.BookException;

/**
 * Data about Compression types.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class CompressionType implements Serializable
{
    /**
     * The level of compression is the Book
     */
    public static final CompressionType COMPRESSION_ZIP = new CompressionType("ZIP") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.sword.CompressionType#isSupported()
         */
        @Override
        public boolean isSupported()
        {
            return true;
        }

        @Override
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
        @Override
        public boolean isSupported()
        {
            return false;
        }

        @Override
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
    @Override
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
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
        COMPRESSION_LZSS,
    };
}
