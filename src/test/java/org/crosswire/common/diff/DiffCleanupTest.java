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
import java.util.List;

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
public class DiffCleanupTest {

    @Test
    public void testDiffCleanupMerge() {
        // Cleanup a messy diff
        List<Difference> diffs = diffList();
        DiffCleanup.cleanupMerge(diffs);
        Assert.assertEquals("DiffCleanup.cleanupMerge: Null case.", diffList(), diffs);
        diffs = diffList(
                new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.INSERT, "c"));
        DiffCleanup.cleanupMerge(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupMerge: No change case.", diffList(new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.INSERT, "c")), diffs);
        diffs = diffList(
                new Difference(EditType.EQUAL, "a"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.EQUAL, "c"));
        DiffCleanup.cleanupMerge(diffs);
        Assert.assertEquals("DiffCleanup.cleanupMerge: Merge equalities.", diffList(new Difference(EditType.EQUAL, "abc")), diffs);
        diffs = diffList(
                new Difference(EditType.DELETE, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.DELETE, "c"));
        DiffCleanup.cleanupMerge(diffs);
        Assert.assertEquals("DiffCleanup.cleanupMerge: Merge deletions.", diffList(new Difference(EditType.DELETE, "abc")), diffs);
        diffs = diffList(
                new Difference(EditType.INSERT, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.INSERT, "c"));
        DiffCleanup.cleanupMerge(diffs);
        Assert.assertEquals("DiffCleanup.cleanupMerge: Merge insertions.", diffList(new Difference(EditType.INSERT, "abc")), diffs);
        diffs = diffList(
                new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.DELETE, "c"), new Difference(EditType.INSERT, "d"), new Difference(EditType.EQUAL, "e"), new Difference(EditType.EQUAL, "f"));
        DiffCleanup.cleanupMerge(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupMerge: Merge interweave.", diffList(new Difference(EditType.DELETE, "ac"), new Difference(EditType.INSERT, "bd"), new Difference(EditType.EQUAL, "ef")), diffs);
        diffs = diffList(
                new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "abc"), new Difference(EditType.DELETE, "dc"));
        DiffCleanup.cleanupMerge(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupMerge: Prefix and suffix detection.", diffList(new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "d"), new Difference(EditType.INSERT, "b"), new Difference(EditType.EQUAL, "c")), diffs);
    }

    @Test
    public void testDiffCleanupSemantic() {
        // Cleanup semantically trivial equalities
        List<Difference> diffs = diffList();
        DiffCleanup.cleanupSemantic(diffs);
        Assert.assertEquals("DiffCleanup.cleanupSemantic: Null case.", diffList(), diffs);
        diffs = diffList(
                new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.EQUAL, "cd"), new Difference(EditType.DELETE, "e"));
        DiffCleanup.cleanupSemantic(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupSemantic: No elimination.", diffList(new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.EQUAL, "cd"), new Difference(EditType.DELETE, "e")), diffs);
        diffs = diffList(
                new Difference(EditType.DELETE, "a"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.DELETE, "c"));
        DiffCleanup.cleanupSemantic(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupSemantic: Simple elimination.", diffList(new Difference(EditType.DELETE, "abc"), new Difference(EditType.INSERT, "b")), diffs);
        diffs = diffList(
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.EQUAL, "cd"), new Difference(EditType.DELETE, "e"), new Difference(EditType.EQUAL, "f"), new Difference(EditType.INSERT, "g"));
        DiffCleanup.cleanupSemantic(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupSemantic: Backpass elimination.", diffList(new Difference(EditType.DELETE, "abcdef"), new Difference(EditType.INSERT, "cdfg")), diffs);
    }

    @Test
    public void testDiffCleanupEfficiency() {
        // Cleanup operationally trivial equalities
        DiffCleanup.setEditCost(4);
        List<Difference> diffs = diffList();
        DiffCleanup.cleanupEfficiency(diffs);
        Assert.assertEquals("DiffCleanup.cleanupEfficiency: Null case.", diffList(), diffs);
        diffs = diffList(
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34"));
        DiffCleanup.cleanupEfficiency(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupEfficiency: No elimination.", diffList(new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34")), diffs);
        diffs = diffList(
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "xyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34"));
        DiffCleanup.cleanupEfficiency(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupEfficiency: Four-edit elimination.", diffList(new Difference(EditType.DELETE, "abxyzcd"), new Difference(EditType.INSERT, "12xyz34")), diffs);
        diffs = diffList(
                new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "x"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34"));
        DiffCleanup.cleanupEfficiency(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupEfficiency: Three-edit elimination.", diffList(new Difference(EditType.DELETE, "xcd"), new Difference(EditType.INSERT, "12x34")), diffs);
        diffs = diffList(
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "xy"), new Difference(EditType.INSERT, "34"), new Difference(EditType.EQUAL, "z"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "56"));
        DiffCleanup.cleanupEfficiency(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupEfficiency: Backpass elimination.", diffList(new Difference(EditType.DELETE, "abxyzcd"), new Difference(EditType.INSERT, "12xy34z56")), diffs);
        DiffCleanup.setEditCost(5);
        diffs = diffList(
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34"));
        DiffCleanup.cleanupEfficiency(diffs);
        Assert.assertEquals(
                "DiffCleanup.cleanupEfficiency: High cost elimination.", diffList(new Difference(EditType.DELETE, "abwxyzcd"), new Difference(EditType.INSERT, "12wxyz34")), diffs);
        DiffCleanup.setEditCost(4);
    }

    // Private function for quickly building lists of diffs.
    private static List<Difference> diffList() {
        return new ArrayList<Difference>();
    }

    // Private function for quickly building lists of diffs.
    private static <T> List<T> diffList(T... items) {
        List<T> list = new ArrayList<T>();
        list.addAll(Arrays.asList(items));
        return list;
    }
}
