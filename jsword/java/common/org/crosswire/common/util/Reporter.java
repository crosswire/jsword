package org.crosswire.common.util;

/**
 * This package looks after Exceptions and messages as they happen. It would be
 * nice not to need this class - the principle being that any library that
 * encounters an error can throw an exception to indicate that there is a
 * problem. However this is not always the case. For example:
 * <li>static class constructors should not throw, unless the class really is
 *     of no use given the error, and yet we may want to tell the user that
 *     there was a (non-critical) error.</li>
 * <li>Any library routine that works in a loop, applying some (potentially
 *     failing) functionality, may want to continue the work without throwing
 *     in response to a single error.</li>
 * <li>The class being implemented may implement an interface that disallows
 *     nested exceptions and yet does not want to loose the root cause error
 *     information. (This is the weakest of the above arguements, but probably
 *     still valid.)</li>
 * However in many of the times this class is used, this is the reason:
 * <li>Within UI specific code - to throw up a dialog box (or whatever). Now
 *     this use is currently tollerated, however it is probably a poor idea to
 *     use GUI agnostic messaging in a GUI specific context. But I'm not
 *     bothered enough to change it now. Specifically this use is deprecated
 *     because it makes the app more susceptible to the configuration of the
 *     things that listen to reports.</li>
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
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Reporter
{
    /**
     * Enforce Singleton
     */
    private Reporter()
    {
    }

    /**
     * Something has gone wrong. We need to tell the user or someone, but
     * we can carry on. In general having caught an exception and passed
     * it to Reporter.informUser(), you should not throw another Exception.
     * Called to fire a commandEntered event to all the Listeners
     * @param source The cause of the problem, a Component if possible.
     * @param prob The Exception that was thrown
     */
    public static void informUser(Object source, Throwable prob)
    {
        Class cat = (source != null) ? source.getClass() : Reporter.class;
        Logger templog = Logger.getLogger(cat);

        templog.warn(prob.getMessage(), prob);
        if (prob instanceof ThreadDeath)
        {
            throw (ThreadDeath) prob;
        }

        fireCapture(new ReporterEvent(source, prob));
    }

    /**
     * Something has happened. We need to tell the user or someone.
     *
     * <p>Maybe we should have an extra parameter (or even several
     * versions of this method like log*()) that describes the severity
     * of the message. A Sw*ng listener could use this to decide the
     * icon in the OptionPane for example.</p>
     *
     * @param source The cause of the message, a Component if possible.
     * @param message The message to pass to the user
     */
    public static void informUser(Object source, String message)
    {
        log.debug(message);

        fireCapture(new ReporterEvent(source, message));
    }

    /**
     * Add an Exception listener to the list of things wanting
     * to know whenever we capture an Exception
     */
    public static void addReporterListener(ReporterListener li)
    {
        listeners.add(ReporterListener.class, li);
    }

    /**
     * Remove an Exception listener from the list of things wanting
     * to know whenever we capture an Exception
     */
    public static void removeReporterListener(ReporterListener li)
    {
        listeners.remove(ReporterListener.class, li);
    }

    /**
     * Log a message.
     */
    protected static void fireCapture(ReporterEvent ev)
    {
        // Guaranteed to return a non-null array
        Object[] liArr = listeners.getListenerList();

        if (liArr.length == 0)
        {
            log.warn("Nothing to listen to report: message="+ev.getMessage(), ev.getException());
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i=liArr.length-2; i>=0; i-=2)
        {
            if (liArr[i] == ReporterListener.class)
            {
                ReporterListener li = (ReporterListener) liArr[i+1];
                try
                {
                    if (ev.getException() != null)
                    {
                        li.reportException(ev);
                    }
                    else
                    {
                        li.reportMessage(ev);
                    }
                }
                catch (Exception ex)
                {
                    listeners.remove(CaptureListener.class, li);
        
                    log.warn("Dispatch failure", ex);
                }
            }
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Reporter.class);

    /**
     * The list of listeners
     */
    protected static final EventListenerList listeners = new EventListenerList();
}
