
package org.crosswire.jsword.view.swing.book;

import java.awt.Component;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.crosswire.jsword.book.BibleDriver;
import org.crosswire.jsword.book.Books;

/**
 * DriversListModel.
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
public class DriversListModel extends AbstractListModel
{
    /**
     * Basic constructor
     */
    public DriversListModel(boolean include_ro)
    {
        if (include_ro)
            drivers = Books.getDrivers();
        else
            drivers = Books.getWritableDrivers();
    }

    /**
     * Returns the length of the list.
     */
    public int getSize()
    {
        return drivers.length;
    }

    /**
     * Returns the value at the specified index.
     */
    public Object getElementAt(int index)
    {
        if (index >= drivers.length)
            return null;

        return drivers[index].getClass().getName();
    }

    /**
     * Given an item, work out the name of the Bible that it represents
     * @param The item from the list
     * @return A Bible name
     */
    public String getDriverName(Object test)
    {
        String item = test.toString();
        int end = item.indexOf(" (");
        return item.substring(0, end);
    }

    /**
     * Given an item, work out the name of the Driver that it represents
     * @param The item from the list
     * @return A Driver
     */
    public BibleDriver getDriver(Object test)
    {
        return drivers[getIndexOf(test)];
    }

    /**
     * Returns the index-position of the specified object in the list.
     * @param test the object to find
     * @return an int representing the index position, where 0 is the first position
     */
    public int getIndexOf(Object test)
    {
        for (int i=0; i<drivers.length; i++)
        {
            if (test.equals(getElementAt(i)))
                return i;
        }

        return -1;
    }

    /** The array of drivers */
    protected BibleDriver[] drivers;

    /** The small version icon */
    private final static ImageIcon small_icon = new ImageIcon("/org/crosswire/resources/task_small.gif");

    /** border if we do not have focus */
    protected static Border no_focus;

    /**
     * Create a BibleListCellRenderer
     */
    public static ListCellRenderer getListCellRenderer()
    {
        return new BibleListCellRenderer();
    }

    /**
     * A custom list view that paints icons alongside the words. This is a
     * simple modification of DeafultListCellRenderer
     */
    public static class BibleListCellRenderer extends JLabel implements ListCellRenderer
    {
        //Constructs a default renderer object for an item in a list.
        public BibleListCellRenderer()
        {
            if (no_focus == null)
                no_focus = BorderFactory.createEmptyBorder(1, 1, 1, 1);

            setOpaque(true);
            setBorder(no_focus);
        }

        /**
         * This is the only method defined by ListCellRenderer.  We just
         * reconfigure the Jlabel each time we're called.
         * @param list The JLists that we are part of
         * @param value Value to display
         * @param index Cell index
         * @param selected Is the cell selected
         * @param focus Does the list and the cell have the focus
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus)
        {
            if (selected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setText((value == null) ? "" : value.toString());
            setIcon(small_icon);

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder(focus ? UIManager.getBorder("List.focusCellHighlightBorder") : no_focus);

            return this;
        }
    }
}
