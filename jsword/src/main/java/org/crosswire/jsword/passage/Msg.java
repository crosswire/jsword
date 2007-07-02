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
package org.crosswire.jsword.passage;

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
    static final Msg ACCURACY_BOOK = new Msg("AccuracyType.Book"); //$NON-NLS-1$
    static final Msg ACCURACY_BOOK_CHAPTER = new Msg("AccuracyType.BookChapter"); //$NON-NLS-1$

    static final Msg TALLY_ERROR_ENUM = new Msg("PassageTally.ErrorEnum"); //$NON-NLS-1$
    static final Msg TALLY_ERROR_ORDER = new Msg("PassageTally.ErrorOrder"); //$NON-NLS-1$

    static final Msg PASSAGE_READONLY = new Msg("ReadOnlyPassage.Readonly"); //$NON-NLS-1$

    static final Msg ABSTRACT_VERSE_SINGULAR = new Msg("AbstractPassage.VerseSingular"); //$NON-NLS-1$
    static final Msg ABSTRACT_VERSE_PLURAL = new Msg("AbstractPassage.VersePlural"); //$NON-NLS-1$
    static final Msg ABSTRACT_BOOK_SINGULAR = new Msg("AbstractPassage.BookSingular"); //$NON-NLS-1$
    static final Msg ABSTRACT_BOOK_PLURAL = new Msg("AbstractPassage.BookPlural"); //$NON-NLS-1$

    static final Msg RANGE_BLURBOOK = new Msg("VerseRange.BlurBook"); //$NON-NLS-1$
    static final Msg RANGE_BLURNONE = new Msg("VerseRange.BlurNone"); //$NON-NLS-1$
    static final Msg RANGE_HICOUNT = new Msg("VerseRange.HiCount"); //$NON-NLS-1$
    static final Msg RANGE_LOCOUNT = new Msg("VerseRange.LoCount"); //$NON-NLS-1$
    static final Msg RANGE_PARTS = new Msg("VerseRange.Parts"); //$NON-NLS-1$

    static final Msg VERSE_PARTS = new Msg("Verse.Parts"); //$NON-NLS-1$
    static final Msg VERSE_PARSE = new Msg("Verse.Parse"); //$NON-NLS-1$

    static final Msg ERROR_PATCH = new Msg("Verse.ErrorPatch"); //$NON-NLS-1$

    static final Msg PASSAGE_UNKNOWN = new Msg("PassageUtil.Unknown"); //$NON-NLS-1$

    static final Msg ERROR_BLUR = new Msg("PassageUtil.ErrorBlur"); //$NON-NLS-1$

    static final Msg ABSTRACT_CAST = new Msg("AbstractPassage.Cast"); //$NON-NLS-1$
    static final Msg ABSTRACT_INDEX = new Msg("AbstractPassage.Index"); //$NON-NLS-1$

    static final Msg KEYLIST_READONLY = new Msg("ReadOnlyKeyList.Readonly"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
