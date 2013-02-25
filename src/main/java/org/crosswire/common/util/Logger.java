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

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * This class is going away. It is a facade for slf4j, which itself is a facade
 * for an application's logger of choice.
 * 
 * This class is very similar to Commons-Logging except it should be even
 * smaller and have an API closer to the Log4J API (and even J2SE 1.4 logging).
 * 
 * This implementation is lazy. The actual internal logger is not initialized
 * until first use. Turns out that this class indirectly depends upon JSword's
 * Project class to help find the logging configuration file. If it is not lazy,
 * it looks in the wrong places for the configuration file.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class Logger {
    /**
     * Get a new logger for the class that shows the class, method and line
     * number of the caller.
     * @param clazz the class that holds the logger.
     */
    public static <T> Logger getLogger(Class<T> clazz) {
        return new Logger(clazz);
    }

    /**
     * Get a new logger for the class that shows the class of the caller.
     * @param clazz the class that holds the logger.
     * @param showLocation when true it will get the method and line where logging occurred.
     */
    public static <T> Logger getLogger(Class<T> clazz, boolean showLocation) {
        return new Logger(clazz);
    }

    /**
     * Set the level at which output occurs for this Logger.
     * 
     * @param newLevel
     *            the level to apply
     * @deprecated Use your underlying logger to set levels
     */
    @Deprecated
    public void setLevel(Level newLevel) {
        level = newLevel;
    }

    /**
     * Stop all logging output
     * @deprecated Use your underlying logger to set levels
     */
    @Deprecated
    public static synchronized void outputNothing() {
        level = Level.OFF;
    }

    /**
     * Output a minimum of stuff
     * @deprecated Use your underlying logger to set levels
     */
    @Deprecated
    public static synchronized void outputInfoMinimum() {
        level = Level.WARNING;
    }

    /**
     * Output everything
     * @deprecated Use your underlying logger to set levels
     */
    @Deprecated
    public static synchronized void outputEverything() {
        level = Level.ALL;
    }

    /**
     * Log a message object with the SEVERE level.
     * 
     * @param msg
     *            the message to log.
     * @deprecated Use {@link Logger#error(String)} instead.
     */
    @Deprecated
    public void fatal(String msg) {
        Logger.establishLogging();
        logger.error(msg);
    }

    /**
     * Log a message object with the SEVERE level.
     * 
     * @param msg
     *            the message object to log.
     * @deprecated Use {@link Logger#error(String, Throwable)} instead.
     */
    @Deprecated
    public void fatal(String msg, Throwable th) {
        Logger.establishLogging();
        logger.error(msg, th);
    }

    /**
     * Is the logger instance enabled for the ERROR level?
     *
     * @return True if this Logger is enabled for the ERROR level,
     *         false otherwise.
     */
    public boolean isErrorEnabled() {
        Logger.establishLogging();
        return logger.isErrorEnabled();
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    public void error(String msg) {
        Logger.establishLogging();
        logger.error(msg);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and argument.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level. </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public void error(String format, Object arg) {
        Logger.establishLogging();
        logger.error(format, arg);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level. </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public void error(String format, Object arg1, Object arg2) {
        Logger.establishLogging();
        logger.error(format, arg1, arg2);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the ERROR level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for ERROR. The variants taking
     * {@link #error(String, Object) one} and {@link #error(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void error(String format, Object... arguments) {
        Logger.establishLogging();
        logger.error(format, arguments);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public void error(String msg, Throwable th) {
        Logger.establishLogging();
        logger.error(msg, th);
    }

    /**
     * Is the logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level,
     *         false otherwise.
     */
    public boolean isInfoEnabled() {
        Logger.establishLogging();
        return logger.isInfoEnabled();
    }

    /**
     * Log a message at the INFO level.
     *
     * @param msg the message string to be logged
     */
    public void info(String msg) {
        Logger.establishLogging();
        logger.info(msg);
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and argument.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the INFO level. </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public void info(String format, Object arg) {
        Logger.establishLogging();
        logger.info(format, arg);
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the INFO level. </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public void info(String format, Object arg1, Object arg2) {
        Logger.establishLogging();
        logger.info(format, arg1, arg2);
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the INFO level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for INFO. The variants taking
     * {@link #info(String, Object) one} and {@link #info(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void info(String format, Object... arguments) {
        Logger.establishLogging();
        logger.info(format, arguments);
    }

    /**
     * Log an exception (throwable) at the INFO level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public void info(String msg, Throwable th) {
        Logger.establishLogging();
        logger.info(msg, th);
    }

    /**
     * Is the logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level,
     *         false otherwise.
     */
    public boolean isWarnEnabled() {
        Logger.establishLogging();
        return logger.isWarnEnabled();
    }

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    public void warn(String msg) {
        Logger.establishLogging();
        logger.warn(msg);
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and argument.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the WARN level. </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public void warn(String format, Object arg) {
        Logger.establishLogging();
        logger.warn(format, arg);
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the WARN level. </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public void warn(String format, Object arg1, Object arg2) {
        Logger.establishLogging();
        logger.warn(format, arg1, arg2);
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the WARN level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for WARN. The variants taking
     * {@link #warn(String, Object) one} and {@link #warn(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void warn(String format, Object... arguments) {
        Logger.establishLogging();
        logger.warn(format, arguments);
    }

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public void warn(String msg, Throwable th) {
        Logger.establishLogging();
        logger.debug(msg, th);
    }

    /**
     * Is the logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for the DEBUG level,
     *         false otherwise.
     */
    public boolean isDebugEnabled() {
        Logger.establishLogging();
        return logger.isDebugEnabled();
    }

    /**
     * Log a message at the DEBUG level.
     *
     * @param msg the message string to be logged
     */
    public void debug(String msg) {
        Logger.establishLogging();
        logger.debug(msg);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and argument.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level. </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public void debug(String format, Object arg) {
        Logger.establishLogging();
        logger.debug(format, arg);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level. </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public void debug(String format, Object arg1, Object arg2) {
        Logger.establishLogging();
        logger.debug(format, arg1, arg2);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the DEBUG level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for DEBUG. The variants taking
     * {@link #debug(String, Object) one} and {@link #debug(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public void debug(String format, Object... arguments) {
        Logger.establishLogging();
        logger.debug(format, arguments);
    }

    /**
     * Log an exception (throwable) at the DEBUG level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public void debug(String msg, Throwable t) {
        Logger.establishLogging();
        logger.debug(msg, t);
    }

    /**
     * Log a message with the supplied level.
     * 
     * @param lev
     *            the level at which to log.
     * @param msg
     *            the message to log.
     * @deprecated Use {@link Logger#error(String)},
     *                 {@link Logger#warn(String)},
     *                 {@link Logger#info(String)}, 
     *                 {@link Logger#debug(String)},
     *              or {@link Logger#trace(String)} 
     */
    @Deprecated
    public void log(Level lev, String msg) {
        if (lev == null) {
            throw new IllegalArgumentException("must supply a Level");
        }
        int intLevel = lev.intValue();
        if (intLevel == Level.OFF.intValue() || intLevel == Level.ALL.intValue()) {
            return;
        }
        if (intLevel >= Level.SEVERE.intValue()) {
            logger.error(msg);
        } else if (intLevel >= Level.WARNING.intValue()) {
            logger.warn(msg);
        } else if (intLevel >= Level.INFO.intValue()) {
            logger.info(msg);
        } else if (intLevel >= Level.FINE.intValue()) {
            logger.debug(msg);
        } else if (intLevel >= Level.FINEST.intValue()) {
            logger.trace(msg);
        }
    }

    /**
     * Log a message with the supplied level, recording the exception when not
     * null.
     * 
     * @param msg
     *            the message object to log.
     * @deprecated Use {@link Logger#error(String, Throwable)},
     *                 {@link Logger#warn(String, Throwable)},
     *                 {@link Logger#info(String, Throwable)}, 
     *                 {@link Logger#debug(String, Throwable)},
     *              or {@link Logger#trace(String, Throwable)} 
     */
    @Deprecated
    public void log(Level lev, String msg, Throwable th) {
        if (lev == null) {
            throw new IllegalArgumentException("must supply a Level");
        }
        int intLevel = lev.intValue();
        if (intLevel == Level.OFF.intValue() || intLevel == Level.ALL.intValue()) {
            return;
        }
        if (intLevel >= Level.SEVERE.intValue()) {
            logger.error(msg, th);
        } else if (intLevel >= Level.WARNING.intValue()) {
            logger.warn(msg, th);
        } else if (intLevel >= Level.INFO.intValue()) {
            logger.info(msg, th);
        } else if (intLevel >= Level.FINE.intValue()) {
            logger.debug(msg, th);
        } else { //if (intLevel >= Level.FINEST.intValue()) {
            logger.trace(msg, th);
        }
    }

    /**
     * Create a logger for the class. Wrapped by {@link org.slf4j.Logger#getLogger(Class)}.
     */
    private <T> Logger(Class<T> id) {
        this.logger = org.slf4j.LoggerFactory.getLogger(id);
    }

    // Private method to infer the caller's class and method names
//    private void doLogging(Level theLevel, String message, Throwable th) {
//        // now check whether we should do any work
//        if (!shouldLog(theLevel)) {
//            return;
//        }
//
//        LogRecord logRecord = new LogRecord(theLevel, message);
//        logRecord.setLoggerName(logger.getName());
//        logRecord.setSourceClassName(CallContext.getCallingClass(1).getName());
//        logRecord.setThrown(th);
//
//        if (showLocation && (showLocationForInfoDebugTrace || theLevel.intValue() > Level.INFO.intValue())) {
//            String methodName = null;
//            int lineNumber = -1;
//
//            // Get the stack trace.
//            StackTraceElement[] stack = (new Throwable()).getStackTrace();
//
//            // First, search back to a method in the Logger class.
//            int ix = 0;
//            while (ix < stack.length) {
//                StackTraceElement frame = stack[ix];
//                String cname = frame.getClassName();
//                if (cname.equals(CLASS_NAME)) {
//                    break;
//                }
//                ix++;
//            }
//
//            // Now search for the first frame with the name of the caller.
//            while (ix < stack.length) {
//                StackTraceElement frame = stack[ix];
//                if (!frame.getClassName().equals(CLASS_NAME)) {
//                    // We've found the relevant frame.
//                    methodName = frame.getMethodName();
//                    lineNumber = frame.getLineNumber();
//                    break;
//                }
//                ix++;
//            }
//
//            logRecord.setSourceMethodName(methodName);
//            // This is a non-standard use of sequence number.
//            // We could just subclass LogRecord and add line number.
//            logRecord.setSequenceNumber(lineNumber);
//        }
//
//        logger.log(logRecord);
//    }

    /**
     * Should log, returns true if theLevel should be logged by this logger. See {@link java.util.logging.Logger#isLoggable(Level) for more details}
     *
     * @param theLevel the the level
     * @return true, if successful
     * @deprecated Use {@link Logger#isErrorEnabled()},
     *                 {@link Logger#isWarnEnabled()},
     *                 {@link Logger#isInfoEnabled()}, 
     *                 {@link Logger#isDebugEnabled()},
     *              or {@link Logger#isTraceEnabled()} 
     */
    @Deprecated
    public boolean shouldLog(Level theLevel) {
        int levelValue = level.intValue();
        if (theLevel.intValue() < levelValue || levelValue == Level.OFF.intValue()) {
            return false;
        }
        return true;
    }

    public static void establishLogging() {
        if (established) {
            return;
        }
        established = true;

        Exception ex = null;
        try {
            InputStream cwConfigStream = ResourceUtil.getResourceAsStream("CWLogging.properties");
            LogManager.getLogManager().readConfiguration(cwConfigStream);
        } catch (SecurityException e) {
            ex = e;
        } catch (MissingResourceException e) {
            ex = e;
        } catch (IOException e) {
            ex = e;
        }
        if (ex != null) {
            cwLogger.info("Can't load CWLogging.properties", ex);
        }
    }

    /**
     * Sets the show location for debug and trace.
     *
     * @param enabled true to display the location on info, debug and trace as well as error and warn
     */
    public static void setShowLocationForInfoDebugTrace(boolean enabled) {
        //showLocationForInfoDebugTrace = enabled;
    }

    private static volatile boolean established;
    private static volatile Level level;

    /**
     * The actual logger.
     */
    private org.slf4j.Logger logger;
    private static Logger cwLogger = getLogger(Logger.class);

}
