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


public class diff_match_patch_test extends TestCase {


  protected void setUp()
  {
  }


  //  DIFF TEST FUNCTIONS

  
  public void testDiffPrefix()
  {
    // Detect and remove any common prefix.
    assertEquals("diff_commonPrefix: Null case.", 0, Commonality.prefix("abc", "xyz")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("diff_commonPrefix: Non-null case.", 4, Commonality.prefix("1234abc", "1234xyz")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  public void testDiffSuffix()
  {
    // Detect and remove any common suffix.
    assertEquals("diff_commonSuffix: Null case.", 0, Commonality.suffix("abc", "xyz")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("diff_commonSuffix: Non-null case.", 4, Commonality.suffix("abc1234", "xyz1234")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  public void testDiffHalfmatch()
  {
    // Detect a halfmatch.
    assertNull("diff_halfMatch: No match.", Commonality.halfMatch("1234567890", "abcdef")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("diff_halfMatch: Single Match #1.", new CommonMiddle("12", "90", "345678", "a", "z"), Commonality.halfMatch("1234567890", "a345678z")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    assertEquals("diff_halfMatch: Single Match #2.", new CommonMiddle("a", "z", "345678", "12", "90"), Commonality.halfMatch("a345678z", "1234567890")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    assertEquals("diff_halfMatch: Multiple Matches #1.", new CommonMiddle("12123", "123121", "1234123451234", "a", "z"), Commonality.halfMatch("121231234123451234123121", "a1234123451234z")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    assertEquals("diff_halfMatch: Multiple Matches #2.", new CommonMiddle("", "-=-=-=-=-=", "x-=-=-=-=-=-=-=", "x", ""), Commonality.halfMatch("x-=-=-=-=-=-=-=-=-=-=-=-=", "xx-=-=-=-=-=-=-=")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    assertEquals("diff_halfMatch: Multiple Matches #3.", new CommonMiddle("-=-=-=-=-=", "", "-=-=-=-=-=-=-=y", "", "y"), Commonality.halfMatch("-=-=-=-=-=-=-=-=-=-=-=-=y", "-=-=-=-=-=-=-=yy")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
  }

  public void testDiffLinesToChars()
  {
    // Convert lines down to characters
    ArrayList tmpVector = new ArrayList();
    tmpVector.add(""); //$NON-NLS-1$
    tmpVector.add("alpha\n"); //$NON-NLS-1$
    tmpVector.add("beta\n"); //$NON-NLS-1$
    LineMap map = new LineMap("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n"); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("diff_linesToChars:", "\u0001\u0002\u0001", map.getSourceMap()); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("diff_linesToChars:", "\u0002\u0001\u0002", map.getTargetMap()); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("diff_linesToChars:", tmpVector, map.getLines()); //$NON-NLS-1$

    tmpVector.clear();
    tmpVector.add(""); //$NON-NLS-1$
    tmpVector.add("alpha\r\n"); //$NON-NLS-1$
    tmpVector.add("beta\r\n"); //$NON-NLS-1$
    tmpVector.add("\r\n"); //$NON-NLS-1$
    map = new LineMap("", "alpha\r\nbeta\r\n\r\n\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("diff_linesToChars:", "", map.getSourceMap()); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("diff_linesToChars:", "\u0001\u0002\u0003\u0003", map.getTargetMap()); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals("diff_linesToChars:", tmpVector, map.getLines()); //$NON-NLS-1$
  }

  public void testDiffCharsToLines()
  {
    // First check that Diff equality works
    assertTrue("diff_charsToLines:", new Difference(EditType.EQUAL, "a").equals(new Difference(EditType.EQUAL, "a"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("diff_charsToLines:", new Difference(EditType.EQUAL, "a"), new Difference(EditType.EQUAL, "a")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    // Second check that Diff hash codes work
    assertTrue("diff_charsToLines:", (new Difference(EditType.EQUAL, "a")).hashCode() == (new Difference(EditType.EQUAL, "a")).hashCode()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertFalse("diff_charsToLines:", (new Difference(EditType.EQUAL, "a")).hashCode() == (new Difference(EditType.EQUAL, "ab")).hashCode()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertFalse("diff_charsToLines:", (new Difference(EditType.EQUAL, "a")).hashCode() == (new Difference(EditType.INSERT, "a")).hashCode()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    // Convert chars up to lines
    List diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "\u0001\u0002\u0001"), new Difference(EditType.INSERT, "\u0002\u0001\u0002") }); //$NON-NLS-1$ //$NON-NLS-2$
    ArrayList tmpVector = new ArrayList();
    tmpVector.add(""); //$NON-NLS-1$
    tmpVector.add("alpha\n"); //$NON-NLS-1$
    tmpVector.add("beta\n"); //$NON-NLS-1$
    LineMap map = new LineMap("alpha\nbeta\nalpha\n", "beta\nalpha\nbeta\n"); //$NON-NLS-1$ //$NON-NLS-2$
    map.restore(diffs);
    assertEquals("diff_charsToLines:", diffList(new Object[] {new Difference(EditType.EQUAL, "alpha\nbeta\nalpha\n"), new Difference(EditType.INSERT, "beta\nalpha\nbeta\n") }).get(0), diffs.get(0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("diff_charsToLines:", diffList(new Object[] {new Difference(EditType.EQUAL, "alpha\nbeta\nalpha\n"), new Difference(EditType.INSERT, "beta\nalpha\nbeta\n") }).get(diffs.size() - 1), diffs.get(diffs.size() - 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    assertEquals("diff_charsToLines:", diffList(new Object[] {new Difference(EditType.EQUAL, "alpha\nbeta\nalpha\n"), new Difference(EditType.INSERT, "beta\nalpha\nbeta\n") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

//  public void testDiffCleanupMerge() {
//    // Cleanup a messy diff
//    List diffs = diffList();
//    dmp.diff_cleanupMerge(diffs);
//    assertEquals("diff_cleanupMerge: Null case.", diffList(), diffs); //$NON-NLS-1$
//    diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.INSERT, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.diff_cleanupMerge(diffs);
//    assertEquals("diff_cleanupMerge: No change case.", diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.INSERT, "c") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//    diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.EQUAL, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.diff_cleanupMerge(diffs);
//    assertEquals("diff_cleanupMerge: Merge equalities.", diffList(new Object[] { new Difference(EditType.EQUAL, "abc") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.DELETE, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.diff_cleanupMerge(diffs);
//    assertEquals("diff_cleanupMerge: Merge deletions.", diffList(new Object[] { new Difference(EditType.DELETE, "abc") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$
//    diffs = diffList(new Object[] { new Difference(EditType.INSERT, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.INSERT, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.diff_cleanupMerge(diffs);
//    assertEquals("diff_cleanupMerge: Merge insertions.", diffList(new Object[] { new Difference(EditType.INSERT, "abc") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.DELETE, "c"), new Difference(EditType.INSERT, "d"), new Difference(EditType.EQUAL, "e"), new Difference(EditType.EQUAL, "f") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
//    dmp.diff_cleanupMerge(diffs);
//    assertEquals("diff_cleanupMerge: Merge interweave.", diffList(new Object[] { new Difference(EditType.DELETE, "ac"), new Difference(EditType.INSERT, "bd"), new Difference(EditType.EQUAL, "ef") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "abc"), new Difference(EditType.DELETE, "dc") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.diff_cleanupMerge(diffs);
//    assertEquals("diff_cleanupMerge: Prefix and suffix detection.", diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "d"), new Difference(EditType.INSERT, "b"), new Difference(EditType.EQUAL, "c") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//  }

//  public void testDiffCleanupSemantic() {
//    // Cleanup semantically trivial equalities
//    List diffs = diffList();
//    dmp.diff_cleanupSemantic(diffs);
//    assertEquals("diff_cleanupSemantic: Null case.", diffList(), diffs); //$NON-NLS-1$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.EQUAL, "cd"), new Difference(EditType.DELETE, "e") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//    dmp.diff_cleanupSemantic(diffs);
//    assertEquals("diff_cleanupSemantic: No elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b"), new Difference(EditType.EQUAL, "cd"), new Difference(EditType.DELETE, "e") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.DELETE, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.diff_cleanupSemantic(diffs);
//    assertEquals("diff_cleanupSemantic: Simple elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abc"), new Difference(EditType.INSERT, "b") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "ab"), new Difference(EditType.EQUAL, "cd"), new Difference(EditType.DELETE, "e"), new Difference(EditType.EQUAL, "f"), new Difference(EditType.INSERT, "g") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//    dmp.diff_cleanupSemantic(diffs);
//    assertEquals("diff_cleanupSemantic: Backpass elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abcdef"), new Difference(EditType.INSERT, "cdfg") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//  }

//  public void testDiffCleanupEfficiency() {
//    // Cleanup operationally trivial equalities
//    dmp.Diff_EditCost = 4;
//    List diffs = diffList();
//    dmp.diff_cleanupEfficiency(diffs);
//    assertEquals("diff_cleanupEfficiency: Null case.", diffList(), diffs); //$NON-NLS-1$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//    dmp.diff_cleanupEfficiency(diffs);
//    assertEquals("diff_cleanupEfficiency: No elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "xyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//    dmp.diff_cleanupEfficiency(diffs);
//    assertEquals("diff_cleanupEfficiency: Four-edit elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abxyzcd"), new Difference(EditType.INSERT, "12xyz34") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "x"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//    dmp.diff_cleanupEfficiency(diffs);
//    assertEquals("diff_cleanupEfficiency: Three-edit elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "xcd"), new Difference(EditType.INSERT, "12x34") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "xy"), new Difference(EditType.INSERT, "34"), new Difference(EditType.EQUAL, "z"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "56") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
//    dmp.diff_cleanupEfficiency(diffs);
//    assertEquals("diff_cleanupEfficiency: Backpass elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abxyzcd"), new Difference(EditType.INSERT, "12xy34z56") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Diff_EditCost = 5;
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "ab"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxyz"), new Difference(EditType.DELETE, "cd"), new Difference(EditType.INSERT, "34") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//    dmp.diff_cleanupEfficiency(diffs);
//    assertEquals("diff_cleanupEfficiency: High cost elimination.", diffList(new Object[] { new Difference(EditType.DELETE, "abwxyzcd"), new Difference(EditType.INSERT, "12wxyz34") }), diffs); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Diff_EditCost = 4;
//  }

//  public void testDiffAddIndex() {
//    // Add an index to each diff tuple
//    List diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "12"), new Difference(EditType.EQUAL, "wxy"), new Difference(EditType.INSERT, "34"), new Difference(EditType.EQUAL, "z"), new Difference(EditType.DELETE, "bcd"), new Difference(EditType.INSERT, "56") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
//    dmp.diff_addIndex(diffs);
//    String indexString = ""; //$NON-NLS-1$
//    for (Diff aDiff : diffs) {
//      indexString += aDiff.index + ","; //$NON-NLS-1$
//    }
//    assertEquals("diff_addIndex:", "0,0,2,5,7,8,8,", indexString); //$NON-NLS-1$ //$NON-NLS-2$
//  }

//  public void testDiffPrettyHtml() {
//    // Pretty print
//    List diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a\n"), new Difference(EditType.DELETE, "<B>b</B>"), new Difference(EditType.INSERT, "c&d") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("diff_prettyHtml:", "<SPAN TITLE=\"i=0\">a&para;<BR></SPAN><DEL STYLE=\"background:#FFE6E6;\" TITLE=\"i=2\">&lt;B&gt;b&lt;/B&gt;</DEL><INS STYLE=\"background:#E6FFE6;\" TITLE=\"i=2\">c&amp;d</INS>", dmp.diff_prettyHtml(diffs)); //$NON-NLS-1$ //$NON-NLS-2$
//  }

//  public void testDiffXIndex() {
//    // Translate a location in text1 to text2
//    List diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "1234"), new Difference(EditType.EQUAL, "xyz") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("diff_xIndex: Translation on equality.", 5, dmp.diff_xIndex(diffs, 2)); //$NON-NLS-1$
//    diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "1234"), new Difference(EditType.EQUAL, "xyz") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("diff_xIndex: Translation on deletion.", 1, dmp.diff_xIndex(diffs, 3)); //$NON-NLS-1$
//  }

//  public void testDiffPath() {
//    // Single letters
//    // Trace a path from back to front.
//    List v_map;
//    Set row_set;
//    v_map = new ArrayList();
//    {
//      row_set = new HashSet();
//      row_set.add("0,0"); //$NON-NLS-1$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,1"); row_set.add("1,0"); //$NON-NLS-1$ //$NON-NLS-2$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,2"); row_set.add("2,0"); row_set.add("2,2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,3"); row_set.add("2,3"); row_set.add("3,0"); row_set.add("4,3"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,4"); row_set.add("2,4"); row_set.add("4,0"); row_set.add("4,4"); row_set.add("5,3"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,5"); row_set.add("2,5"); row_set.add("4,5"); row_set.add("5,0"); row_set.add("6,3"); row_set.add("6,5"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,6"); row_set.add("2,6"); row_set.add("4,6"); row_set.add("6,6"); row_set.add("7,5"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//      v_map.add(row_set);
//    }
//    List diffs = diffList(new Object[] { new Difference(EditType.INSERT, "W"), new Difference(EditType.DELETE, "A"), new Difference(EditType.EQUAL, "1"), new Difference(EditType.DELETE, "B"), new Difference(EditType.EQUAL, "2"), new Difference(EditType.INSERT, "X"), new Difference(EditType.DELETE, "C"), new Difference(EditType.EQUAL, "3"), new Difference(EditType.DELETE, "D") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
//    assertEquals("diff_path1: Single letters.", diffs, dmp.diff_path1(v_map, "A1B2C3D", "W12X3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    // Trace a path from front to back.
//    v_map.remove(v_map.size() - 1);
//    diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "4"), new Difference(EditType.DELETE, "E"), new Difference(EditType.INSERT, "Y"), new Difference(EditType.EQUAL, "5"), new Difference(EditType.DELETE, "F"), new Difference(EditType.EQUAL, "6"), new Difference(EditType.DELETE, "G"), new Difference(EditType.INSERT, "Z") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
//    assertEquals("diff_path2: Single letters.", diffs, dmp.diff_path2(v_map, "4E5F6G", "4Y56Z")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//
//    // Double letters
//    // Trace a path from back to front.
//    v_map = new ArrayList();
//    {
//      row_set = new HashSet();
//      row_set.add("0,0"); //$NON-NLS-1$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,1"); row_set.add("1,0"); //$NON-NLS-1$ //$NON-NLS-2$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,2"); row_set.add("1,1"); row_set.add("2,0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,3"); row_set.add("1,2"); row_set.add("2,1"); row_set.add("3,0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,4"); row_set.add("1,3"); row_set.add("3,1"); row_set.add("4,0"); row_set.add("4,4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//      v_map.add(row_set);
//    }
//    diffs = diffList(new Object[] { new Difference(EditType.INSERT, "WX"), new Difference(EditType.DELETE, "AB"), new Difference(EditType.EQUAL, "12") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("diff_path1: Double letters.", diffs, dmp.diff_path1(v_map, "AB12", "WX12")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    // Trace a path from front to back.
//    v_map = new ArrayList();
//    {
//      row_set = new HashSet();
//      row_set.add("0,0"); //$NON-NLS-1$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("0,1"); row_set.add("1,0"); //$NON-NLS-1$ //$NON-NLS-2$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("1,1"); row_set.add("2,0"); row_set.add("2,4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("2,1"); row_set.add("2,5"); row_set.add("3,0"); row_set.add("3,4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//      v_map.add(row_set);
//      row_set = new HashSet();
//      row_set.add("2,6"); row_set.add("3,5"); row_set.add("4,4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//      v_map.add(row_set);
//    }
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "CD"), new Difference(EditType.EQUAL, "34"), new Difference(EditType.INSERT, "YZ") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("diff_path2: Double letters.", diffs, dmp.diff_path2(v_map, "CD34", "34YZ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//  }

//  public void testDiffMain() {
//    // Perform a trivial diff
//    List diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "abc") }); //$NON-NLS-1$
//    assertEquals("diff_main: Null case.", diffs, dmp.diff_main("abc", "abc", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "ab"), new Difference(EditType.INSERT, "123"), new Difference(EditType.EQUAL, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("diff_main: Simple insertion.", diffs, dmp.diff_main("abc", "ab123c", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "123"), new Difference(EditType.EQUAL, "bc") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("diff_main: Simple deletion.", diffs, dmp.diff_main("a123bc", "abc", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.INSERT, "123"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.INSERT, "456"), new Difference(EditType.EQUAL, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//    assertEquals("diff_main: Two insertions.", diffs, dmp.diff_main("abc", "a123b456c", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "123"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.DELETE, "456"), new Difference(EditType.EQUAL, "c") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//    assertEquals("diff_main: Two deletions.", diffs, dmp.diff_main("a123b456c", "abc", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//
//    // Perform a real diff
//    // Switch off the timeout.
//    dmp.Diff_Timeout = 0;
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "a"), new Difference(EditType.INSERT, "b") }); //$NON-NLS-1$ //$NON-NLS-2$
//    assertEquals("diff_main: Simple case #1.", diffs, dmp.diff_main("a", "b", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "Apple"), new Difference(EditType.INSERT, "Banana"), new Difference(EditType.EQUAL, "s are a"), new Difference(EditType.INSERT, "lso"), new Difference(EditType.EQUAL, " fruit.") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//    assertEquals("diff_main: Simple case #2.", diffs, dmp.diff_main("Apples are a fruit.", "Bananas are also fruit.", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.DELETE, "1"), new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "y"), new Difference(EditType.EQUAL, "b"), new Difference(EditType.DELETE, "2"), new Difference(EditType.INSERT, "xab") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
//    assertEquals("diff_main: Overlap #1.", diffs, dmp.diff_main("1ayb2", "abxab", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    diffs = diffList(new Object[] { new Difference(EditType.INSERT, "x"), new Difference(EditType.EQUAL, "a"), new Difference(EditType.DELETE, "b"), new Difference(EditType.INSERT, "x"), new Difference(EditType.EQUAL, "c"), new Difference(EditType.DELETE, "y"), new Difference(EditType.INSERT, "xabc") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
//    assertEquals("diff_main: Overlap #2.", diffs, dmp.diff_main("abcy", "xaxcxabc", false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Diff_Timeout = 0.001f; // 1ms
//    // This test may fail on extremely fast computers.  If so, just increase the text lengths.
//    assertNull("diff_main: Timeout.", dmp.diff_map("`Twas brillig, and the slithy toves\nDid gyre and gimble in the wabe:\nAll mimsy were the borogoves,\nAnd the mome raths outgrabe.", "I am the very model of a modern major general,\nI've information vegetable, animal, and mineral,\nI know the kings of England, and I quote the fights historical,\nFrom Marathon to Waterloo, in order categorical.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Diff_Timeout = 0;
//
//    // Test the linemode speedup
//    // Must be long to pass the 250 char cutoff.
//    String a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n"; //$NON-NLS-1$
//    String b = "abcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\nabcdefghij\n"; //$NON-NLS-1$
//    assertEquals("diff_main: Simple.", dmp.diff_main(a, b, true), dmp.diff_main(a, b, false)); //$NON-NLS-1$
//    a = "1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n1234567890\n"; //$NON-NLS-1$
//    b = "abcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n1234567890\n1234567890\n1234567890\nabcdefghij\n"; //$NON-NLS-1$
//    String[] texts_linemode = diff_rebuildtexts(dmp.diff_main(a, b, true));
//    String[] texts_textmode = diff_rebuildtexts(dmp.diff_main(a, b, false));
//    assertArrayEquals("diff_main: Overlap.", texts_textmode, texts_linemode); //$NON-NLS-1$
//  }

  
  //  MATCH TEST FUNCTIONS


  public void testMatchAlphabet() {
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
    assertEquals("match_alphabet: Duplicates.", bitmask, bitap.getAlphabet()); //$NON-NLS-1$ //$NON-NLS-2$
  }

//  public void testMatchBitap() {
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
//  }

//  public void testMatchMain() {
//    // Full match
//    assertEquals("match_main: Equality.", 0, dmp.match_main("abcdef", "abcdef", 1000)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_main: Null text.", -1, dmp.match_main("", "abcdef", 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_main: Null pattern.", 3, dmp.match_main("abcdef", "", 3)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("match_main: Exact match.", 3, dmp.match_main("abcdef", "de", 3)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Match_Threshold = 0.7f;
//    assertEquals("match_main: Complex match.", 4, dmp.match_main("I am the very model of a modern major general.", " that berry ", 5)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    dmp.Match_Threshold = 0.5f;
//  }


  //  PATCH TEST FUNCTIONS


//  public void testPatchObj() {
//    // Patch Object
//    Patch p = new Patch();
//    p.start1 = 20;
//    p.start2 = 21;
//    p.length1 = 18;
//    p.length2 = 17;
//    p.diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "jump"), new Difference(EditType.DELETE, "s"), new Difference(EditType.INSERT, "ed"), new Difference(EditType.EQUAL, " over "), new Difference(EditType.DELETE, "the"), new Difference(EditType.INSERT, "a"), new Difference(EditType.EQUAL, " laz") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
//    assertEquals("Patch: text1.", "jumps over the laz", p.text1()); //$NON-NLS-1$ //$NON-NLS-2$
//    assertEquals("Patch: text2.", "jumped over a laz", p.text2()); //$NON-NLS-1$ //$NON-NLS-2$
//    String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n"; //$NON-NLS-1$
//    assertEquals("Patch: toString.", strp, p.toString()); //$NON-NLS-1$
//  }

//  public void testMatchFromText() {
//    String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n"; //$NON-NLS-1$
//    assertEquals("patch_fromText: #1.", strp, dmp.patch_fromText(strp).get(0).toString()); //$NON-NLS-1$
//    assertEquals("patch_fromText: #2.", "@@ -1 +1 @@\n-a\n+b\n", dmp.patch_fromText("@@ -1 +1 @@\n-a\n+b\n").get(0).toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("patch_fromText: #3.", "@@ -1,3 +0,0 @@\n-abc\n", dmp.patch_fromText("@@ -1,3 +0,0 @@\n-abc\n").get(0).toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    assertEquals("patch_fromText: #4.", "@@ -0,0 +1,3 @@\n+abc\n", dmp.patch_fromText("@@ -0,0 +1,3 @@\n+abc\n").get(0).toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//  }

//  public void testMatchToText() {
//    String strp = "@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n"; //$NON-NLS-1$
//    Patch p = dmp.patch_fromText(strp).get(0);
//    List patches = new ArrayList();
//    patches.add(p);
//    assertEquals("patch_toText:", strp, dmp.patch_toText(patches)); //$NON-NLS-1$
//  }

//  public void testMatchAddContext() {
//    dmp.Patch_Margin = 4;
//    Patch p = dmp.patch_fromText("@@ -21,4 +21,10 @@\n-jump\n+somersault\n").get(0); //$NON-NLS-1$
//    dmp.patch_addContext(p, "The quick brown fox jumps over the lazy dog."); //$NON-NLS-1$
//    assertEquals("patch_addContext: Simple case.", "@@ -17,12 +17,18 @@\n fox \n-jump\n+somersault\n s ov\n", p.toString()); //$NON-NLS-1$ //$NON-NLS-2$
//    p = dmp.patch_fromText("@@ -21,4 +21,10 @@\n-jump\n+somersault\n").get(0); //$NON-NLS-1$
//    dmp.patch_addContext(p, "The quick brown fox jumps."); //$NON-NLS-1$
//    assertEquals("patch_addContext: Not enough trailing context.", "@@ -17,10 +17,16 @@\n fox \n-jump\n+somersault\n s.\n", p.toString()); //$NON-NLS-1$ //$NON-NLS-2$
//    p = dmp.patch_fromText("@@ -3 +3,2 @@\n-e\n+at\n").get(0); //$NON-NLS-1$
//    dmp.patch_addContext(p, "The quick brown fox jumps."); //$NON-NLS-1$
//    assertEquals("patch_addContext: Not enough leading context.", "@@ -1,7 +1,8 @@\n Th\n-e\n+at\n  qui\n", p.toString()); //$NON-NLS-1$ //$NON-NLS-2$
//    p = dmp.patch_fromText("@@ -3 +3,2 @@\n-e\n+at\n").get(0); //$NON-NLS-1$
//    dmp.patch_addContext(p, "The quick brown fox jumps.  The quick brown fox crashes."); //$NON-NLS-1$
//    assertEquals("patch_addContext: Ambiguity.", "@@ -1,27 +1,28 @@\n Th\n-e\n+at\n  quick brown fox jumps. \n", p.toString()); //$NON-NLS-1$ //$NON-NLS-2$
//  }

//  public void testMatchMake() {
//    List patches;
//    patches = dmp.patch_make("The quick brown fox jumps over the lazy dog.", "That quick brown fox jumped over a lazy dog."); //$NON-NLS-1$ //$NON-NLS-2$
//    assertEquals("patch_make", "@@ -1,11 +1,12 @@\n Th\n-e\n+at\n  quick b\n@@ -21,18 +22,17 @@\n jump\n-s\n+ed\n  over \n-the\n+a\n  laz\n", dmp.patch_toText(patches)); //$NON-NLS-1$ //$NON-NLS-2$
//    patches = dmp.patch_make("`1234567890-=[]\\;',./", "~!@#$%^&*()_+{}|:\"<>?"); //$NON-NLS-1$ //$NON-NLS-2$
//    assertEquals("patch_toString: Character encoding.", "@@ -1,21 +1,21 @@\n-%601234567890-=%5B%5D%5C;',./\n+~!@#$%25%5E&*()_+%7B%7D%7C:%22%3C%3E?\n", dmp.patch_toText(patches)); //$NON-NLS-1$ //$NON-NLS-2$
//    List diffs = diffList(new Object[] { new Difference(EditType.DELETE, "`1234567890-=[]\\;',./"), new Difference(EditType.INSERT, "~!@#$%^&*()_+{}|:\"<>?"))); //$NON-NLS-1$ //$NON-NLS-2$
//    assertEquals("patch_fromText: Character decoding.", diffs, dmp.patch_fromText("@@ -1,21 +1,21 @@\n-%601234567890-=%5B%5D%5C;',./\n+~!@#$%25%5E&*()_+%7B%7D%7C:%22%3C%3E?\n").get(0).diffs); //$NON-NLS-1$ //$NON-NLS-2$
//  }

//  public void testMatchSplitMax() {
//    // Assumes that Match_MaxBits is 32.
//    List patches;
//    patches = dmp.patch_make("abcdef1234567890123456789012345678901234567890123456789012345678901234567890uvwxyz", "abcdefuvwxyz"); //$NON-NLS-1$ //$NON-NLS-2$
//    dmp.patch_splitMax(patches);
//    assertEquals("patch_splitMax:", "@@ -3,32 +3,8 @@\n cdef\n-123456789012345678901234\n 5678\n@@ -27,32 +3,8 @@\n cdef\n-567890123456789012345678\n 9012\n@@ -51,30 +3,8 @@\n cdef\n-9012345678901234567890\n uvwx\n", dmp.patch_toText(patches)); //$NON-NLS-1$ //$NON-NLS-2$
//  }

//  public void testMatchApply() {
//    List patches;
//    patches = dmp.patch_make("The quick brown fox jumps over the lazy dog.", "That quick brown fox jumped over a lazy dog."); //$NON-NLS-1$ //$NON-NLS-2$
//    Object[] results = dmp.patch_apply(patches, "The quick brown fox jumps over the lazy dog."); //$NON-NLS-1$
//    boolean[] boolArray = (boolean[]) results[1];
//    String resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1]; //$NON-NLS-1$ //$NON-NLS-2$
//    assertEquals("patch_apply: Exact match.", "That quick brown fox jumped over a lazy dog.\ttrue\ttrue", resultStr); //$NON-NLS-1$ //$NON-NLS-2$
//    results = dmp.patch_apply(patches, "The quick red rabbit jumps over the tired tiger."); //$NON-NLS-1$
//    boolArray = (boolean[]) results[1];
//    resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1]; //$NON-NLS-1$ //$NON-NLS-2$
//    assertEquals("patch_apply: Partial match.", "That quick red rabbit jumped over a tired tiger.\ttrue\ttrue", resultStr); //$NON-NLS-1$ //$NON-NLS-2$
//    results = dmp.patch_apply(patches, "I am the very model of a modern major general."); //$NON-NLS-1$
//    boolArray = (boolean[]) results[1];
//    resultStr = results[0] + "\t" + boolArray[0] + "\t" + boolArray[1]; //$NON-NLS-1$ //$NON-NLS-2$
//    assertEquals("patch_apply: Failed match.", "I am the very model of a modern major general.\tfalse\tfalse", resultStr); //$NON-NLS-1$ //$NON-NLS-2$
//  }
  
  private void assertArrayEquals(String error_msg, Object[] a, Object[] b) {
    List list_a = Arrays.asList(a);
    List list_b = Arrays.asList(b);
    assertEquals(error_msg, list_a, list_b);
  }


//  // Construct the two texts which made up the diff originally.
//  private static String[] diff_rebuildtexts(List diffs) {
//    String[] text = {"", ""}; //$NON-NLS-1$ //$NON-NLS-2$
//    for (Diff myDiff : diffs) {
//      if (myDiff.operation != EditType.INSERT) {
//        text[0] += myDiff.text;
//      }
//      if (myDiff.operation != EditType.DELETE) {
//        text[1] += myDiff.text;
//      }
//    }
//    return text;
//  }

  
  // Private function for quickly building lists of diffs.
  private static List diffList()
  {
      return new ArrayList();
  }

  // Private function for quickly building lists of diffs.
  private static List diffList(Object[] diffs)
  {
      List myDiffList = new ArrayList();
      myDiffList.addAll(Arrays.asList(diffs));
      return myDiffList;
  }
}
