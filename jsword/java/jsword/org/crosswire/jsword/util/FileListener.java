
package org.crosswire.jsword.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.event.CaptureEvent;
import org.crosswire.common.util.event.CaptureListener;
import org.crosswire.common.util.event.ReporterEvent;
import org.crosswire.common.util.event.ReporterListener;

/**
 * The FileListener logs exceptions and log messages to a server log
 * file and assumes that we are runnging from within JSword so we
 * do not need to sort out filenames.
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
public class FileListener implements CaptureListener, ReporterListener
{
    /**
     * Basic constructor
     */
    public FileListener()
    {
        try
        {
            out = Project.resource().getLogFileWriter();
            err = Project.resource().getErrorFileWriter();
        }
        catch (IOException ex)
        {
            // Well what else are we supposed to do? We can't report to
            // the Log since it is delegating to us.
            ex.printStackTrace();
        }
    }

    /**
     * Called whenever Reporter.informUser() is passed an Exception
     * @param ev The event describing the Exception
     */
    public void captureException(CaptureEvent ev)
    {
        String time = tf.format(new Date());

        err.println(time+"\t"+ev.getSourceName());
        ev.getException().printStackTrace(err);
        err.flush();
    }

    /**
     * Called whenever Reporter.informUser() is passed a message
     * @param ev The event describing the message
     */
    public void captureMessage(CaptureEvent ev)
    {
        String time = tf.format(new Date());

        err.println(time+"\t"+ev.getSourceName()+"\t"+ev.getMessage());
        err.flush();
    }

    /**
     * Called whenever Reporter.informUser() is passed an Exception
     * @param ev The event describing the Exception
     */
    public void reportException(ReporterEvent ev)
    {
        String time = tf.format(new Date());

        err.println(time+"\t"+ev.getSourceName());
        ev.getException().printStackTrace(err);
        err.flush();
    }

    /**
     * Called whenever Reporter.informUser() is passed a message
     * @param ev The event describing the message
     */
    public void reportMessage(ReporterEvent ev)
    {
        String time = tf.format(new Date());

        err.println(time+"\t"+ev.getSourceName()+"\t"+ev.getMessage());
        err.flush();
    }

    /**
     * Called whenever Reporter.informUser() is passed an Exception
     * @param ev Object describing the exception
     */
    public void messageLogged(CaptureEvent ev)
    {
        String time = tf.format(new Date());

        out.println(time+"\t"+ev.getSourceName()+"\t"+ev.getMessage());
        out.flush();
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
            li = new FileListener();
            Reporter.addReporterListener(li);
            Logger.addLogCaptureListener(li);
        }

        if (!joined && li != null)
        {
            Reporter.removeReporterListener(li);
            Logger.removeLogCaptureListener(li);
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

    /** The listener */
    private static FileListener li = null;

    /** The output stream */
    private PrintWriter out;

    /** The output stream */
    private PrintWriter err;

    /** The Time formatter */
    private static final DateFormat tf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
}
