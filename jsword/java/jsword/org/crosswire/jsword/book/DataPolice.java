package org.crosswire.jsword.book;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.Key;

/**
 * When we can't convert some source data then the user doesn't really care and
 * just wants it to work, but it would be good to have some way to get the
 * problems fixed, so as a start point we report them through this class.
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
public class DataPolice
{
    /**
     * Prevent instansiation
     */
    private DataPolice()
    {
    }

    /**
     * Set the current book to enhance error reports
     */
    public static void setBook(BookMetaData bmd)
    {
        DataPolice.bmd = bmd;
    }

    /**
     * Set the current verse to enhance error reports
     */
    public static void setKey(Key key)
    {
        DataPolice.key = key;
    }

    /**
     * Report a message against the current verse
     */
    public static void report(String message)
    {
        if (bmd != null && key != null)
        {
            log.debug(bmd.getInitials()+":"+key.getName());
        }

        log.debug(message);
    }

    /**
     * the last known verse
     */
    private static Key key;

    /**
     * The last known Book
     */
    private static BookMetaData bmd;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(DataPolice.class);
}
