package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import junit.framework.TestCase;

public class diff_match_patch_test extends TestCase {

    protected void setUp() {
    }

    // DIFF TEST FUNCTIONS

    // public void testDiffAddIndex() {
    // // Add an index to each diff tuple
    //    List diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxy"), new Difference(EditType.INSERT, "34"), new Difference(EditType.EQUAL, "z"), new Difference(EditType.DELETE, "bcd"), new Difference(EditType.INSERT, "56") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
    // dmp.diff_addIndex(diffs);
    //    String indexString = ""; //$NON-NLS-1$
    // for (Diff aDiff : diffs) {
    //      indexString += aDiff.index + ","; //$NON-NLS-1$
    // }
    //    assertEquals("diff_addIndex:", "0,0,2,5,7,8,8,", indexString); //$NON-NLS-1$ //$NON-NLS-2$
    // }

    // public void testDiffPrettyHtml() {
    // // Pretty print
    //    List diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a\n"), new Difference(EditType.DELETE, "<B>b</B>"), new Difference(EditType.INSERT, "c&d") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    //    assertEquals("diff_prettyHtml:", "<SPAN TITLE=\"i=0\">a&para;<BR></SPAN><DEL STYLE=\"background:#FFE6E6;\" TITLE=\"i=2\">&lt;B&gt;b&lt;/B&gt;</DEL><INS STYLE=\"background:#E6FFE6;\" TITLE=\"i=2\">c&amp;d</INS>", dmp.diff_prettyHtml(diffs)); //$NON-NLS-1$ //$NON-NLS-2$
    // }

    // // Private function for quickly building lists of diffs.
    // private static List diffList()
    // {
    // return new ArrayList();
    // }
    //
    // // Private function for quickly building lists of diffs.
    // private static List diffList(Object[] diffs)
    // {
    // List myDiffList = new ArrayList();
    // myDiffList.addAll(Arrays.asList(diffs));
    // return myDiffList;
    // }
}
