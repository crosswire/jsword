package org.crosswire.common.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;

/**
 * This class is very similar to Commons-Logging except it should be even
 * smaller and have an API closer to the Log4J API (and even J2SE 1.4 logging)
 * to help us to move over.
 * Having our own class will also help with re-factoring.
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
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.ERROR);
    }

    /**
     * Stop all logging output
     */
    public static void outputInfoMinimum()
    {
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
    }

    /**
     * Stop all logging output
     */
    public static void outputEverything()
    {
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG);
    }

    /**
     * Simple ctor
     */
    private Logger(Class id)
    {
        log4j = org.apache.log4j.Logger.getLogger(id);

        Object check = loggers.get(id);
        if (check != null)
        {
            log4j.error("Logger reuse for: " + id.getName()); //$NON-NLS-1$
            log4j.debug("Javascript creates a new classloader so this might not be a problem"); //$NON-NLS-1$
        }

        loggers.put(id, this);

        // This can be useful in tracking down Logger reuse
        // ex = new Exception();
        // ex.fillInStackTrace();
    }

    /**
     * Log a message object with the FATAL level.
     * @param message the message object to log.
     */
    public void fatal(String message)
    {
        log4j.fatal(message);
    }

    /**
     * Log a message object with the FATAL level.
     * @param message the message object to log.
     */
    public void fatal(String message, Throwable th)
    {
        log4j.fatal(message, th);
    }

    /**
     * Log a message object with the ERROR level.
     * @param message the message object to log.
     */
    public void error(String message)
    {
        log4j.error(message);
    }

    /**
     * Log a message object with the ERROR level.
     * @param message the message object to log.
     */
    public void error(String message, Throwable th)
    {
        log4j.error(message, th);
    }

    /**
     * Log a message object with the INFO level.
     * @param message the message object to log.
     */
    public void info(String message)
    {
        log4j.info(message);
    }

    /**
     * Log a message object with the INFO level.
     * @param message the message object to log.
     */
    public void info(String message, Throwable th)
    {
        log4j.info(message, th);
    }

    /**
     * Log a message object with the WARN level.
     * @param message the message object to log.
     */
    public void warn(String message)
    {
        log4j.warn(message);
    }

    /**
     * Log a message object with the WARN level.
     * @param message the message object to log.
     */
    public void warn(String message, Throwable th)
    {
        log4j.warn(message, th);
    }

    /**
     * Log a message object with the DEBUG level.
     * @param message the message object to log.
     */
    public void debug(String message)
    {
        log4j.debug(message);
    }

    /**
     * Check whether this category is enabled for the <code>DEBUG</code> Level.
     */
    public boolean isDebugEnabled()
    {
        return log4j.isDebugEnabled();
    }

    private static Map loggers = new HashMap();
    private org.apache.log4j.Logger log4j = null;
    // private Exception ex;
}
