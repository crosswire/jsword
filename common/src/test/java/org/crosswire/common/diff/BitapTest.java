package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.crosswire.common.diff.Bitap;
import org.crosswire.common.diff.CommonMiddle;
import org.crosswire.common.diff.Commonality;
import org.crosswire.common.diff.Diff;
import org.crosswire.common.diff.Difference;
import org.crosswire.common.diff.EditType;
import org.crosswire.common.diff.LineMap;
import org.crosswire.common.diff.Patch;


public class BitapTest extends TestCase {


  protected void setUp()
  {
  }


  //  MATCH TEST FUNCTIONS


  public void testMatchAlphabet()
  {
    // Initialise the bitmasks for Bitap
    Map bitmask;
    bitmask = new HashMap();
    bitmask.put(new Character('a'), new Integer(4)); bitmask.put(new Character('b'), new Integer(2)); bitmask.put(new Character('c'), new Integer(1));
    Bitap bitap = new Bitap("","abc",0); //$NON-NLS-1$ //$NON-NLS-2$
    bitap.alphabet();
    assertEquals("match_alphabet: Unique.", bitmask, bitap.getAlphabet()); //$NON-NLS-1$
    bitmask = new HashMap();
    bitmask.put(new Character('a'), new Integer(37)); bitmask.put(new Character('b'), new Integer(18)); bitmask.put(new Character('c'), new Integer(8));
    bitap = new Bitap("","abcaba",0); //$NON-NLS-1$ //$NON-NLS-2$
    bitap.alphabet();
    assertEquals("match_alphabet: Duplicates.", bitmask, bitap.getAlphabet()); //$NON-NLS-1$
  }

  public void testMatchBitap()
  {
//    // Bitap algorithm
//    dmp.Match_Balance = 0.5f;
//    dmp.Match_Threshold = 0.5f;
//    dmp.Match_MinLength = 100;
//    dmp.Match_MaxLength = 1000;
//    assertEquals("match_bitap: Exact match #1.", 5, dmp.match_bitap("abcdefghijk", "fgh", 5)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_bitap: Exact match #2.", 5, dmp.match_bitap("abcdefghijk", "fgh", 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_bitap: Fuzzy match #1.", 4, dmp.match_bitap("abcdefghijk", "efxhi", 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_bitap: Fuzzy match #2.", 2, dmp.match_bitap("abcdefghijk", "cdefxyhijk", 5)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_bitap: Fuzzy match #3.", -1, dmp.match_bitap("abcdefghijk", "bxy", 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_bitap: Overflow.", 2, dmp.match_bitap("123456789xx0", "3456789x0", 2)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Match_Threshold = 0.75f;
//    assertEquals("match_bitap: Threshold #1.", 4, dmp.match_bitap("abcdefghijk", "efxyhi", 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Match_Threshold = 0.1f;
//    assertEquals("match_bitap: Threshold #2.", 1, dmp.match_bitap("abcdefghijk", "bcdef", 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Match_Threshold = 0.5f;
//    assertEquals("match_bitap: Multiple select #1.", 0, dmp.match_bitap("abcdexyzabcde", "abccde", 3)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_bitap: Multiple select #2.", 8, dmp.match_bitap("abcdexyzabcde", "abccde", 5)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Match_Balance = 0.6f;         // Strict location, loose accuracy.
//    assertEquals("match_bitap: Balance test #1.", -1, dmp.match_bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_bitap: Balance test #2.", 0, dmp.match_bitap("abcdefghijklmnopqrstuvwxyz", "abcxdxexfgh", 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Match_Balance = 0.4f;         // Strict accuracy loose location.
//    assertEquals("match_bitap: Balance test #3.", 0, dmp.match_bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_bitap: Balance test #4.", -1, dmp.match_bitap("abcdefghijklmnopqrstuvwxyz", "abcxdxexfgh", 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Match_Balance = 0.5f;
  }

}
