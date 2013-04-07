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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.common.history;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public class HistoryTest extends TestCase {
    public void testAdd() {
        History history = new History();
        assertEquals(null, history.getCurrent());
        history.add("a");
        assertEquals("a", history.getCurrent());
        history.add("b");
        assertEquals("[a, b]", history.getPreviousList().toString());
        // re-adding the current element won't change the list
        history.add("b");
        assertEquals("[a, b]", history.getPreviousList().toString());
        history.add("c");
        assertEquals("[a, b, c]", history.getPreviousList().toString());
    }

    public void testGo() {
        History history = new History();
        assertEquals(null, history.getCurrent());
        history.add("a");
        history.add("b");
        history.add("c");
        history.add("d");
        assertEquals("[a, b, c, d]", history.getPreviousList().toString());
        history.go(-1);
        assertEquals("[a, b, c]", history.getPreviousList().toString());
        assertEquals("[d]", history.getNextList().toString());

        history.go(-2);
        assertEquals("[a]", history.getPreviousList().toString());
        assertEquals("[b, c, d]", history.getNextList().toString());

        history.go(3);
        assertEquals("[a, b, c, d]", history.getPreviousList().toString());

        history.go(-10);
        assertEquals("[a]", history.getPreviousList().toString());
        assertEquals("[b, c, d]", history.getNextList().toString());

        history.go(10);
        assertEquals("[a, b, c, d]", history.getPreviousList().toString());
    }

    public void testNav() {
        History history = new History();
        assertEquals(null, history.getCurrent());
        history.add("a");
        history.add("b");
        history.add("c");
        history.add("d");
        history.add("e");
        history.add("f");
        history.add("g");
        history.add("h");
        history.add("i");
        assertEquals("[a, b, c, d, e, f, g, h, i]", history.getPreviousList().toString());

        history.go(-5);
        assertEquals("[a, b, c, d]", history.getPreviousList().toString());
        assertEquals("[e, f, g, h, i]", history.getNextList().toString());
        assertEquals("d", history.getCurrent());

        // Adding the current does not change anything
        history.add("d");
        assertEquals("[a, b, c, d]", history.getPreviousList().toString());
        assertEquals("[e, f, g, h, i]", history.getNextList().toString());

        // Adding the next splits the list
        history.add("e");
        assertEquals("[a, b, c, d, e]", history.getPreviousList().toString());
        assertEquals("[f, g, h, i]", history.getNextList().toString());

        // Adding the next splits the list
        history.add("h");
        assertEquals("[a, b, c, d, e, h]", history.getPreviousList().toString());
        assertEquals("[i]", history.getNextList().toString());

        history.go(-5);
        assertEquals("[a]", history.getPreviousList().toString());
        assertEquals("[b, c, d, e, h, i]", history.getNextList().toString());

        history.add("e");
        assertEquals("[a, e]", history.getPreviousList().toString());
        assertEquals("[h, i]", history.getNextList().toString());

    }
}
