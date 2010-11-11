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

public class LineMapTest extends TestCase {

    protected void setUp() {
    }

    public void testCompile() {
        // Convert lines down to characters
        ArrayList list = new ArrayList();
        list.add("");
        list.add("alpha\n");
        list.add("beta\n");
        LineMap map = new LineMap("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n");
        assertEquals("new LineMap:", "\u0001\u0002\u0001", map.getSourceMap());
        assertEquals("new LineMap:", "\u0002\u0001\u0002", map.getTargetMap());
        assertEquals("new LineMap:", list, map.getLines());

        list.clear();
        list.add("");
        list.add("alpha\r\n");
        list.add("beta\r\n");
        list.add("\r\n");
        map = new LineMap("", "alpha\r\nbeta\r\n\r\n\r\n");
        assertEquals("new LineMap:", "", map.getSourceMap());
        assertEquals("new LineMap:", "\u0001\u0002\u0003\u0003", map.getTargetMap());
        assertEquals("new LineMap:", list, map.getLines());
    }

    public void testRestore() {
        // Convert chars up to lines
        List diffs = diffList(new Object[] {
                new Difference(EditType.EQUAL, "\u0001\u0002\u0001"), new Difference(EditType.INSERT, "\u0002\u0001\u0002")});
        ArrayList list = new ArrayList();
        list.add("");
        list.add("alpha\n");
        list.add("beta\n");
        LineMap map = new LineMap("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n");
        map.restore(diffs);
        assertEquals(
                "LineMap.restore:", diffList(new Object[] { new Difference(EditType.EQUAL, "alpha\nbeta\nalpha\n"), new Difference(EditType.INSERT, "beta\nalpha\nbeta\n")}).get(0), diffs.get(0));
        assertEquals(
                "LineMap.restore:", diffList(new Object[] { new Difference(EditType.EQUAL, "alpha\nbeta\nalpha\n"), new Difference(EditType.INSERT, "beta\nalpha\nbeta\n")}).get(diffs.size() - 1), diffs.get(diffs.size() - 1));
        assertEquals(
                "LineMap.restore:", diffList(new Object[] { new Difference(EditType.EQUAL, "alpha\nbeta\nalpha\n"), new Difference(EditType.INSERT, "beta\nalpha\nbeta\n")}), diffs);
    }

    // Private function for quickly building lists of diffs.
    private static List diffList(Object[] diffs) {
        List myDiffList = new ArrayList();
        myDiffList.addAll(Arrays.asList(diffs));
        return myDiffList;
    }
}
