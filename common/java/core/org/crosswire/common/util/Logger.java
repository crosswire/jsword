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

import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * This class is very similar to Commons-Logging except it should be even
 * smaller and have an API closer to the Log4J API (and even J2SE 1.4 logging)
 * to help us to move over.
 * Having our own class will also help with re-factoring.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class Logger
{
    /**
     * Same as calling <code>getLogger(clazz.getName())</code>.
     */
    public static Logger getLogger(Class clazz)
    {
        return new Logger(clazz);
    }

    /**
     * Stop all logging output
     */
    public static void outputNothing()
    {
        java.util.logging.Logger.getLogger(ROOT_LOGGER).setLevel(Level.OFF);
    }

    /**
     * Output a minimum of stuff
     */
    public static void outputInfoMinimum()
    {
        java.util.logging.Logger.getLogger(ROOT_LOGGER).setLevel(Level.WARNING);
    }

    /**
     * Output everything
     */
    public static void outputEverything()
    {
        java.util.logging.Logger.getLogger(ROOT_LOGGER).setLevel(Level.FINEST);
    }

    /**
     * Simple ctor
     */
    private Logger(Class id)
    {
        logger = java.util.logging.Logger.getLogger(id.getName());
    }

    /**
     * Log a message object with the FATAL level.
     * @param message the message object to log.
     */
    public void fatal(String message)
    {
        doLogging(Level.SEVERE, message, null);
    }

    /**
     * Log a message object with the FATAL level.
     * @param message the message object to log.
     */
    public void fatal(String message, Throwable th)
    {
        doLogging(Level.SEVERE, message, th);
    }

    /**
     * Log a message object with the ERROR level.
     * @param message the message object to log.
     */
    public void error(String message)
    {
        doLogging(Level.WARNING, message, null);
    }

    /**
     * Log a message object with the ERROR level.
     * @param message the message object to log.
     */
    public void error(String message, Throwable th)
    {
        doLogging(Level.WARNING, message, th);
    }

    /**
     * Log a message object with the INFO level.
     * @param message the message object to log.
     */
    public void info(String message)
    {
        doLogging(Level.CONFIG, message, null);
    }

    /**
     * Log a message object with the INFO level.
     * @param message the message object to log.
     */
    public void info(String message, Throwable th)
    {
        doLogging(Level.CONFIG, message, th);
    }

    /**
     * Log a message object with the WARN level.
     * @param message the message object to log.
     */
    public void warn(String message)
    {
        doLogging(Level.INFO, message, null);
    }

    /**
     * Log a message object with the WARN level.
     * @param message the message object to log.
     */
    public void warn(String message, Throwable th)
    {
        doLogging(Level.INFO, message, th);
    }

    /**
     * Log a message object with the DEBUG level.
     * @param message the message object to log.
     */
    public void debug(String message)
    {
        logger.fine(message);
    }

    // Private method to infer the caller's class and method names
    private void doLogging(Level level, String message, Throwable th)
    {
        String className = null;
        String methodName = null;
        int lineNumber = -1;
        // Get the stack trace.
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        // First, search back to a method in the Logger class.
        int ix = 0;
        while (ix < stack.length)
        {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (cname.equals(CLASS_NAME))
            {
                break;
            }
            ix++;
        }
        // Now search for the first frame before the "Logger" class.
        while (ix < stack.length)
        {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (!cname.equals(CLASS_NAME))
            {
                // We've found the relevant frame.
                className = cname;
                methodName = frame.getMethodName();
                lineNumber = frame.getLineNumber();
                break;
            }
            ix++;
        }
        LogRecord logRecord = new LogRecord(level, message);
        logRecord.setLoggerName(logger.getName());
        logRecord.setSourceClassName(className);
        logRecord.setSourceMethodName(methodName);
        logRecord.setThrown(th);
        // This is a non-standard use of sequence number.
        // We could just subclass LogRecord and add line number.
        logRecord.setSequenceNumber(lineNumber);
        logger.log(logRecord);
    }

    static
    {
        // Establish a class that will load logging properties into java.util.logging.LogManager
        System.setProperty("java.util.logging.config.class", LogConfig.class.getName()); //$NON-NLS-1$
    }

    private static final String ROOT_LOGGER = ""; //$NON-NLS-1$
    private static final String CLASS_NAME = Logger.class.getName();
    private java.util.logging.Logger logger;
}
