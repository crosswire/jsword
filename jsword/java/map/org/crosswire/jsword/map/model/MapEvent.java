
package org.crosswire.jsword.map.model;

import java.util.EventObject;

import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.common.util.LogicError;

/**
* A MapEvent happens whenever a Map changes.
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
* @version D7.I6.T2
*/
public class MapEvent extends EventObject
{
    /**
    * Initialize a MapEvent
    * @param source The map that started this off
    * @param ord The verse ordinal that changed position
    */
    public MapEvent(Map source, int ord)
    {
        super(source);

        if (ord < 1 || ord > Books.versesInBible())
            throw new IllegalArgumentException("Invalid verse ordinal");

        this.ord = ord;
    }

    /**
    * Initialize a MapEvent
    * @param source The Object that started this off
    * @param command The command typed
    * @param lang The Progress that interprets this command
    */
    public MapEvent(Map source)
    {
        super(source);

        ord = -1;
    }

    /**
    * Get the verse ordinal that changed position or null if the whole
    * table changed
    * @return The progress
    */
    public int getChangedOrdinal()
    {
        return ord;
    }

    /**
    * Get the verse that changed position or null if the whole table
    * changed.
    * @return The progress
    */
    public Verse getChangedVerse()
    {
        if (ord == -1) return null;

        try
        {
            return new Verse(ord);
        }
        catch (NoSuchVerseException ex)
        {
            // This should not happen because of the check above
            throw new LogicError(ex);
        }
    }

    /** The ordinal number of the node that changed or -1 if the whole table changed */
    private int ord;
}
