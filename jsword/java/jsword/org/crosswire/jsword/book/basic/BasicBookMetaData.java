
package org.crosswire.jsword.book.basic;

import java.net.URL;
import java.util.Date;

import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.common.util.StringUtil;

/**
 * BasicVersion is the default and probably only implementation of the
 * Version interface.
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
public class BasicBookMetaData implements BookMetaData
{
    /**
     * Basic constructor
     */
    protected BasicBookMetaData(String name, String edition, String initials, Date pub, int open, URL licence)
    {
        this.name = name;
        this.edition = edition;
        this.pub = pub;
        this.open = open;
        this.licence = licence;

        if (initials == null || initials.trim().length() == 0)
            this.initials = StringUtil.getInitials(name);
        else
            this.initials = initials;
    }

    /**
     * The name of the version, for example "King James Version" or
     * "Bible in Basic English" or "Greek". In general it should be
     * possible to deduce the initials from the name by removing all the
     * non-capital letters.
     * @return The name of this version
     */
    public String getName()
    {
        return name;
    }

    /**
     * The edition of this version, for example "Anglicised" (NIV),
     * "Stephanus" (Greek). For 2 versions to be equal both the name and
     * the edition must be equal. In general the text returned by this
     * method should not include the word "Edition"
     * @return The name of the edition
     */
    public String getEdition()
    {
        return edition;
    }

    /**
     * The full name including edition of the version, for example
     * "New International Version, Anglicised". The format is "name, edition"
     * @return The full name of this version
     */
    public String getFullName()
    {
        return VersionFactory.getFullName(getName(), getEdition());
    }

    /**
     * Do the 2 versions have matching names and editions.
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
        BookMetaData that = (BookMetaData) obj;
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
     * The initials of the version - how most people will know it, for
     * example "NIV", "KJV"
     * @return The versions initials
     */
    public String getInitials()
    {
        return initials;
    }

    /**
     * The date of first publishing. This does not need to be accurate and
     * 2 versions can be considered equal even if they have different
     * first publishing dates for that reason. In general "1 Jan 1970"
     * means published in 1970, and so on.
     * @return The date of first publishing
     */
    public Date getFirstPublished()
    {
        return pub;
    }

    /**
     * Is this version sold for commercial profit like the NIV, or kept
     * open like the NET version.
     * @return A STATUS_* constant
     */
    public int getOpenness()
    {
        return open;
    }

    /**
     * Not sure about this one - Do we need a way of getting at the dist.
     * licence? Are we going to be able to tie it down to a single Version
     * policy like this?
     * @return String detailing the users right to distribute this version
     */
    public URL getLicence()
    {
        return licence;
    }

    /**
     * The name of the version
     */
    private String name;

    /**
     * The edition of this version
     */
    private String edition;

    /**
     * The common initials of the version name
     */
    private String initials;

    /**
     * The approximate date of first publishing
     */
    private Date pub;

    /**
     * The openness of the version
     */
    private int open;

    /**
     * The URL of the distribution licence
     */
    private URL licence;
}
