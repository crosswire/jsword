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

import org.crosswire.common.activate.Lock;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;

/**
 * A backend to read LZSS compressed data files.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class LZSSBackend extends AbstractBackend
{
    /**
     * Simple ctor
     */
    public LZSSBackend(SwordBookMetaData sbmd, File rootPath)
    {
        super(sbmd, rootPath);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    @Override
    public String getRawText(Key key) throws BookException
    {
        // LATER(joe): implement this
        throw new BookException(Msg.COMPRESSION_UNSUPPORTED, new Object[] { getBookMetaData().getName() });
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#readIndex()
     */
    @Override
    public Key readIndex()
    {
        // PENDING(joe): refactor to get rid of this
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#isSupported()
     */
    @Override
    public boolean isSupported()
    {
        return false;
    }
}
