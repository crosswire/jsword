package org.crosswire.common.swing.desktop;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import org.crosswire.common.swing.desktop.event.ViewEventListener;

/**
 * Interface defining what is Viewable.
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
 * @see gnu.gpl.Licence
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public interface Viewable
{
    /**
     * Add a view to the set.
     */
    public abstract void addView(Component component);

    /**
     * Remove a view from the set.
     */
    public abstract void removeView(Component component);

    /**
     * Get a snapshot of the views as a collection.
     * @return the views
     */
    public abstract Collection getViews();

    /**
     * Get an iterator of a snapshot of views.
     * @return an iterator over the views.
     */
    public abstract Iterator iterator();

    /**
     * Copies all the views from the one layout to the other
     * @param other the other layout
     */
    public abstract void moveTo(ViewLayout other);

    /**
     * Close all the views. Note the policy is enforced that one view is kept.
     * This will keep the last one added.
     */
    public abstract void closeAll();

    /**
     * Close all the views but the one provided.
     * @param component the view that is to remain open.
     */
    public abstract void closeOthers(Component component);

    /**
     * Visit every view in the order that they were added.
     * @param visitor The visitor for the view
     */
    public abstract void visit(ViewVisitor visitor);

    /**
     * Update the title of the view. If the component does not
     * implement Titleable, then a generated title will be used.
     * @param component the component whose title is to be used
     */
    public abstract void updateTitle(Component component);

    /**
     * Returns the top view. If no view is the top, it returns the
     * first one added.
     */
    public abstract Component getSelected();

    /**
     * Find the view and select it.
     * @param component
     */
    public abstract void select(Component component);

    /**
     * The number of views held by this layout.
     * @return the number of views held by this layout
     */
    public abstract int getViewCount();

    /**
     * Get the view by position. Note that adding and removing views
     * changes the indexes of the views. Do not use this for iteration
     * as it is not thread safe.
     * @param i the index of the view
     * @return the requested view.
     */
    public abstract Component getView(int i);

    /**
     * Adds a view event listener for notification of any changes to the view.
     *
     * @param listener the listener
     */
    public abstract void addViewEventListener(ViewEventListener listener);

    /**
     * Removes a view event listener.
     *
     * @param listener the listener
     */
    public abstract void removeViewEventListener(ViewEventListener listener);
}
