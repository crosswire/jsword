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

/**
 * RedLetterText remembers when text should be red. Red Letter Text is used to
 * highlight the words of Jesus.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * 
 */
public class RedLetterText {
    private int rlt;

    /**
     * Construct a RedLetterText.
     */
    public RedLetterText() {
        rlt = 0;
    }

    /**
     * Call when Red Letter Text is entered
     * 
     */
    public void enter() {
        rlt++;
    }

    /**
     * Call when Red Letter Text is left
     * 
     */
    public void leave() {
        if (rlt > 0) {
            rlt--;
        }
    }

    /**
     * Returns true when one is in Red Letter Text
     * 
     * @return true if in RLT, false otherwise
     */
    public boolean isRLT() {
        return rlt > 0;
    }
}
