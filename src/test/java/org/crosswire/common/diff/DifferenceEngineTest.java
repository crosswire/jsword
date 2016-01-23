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
package org.crosswire.common.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class DifferenceEngineTest {
    @Test
    public void testDiffPath() {
        // Single letters
        // Trace a path from back to front.
        DifferenceEngine generator = new DifferenceEngine();
        List<Set<String>> vMap = new ArrayList<Set<String>>();
        Set<String> rowSet = new HashSet<String>();
        rowSet.add("0,0");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,1");
        rowSet.add("1,0");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,2");
        rowSet.add("2,0");
        rowSet.add("2,2");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,3");
        rowSet.add("2,3");
        rowSet.add("3,0");
        rowSet.add("4,3");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,4");
        rowSet.add("2,4");
        rowSet.add("4,0");
        rowSet.add("4,4");
        rowSet.add("5,3");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,5");
        rowSet.add("2,5");
        rowSet.add("4,5");
        rowSet.add("5,0");
        rowSet.add("6,3");
        rowSet.add("6,5");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,6");
        rowSet.add("2,6");
        rowSet.add("4,6");
        rowSet.add("6,6");
        rowSet.add("7,5");
        vMap.add(rowSet);

        List<Difference> diffs = diffList(
                new Difference(EditType.INSERT, "W"), new Difference(EditType.DELETE, "A"), new Difference(EditType.EQUAL, "1"), new Difference(EditType.DELETE, "B"), new Difference(EditType.EQUAL, "2"), new Difference(EditType.INSERT, "X"), new Difference(EditType.DELETE, "C"), new Difference(EditType.EQUAL, "3"), new Difference(EditType.DELETE, "D"));
        Assert.assertEquals("diff_path1: Single letters.", diffs, generator.path1(vMap, "A1B2C3D", "W12X3"));

        // Trace a path from front to back.
        vMap.remove(vMap.size() - 1);
        diffs = diffList(
                new Difference(EditType.EQUAL, "4"), new Difference(EditType.DELETE, "E"), new Difference(EditType.INSERT, "Y"), new Difference(EditType.EQUAL, "5"), new Difference(EditType.DELETE, "F"), new Difference(EditType.EQUAL, "6"), new Difference(EditType.DELETE, "G"), new Difference(EditType.INSERT, "Z"));
        Assert.assertEquals("diff_path2: Single letters.", diffs, generator.path2(vMap, "4E5F6G", "4Y56Z"));

        // Double letters
        // Trace a path from back to front.
        vMap = new ArrayList<Set<String>>();
        rowSet = new HashSet<String>();
        rowSet.add("0,0");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,1");
        rowSet.add("1,0");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,2");
        rowSet.add("1,1");
        rowSet.add("2,0");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,3");
        rowSet.add("1,2");
        rowSet.add("2,1");
        rowSet.add("3,0");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,4");
        rowSet.add("1,3");
        rowSet.add("3,1");
        rowSet.add("4,0");
        rowSet.add("4,4");
        vMap.add(rowSet);

        diffs = diffList(
                new Difference(EditType.INSERT, "WX"), new Difference(EditType.DELETE, "AB"), new Difference(EditType.EQUAL, "12"));
        Assert.assertEquals("diff_path1: Double letters.", diffs, generator.path1(vMap, "AB12", "WX12"));

        // Trace a path from front to back.
        vMap = new ArrayList<Set<String>>();
        rowSet = new HashSet<String>();
        rowSet.add("0,0");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("0,1");
        rowSet.add("1,0");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("1,1");
        rowSet.add("2,0");
        rowSet.add("2,4");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("2,1");
        rowSet.add("2,5");
        rowSet.add("3,0");
        rowSet.add("3,4");
        vMap.add(rowSet);
        rowSet = new HashSet<String>();
        rowSet.add("2,6");
        rowSet.add("3,5");
        rowSet.add("4,4");
        vMap.add(rowSet);

        diffs = diffList(
                new Difference(EditType.DELETE, "CD"), new Difference(EditType.EQUAL, "34"), new Difference(EditType.INSERT, "YZ"));
        Assert.assertEquals("diff_path2: Double letters.", diffs, generator.path2(vMap, "CD34", "34YZ"));
    }

    @Test
    public void testTimeout() {
        DifferenceEngine.setTimeout(0.001f); // 1ms
        // This test may fail on extremely fast computers. If so, just increase
        // the text lengths.
        Assert.assertNull(
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
