
package org.crosswire.jsword.book;

import java.net.URL;
import java.util.Date;

/**
 * A BookMetaData represents a method of translating the Bible. All Bibles with
 * the same BookMetaData should return identical text for any call to
 * <code>Bible.getText(VerseRange)</code>. The implication of this is that
 * there may be many instances of the Version "NIV", as there are several
 * different versions of the NIV - Original American-English, Anglicized,
 * and Inclusive Language editions at least.
 *
 * <p>BookMetaData like Strings must be compared using <code>.equals()<code>
 * instead of ==. A Bible must have the ability to handle a version
 * unknown to JSword. So Bibles must be able to add versions to the
 * system, and the system must cope with versions that already exist.</p>
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
public interface BookMetaData
{
    /**
     * The name of the version, for example "King James Version" or
     * "Bible in Basic English" or "Greek". In general it should be
     * possible to deduce the initials from the name by removing all the
     * non-capital letters.
     * @return The name of this version
     */
    public String getName();

    /**
     * The edition of this version, for example "Anglicised" (NIV),
     * "Stephanus" (Greek). For 2 versions to be equal both the name and
     * the edition must be equal. In general the text returned by this
     * method should not include the word "Edition"
     * @return The name of the edition
     */
    public String getEdition();

    /**
     * The full name including edition of the version, for example
     * "New International Version, Anglicised". The format is "name, edition"
     * @return The full name of this version
     */
    public String getFullName();

    /**
     * Do the 2 versions have matching names.
     * @param version The version to compare to
     * @return true if the names match
     */
    public boolean isSameFamily(BookMetaData version);

    /**
     * The initials of the version - how most people will know it, for
     * example "NIV", "KJV"
     * @return The versions initials
     */
    public String getInitials();

    /**
     * The date of first publishing. This does not need to be accurate and
     * 2 versions can be considered equal even if they have different
     * first publishing dates for that reason. In general "1 Jan 1970"
     * means published in 1970, and so on. <b>A null return from this
     * method is entirely valid</b> if the date of first publishing is not
     * known.
     * @return The date of first publishing
     */
    public Date getFirstPublished();

    /**
     * Is this version sold for commercial profit like the NIV, or kept
     * open like the NET version.
     * @return A STATUS_* constant
     */
    public Openness getOpenness();

    /**
     * Not sure about this one - Do we need a way of getting at the dist.
     * licence? Are we going to be able to tie it down to a single Version
     * policy like this?
     * @return String detailing the users right to distribute this version
     */
    public URL getLicence();
}
