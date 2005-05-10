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
package org.crosswire.jsword.book.search.ser;

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
    static final Msg INDEX_START = new Msg("SerIndex.Indexing"); //$NON-NLS-1$
    static final Msg SER_INIT = new Msg("SerIndex.Init"); //$NON-NLS-1$
    static final Msg DELETE_FAILED = new Msg("SerIndex.DeleteFailed"); //$NON-NLS-1$
    static final Msg WRITE_ERROR = new Msg("SerIndex.WriteError"); //$NON-NLS-1$
    static final Msg FINDING_WORDS = new Msg("SerIndex.FindingWords"); //$NON-NLS-1$
    static final Msg WRITING_WORDS = new Msg("SerIndex.WritingWords"); //$NON-NLS-1$
    static final Msg SAVING = new Msg("SerIndex.Saving"); //$NON-NLS-1$
    static final Msg INSTALL_FAIL = new Msg("SerIndex.InstallFailed"); //$NON-NLS-1$

    /**
     * Passthrough ctor 
     */
    private Msg(String name)
    {
        super(name);
    }
}
