
package org.crosswire.common.swing;

import java.util.Iterator;
import java.util.SortedSet;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 * A simple implementation of ListModel that is backed by a SortedSet
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
public class SortedSetListModel extends AbstractListModel implements ListModel
{
    /**
     * Constructor for ListListModel.
     */
    public SortedSetListModel(SortedSet set)
    {
        this.set = set;
    }

    /**
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        return set.size();
    }

    /**
     * There must be a faster way of doing this?
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        Iterator it = set.iterator();
        int i = 0;
        while (it.hasNext())
        {
            Object element = it.next();
            if (i == index)
                return element;

            i++;
        }

        throw new ArrayIndexOutOfBoundsException();
    }

    private SortedSet set;
}
