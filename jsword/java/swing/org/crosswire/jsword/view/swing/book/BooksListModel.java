package org.crosswire.jsword.view.swing.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookList;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;

/**
 * BooksListModel creates a Swing ListModel from the available Bibles.
 * I would normally implement BooksListener in an inner class however
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class BooksListModel extends AbstractListModel
{
    /**
     * Basic constructor
     */
    public BooksListModel()
    {
        this(null);
    }

    /**
     * Basic constructor
     */
    public BooksListModel(BookFilter filter)
    {
        this(filter, Books.installed());
    }

    /**
     * Basic constructor
     */
    public BooksListModel(BookFilter filter, BookList books)
    {
        this.filter = filter;
        this.books = books;

        cacheData();
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        return bmds.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        // PARANOIA(joe): this check shouldn't be needed
        if (index > bmds.size())
        {
            log.error("trying to get book at "+index+" when there are only "+bmds.size()+" known books.", new Exception());
            return null;
        }

        return bmds.get(index);
    }

    /**
     * Returns the index-position of the specified object in the list.
     * @param test the object to find
     * @return an int representing the index position, where 0 is the first position
     */
    public int getIndexOf(Object test)
    {
        return bmds.indexOf(test);
    }

    /**
     * @param filter
     */
    public void setFilter(BookFilter filter)
    {
        this.filter = filter;
        cacheData();

        fireContentsChanged(this, 0, getSize());
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    public void addListDataListener(ListDataListener li)
    {
        if (listenerList.getListenerCount() == 0)
        {
            books.addBooksListener(listener);
        }

        super.addListDataListener(li);
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     */
    public void removeListDataListener(ListDataListener li)
    {
        super.removeListDataListener(li);

        if (listenerList.getListenerCount() == 0)
        {
            books.removeBooksListener(listener);
        }
    }

    /**
     * Setup the data-stores of the current Bibles and drivers
     */
    protected void cacheData()
    {
        bmds = new ArrayList();
        bmds.addAll(books.getBookMetaDatas(filter));
        Collections.sort(bmds);
    }

    /**
     * So we can get a handle on what Bibles there are
     */
    class CustomListDataListener implements BooksListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookAdded(BooksEvent ev)
        {
            int old_size = getSize();
            cacheData();
            fireIntervalAdded(ev.getSource(), 0, old_size);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookRemoved(BooksEvent ev)
        {
            int old_size = getSize();
            cacheData();
            fireIntervalRemoved(ev.getSource(), 0, old_size);
        }    
    }

    /* (non-Javadoc)
     * @see javax.swing.AbstractListModel#fireIntervalAdded(java.lang.Object, int, int)
     */
    protected void fireIntervalAdded(Object source, int index0, int index1)
    {
        super.fireIntervalAdded(source, index0, index1);
    }

    /* (non-Javadoc)
     * @see javax.swing.AbstractListModel#fireIntervalRemoved(java.lang.Object, int, int)
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1)
    {
        super.fireIntervalRemoved(source, index0, index1);
    }

    /**
     * The list of books in this tree
     */
    private BookList books;

    /**
     * The filter used to choose Bibles
     */
    private BookFilter filter = null;

    /**
     * The listener
     */
    private CustomListDataListener listener = new CustomListDataListener();

    /**
     * The array of versions
     */
    protected List bmds = null;

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(BooksListModel.class);
}
