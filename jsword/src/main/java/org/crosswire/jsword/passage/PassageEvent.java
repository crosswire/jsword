/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.passage;

import java.util.EventObject;

import org.crosswire.jsword.versification.BibleBook;

/**
 * Defines an event that encapsulates changes to a Passage. For many operations
 * on a Passage, calculating the extent of the changes is hard. In these cases
 * we default the range to Gen 1:1-Rev 22:21
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PassageEvent extends EventObject {

    /**
     * Indicates what kind of change happened to a Passage.
     */
    public enum EventType {
        /**
         * Identifies one or more changes in the lists contents.
         */
       CHANGED,

        /**
         * Identifies the addition of one or more contiguous items to the list
         */
       ADDED,

        /**
         * Identifies the removal of one or more contiguous items from the list
         */
        REMOVED,
    }

    /**
     * Constructs a PassageEvent object.
     * 
     * @param source
     *            the source Object (typically <code>this</code>)
     * @param versesChanged
     *            an int specifying VERSES_CHANGED, VERSES_ADDED, VERSES_REMOVED
     * @param lower
     *            an int specifying the bottom of a range
     * @param upper
     *            an int specifying the top of a range
     */
    public PassageEvent(Object source, EventType versesChanged, Verse lower, Verse upper) {
        super(source);

        this.type = versesChanged;
        this.lower = lower;
        this.upper = upper;

        if (this.lower == null) {
            this.lower = VERSE_LOWEST;
        }
        if (this.upper == null) {
            this.upper = VERSE_HIGHEST;
        }
    }

    /**
     * Returns the event type. The possible values are:
     * <ul>
     * <li>VERSES_CHANGED
     * <li>VERSES_ADDED
     * <li>VERSES_REMOVED
     * </ul>
     * 
     * @return an int representing the type value
     */
    public EventType getType() {
        return type;
    }

    /**
     * Returns the lower index of the range. For a single element, this value is
     * the same as that returned by {@link #getUpperIndex()}.
     * 
     * @return an int representing the lower index value
     */
    public Verse getLowerIndex() {
        return lower;
    }

    /**
     * Returns the upper index of the range. For a single element, this value is
     * the same as that returned by {@link #getLowerIndex()}.
     * 
     * @return an int representing the upper index value
     */
    public Verse getUpperIndex() {
        return upper;
    }

    /**
     * When the lower verse is null
     */
    private static final Verse VERSE_LOWEST = new Verse(BibleBook.GEN, 1, 1);

    /**
     * When the upper verse is null
     */
    private static final Verse VERSE_HIGHEST = new Verse(BibleBook.REV, 22, 21);

    /**
     * The type of change
     */
    private EventType type;

    /**
     * The lowest numbered element to have changed
     */
    private Verse lower;

    /**
     * The highest numbered element to have changed
     */
    private Verse upper;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3906647492467898675L;
}
