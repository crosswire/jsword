/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.book.sword;

import java.io.Serializable;

/**
 * Data about Block types.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class BlockType implements Serializable
{
    /**
     * The level of compression is the Book
     */
    public static final BlockType BLOCK_BOOK = new BlockType("BOOK") //$NON-NLS-1$
    {
        public char getIndicator()
        {
            return 'b';
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257569486067807287L;
    };

    /**
     * The level of compression is the Book
     */
    public static final BlockType BLOCK_CHAPTER = new BlockType("CHAPTER") //$NON-NLS-1$
    {
        public char getIndicator()
        {
            return 'c';
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3762533416838968372L;
    };

    /**
     * The level of compression is the Book
     */
    public static final BlockType BLOCK_VERSE = new BlockType("VERSE") //$NON-NLS-1$
    {
        public char getIndicator()
        {
            return 'v';
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257572793192362551L;
    };

    /**
     * Simple ctor
     */
    public BlockType(String name)
    {
        this.name = name;
    }

    /**
     * Return a character indicating the kind of blocking.
     * This is used in the names of compressed sword modules.
     *
     * @return the indicator
     */
    abstract char getIndicator();

    /**
     * Lookup method to convert from a String
     */
    public static BlockType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            BlockType obj = VALUES[i];
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
    public static BlockType fromInteger(int i)
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
     * The name of the BlockType
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final BlockType[] VALUES =
    {
        BLOCK_BOOK,
        BLOCK_CHAPTER,
        BLOCK_VERSE
    };
}
