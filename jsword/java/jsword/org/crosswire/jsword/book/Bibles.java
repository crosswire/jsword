
package org.crosswire.jsword.book;

import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.events.BiblesEvent;
import org.crosswire.jsword.book.events.BiblesListener;

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
    /**
     * Get an array of the available Bible names. This is done by asking
     * all of the available Bibles in turn and collating the results.
     * @return An array of available version IDs
     * @throws BookException If anything goes wrong with this method
     */
    public static BibleMetaData[] getBibles() throws BookException
    {
        int total = 0;

        // How many Bibles do we have in total?
        BibleDriver[] drivers = BibleDriverManager.getDrivers();
        for (int i = 0; i < drivers.length; i++)
        {
            try
            {
                total += drivers[i].getBibles().length;
            }
            catch (Exception ex)
            {
                Reporter.informUser(Bibles.class, ex);
            }
        }

        if (total == 0)
            return new BibleMetaData[0];

        // Fetch them all into a big list
        BibleMetaData[] retcode = new BibleMetaData[total];
        int count = 0;
        for (int i = 0; i < drivers.length; i++)
        {
            try
            {
                BibleMetaData[] names = drivers[i].getBibles();
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
     * Set the default Bible. The new name must be equal() to a string
     * returned from getBibleNames. (if does not need to be == however)
     * A BookException results if you get it wrong.
     * @param name The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static void setDefault(BibleMetaData bmd) throws BookException
    {
        deft = bmd.getBible();
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
        if (deft == null)
        {
            BibleMetaData bmd = getBibles()[0];
            deft = bmd.getBible();
        }

        return deft;
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

    /** The default Bible */
    private static Bible deft = null;

    /** The list of listeners */
    protected static EventListenerList listeners = new EventListenerList();
}
