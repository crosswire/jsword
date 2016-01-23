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
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.common.history;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class HistoryTest {
    @Test
    public void testAdd() {
        History history = new History();
        Assert.assertEquals(null, history.getCurrent());
        history.add("a");
        Assert.assertEquals("a", history.getCurrent());
        history.add("b");
        Assert.assertEquals("[a, b]", history.getPreviousList().toString());
        // re-adding the current element won't change the list
        history.add("b");
        Assert.assertEquals("[a, b]", history.getPreviousList().toString());
        history.add("c");
        Assert.assertEquals("[a, b, c]", history.getPreviousList().toString());
    }

    @Test
    public void testGo() {
        History history = new History();
        Assert.assertEquals(null, history.getCurrent());
        history.add("a");
        history.add("b");
        history.add("c");
        history.add("d");
        Assert.assertEquals("[a, b, c, d]", history.getPreviousList().toString());
        history.go(-1);
        Assert.assertEquals("[a, b, c]", history.getPreviousList().toString());
        Assert.assertEquals("[d]", history.getNextList().toString());

        history.go(-2);
        Assert.assertEquals("[a]", history.getPreviousList().toString());
        Assert.assertEquals("[b, c, d]", history.getNextList().toString());

        history.go(3);
        Assert.assertEquals("[a, b, c, d]", history.getPreviousList().toString());

        history.go(-10);
        Assert.assertEquals("[a]", history.getPreviousList().toString());
        Assert.assertEquals("[b, c, d]", history.getNextList().toString());

        history.go(10);
        Assert.assertEquals("[a, b, c, d]", history.getPreviousList().toString());
    }

    @Test
    public void testNav() {
        History history = new History();
        Assert.assertEquals(null, history.getCurrent());
        history.add("a");
        history.add("b");
        history.add("c");
        history.add("d");
        history.add("e");
        history.add("f");
        history.add("g");
        history.add("h");
        history.add("i");
        Assert.assertEquals("[a, b, c, d, e, f, g, h, i]", history.getPreviousList().toString());

        history.go(-5);
        Assert.assertEquals("[a, b, c, d]", history.getPreviousList().toString());
        Assert.assertEquals("[e, f, g, h, i]", history.getNextList().toString());
        Assert.assertEquals("d", history.getCurrent());

        // Adding the current does not change anything
        history.add("d");
        Assert.assertEquals("[a, b, c, d]", history.getPreviousList().toString());
        Assert.assertEquals("[e, f, g, h, i]", history.getNextList().toString());

        // Adding the next splits the list
        history.add("e");
        Assert.assertEquals("[a, b, c, d, e]", history.getPreviousList().toString());
        Assert.assertEquals("[f, g, h, i]", history.getNextList().toString());

        // Adding the next splits the list
        history.add("h");
        Assert.assertEquals("[a, b, c, d, e, h]", history.getPreviousList().toString());
        Assert.assertEquals("[i]", history.getNextList().toString());

        history.go(-5);
        Assert.assertEquals("[a]", history.getPreviousList().toString());
        Assert.assertEquals("[b, c, d, e, h, i]", history.getNextList().toString());

        history.add("e");
        Assert.assertEquals("[a, e]", history.getPreviousList().toString());
        Assert.assertEquals("[h, i]", history.getNextList().toString());

    }
}
