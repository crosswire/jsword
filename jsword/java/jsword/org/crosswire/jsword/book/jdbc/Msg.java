package org.crosswire.jsword.book.jdbc;

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
    static final Msg FILTER_FAIL = new Msg("FILTER_FAIL"); //$NON-NLS-1$

    static final Msg BIBLE_LOAD = new Msg("BIBLE_LOAD"); //$NON-NLS-1$
    static final Msg BIBLE_CONNECT = new Msg("BIBLE_CONNECT"); //$NON-NLS-1$
    static final Msg BIBLE_DB = new Msg("BIBLE_DB"); //$NON-NLS-1$
    static final Msg BIBLE_VERSE = new Msg("BIBLE_VERSE"); //$NON-NLS-1$
    static final Msg BIBLE_LOST = new Msg("BIBLE_LOST"); //$NON-NLS-1$

    static final Msg DRIVER_FIND = new Msg("DRIVER_FIND"); //$NON-NLS-1$
    static final Msg DRIVER_CONF = new Msg("DRIVER_CONF"); //$NON-NLS-1$
    static final Msg DRIVER_SAVE = new Msg("DRIVER_SAVE"); //$NON-NLS-1$
    static final Msg DRIVER_READONLY = new Msg("DRIVER_READONLY"); //$NON-NLS-1$

    static final Msg SEARCH_FAIL = new Msg("SEARCH_FAIL"); //$NON-NLS-1$

    static final Msg DELIM_UNMATCHED = new Msg("DELIM_UNMATCHED"); //$NON-NLS-1$
    static final Msg DELIM_NESTED = new Msg("DELIM_NESTED"); //$NON-NLS-1$

    static final Msg ERROR = new Msg("ERROR"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
