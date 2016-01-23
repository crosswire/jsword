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
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.diff;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class MatchTest {

    @Test
    public void testMatchMain() {
        Assert.assertEquals("match_main: Equality.", 0, new Match("abcdef", "abcdef", 1000).locate());
        Assert.assertEquals("match_main: Null text.", -1, new Match("", "abcdef", 1).locate());
        Assert.assertEquals("match_main: Null pattern.", 3, new Match("abcdef", "", 3).locate());
        Assert.assertEquals("match_main: Exact match.", 3, new Match("abcdef", "de", 3).locate());
        Bitap.setThreshold(0.7f);
        Assert.assertEquals("match_main: Complex match.", 4, new Match("I am the very model of a modern major general.", " that berry ", 5).locate());
        Bitap.setThreshold(0.5f);
    }

}
