
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Openness;

/**
 * Simple BibleMetaData for the sword implementation.
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
public class SwordBibleMetaData implements BibleMetaData
{
    /**
     * Constructor for SwordBibleMetaData.
     */
    public SwordBibleMetaData(SwordBookDriver driver, File parent, String filename) throws IOException
    {
        this.driver = driver;
        this.config = new SwordConfig(parent, filename);
    }

    /**
     * Fetch a currently existing Bible, read-only
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible getBible() throws BookException
    {
        // DCL
        // I know double checked locking is theoretically broken however it isn't
        // practically broken 99% of the time, and even if the 1% comes up here
        // the only effect is some temporary wasted memory
        if (bible == null)
        {
            synchronized(this)
            {
                if (bible == null)
                {
                    bible = new SwordBible(driver, this);
                }
            }
        }

        return bible;
    }

    /**
     * PENDING(joe): get rid of this:
     * @param swordConfig
     * @return boolean
     */
    protected boolean isBible()
    {
        if (config.getModDrv() == SwordConstants.DRIVER_RAW_TEXT)
            return true;

        if (config.getModDrv() == SwordConstants.DRIVER_Z_TEXT)
            return true;

        return false;
    }

    /**
     * Some basic info about who we are
     * @param A short identifing string
     */
    public String getDriverName()
    {
        return "Sword";
    }

    /**
     * We want to merge with this
     * @return SwordConfig
     */
    protected SwordConfig getSwordConfig()
    {
        return config;
    }

    /**
     * The expected speed at which this implementation gets correct answers.
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public int getSpeed()
    {
        return Books.SPEED_FAST;
    }
    /**
     * @see org.crosswire.jsword.book.BookMetaData#getName()
     */
    public String getName()
    {
        return config.getName();
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getEdition()
     */
    public String getEdition()
    {
        return "";
    }

    /**
     * Do the 2 versions have matching names, editions and drivers.
     * @param obj The object to compare to
     * @return true if the names and editions match
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
            return false;

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
            return false;

        // If super does equals ...
        if (super.equals(obj) == false)
            return false;

        // The real bit ...
        SwordBibleMetaData that = (SwordBibleMetaData) obj;

        if (!getName().equals(that.getName()))
            return false;

        return getEdition().equals(that.getEdition());
    }

    /**
     * Get a moderately unique id for this Object.
     * @return The hashing number
     */
    public int hashCode()
    {
        return (getName() + getEdition()).hashCode();
    }

    /**
     * The full name including edition of the version, for example
     * "New International Version, Anglicised". The format is "name, edition"
     * @return The full name of this version
     */
    public String getFullName()
    {
        return getName() + ", " + getEdition() + " (" + getDriverName() + ")";
    }

    /**
     * Get a human readable version of this Version -just bounce to
     * getFullName()
     * @return The full name of this version
     */
    public String toString()
    {
        return getFullName();
    }

    /**
     * Do the 2 versions have matching names.
     * @param version The version to compare to
     * @return true if the names match
     */
    public boolean isSameFamily(BookMetaData version)
    {
        return getName().equals(version.getName());
    }

    /**
     * Delete a Bible
     * @throws BookException If anything goes wrong with this method
     * @see org.crosswire.jsword.book.BookMetaData#delete()
     */
    public void delete() throws BookException
    {
        throw new BookException("book_nodel", new Object[] { getName() });
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getInitials()
     */
    public String getInitials()
    {
        return StringUtil.getInitials(getName());
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getFirstPublished()
     */
    public Date getFirstPublished()
    {
        return null;
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getOpenness()
     */
    public Openness getOpenness()
    {
        return Openness.UNKNOWN;
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getLicence()
     */
    public URL getLicence()
    {
        return null;
    }

    /**
     * @see org.crosswire.jsword.book.BookMetaData#getBook()
     */
    public Book getBook() throws BookException
    {
        return getBible();
    }

    /**
     * Needed for when we create the Bible
     */
    private SwordBookDriver driver;

    /**
     * The cached bible so we don't have to create too many
     */
    private Bible bible = null;

    /**
     * We want to merge this with us
     */
    private SwordConfig config;
}
