
package org.crosswire.jsword.book.readings;

import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.crosswire.jsword.util.Project;

/**
 * A driver for the readings dictionary.
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
public class ReadingsBookDriver extends AbstractBookDriver
{
    /**
     * A list of all the books available under this driver
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public BookMetaData[] getBooks()
    {
        return new BookMetaData[] { new ReadingsDictionaryMetaData(this) };
    }

    /**
     * Get a list of the available readings sets
     */
    public static String[] getInstalledReadingsSets()
    {
        return Project.instance().getInstalledReadingsSets();
    }

    /**
     * Accessor for the current readings set
     */
    public static String getReadingsSet()
    {
        if (set == null)
        {
            String[] readings = getInstalledReadingsSets();
            if (readings.length > 0)
            {
                set = readings[0];
            }
        }

        return set;
    }
    
    /**
     * Accessor for the current readings set
     */
    public static void setReadingsSet(String set)
    {
        ReadingsBookDriver.set = set;
    }

    /**
     * The current readings set
     */    
    private static String set = null;
}
