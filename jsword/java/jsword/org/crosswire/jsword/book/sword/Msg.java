package org.crosswire.jsword.book.sword;

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
    static final Msg NO_KEY = new Msg("No {0} in index.");
    static final Msg BAD_KEY = new Msg("Invalid Key type={0}, name={1}");
    static final Msg GZIP_FORMAT = new Msg("Error whilst reading field {0}");
    static final Msg FILTER_FAIL = new Msg("Filtering input data failed.");
    static final Msg FILE_ONLY = new Msg("SwordDrivers only work from file: URLs");
    static final Msg MISSING_FILE = new Msg("Missing data files for old and new testaments in {0}.");
    static final Msg NOT_FOUND = new Msg("Module directory not found.");
    static final Msg READ_FAIL = new Msg("Error reading {0}");
    static final Msg READ_ONLY = new Msg("Sword modules are read-only");
    static final Msg COMPRESSION_UNSUPPORTED = new Msg("Unsupported compression type");
    static final Msg TYPE_UNSUPPORTED = new Msg("Unsupported type: {0} when reading {1}");
    static final Msg TYPE_UNKNOWN = new Msg("Unknown type: {0} when reading {1}");
    static final Msg MISSING_SEARCHER = new Msg("Configuration error: Missing search engine.");
    static final Msg MISSING_BACKEND = new Msg("Configuration error: Missing backend engine.");
    static final Msg DRIVER_READONLY = new Msg("This driver is read only. Sorry.");
    static final Msg MISSING_NAME = new Msg("Missing name");

    /**
     * Initialise any resource bundles
     */
    static
    {
        init(Msg.class.getName());
    }

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
