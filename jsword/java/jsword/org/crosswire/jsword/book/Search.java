
package org.crosswire.jsword.book;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
 * Search is a way of specifying what we are searching for.
 * 
 * We are using a class rather than a simple String so we can add meta-data to
 * the search.
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
public class Search
{
    /**
     * Simple constructor.
     * @param search the string to find
     * @param match Are we doing a best match search or a boolean style search
     */
    public Search(String search, boolean match)
    {
        this.search = search;
        this.match = match;
    }

    /**
     * Accessor for the string to search for.
     * @return String
     */
    public String getMatch()
    {
        return search;
    }

    /**
     * Accessor for the string to search for.
     * @return String
     */
    public boolean isBestMatch()
    {
        return match;
    }

    /**
     * Returns the restriction.
     * @return Passage
     */
    public Passage getRestriction()
    {
        return restriction;
    }

    /**
     * Sets the restriction. To unset a restriction, use a restriction of null. 
     * @param restriction The restriction to set
     */
    public void setRestriction(Passage restriction)
    {
        if (restriction == null)
            this.restriction = UNRESTRICTED;
        else
            this.restriction = restriction;
    }

    /**
     * Test to see is the restriction is equal to the constant UNRESTRICTED.
     */
    public boolean isRestricted()
    {
        return restriction != UNRESTRICTED;
    }

    /**
     * the whole Bible - ie no restrictions.
     */
    private static final Passage UNRESTRICTED = PassageFactory.getWholeBiblePassage(); 

    /**
     * default to no restrictions
     */
    private Passage restriction = UNRESTRICTED;

    /**
     * What are we looking for?
     */
    private String search;

    /**
     * Is this a best match search?
     */
    private boolean match;
}
