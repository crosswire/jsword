
package org.crosswire.jsword.book.jdbc;

import org.crosswire.common.util.I18NBase;

/**
 * Compile safe I18N resource settings.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
class I18N extends I18NBase
{
    public static final I18N BIBLE_LOAD = new I18N("Failed to load any of the JDBC Drivers. (Tried {0} drivers)");
    public static final I18N BIBLE_CONNECT = new I18N("Failed to connect to ODBC Database.");
    public static final I18N BIBLE_DB = new I18N("Database Error.");
    public static final I18N BIBLE_VERSE = new I18N("Must be 3 parts to the reference.");
    public static final I18N BIBLE_LOST = new I18N("Can't find that verse in the database.");

    public static final I18N DRIVER_FIND = new I18N("No Bibles found at \"{0}\".");
    public static final I18N DRIVER_CONF = new I18N("Error finding configuration file.");
    public static final I18N DRIVER_SAVE = new I18N("Error saving configuration file \"{0}\".");
    public static final I18N DRIVER_READONLY = new I18N("The JDBC Version is read only. Sorry.");

    /** Initialise any resource bundles */
    static
    {
        init(I18N.class.getName());
    }

    /** Passthrough ctor */
    private I18N(String name)
    {
        super(name);
    }
}
