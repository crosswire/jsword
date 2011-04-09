/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005-2011
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BitapTest extends TestCase {

    @Override
    protected void setUp() {
    }

    public void testAlphabet() {
        // Initialise the bitmasks for Bitap
        Map<Character,Integer> bitmask;
        bitmask = new HashMap<Character,Integer>();
        bitmask.put(Character.valueOf('a'), Integer.valueOf(4));
        bitmask.put(Character.valueOf('b'), Integer.valueOf(2));
        bitmask.put(Character.valueOf('c'), Integer.valueOf(1));
        Bitap bitap = new Bitap("", "abc", 0);
        bitap.alphabet();
        assertEquals("match_alphabet: Unique.", bitmask, bitap.getAlphabet());
        bitmask = new HashMap<Character,Integer>();
        bitmask.put(Character.valueOf('a'), Integer.valueOf(37));
        bitmask.put(Character.valueOf('b'), Integer.valueOf(18));
        bitmask.put(Character.valueOf('c'), Integer.valueOf(8));
        bitap = new Bitap("", "abcaba", 0);
        bitap.alphabet();
        assertEquals("match_alphabet: Duplicates.", bitmask, bitap.getAlphabet());
    }

    public void testBitap() {
        // Bitap algorithm
        Bitap.setBalance(0.5f);
        Bitap.setThreshold(0.5f);
        Bitap.setMinLength(100);
        Bitap.setMaxLength(1000);
        assertEquals("match_bitap: Exact match #1.", 5, new Bitap("abcdefghijk", "fgh", 5).locate());
        assertEquals("match_bitap: Exact match #2.", 5, new Bitap("abcdefghijk", "fgh", 0).locate());
        assertEquals("match_bitap: Fuzzy match #1.", 4, new Bitap("abcdefghijk", "efxhi", 0).locate());
        assertEquals("match_bitap: Fuzzy match #2.", 2, new Bitap("abcdefghijk", "cdefxyhijk", 5).locate());
        assertEquals("match_bitap: Fuzzy match #3.", -1, new Bitap("abcdefghijk", "bxy", 1).locate());
        assertEquals("match_bitap: Overflow.", 2, new Bitap("123456789xx0", "3456789x0", 2).locate());
        Bitap.setThreshold(0.75f);
        assertEquals("match_bitap: Threshold #1.", 4, new Bitap("abcdefghijk", "efxyhi", 1).locate());
        Bitap.setThreshold(0.1f);
        assertEquals("match_bitap: Threshold #2.", 1, new Bitap("abcdefghijk", "bcdef", 1).locate());
        Bitap.setThreshold(0.5f);
        assertEquals("match_bitap: Multiple select #1.", 0, new Bitap("abcdexyzabcde", "abccde", 3).locate());
        assertEquals("match_bitap: Multiple select #2.", 8, new Bitap("abcdexyzabcde", "abccde", 5).locate());
        Bitap.setBalance(0.6f); // Strict location, loose accuracy.
        assertEquals("match_bitap: Balance test #1.", -1, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24).locate());
        assertEquals("match_bitap: Balance test #2.", 0, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcxdxexfgh", 1).locate());
        Bitap.setBalance(0.4f); // Strict accuracy loose location.
        assertEquals("match_bitap: Balance test #3.", 0, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24).locate());
        assertEquals("match_bitap: Balance test #4.", -1, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcxdxexfgh", 1).locate());
        Bitap.setBalance(0.5f);
    }

}
