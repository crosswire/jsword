
package org.crosswire.common.util;

import java.util.*;

import org.crosswire.common.util.event.*;

/**
 * This package looks after Exceptions as they happen.
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
public class Logger
{
    /**
     * Simple ctor for use only with getLogger(String)
     */
    protected Logger(String source)
    {
        this.source = source;
    }

    /**
     * Create a new Logger
     */
    public static Logger getLogger(String source)
    {
        Logger reply = (Logger) loggers.get(source);

        if (reply == null)
        {
            reply = new Logger(source);
            loggers.put(source, reply);
        }

        return reply;
    }

    /**
     * Log a message to a specific log level
     */
    public void log(int level, String message)
    {
        fireCapture(log_list, new CaptureEvent(source, message, level));
    }

    /**
     * Log an Exception but don't tell the user about it
     * @param prob What went wrong
     */
    public void log(int level, String message, Throwable prob)
    {
        if (prob instanceof ThreadDeath)
            throw (ThreadDeath) prob;

        fireCapture(log_list, new CaptureEvent(source, prob, level));
    }

    /**
     * Log a message
     * @param message The text message
     */
    public void finest(String message)
    {
        fireCapture(log_list, new CaptureEvent(source, message, Level.FINEST));
    }

    /**
     * Log a message
     * @param message The text message
     */
    public void finer(String message)
    {
        fireCapture(log_list, new CaptureEvent(source, message, Level.FINER));
    }

    /**
     * Log a message
     * @param message The text message
     */
    public void fine(String message)
    {
        fireCapture(log_list, new CaptureEvent(source, message, Level.FINE));
    }

    /**
     * Log a message
     * @param message The text message
     */
    public void info(String message)
    {
        fireCapture(log_list, new CaptureEvent(source, message, Level.INFO));
    }

    /**
     * Log a message
     * @param message The text message
     */
    public void config(String message)
    {
        fireCapture(log_list, new CaptureEvent(source, message, Level.CONFIG));
    }

    /**
     * Log a message
     * @param message The text message
     */
    public void warning(String message)
    {
        fireCapture(log_list, new CaptureEvent(source, message, Level.WARNING));
    }

    /**
     * Log a message
     * @param message The text message
     */
    public void severe(String message)
    {
        fireCapture(log_list, new CaptureEvent(source, message, Level.SEVERE));
    }

    /**
     * Log a message
     * @param source Where the message comes from
     * @param message The text message
     */
    protected static void fireCapture(EventListenerList list, CaptureEvent ev)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = list.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i=listeners.length-2; i>=0; i-=2)
        {
            if (listeners[i] == CaptureListener.class)
            {
                CaptureListener li = (CaptureListener) listeners[i+1];
                try
                {
                    if (ev.getException() != null)
                        li.captureException(ev);
                    else
                        li.captureMessage(ev);
                }
                catch (Throwable ex)
                {
                    if (ex instanceof ThreadDeath)
                        throw (ThreadDeath) ex;

                    list.remove(CaptureListener.class, li);

                    Reporter.informUser(li, ex);
                }
            }
        }
    }

    /**
     * Gets a short HTML description of an Exception for display in a
     * window
     */
    public static String getHTMLDescription(Throwable ex)
    {
        StringBuffer retcode = new StringBuffer();

        // The message in the exception
        String message = ex.getMessage();
        if (message == null || message.equals(""))
            message = "No description available";
        message = StringUtil.swap(message, "\n", "<br>");

        // The name of the exception
        String classname = ex.getClass().getName();
        int lastdot = classname.lastIndexOf('.');
        if (lastdot != -1)
            classname = classname.substring(lastdot+1);
        if (classname.endsWith("Exception") && classname.length() > "Exception".length())
            classname = classname.substring(0, classname.length() - "Exception".length());
        if (classname.endsWith("Error") && classname.length() > "Error".length())
            classname = classname.substring(0, classname.length() - "Error".length());
        classname = StringUtil.createTitle(classname);
        if (classname.equals("IO")) classname = "Input / Output";

        retcode.append("<font size=\"-1\"><strong>");
        retcode.append(classname);
        retcode.append("</strong></font><br>");
        retcode.append(message);

        // If this is a LucidException with a nested Exception
        if (ex instanceof LucidException)
        {
            Throwable nex = ((LucidException) ex).getException();
            if (nex != null)
            {
                retcode.append("<p><br><font size=\"-1\">This was caused by: </font>");
                retcode.append(getHTMLDescription(nex));
            }
        }

        return retcode.toString();
    }

    /**
     * Add an Exception listener to the list of things wanting
     * to know whenever we capture an Exception
     */
    public static void addLogCaptureListener(CaptureListener li)
    {
        log_list.add(CaptureListener.class, li);
    }

    /**
     * Remove an Exception listener from the list of things wanting
     * to know whenever we capture an Exception
     */
    public static void removeLogCaptureListener(CaptureListener li)
    {
        log_list.remove(CaptureListener.class, li);
    }

    /** The originator of the message */
    private String source;

    /** The list of generated Loggers */
    private static Hashtable loggers = new Hashtable();

    /** The list of listeners */
    protected static EventListenerList log_list = new EventListenerList();
}
