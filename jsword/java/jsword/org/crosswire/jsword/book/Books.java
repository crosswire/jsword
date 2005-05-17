/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class Books implements BookList
{
    /**
     * Create a singleton instance of the class.
     * This is private to ensure that only one can be created.
     * This also makes the class final!
     */
    private Books()
    {
        books = new ArrayList();
        drivers = new HashSet();
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
     * @see org.crosswire.jsword.book.BookList#getBooks()
     */
    public synchronized List getBooks()
    {
        return Collections.unmodifiableList(books);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBook(java.lang.String)
     */
    public synchronized Book getBook(String name)
    {
        // Check name first
        // First check for exact matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            Book book = (Book) it.next();
            if (name.equals(book.getName()))
            {
                return book;
            }
        }

        // Next check for case-insensitive matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            Book book = (Book) it.next();
            if (name.equalsIgnoreCase(book.getName()))
            {
                return book;
            }
        }

        // Then check initials
        // First check for exact matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            Book book = (Book) it.next();
            BookMetaData bmd = book.getBookMetaData();
            if (name.equals(bmd.getInitials()))
            {
                return book;
            }
        }

        // Next check for case-insensitive matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            Book book = (Book) it.next();
            if (name.equalsIgnoreCase(book.getInitials()))
            {
                return book;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBooks(org.crosswire.jsword.book.BookFilter)
     */
    public synchronized List getBooks(BookFilter filter)
    {
        List temp = CollectionUtil.createList(new BookFilterIterator(getBooks().iterator(), filter));
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
     * @param book The changed Book
     * @param added Is it added?
     */
    protected synchronized void fireBooksChanged(Object source, Book book, boolean added)
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
                    ev = new BooksEvent(source, book, added);
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
    public synchronized void addBook(Book book)
    {
        //log.debug("registering book: "+bmd.getName());

        if (!books.contains(book))
        {
            books.add(book);
            fireBooksChanged(instance, book, true);
        }
    }

    /**
     * Remove a Bible from the current list of Books.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public synchronized void removeBook(Book book) throws BookException
    {
        //log.debug("unregistering book: "+bmd.getName());

        Activator.deactivate(book);

        boolean removed = books.remove(book);
        if (removed)
        {
            fireBooksChanged(instance, book, true);
        }
        else
        {
            throw new BookException(Msg.BOOK_NOREMOVE);
        }
    }

    /**
     * Register the driver, adding its books to the list. Any books that this driver
     * used, but not any more are removed. This can be called repeatedly to re-register
     * the driver.
     * @param driver The BookDriver to add
     */
    public synchronized void registerDriver(BookDriver driver) throws BookException
    {
        log.debug("begin registering driver: " + driver.getClass().getName()); //$NON-NLS-1$

        drivers.add(driver);

        // Go through all the books and add all the new ones.
        // Remove those that are not known to the driver, but used to be.
        Book[] bookArray = driver.getBooks();
        Set current = CollectionUtil.createSet(new BookFilterIterator(getBooks().iterator(), BookFilters.getBooksByDriver(driver)));

        for (int j = 0; j < bookArray.length; j++)
        {
            Book b = bookArray[j];
            if (current.contains(b))
            {
                // Since it was already in there, we don't add it.
                // By removing it from current we will be left with
                // what is not now known by the driver.
                current.remove(b);
            }
            else
            {
                addBook(bookArray[j]);
            }
        }

        // Remove the books from the previous version of the driver
        // that are not in this version.
        Iterator iter = current.iterator();
        while (iter.hasNext())
        {
            removeBook((Book) iter.next());
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

        Book[] bookArray = driver.getBooks();
        for (int j = 0; j < bookArray.length; j++)
        {
            removeBook(bookArray[j]);
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
                    Method driverInstance = types[i].getMethod("instance", new Class[0]); //$NON-NLS-1$
//                    Object retval = driverInstance.invoke(null, new Object[0]);
                    BookDriver driver = (BookDriver) driverInstance.invoke(null, new Object[0]); //types[i].newInstance();
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
    }

    /**
     * The list of Books
     */
    private List books;

    /**
     * An array of BookDrivers
     */
    private Set drivers;

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
    private boolean threaded;

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
