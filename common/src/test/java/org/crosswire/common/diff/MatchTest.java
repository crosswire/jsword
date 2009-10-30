package org.crosswire.common.diff;

import junit.framework.TestCase;

public class MatchTest extends TestCase {

    protected void setUp() {
    }

    public void testMatchMain() {
        assertEquals("match_main: Equality.", 0, new Match("abcdef", "abcdef", 1000).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("match_main: Null text.", -1, new Match("", "abcdef", 1).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("match_main: Null pattern.", 3, new Match("abcdef", "", 3).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("match_main: Exact match.", 3, new Match("abcdef", "de", 3).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Bitap.setThreshold(0.7f);
        assertEquals("match_main: Complex match.", 4, new Match("I am the very model of a modern major general.", " that berry ", 5).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Bitap.setThreshold(0.5f);
    }

}
