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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: CallContext.java 1150 2006-10-10 19:28:31 -0400 (Tue, 10 Oct 2006) dmsmith $
 */
package org.crosswire.common.diff;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

import org.crosswire.common.util.IntStack;

/**
 * Computes the difference between two texts to create a patch.
 * Applies the patch onto another text, allowing for errors.
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Diff
{
    //Find the differences between two texts.  Return an array of changes.
  public static List main(String text1, String text2, boolean checklines)
  {
      return null;
    ////If checklines is present and false, then don't run a line-level diff first to identify the changed areas.
    ////Check for equality (speedup)
//if (text1 == text2)
//return [new Difference(EditType.EQUAL, text1]];
//
//if (typeof checklines == 'undefined')
//checklines = true;
//
//var a;
    ////Trim off common prefix (speedup)
//a = Diff.prefix(text1, text2);
//text1 = a[0];
//text2 = a[1];
//var commonprefix = a[2];
//
    ////Trim off common suffix (speedup)
//a = Diff.suffix(text1, text2);
//text1 = a[0];
//text2 = a[1];
//var commonsuffix = a[2];
//
//var diff, i;
//var longtext = text1.length > text2.length ? text1 : text2;
//var shorttext = text1.length > text2.length ? text2 : text1;
//
//if (!text1) {  // Just add some text (speedup)
//diff = [new Difference(EditType.INSERT, text2]];
//} else if (!text2) { // Just delete some text (speedup)
//diff = [new Difference(EditType.DELETE, text1]];
//} else if ((i = longtext.indexOf(shorttext)) != -1) {
//// Shorter text is inside the longer text (speedup)
//diff = [new Difference(EditType.INSERT, longtext.substring(0, i)], new Difference(EditType.EQUAL, shorttext], new Difference(EditType.INSERT, longtext.substring(i+shorttext.length)]];
//// Swap insertions for deletions if diff is reversed.
//if (text1.length > text2.length)
//  diff[0][0] = diff[2][0] = EditType.DELETE;
//} else {
//longtext = shorttext = null; // Garbage collect
//// Check to see if the problem can be split in two.
//var hm = Diff.halfmatch(text1, text2);
//if (hm) {
//  // A half-match was found, sort out the return data.
//  var text1_a = hm[0];
//  var text1_b = hm[1];
//  var text2_a = hm[2];
//  var text2_b = hm[3];
//  var mid_common = hm[4];
//  // Send both pairs off for separate processing.
//  var diff_a = Diff.main(text1_a, text2_a, checklines);
//  var diff_b = Diff.main(text1_b, text2_b, checklines);
//  // Merge the results.
//  diff = diff_a.concat([new Difference(EditType.EQUAL, mid_common]], diff_b);
//} else {
//  // Perform a real diff.
//  if (checklines && text1.length + text2.length < 250)
//    checklines = false; // Too trivial for the overhead.
//  if (checklines) {
//    // Scan the text on a line-by-line basis first.
//    a = Diff.lines2chars(text1, text2);
//    text1 = a[0];
//    text2 = a[1];
//    var linearray = a[2];
//  }
//  diff = Diff.map(text1, text2);
//  if (!diff) // No acceptable result.
//    diff = [new Difference(EditType.DELETE, text1], new Difference(EditType.INSERT, text2]];
//  if (checklines) {
//    Diff.chars2lines(diff, linearray); // Convert the diff back to original text.
//    Diff.cleanup_semantic(diff); // Eliminate freak matches (e.g. blank lines)
//
//    // Rediff any replacement blocks, this time on character-by-character basis.
//    diff.push(new Difference(EditType.EQUAL, ""]);  // Add a dummy entry at the end.
//    var pointer = 0;
//    var count_delete = 0;
//    var count_insert = 0;
//    var text_delete = "";
//    var text_insert = "";
//    while(pointer < diff.length) {
//      if (diff[pointer][0] == EditType.INSERT) {
//        count_insert++;
//        text_insert += diff[pointer][1];
//      } else if (diff[pointer][0] == EditType.DELETE) {
//        count_delete++;
//        text_delete += diff[pointer][1];
//      } else {  // Upon reaching an equality, check for prior redundancies.
//        if (count_delete >= 1 && count_insert >= 1) {
//          // Delete the offending records and add the merged ones.
//          a = Diff.main(text_delete, text_insert, false);
//          diff.splice(pointer - count_delete - count_insert, count_delete + count_insert);
//          pointer = pointer - count_delete - count_insert;
//          for (i=a.length-1; i>=0; i--)
//            diff.splice(pointer, 0, a[i]);
//          pointer = pointer + a.length;
//        }
//        count_insert = 0;
//        count_delete = 0;
//        text_delete = "";
//        text_insert = "";
//      }
//      pointer++;
//    }
//    diff.pop();  // Remove the dummy entry at the end.
//
//  }
//}
//}
//
//if (commonprefix)
//diff.unshift(new Difference(EditType.EQUAL, commonprefix]);
//if (commonsuffix)
//diff.push(new Difference(EditType.EQUAL, commonsuffix]);
//Diff.cleanup_merge(diff);
//return diff;
}


  //Split text into an array of strings.
  //Reduce the texts to a string of hashes where each character represents one line.
  public static List lines2chars(String text1, String text2)
  {
      return null;
//var linearray = new Array();  // linearray[4] == "Hello\n"
//var linehash = new Object();  // linehash["Hello\n"] == 4
//
////"\x00" is a valid JavaScript character, but the Venkman debugger doesn't like it (bug 335098)
////So we'll insert a junk entry to avoid generating a null character.
//linearray.push("");
//
//function lines2chars_munge(text) {
//// My first ever closure!
//var i, line;
//var chars = "";
//while (text) {
//  i = text.indexOf('\n');
//  if (i == -1)
//    i = text.length;
//  line = text.substring(0, i+1);
//  text = text.substring(i+1);
//  if (linehash.hasOwnProperty ? linehash.hasOwnProperty(line) : (linehash[line] !== undefined)) {
//    chars += String.fromCharCode(linehash[line]);
//  } else {
//    linearray.push(line);
//    linehash[line] = linearray.length - 1;
//    chars += String.fromCharCode(linearray.length - 1);
//  }
//}
//return chars;
//}
//
//var chars1 = Diff.lines2chars_munge(text1);
//var chars2 = Diff.lines2chars_munge(text2);
//return [chars1, chars2, linearray];
}


  //Rehydrate the text in a diff from a string of line hashes to real lines of text.
  public static void chars2lines(List diffs, List linearray) {
      String chars;
      StringBuffer text = new StringBuffer();
      for (int x = 0; x < diffs.size(); x++)
      {
          Difference diff = (Difference) diffs.get(x);
          chars = diff.getText();

          for (int y = 0; y < chars.length(); y++)
          {
              text.append(linearray.get(chars.charAt(y)));
          }

          diff.setText(text.toString());
      }
  }


  //Explore the intersection points between the two texts.
  public static String map(String text1, String text2)
  {
      return null;
//var now = new Date();
//var ms_end = now.getTime() + DIFF_TIMEOUT * 1000; // Don't run for too long.
//var max = (text1.length + text2.length) / 2;
//var v_map1 = new Array();
//var v_map2 = new Array();
//var v1 = new Object();
//var v2 = new Object();
//v1[1] = 0;
//v2[1] = 0;
//var x, y;
//var footstep; // Used to track overlapping paths.
//var footsteps = new Object();
//var done = false;
//var hasOwnProperty = !!(footsteps.hasOwnProperty);
////If the total number of characters is odd, then the front path will collide with the reverse path.
//var front = (text1.length + text2.length) % 2;
//for (var d=0; d<max; d++) {
//now = new Date();
//if (DIFF_TIMEOUT > 0 && now.getTime() > ms_end) // Timeout reached
//  return null;
//
//// Walk the front path one step.
//v_map1[d] = new Object();
//for (var k=-d; k<=d; k+=2) {
//  if (k == -d || k != d && v1[k-1] < v1[k+1])
//    x = v1[k+1];
//  else
//    x = v1[k-1]+1;
//  y = x - k;
//  footstep = x+","+y;
//  if (front && (hasOwnProperty ? footsteps.hasOwnProperty(footstep) : (footsteps[footstep] !== undefined)))
//    done = true;
//  if (!front)
//    footsteps[footstep] = d;
//  while (!done && x < text1.length && y < text2.length && text1.charAt(x) == text2.charAt(y)) {
//    x++; y++;
//    footstep = x+","+y;
//    if (front && (hasOwnProperty ? footsteps.hasOwnProperty(footstep) : (footsteps[footstep] !== undefined)))
//      done = true;
//    if (!front)
//      footsteps[footstep] = d;
//  }
//  v1[k] = x;
//  v_map1[d][x+","+y] = true;
//  if (done) {
//    // Front path ran over reverse path.
//    v_map2 = v_map2.slice(0, footsteps[footstep]+1);
//    var a = Diff.path1(v_map1, text1.substring(0, x), text2.substring(0, y));
//    return a.concat(Diff.path2(v_map2, text1.substring(x), text2.substring(y)));
//  }
//}
//
//// Walk the reverse path one step.
//v_map2[d] = new Object();
//for (var k=-d; k<=d; k+=2) {
//  if (k == -d || k != d && v2[k-1] < v2[k+1])
//    x = v2[k+1];
//  else
//    x = v2[k-1]+1;
//  y = x - k;
//  footstep = (text1.length-x)+","+(text2.length-y);
//  if (!front && (hasOwnProperty ? footsteps.hasOwnProperty(footstep) : (footsteps[footstep] !== undefined)))
//    done = true;
//  if (front)
//    footsteps[footstep] = d;
//  while (!done && x < text1.length && y < text2.length && text1.charAt(text1.length-x-1) == text2.charAt(text2.length-y-1)) {
//    x++; y++;
//    footstep = (text1.length-x)+","+(text2.length-y);
//    if (!front && (hasOwnProperty ? footsteps.hasOwnProperty(footstep) : (footsteps[footstep] !== undefined)))
//      done = true;
//    if (front)
//      footsteps[footstep] = d;
//  }
//  v2[k] = x;
//  v_map2[d][x+","+y] = true;
//  if (done) {
//    // Reverse path ran over front path.
//    v_map1 = v_map1.slice(0, footsteps[footstep]+1);
//    var a = Diff.path1(v_map1, text1.substring(0, text1.length-x), text2.substring(0, text2.length-y));
//    return a.concat(Diff.path2(v_map2, text1.substring(text1.length-x), text2.substring(text2.length-y)));
//  }
//}
//}
////Number of diffs equals number of characters, no commonality at all.
//return null;
}


  // Work from the middle back to the start to determine the path.
  public static List path1(Map v_map, String text1, String text2) {
      return null;
//var path = [];
//var x = text1.length;
//var y = text2.length;
//var last_op = null;
//for (var d=v_map.length-2; d>=0; d--) {
//while(1) {
//  if (v_map[d].hasOwnProperty ? v_map[d].hasOwnProperty((x-1)+","+y) : (v_map[d][(x-1)+","+y] !== undefined)) {
//    x--;
//    if (last_op === EditType.DELETE)
//      path[0][1] = text1.charAt(x) + path[0][1];
//    else
//      path.unshift(new Difference(EditType.DELETE, text1.charAt(x)]);
//    last_op = EditType.DELETE;
//    break;
//  } else if (v_map[d].hasOwnProperty ? v_map[d].hasOwnProperty(x+","+(y-1)) : (v_map[d][x+","+(y-1)] !== undefined)) {
//    y--;
//    if (last_op === EditType.INSERT)
//      path[0][1] = text2.charAt(y) + path[0][1];
//    else
//      path.unshift(new Difference(EditType.INSERT, text2.charAt(y)]);
//    last_op = EditType.INSERT;
//    break;
//  } else {
//    x--;
//    y--;
//    //if (text1.charAt(x) != text2.charAt(y))
//    //  return alert("No diagonal.  Can't happen. (Diff.path1)");
//    if (last_op === EditType.EQUAL)
//      path[0][1] = text1.charAt(x) + path[0][1];
//    else
//      path.unshift(new Difference(EditType.EQUAL, text1.charAt(x)]);
//    last_op = EditType.EQUAL;
//  }
//}
//}
//return path;
}


  // Work from the middle back to the end to determine the path.
public static List path2(Map v_map, String text1, String text2) {
    return null;
//var path = [];
//var x = text1.length;
//var y = text2.length;
//var last_op = null;
//for (var d=v_map.length-2; d>=0; d--) {
//while(1) {
//  if (v_map[d].hasOwnProperty ? v_map[d].hasOwnProperty((x-1)+","+y) : (v_map[d][(x-1)+","+y] !== undefined)) {
//    x--;
//    if (last_op === EditType.DELETE)
//      path[path.length-1][1] += text1.charAt(text1.length-x-1);
//    else
//      path.push(new Difference(EditType.DELETE, text1.charAt(text1.length-x-1)]);
//    last_op = EditType.DELETE;
//    break;
//  } else if (v_map[d].hasOwnProperty ? v_map[d].hasOwnProperty(x+","+(y-1)) : (v_map[d][x+","+(y-1)] !== undefined)) {
//    y--;
//    if (last_op === EditType.INSERT)
//      path[path.length-1][1] += text2.charAt(text2.length-y-1);
//    else
//      path.push(new Difference(EditType.INSERT, text2.charAt(text2.length-y-1)]);
//    last_op = EditType.INSERT;
//    break;
//  } else {
//    x--;
//    y--;
//    //if (text1.charAt(text1.length-x-1) != text2.charAt(text2.length-y-1))
//    //  return alert("No diagonal.  Can't happen. (Diff.path2)");
//    if (last_op === EditType.EQUAL)
//      path[path.length-1][1] += text1.charAt(text1.length-x-1);
//    else
//      path.push(new Difference(EditType.EQUAL, text1.charAt(text1.length-x-1)]);
//    last_op = EditType.EQUAL;
//  }
//}
//}
//return path;
}

// Trim off common prefix
public static CommonEnd prefix(String text1, String text2) {
    int pointermin = 0;
    int pointermax = Math.min(text1.length(), text2.length());
    int pointermid = pointermax;
    while (pointermin < pointermid)
    {
        if (text1.substring(0, pointermid) == text2.substring(0, pointermid))
        {
            pointermin = pointermid;
        }
        else
        {
            pointermax = pointermid;
        }
        pointermid = (int) Math.floor((pointermax - pointermin) / 2 + pointermin);
    }
    String commonprefix = text1.substring(0, pointermid);
    String left = text1.substring(pointermid);
    String right = text2.substring(pointermid);
    return new CommonPrefix(left, right, commonprefix);
}


    // Trim off common suffix
public static CommonEnd suffix(String text1, String text2)
{
    int pointermin = 0;
    int pointermax = Math.min(text1.length(), text2.length());
    int pointermid = pointermax;
    while (pointermin < pointermid)
    {
        if (text1.substring(text1.length() - pointermid) == text2.substring(text2.length() - pointermid))
        {
            pointermin = pointermid;
        }
        else
        {
            pointermax = pointermid;
        }
        pointermid = (int) Math.floor((pointermax - pointermin) / 2 + pointermin);
    }
    String commonsuffix = text1.substring(text1.length() - pointermid);
    String left = text1.substring(0, text1.length() - pointermid);
    String right = text2.substring(0, text2.length() - pointermid);
    return new CommonSuffix(left, right, commonsuffix);
}


// Do the two texts share a substring which is at least half the length of the longer text?
public static CommonMiddle halfmatch(String text1, String text2)
{
    String longtext = text1.length() > text2.length() ? text1 : text2;
    String shorttext = text1.length() > text2.length() ? text2 : text1;
    if (longtext.length() < 10 || shorttext.length() < 1)
    {
        return null; // Pointless.
    }

    // First check if the second quarter is the seed for a half-match.
    CommonMiddle hm1 = halfmatch_i(longtext, shorttext, (int) Math.ceil(longtext.length()/4));
    // Check again based on the third quarter.
    CommonMiddle hm2 = halfmatch_i(longtext, shorttext, (int) Math.ceil(longtext.length()/2));
    CommonMiddle hm = null;
    if (hm1 == null && hm2 == null)
    {
        return null;
    }
    else if (hm2 == null)
    {
        hm = hm1;
    }
    else if (hm1 == null)
    {
        hm = hm2;
    }
    else // Both matched.  Select the longest.
    {
        hm = hm1.getCommonality().length() > hm2.getCommonality().length() ? hm1 : hm2;
    }

    String text1_a = ""; //$NON-NLS-1$
    String text1_b = ""; //$NON-NLS-1$
    String text2_a = ""; //$NON-NLS-1$
    String text2_b = ""; //$NON-NLS-1$

    // A half-match was found, sort out the return data.
    if (text1.length() > text2.length())
    {
        text1_a = hm.getLeftStart();
        text1_b = hm.getRightStart();
        text2_a = hm.getLeftEnd();
        text2_b = hm.getRightEnd();
    }
    else
    {
        text2_a = hm.getLeftStart();
        text2_b = hm.getRightStart();
        text1_a = hm.getRightEnd();
        text1_b = hm.getRightEnd();
    }
    String mid_common = hm.getCommonality();
    return new CommonMiddle(text1_a, text1_b, mid_common, text2_a, text2_b);
}

    private static CommonMiddle halfmatch_i(String longtext, String shorttext, int i)
    {
        // Start with a 1/4 length substring at position i as a seed.
        String seed = longtext.substring(i, i + (int) Math.floor(longtext.length()/4));
        int j = -1;
        String best_common = ""; //$NON-NLS-1$
        String best_longtext_a = ""; //$NON-NLS-1$
        String best_longtext_b = ""; //$NON-NLS-1$
        String best_shorttext_a = ""; //$NON-NLS-1$
        String best_shorttext_b = ""; //$NON-NLS-1$
        while ((j = shorttext.indexOf(seed, j + 1)) != -1)
        {
            CommonEnd my_prefix = Diff.prefix(longtext.substring(i), shorttext.substring(j));
            CommonEnd my_suffix = Diff.suffix(longtext.substring(0, i), shorttext.substring(0, j));
            if (best_common.length() < (my_suffix.getCommonality() + my_prefix.getCommonality()).length())
            {
                best_common = my_suffix.getCommonality() + my_prefix.getCommonality();
                best_longtext_a = my_suffix.getLeft();
                best_longtext_b = my_prefix.getLeft();
                best_shorttext_a = my_suffix.getRight();
                best_shorttext_b = my_prefix.getRight();
            }
        }

        if (best_common.length() >= longtext.length()/2)
        {
            return new CommonMiddle(best_longtext_a, best_longtext_b, best_common, best_shorttext_a, best_shorttext_b);
        }

        return null;
    }


    //Reduce the number of edits by eliminating semantically trivial equalities.
    public static void cleanup_semantic(List diffs)
    {
        boolean changes = false;
        Stack equalities = new Stack(); // Stack of indices where equalities are found.
        String lastequality = ""; //$NON-NLS-1$ // Always equal to diff[equalities[equalities.length-1]].getText()
        int length_changes1 = 0; // Number of characters that changed prior to the equality.
        int length_changes2 = 0; // Number of characters that changed after the equality.
        ListIterator pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        while (curDiff != null)
        {
            EditType editType =  curDiff.getEditType();
            if (EditType.EQUAL.equals(editType))
            {
                // equality found
                equalities.push(curDiff);
                length_changes1 = length_changes2;
                length_changes2 = 0;
                lastequality = curDiff.getText();
            }
            else
            {
                // an insertion or deletion
                length_changes2 += curDiff.getText().length();
                int lastLen = lastequality.length();
                if (lastequality != null && (lastLen <= length_changes1) && (lastLen <= length_changes2))
                {
                    // position pointer to the element after the one at the end of the stack
                    while (curDiff != equalities.lastElement())
                    {
                        curDiff = (Difference) pointer.previous();
                    }
                    pointer.next();

                    // Replace equality with a delete.
                    pointer.set(new Difference(EditType.DELETE, lastequality));
                    // Insert a coresponding an insert.
                    pointer.add(new Difference(EditType.INSERT, lastequality));
                    equalities.pop();  // Throw away the equality we just deleted;
                    if (!equalities.empty())
                    {
                        // Throw away the previous equality (it needs to be reevaluated).
                        equalities.pop();
                    }
                    if (equalities.empty())
                    {
                        // There are no previous equalities, walk back to the start.
                        while (pointer.hasPrevious())
                        {
                          pointer.previous();
                        }
                    }
                    else
                    {
                        // There is a safe equality we can fall back to.
                        curDiff = (Difference) equalities.lastElement();
                        while (curDiff != pointer.previous())
                        {
                          // Intentionally empty loop.
                        }
                    }

                    length_changes1 = 0; // Reset the counters.
                    length_changes2 = 0;
                    lastequality = null;
                    changes = true;
                }
            }
            curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        }

        if (changes)
        {
            Diff.cleanup_merge(diffs);
        }
    }


    // Reduce the number of edits by eliminating operationally trivial equalities.
    public static void cleanup_efficiency(List diff)
    {
        boolean changes = false;
        IntStack equalities = new IntStack(); // Stack of indices where equalities are found.
        String lastequality = ""; //$NON-NLS-1$ // Always equal to diff[equalities[equalities.length-1]].getText();
        int pointer = 0; // Index of current position.
        int pre_ins = 0; // Is there an insertion operation before the last equality.
        int pre_del = 0; // Is there an deletion operation before the last equality.
        int post_ins = 0; // Is there an insertion operation after the last equality.
        int post_del = 0; // Is there an deletion operation after the last equality.

        while (pointer < diff.size())
        {
            Difference curDiff = (Difference) diff.get(pointer);
            EditType diff_type = curDiff.getEditType();
            if (EditType.EQUAL.equals(diff_type)) // equality found
            {
                if (curDiff.getText().length() < DIFF_EDIT_COST && (post_ins == 1 || post_del == 1))
                {
                    // Candidate found.
                    equalities.push(pointer);
                    pre_ins = post_ins;
                    pre_del = post_del;
                    lastequality = curDiff.getText();
                }
                else
                {
                    // Not a candidate, and can never become one.
                    equalities.clear();
                    lastequality = ""; //$NON-NLS-1$
                }
                post_ins = 0;
                post_del = 0;
            }
            else
            {
                // an insertion or deletion
                if (EditType.DELETE.equals(diff_type))
                {
                    post_del = 1;
                }
                else
                {
                    post_ins = 1;
                }

                // Five types to be split:
                // <ins>A</ins><del>B</del>XY<ins>C</ins><del>D</del>
                // <ins>A</ins>X<ins>C</ins><del>D</del>
                // <ins>A</ins><del>B</del>X<ins>C</ins>
                // <ins>A</del>X<ins>C</ins><del>D</del>
                // <ins>A</ins><del>B</del>X<del>C</del>
                if (lastequality.length() > 0 && (((pre_ins + pre_del + post_ins + post_del) > 0) || ((lastequality.length() < DIFF_EDIT_COST/2) && (pre_ins + pre_del + post_ins + post_del) == 3)))
                {
                    int newLoc = equalities.peek();
                    Difference newLocDifference = (Difference) diff.get(newLoc);
                    diff.add(newLoc, new Difference(EditType.DELETE, lastequality)); // Duplicate record
                    newLocDifference.setEditType(EditType.INSERT); // Change following to insert.
                    equalities.pop();  // Throw away the equality we just deleted;
                    lastequality = ""; //$NON-NLS-1$
                    if (pre_ins == 1 && pre_del == 1)
                    {
                        // No changes made which could affect previous entry, keep going.
                        post_ins = 1;
                        post_del = 1;
                        equalities.clear();
                    }
                    else
                    {
                        equalities.pop();  // Throw away the previous equality;
                        pointer = equalities.empty() ? -1 : equalities.peek();
                        post_ins = 0;
                        post_del = 0;
                    }
                    changes = true;
                }
            }
            pointer++;
        }

        if (changes)
        {
            Diff.cleanup_merge(diff);
        }
    }


    // Reorder and merge like edit sections.  Merge equalities.
    // Any edit section can move as long as it doesn't cross an equality.
    public static void cleanup_merge(List diff)
    {
        // Add a dummy entry at the end.
        diff.add(new Difference(EditType.EQUAL, ""));  //$NON-NLS-1$
        int pointer = 0;
        int count_delete = 0;
        int count_insert = 0;
        String text_delete = ""; //$NON-NLS-1$
        String text_insert = ""; //$NON-NLS-1$
        CommonEnd my_xfix = null;
        while (pointer < diff.size())
        {
            Difference curDiff = (Difference) diff.get(pointer);
            if (curDiff.getEditType() == EditType.INSERT)
            {
                count_insert++;
                text_insert += curDiff.getText();
                pointer++;
            }
            else if (curDiff.getEditType() == EditType.DELETE)
            {
                count_delete++;
                text_delete += curDiff.getText();
                pointer++;
            }
            else
            {
                // Upon reaching an equality, check for prior redundancies.
                if (count_delete != 0 || count_insert != 0)
                {
                    int modSize = count_delete + count_insert;
                    int modStart = pointer - modSize;
                    if (count_delete != 0 && count_insert != 0)
                    {
                        // Factor out any common prefixies.
                        my_xfix = Diff.prefix(text_insert, text_delete);
                        if (my_xfix.getCommonality().length() > 0)
                        {
                            Difference preModDifference = null;
                            if (modStart > 0)
                            {
                                preModDifference = (Difference) diff.get(modStart - 1);
                            }
                            if (preModDifference != null && EditType.EQUAL.equals(preModDifference.getEditType()))
                            {
                                preModDifference.appendText(my_xfix.getCommonality());
                            }
                            else
                            {
                                // TODO(DMS): Is this correct? shouldn't it be modStart??? was splice(0,0,...)
                                diff.add(0, new Difference(EditType.EQUAL, my_xfix.getCommonality()));
                                pointer++;
                            }
                            text_insert = my_xfix.getLeft();
                            text_delete = my_xfix.getRight();
                        }

                        // Factor out any common suffixies.
                        my_xfix = Diff.suffix(text_insert, text_delete);
                        if (my_xfix.getCommonality().length() > 0)
                        {
                            text_insert = my_xfix.getLeft();
                            text_delete = my_xfix.getRight();
                            curDiff.setText(my_xfix.getCommonality() + curDiff.getText());
                        }
                    }

                    // Delete the offending records and add the merged ones.
                    diff.subList(modStart, modSize).clear();
                    if (count_delete == 0)
                    {
                        diff.add(modStart, new Difference(EditType.INSERT, text_insert));
                    }
                    else if (count_insert == 0)
                    {
                        diff.add(modStart, new Difference(EditType.DELETE, text_delete));
                    }
                    else
                    {
                        diff.add(modStart, new Difference(EditType.DELETE, text_delete));
                        diff.add(modStart + 1, new Difference(EditType.INSERT, text_insert));
                    }

                    // Adjust pointer to account for the delete and the addition of one of two Differences
                    // And to point to the next
                    pointer = pointer - modSize + (count_delete > 0 ? 1 : 0) + (count_insert > 0 ? 1 : 0) + 1;
                }
                else
                {
                    Difference prevDiff = null;
                    if (pointer > 0)
                    {
                        prevDiff = (Difference) diff.get(pointer - 1);
                    }

                    if (prevDiff != null && EditType.EQUAL.equals(prevDiff.getEditType()))
                    {
                        // Merge this equality with the previous one.
                        prevDiff.appendText(curDiff.getText());
                        diff.remove(pointer);
                    }
                    else
                    {
                        pointer++;
                    }
                }

                count_insert = 0;
                count_delete = 0;
                text_delete = ""; //$NON-NLS-1$
                text_insert = ""; //$NON-NLS-1$
            }
        }

        Difference lastDiff = (Difference) diff.get(diff.size() - 1);
        if (lastDiff.getText().length() == 0)
        {
            diff.remove(diff.size() - 1);  // Remove the dummy entry at the end.
        }
    }

    //Add an index to each tuple, represents where the tuple is located in text2.
    //e.g. [new Difference(EditType.DELETE, 'h', 0], new Difference(EditType.INSERT, 'c', 0], new Difference(EditType.EQUAL, 'at', 1]]
    private static void addindex(List diffs)
    {
        int i = 0;
        for (int x = 0; x < diffs.size(); x++)
        {
            Difference diff = (Difference) diffs.get(x);
            diff.setIndex(i);
            if (!EditType.DELETE.equals(diff.getEditType()))
            {
                i += diff.getText().length();
            }
        }
    }


    //loc is a location in text1, compute and return the equivalent location in text2.
    public static int xindex(List diffs, int loc)
    {
        // e.g. "The cat" vs "The big cat", 1->1, 5->8
        int chars1 = 0;
        int chars2 = 0;
        int last_chars1 = 0;
        int last_chars2 = 0;
        int x = 0;
        EditType diff_type = null;
        for (x = 0; x < diffs.size(); x++)
        {
            Difference diff = (Difference) diffs.get(x);
            diff_type = diff.getEditType();

            if (!EditType.INSERT.equals(diff_type)) // Equality or deletion.
            {
                chars1 += diff.getText().length();
            }

            if (!EditType.DELETE.equals(diff_type)) // Equality or insertion.
            {
                chars2 += diff.getText().length();
            }

            if (chars1 > loc) // Overshot the location.
            {
                break;
            }
            last_chars1 = chars1;
            last_chars2 = chars2;
        }

        if (diffs.size() != x && EditType.DELETE.equals(diff_type)) // The location was deleted.
        {
            return last_chars2;
        }

        // Add the remaining character length.
        return last_chars2 + (loc - last_chars1);
    }


    //Convert a diff array into a pretty HTML report.
    public static String prettyhtml(List diffs)
    {
        Diff.addindex(diffs);
        StringBuffer buf = new StringBuffer();
        for (int x = 0; x < diffs.size(); x++)
        {
            Difference diff = (Difference) diffs.get(x);
            EditType m = diff.getEditType(); // Mode (delete, equal, insert)
            String t = diff.getText(); // Text of change.
            int i = 0; //diff.getIndex(); // Index of change.
            // TODO(DMS): Do replacements
            // t = t.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            // t = t.replace(/\n/g, "&para;<BR>");
            if (EditType.DELETE.equals(m))
            {
                buf.append("<DEL STYLE='background:#FFE6E6;' title='i="); //$NON-NLS-1$
                buf.append(i);
                buf.append("'>"); //$NON-NLS-1$
                buf.append(t);
                buf.append("</DEL>"); //$NON-NLS-1$
            }
            else if (EditType.INSERT.equals(m))
            {
                buf.append("<ins style='background:#E6FFE6;' title='i="); //$NON-NLS-1$
                buf.append(i);
                buf.append("'>"); //$NON-NLS-1$
                buf.append(t);
                buf.append("</ins>"); //$NON-NLS-1$
            }
            else
            {
                buf.append("<span title='i="); //$NON-NLS-1$
                buf.append(i);
                buf.append("'>"); //$NON-NLS-1$
                buf.append(t);
                buf.append("</span>"); //$NON-NLS-1$
            }
        }
        return buf.toString();
    }

    /**
     * Represents a common prefix or a common suffix.
     */
    public static class CommonEnd
    {
        /**
         * @param left
         * @param right
         * @param commonality
         */
        public CommonEnd(String left, String right, String commonality)
        {
            this.left = left;
            this.right = right;
            this.commonality = commonality;
        }

        /**
         * @return the left
         */
        public String getLeft()
        {
            return left;
        }

        /**
         * @return the right
         */
        public String getRight()
        {
            return right;
        }

        /**
         * @return the commonality
         */
        public String getCommonality()
        {
            return commonality;
        }

        private String left;
        private String right;
        private String commonality;
    }

    /**
     * Represents a common prefix
     */
    public static class CommonPrefix extends CommonEnd
    {
        /**
         * @param left
         * @param right
         * @param commonality
         */
        public CommonPrefix(String left, String right, String commonality)
        {
            super(left, right, commonality);
        }
        
    }

    /**
     * Represents a common suffix.
     */
    public static class CommonSuffix extends CommonEnd
    {
        /**
         * @param left
         * @param right
         * @param commonality
         */
        public CommonSuffix(String left, String right, String commonality)
        {
            super(left, right, commonality);
        }
    }

    /**
     * Represents a common middle.
     */
    public static class CommonMiddle
    {
        /**
         * @param leftStart
         * @param rightStart
         * @param commonality
         * @param leftEnd
         * @param rightEnd
         */
        public CommonMiddle(String leftStart, String rightStart, String commonality, String leftEnd, String rightEnd)
        {
            super();
            this.leftStart = leftStart;
            this.rightStart = rightStart;
            this.commonality = commonality;
            this.leftEnd = leftEnd;
            this.rightEnd = rightEnd;
        }

        /**
         * @return the left start
         */
        public String getLeftStart()
        {
            return leftStart;
        }

        /**
         * @return the right start
         */
        public String getRightStart()
        {
            return rightStart;
        }

        /**
         * @return the commonality
         */
        public String getCommonality()
        {
            return commonality;
        }

        /**
         * @return the left end
         */
        public String getLeftEnd()
        {
            return leftEnd;
        }

        /**
         * @return the right end
         */
        public String getRightEnd()
        {
            return rightEnd;
        }

        private String leftStart;
        private String rightStart;
        private String commonality;
        private String leftEnd;
        private String rightEnd;
    }

    /**
     * Number of seconds to map a diff before giving up.  (0 for infinity)
     */
    public static final double DIFF_TIMEOUT = 1.0;

    /**
     * Cost of an empty edit operation in terms of edit characters.
     */
    public static final int DIFF_EDIT_COST = 4;

}
