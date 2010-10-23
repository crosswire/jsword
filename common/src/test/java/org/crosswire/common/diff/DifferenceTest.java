package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import junit.framework.TestCase;

public class DifferenceTest extends TestCase {

    protected void setUp() {
    }

    public void testHashCode() {
        assertTrue("Difference.hashCode:", (new Difference(EditType.EQUAL, "a")).hashCode() == (new Difference(EditType.EQUAL, "a")).hashCode());
        assertFalse("Difference.hashCode:", (new Difference(EditType.EQUAL, "a")).hashCode() == (new Difference(EditType.EQUAL, "ab")).hashCode());
        assertFalse("Difference.hashCode:", (new Difference(EditType.EQUAL, "a")).hashCode() == (new Difference(EditType.INSERT, "a")).hashCode());
    }

    public void testEquals() {
        // First check that Diff equality works
        assertTrue("Difference.equals:", new Difference(EditType.EQUAL, "a").equals(new Difference(EditType.EQUAL, "a")));
        assertEquals("Difference.equals:", new Difference(EditType.EQUAL, "a"), new Difference(EditType.EQUAL, "a"));
    }
}
