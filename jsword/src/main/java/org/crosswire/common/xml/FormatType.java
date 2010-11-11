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
package org.crosswire.common.xml;

import java.io.Serializable;

/**
 * The PrettySerializingContentHandler uses a FormatType to control its output.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class FormatType implements Serializable {
    public static final FormatType AS_IS = new FormatType("AS_IS", false, false, false);
    public static final FormatType ANALYSIS = new FormatType("ANALYSIS", true, false, false);
    public static final FormatType CLASSIC = new FormatType("CLASSIC", true, false, true);
    public static final FormatType ANALYSIS_INDENT = new FormatType("ANALYSIS_INDENT", true, true, false);
    public static final FormatType CLASSIC_INDENT = new FormatType("CLASSIC_INDENT", true, true, true);

    /**
     * Simple ctor
     */
    public FormatType(String aName, boolean displayNewlines, boolean doIndenting, boolean classicLines) {
        name = aName;
        multiline = displayNewlines;
        // the following are true only if we add newlines.
        indented = doIndenting && multiline;
        classic = classicLines && multiline;
        analytic = !classicLines && multiline;
    }

    /**
     * Whether newlines are introduced into the document.
     * 
     * @return true if newlines are added to the document
     */
    public boolean isMultiline() {
        return multiline;
    }

    /**
     * Whether indents are introduced into the document.
     * 
     * @return true if indents are added to the document
     */
    public boolean isIndented() {
        return indented;
    }

    /**
     * Whether added whitespace is inside tags. Note, this does not change the
     * document.
     * 
     * @return true if whitespace is added inside tags of document
     */
    public boolean isAnalytic() {
        return analytic;
    }

    /**
     * Whether added whitespace is between tags. Note, this does change the
     * document as whitespace is added to either side of existing text.
     * 
     * @return true if whitespace is added inside tags of document
     */
    public boolean isClassic() {
        return classic;
    }

    /**
     * Get an integer representation for this FormatType
     */
    public int toInteger() {
        for (int i = 0; i < VALUES.length; i++) {
            if (equals(VALUES[i])) {
                return i;
            }
        }
        // cannot get here
        assert false;
        return -1;
    }

    /**
     * Lookup method to convert from a String
     */
    public static FormatType fromString(String name) {
        for (int i = 0; i < VALUES.length; i++) {
            FormatType obj = VALUES[i];
            if (obj.name.equalsIgnoreCase(name)) {
                return obj;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static FormatType fromInteger(int i) {
        return VALUES[i];
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return name;
    }

    /**
     * The name of the FormatType
     */
    private String name;
    private boolean indented;
    private boolean multiline;
    private boolean analytic;
    private boolean classic;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve() {
        return VALUES[obj];
    }

    private static final FormatType[] VALUES = {
            AS_IS, ANALYSIS, CLASSIC, ANALYSIS_INDENT, CLASSIC_INDENT,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3544385916136142129L;
}
