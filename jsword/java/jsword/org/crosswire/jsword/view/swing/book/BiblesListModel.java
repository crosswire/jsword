
package org.crosswire.jsword.view.swing.book;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.events.BiblesEvent;
import org.crosswire.jsword.book.events.BiblesListener;

/**
 * BiblesListModel creates a Swing ListModel from the available Bibles.
 * I would normally implement BiblesListener in an inner class however
 * doing that would stop me calling fireInterval*() in AbstractListModel
 * because that is a protected method and the inner class is neither
 * in the same package or a sub class.
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
public class BiblesListModel extends AbstractListModel implements BiblesListener
{
    /**
     * Basic constructor
     */
    public BiblesListModel()
    {
        cacheData();
    }

    /**
     * Setup the data-stores of the current Bibles and drivers
     */
    private void cacheData()
    {
        try
        {
            bmds = Bibles.getBibles();
        }
        catch (BookException ex)
        {
            Reporter.informUser(this, ex);
            bmds = new BibleMetaData[0];
        }
    }

    /**
     * Returns the length of the list.
     */
    public int getSize()
    {
        return bmds.length;
    }

    /**
     * Returns the value at the specified index.
     */
    public Object getElementAt(int index)
    {
        if (index >= bmds.length)
            return null;

        return bmds[index].getFullName();
    }

    /**
     * Returns the index-position of the specified object in the list.
     * @param test the object to find
     * @return an int representing the index position, where 0 is the first position
     */
    public int getIndexOf(Object test)
    {
        for (int i=0; i<bmds.length; i++)
        {
            if (test == bmds[i])
                return i;
        }

        return -1;
    }

    /**
     * Add a listener to the list that's notified each time a change
     * to the data model occurs.
     * @param li the ListDataListener
     */
    public void addListDataListener(ListDataListener li)
    {
        if (listenerList.getListenerCount() == 0)
            Bibles.addBiblesListener(this);

        super.addListDataListener(li);
    }

    /**
     * Remove a listener from the list that's notified each time a
     * change to the data model occurs.
     * @param li the ListDataListener
     */
    public void removeListDataListener(ListDataListener li)
    {
        super.removeListDataListener(li);

        if (listenerList.getListenerCount() == 0)
            Bibles.removeBiblesListener(this);
    }

    /**
     * Called whenever a new Bible is added or a Bible is removed from
     * the system.
     * @param ev A description of the change
     */
    public void bibleAdded(BiblesEvent ev)
    {
        int old_size = getSize();

        cacheData();

        fireIntervalAdded(ev.getSource(), 0, old_size);
    }

    /**
     * Called whenever a new Bible is added or a Bible is removed from
     * the system.
     * @param ev A description of the change
     */
    public void bibleRemoved(BiblesEvent ev)
    {
        int old_size = getSize();

        cacheData();

        fireIntervalRemoved(ev.getSource(), 0, old_size);
    }

    /** The array of versions */
    protected BibleMetaData[] bmds;
}
