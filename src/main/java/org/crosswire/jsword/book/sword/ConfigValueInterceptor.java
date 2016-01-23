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
 * © CrossWire Bible Society, 2014 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

/**
 * Intercepts values from the configuration before these are widely distributed to the rest of the application.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Chris Burrell
 */
public interface ConfigValueInterceptor {
    /**
     * Intercepts a value before distribution to the rest of the JSword library
     * @param bookName the initials of the book that is being intercepted
     * @param configEntryType the configuration entry type, describing which field is being accessed
     * @param value the value to be intercepted
     * @return the new value, if different
     */
    Object intercept(String bookName, ConfigEntryType configEntryType, Object value);
}
