
package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.CollectionUtil;
import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.util.Project;

/**
 * The Bibles class (along with Bible) is the central point of contact
 * between the rest of the world and this set of packages.
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
public class Books
{
    /**
     * Prevent Instansiation
     */
    private Books()
    {
    }

    /**
     * The SPEED_* constants specify how fast a Book implementation is.
     * 
     * Important values include 5, were the remoting system will not remote
     * Books where getSpeed() >= 5 (to save re-remoting already remote Books).
     * 10 is also special - values > 10 indicate the data returned is likely to
     * be wrong (i.e. test data) So we should probably not ship systems with
     * BibleDrivers that return > 10.
     */
    public static final int SPEED_FASTEST = 10;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_FAST = 9;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_MEDIUM = 8;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_SLOW = 7;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_SLOWEST = 6;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_REMOTE_FASTEST = 5;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_REMOTE_FAST = 4;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_REMOTE_MEDIUM = 3;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_REMOTE_SLOW = 2;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_REMOTE_SLOWEST = 1;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_IGNORE = 0;

    /** @see Books#SPEED_FASTEST */
    public static final int SPEED_INACCURATE = -1;

    /**
     * The list of Books
     */
    private static List books = new ArrayList();

    /**
     * The list of listeners
     */
    private static EventListenerList listeners = new EventListenerList();

    /**
     * An array of BookDrivers
     */
    private static List drivers = new ArrayList();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Books.class);

    /**
     * Get an iterator over all the Books of all types.
     */
    public static List getBooks()
    {
        return Collections.unmodifiableList(books);
    }

    /**
     * Get a filtered iterator over all the Books.
     * @see BookFilters
     */
    public static List getBooks(BookFilter filter)
    {
        List temp = CollectionUtil.createList(new BookFilterIterator(books.iterator(), filter));
        return Collections.unmodifiableList(temp);
    }

    /**
     * Remove a BibleListener from our list of listeners
     * @param li The old listener
     */
    public static void addBooksListener(BooksListener li)
    {
        listeners.add(BooksListener.class, li);
    }

    /**
     * Add a BibleListener to our list of listeners
     * @param li The new listener
     */
    public static void removeBooksListener(BooksListener li)
    {
        listeners.remove(BooksListener.class, li);
    }

    /**
     * Add a Bible to the current list of Books.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public static void addBook(BookMetaData bmd)
    {
        log.debug("registering book: "+bmd.getName());

        books.add(bmd);

        fireBooksChanged(Books.class, bmd, true);
    }

    /**
     * Add a Bible to the current list of Books.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public static void removeBook(BookMetaData bmd) throws BookException
    {
        log.debug("unregistering book: "+bmd.getName());

        boolean removed = books.remove(bmd);
        if (removed)
        {
            fireBooksChanged(Books.class, bmd, true);
        }
        else
        {
            throw new BookException(Msg.BOOK_NOREMOVE);
        }
    }

    /**
     * Kick of an event sequence
     * @param source The event source
     * @param bmd The meta-data of the changed Bible
     * @param added Is it added?
     */
    protected static void fireBooksChanged(Object source, BookMetaData bmd, boolean added)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        BooksEvent ev = null;
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == BooksListener.class)
            {
                if (ev == null)
                {
                    ev = new BooksEvent(source, bmd, added);
                }

                if (added)
                {
                    ((BooksListener) contents[i + 1]).bookAdded(ev);
                }
                else
                {
                    ((BooksListener) contents[i + 1]).bookRemoved(ev);
                }
            }
        }
    }

    /**
     * Add to the list of drivers
     * @param driver The BookDriver to add
     */
    public static void registerDriver(BookDriver driver) throws BookException
    {
        log.debug("begin registering driver: "+driver.getClass().getName());

        if (drivers.contains(driver))
        {
            throw new BookException(Msg.DUPLICATE_DRIVER);
        }

        drivers.add(driver);

        BookMetaData[] bmds = driver.getBooks();
        for (int j=0; j<bmds.length; j++)
        {
            addBook(bmds[j]);
        }

        log.debug("end registering driver: "+driver.getClass().getName());
    }

    /**
     * Remove from the list of drivers
     * @param driver The BookDriver to remove
     */
    public static void unregisterDriver(BookDriver driver) throws BookException
    {
        log.debug("begin un-registering driver: "+driver.getClass().getName());

        BookMetaData[] bmds = driver.getBooks();
        for (int j=0; j<bmds.length; j++)
        {
            removeBook(bmds[j]);
        }

        if (!drivers.remove(driver))
        {
            throw new BookException(Msg.DRIVER_NOREMOVE, new Object[] { driver.getClass().getName() });
        }

        log.debug("end un-registering driver: "+driver.getClass().getName());
    }

    /**
     * Since Books keeps a track of drivers itself, including creating them when
     * registered it can be hard to get ahold of the current book driver. This
     * method gives access to the registered instances.
     */
    public static BookDriver[] getDriversByClass(Class type)
    {
        List matches = new ArrayList();
        for (Iterator it = drivers.iterator(); it.hasNext();)
        {
            BookDriver driver = (BookDriver) it.next();
            if (driver.getClass() == type)
            {
                matches.add(driver);
            }
        }
        
        return (BookDriver[]) matches.toArray(new BookDriver[matches.size()]);
    }

    /**
     * Get an array of all the known drivers
     * @return Found int or the default value
     */
    public static BookDriver[] getDrivers()
    {
        return (BookDriver[]) drivers.toArray(new BookDriver[drivers.size()]);
    }

    /**
     * Get an array of all the known drivers
     * @return Found int or the default value
     */
    public static BookDriver[] getWritableDrivers()
    {
        int i = 0;
        for (Iterator it = drivers.iterator(); it.hasNext();)
        {
            BookDriver driver = (BookDriver) it.next();
            if (driver.isWritable())
            {
                i++;
            }
        }
        
        BookDriver[] reply = new BookDriver[i];

        i = 0;
        for (Iterator it = drivers.iterator(); it.hasNext();)
        {
            BookDriver driver = (BookDriver) it.next();
            if (driver.isWritable())
            {
                reply[i++] = driver;
            }
        }

        return reply;
    }

    /**
     * Initialize the name array
     */
    static
    {
        // This will classload them all and they will register themselves.
        Class[] types = Project.resource().getImplementors(BookDriver.class);

        log.debug("begin auto-registering "+types.length+" drivers:");

        for (int i=0; i<types.length; i++)
        {
            try
            {
                BookDriver driver = (BookDriver) types[i].newInstance();
                registerDriver(driver);
            }
            catch (Throwable ex)
            {
                Reporter.informUser(Books.class, ex);
            }
        }

        log.debug("end auto-registering drivers:");
    }
}
