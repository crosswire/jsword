
package org.crosswire.jsword.book.events;

import java.util.EventObject;

/**
 * A BiblesEvent is fired whenever a Bible is added or removed from the
 * system.
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
 */
public class BiblesEvent extends EventObject
{
    /**
     * Basic constructor
     * @param name The name of the changed Bible, or null if there is more than
     *              one change.
     * @param added True if the changed Bible is an addition.
     */
    public BiblesEvent(Object source, String name, boolean added)
    {
        super(source);

        this.name = name;
        this.added = added;
    }

    /**
     * Get the name of the changed Bible
     * @return The Bible name
     */
    public String getBibleName()
    {
        return name;
    }

    /**
     * Is this an addition event?
     */
    public boolean isAddition()
    {
        return added;
    }

    /** Is this an addition event? */
    private boolean added;

    /** The name of the changed Bible */
    private String name;
}
