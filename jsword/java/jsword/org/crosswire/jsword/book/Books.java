package org.crosswire.jsword.book;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.book.basic.AbstractBookList;
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
public class Books extends AbstractBookList
{
    /**
     * Prevent Instansiation
     */
    private Books()
    {
    }

    /**
     * Accessor for the singleton instance
     */
    public static Books installed()
    {
        return instance;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBookMetaDatas()
     */
    public List getBookMetaDatas()
    {
        return Collections.unmodifiableList(books);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBookMetaData(java.lang.String)
     */
    public BookMetaData getBookMetaData(String name)
    {
        // First check for exact matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            BookMetaData bmd = (BookMetaData) it.next();
            if (bmd.getName().equals(name))
            {
                return bmd;
            }
        }

        // Next check for case-insensitive matches
        for (Iterator it = books.iterator(); it.hasNext(); )
        {
            BookMetaData bmd = (BookMetaData) it.next();
            if (bmd.getName().equalsIgnoreCase(name))
            {
                return bmd;
            }
        }

        return null;
    }

    /**
     * The list of Books
     */
    private static List books = new ArrayList();

    /**
     * An array of BookDrivers
     */
    private static List drivers = new ArrayList();

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(Books.class);

    /**
     * The singleton instance
     */
    private static final Books instance = new Books();

    /**
     * Do we try to get clever in registering books
     */
    private static boolean threaded = false;
    
    /**
     * Add a Bible to the current list of Books.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public static synchronized void addBook(BookMetaData bmd)
    {
        //log.debug("registering book: "+bmd.getName());

        books.add(bmd);

        fireBooksChanged(Books.class, bmd, true);
    }

    /**
     * Add a Bible to the current list of Books.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public static synchronized void removeBook(BookMetaData bmd) throws BookException
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
    public static synchronized void registerDriver(BookDriver driver) throws BookException
    {
        log.debug("begin registering driver: "+driver.getClass().getName());

        if (drivers.contains(driver))
        {
            throw new BookException(Msg.DUPLICATE_DRIVER);
        }

        drivers.add(driver);

        BookMetaData[] bmds = driver.getBookMetaDatas();
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
    public static synchronized void unregisterDriver(BookDriver driver) throws BookException
    {
        log.debug("begin un-registering driver: "+driver.getClass().getName());

        BookMetaData[] bmds = driver.getBookMetaDatas();
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
    public static synchronized BookDriver[] getDriversByClass(Class type)
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
    public static synchronized BookDriver[] getDrivers()
    {
        return (BookDriver[]) drivers.toArray(new BookDriver[drivers.size()]);
    }

    /**
     * Get an array of all the known drivers
     * @return Found int or the default value
     */
    public static synchronized BookDriver[] getWritableDrivers()
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
        Runnable regman = new RegisterRunnable();
        
        if (threaded)
        {
            Thread init = new Thread(regman, "book-driver-registration");
            init.setPriority(Thread.MIN_PRIORITY);
            init.start();
        }
        else
        {
            regman.run();
        }
    }

    /**
     * A class to register all the BookDrivers
     */
    private static final class RegisterRunnable implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            URL predicturl = Project.instance().getWritablePropertiesURL("books");
            Job job = JobManager.createJob("Job Title", predicturl, null, true);

            try
            {
                // This will classload them all and they will register themselves.
                Class[] types = ResourceUtil.getImplementors(BookDriver.class);

                log.debug("begin auto-registering "+types.length+" drivers:");

                for (int i=0; i<types.length; i++)
                {
                    job.setProgress("Registering Driver: "+ClassUtils.getShortClassName(types[i]));

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
                job.ignoreTimings();
            }
            finally
            {
                job.done();
            }
        }
    }
}
