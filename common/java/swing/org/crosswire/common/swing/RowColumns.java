/*
 * Distribution Licence:
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details.
 * The License is available on the internet at:
 *     http://www.gnu.org/copyleft/gpl.html,
 * or by writing to:
 *     Free Software Foundation, Inc.
 *     59 Temple Place - Suite 330
 *     Boston, MA 02111-1307, USA
 * 
 * The copyright to this program is held by it's authors
 * Copyright: 2004
 */
package org.crosswire.common.swing;

import javax.swing.table.DefaultTableColumnModel;

/**
 * Defines the prototypes needed to display a RowTable.
 * Also defines some column indexed concrete methods to access
 * the prototypes.
 * 
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public abstract class RowColumns extends DefaultTableColumnModel
{

    /**
     * Method getHeaders gets the headers for all the columns
     * @return String[] of table headers.
     */
    public abstract String[] getHeaders();

    /**
     * Method getHeaderToolTips gets the tooltips
     * for the headers for all the columns
     * @return String[] of table header's tooltips.
     */
    public abstract String[] getHeaderToolTips();

    /**
     * Method getCharacterWidths gets the widths of all the columns,
     * expressed in Standard Width Characters.
     * @return int[] of widths in standard characters
     */
    public abstract int[] getCharacterWidths();

    /**
     * Method getFixedWidths gives whether a column is not
     * resizable (true) or resizable (false)
     * @return boolean[] of whether a column is fixed
     */
    public abstract boolean[] getFixedWidths();

    /**
     * Method getClasses indicates the type of the data in a column
     * @return Class[] of data types of the columns
     */
    public abstract Class[] getClasses();

    /**
     * Method getSortKeys returns the primary (array of size 1) or
     * composite key (size > 1) used for default sorting and
     * for secondary sorting.
     * @return int[] of the order of columns participating in sort.
     */
    public abstract int[] getSortKeys();

    /**
     * Method getValueAt gets the contents of a cell from a row.
     * @param row the row
     * @param columnIndex int
     * @return Object The content of a cell from a row
     */
    public abstract Object getValueAt(Object row, int columnIndex);

    /**
     * Method getTableName provides the string for a Titled Border.
     * @return String the table name
     */
    public abstract String getTableName();

    /**
     * Method getCount is the number of columns in the table.
     * @return int the number of columns in the table.
     */
    public int getCount()
    {
        return getHeaders().length;
    }

    /**
     * Method getClass gets the class of a given column
     * @param columnIndex int
     * @return Class of the given column
     */
    public Class getClass(int columnIndex)
    {
        final Class[] classes = getClasses();
        if (classes != null && columnIndex < classes.length)
        {
            return classes[columnIndex];
        }
        return null;
    }

    /**
     * Method getName gets the header for the given column
     * @param columnIndex int
     * @return String the header name of the given column
     */
    public String getName(int columnIndex)
    {
        final String[] headers = getHeaders();
        if (headers != null && columnIndex < headers.length)
        {
            return headers[columnIndex];
        }
        return null;
    }

    /**
     * Method getClass gets the class of a given column
     * @param columnIndex int
     * @return Class of the given column
     */
    public String getHeaderToolTip(int columnIndex)
    {
        final String[] tooltips = getHeaderToolTips();
        if (tooltips != null && columnIndex < tooltips.length)
        {
            return tooltips[columnIndex];
        }
        return null;
    }

    /**
     * Method isFixedWidth indicates whether a column is fixed
     * @param columnIndex int
     * @return boolean, true if the column cannot be resized
     */
    public boolean isFixedWidth(int columnIndex)
    {
        final boolean[] fixedWidths = getFixedWidths();
        if (fixedWidths != null && columnIndex < fixedWidths.length)
        {
            return fixedWidths[columnIndex];
        }
        return false;
    }

    /**
     * Method getCharacterWidth gets the width of the column,
     * expressed in Standard Characters
     * @param columnIndex int
     * @return int the number of characters wide the column is to be.
     */
    public int getCharacterWidth(int columnIndex)
    {
        final int[] characterWidths = getCharacterWidths();
        if (characterWidths != null && columnIndex < characterWidths.length)
        {
            return characterWidths[columnIndex];
        }
        return 0;
    }

}
