
package org.crosswire.jsword.book;

import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
 * PassageKey is a Key tailored for Bibles.
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
public class PassageKey implements Key
{
    /**
     * Create a PassageKey from a Passage
     * @param ref
     */
    public PassageKey(Passage ref)
    {
        this.ref = ref;
    }

    /**
     * Create a PassageKey from a Passage
     * @param ref
     */
    public PassageKey(String refstr) throws NoSuchVerseException
    {
        this.ref = PassageFactory.createPassage(refstr);
    }

    /**
     * Convert this Key to a String
     * @return a String version of the Key
     */
    public String getText()
    {
        return ref.getName();
    }

    /**
     * @return The stored Passage
     */
    public Passage getPassage()
    {
        return ref;
    }

    /**
     * The Passage that we are wrapping
     */
    private Passage ref = null;
}
