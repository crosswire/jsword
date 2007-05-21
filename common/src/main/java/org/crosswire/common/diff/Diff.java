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
import java.util.Map;

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
//return [[DIFF_EQUAL, text1]];
//
//if (typeof checklines == 'undefined')
//checklines = true;
//
//var a;
    ////Trim off common prefix (speedup)
//a = diff_prefix(text1, text2);
//text1 = a[0];
//text2 = a[1];
//var commonprefix = a[2];
//
    ////Trim off common suffix (speedup)
//a = diff_suffix(text1, text2);
//text1 = a[0];
//text2 = a[1];
//var commonsuffix = a[2];
//
//var diff, i;
//var longtext = text1.length > text2.length ? text1 : text2;
//var shorttext = text1.length > text2.length ? text2 : text1;
//
//if (!text1) {  // Just add some text (speedup)
//diff = [[DIFF_INSERT, text2]];
//} else if (!text2) { // Just delete some text (speedup)
//diff = [[DIFF_DELETE, text1]];
//} else if ((i = longtext.indexOf(shorttext)) != -1) {
//// Shorter text is inside the longer text (speedup)
//diff = [[DIFF_INSERT, longtext.substring(0, i)], [DIFF_EQUAL, shorttext], [DIFF_INSERT, longtext.substring(i+shorttext.length)]];
//// Swap insertions for deletions if diff is reversed.
//if (text1.length > text2.length)
//  diff[0][0] = diff[2][0] = DIFF_DELETE;
//} else {
//longtext = shorttext = null; // Garbage collect
//// Check to see if the problem can be split in two.
//var hm = diff_halfmatch(text1, text2);
//if (hm) {
//  // A half-match was found, sort out the return data.
//  var text1_a = hm[0];
//  var text1_b = hm[1];
//  var text2_a = hm[2];
//  var text2_b = hm[3];
//  var mid_common = hm[4];
//  // Send both pairs off for separate processing.
//  var diff_a = diff_main(text1_a, text2_a, checklines);
//  var diff_b = diff_main(text1_b, text2_b, checklines);
//  // Merge the results.
//  diff = diff_a.concat([[DIFF_EQUAL, mid_common]], diff_b);
//} else {
//  // Perform a real diff.
//  if (checklines && text1.length + text2.length < 250)
//    checklines = false; // Too trivial for the overhead.
//  if (checklines) {
//    // Scan the text on a line-by-line basis first.
//    a = diff_lines2chars(text1, text2);
//    text1 = a[0];
//    text2 = a[1];
//    var linearray = a[2];
//  }
//  diff = diff_map(text1, text2);
//  if (!diff) // No acceptable result.
//    diff = [[DIFF_DELETE, text1], [DIFF_INSERT, text2]];
//  if (checklines) {
//    diff_chars2lines(diff, linearray); // Convert the diff back to original text.
//    diff_cleanup_semantic(diff); // Eliminate freak matches (e.g. blank lines)
//
//    // Rediff any replacement blocks, this time on character-by-character basis.
//    diff.push([DIFF_EQUAL, '']);  // Add a dummy entry at the end.
//    var pointer = 0;
//    var count_delete = 0;
//    var count_insert = 0;
//    var text_delete = '';
//    var text_insert = '';
//    while(pointer < diff.length) {
//      if (diff[pointer][0] == DIFF_INSERT) {
//        count_insert++;
//        text_insert += diff[pointer][1];
//      } else if (diff[pointer][0] == DIFF_DELETE) {
//        count_delete++;
//        text_delete += diff[pointer][1];
//      } else {  // Upon reaching an equality, check for prior redundancies.
//        if (count_delete >= 1 && count_insert >= 1) {
//          // Delete the offending records and add the merged ones.
//          a = diff_main(text_delete, text_insert, false);
//          diff.splice(pointer - count_delete - count_insert, count_delete + count_insert);
//          pointer = pointer - count_delete - count_insert;
//          for (i=a.length-1; i>=0; i--)
//            diff.splice(pointer, 0, a[i]);
//          pointer = pointer + a.length;
//        }
//        count_insert = 0;
//        count_delete = 0;
//        text_delete = '';
//        text_insert = '';
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
//diff.unshift([DIFF_EQUAL, commonprefix]);
//if (commonsuffix)
//diff.push([DIFF_EQUAL, commonsuffix]);
//diff_cleanup_merge(diff);
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
//linearray.push('');
//
//function diff_lines2chars_munge(text) {
//// My first ever closure!
//var i, line;
//var chars = '';
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
//var chars1 = diff_lines2chars_munge(text1);
//var chars2 = diff_lines2chars_munge(text2);
//return [chars1, chars2, linearray];
}


  //Rehydrate the text in a diff from a string of line hashes to real lines of text.
  public static void chars2lines(List diff, List linearray) {
//var chars, text;
//for (var x=0; x<diff.length; x++) {
//chars = diff[x][1];
//text = '';
//for (var y=0; y<chars.length; y++)
//  text += linearray[chars.charCodeAt(y)];
//diff[x][1] = text;
//}
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
//    var a = diff_path1(v_map1, text1.substring(0, x), text2.substring(0, y));
//    return a.concat(diff_path2(v_map2, text1.substring(x), text2.substring(y)));
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
//    var a = diff_path1(v_map1, text1.substring(0, text1.length-x), text2.substring(0, text2.length-y));
//    return a.concat(diff_path2(v_map2, text1.substring(text1.length-x), text2.substring(text2.length-y)));
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
//    if (last_op === DIFF_DELETE)
//      path[0][1] = text1.charAt(x) + path[0][1];
//    else
//      path.unshift([DIFF_DELETE, text1.charAt(x)]);
//    last_op = DIFF_DELETE;
//    break;
//  } else if (v_map[d].hasOwnProperty ? v_map[d].hasOwnProperty(x+","+(y-1)) : (v_map[d][x+","+(y-1)] !== undefined)) {
//    y--;
//    if (last_op === DIFF_INSERT)
//      path[0][1] = text2.charAt(y) + path[0][1];
//    else
//      path.unshift([DIFF_INSERT, text2.charAt(y)]);
//    last_op = DIFF_INSERT;
//    break;
//  } else {
//    x--;
//    y--;
//    //if (text1.charAt(x) != text2.charAt(y))
//    //  return alert("No diagonal.  Can't happen. (diff_path1)");
//    if (last_op === DIFF_EQUAL)
//      path[0][1] = text1.charAt(x) + path[0][1];
//    else
//      path.unshift([DIFF_EQUAL, text1.charAt(x)]);
//    last_op = DIFF_EQUAL;
//  }
//}
//}
//return path;
}


  ////Work from the middle back to the end to determine the path.
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
//    if (last_op === DIFF_DELETE)
//      path[path.length-1][1] += text1.charAt(text1.length-x-1);
//    else
//      path.push([DIFF_DELETE, text1.charAt(text1.length-x-1)]);
//    last_op = DIFF_DELETE;
//    break;
//  } else if (v_map[d].hasOwnProperty ? v_map[d].hasOwnProperty(x+","+(y-1)) : (v_map[d][x+","+(y-1)] !== undefined)) {
//    y--;
//    if (last_op === DIFF_INSERT)
//      path[path.length-1][1] += text2.charAt(text2.length-y-1);
//    else
//      path.push([DIFF_INSERT, text2.charAt(text2.length-y-1)]);
//    last_op = DIFF_INSERT;
//    break;
//  } else {
//    x--;
//    y--;
//    //if (text1.charAt(text1.length-x-1) != text2.charAt(text2.length-y-1))
//    //  return alert("No diagonal.  Can't happen. (diff_path2)");
//    if (last_op === DIFF_EQUAL)
//      path[path.length-1][1] += text1.charAt(text1.length-x-1);
//    else
//      path.push([DIFF_EQUAL, text1.charAt(text1.length-x-1)]);
//    last_op = DIFF_EQUAL;
//  }
//}
//}
//return path;
}

//Trim off common prefix
public static void prefix(String text1, String text2) {
//var pointermin = 0;
//var pointermax = Math.min(text1.length, text2.length);
//var pointermid = pointermax;
//while(pointermin < pointermid) {
//if (text1.substring(0, pointermid) == text2.substring(0, pointermid))
//  pointermin = pointermid;
//else
//  pointermax = pointermid;
//pointermid = Math.floor((pointermax - pointermin) / 2 + pointermin);
//}
//var commonprefix = text1.substring(0, pointermid);
//text1 = text1.substring(pointermid);
//text2 = text2.substring(pointermid);
//return [text1, text2, commonprefix];
}


    // Trim off common suffix
public static void suffix(String text1, String text2)
{
//var pointermin = 0;
//var pointermax = Math.min(text1.length, text2.length);
//var pointermid = pointermax;
//while(pointermin < pointermid) {
//if (text1.substring(text1.length-pointermid) == text2.substring(text2.length-pointermid))
//  pointermin = pointermid;
//else
//  pointermax = pointermid;
//pointermid = Math.floor((pointermax - pointermin) / 2 + pointermin);
//}
//var commonsuffix = text1.substring(text1.length-pointermid);
//text1 = text1.substring(0, text1.length-pointermid);
//text2 = text2.substring(0, text2.length-pointermid);
//return [text1, text2, commonsuffix];
}


    // Do the two texts share a substring which is at least half the length of the longer text?
    public static void halfmatch(String text1, String text2) {
//var longtext = text1.length > text2.length ? text1 : text2;
//var shorttext = text1.length > text2.length ? text2 : text1;
//if (longtext.length < 10 || shorttext.length < 1)
//return null; // Pointless.
//
//function diff_halfmatch_i(longtext, shorttext, i) {
//// Start with a 1/4 length substring at position i as a seed.
//var seed = longtext.substring(i, i+Math.floor(longtext.length/4));
//var j = -1;
//var best_common = '';
//var best_longtext_a, best_longtext_b, best_shorttext_a, best_shorttext_b;
//while ((j = shorttext.indexOf(seed, j+1)) != -1) {
//  var my_prefix = diff_prefix(longtext.substring(i), shorttext.substring(j));
//  var my_suffix = diff_suffix(longtext.substring(0, i), shorttext.substring(0, j));
//  if (best_common.length < (my_suffix[2] + my_prefix[2]).length) {
//    best_common = my_suffix[2] + my_prefix[2];
//    best_longtext_a = my_suffix[0];
//    best_longtext_b = my_prefix[0];
//    best_shorttext_a = my_suffix[1];
//    best_shorttext_b = my_prefix[1];
//  }
//}
//if (best_common.length >= longtext.length/2)
//  return [best_longtext_a, best_longtext_b, best_shorttext_a, best_shorttext_b, best_common];
//else
//  return null;
//}
//
////First check if the second quarter is the seed for a half-match.
//var hm1 = diff_halfmatch_i(longtext, shorttext, Math.ceil(longtext.length/4));
////Check again based on the third quarter.
//var hm2 = diff_halfmatch_i(longtext, shorttext, Math.ceil(longtext.length/2));
//var hm;
//if (!hm1 && !hm2)
//return null;
//else if (!hm2)
//hm = hm1;
//else if (!hm1)
//hm = hm2;
//else // Both matched.  Select the longest.
//hm = hm1[4].length > hm2[4].length ? hm1 : hm2;
//
////A half-match was found, sort out the return data.
//if (text1.length > text2.length) {
//var text1_a = hm[0];
//var text1_b = hm[1];
//var text2_a = hm[2];
//var text2_b = hm[3];
//} else {
//var text2_a = hm[0];
//var text2_b = hm[1];
//var text1_a = hm[2];
//var text1_b = hm[3];
//}
//var mid_common = hm[4];
//return [text1_a, text1_b, text2_a, text2_b, mid_common];
}


////Reduce the number of edits by eliminating semantically trivial equalities.
public static void cleanup_semantic(List diff) {
//var changes = false;
//var equalities = []; // Stack of indices where equalities are found.
//var lastequality = null; // Always equal to equalities[equalities.length-1][1]
//var pointer = 0; // Index of current position.
//var length_changes1 = 0; // Number of characters that changed prior to the equality.
//var length_changes2 = 0; // Number of characters that changed after the equality.
//while (pointer < diff.length) {
//if (diff[pointer][0] == DIFF_EQUAL) { // equality found
//  equalities.push(pointer);
//  length_changes1 = length_changes2;
//  length_changes2 = 0;
//  lastequality = diff[pointer][1];
//} else { // an insertion or deletion
//  length_changes2 += diff[pointer][1].length;
//  if (lastequality != null && (lastequality.length <= length_changes1) && (lastequality.length <= length_changes2)) {
//    //alert("Splitting: '"+lastequality+"'");
//    diff.splice(equalities[equalities.length-1], 0, [DIFF_DELETE, lastequality]); // Duplicate record
//    diff[equalities[equalities.length-1]+1][0] = DIFF_INSERT; // Change second copy to insert.
//    equalities.pop();  // Throw away the equality we just deleted;
//    equalities.pop();  // Throw away the previous equality;
//    pointer = equalities.length ? equalities[equalities.length-1] : -1;
//    length_changes1 = 0; // Reset the counters.
//    length_changes2 = 0;
//    lastequality = null;
//    changes = true;
//  }
//}
//pointer++;
//}
//
//if (changes)
//diff_cleanup_merge(diff);
}


    // Reduce the number of edits by eliminating operationally trivial equalities.
public static void cleanup_efficiency(List diff)
{
//var changes = false;
//var equalities = []; // Stack of indices where equalities are found.
//var lastequality = ''; // Always equal to equalities[equalities.length-1][1]
//var pointer = 0; // Index of current position.
//var pre_ins = false; // Is there an insertion operation before the last equality.
//var pre_del = false; // Is there an deletion operation before the last equality.
//var post_ins = false; // Is there an insertion operation after the last equality.
//var post_del = false; // Is there an deletion operation after the last equality.
//while (pointer < diff.length) {
//if (diff[pointer][0] == DIFF_EQUAL) { // equality found
//  if (diff[pointer][1].length < DIFF_EDIT_COST && (post_ins || post_del)) {
//    // Candidate found.
//    equalities.push(pointer);
//    pre_ins = post_ins;
//    pre_del = post_del;
//    lastequality = diff[pointer][1];
//  } else {
//    // Not a candidate, and can never become one.
//    equalities = [];
//    lastequality = '';
//  }
//  post_ins = post_del = false;
//} else { // an insertion or deletion
//  if (diff[pointer][0] == DIFF_DELETE)
//    post_del = true;
//  else
//    post_ins = true;
//  // Five types to be split:
//  // <ins>A</ins><del>B</del>XY<ins>C</ins><del>D</del>
//  // <ins>A</ins>X<ins>C</ins><del>D</del>
//  // <ins>A</ins><del>B</del>X<ins>C</ins>
//  // <ins>A</del>X<ins>C</ins><del>D</del>
//  // <ins>A</ins><del>B</del>X<del>C</del>
//  if (lastequality && ((pre_ins && pre_del && post_ins && post_del) || ((lastequality.length < DIFF_EDIT_COST/2) && (pre_ins + pre_del + post_ins + post_del) == 3))) {
//    //alert("Splitting: '"+lastequality+"'");
//    diff.splice(equalities[equalities.length-1], 0, [DIFF_DELETE, lastequality]); // Duplicate record
//    diff[equalities[equalities.length-1]+1][0] = DIFF_INSERT; // Change second copy to insert.
//    equalities.pop();  // Throw away the equality we just deleted;
//    lastequality = '';
//    if (pre_ins && pre_del) {
//      // No changes made which could affect previous entry, keep going.
//      post_ins = post_del = true;
//      equalities = [];
//    } else {
//      equalities.pop();  // Throw away the previous equality;
//      pointer = equalities.length ? equalities[equalities.length-1] : -1;
//      post_ins = post_del = false;
//    }
//    changes = true;
//  }
//}
//pointer++;
//}
//
//if (changes)
//diff_cleanup_merge(diff);
}


    // Reorder and merge like edit sections.  Merge equalities.
    // Any edit section can move as long as it doesn't cross an equality.
public static void cleanup_merge(List diff)
{
//diff.push([DIFF_EQUAL, '']);  // Add a dummy entry at the end.
//var pointer = 0;
//var count_delete = 0;
//var count_insert = 0;
//var text_delete = '';
//var text_insert = '';
//var record_insert, record_delete;
//var my_xfix;
//while(pointer < diff.length) {
//if (diff[pointer][0] == DIFF_INSERT) {
//  count_insert++;
//  text_insert += diff[pointer][1];
//  pointer++;
//} else if (diff[pointer][0] == DIFF_DELETE) {
//  count_delete++;
//  text_delete += diff[pointer][1];
//  pointer++;
//} else {  // Upon reaching an equality, check for prior redundancies.
//  if (count_delete != 0 || count_insert != 0) {
//    if (count_delete != 0 && count_insert != 0) {
//      // Factor out any common prefixies.
//      my_xfix = diff_prefix(text_insert, text_delete);
//      if (my_xfix[2] != '') {
//        if ((pointer - count_delete - count_insert) > 0 && diff[pointer - count_delete - count_insert - 1][0] == DIFF_EQUAL) {
//          diff[pointer - count_delete - count_insert - 1][1] += my_xfix[2];
//        } else {
//          diff.splice(0, 0, [DIFF_EQUAL, my_xfix[2]]);
//          pointer++;
//        }
//        text_insert = my_xfix[0];
//        text_delete = my_xfix[1];
//      }
//      // Factor out any common suffixies.
//      my_xfix = diff_suffix(text_insert, text_delete);
//      if (my_xfix[2] != '') {
//        text_insert = my_xfix[0];
//        text_delete = my_xfix[1];
//        diff[pointer][1] = my_xfix[2] + diff[pointer][1];
//      }
//    }
//    // Delete the offending records and add the merged ones.
//    if (count_delete == 0)
//      diff.splice(pointer - count_delete - count_insert, count_delete + count_insert, [DIFF_INSERT, text_insert]);
//    else if (count_insert == 0)
//      diff.splice(pointer - count_delete - count_insert, count_delete + count_insert, [DIFF_DELETE, text_delete]);
//    else
//      diff.splice(pointer - count_delete - count_insert, count_delete + count_insert, [DIFF_DELETE, text_delete], [DIFF_INSERT, text_insert]);
//    pointer = pointer - count_delete - count_insert + (count_delete ? 1 : 0) + (count_insert ? 1 : 0) + 1;
//  } else if (pointer != 0 && diff[pointer-1][0] == DIFF_EQUAL) {
//    // Merge this equality with the previous one.
//    diff[pointer-1][1] += diff[pointer][1];
//    diff.splice(pointer, 1);
//  } else {
//    pointer++;
//  }
//  count_insert = 0;
//  count_delete = 0;
//  text_delete = '';
//  text_insert = '';
//}
//}
//if (diff[diff.length-1][1] == '')
//diff.pop();  // Remove the dummy entry at the end.
}



    //Add an index to each tuple, represents where the tuple is located in text2.
    //e.g. [[DIFF_DELETE, 'h', 0], [DIFF_INSERT, 'c', 0], [DIFF_EQUAL, 'at', 1]]
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
     * Number of seconds to map a diff before giving up.  (0 for infinity)
     */
    public static final double DIFF_TIMEOUT = 1.0;

    /**
     * Cost of an empty edit operation in terms of edit characters.
     */
    public static final int DIFF_EDIT_COST = 4;

}
//
//
////Defaults.
////Redefine these in your program to override the defaults.
//
////Number of seconds to map a diff before giving up.  (0 for infinity)
//var DIFF_TIMEOUT = 1.0;
////Cost of an empty edit operation in terms of edit characters.
//var DIFF_EDIT_COST = 4;
////Tweak the relative importance (0.0 = accuracy, 1.0 = proximity)
//var MATCH_BALANCE = 0.5;
////At what point is no match declared (0.0 = perfection, 1.0 = very loose)
//var MATCH_THRESHOLD = 0.5;
////The min and max cutoffs used when computing text lengths.
//var MATCH_MINLENGTH = 100;
//var MATCH_MAXLENGTH = 1000;
////Chunk size for context length.
//var PATCH_MARGIN = 4;
//
//
////////////////////////////////////////////////////////////////////////
////  Diff                                                            //
////////////////////////////////////////////////////////////////////////
//
////The data structure representing a diff is an array of tuples:
////[[DIFF_DELETE, "Hello"], [DIFF_INSERT, "Goodbye"], [DIFF_EQUAL, " world."]]
////which means: delete "Hello", add "Goodbye" and keep " world."
//var DIFF_DELETE = -1;
//var DIFF_INSERT = 1;
//var DIFF_EQUAL = 0;
