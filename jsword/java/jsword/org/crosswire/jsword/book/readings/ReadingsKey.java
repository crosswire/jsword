package org.crosswire.jsword.book.readings;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.crosswire.jsword.book.Key;

/**
 * For a readings dictionary the keys are dates.
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
public class ReadingsKey implements Key, Comparable
{
    /**
     * Simple Constructor.
     * @param text The textual version of the date for these readings in the
     * format "d mmmm"
     */
    protected ReadingsKey(String text) throws ParseException
    {
        date = DF.parse(text);
    }

    /**
     * Simple Constructor.
     * @param date The date for this key
     */
    protected ReadingsKey(Date date)
    {
        this.date = date;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Key#getText()
     */
    public String getText()
    {
        return DF.format(date);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getText();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
        {
            return false;
        }

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        // If super does equals ...
        /* Commented out because super.equals() always equals false
        if (!super.equals(obj))
        {
            return false;
        }
        */

        // The real bit ...
        ReadingsKey that = (ReadingsKey) obj;

        return date.equals(that.date);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return date.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        ReadingsKey that = (ReadingsKey) obj;
        return this.date.compareTo(that.date);
    }

    /**
     * The day of the year for the readings
     */
    private Date date;

    /**
     * Date formatter
     */
    private static final DateFormat DF = new SimpleDateFormat("d MMMM");
    static
    {
        DF.setLenient(true);
    }
}
