
package org.crosswire.jsword.book;

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
    public static final Msg NO_COMMENTARIES = new Msg("No Commentaries found");
    public static final Msg NO_DICTIONARIES = new Msg("No Dictionaries found");
    public static final Msg NO_BIBLES = new Msg("No Bibles found");

    public static final Msg BOOK_NOREMOVE = new Msg("Could not remove unregistered Book");
    public static final Msg DUPLICATE_DRIVER = new Msg("Driver already registered");
    public static final Msg DRIVER_NOREMOVE = new Msg("Could not remove unregistered Driver");

    public static final Msg BIBLE_NOTFOUND = new Msg("Bible called \"{0}\" could not be found.");
    public static final Msg DICTIONRY_NOTFOUND = new Msg("Dictionary called \"{0}\" could not be found.");
    public static final Msg COMMENTARY_NOTFOUND = new Msg("Commentary called \"{0}\" could not be found.");

    /** Initialise any resource bundles */
    static
    {
        init(Msg.class.getName());
    }

    /** Passthrough ctor */
    private Msg(String name)
    {
        super(name);
    }
}
