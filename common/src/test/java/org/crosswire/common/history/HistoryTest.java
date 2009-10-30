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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.history;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class HistoryTest extends TestCase {
    public void testAdd() {
        History history = new History();
        assertEquals(null, history.getCurrent());
        history.add("a"); //$NON-NLS-1$
        assertEquals("a", history.getCurrent()); //$NON-NLS-1$
        history.add("b"); //$NON-NLS-1$
        assertEquals("[a, b]", history.getPreviousList().toString()); //$NON-NLS-1$
        // re-adding the current element won't change the list
        history.add("b"); //$NON-NLS-1$
        assertEquals("[a, b]", history.getPreviousList().toString()); //$NON-NLS-1$
        history.add("c"); //$NON-NLS-1$
        assertEquals("[a, b, c]", history.getPreviousList().toString()); //$NON-NLS-1$
    }

    public void testGo() {
        History history = new History();
        assertEquals(null, history.getCurrent());
        history.add("a"); //$NON-NLS-1$
        history.add("b"); //$NON-NLS-1$
        history.add("c"); //$NON-NLS-1$
        history.add("d"); //$NON-NLS-1$
        assertEquals("[a, b, c, d]", history.getPreviousList().toString()); //$NON-NLS-1$
        history.go(-1);
        assertEquals("[a, b, c]", history.getPreviousList().toString()); //$NON-NLS-1$
        assertEquals("[d]", history.getNextList().toString()); //$NON-NLS-1$

        history.go(-2);
        assertEquals("[a]", history.getPreviousList().toString()); //$NON-NLS-1$
        assertEquals("[b, c, d]", history.getNextList().toString()); //$NON-NLS-1$

        history.go(3);
        assertEquals("[a, b, c, d]", history.getPreviousList().toString()); //$NON-NLS-1$

        history.go(-10);
        assertEquals("[a]", history.getPreviousList().toString()); //$NON-NLS-1$
        assertEquals("[b, c, d]", history.getNextList().toString()); //$NON-NLS-1$

        history.go(10);
        assertEquals("[a, b, c, d]", history.getPreviousList().toString()); //$NON-NLS-1$
    }

    public void testNav() {
        History history = new History();
        assertEquals(null, history.getCurrent());
        history.add("a"); //$NON-NLS-1$
        history.add("b"); //$NON-NLS-1$
        history.add("c"); //$NON-NLS-1$
        history.add("d"); //$NON-NLS-1$
        history.add("e"); //$NON-NLS-1$
        history.add("f"); //$NON-NLS-1$
        history.add("g"); //$NON-NLS-1$
        history.add("h"); //$NON-NLS-1$
        history.add("i"); //$NON-NLS-1$
        assertEquals("[a, b, c, d, e, f, g, h, i]", history.getPreviousList().toString()); //$NON-NLS-1$

        history.go(-5);
        assertEquals("[a, b, c, d]", history.getPreviousList().toString()); //$NON-NLS-1$
        assertEquals("[e, f, g, h, i]", history.getNextList().toString()); //$NON-NLS-1$
        assertEquals("d", history.getCurrent()); //$NON-NLS-1$

        // Adding the current does not change anything
        history.add("d"); //$NON-NLS-1$
        assertEquals("[a, b, c, d]", history.getPreviousList().toString()); //$NON-NLS-1$
        assertEquals("[e, f, g, h, i]", history.getNextList().toString()); //$NON-NLS-1$

        // Adding the next splits the list
        history.add("e"); //$NON-NLS-1$
        assertEquals("[a, b, c, d, e]", history.getPreviousList().toString()); //$NON-NLS-1$
        assertEquals("[f, g, h, i]", history.getNextList().toString()); //$NON-NLS-1$

        // Adding the next splits the list
        history.add("h"); //$NON-NLS-1$
        assertEquals("[a, b, c, d, e, h]", history.getPreviousList().toString()); //$NON-NLS-1$
        assertEquals("[i]", history.getNextList().toString()); //$NON-NLS-1$

        history.go(-5);
        assertEquals("[a]", history.getPreviousList().toString()); //$NON-NLS-1$
        assertEquals("[b, c, d, e, h, i]", history.getNextList().toString()); //$NON-NLS-1$

        history.add("e"); //$NON-NLS-1$
        assertEquals("[a, e]", history.getPreviousList().toString()); //$NON-NLS-1$
        assertEquals("[h, i]", history.getNextList().toString()); //$NON-NLS-1$

    }
}
