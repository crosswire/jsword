
package org.crosswire.common.util;

/**
 * This class is very similar to Commons-Logging except it should be even
 * smaller and have an API closer to the Log4J API (and even JDK 1.4 logging)
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Logger
{
    /**
     * Retrieve a logger by name.
     */
    public static Logger getLogger(String name)
    {
        return new Logger(name);
    }

    /**
     * Same as calling <code>getLogger(clazz.getName())</code>.
     */
    public static Logger getLogger(Class clazz)
    {
        return new Logger(clazz.getName());
    }

    /**
     * Retrieve the root logger.
     */
    public static Logger getRootLogger()
    {
        return new Logger("");
    }

    /**
     * 
     */
    public Logger(String message)
    {
    }

    /**
     * Log a message object with the DEBUG level.
     * @param message the message object to log.
     */
    public void debug(String message)
    {
    }

    /**
     * Log a message object with the DEBUG level.
     * @param message the message object to log.
     */
    public void debug(String message, Throwable th)
    {
    }

    /**
     * Log a message object with the ERROR level.
     * @param message the message object to log.
     */
    public void error(String message)
    {
    }

    /**
     * Log a message object with the ERROR level.
     * @param message the message object to log.
     */
    public void error(String message, Throwable th)
    {
    }

    /**
     * Log a message object with the FATAL level.
     * @param message the message object to log.
     */
    public void fatal(String message)
    {
    }

    /**
     * Log a message object with the FATAL level.
     * @param message the message object to log.
     */
    public void fatal(String message, Throwable th)
    {
    }

    /**
     * Log a message object with the INFO level.
     * @param message the message object to log.
     */
    public void info(String message)
    {
    }

    /**
     * Log a message object with the INFO level.
     * @param message the message object to log.
     */
    public void info(String message, Throwable th)
    {
    }

    /**
     * Log a message object with the WARN level.
     * @param message the message object to log.
     */
    public void warn(String message)
    {
    }

    /**
     * Log a message object with the WARN level.
     * @param message the message object to log.
     */
    public void warn(String message, Throwable th)
    {
    }

    /**
     * Check whether this category is enabled for the <code>DEBUG</code> Level.
     */
    public boolean isDebugEnabled()
    {
        return false;
    }
}
