
package org.crosswire.jsword.book;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Handles the current default Books.
 * 
 * <p>This is used whenever the user works with one Book at a time and many
 * parts of the system want to know what the current is.
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
public class Defaults
{
    /**
     * Prevent construction
     */
    private Defaults()
    {
    }

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(Books.class);

    /**
     * Has the default Bible been manually set or are we picking the fastest
     * as the default?
     */
    private static boolean autobdeft = true;

    /**
     * The default Bible
     */
    protected static BibleMetaData bdeft = null;

    /**
     * Has the default Commentary been manually set or are we picking the fastest
     * as the default?
     */
    private static boolean autocdeft = true;

    /**
     * The default Commentary
     */
    protected static CommentaryMetaData cdeft = null;

    /**
     * Has the default Dictionary been manually set or are we picking the fastest
     * as the default?
     */
    private static boolean autoddeft = true;

    /**
     * The default Dictionary
     */
    protected static DictionaryMetaData ddeft = null;

    /**
     * Set the default Bible. The new name must be equal() to a string
     * returned from getBibleNames. (if does not need to be == however)
     * A BookException results if you get it wrong.
     * @param bmd The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static void setBibleMetaData(BibleMetaData bmd)
    {
        autobdeft = false;
        bdeft = bmd;
    }

    /**
     * Get the current default Bible.
     * <p>If there are no Bibles that can be accessed (sounds like an
     * installation problem or something) then a BookException results.
     * Otherwise this should always get you something useful.
     * <p>In general if you are calling this method from <b>outside</b> GUI code
     * the there is probably something wrong.
     * @return the current default version
     * @throws BookException If anything goes wrong with this method
     */
    public static BibleMetaData getBibleMetaData() throws BookException
    {
        return bdeft;
    }

    /**
     * This method is identical to <code>getBible().getFullName()</code> and is
     * only used by Config which works best with strings under reflection.
     * <p>Generally <code>getBible().getFullName()</code> is a better way of
     * getting what you want.
     * @param name The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static String getBibleByName() throws BookException
    {
        return getBibleMetaData().getFullName();
    }

    /**
     * Trawl through all the known Bibles looking for the one closest to
     * the given name.
     * <p>This method is for use with config scripts and other things that
     * <b>need</b> to work with Strings. The preferred method is to use
     * BibleMetaData objects.
     * <p>This method is picky in that it only matches when the driver and the
     * version are the same. The user (probably) only cares about the version
     * though, and so might be dissapointed when we fail to match AV (FooDriver)
     * against AV (BarDriver).
     * @param name The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static void setBibleByName(String name) throws BookException
    {
        autobdeft = false;

        List lbmds = Books.getBooks();
        for (Iterator it=lbmds.iterator(); it.hasNext();)
        {
            BibleMetaData bmd = (BibleMetaData) it.next();
            String tname = bmd.getFullName();
            if (tname.equals(name))
            {
                setBibleMetaData(bmd);
                return;
            }
        }
    
        throw new BookException(Msg.BOOK_NOTFOUND, new Object[] { name });
    }

    /**
     * Go through all of the current books checking to see if we need to replace
     * the current defaults with one of these.
     */
    protected static void checkAllPreferable()
    {
        List bmds = Books.getBooks();
        for (Iterator it = bmds.iterator(); it.hasNext();)
        {
            BookMetaData bmd = (BookMetaData) it.next();
            checkPreferable(bmd);
        }
    }

    /**
     * Should this Bible become the default?
     */
    protected static void checkPreferable(BookMetaData bmd)
    {
        if (bmd == null)
        {
            throw new NullPointerException();
        }

        if (bmd instanceof BibleMetaData)
        {
            // Do we even think about replacing the default Bible?
            if (autobdeft || bdeft == null)
            {
                // If there is no default or this is faster
                if (bdeft == null || bmd.getSpeed() > bdeft.getSpeed())
                {
                    bdeft = (BibleMetaData) bmd;
                    log.debug("setting as default bible since speed="+bdeft.getSpeed());
                }
            }
        }
        else if (bmd instanceof CommentaryMetaData)
        {
            // Do we even think about replacing the default Bible?
            if (autocdeft || cdeft == null)
            {
                // If there is no default or this is faster
                if (cdeft == null || bmd.getSpeed() > cdeft.getSpeed())
                {
                    cdeft = (CommentaryMetaData) bmd;
                    log.debug("setting as default commentary since speed="+cdeft.getSpeed());
                }
            }
        }
        else if (bmd instanceof DictionaryMetaData)
        {
            // Do we even think about replacing the default Bible?
            if (autoddeft || ddeft == null)
            {
                // If there is no default or this is faster
                if (ddeft == null || bmd.getSpeed() > ddeft.getSpeed())
                {
                    ddeft = (DictionaryMetaData) bmd;
                    log.debug("setting as default dictionary since speed="+ddeft.getSpeed());
                }
            }
        }
    }

    /**
     * Register with Books so we know how to provide valid defaults
     */
    static
    {
        if (bdeft == null)
        {
            checkAllPreferable();
        }

        Books.addBooksListener(new DefaultsBookListener());
    }

    /**
     * 
     */
    private static class DefaultsBookListener implements BooksListener
    {
        public void bookAdded(BooksEvent ev)
        {
            BookMetaData bmd = ev.getBookMetaData(); 
            checkPreferable(bmd);
        }

        public void bookRemoved(BooksEvent ev)
        {
            BookMetaData bmd = ev.getBookMetaData(); 

            // Was this a default?
            if (bmd.equals(bdeft) || bmd.equals(cdeft) || bmd.equals(ddeft))
            {
                // find the next fastest
                checkAllPreferable();
            }
        }
    }
}
