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
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
final class Msg extends MsgBase
{
    static final Msg NO_KEY = new Msg("SwordDictionary.NoKey"); //$NON-NLS-1$
    static final Msg BAD_KEY = new Msg("RawLDBackend.BadKey"); //$NON-NLS-1$
    static final Msg GZIP_FORMAT = new Msg("SwordUtil.GZIPFormat"); //$NON-NLS-1$
    static final Msg FILTER_FAIL = new Msg("SwordDictionary.FilterFail"); //$NON-NLS-1$
    static final Msg MISSING_FILE = new Msg("GZIPBackend.MissingFile"); //$NON-NLS-1$
    static final Msg READ_FAIL = new Msg("GZIPBackend.ReadFail"); //$NON-NLS-1$
    static final Msg COMPRESSION_UNSUPPORTED = new Msg("BookType.CompressionUnsupported"); //$NON-NLS-1$
    static final Msg TYPE_UNSUPPORTED = new Msg("SwordBookDriver.TypeUnsuported"); //$NON-NLS-1$
    static final Msg DELETE_FAILED = new Msg("SwordBookDriver.DeleteFailed"); //$NON-NLS-1$
    static final Msg TYPE_UNKNOWN = new Msg("RawLDBackend.TypeUnknown"); //$NON-NLS-1$
    static final Msg MISSING_BACKEND = new Msg("SwordDictionary.MissingBackend"); //$NON-NLS-1$
    static final Msg DRIVER_READONLY = new Msg("SwordBook.DriverReadonly"); //$NON-NLS-1$
    static final Msg UNDEFINED_BOOK_TYPE = new Msg("BookType.UndefinedBookType"); //$NON-NLS-1$
    static final Msg UNDEFINED_DATATYPE = new Msg("BookType.UndefinedDatatype"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
