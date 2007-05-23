package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;


public class PatchEntryTest extends TestCase
{
    protected void setUp()
    {
    }

    public void testPatchObj()
    {
        // Patch Object
        PatchEntry p = new PatchEntry();
        p.setLeftStart(20);
        p.setRightStart(21);
        p.setLeftLength(18);
        p.setRightLength(17);
        p.setDifferences(diffList(new Object[] { new Difference(EditType.EQUAL, "jump"), new Difference(EditType.DELETE, "s"), new Difference(EditType.INSERT, "ed"), new Difference(EditType.EQUAL, " over "), new Difference(EditType.DELETE, "the"), new Difference(EditType.INSERT, "a"), new Difference(EditType.EQUAL, " laz") })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        assertEquals("Patch: text1.", "jumps over the laz", p.getLeftText()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Patch: text2.", "jumped over a laz", p.getRightText()); //$NON-NLS-1$ //$NON-NLS-2$
        String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n"; //$NON-NLS-1$
        assertEquals("Patch: toString.", strp, p.toString()); //$NON-NLS-1$
    }

    public void testMatchFromText()
    {
        String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n"; //$NON-NLS-1$
        assertEquals("PatchEntry.fromText: #1.", strp, new PatchEntry(strp).toString()); //$NON-NLS-1$
        assertEquals("PatchEntry.fromText: #2.", "@@ -1 +1 @@\n-a\n+b\n", new PatchEntry("@@ -1 +1 @@\n-a\n+b\n").toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("PatchEntry.fromText: #3.", "@@ -1,3 +0,0 @@\n-abc\n", new PatchEntry("@@ -1,3 +0,0 @@\n-abc\n").toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("PatchEntry.fromText: #4.", "@@ -0,0 +1,3 @@\n+abc\n", new PatchEntry("@@ -0,0 +1,3 @@\n+abc\n").toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void testMatchAddContext()
    {
        PatchEntry.setMargin(4);
        PatchEntry p = new PatchEntry("@@ -21,4 +21,10 @@\n-jump\n+somersault\n"); //$NON-NLS-1$
        p.addContext("The quick brown fox jumps over the lazy dog."); //$NON-NLS-1$
        assertEquals("PatchEntry.addContext: Simple case.", "@@ -17,12 +17,18 @@\n fox \n-jump\n+somersault\n s ov\n", p.toString()); //$NON-NLS-1$ //$NON-NLS-2$

        p = new PatchEntry("@@ -21,4 +21,10 @@\n-jump\n+somersault\n"); //$NON-NLS-1$
        p.addContext("The quick brown fox jumps."); //$NON-NLS-1$
        assertEquals("PatchEntry.addContext: Not enough trailing context.", "@@ -17,10 +17,16 @@\n fox \n-jump\n+somersault\n s.\n", p.toString()); //$NON-NLS-1$ //$NON-NLS-2$

        p = new PatchEntry("@@ -3 +3,2 @@\n-e\n+at\n"); //$NON-NLS-1$
        p.addContext("The quick brown fox jumps."); //$NON-NLS-1$
        assertEquals("PatchEntry.addContext: Not enough leading context.", "@@ -1,7 +1,8 @@\n Th\n-e\n+at\n  qui\n", p.toString()); //$NON-NLS-1$ //$NON-NLS-2$

        p = new PatchEntry("@@ -3 +3,2 @@\n-e\n+at\n"); //$NON-NLS-1$
        p.addContext("The quick brown fox jumps.  The quick brown fox crashes."); //$NON-NLS-1$
        assertEquals("PatchEntry.addContext: Ambiguity.", "@@ -1,27 +1,28 @@\n Th\n-e\n+at\n  quick brown fox jumps. \n", p.toString()); //$NON-NLS-1$ //$NON-NLS-2$
    }

  
    // Private function for quickly building lists of diffs.
    private static List diffList(Object[] diffs)
    {
        List myDiffList = new ArrayList();
        myDiffList.addAll(Arrays.asList(diffs));
        return myDiffList;
    }
}
