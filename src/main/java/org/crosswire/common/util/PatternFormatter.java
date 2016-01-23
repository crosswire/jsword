/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Formats a log entry by pattern.
 * 
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
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 * @author Joe Walker
 */
public class PatternFormatter extends Formatter {
    /**
     * Format the given LogRecord.
     * 
     * @param record
     *            the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record) {
        // Minimize memory allocations here.
        dat.setTime(record.getMillis());
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }

        String format = LogManager.getLogManager().getProperty(PatternFormatter.class.getName() + ".format");
        String loggerName = record.getLoggerName();
        Logger logger = LogManager.getLogManager().getLogger(loggerName);

        for (Logger aLogger = logger; aLogger != null; aLogger = aLogger.getParent()) {
            String property = null;
            String aLoggerName = aLogger.getName();

            if (aLoggerName != null) {
                property = LogManager.getLogManager().getProperty(aLoggerName + ".format");
            }

            if (property != null) {
                format = property;
                break;
            }
        }

        if (format == null) {
            format = DEFAULT_FORMAT;
        }

        return MessageFormat.format(format,
                dat, // 0
                record.getLoggerName(), // 1
                record.getLevel().getLocalizedName(), // 2
                formatMessage(record), // 3
                throwable, // 4
                record.getSourceClassName(), // 5
                record.getSourceMethodName(), // 6
                Long.valueOf(record.getSequenceNumber()), // 7
                lineSeparator // 8
        );
    }

    private Date dat = new Date();
    private static final String DEFAULT_FORMAT = "{1}({2}): {3}{8} {4}";

    // Line separator string. This is the value of the line.separator
    // property at the moment that the PatternFormatter was created.
    private String lineSeparator = System.getProperty("line.separator", "\r\n");
}
