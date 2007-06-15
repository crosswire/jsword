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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.compress;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

/**
 * An Enumeration of the possible Edits.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class CompressorType implements Serializable
{
    /**
     * Delete a sequence.
     */
    public static final CompressorType ZIP = new CompressorType("ZIP") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.compress.CompressorType#getCompressor(byte[])
         */
        public Compressor getCompressor(byte[] input)
        {
            return new Zip(new ByteArrayInputStream(input));
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 7380833510438155782L;
    };

    /**
     * Insert a sequence
     */
    public static final CompressorType LZSS = new CompressorType("LZSS") //$NON-NLS-1$
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.compress.CompressorType#getCompressor(byte[])
         */
        public Compressor getCompressor(byte[] input)
        {
            return new LZSS(new ByteArrayInputStream(input));
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -5794644645111043930L;
    };

    /**
     * @param name The name of the CompressorType
     */
    protected CompressorType(String name)
    {
        this.name = name;
    }

    /**
     * Get a compressor.
     */
    public abstract Compressor getCompressor(byte[] input);

    /**
     * Lookup method to convert from a String
     */
    public static CompressorType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            CompressorType o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static CompressorType fromInteger(int i)
    {
        return VALUES[i];
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    /**
     * The name of the EditType
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final CompressorType[] VALUES =
    {
        ZIP,
        LZSS,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727260177708345L;
}
