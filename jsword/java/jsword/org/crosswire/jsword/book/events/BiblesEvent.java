
package org.crosswire.jsword.book.events;

import java.util.EventObject;

import org.crosswire.jsword.book.BibleMetaData;

/**
 * A BiblesEvent is fired whenever a Bible is added or removed from the
 * system.
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
public class BiblesEvent extends EventObject
{
    /**
     * Basic constructor
     * @param name The name of the changed Bible, or null if there is more than one change.
     * @param added True if the changed Bible is an addition.
     */
    public BiblesEvent(Object source, BibleMetaData bmd, boolean added)
    {
        super(source);

        this.bmd = bmd;
        this.added = added;
    }

    /**
     * Get the name of the changed Bible
     * @return The Bible bmd
     */
    public BibleMetaData getBibleName()
    {
        return bmd;
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
    private BibleMetaData bmd;
}
