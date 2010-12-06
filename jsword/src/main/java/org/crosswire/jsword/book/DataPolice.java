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
package org.crosswire.jsword.book;

import java.util.logging.Level;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.Key;

/**
 * When we can't convert some source data then the user doesn't really care and
 * just wants it to work, but it would be good to have some way to get the
 * problems fixed, so as a start point we report them through this class.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class DataPolice {
    /**
     * Prevent instantiation
     */
    private DataPolice() {
    }

    /**
     * Set the current book to enhance error reports
     * 
     * @param bmd
     *            the book to report.
     */
    public static void setBook(BookMetaData bmd) {
        DataPolice.bmd = bmd;
    }

    /**
     * Set the current item to enhance error reports
     */
    public static void setKey(Key key) {
        DataPolice.key = key;
    }

    /**
     * Set the current level at which to report problems. Problems at too fine
     * grain a level might be filtered by default.
     * 
     * @param lev
     *            the level to set
     */
    public static void setReportingLevel(Level lev) {
        DataPolice.level = lev;
    }

    /**
     * Set the level at which to show problems.
     * 
     * @param lev
     *            the level to set
     */
    public static void setLevel(Level lev) {
        log.setLevel(lev);
    }

    /**
     * Report a message against the current book and key.
     * 
     * @param lev
     *            the level at which to report
     * @param message
     *            the police report.
     */
    public static void report(Level lev, String message) {
        StringBuilder buf = new StringBuilder();
        if (bmd != null) {
            buf.append(bmd.getInitials());
        }
        if (bmd != null && key != null) {
            buf.append(':');
            log.debug(bmd.getInitials() + ':' + key.getName());
        }
        if (key != null) {
            buf.append(key.getName());
        }
        buf.append(": ");
        buf.append(message);
        log.log(lev, buf.toString());
    }

    /**
     * Report a message against the current book and key.
     * 
     * @param message
     *            the police report.
     */
    public static void report(String message) {
        report(level, message);
    }

    /**
     * the last known item
     */
    private static Key key;

    /**
     * The last known Book
     */
    private static BookMetaData bmd;

    /**
     * The level at which to do logging. Default is FINE.
     */
    private static Level level = Level.FINE;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DataPolice.class, false);
}
