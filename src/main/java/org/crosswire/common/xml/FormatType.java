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
package org.crosswire.common.xml;

/**
 * The PrettySerializingContentHandler uses a FormatType to control its output.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public enum FormatType {
    AS_IS           (false, false, false),
    ANALYSIS        (true,  false, false),
    CLASSIC         (true,  false, true),
    ANALYSIS_INDENT (true,  true,  false),
    CLASSIC_INDENT  (true,  true,  true);

    /**
     * Simple ctor
     */
    FormatType(boolean displayNewlines, boolean doIndenting, boolean classicLines) {
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

    private boolean indented;
    private boolean multiline;
    private boolean analytic;
    private boolean classic;
}
