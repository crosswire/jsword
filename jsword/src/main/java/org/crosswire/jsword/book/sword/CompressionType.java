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

import org.crosswire.jsword.book.BookException;

/**
 * Data about Compression types.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum CompressionType
{
    /**
     * The level of compression is the Book
     */
    ZIP
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
            BlockType blockType = Enum.valueOf(BlockType.class, sbmd.getProperty(ConfigEntryType.BLOCK_TYPE));
            return new GZIPBackend(sbmd, rootPath, blockType);
        }
    },

    /**
     * The level of compression is the Book
     */
    LZSS
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
    };

    /**
     * Returns whether this compression is implemented at this time.
     *
     * @return true if it is supported.
     */
    abstract boolean isSupported();

    abstract AbstractBackend getBackend(SwordBookMetaData sbmd, File rootPath) throws BookException;

}
