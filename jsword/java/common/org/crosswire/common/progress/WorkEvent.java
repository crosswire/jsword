
package org.crosswire.common.progress;

import java.util.EventObject;


/**
 * A WorkEvent happens whenever a MutableBook makes some progress
 * in generating a new Bible.
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
public class WorkEvent extends EventObject
{
    /**
     * Initialize a WorkEvent
     */
    public WorkEvent(Job source, String desc, int percent)
    {
        super(source);

        this.desc = desc;
        this.percent = percent;
        this.finished = false;
    }

    /**
     * Initialize a WorkEvent that is just finishing
     */
    public WorkEvent(Job source, String desc, boolean finished)
    {
        super(source);

        if (!finished)
        {
            throw new IllegalArgumentException("finished must be true");
        }

        this.desc = desc;
        this.percent = 100;
        this.finished = true;
    }

    /**
     * Accessor for the Job
     */
    public Job getJob()
    {
        return (Job) getSource();
    }

    /**
     * Shortcut to check if percent == 100
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Get the total percent progress
     * @return The progress
     */
    public int getPercent()
    {
        return percent;
    }

    /**
     * Get a short descriptive phrase
     * @return The description
     */
    public String getDescription()
    {
        return desc;
    }

    /**
     * Have we just finished?
     */
    private boolean finished;

    /**
     * The total progress
     */
    private int percent;

    /**
     * A short descriptive phrase
     */
    private String desc;
}
