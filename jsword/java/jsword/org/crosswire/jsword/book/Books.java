package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.CollectionUtil;
import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;

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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class Books implements BookList
{
    /**
     * Create a singleton instance of the class.
     * This is private to ensure that only one can be created.
     * This also makes the class final!
     */
    private Books()
    {
        books = new ArrayList();
        drivers = new ArrayList();
        listeners = new EventListenerList();
        threaded = false;

        initialize(threaded);
    }

    /**
     * Accessor for the singleton instance
     * @return The singleton instance
     */
    public static Books installed()
    {
        return instance;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBookMetaDatas()
     */
    public synchronized List getBookMetaDatas()
    {
        return Collections.unmodifiableList(books);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBookMetaData(java.lang.String)
     */
    public synchronized BookMetaData getBookMetaData(String name)
    {
        // Check name first
        // First check for exact matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            BookMetaData bmd = (BookMetaData) it.next();
            if (name.equals(bmd.getName()))
            {
                return bmd;
            }
        }

        // Next check for case-insensitive matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            BookMetaData bmd = (BookMetaData) it.next();
            if (name.equalsIgnoreCase(bmd.getName()))
            {
                return bmd;
            }
        }

        // Then check initials
        // First check for exact matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            BookMetaData bmd = (BookMetaData) it.next();
            if (name.equals(bmd.getInitials()))
            {
                return bmd;
            }
        }

        // Next check for case-insensitive matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            BookMetaData bmd = (BookMetaData) it.next();
            if (name.equalsIgnoreCase(bmd.getInitials()))
            {
                return bmd;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBookMetaDatas(org.crosswire.jsword.book.BookFilter)
     */
    public synchronized List getBookMetaDatas(BookFilter filter)
    {
        List temp = CollectionUtil.createList(new BookFilterIterator(getBookMetaDatas().iterator(), filter));
        return Collections.unmodifiableList(temp);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#addBooksListener(org.crosswire.jsword.book.BooksListener)
     */
    public synchronized void addBooksListener(BooksListener li)
    {
        listeners.add(BooksListener.class, li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#removeBooksListener(org.crosswire.jsword.book.BooksListener)
     */
    public synchronized void removeBooksListener(BooksListener li)
    {
        listeners.remove(BooksListener.class, li);
    }

    /**
     * Kick of an event sequence
     * @param source The event source
     * @param bmd The meta-data of the changed Bible
     * @param added Is it added?
     */
    protected synchronized void fireBooksChanged(Object source, BookMetaData bmd, boolean added)
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
     * Add a Bible to the current list of Books.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public synchronized void addBook(BookMetaData bmd)
    {
        //log.debug("registering book: "+bmd.getName());

        books.add(bmd);

        fireBooksChanged(Books.class, bmd, true);
    }

    /**
     * Remove a Bible from the current list of Books.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public synchronized void removeBook(BookMetaData bmd) throws BookException
    {
        //log.debug("unregistering book: "+bmd.getName());

        Activator.deactivate(bmd.getBook());

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
     * Add to the list of drivers
     * @param driver The BookDriver to add
     */
    public synchronized void registerDriver(BookDriver driver) throws BookException
    {
        log.debug("begin registering driver: " + driver.getClass().getName()); //$NON-NLS-1$

        if (drivers.contains(driver))
        {
            throw new BookException(Msg.DUPLICATE_DRIVER);
        }

        drivers.add(driver);

        BookMetaData[] bmds = driver.getBookMetaDatas();
        for (int j = 0; j < bmds.length; j++)
        {
            addBook(bmds[j]);
        }

        log.debug("end registering driver: " + driver.getClass().getName()); //$NON-NLS-1$
    }

    /**
     * Remove from the list of drivers
     * @param driver The BookDriver to remove
     */
    public synchronized void unregisterDriver(BookDriver driver) throws BookException
    {
        log.debug("begin un-registering driver: " + driver.getClass().getName()); //$NON-NLS-1$

        BookMetaData[] bmds = driver.getBookMetaDatas();
        for (int j = 0; j < bmds.length; j++)
        {
            removeBook(bmds[j]);
        }

        if (!drivers.remove(driver))
        {
            throw new BookException(Msg.DRIVER_NOREMOVE, new Object[] { driver.getClass().getName() });
        }

        log.debug("end un-registering driver: " + driver.getClass().getName()); //$NON-NLS-1$
    }

    /**
     * Since Books keeps a track of drivers itself, including creating them when
     * registered it can be hard to get ahold of the current book driver. This
     * method gives access to the registered instances.
     */
    public synchronized BookDriver[] getDriversByClass(Class type)
    {
        List matches = new ArrayList();
        for (Iterator it = drivers.iterator(); it.hasNext(); )
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
    public synchronized BookDriver[] getDrivers()
    {
        return (BookDriver[]) drivers.toArray(new BookDriver[drivers.size()]);
    }

    /**
     * Get an array of all the known drivers
     * @return Found int or the default value
     */
    public synchronized BookDriver[] getWritableDrivers()
    {
        int i = 0;
        for (Iterator it = drivers.iterator(); it.hasNext(); )
        {
            BookDriver driver = (BookDriver) it.next();
            if (driver.isWritable())
            {
                i++;
            }
        }

        BookDriver[] reply = new BookDriver[i];

        i = 0;
        for (Iterator it = drivers.iterator(); it.hasNext(); )
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
     * Registers all the drivers known to the program.
     * Either in a thread or in the main thread
     */
    private void initialize(boolean doThreading)
    {
        if (doThreading)
        {
            Runnable runner = new Runnable()
            {
                public void run()
                {
                    autoRegister();
                }
            };

            Thread init = new Thread(runner, "book-driver-registration"); //$NON-NLS-1$
            init.setPriority(Thread.MIN_PRIORITY);
            init.start();
        }
        else
        {
            autoRegister();
        }
    }

    /**
     * Registers all the drivers known to the program.
     */
    protected void autoRegister()
    {
        // URL predicturl = Project.instance().getWritablePropertiesURL("books"); //$NON-NLS-1$
        // Job job = JobManager.createJob(Msg.JOB_TITLE.toString(), predicturl, null, true);

        try
        {
            // This will classload them all and they will register themselves.
            Class[] types = ClassUtil.getImplementors(BookDriver.class);

            log.debug("begin auto-registering " + types.length + " drivers:"); //$NON-NLS-1$ //$NON-NLS-2$

            for (int i = 0; i < types.length; i++)
            {
                //job.setProgress(Msg.JOB_DRIVER.toString() + ClassUtils.getShortClassName(types[i]));

                try
                {
                    BookDriver driver = (BookDriver) types[i].newInstance();
                    registerDriver(driver);
                }
                catch (Exception ex)
                {
                    Reporter.informUser(Books.class, ex);
                }
            }
        }
        catch (Exception ex)
        {
            log.debug("Unexpected exception: " + ex); //$NON-NLS-1$
            // job.ignoreTimings();
        }
        finally
        {
            // job.done();
        }
    }

    /**
     * The list of Books
     */
    private List books;

    /**
     * An array of BookDrivers
     */
    private List drivers;

    /**
     * The list of listeners
     */
    private EventListenerList listeners;

    /**
     * Do we try to get clever in registering books?.
     * Not until we can get it to work!
     * At this time there is no way to set this or influence it
     * So it just acts as a means of commenting out code.
     */
    private boolean threaded =  false;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(Books.class);

    /**
     * The singleton instance.
     * This needs to be declared after all other statics it uses.
     */
    private static final Books instance = new Books();
}
