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

import java.util.regex.Pattern;

import org.junit.Ignore;

/**
 * A set of utilities to help tests run properly
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
@Ignore
public class PlatformTestUtils {
    private PlatformTestUtils() {
    }
    /**
     * Simple helper to wrap around the pattern
     * @param result the string to be tested
     * @param prefixPattern the pattern
     * @return true if matches
     */
    public static boolean startsWith(String result, String prefixPattern) {
        return Pattern.compile(prefixPattern).matcher(result).find();
    }
}
