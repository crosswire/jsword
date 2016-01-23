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

import java.util.HashMap;
import java.util.Map;

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
public class BitapTest {

    @Test
    public void testAlphabet() {
        // Initialise the bitmasks for Bitap
        Map<Character, Integer> bitmask;
        bitmask = new HashMap<Character, Integer>();
        bitmask.put(Character.valueOf('a'), Integer.valueOf(4));
        bitmask.put(Character.valueOf('b'), Integer.valueOf(2));
        bitmask.put(Character.valueOf('c'), Integer.valueOf(1));
        Bitap bitap = new Bitap("", "abc", 0);
        bitap.alphabet();
        Assert.assertEquals("match_alphabet: Unique.", bitmask, bitap.getAlphabet());
        bitmask = new HashMap<Character, Integer>();
        bitmask.put(Character.valueOf('a'), Integer.valueOf(37));
        bitmask.put(Character.valueOf('b'), Integer.valueOf(18));
        bitmask.put(Character.valueOf('c'), Integer.valueOf(8));
        bitap = new Bitap("", "abcaba", 0);
        bitap.alphabet();
        Assert.assertEquals("match_alphabet: Duplicates.", bitmask, bitap.getAlphabet());
    }

    @Test
    public void testBitap() {
        // Bitap algorithm
        Bitap.setBalance(0.5f);
        Bitap.setThreshold(0.5f);
        Bitap.setMinLength(100);
        Bitap.setMaxLength(1000);
        Assert.assertEquals("match_bitap: Exact match #1.", 5, new Bitap("abcdefghijk", "fgh", 5).locate());
        Assert.assertEquals("match_bitap: Exact match #2.", 5, new Bitap("abcdefghijk", "fgh", 0).locate());
        Assert.assertEquals("match_bitap: Fuzzy match #1.", 4, new Bitap("abcdefghijk", "efxhi", 0).locate());
        Assert.assertEquals("match_bitap: Fuzzy match #2.", 2, new Bitap("abcdefghijk", "cdefxyhijk", 5).locate());
        Assert.assertEquals("match_bitap: Fuzzy match #3.", -1, new Bitap("abcdefghijk", "bxy", 1).locate());
        Assert.assertEquals("match_bitap: Overflow.", 2, new Bitap("123456789xx0", "3456789x0", 2).locate());
        Bitap.setThreshold(0.75f);
        Assert.assertEquals("match_bitap: Threshold #1.", 4, new Bitap("abcdefghijk", "efxyhi", 1).locate());
        Bitap.setThreshold(0.1f);
        Assert.assertEquals("match_bitap: Threshold #2.", 1, new Bitap("abcdefghijk", "bcdef", 1).locate());
        Bitap.setThreshold(0.5f);
        Assert.assertEquals("match_bitap: Multiple select #1.", 0, new Bitap("abcdexyzabcde", "abccde", 3).locate());
        Assert.assertEquals("match_bitap: Multiple select #2.", 8, new Bitap("abcdexyzabcde", "abccde", 5).locate());
        Bitap.setBalance(0.6f); // Strict location, loose accuracy.
        Assert.assertEquals("match_bitap: Balance test #1.", -1, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24).locate());
        Assert.assertEquals("match_bitap: Balance test #2.", 0, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcxdxexfgh", 1).locate());
        Bitap.setBalance(0.4f); // Strict accuracy loose location.
        Assert.assertEquals("match_bitap: Balance test #3.", 0, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24).locate());
        Assert.assertEquals("match_bitap: Balance test #4.", -1, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcxdxexfgh", 1).locate());
        Bitap.setBalance(0.5f);
    }

}
