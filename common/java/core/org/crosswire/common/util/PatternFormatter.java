package org.crosswire.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * Formats a log entry by pattern.
 * <p>
 * <ul>
 * <li>{0} is the Date</li>
 * <li>{1} is the name of the logger</li>
 * <li>{2} is the level of the record</li>
 * <li>{3} is the message</li>
 * <li>{4} is the throwable</li>
 * <li>{5} is the class name (typically the same as the logger's name)</li>
 * <li>{6} is the method name</li>
 * <li>{7} is the line number</li>
 * <li>{8} is the system supplied new line</li>
 * </ul>
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
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class PatternFormatter extends Formatter
{

    /**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record)
    {
        // Minimize memory allocations here.
        dat.setTime(record.getMillis());
        String throwable = ""; //$NON-NLS-1$
        if (record.getThrown() != null)
        {
            try
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
            }
            catch (Exception ex)
            {
                assert false;
            }
        }
        String format = LogManager.getLogManager().getProperty(PatternFormatter.class.getName() + ".format"); //$NON-NLS-1$
        String loggerName = record.getLoggerName();
        java.util.logging.Logger logger = LogManager.getLogManager().getLogger(loggerName);
        for (java.util.logging.Logger aLogger = logger; aLogger != null; aLogger = aLogger.getParent())
        {
            String property = null;
            String aLoggerName = aLogger.getName();
            if (aLoggerName != null)
            {
                property = LogManager.getLogManager().getProperty(aLoggerName + ".format"); //$NON-NLS-1$
            }
            if (property != null)
            {
                format = property;
                break;
            }
        }
        if (format == null)
        {
            format = DEFAULT_FORMAT;
        }
        Object[] args =
        {
                        dat, record.getLoggerName(), record.getLevel().getLocalizedName(), formatMessage(record), throwable, record.getSourceClassName(),
                        record.getSourceMethodName(), new Long(record.getSequenceNumber()), lineSeparator
        };
        StringBuffer text = new StringBuffer();
        formatter = new MessageFormat(format);
        formatter.format(args, text, null);
        return text.toString();
    }

    private Date dat = new Date();
    private static final String DEFAULT_FORMAT = "{1}({2}): {3}{8}"; //$NON-NLS-1$
    private MessageFormat formatter;

    // Line separator string.  This is the value of the line.separator
    // property at the moment that the PatternFormatter was created.
    private String lineSeparator = (String) java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator")); //$NON-NLS-1$

}
