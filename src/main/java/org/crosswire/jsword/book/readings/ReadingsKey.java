/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.readings;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import org.crosswire.common.icu.DateFormatter;
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * For a readings dictionary the keys are dates.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class ReadingsKey extends DefaultLeafKeyList {
    /**
     * Simple Constructor.
     * 
     * @param text
     *            The textual version of the date for these readings in the
     *            format "d mmmm"
     * @param osisName
     *            The OSIS id of this Key
     * @param parent
     *            This Key's parent (or null of this Key has no parent)
     */
    protected ReadingsKey(String text, String osisName, Key parent) {
        super(text, osisName, parent);

        DateFormatter formatter = DateFormatter.getDateInstance();
        formatter.setLenient(true);
        date = formatter.parse(text);
    }

    /**
     * Simple Constructor.
     * 
     * @param date
     *            The date for this key
     */
    protected ReadingsKey(Date date) {
        super(DateFormatter.getDateInstance().format(date), DateFormatter.getSimpleDateInstance("d.MMMM").format(date));
        this.date = date;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        // Since this can not be null
        if (obj == null) {
            return false;
        }

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }

        // The real bit ...
        ReadingsKey that = (ReadingsKey) obj;

        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public int compareTo(Key obj) {
        ReadingsKey that = (ReadingsKey) obj;
        return this.date.compareTo(that.date);
    }

    @Override
    public ReadingsKey clone() {
        return (ReadingsKey) super.clone();
    }

    /**
     * Convert the Gregorian Calendar to a string.
     * 
     * @param externalKey
     * @return the internal representation of the key
     */
    public static String external2internal(Calendar externalKey) {
        Object[] objs = {
                Integer.valueOf(1 + externalKey.get(Calendar.MONTH)), Integer.valueOf(externalKey.get(Calendar.DATE))
        };
        return KEY_FORMAT.format(objs);

    }

    /**
     * The day of the year for the readings
     */
    private Date date;

    /**
     * Date formatter
     */
    private static final MessageFormat KEY_FORMAT = new MessageFormat("{0,number,00}.{1,number,00}");

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -5500401548068844993L;
}
