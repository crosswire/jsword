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
package org.crosswire.common.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crosswire.common.util.EventListenerList;

/**
 * Maintains a navigable history of objects.
 * This maintains a dated list of objects and
 * a current navigation list.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */

public class History
{
    /**
     * Create an empty navigation and history list.
     *
     */
    public History()
    {
        nav = new ArrayList();
        history = new HashMap();
        listeners = new EventListenerList();
    }

    /**
     * Make a particular element in the navigation list the current
     * item in history.
     * 
     * @param index the index of item to make the last one in the back list,
     *          -1 (or lower) will put everything in the forward list.
     *          Indexes beyond the end of the list will put everything
     *          in the back list.
     */
    public Object select(int index)
    {
        int i = index;

        // Adjust to be 1 based
        int size = nav.size();

        if (i > size)
        {
            i = size;
        }
        else if (i < 1)
        {
            i = 1;
        }

        // Only fire history changes when there is a change.
        if (i != backCount)
        {
            backCount = i;
            fireHistoryChanged();
        }

        return getCurrent();
    }

    /**
     * Add an element to history. If the element is in the forward list,
     * then it replaces everything in the forward list upto it.
     * Otherwise, it replaces the forward list.
     * 
     * @param obj the object to add
     */
    public void add(Object obj)
    {
        Object current = getCurrent();

        // Don't add null objects or the same object.
        if (obj == null || obj.equals(current))
        {
            return;
        }

        // If we are adding the next element, then just advance
        // otherwise ...
//        Object next = peek(1);
//        if (!obj.equals(next))
//        {
            int size = nav.size();
            if (size > backCount)
            {
                int pos = backCount;
                while (pos < size && !obj.equals(nav.get(pos)))
                {
                    pos++;
                }
                // At this point pos either == size or the element at pos matches what we are navigating to.
                nav.subList(backCount, Math.min(pos++, size)).clear();
            }

            // If it matches, then we don't have to do anything more
            if (!obj.equals(peek(1)))
            {
                // then we add it
                nav.add(backCount, obj);
            }
//        }

        backCount++;

        // and remember when we saw it
        visit(obj);

        fireHistoryChanged();
    }

    /**
     * Get all the elements in "back" list.
     * @return the elements in the back list.
     */
    public List getPreviousList()
    {
        if (backCount > 0)
        {
            return Collections.unmodifiableList(nav.subList(0, backCount));
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Get all the elements in the "forward" list.
     * @return the elements in the forward list.
     */
    public List getNextList()
    {
        if (backCount < nav.size())
        {
            return Collections.unmodifiableList(nav.subList(backCount, nav.size()));
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Increments the current history item by the given amount.
     * Positive numbers are forward. Negative numbers are back.
     * @param i the distance to travel
     * @return the item at the requested location, or
     *                  at the end of the list if i is too big, or
     *                  at the beginning of the list if i is too small, otherwise
     *                  null.
     */
    public Object go(int i)
    {
        return select(backCount + i);
    }

    /**
     * Get the current item in the "back" list
     * @return the current item in the back list.
     */
    public Object getCurrent()
    {
        if (nav.size() > 0 && backCount > 0)
        {
            return nav.get(backCount - 1);
        }
        return null;
    }

    /**
     * Get the current item in the "back" list
     * @param i the distance to travel
     * @return the requested item in the navigation list.
     */
    private Object peek(int i)
    {
        int size = nav.size();
        if (size > 0 && backCount > 0 && backCount + i <= size)
        {
            return nav.get(backCount + i - 1);
        }
        return null;
    }

    /**
     * Add a listener for history events.
     * @param li the interested listener
     */
    public synchronized void addHistoryListener(HistoryListener li)
    {
        listeners.add(HistoryListener.class, li);
    }

    /**
     * Remove a listener of history events.
     * @param li the disinterested listener
     */
    public synchronized void removeHistoryListener(HistoryListener li)
    {
        listeners.remove(HistoryListener.class, li);
    }

    /**
     * Note that this object has been seen at this time.
     * @param obj
     */
    private void visit(Object obj)
    {
        history.put(obj, new Long(System.currentTimeMillis()));
    }

    /**
     * Kick of an event sequence
     */
    private synchronized void fireHistoryChanged()
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        HistoryEvent ev = null;
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == HistoryListener.class)
            {
                if (ev == null)
                {
                    ev = new HistoryEvent(this);
                }

                ((HistoryListener) contents[i + 1]).historyChanged(ev);
            }
        }
    }

    /**
     * The elements that can be navigated.
     */
    private List nav;

    /**
     * A map of elements that have been seen so far to when they have been seen.
     */
    private Map history;

    /**
     * The number of elements in the "back" list.
     */
    private int backCount;

    /**
     * Listeners that are interested when history has changed.
     */
    private EventListenerList listeners;
}
