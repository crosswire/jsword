
package org.crosswire.common.util.event;

import java.io.*;
import org.crosswire.common.util.*;

/**
 * This class listens to Reporter captures and copies them to a
 * stream.
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
public class StdOutCaptureListener implements CaptureListener, ReporterListener
{
    /**
     * Called whenever Reporter.informUser() is passed an Exception
     * @param ev The event describing the Exception
     */
    public void captureException(CaptureEvent ev)
    {
        println(ev.getException());
    }

    /**
     * Called whenever Reporter.informUser() is passed a message
     * @param ev The event describing the message
     */
    public void captureMessage(CaptureEvent ev)
    {
        out.println(ev.getMessage());
    }

    /**
     * Called whenever Reporter.informUser() is passed an Exception
     * @param ev The event describing the Exception
     */
    public void reportException(ReporterEvent ev)
    {
        println(ev.getException());
    }

    /**
     * Called whenever Reporter.informUser() is passed a message
     * @param ev The event describing the message
     */
    public void reportMessage(ReporterEvent ev)
    {
        out.println(ev.getMessage());
    }

    /**
     * Actually println the Throwable and recurse if needed
     * @param ex The exception to be displayed
     */
    private void println(Throwable ex)
    {
        ex.printStackTrace(out);

        if (ex instanceof LucidException)
        {
            LucidException lex = (LucidException) ex;
            Throwable nex = lex.getException();
            if (nex != null)
            {
                out.println();
                out.println("This was caused by:");
                println(nex);
            }
        }
    }

    /** The stream to log to */
    private PrintStream out = System.out;

    /**
     * You must call setHelpDeskListener() in order to start displaying
     * Exceptions sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     * @param joined Are we listening to the Log
     */
    public static void setHelpDeskInformListener(boolean joined)
    {
        if (joined && inform == null)
        {
            inform = new StdOutCaptureListener();
            Reporter.addReporterListener(inform);
        }

        if (!joined && inform != null)
        {
            Reporter.removeReporterListener(inform);
            inform = null;
        }
    }

    /**
     * Get the listening status
     */
    public static boolean getHelpDeskInformListener()
    {
        return (inform != null);
    }

    /**
     * You must call setHelpDeskListener() in order to start logging
     * messages sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     * @param joined Are we listening to the Log
     */
    public static void setHelpDeskLogListener(boolean joined)
    {
        if (joined && logger == null)
        {
            logger = new StdOutCaptureListener();
            Logger.addLogCaptureListener(logger);
        }

        if (!joined && logger != null)
        {
            Logger.removeLogCaptureListener(logger);
            logger = null;
        }
    }

    /**
     * Get the listening status
     */
    public static boolean getHelpDeskLogListener()
    {
        return (logger != null);
    }

    /** The listener for the logging service */
    private static StdOutCaptureListener logger = null;

    /** The listener for the inform service */
    private static StdOutCaptureListener inform = null;

    /**
     * Make the default to log exceptions to std out
     */
    static
    {
        // StdOutCaptureListener.setHelpDeskListener(true);
    }
}
