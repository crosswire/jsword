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
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.common.compress;

import java.io.ByteArrayInputStream;

/**
 * An Enumeration of the possible Compressions.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public enum CompressorType {
    ZIP {
        @Override
        public Compressor getCompressor(byte[] input) {
            return new Zip(new ByteArrayInputStream(input));
        }
    },

    LZSS {
        @Override
        public Compressor getCompressor(byte[] input) {
            return new LZSS(new ByteArrayInputStream(input));
        }
    },

    BZIP2 {
        @Override
        public Compressor getCompressor(byte[] input) {
            return new BZip2(new ByteArrayInputStream(input));
        }
    },

    GZIP {
        @Override
        public Compressor getCompressor(byte[] input) {
            return new Gzip(new ByteArrayInputStream(input));
        }
    },

    XZ {
        @Override
        public Compressor getCompressor(byte[] input) {
            return new XZ(new ByteArrayInputStream(input));
        }
    };

    /**
     * Get a compressor.
     * 
     * @param input the stream to compress or to uncompress.
     * @return the compressor for the stream
     */
    public abstract Compressor getCompressor(byte[] input);

    /**
     * Get a CompressorType from a String
     * 
     * @param name the case insensitive representation of the desired CompressorType
     * @return the desired compressor or null if not found.
     */
    public static CompressorType fromString(String name) {
        for (CompressorType v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }
}
