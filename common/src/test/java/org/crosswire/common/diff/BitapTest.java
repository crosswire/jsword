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


public class BitapTest extends TestCase {


  protected void setUp()
  {
  }

  public void testAlphabet()
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

  public void testBitap()
  {
      // Bitap algorithm
      Bitap.setBalance(0.5f);
      Bitap.setThreshold(0.5f);
      Bitap.setMinLength(100);
      Bitap.setMaxLength(1000);
      assertEquals("match_bitap: Exact match #1.", 5, new Bitap("abcdefghijk", "fgh", 5).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      assertEquals("match_bitap: Exact match #2.", 5, new Bitap("abcdefghijk", "fgh", 0).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      assertEquals("match_bitap: Fuzzy match #1.", 4, new Bitap("abcdefghijk", "efxhi", 0).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      assertEquals("match_bitap: Fuzzy match #2.", 2, new Bitap("abcdefghijk", "cdefxyhijk", 5).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      assertEquals("match_bitap: Fuzzy match #3.", -1, new Bitap("abcdefghijk", "bxy", 1).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      assertEquals("match_bitap: Overflow.", 2, new Bitap("123456789xx0", "3456789x0", 2).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      Bitap.setThreshold(0.75f);
      assertEquals("match_bitap: Threshold #1.", 4, new Bitap("abcdefghijk", "efxyhi", 1).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      Bitap.setThreshold(0.1f);
      assertEquals("match_bitap: Threshold #2.", 1, new Bitap("abcdefghijk", "bcdef", 1).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      Bitap.setThreshold(0.5f);
      assertEquals("match_bitap: Multiple select #1.", 0, new Bitap("abcdexyzabcde", "abccde", 3).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      assertEquals("match_bitap: Multiple select #2.", 8, new Bitap("abcdexyzabcde", "abccde", 5).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      Bitap.setBalance(0.6f);         // Strict location, loose accuracy.
      assertEquals("match_bitap: Balance test #1.", -1, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      assertEquals("match_bitap: Balance test #2.", 0, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcxdxexfgh", 1).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      Bitap.setBalance(0.4f);         // Strict accuracy loose location.
      assertEquals("match_bitap: Balance test #3.", 0, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcdefg", 24).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      assertEquals("match_bitap: Balance test #4.", -1, new Bitap("abcdefghijklmnopqrstuvwxyz", "abcxdxexfgh", 1).locate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      Bitap.setBalance(0.5f);
  }

}
