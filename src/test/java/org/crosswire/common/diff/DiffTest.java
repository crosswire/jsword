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
public class DiffTest {
    @Test
    public void testDiffMain() {
        // Perform a trivial diff
        List<Difference> diffs = diffList(
            new Difference(EditType.EQUAL, "abc"));
        assertEquals("diff_main: Null case.", diffs, new Diff("abc", "abc", false).compare());
        diffs = diffList(
                new Difference(EditType.EQUAL, "ab"), new Difference(EditType.INSERT, "123"), new Difference(EditType.EQUAL, "c"));
        assertEquals("diff_main: Simple insertion.", diffs, new Diff("abc", "ab123c", false).compare());
        diffs = diffList(
                new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "123"), new Difference(EditType.EQUAL, "bc"));
        assertEquals("diff_main: Simple deletion.", diffs, new Diff("a123bc", "abc", false).compare());
        diffs = diffList(
                new Difference(EditType.EQUAL, "a"), new Difference(EditType.INSERT, "123"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.INSERT, "456"), new Difference(EditType.EQUAL, "c"));
        assertEquals("diff_main: Two insertions.", diffs, new Diff("abc", "a123b456c", false).compare());
        diffs = diffList(
                new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "123"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.DELETE, "456"), new Difference(EditType.EQUAL, "c"));
        assertEquals("diff_main: Two deletions.", diffs, new Diff("a123b456c", "abc", false).compare());

        // Perform a real diff
        // Switch off the timeout.
        // dmp.Diff_Timeout = 0;
        diffs = diffList(
                new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"));
        assertEquals("diff_main: Simple case #1.", diffs, new Diff("a", "b", false).compare());
        diffs = diffList(
                new Difference(EditType.DELETE, "Apple"), new Difference(EditType.INSERT, "Banana"), new Difference(EditType.EQUAL, "s are a"), new Difference(EditType.INSERT, "lso"), new Difference(EditType.EQUAL, " fruit."));
        assertEquals("diff_main: Simple case #2.", diffs, new Diff("Apples are a fruit.", "Bananas are also fruit.", false).compare());
        diffs = diffList(
                new Difference(EditType.DELETE, "1"), new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "y"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.DELETE, "2"), new Difference(EditType.INSERT, "xab"));
        assertEquals("diff_main: Overlap #1.", diffs, new Diff("1ayb2", "abxab", false).compare());
        diffs = diffList(
                new Difference(EditType.INSERT, "x"), new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.INSERT, "x"), new Difference(EditType.EQUAL, "c"), new Difference(EditType.DELETE, "y"), new Difference(EditType.INSERT, "xabc"));
        assertEquals("diff_main: Overlap #2.", diffs, new Diff("abcy", "xaxcxabc", false).compare());

        // Test the linemode speedup
        // Must be long to pass the 250 char cutoff.
        String a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n";
        String b = "abcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\n";
        assertEquals("diff_main: Simple.", new Diff(a, b, true).compare(), new Diff(a, b, false).compare());
        a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n";
        b = "abcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n";
        String[] texts_linemode = diff_rebuildtexts(new Diff(a, b, true).compare());
        String[] texts_textmode = diff_rebuildtexts(new Diff(a, b, false).compare());
        assertArrayEquals("diff_main: Overlap.", texts_textmode, texts_linemode);
    }

    @Test
    public void testDiffXIndex() {
        // Translate a location in text1 to text2
        List<Difference> diffs = diffList(
                new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "1234"), new Difference(EditType.EQUAL, "xyz"));
        assertEquals("diff_xIndex: Translation on equality.", 5, new Diff("", "").xIndex(diffs, 2));
        diffs = diffList(
                new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "1234"), new Difference(EditType.EQUAL, "xyz"));
        assertEquals("diff_xIndex: Translation on deletion.", 1, new Diff("", "").xIndex(diffs, 3));
    }

    private <T> void assertArrayEquals(String error_msg, T[] a, T[] b) {
        List<T> list_a = Arrays.asList(a);
        List<T> list_b = Arrays.asList(b);
        assertEquals(error_msg, list_a, list_b);
    }

    // Construct the two texts which made up the diff originally.
    private static String[] diff_rebuildtexts(List<Difference> diffs) {
        String[] text = {
                "", ""};
        for (Difference myDiff : diffs) {
            EditType editType = myDiff.getEditType();
            if (!EditType.INSERT.equals(editType)) {
                text[0] += myDiff.getText();
            }

            if (!EditType.DELETE.equals(editType)) {
                text[1] += myDiff.getText();
            }
        }
        return text;
    }

    // Private function for quickly building lists of diffs.
    private static <T> List<T> diffList(T... items) {
        List<T> list = new ArrayList<T>();
        list.addAll(Arrays.asList(items));
        return list;
    }
}
