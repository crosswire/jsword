
package org.crosswire.common.util.event;

import java.io.*;

import org.crosswire.common.util.*;

/**
 * This class listens to Logger messages and copies them to a
 * file.
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
public class FileCaptureListener implements CaptureListener
{
    /**
     * Called whenever Reporter.informUser() is passed an Exception
     * @param ev Object describing the exception
     */
    public void captureMessage(CaptureEvent ev)
    {
        try
        {
            if (out == null)
                out = new PrintWriter(new FileWriter(filename));

            out.println(ev.getMessage());
        }
        catch (IOException ex)
        {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * Called whenever Reporter.informUser() is passed an Exception
     * @param ev Object describing the exception
     */
    public void captureException(CaptureEvent ev)
    {
        try
        {
            if (out == null)
                out = new PrintWriter(new FileWriter(filename));

            out.println(ev.getException().getMessage());
        }
        catch (IOException ex)
        {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * What file are we writing to?
     * @param filename The name of the file to open for output
     */
    public void setOutputFilename(String filename)
    {
        this.filename = filename;

        if (out != null)
            out.close();

        out = null;
    }

    /**
     * What file are we writing to?
     * @param filename The name of the file to open for output
     */
    public String getOutputFilename()
    {
        return filename;
    }

    /** The stream to log to */
    private PrintWriter out = null;

    /** The name of the file that we log to */
    private String filename = default_filename;

    /**
     * What file are we writing to?
     * @param filename The name of the file to open for output
     */
    public static void setDefaultOutputFilename(String default_filename)
    {
        FileCaptureListener.default_filename = default_filename;
    }

    /**
     * What file are we writing to?
     * @param filename The name of the file to open for output
     */
    public static String getDefaultOutputFilename()
    {
        return default_filename;
    }

    /**
     * You must call setHelpDeskListener() in order to start logging
     * messages sent to the Log, and in order to properly
     * close this class you must call it again (with false).
     * @param joined Are we listening to the Log
     */
    public static void setHelpDeskListener(boolean joined)
    {
        if (joined && li == null)
        {
            li = new FileCaptureListener();
            Logger.addLogCaptureListener(li);
        }

        if (!joined && li != null)
        {
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

    /** The name of the file that we log to */
    private static String default_filename = "log.txt";

    /** The listener that pops up the ExceptionPanes */
    private static FileCaptureListener li = null;
}
