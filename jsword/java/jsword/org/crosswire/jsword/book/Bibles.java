
package org.crosswire.jsword.book;

import java.util.Hashtable;

import org.crosswire.jsword.book.events.BiblesEvent;
import org.crosswire.jsword.book.events.BiblesListener;
import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.Reporter;

/**
* The Bibles class (along with Bible) is the central point of contact
* between the rest of the world and this set of packages.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
* @version $Id$
*/
public class Bibles
{
    /**
    * Get an array of the available Bible names. This is done by asking
    * all of the available Bibles in turn and collating the results.
    * @return An array of available version IDs
    * @throws BookException If anything goes wrong with this method
    */
    public static String[] getBibleNames() throws BookException
    {
        int total = 0;

        // How many Bibles do we have in total?
        BibleDriver[] drivers = BibleDriverManager.getDrivers();
        for (int i = 0; i < drivers.length; i++)
        {
            try
            {
                total += drivers[i].countBibles();
            }
            catch (Exception ex)
            {
                Reporter.informUser(Bibles.class, ex);
            }
        }

        // Fetch them all into a big list
        String[] retcode = new String[total];
        int count = 0;
        for (int i = 0; i < drivers.length; i++)
        {
            try
            {
                String[] names = drivers[i].getBibleNames();
                System.arraycopy(names, 0, retcode, count, names.length);
                count += names.length;
            }
            catch (Exception ex)
            {
                Reporter.informUser(Bibles.class, ex);
            }
        }

        return retcode;
    }

    /**
    * Load up a Bible by name
    * @param name The version name to create
    * @return The version requested ready for use
    * @throws BookException If anything goes wrong with this method
    */
    public static Bible getBible(String name) throws BookException
    {
        Bible retcode = null;

        // If we are cacheing versions ...
        if (bibles != null)
            retcode = (Bible) bibles.get(name);

        // If we don't have a version, create one
        if (retcode == null)
        {
            BibleDriver driver = BibleDriverManager.getDriverForBible(name);
            retcode = driver.getBible(name);

            bibles.put(name, retcode);
        }

        return retcode;
    }

    /**
    * Get a MutableBook ready to have generate called.
    * dest_name should not be the name of an existing Bible.
    * @param dest_name The version name to create
    * @param dest_driver The driver to use
    * @return The version requested ready for generate to be called
    * @throws BookException If anything goes wrong with this method
    */
    public static WritableBible createBible(String dest_name, WritableBibleDriver dest_driver) throws BookException
    {
        // There might be a better way of doing this
        try
        {
            Bible version = getBible(dest_name);
            throw new BookException("book_exists",
                new Object[] { dest_name });
        }
        catch (BookException ex) { }

        WritableBible dest_version = dest_driver.createBible(dest_name);
        fireBiblesChanged(Bibles.class, dest_name, true);

        return dest_version;
    }

    /**
    * Delete a Bible by name
    * @param name The version name to delete
    * @throws BookException If anything goes wrong with this method
    */
    public static void deleteBible(String name) throws BookException
    {
        WritableBibleDriver driver = BibleDriverManager.getWritableDriverForBible(name);
        driver.deleteBible(name);

        if (bibles.contains(name))
            bibles.remove(name);

        fireBiblesChanged(Bibles.class, name, false);
    }

    /**
    * Rename a Bible by name
    * @param old_name The version name to delete
    * @param new_name The version name to create
    * @throws BookException If anything goes wrong with this method
    */
    public static void renameBible(String old_name, String new_name) throws BookException
    {
        WritableBibleDriver driver = BibleDriverManager.getWritableDriverForBible(old_name);
        driver.renameBible(old_name, new_name);

        if (bibles.contains(old_name))
            bibles.remove(old_name);

        fireBiblesChanged(Bibles.class, old_name, false);
        fireBiblesChanged(Bibles.class, new_name, true);
    }

    /**
    * Set the default Bible. The new name must be equal() to a string
    * returned from getBibleNames. (if does not need to be == however)
    * A BookException results if you get it wrong.
    * @param name The version to use as default.
    * @exception BookException If the name is not valid
    */
    public static void setDefaultName(String name) throws BookException
    {
        // Check that this is valid
        //BookConfig config = new BookConfig(name);
        //Class driver = config.getDriver();

        // We need to do this (only if the driver has changed) so that
        // the next time getBible() is called the new version will be
        // re-created
        if (!default_name.equals(name))
        {
            BibleDriver driver = BibleDriverManager.getDriverForBible(name);
            default_bible = driver.getBible(name);
            default_name = name;
        }
    }

    /**
    * Get the current default Bible name. If there are no Bibles that
    * can be accessed (sounds like an installation problem or something)
    * then a BookException results. Otherwise this should always get
    * you something useful.
    * @return the current default version name
    * @throws BookException If anything goes wrong with this method
    */
    public static String getDefaultName() throws BookException
    {
        if (default_name == null)
            default_name = getBibleNames() [0];

        return default_name;
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
        if (default_bible == null)
        {
            BibleDriver driver = BibleDriverManager.getDriverForBible(getDefaultName());
            default_bible = driver.getBible(getDefaultName());
        }

        return default_bible;
    }

    /**
    * @return are we cacheing Bibles?
    */
    public static boolean getCacheingBibles()
    {
        return bibles != null;
    }

    /**
    * Are we cacheing Bibles?
    * @param cache The new cache setting
    */
    public static void setCacheingBibles(boolean cache)
    {
        if (cache && bibles == null)
        {
            bibles = new Hashtable();
        }
        else if (!cache && bibles != null)
        {
            bibles = null;
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
    * Kick of an event sequence
    * @param source The event source
    * @param name The name of the changed Bible
    * @param added Is it added?
    */
    public static void fireBiblesChanged(Object source, String name, boolean added)
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
                    ev = new BiblesEvent(source, name, added);

                ((BiblesListener) contents[i + 1]).biblesChanged(ev);
            }
        }
    }

    /** The cache of versions */
    private static Hashtable bibles = new Hashtable();

    /** The default Bible */
    private static Bible default_bible = null;

    /** The default version name */
    private static String default_name;

    /** The list of listeners */
    protected static EventListenerList listeners = new EventListenerList();
}
