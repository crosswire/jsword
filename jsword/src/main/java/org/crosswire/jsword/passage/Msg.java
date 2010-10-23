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
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Msg extends MsgBase {
    static final Msg TALLY_ERROR_ENUM = new Msg("PassageTally.ErrorEnum");
    static final Msg TALLY_ERROR_ORDER = new Msg("PassageTally.ErrorOrder");
    static final Msg PASSAGE_READONLY = new Msg("ReadOnlyPassage.Readonly");
    static final Msg ERROR_PATCH = new Msg("Verse.ErrorPatch");
    static final Msg PASSAGE_UNKNOWN = new Msg("PassageUtil.Unknown");
    static final Msg ABSTRACT_CAST = new Msg("AbstractPassage.Cast");
    static final Msg ABSTRACT_INDEX = new Msg("AbstractPassage.Index");
    static final Msg KEYLIST_READONLY = new Msg("ReadOnlyKeyList.Readonly");

    /**
     * Passthrough ctor
     */
    private Msg(String name) {
        super(name);
    }
}
