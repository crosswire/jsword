
package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.events.BiblesEvent;
import org.crosswire.jsword.book.events.BiblesListener;
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Bibles
{
    public static final int SPEED_REMOTE = 5;
    public static final int SPEED_INACCURATE = 11;

    /**
     * The list of Bibles
     */
    private static List bibles = new ArrayList();

    /**
     * The default Bible
     */
    private static BibleMetaData deft = null;

    /**
     * The list of listeners
     */
    protected static EventListenerList listeners = new EventListenerList();

    /**
     * An array of BookDrivers
     */
    private static List drivers = new ArrayList();

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(Bibles.class);

    /**
     * Get an array of the available Bible names.
     * This is done by asking all of the available Bibles in turn and collating
     * the results. Using this method (equivalent to calling getBibles()
     * followed by calling addBiblesListener()) is preferred because it helps
     * keep people up to date with the available Bibles.
     * @return An array of available version IDs
     * @throws BookException If anything goes wrong with this method
     */
    public static BibleMetaData[] getBibles(BiblesListener li) throws BookException
    {
        return (BibleMetaData[]) bibles.toArray(new BibleMetaData[bibles.size()]);
    }

    /**
     * Get an array of the available Bible names.
     * This is done by asking all of the available Bibles in turn and collating
     * the results. You are encouraged <b>not</b> use this method, but to use
     * getBibles(BiblesListener) to keep up to date with available Bibles.
     * @see #getBibles(BiblesListener)
     * @return An array of available version IDs
     * @throws BookException If anything goes wrong with this method
     */
    public static BibleMetaData[] getBibles() throws BookException
    {
        return (BibleMetaData[]) bibles.toArray(new BibleMetaData[bibles.size()]);
    }

    /**
     * Get the current default Bible. If there are no Bibles that
     * can be accessed (sounds like an installation problem or something)
     * then a BookException results. Otherwise this should always get
     * you something useful.
     * @return the current default version
     * @throws BookException If anything goes wrong with this method
     */
    public static BibleMetaData getDefault() throws BookException
    {
        if (deft == null)
        {
            deft = getBibles()[0];
        }

        return deft;
    }

    /**
     * Get the current default Bible. If there are no Bibles that
     * can be accessed (sounds like an installation problem or something)
     * then a BookException results. Otherwise this should always get
     * you something useful.
     * @return the current default version
     * @throws BookException If anything goes wrong with this method
     */
    public static Bible getDefaultBible() throws BookException
    {
        return getDefault().getBible();
    }

    /**
     * Trawl through all the known Bibles looking for the one closest to
     * the given name.
     * This method is for use with config scripts and other things that
     * <b>need</b> to work with Strings. The preferred method is to use
     * BibleMetaData objects.
     * @param name The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static String getDefaultByName() throws BookException
    {
        return getDefault().getFullName();
    }

    /**
     * Set the default Bible. The new name must be equal() to a string
     * returned from getBibleNames. (if does not need to be == however)
     * A BookException results if you get it wrong.
     * @param bmd The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static void setDefault(BibleMetaData bmd) throws BookException
    {
        deft = bmd;
    }

    /**
     * Trawl through all the known Bibles looking for the one closest to
     * the given name.
     * This method is for use with config scripts and other things that
     * <b>need</b> to work with Strings. The preferred method is to use
     * BibleMetaData objects.
     * @param name The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static void setDefaultByName(String name) throws BookException
    {
        BibleMetaData[] bmds = getBibles();
        for (int i=0; i<bmds.length; i++)
        {
            if (bmds[i].getName().equals(name))
                setDefault(bmds[i]);
        }
    }

    /**
     * Remove a BibleListener from our list of listeners
     * @param li The old listener
     */
    public static void addBiblesListener(BiblesListener li)
    {
        listeners.add(BiblesListener.class, li);
    }

    /**
     * Add a BibleListener to our list of listeners
     * @param li The new listener
     */
    public static void removeBiblesListener(BiblesListener li)
    {
        listeners.remove(BiblesListener.class, li);
    }

    /**
     * Add a Bible to the current list of Bibles.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public static void addBible(BibleMetaData bmd) throws BookException
    {
        log.debug("registering bible: "+bmd.getName());

        bibles.add(bmd);
        fireBiblesChanged(Bibles.class, bmd, true);
    }

    /**
     * Add a Bible to the current list of Bibles.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public static void removeBible(BibleMetaData bmd) throws BookException
    {
        log.debug("unregistering bible: "+bmd.getName());

        boolean removed = bibles.remove(bmd);
        if (removed)
        {
            fireBiblesChanged(Bibles.class, bmd, true);
        }
        else
        {
            throw new BookException("bibles_booknotfound");
        }
    }

    /**
     * Kick of an event sequence
     * @param source The event source
     * @param name The name of the changed Bible
     * @param added Is it added?
     */
    protected static void fireBiblesChanged(Object source, BibleMetaData bmd, boolean added)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        BiblesEvent ev = null;
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == BiblesListener.class)
            {
                if (ev == null)
                    ev = new BiblesEvent(source, bmd, added);

                if (added)
                    ((BiblesListener) contents[i + 1]).bibleAdded(ev);
                else
                    ((BiblesListener) contents[i + 1]).bibleRemoved(ev);
            }
        }
    }

    /**
     * Add to the list of drivers
     * @param driver The BookDriver to add
     */
    public static void registerDriver(BibleDriver driver) throws BookException
    {
        log.debug("begin registering driver: "+driver.getClass().getName());

        if (drivers.contains(driver))
            throw new BookException("book_duplicate_driver");

        drivers.add(driver);

        BibleMetaData[] bmds = driver.getBibles();
        for (int j=0; j<bmds.length; j++)
        {
            addBible(bmds[j]);
        }

        log.debug("end registering driver: "+driver.getClass().getName());
    }

    /**
     * Remove from the list of drivers
     * @param driver The BookDriver to remove
     */
    public static void unregisterDriver(BibleDriver driver) throws BookException
    {
        log.debug("begin un-registering driver: "+driver.getClass().getName());

        BibleMetaData[] bmds = driver.getBibles();
        for (int j=0; j<bmds.length; j++)
        {
            removeBible(bmds[j]);
        }

        if (!drivers.remove(driver))
            throw new BookException("book_not_registered", new Object[] { driver.getClass().getName() });

        log.debug("end un-registering driver: "+driver.getClass().getName());
    }

    /**
     * Get an array of all the known drivers
     * @return Found int or the default value
     */
    public static BibleDriver[] getDrivers()
    {
        return (BibleDriver[]) drivers.toArray(new BibleDriver[drivers.size()]);
    }

    /**
     * Get an array of all the known drivers
     * @return Found int or the default value
     */
    public static BibleDriver[] getWritableDrivers()
    {
        int i = 0;
        for (Iterator it = drivers.iterator(); it.hasNext();)
        {
            BibleDriver driver = (BibleDriver) it.next();
            if (driver.isWritable())
                i++;
        }
        
        BibleDriver[] reply = new BibleDriver[i];

        i = 0;
        for (Iterator it = drivers.iterator(); it.hasNext();)
        {
            BibleDriver driver = (BibleDriver) it.next();
            if (driver.isWritable())
                reply[i++] = driver;
        }

        return reply;
    }

    /**
     * Initialize the name array
     */
    static
    {
        // This will classload them all and they will register themselves.
        Class[] types = Project.resource().getImplementors(BibleDriver.class);

        log.debug("begin auto-registering "+types.length+" drivers:");

        for (int i=0; i<types.length; i++)
        {
            try
            {
                BibleDriver driver = (BibleDriver) types[i].newInstance();
                registerDriver(driver);
            }
            catch (Throwable ex)
            {
                Reporter.informUser(Bibles.class, ex);
            }
        }

        log.debug("end auto-registering drivers:");
    }
}
