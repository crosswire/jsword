
package org.crosswire.jsword.book.basic;

import org.crosswire.jsword.book.WritableBible;
import org.crosswire.jsword.book.events.ProgressEvent;
import org.crosswire.jsword.book.events.ProgressListener;
import org.crosswire.common.util.EventListenerList;

/**
 * An AbstractWritableBible implements a few of the more generic methods of
 * WritableBible.
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
public abstract class AbstractWritableBible extends AbstractBible implements WritableBible
{
    /**
     * Add a progress listener to the list of things wanting
     * to know whenever we make some progress
     */
    public void addProgressListener(ProgressListener li)
    {
        listeners.add(ProgressListener.class, li);
    }

    /**
     * Remove a progress listener from the list of things wanting
     * to know whenever we make some progress
     */
    public void removeProgressListener(ProgressListener li)
    {
        listeners.remove(ProgressListener.class, li);
    }

    /**
     * Called to fire a ProgressEvent to all the Listeners, but only if
     * there is actual progress since last time.
     * @param percent The percentage of the way through that we are now
     */
    protected void fireProgressMade(String name, int percent)
    {
        if (this.percent == percent)
            return;

        this.percent = percent;

        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        ProgressEvent ev = null;
        for (int i=contents.length-2; i>=0; i-=2)
        {
            if (contents[i] == ProgressListener.class)
            {
                if (ev == null)
                    ev = new ProgressEvent(this, name, percent);

                ((ProgressListener) contents[i+1]).progressMade(ev);
            }
        }
    }

    /** The list of listeners */
    protected EventListenerList listeners = new EventListenerList();

    /** The current progress */
    protected int percent = -1;
}
