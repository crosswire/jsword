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
 * © CrossWire Bible Society, 2011 - 2016
 *
 */
package org.crosswire.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Version is an immutable representation of dotted "number" consisting of 1 to 4 parts.
 * 
 * <p>
 * Here is the grammar for version strings:
 * </p>
 * <pre>
 * version ::= major('.'minor('.'micro('.'nano)?)?)?
 * major ::= [0-9]+
 * minor ::= [0-9]+
 * micro ::= [0-9]+
 * nano  ::= [0-9]+
 * </pre>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class Version implements Comparable<Version> {
    public static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)(?:.(\\d+))?(?:.(\\d+))?(?:.(\\d+))?$");

    /**
     * Created a version identifier from the specified string.
     * 
     * @param version String representation of the version identifier.
     * @throws IllegalArgumentException If <code>version</code> is improperly
     *         formatted.
     */
    public Version(String version) {
        if (version == null) {
            throw new IllegalArgumentException("Null version not allowed.");
        }
        this.original = version;
        this.parts = new int[] { -1, -1, -1, -1 };
        Matcher matcher = VERSION_PATTERN.matcher(this.original);
        if (matcher.matches()) {
            int count = matcher.groupCount();
            for (int i = 1; i <= count; i++) {
                String part = matcher.group(i);
                if (part == null) {
                    break;
                }
                parts[i - 1] = Integer.parseInt(part);
            }
        } else {
            throw new IllegalArgumentException("invalid: " + version);
        }
    }

    /**
     * Returns the original string representation of this version identifier.
     *
     * @return The original string representation of this version identifier.
     */
    @Override
    public String toString() {
        return original;
    }

    @Override
    public int hashCode() {
        return original.hashCode();
    }

    /**
     * Compares this <code>Version</code> object to another object.
     * 
     * <p>
     * A version is considered to be equal to another version if all the
     * parts are equal.</p>
     * 
     * @param object The <code>Version</code> object to be compared.
     * @return true if the two objects are equal.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Version)) {
            return false;
        }

        Version that = (Version) object;
        if (that == this) {
            return true;
        }

        for (int i = 0; i < parts.length; i++) {
            if (parts[i] != that.parts[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compares this <code>Version</code> object to another object.
     * 
     * <p>
     * The comparison considers each of the parts (major, minor, micro, nano) in turn,
     * comparing like with like. At any point the comparison is not equal, a result is
     * known.
     * </p>
     * 
     * @param object The <code>Version</code> object to be compared.
     * @return A negative integer, zero, or a positive integer if this object is
     *         less than, equal to, or greater than the specified
     *         <code>Version</code> object.
     * @throws ClassCastException If the specified object is not a <code>Version</code>.
     */
    public int compareTo(Version object) {
        if (object == this) {
            return 0;
        }

        for (int i = 0; i < parts.length; i++) {
            int result = parts[i] - object.parts[i];
            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    private final String         original;
    private final int[]          parts;

}
