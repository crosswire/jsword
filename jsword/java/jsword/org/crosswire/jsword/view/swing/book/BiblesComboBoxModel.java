
package org.crosswire.jsword.view.swing.book;

import javax.swing.ComboBoxModel;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.BibleMetaData;

/**
 * The BibleModels class implements a number of swing DataModels
 * and gives access to the list of current Bibles.
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
public class BiblesComboBoxModel extends BiblesListModel implements ComboBoxModel
{
    /**
     * Basic Constructor
     */
    public BiblesComboBoxModel()
    {
        if (bmds.length > 0)
            current = bmds[0];
    }

    /**
     * implements javax.swing.ComboBoxModel
     */
    public void setSelectedItem(Object current)
    {
        this.current = (BibleMetaData) current;
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
     * Get the selected Bible
     * @return A Bible
     */
    public BibleMetaData getSelectedBibleMetaData()
    {
        return current;
    }

    /** The currently selected version */
    protected BibleMetaData current;

    /** The log stream */
    protected static Logger log = Logger.getLogger(BiblesComboBoxModel.class);
}
