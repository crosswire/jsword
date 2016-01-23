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
package org.crosswire.jsword.book;

import org.crosswire.jsword.passage.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When we can't convert some source data then the user doesn't really care and
 * just wants it to work, but it would be good to have some way to get the
 * problems fixed, so as a start point we report them through this class.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class DataPolice {
    /**
     * Prevent instantiation
     */
    private DataPolice() {
    }

    /**
     * Report a message against the current book and key.
     * 
     * @param book
     *            the book against which to report
     * @param key
     *            the key against which to report
     * @param message
     *            the police report.
     */
    public static void report(Book book, Key key, String message) {
        if (!reporting) {
            return;
        }
        StringBuilder buf = new StringBuilder();
        BookMetaData bmd = book.getBookMetaData();
        if (bmd != null) {
            buf.append(bmd.getInitials());
        }
        if (bmd != null && key != null) {
            buf.append(':');
        }
        if (key != null) {
            buf.append(key.getOsisID());
        }
        buf.append(": ");
        buf.append(message);
        LOGGER.info(buf.toString());
    }

    /**
     * @return Returns whether to report found problems.
     */
    public static synchronized boolean isReporting() {
        return reporting;
    }

    /**
     * @param reporting
     *            Turn on reporting of found problems
     */
    public static synchronized void setReporting(boolean reporting) {
        DataPolice.reporting = reporting;
    }

    /**
     * Whether to report problems or not. By default, reporting is off.
     */
    private static boolean reporting;

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataPolice.class);
}
