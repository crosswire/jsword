
package org.crosswire.common.util.event;

import org.apache.log4j.Logger;
import org.crosswire.common.util.Reporter;

/**
 * This class listens to Reporter captures and copies them to a
 * log.
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
public class LogCaptureListener implements ReporterListener
{
    /**
     * Called whenever Reporter.informUser() is passed an Exception
     * @param ev The event describing the Exception
     */
    public void reportException(ReporterEvent ev)
    {
        Object source = ev.getSource();
        if (source == null) source = this;
        Logger log = Logger.getLogger(source.getClass());

        log.warn(ev.getMessage(), ev.getException());
    }

    /**
     * Called whenever Reporter.informUser() is passed a message
     * @param ev The event describing the message
     */
    public void reportMessage(ReporterEvent ev)
    {
        Object source = ev.getSource();
        if (source == null) source = this;
        Logger log = Logger.getLogger(source.getClass());

        log.warn(ev.getMessage());
    }

    /**
     * You must call setHelpDeskListener() in order to start displaying
     * Exceptions sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     * @param joined Are we listening to the Log
     */
    public static void setHelpDeskListener(boolean joined)
    {
        if (joined && li == null)
        {
            li = new LogCaptureListener();
            Reporter.addReporterListener(li);
        }

        if (!joined && li != null)
        {
            Reporter.removeReporterListener(li);
            li = null;
        }
    }

    /**
     * Get the listening status
     */
    public static boolean getHelpDeskListener()
    {
        return (li != null);
    }

    /** The listener that pops up the ExceptionPanes */
    private static LogCaptureListener li = null;
}
