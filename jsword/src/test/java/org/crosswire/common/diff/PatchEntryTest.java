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

public class PatchEntryTest extends TestCase {
    protected void setUp() {
    }

    public void testPatchObj() {
        // Patch Object
        PatchEntry p = new PatchEntry();
        p.setSourceStart(20);
        p.setTargetStart(21);
        p.setSourceLength(18);
        p.setTargetLength(17);
        p
                .setDifferences(diffList(new Object[] {
                        new Difference(EditType.EQUAL, "jump"), new Difference(EditType.DELETE, "s"), new Difference(EditType.INSERT, "ed"), new Difference(EditType.EQUAL, " over "), new Difference(EditType.DELETE, "the"), new Difference(EditType.INSERT, "a"), new Difference(EditType.EQUAL, " laz")}));
        assertEquals("Patch: text1.", "jumps over the laz", p.getSourceText());
        assertEquals("Patch: text2.", "jumped over a laz", p.getTargetText());
        String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n";
        assertEquals("Patch: toString.", strp, p.toString());
    }

    public void testMatchFromText() {
        String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n";
        assertEquals("PatchEntry.fromText: #1.", strp, new PatchEntry(strp).toString());
        assertEquals("PatchEntry.fromText: #2.", "@@ -1 +1 @@\n-a\n+b\n", new PatchEntry("@@ -1 +1 @@\n-a\n+b\n").toString());
        assertEquals("PatchEntry.fromText: #3.", "@@ -1,3 +0,0 @@\n-abc\n", new PatchEntry("@@ -1,3 +0,0 @@\n-abc\n").toString());
        assertEquals("PatchEntry.fromText: #4.", "@@ -0,0 +1,3 @@\n+abc\n", new PatchEntry("@@ -0,0 +1,3 @@\n+abc\n").toString());
        assertEquals("PatchEntry.fromText: #5.", "@@ -1,7 +1,6 @@\n foo\n-%0A\n bar\n", new PatchEntry("@@ -1,7 +1,6 @@\n foo\n-%0A\n bar\n").toString());
        assertEquals("PatchEntry.fromText: #6.", "@@ -1,4 +1,3 @@\n foo\n-%0A\n", new PatchEntry("@@ -1,4 +1,3 @@\n foo\n-%0A\n").toString());
        assertEquals("PatchEntry.fromText: #7.", "@@ -1,4 +1,3 @@\n-%0A\n foo\n", new PatchEntry("@@ -1,4 +1,3 @@\n-%0A\n foo\n").toString());
        assertEquals("PatchEntry.fromText: #8.", "@@ -1,3 +1,4 @@\n foo\n+%0A\n", new PatchEntry("@@ -1,3 +1,4 @@\n foo\n+%0A\n").toString());
        assertEquals("PatchEntry.fromText: #9.", "@@ -1,3 +1,4 @@\n+%0A\n foo\n", new PatchEntry("@@ -1,3 +1,4 @@\n+%0A\n foo\n").toString());
        assertEquals("PatchEntry.fromText: #10.", "@@ -1,4 +1,4 @@\n-foo%0A\n+%0Afoo\n", new PatchEntry("@@ -1,4 +1,4 @@\n-foo%0A\n+%0Afoo\n").toString());
        assertEquals("PatchEntry.fromText: #11.", "@@ -1,4 +1,4 @@\n-%0Afoo\n+foo%0A\n", new PatchEntry("@@ -1,4 +1,4 @@\n-%0Afoo\n+foo%0A\n").toString());
    }

    public void testMatchAddContext() {
        PatchEntry.setMargin(4);
        PatchEntry p = new PatchEntry("@@ -21,4 +21,10 @@\n-jump\n+somersault\n");
        p.addContext("The quick brown fox jumps over the lazy dog.");
        assertEquals("PatchEntry.addContext: Simple case.", "@@ -17,12 +17,18 @@\n fox \n-jump\n+somersault\n s ov\n", p.toString());

        p = new PatchEntry("@@ -21,4 +21,10 @@\n-jump\n+somersault\n");
        p.addContext("The quick brown fox jumps.");
        assertEquals("PatchEntry.addContext: Not enough trailing context.", "@@ -17,10 +17,16 @@\n fox \n-jump\n+somersault\n s.\n", p.toString());

        p = new PatchEntry("@@ -3 +3,2 @@\n-e\n+at\n");
        p.addContext("The quick brown fox jumps.");
        assertEquals("PatchEntry.addContext: Not enough leading context.", "@@ -1,7 +1,8 @@\n Th\n-e\n+at\n  qui\n", p.toString());

        p = new PatchEntry("@@ -3 +3,2 @@\n-e\n+at\n");
        p.addContext("The quick brown fox jumps.  The quick brown fox crashes.");
        assertEquals("PatchEntry.addContext: Ambiguity.", "@@ -1,27 +1,28 @@\n Th\n-e\n+at\n  quick brown fox jumps. \n", p.toString());
    }

    // Private function for quickly building lists of diffs.
    private static List diffList(Object[] diffs) {
        List myDiffList = new ArrayList();
        myDiffList.addAll(Arrays.asList(diffs));
        return myDiffList;
    }
}
