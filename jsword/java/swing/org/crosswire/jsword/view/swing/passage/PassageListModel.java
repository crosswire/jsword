
package org.crosswire.jsword.view.swing.passage;

import javax.swing.AbstractListModel;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageEvent;
import org.crosswire.jsword.passage.PassageListener;

/**
 * The PassageListModel class gives access to a Passage via a
 * ListModel.
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
 * @see javax.swing.JList
 * @see javax.swing.AbstractListModel
 */
public class PassageListModel extends AbstractListModel implements PassageListener
{
    /**
     * Create a PassageListModel from a Passage. We also specify whether
     * to list the individual verses using the constant
     * <code>PassageListModel.LIST_VERSES</code> or to list ranges using
     * <code>PassageListModel.LIST_RANGES</code>.
     * @param ref The reference that we are modeling
     * @param mode The verse/range mode
     * @exception IllegalArgumentException If the mode is illegal
     */
    public PassageListModel(Passage ref, int mode)
    {
        this.ref = ref;

        setMode(mode);
        setPassage(ref);
    }

    /**
     * Change the mode we are operating in. Must be one of:
     * <code>PassageListModel.LIST_VERSES</code> or
     * <code>PassageListModel.LIST_RANGES</code>.
     * @param mode The new operation mode
     * @exception IllegalArgumentException If the mode is illegal
     */
    public void setMode(int mode)
    {
        if (mode != LIST_VERSES && mode != LIST_RANGES)
            throw new IllegalArgumentException(""+mode);

        this.mode = mode;
    }

    /**
     * Return the mode we are operating in.
     * @return The operation mode
     */
    public int getMode()
    {
        return mode;
    }

    /**
     * Returns the length of the list.
     * @return The number of verses/ranges in the list
     */
    public int getSize()
    {
        if (ref == null)
        {
            return 0;
        }

        if (mode == LIST_RANGES)
        {
            return ref.countRanges();
        }

        return ref.countVerses();
    }

    /**
     * Returns the value at the specified index.
     * @param index The index (based at 0) of the element to fetch
     * @return The required verse/range
     */
    public Object getElementAt(int index)
    {
        if (ref == null)
        {
            return null;
        }

        if (mode == LIST_RANGES)
        {
            return ref.getVerseRangeAt(index);
        }
        
        return ref.getVerseAt(index);
    }

    /**
     * Sent after stuff has been added to the Passage.
     * More info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    public void versesAdded(PassageEvent ev)
    {
        fireContentsChanged(ev.getSource(), 0, getSize());
        // it would be good to be able to do something like:
        // fireIntervalAdded(ev.getSource(), ev.getLowerIndex(), ev.getUpperIndex());
    }

    /**
     * Sent after stuff has been removed from the Passage.
     * More info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    public void versesRemoved(PassageEvent ev)
    {
        fireContentsChanged(ev.getSource(), 0, getSize());
        // it would be good to be able to do something like:
        // fireIntervalRemoved(ev.getSource(), ev.getLowerIndex(), ev.getUpperIndex());
    }

    /**
     * Sent after verses have been simultaneously added and removed from the Passage.
     * More info about what and where can be had from the Event
     * @param ev a PassageEvent encapuslating the event information
     */
    public void versesChanged(PassageEvent ev)
    {
        fireContentsChanged(ev.getSource(), 0, getSize());
        // it would be good to be able to do something like:
        // fireContentsChanged(ev.getSource(), ev.getLowerIndex(), ev.getUpperIndex());
    }

    /**
     * Accessor for the current passage
     */
    public void setPassage(Passage ref)
    {
        if (this.ref != null)
        {
            this.ref.removePassageListener(this);
        }

        if (ref != null)
        {
            ref.optimizeReads();
            ref.addPassageListener(this);
        }

        this.ref = ref;
        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Accessor for the current passage
     */
    public Passage getPassage()
    {
        return ref;
    }

    /** Constant to make us list individual verses not ranges */
    public static final int LIST_VERSES = 0;

    /** Constant to make us list verses in ranges */
    public static final int LIST_RANGES = 1;

    /** The Passage that we are modeling */
    private Passage ref;

    /** Are we modeling in groups or individually */
    private int mode;
}

