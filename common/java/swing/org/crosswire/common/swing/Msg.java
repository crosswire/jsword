package org.crosswire.common.swing;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
class Msg extends MsgBase
{
    static final Msg CLOSE = new Msg("CLOSE"); //$NON-NLS-1$
    static final Msg ERROR_OCCURED = new Msg("ERROR_OCCURED"); //$NON-NLS-1$
    static final Msg OK = new Msg("OK"); //$NON-NLS-1$
    static final Msg DETAILS = new Msg("DETAILS"); //$NON-NLS-1$
    static final Msg NO_FILE = new Msg("NO_FILE"); //$NON-NLS-1$
    static final Msg ERROR = new Msg("ERROR"); //$NON-NLS-1$
    static final Msg CAUSED_BY = new Msg("CAUSED_BY"); //$NON-NLS-1$
    static final Msg NO_DESC = new Msg("NO_DESC"); //$NON-NLS-1$
    static final Msg SOURCE_NOT_FOUND = new Msg("SOURCE_NOT_FOUND"); //$NON-NLS-1$
    static final Msg SOURCE_ATTEMPT = new Msg("SOURCE_ATTEMPT"); //$NON-NLS-1$
    static final Msg ERROR_TABLE_MODEL = new Msg("ERROR_TABLE_MODEL"); //$NON-NLS-1$
    static final Msg NO_PROBLEMS = new Msg("NO_PROBLEMS"); //$NON-NLS-1$
    static final Msg STATUS = new Msg("STATUS"); //$NON-NLS-1$
    static final Msg REMOVE = new Msg("REMOVE"); //$NON-NLS-1$
    static final Msg CANCEL = new Msg("CANCEL"); //$NON-NLS-1$
    static final Msg SELECT_FONT = new Msg("SELECT_FONT"); //$NON-NLS-1$
    static final Msg ERROR_CELL_RENDER = new Msg("ERROR_CELL_RENDER"); //$NON-NLS-1$
    static final Msg PLAF_CHANGE = new Msg("PLAF_CHANGE"); //$NON-NLS-1$
    static final Msg BOLD = new Msg("BOLD"); //$NON-NLS-1$
    static final Msg ITALIC = new Msg("ITALIC"); //$NON-NLS-1$
    static final Msg KEYS = new Msg("KEYS"); //$NON-NLS-1$
    static final Msg VALUES = new Msg("VALUES"); //$NON-NLS-1$
    static final Msg LOADING = new Msg("LOADING"); //$NON-NLS-1$
    static final Msg COPY_TO_CLIP = new Msg("COPY_TO_CLIP"); //$NON-NLS-1$
    static final Msg TEXT_VIEWER = new Msg("TEXT_VIEWER"); //$NON-NLS-1$
    static final Msg ERROR_READING = new Msg("ERROR_READING"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
