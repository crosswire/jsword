
package org.crosswire.common.util.config;

import org.crosswire.common.config.choices.BooleanChoice;
import org.crosswire.common.util.event.StdOutCaptureListener;

/**
 * A Choice to configure if we log to std out.
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
 * @version D0.I0.T0
 */
public class StdOutCaptureInformChoice extends BooleanChoice
{
    /**
     * Get StdOut logging status
     */
    public boolean getBoolean()
    {
        return StdOutCaptureListener.getHelpDeskInformListener();
    }

    /**
     * Set StdOut logging status
     */
    public void setBoolean(boolean value)
    {
        StdOutCaptureListener.setHelpDeskInformListener(value);
    }

    /**
     * Help text
     */
    public String getHelpText()
    {
        return "Do we copy exceptions to the system output.";
    }

    /**
     * The priority which which we configure this item
     * @return A priority level
     */
    public int priority()
    {
        return PRIORITY_SYSTEM;
    }
}
