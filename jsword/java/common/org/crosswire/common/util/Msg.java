package org.crosswire.common.util;

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
    static final Msg REASON = new Msg("Reason: "); //$NON-NLS-1$
    static final Msg SECURITY = new Msg("Could not create ClassResolver: "); //$NON-NLS-1$
    static final Msg UNAVILABLE = new Msg("<Unavailable>"); //$NON-NLS-1$
    static final Msg NO_RESOURCE = new Msg("Can't find resource: {0}"); //$NON-NLS-1$
    static final Msg NOT_ASSIGNABLE = new Msg("Class {0} does not implement {1}"); //$NON-NLS-1$
    static final Msg IS_FILE = new Msg("The given URL {0} is a file."); //$NON-NLS-1$
    static final Msg CREATE_DIR_FAIL = new Msg("The given URL {0} could not be created as a directory."); //$NON-NLS-1$
    static final Msg IS_DIR = new Msg("The given URL {0} is a directory."); //$NON-NLS-1$
    static final Msg CREATE_FILE_FAIL = new Msg("The given URL {0} could not be created as a file."); //$NON-NLS-1$
    static final Msg CANT_STRIP = new Msg("The URL {0} does not end in {1}."); //$NON-NLS-1$
    static final Msg NOT_DIR = new Msg("URL {0} is not a directory"); //$NON-NLS-1$
    static final Msg NOT_FILE_URL = new Msg("The given URL {0} is not a file: URL."); //$NON-NLS-1$
    static final Msg WRONG_TYPE = new Msg("Listener {0} is not of type {1}"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
