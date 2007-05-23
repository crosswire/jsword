package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;


public class DiffTest extends TestCase {


    protected void setUp()
    {
    }


    public void testDiffMain()
    {
        // Perform a trivial diff
        List diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "abc") }); //$NON-NLS-1$
        assertEquals("diff_main: Null case.", diffs, new Diff("abc", "abc", false).compare()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "ab"), new Difference(EditType.INSERT, "123"), new Difference(EditType.EQUAL, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("diff_main: Simple insertion.", diffs, new Diff("abc", "ab123c", false).compare()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "123"), new Difference(EditType.EQUAL, "bc") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("diff_main: Simple deletion.", diffs, new Diff("a123bc", "abc", false).compare()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.INSERT, "123"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.INSERT, "456"), new Difference(EditType.EQUAL, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        assertEquals("diff_main: Two insertions.", diffs, new Diff("abc", "a123b456c", false).compare()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "123"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.DELETE, "456"), new Difference(EditType.EQUAL, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        assertEquals("diff_main: Two deletions.", diffs, new Diff("a123b456c", "abc", false).compare()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Perform a real diff
        // Switch off the timeout.
        //dmp.Diff_Timeout = 0;
        diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b") }); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("diff_main: Simple case #1.", diffs, new Diff("a", "b", false).compare()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        diffs = diffList(new Object[] { new Difference(EditType.DELETE, "Apple"), new Difference(EditType.INSERT, "Banana"), new Difference(EditType.EQUAL, "s are a"), new Difference(EditType.INSERT, "lso"), new Difference(EditType.EQUAL, " fruit.") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        assertEquals("diff_main: Simple case #2.", diffs, new Diff("Apples are a fruit.", "Bananas are also fruit.", false).compare()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        diffs = diffList(new Object[] { new Difference(EditType.DELETE, "1"), new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "y"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.DELETE, "2"), new Difference(EditType.INSERT, "xab") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        assertEquals("diff_main: Overlap #1.", diffs, new Diff("1ayb2", "abxab", false).compare()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        diffs = diffList(new Object[] { new Difference(EditType.INSERT, "x"), new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.INSERT, "x"), new Difference(EditType.EQUAL, "c"), new Difference(EditType.DELETE, "y"), new Difference(EditType.INSERT, "xabc") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        assertEquals("diff_main: Overlap #2.", diffs, new Diff("abcy", "xaxcxabc", false).compare()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Test the linemode speedup
        // Must be long to pass the 250 char cutoff.
        String a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n"; //$NON-NLS-1$
        String b = "abcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\n"; //$NON-NLS-1$
        assertEquals("diff_main: Simple.", new Diff(a, b, true).compare(), new Diff(a, b, false).compare()); //$NON-NLS-1$
        a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n"; //$NON-NLS-1$
        b = "abcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n"; //$NON-NLS-1$
        String[] texts_linemode = diff_rebuildtexts(new Diff(a, b, true).compare());
        String[] texts_textmode = diff_rebuildtexts(new Diff(a, b, false).compare());
        assertArrayEquals("diff_main: Overlap.", texts_textmode, texts_linemode); //$NON-NLS-1$
    }

    public void testDiffXIndex()
    {
        // Translate a location in text1 to text2
        List diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "1234"), new Difference(EditType.EQUAL, "xyz") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("diff_xIndex: Translation on equality.", 5, new Diff("","").xIndex(diffs, 2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "1234"), new Difference(EditType.EQUAL, "xyz") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("diff_xIndex: Translation on deletion.", 1, new Diff("","").xIndex(diffs, 3)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private void assertArrayEquals(String error_msg, Object[] a, Object[] b)
    {
        List list_a = Arrays.asList(a);
        List list_b = Arrays.asList(b);
        assertEquals(error_msg, list_a, list_b);
    }

    // Construct the two texts which made up the diff originally.
    private static String[] diff_rebuildtexts(List diffs)
    {
        String[] text = {"", ""}; //$NON-NLS-1$ //$NON-NLS-2$
        Iterator iter = diffs.iterator();
        while (iter.hasNext())
        {
            Difference myDiff = (Difference) iter.next();
            EditType editType = myDiff.getEditType();
            if (!EditType.INSERT.equals(editType))
            {
                text[0] += myDiff.getText();
            }

            if (!EditType.DELETE.equals(editType))
            {
                text[1] += myDiff.getText();
            }
        }
        return text;
    }

    // Private function for quickly building lists of diffs.
    private static List diffList(Object[] diffs)
    {
        List myDiffList = new ArrayList();
        myDiffList.addAll(Arrays.asList(diffs));
        return myDiffList;
    }
}
