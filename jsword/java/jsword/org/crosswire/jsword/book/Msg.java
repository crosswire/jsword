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
package org.crosswire.jsword.book;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class Msg extends MsgBase
{
    static final Msg BOOK_NOREMOVE = new Msg("Books.BookNoRemove"); //$NON-NLS-1$
    static final Msg DUPLICATE_DRIVER = new Msg("Books.DuplicateDriver"); //$NON-NLS-1$
    static final Msg DRIVER_NOREMOVE = new Msg("Books.DriverNoRemove"); //$NON-NLS-1$
    static final Msg JOB_TITLE = new Msg("Books.JobTitle"); //$NON-NLS-1$
    static final Msg JOB_DRIVER = new Msg("Books.JobDriver"); //$NON-NLS-1$

    static final Msg BIBLE_NOTFOUND = new Msg("Defaults.BibleNotFound"); //$NON-NLS-1$
    static final Msg DICTIONARY_NOTFOUND = new Msg("Defaults.DictionaryNotFound"); //$NON-NLS-1$
    static final Msg COMMENTARY_NOTFOUND = new Msg("Defaults.CommentaryNotFound"); //$NON-NLS-1$

    static final Msg MISSING_VERSE = new Msg("OSISUtil.MissingVerse"); //$NON-NLS-1$
    static final Msg OSIS_BADID = new Msg("OSISUtil.OSISBadID"); //$NON-NLS-1$

    static final Msg OPEN_UNKNOWN = new Msg("Openness.Unknown"); //$NON-NLS-1$
    static final Msg OPEN_PD = new Msg("Openness.PD"); //$NON-NLS-1$
    static final Msg OPEN_FREE = new Msg("Openness.Free"); //$NON-NLS-1$
    static final Msg OPEN_COPYABLE = new Msg("Openness.Copyable"); //$NON-NLS-1$
    static final Msg OPEN_COMMERCIAL = new Msg("Openness.Commercial"); //$NON-NLS-1$

    static final Msg BOOK_METADATA_SET_OTHER = new Msg("BookSet.Other"); //$NON-NLS-1$

    static final Msg STRONGS_GREEK = new Msg("Strongs.Greek"); //$NON-NLS-1$
    static final Msg STRONGS_HEBREW = new Msg("Strongs.Hebrew"); //$NON-NLS-1$
    static final Msg STRONGS_PARSING = new Msg("Strongs.Parsing"); //$NON-NLS-1$

    static final Msg STRONGS_ERROR_PARSE = new Msg("Strongs.ErrorParse"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_NUMBER = new Msg("Strongs.ErrorNumber"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_HEBREW = new Msg("Strongs.ErrorHebrew"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_GREEK = new Msg("Strongs.ErrorGreek"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_PARSING = new Msg("Strongs.ErrorParsing"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_TYPE = new Msg("Strongs.ErrorType"); //$NON-NLS-1$

    static final Msg ERROR_MIXED = new Msg("SentenceUtil.ErrorMixed"); //$NON-NLS-1$
    static final Msg ERROR_BADCASE = new Msg("SentenceUtil.ErrorBadcase"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
