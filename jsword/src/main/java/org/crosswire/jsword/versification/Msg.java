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
package org.crosswire.jsword.versification;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Msg extends MsgBase
{
    static final Msg BOOKS_BOOK = new Msg("BibleInfo.Book"); //$NON-NLS-1$
    static final Msg BOOKS_SECTION = new Msg("BibleInfo.Section"); //$NON-NLS-1$
    static final Msg BOOKS_NUMBER = new Msg("BibleInfo.Number"); //$NON-NLS-1$
    static final Msg BOOKS_FIND = new Msg("BibleInfo.Find"); //$NON-NLS-1$
    static final Msg BOOKS_BOOKCHAP = new Msg("BibleInfo.BookChap"); //$NON-NLS-1$
    static final Msg BOOKS_ORDINAL = new Msg("BibleInfo.Ordinal"); //$NON-NLS-1$
    static final Msg BOOKS_DECODE = new Msg("BibleInfo.Decode"); //$NON-NLS-1$
    static final Msg BOOKS_CHAPTER = new Msg("BibleInfo.Chapter"); //$NON-NLS-1$
    static final Msg BOOKS_VERSE = new Msg("BibleInfo.Verse"); //$NON-NLS-1$

    static final Msg ERROR_CASE = new Msg("BibleInfo.ErrorCase"); //$NON-NLS-1$


    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
