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
 * Copyright: 2005-2011
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DifferenceEngineTest extends TestCase {
    @Override
    protected void setUp() {
    }

    public void testDiffPath() {
        // Single letters
        // Trace a path from back to front.
        DifferenceEngine generator = new DifferenceEngine();
        List<Set<String>> v_map = new ArrayList<Set<String>>();
        Set<String> row_set = new HashSet<String>();
        row_set.add("0,0");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,1");row_set.add("1,0");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,2");row_set.add("2,0");row_set.add("2,2");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,3");row_set.add("2,3");row_set.add("3,0");row_set.add("4,3");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,4");row_set.add("2,4");row_set.add("4,0");row_set.add("4,4");row_set.add("5,3");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,5");row_set.add("2,5");row_set.add("4,5");row_set.add("5,0");row_set.add("6,3");row_set.add("6,5");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,6");row_set.add("2,6");row_set.add("4,6");row_set.add("6,6");row_set.add("7,5");
        v_map.add(row_set);

        List<Difference> diffs = diffList(
                new Difference(EditType.INSERT, "W"), new Difference(EditType.DELETE, "A"), new Difference(EditType.EQUAL, "1"), new Difference(EditType.DELETE, "B"), new Difference(EditType.EQUAL, "2"), new Difference(EditType.INSERT, "X"), new Difference(EditType.DELETE, "C"), new Difference(EditType.EQUAL, "3"), new Difference(EditType.DELETE, "D"));
        assertEquals("diff_path1: Single letters.", diffs, generator.path1(v_map, "A1B2C3D", "W12X3"));

        // Trace a path from front to back.
        v_map.remove(v_map.size() - 1);
        diffs = diffList(
                new Difference(EditType.EQUAL, "4"), new Difference(EditType.DELETE, "E"), new Difference(EditType.INSERT, "Y"), new Difference(EditType.EQUAL, "5"), new Difference(EditType.DELETE, "F"), new Difference(EditType.EQUAL, "6"), new Difference(EditType.DELETE, "G"), new Difference(EditType.INSERT, "Z"));
        assertEquals("diff_path2: Single letters.", diffs, generator.path2(v_map, "4E5F6G", "4Y56Z"));

        // Double letters
        // Trace a path from back to front.
        v_map = new ArrayList<Set<String>>();
        row_set = new HashSet<String>();
        row_set.add("0,0");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,1");row_set.add("1,0");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,2");row_set.add("1,1");row_set.add("2,0");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,3");row_set.add("1,2");row_set.add("2,1");row_set.add("3,0");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,4");row_set.add("1,3");row_set.add("3,1");row_set.add("4,0");row_set.add("4,4");
        v_map.add(row_set);

        diffs = diffList(
                new Difference(EditType.INSERT, "WX"), new Difference(EditType.DELETE, "AB"), new Difference(EditType.EQUAL, "12"));
        assertEquals("diff_path1: Double letters.", diffs, generator.path1(v_map, "AB12", "WX12"));

        // Trace a path from front to back.
        v_map = new ArrayList<Set<String>>();
        row_set = new HashSet<String>();
        row_set.add("0,0");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("0,1");row_set.add("1,0");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("1,1");row_set.add("2,0");row_set.add("2,4");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("2,1");row_set.add("2,5");row_set.add("3,0");row_set.add("3,4");
        v_map.add(row_set);
        row_set = new HashSet<String>();
        row_set.add("2,6");row_set.add("3,5");row_set.add("4,4");
        v_map.add(row_set);

        diffs = diffList(
                new Difference(EditType.DELETE, "CD"), new Difference(EditType.EQUAL, "34"), new Difference(EditType.INSERT, "YZ"));
        assertEquals("diff_path2: Double letters.", diffs, generator.path2(v_map, "CD34", "34YZ"));
    }

    public void testTimeout() {
        DifferenceEngine.setTimeout(0.001f); // 1ms
        // This test may fail on extremely fast computers. If so, just increase
        // the text lengths.
        assertNull(
                "diff_main: Timeout.", new DifferenceEngine("`Twas brillig, and the slithy toves\nDid gyre and gimble in the wabe:\nAll mimsy were the borogoves,\nAnd the mome raths outgrabe.", "I am the very model of a modern major general,\nI've information vegetable, animal, and mineral,\nI know the kings of England, and I quote the fights historical,\nFrom Marathon to Waterloo, in order categorical.").generate());
        DifferenceEngine.setTimeout(0);
    }

    // Private function for quickly building lists of diffs.
    private static <T> List<T> diffList(T... items) {
        List<T> list = new ArrayList<T>();
        list.addAll(Arrays.asList(items));
        return list;
    }
}
