
package org.crosswire.jsword.view.swing.book;

import javax.swing.ComboBoxModel;

import org.crosswire.jsword.book.BibleDriver;

/**
 * The DriverModels class implements ComboBoxModel by extending the
 * DriverListModel.
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
public class DriversComboBoxModel extends DriversListModel implements ComboBoxModel
{
    /**
     * Basic Constructor
     */
    public DriversComboBoxModel(boolean include_ro)
    {
        super(include_ro);

        if (drivers.length > 0)
            current = drivers[0];
    }

    /**
     * implements javax.swing.ComboBoxModel
     */
    public void setSelectedItem(Object current)
    {
        this.current = current;
        fireContentsChanged(this, -1, -1);
    }

    /**
     * implements javax.swing.ComboBoxModel
     */
    public Object getSelectedItem()
    {
        return current;
    }

    /**
     * Given an item, work out the name of the Driver that it represents
     * @param The item from the list
     * @return A Driver
     */
    public BibleDriver getSelectedDriver()
    {
        return drivers[getIndexOf(current)];
    }

    /** The currently selected version */
    protected Object current;
}
