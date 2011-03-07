package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import junit.framework.TestCase;

import org.crosswire.common.diff.Patch.PatchResults;

public class PatchTest extends TestCase {
    @Override
    protected void setUp() {
    }

    public void testMatchFromText() {
        String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n";
        assertEquals("patch_fromText: #1.", strp, new Patch(strp).toText());
        assertEquals("patch_fromText: #2.", "@@ -1 +1 @@\n-a\n+b\n", new Patch("@@ -1 +1 @@\n-a\n+b\n").toText());
        assertEquals("patch_fromText: #3.", "@@ -1,3 +0,0 @@\n-abc\n", new Patch("@@ -1,3 +0,0 @@\n-abc\n").toText());
        assertEquals("patch_fromText: #4.", "@@ -0,0 +1,3 @@\n+abc\n", new Patch("@@ -0,0 +1,3 @@\n+abc\n").toText());
        assertEquals(
                "patch_fromText: #5.", "@@ -1,9 +1,9 @@\n-f\n+F\n oo fooba\n@@ -7,9 +7,9 @@\n obar\n-,\n+.\n  tes\n", new Patch("@@ -1,9 +1,9 @@\n-f\n+F\n oo fooba\n@@ -7,9 +7,9 @@\n obar\n-,\n+.\n  tes\n").toText());
    }

    public void testMatchMake() {
        Patch p = new Patch("The quick brown fox jumps over the lazy dog.", "That quick brown fox jumped over a lazy dog.");
        assertEquals("patch_make", "@@ -1,11 +1,12 @@\n Th\n-e\n+at\n  quick b\n@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n", p.toText());
        //        p = new Patch("`1234567890-=[]\\;',./", "~!@#$%^&*()_+{}|:\"<>?");
        //        assertEquals("patch_toString: Character encoding.", "@@ -1,21 +1,21 @@\n-%601234567890-=%5B%5D%5C;',./\n+~!@#$%25%5E&*()_+%7B%7D%7C:%22%3C%3E?\n", p.toText());
        //        List diffs = diffList(new Difference(EditType.DELETE, "`1234567890-=[]\\;',./"), new Difference(EditType.INSERT, "~!@#$%^&*()_+{}|:\"<>?"));
        //        assertEquals("patch_fromText: Character decoding.", diffs, new Patch("@@ -1,21 +1,21 @@\n-%601234567890-=%5B%5D%5C;',./\n+~!@#$%25%5E&*()_+%7B%7D%7C:%22%3C%3E?\n").get(0).diffs);
    }

    public void testMatchSplitMax() {
        // Assumes that Match.getMaxPatternLength() is 32.
        Patch p = new Patch("abcdef1234567890123456789012345678901234567890123456789012345678901234567890uvwxyz", "abcdefuvwxyz");
        p.splitMax();
        assertEquals(
                "patch_splitMax:", "@@ -3,32 +3,8 @@\n cdef\n-123456789012345678901234\n 5678\n@@ -27,32 +3,8 @@\n cdef\n-567890123456789012345678\n 9012\n@@ -51,30 +3,8 @@\n cdef\n-9012345678901234567890\n uvwx\n", p.toText());
    }

    public void testMatchApply() {
        Patch p = new Patch("The quick brown fox jumps over the lazy dog.", "That quick brown fox jumped over a lazy dog.");
        PatchResults results = p.apply("The quick brown fox jumps over the lazy dog.");
        boolean[] boolArray = results.getResults();
        String resultStr = results.getText() + "\t" + boolArray[0] + "\t" + boolArray[1];
        assertEquals("patch_apply: Exact match.", "That quick brown fox jumped over a lazy dog.\ttrue\ttrue", resultStr);
        results = p.apply("The quick red rabbit jumps over the tired tiger.");
        boolArray = results.getResults();
        resultStr = results.getText() + "\t" + boolArray[0] + "\t" + boolArray[1];
        assertEquals("patch_apply: Partial match.", "That quick red rabbit jumped over a tired tiger.\ttrue\ttrue", resultStr);
        results = p.apply("I am the very model of a modern major general.");
        boolArray = results.getResults();
        resultStr = results.getText() + "\t" + boolArray[0] + "\t" + boolArray[1];
        assertEquals("patch_apply: Failed match.", "I am the very model of a modern major general.\tfalse\tfalse", resultStr);

        p = new Patch("", "test");
        results = p.apply("");
        boolArray = results.getResults();
        resultStr = results.getText() + "\t" + boolArray[0];
        assertEquals("patch_apply: Exact match against empty string.", "test\ttrue", resultStr);

        p = new Patch("test", "");
        results = p.apply("test");
        boolArray = results.getResults();
        resultStr = results.getText() + "\t" + boolArray[0];
        assertEquals("patch_apply: Exact match against empty string.", "\ttrue", resultStr);
    }

}
