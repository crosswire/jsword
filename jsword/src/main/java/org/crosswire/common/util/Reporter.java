/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.util.Properties;

/**
 * This package looks after Exceptions and messages as they happen. It would be
 * nice not to need this class - the principle being that any library that
 * encounters an error can throw an exception to indicate that there is a
 * problem. However this is not always the case. For example:
 * <ul>
 * <li>static class constructors should not throw, unless the class really is of
 * no use given the error, and yet we may want to tell the user that there was a
 * (non-critical) error.</li>
 * <li>Any library routine that works in a loop, applying some (potentially
 * failing) functionality, may want to continue the work without throwing in
 * response to a single error.</li>
 * <li>The class being implemented may implement an interface that disallows
 * nested exceptions and yet does not want to loose the root cause error
 * information. (This is the weakest of the above arguments, but probably still
 * valid.)</li> However in many of the times this class is used, this is the
 * reason:
 * <li>Within UI specific code - to throw up a dialog box (or whatever). Now
 * this use is currently tolerated, however it is probably a poor idea to use
 * GUI agnostic messaging in a GUI specific context. But I'm not bothered enough
 * to change it now. Specifically this use is deprecated because it makes the
 * app more susceptible to the configuration of the things that listen to
 * reports.</li>
 * </ul>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Reporter {
    /**
     * Enforce Singleton
     */
    private Reporter() {
    }

    /**
     * Something has gone wrong. We need to tell the user or someone, but we can
     * carry on. In general having caught an exception and passed it to
     * Reporter.informUser(), you should not throw another Exception. Called to
     * fire a commandEntered event to all the Listeners
     * 
     * @param source
     *            The cause of the problem, a Component if possible.
     * @param prob
     */
    public static void informUser(Object source, Throwable prob) {
        Class<?> cat = (source != null) ? source.getClass() : Reporter.class;
        Logger templog = Logger.getLogger(cat);

        templog.warn(prob.getMessage(), prob);

        fireCapture(new ReporterEvent(source, prob));
    }

    /**
     * Something has gone wrong. We need to tell the user or someone, but we can
     * carry on. In general having caught an exception and passed it to
     * Reporter.informUser(), you should not throw another Exception. Called to
     * fire a commandEntered event to all the Listeners
     * 
     * @param source
     *            The cause of the problem, a Component if possible.
     * @param prob
     *            The Exception that was thrown
     */
    public static void informUser(Object source, LucidException prob) {
        Class<?> cat = (source != null) ? source.getClass() : Reporter.class;
        Logger templog = Logger.getLogger(cat);

        templog.warn(prob.getMessage(), prob);

        fireCapture(new ReporterEvent(source, prob));
    }

    /**
     * Something has gone wrong. We need to tell the user or someone, but we can
     * carry on. In general having caught an exception and passed it to
     * Reporter.informUser(), you should not throw another Exception. Called to
     * fire a commandEntered event to all the Listeners
     * 
     * @param source
     *            The cause of the problem, a Component if possible.
     * @param prob
     *            The Exception that was thrown
     */
    public static void informUser(Object source, LucidRuntimeException prob) {
        Class<?> cat = (source != null) ? source.getClass() : Reporter.class;
        Logger templog = Logger.getLogger(cat);

        templog.warn(prob.getMessage(), prob);

        fireCapture(new ReporterEvent(source, prob));
    }

    /**
     * Something has happened. We need to tell the user or someone.
     * 
     * @param source
     *            The cause of the message, a Component if possible.
     * @param message
     *            The message to pass to the user
     */
    public static void informUser(Object source, String message) {
        log.debug(message);

        fireCapture(new ReporterEvent(source, message));
    }

    /**
     * Something has happened. We need to tell the user or someone.
     * 
     * <p>
     * Maybe we should have an extra parameter (or even several versions of this
     * method like log*()) that describes the severity of the message. A Sw*ng
     * listener could use this to decide the icon in the OptionPane for example.
     * </p>
     * 
     * @param source
     *            The cause of the message, a Component if possible.
     * @param message
     *            The message to pass to the user
     */
    public static void informUser(Object source, MsgBase message) {
        String msg = message.toString();
        log.debug(msg);

        fireCapture(new ReporterEvent(source, msg));
    }

    /**
     * Something has happened. We need to tell the user or someone.
     * 
     * <p>
     * Maybe we should have an extra parameter (or even several versions of this
     * method like log*()) that describes the severity of the message. A Sw*ng
     * listener could use this to decide the icon in the OptionPane for example.
     * </p>
     * 
     * @param source
     *            The cause of the message, a Component if possible.
     * @param message
     *            The message to pass to the user
     * @param param
     *            The parameters to the message
     */
    public static void informUser(Object source, MsgBase message, Object param) {
        String msg = message.toString(param);
        log.debug(msg);

        fireCapture(new ReporterEvent(source, msg));
    }

    /**
     * Something has happened. We need to tell the user or someone.
     * 
     * <p>
     * Maybe we should have an extra parameter (or even several versions of this
     * method like log*()) that describes the severity of the message. A Sw*ng
     * listener could use this to decide the icon in the OptionPane for example.
     * </p>
     * 
     * @param source
     *            The cause of the message, a Component if possible.
     * @param message
     *            The message to pass to the user
     * @param params
     *            The parameters to the message
     */
    public static void informUser(Object source, MsgBase message, Object[] params) {
        String msg = message.toString(params);
        log.debug(msg);

        fireCapture(new ReporterEvent(source, msg));
    }

    /**
     * Add an Exception listener to the list of things wanting to know whenever
     * we capture an Exception
     */
    public static void addReporterListener(ReporterListener li) {
        LISTENERS.add(ReporterListener.class, li);
    }

    /**
     * Remove an Exception listener from the list of things wanting to know
     * whenever we capture an Exception
     */
    public static void removeReporterListener(ReporterListener li) {
        LISTENERS.remove(ReporterListener.class, li);
    }

    /**
     * Log a message.
     */
    protected static void fireCapture(ReporterEvent ev) {
        // Guaranteed to return a non-null array
        Object[] liArr = LISTENERS.getListenerList();

        if (liArr.length == 0) {
            log.warn("Nothing to listen to report: message=" + ev.getMessage(), ev.getException());
        }

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = liArr.length - 2; i >= 0; i -= 2) {
            if (liArr[i] == ReporterListener.class) {
                ReporterListener li = (ReporterListener) liArr[i + 1];
                if (ev.getException() != null) {
                    li.reportException(ev);
                } else {
                    li.reportMessage(ev);
                }
            }
        }
    }

    /**
     * Sets the parent of any exception windows.
     */
    public static void grabAWTExecptions(boolean grab) {
        if (grab) {
            // register ourselves
            System.setProperty(AWT_HANDLER_PROPERTY, OUR_NAME);
        } else {
            // deregister ourselves
            String current = System.getProperty(AWT_HANDLER_PROPERTY);
            if (current != null && current.equals(OUR_NAME)) {
                Properties prop = System.getProperties();
                prop.remove(AWT_HANDLER_PROPERTY);
            }
        }
    }

    /**
     * The system property name for registering AWT exceptions
     */
    private static final String AWT_HANDLER_PROPERTY = "sun.awt.exception.handler";

    /**
     * The name of the class to register for AWT exceptions
     */
    private static final String OUR_NAME = CustomAWTExceptionHandler.class.getName();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Reporter.class);

    /**
     * The list of listeners
     */
    private static final EventListenerList LISTENERS = new EventListenerList();

    /**
     * A class to handle AWT caught Exceptions
     */
    public static final class CustomAWTExceptionHandler {
        /**
         * Its important that we have a no-arg ctor to make this work. So if we
         * ever create an arged ctor then we need to add: public
         * CustomAWTExceptionHandler() { }
         */

        /**
         * Handle AWT exceptions
         */
        public void handle(Throwable ex) {
            // Only allow one to be reported every so often.
            // This interval control was needed because AWT exceptions
            // were causing recursive AWT exceptions
            // and way too many dialogs were being thrown up on the screen.
            if (gate.open()) {
                // TRANSLATOR: Very frequent error condition: The program has encountered a severe problem and it is likely that the program is unusable.
                Reporter.informUser(this, new LucidException(UserMsg.gettext("Unexpected internal problem. You may need to restart."), ex));
            }
        }

        private static TimeGate gate = new TimeGate(2000);
    }
}
