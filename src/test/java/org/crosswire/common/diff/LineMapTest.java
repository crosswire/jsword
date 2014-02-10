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
 * Copyright: 2005 - 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * JUnit Test.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public class LineMapTest {

    @Test
    public void testCompile() {
        // Convert lines down to characters
        ArrayList<String> list = new ArrayList<String>();
        list.add("");
        list.add("alpha\n");
        list.add("beta\n");
        LineMap map = new LineMap("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n");
        assertEquals("new LineMap:", "\u0001\u0002\u0001", map.getSourceMap());
        assertEquals("new LineMap:", "\u0002\u0001\u0002", map.getTargetMap());
        assertEquals("new LineMap:", list, map.getLines());

        list.clear();
        list.add("");
        list.add("alpha\r\n");
        list.add("beta\r\n");
        list.add("\r\n");
        map = new LineMap("", "alpha\r\nbeta\r\n\r\n\r\n");
        assertEquals("new LineMap:", "", map.getSourceMap());
        assertEquals("new LineMap:", "\u0001\u0002\u0003\u0003", map.getTargetMap());
        assertEquals("new LineMap:", list, map.getLines());
    }

    @Test
    public void testRestore() {
        // Convert chars up to lines
        List<Difference> diffs = diffList(
                new Difference(EditType.EQUAL, "\u0001\u0002\u0001"), new Difference(EditType.INSERT, "\u0002\u0001\u0002"));
        ArrayList<String> list = new ArrayList<String>();
        list.add("");
        list.add("alpha\n");
        list.add("beta\n");
        LineMap map = new LineMap("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n");
        map.restore(diffs);
        assertEquals(
                "LineMap.restore:", diffList(new Difference(EditType.EQUAL, "alpha\nbeta\nalpha\n"), new Difference(EditType.INSERT, "beta\nalpha\nbeta\n")).get(0), diffs.get(0));
        assertEquals(
                "LineMap.restore:", diffList(new Difference(EditType.EQUAL, "alpha\nbeta\nalpha\n"), new Difference(EditType.INSERT, "beta\nalpha\nbeta\n")).get(diffs.size() - 1), diffs.get(diffs.size() - 1));
        assertEquals(
                "LineMap.restore:", diffList(new Difference(EditType.EQUAL, "alpha\nbeta\nalpha\n"), new Difference(EditType.INSERT, "beta\nalpha\nbeta\n")), diffs);
    }

    // Private function for quickly building lists of diffs.
    private static <T> List<T> diffList(T... items) {
        List<T> list = new ArrayList<T>();
        list.addAll(Arrays.asList(items));
        return list;
    }
}
