package org.crosswire.common.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class DiffCleanupTest extends TestCase {

    @Override
    protected void setUp() {
    }

    public void testDiffCleanupMerge() {
        // Cleanup a messy diff
        List diffs = diffList();
        DiffCleanup.cleanupMerge(diffs);
        assertEquals("DiffCleanup.cleanupMerge: Null case.", diffList(), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.INSERT, "c")});
        DiffCleanup.cleanupMerge(diffs);
        assertEquals(
                "DiffCleanup.cleanupMerge: No change case.", diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.INSERT, "c")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.EQUAL, "a"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.EQUAL, "c")});
        DiffCleanup.cleanupMerge(diffs);
        assertEquals("DiffCleanup.cleanupMerge: Merge equalities.", diffList(new Object[] { new Difference(EditType.EQUAL, "abc")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.DELETE, "c")});
        DiffCleanup.cleanupMerge(diffs);
        assertEquals("DiffCleanup.cleanupMerge: Merge deletions.", diffList(new Object[] { new Difference(EditType.DELETE, "abc")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.INSERT, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.INSERT, "c")});
        DiffCleanup.cleanupMerge(diffs);
        assertEquals("DiffCleanup.cleanupMerge: Merge insertions.", diffList(new Object[] { new Difference(EditType.INSERT, "abc")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.DELETE, "c"), new Difference(EditType.INSERT, "d"), new Difference(EditType.EQUAL, "e"), new Difference(EditType.EQUAL, "f")});
        DiffCleanup.cleanupMerge(diffs);
        assertEquals(
                "DiffCleanup.cleanupMerge: Merge interweave.", diffList(new Object[] { new Difference(EditType.DELETE, "ac"), new Difference(EditType.INSERT, "bd"), new Difference(EditType.EQUAL, "ef")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "abc"), new Difference(EditType.DELETE, "dc")});
        DiffCleanup.cleanupMerge(diffs);
        assertEquals(
                "DiffCleanup.cleanupMerge: Prefix and suffix detection.", diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "d"), new Difference(EditType.INSERT, "b"), new Difference(EditType.EQUAL, "c")}), diffs);
    }

    public void testDiffCleanupSemantic() {
        // Cleanup semantically trivial equalities
        List diffs = diffList();
        DiffCleanup.cleanupSemantic(diffs);
        assertEquals("DiffCleanup.cleanupSemantic: Null case.", diffList(), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.EQUAL, "cd"), new Difference(EditType.DELETE, "e")});
        DiffCleanup.cleanupSemantic(diffs);
        assertEquals(
                "DiffCleanup.cleanupSemantic: No elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.EQUAL, "cd"), new Difference(EditType.DELETE, "e")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "a"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.DELETE, "c")});
        DiffCleanup.cleanupSemantic(diffs);
        assertEquals(
                "DiffCleanup.cleanupSemantic: Simple elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abc"), new Difference(EditType.INSERT, "b")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.EQUAL, "cd"), new Difference(EditType.DELETE, "e"), new Difference(EditType.EQUAL, "f"), new Difference(EditType.INSERT, "g")});
        DiffCleanup.cleanupSemantic(diffs);
        assertEquals(
                "DiffCleanup.cleanupSemantic: Backpass elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abcdef"), new Difference(EditType.INSERT, "cdfg")}), diffs);
    }

    public void testDiffCleanupEfficiency() {
        // Cleanup operationally trivial equalities
        DiffCleanup.setEditCost(4);
        List diffs = diffList();
        DiffCleanup.cleanupEfficiency(diffs);
        assertEquals("DiffCleanup.cleanupEfficiency: Null case.", diffList(), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34")});
        DiffCleanup.cleanupEfficiency(diffs);
        assertEquals(
                "DiffCleanup.cleanupEfficiency: No elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "xyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34")});
        DiffCleanup.cleanupEfficiency(diffs);
        assertEquals(
                "DiffCleanup.cleanupEfficiency: Four-edit elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abxyzcd"), new Difference(EditType.INSERT, "12xyz34")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "x"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34")});
        DiffCleanup.cleanupEfficiency(diffs);
        assertEquals(
                "DiffCleanup.cleanupEfficiency: Three-edit elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "xcd"), new Difference(EditType.INSERT, "12x34")}), diffs);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "xy"), new Difference(EditType.INSERT, "34"), new Difference(EditType.EQUAL, "z"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "56")});
        DiffCleanup.cleanupEfficiency(diffs);
        assertEquals(
                "DiffCleanup.cleanupEfficiency: Backpass elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abxyzcd"), new Difference(EditType.INSERT, "12xy34z56")}), diffs);
        DiffCleanup.setEditCost(5);
        diffs = diffList(new Object[] {
                new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34")});
        DiffCleanup.cleanupEfficiency(diffs);
        assertEquals(
                "DiffCleanup.cleanupEfficiency: High cost elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abwxyzcd"), new Difference(EditType.INSERT, "12wxyz34")}), diffs);
        DiffCleanup.setEditCost(4);
    }

    // Private function for quickly building lists of diffs.
    private static List diffList() {
        return new ArrayList();
    }

    // Private function for quickly building lists of diffs.
    private static List diffList(Object[] diffs) {
        List myDiffList = new ArrayList();
        myDiffList.addAll(Arrays.asList(diffs));
        return myDiffList;
    }
}
