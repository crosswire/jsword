
package org.crosswire.jsword.passage;

import java.util.EventObject;

/**
 * Defines an event that encapsulates changes to a Passage. For many
 * operations on a Passage, calculating the extent of the changes is
 * hard. In these cases we default the range to Gen 1:1-Rev 22:21
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
public class PassageEvent extends EventObject 
{
    /**
    * Constructs a PassageEvent object.
    * @param source the source Object (typically <code>this</code>)
    * @param type an int specifying VERSES_CHANGED, VERSES_ADDED, VERSES_REMOVED
    * @param lower an int specifying the bottom of a range
    * @param upper an int specifying the top of a range
    */
    public PassageEvent(Object source, int type, Verse lower, Verse upper)
    {
        super(source);

        this.type = type;
        this.lower = lower;
        this.upper = upper;

        if (this.lower == null) this.lower = VERSE_LOWEST;
        if (this.upper == null) this.upper = VERSE_HIGHEST;
    }

    /**
    * Returns the event type. The possible values are:
    * <ul>
    * <li>VERSES_CHANGED
    * <li>VERSES_ADDED
    * <li>VERSES_REMOVED
    * </ul>
    * @return an int representing the type value
    */
    public int getType()
    {
        return type;
    }

    /**
    * Returns the lower index of the range. For a single element,
    * this value is the same as that returned by {@link #getUpperIndex}.
    * @return an int representing the lower index value
    */
    public Verse getLowerIndex()
    {
        return lower;
    }

    /**
    * Returns the upper index of the range. For a single element,
    * this value is the same as that returned by {@link #getLowerIndex}.
    * @return an int representing the upper index value
    */
    public Verse getUpperIndex()
    {
        return upper;
    }

    /**
     * Identifies one or more changes in the lists contents.
     */
    public static final int VERSES_CHANGED = 0;

    /**
     * Identifies the addition of one or more contiguous items to the list
     */
    public static final int VERSES_ADDED = 1;

    /**
     * Identifies the removal of one or more contiguous items from the list
     */
    public static final int VERSES_REMOVED = 2;

    /**
     * When the lower verse is null
     * @label highest
     */
    public static final Verse VERSE_LOWEST = new Verse(1, 1, 1, true);

    /**
     * When the upper verse is null
     * @label lowest
     */
    public static final Verse VERSE_HIGHEST = new Verse(66, 22, 21, true);

    /**
     * The type of change
     */
    private int type;

    /**
     * The lowest numbered element to have changed
     * @label lower
     */
    private Verse lower;

    /**
     * The highest numbered element to have changed
     * @label upper
     */
    private Verse upper;
    
    /**
     * To get around exceptions that can't happen we used the patch_up version
     * of the Verse constructor above. Cafe can't compile this otherwise.
     * JDK and MS SDK seem fine on it though.
    static
    {
        try
        {
            VERSE_LOWEST = new Verse(1, 1, 1);
            VERSE_HIGHEST = new Verse(66, 22, 21);
        }
        catch (NoSuchVerseException ex)
        {
            throw new Error(PassageUtil.getResource("error_logic"));
        }
    }
    */
}

