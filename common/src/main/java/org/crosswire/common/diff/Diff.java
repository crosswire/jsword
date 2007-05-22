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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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
    /**
     * Find the differences between two texts.
     * Run a faster slightly less optimal diff
     * This method allows the 'checklines' of diff_main() to be optional.
     * Most of the time checklines is wanted, so default to true.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     * @return List of Diff objects
     */
    public static List main(String text1, String text2)
    {
        return main(text1, text2, true);
    }

    /**
     * Find the differences between two texts.  Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff
     * @return List of Diff objects
     */
    public static List main(String text1, String text2, boolean checklines)
    {
        // Check for equality (speedup)
        List diffs;
        if (text1.equals(text2))
        {
            diffs = new LinkedList();
            diffs.add(new Difference(EditType.EQUAL, text1));
            return diffs;
        }

        // Trim off common prefix (speedup)
        int commonlength = commonPrefix(text1, text2);
        String commonprefix = text1.substring(0, commonlength);
        String work1 = text1.substring(commonlength);
        String work2 = text2.substring(commonlength);

        // Trim off common suffix (speedup)
        commonlength = commonSuffix(work1, work2);
        String commonsuffix = work1.substring(work1.length() - commonlength);
        work1 = work1.substring(0, work1.length() - commonlength);
        work2 = work2.substring(0, work2.length() - commonlength);

        // Compute the diff on the middle block
        diffs = compute(work1, work2, checklines);

        // Restore the prefix and suffix
        if (!commonprefix.equals("")) //$NON-NLS-1$
        {
            diffs.add(0, new Difference(EditType.EQUAL, commonprefix));
        }

        if (!commonsuffix.equals("")) //$NON-NLS-1$
        {
            diffs.add(new Difference(EditType.EQUAL, commonsuffix));
        }

        cleanupMerge(diffs);

        return diffs;
    }

    /**
     * Find the differences between two texts.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     * @param checklines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff
     * @return Linked List of Diff objects
     */
    public static List compute(String text1, String text2, boolean checklines)
    {
        List diffs = new ArrayList();

        if (text1.equals("")) //$NON-NLS-1$
        {
            // Just add some text (speedup)
            diffs.add(new Difference(EditType.INSERT, text2));
            return diffs;
        }

        if (text2.equals("")) //$NON-NLS-1$
        {
            // Just delete some text (speedup)
            diffs.add(new Difference(EditType.DELETE, text1));
            return diffs;
        }

        String longtext = text1.length() > text2.length() ? text1 : text2;
        String shorttext = text1.length() > text2.length() ? text2 : text1;
        int i = longtext.indexOf(shorttext);
        if (i != -1)
        {
            // Shorter text is inside the longer text (speedup)
            EditType op = (text1.length() > text2.length()) ? EditType.DELETE : EditType.INSERT;
            diffs.add(new Difference(op, longtext.substring(0, i)));
            diffs.add(new Difference(EditType.EQUAL, shorttext));
            diffs.add(new Difference(op, longtext.substring(i + shorttext.length())));
        }

        // Garbage collect
        longtext = null;
        shorttext = null;

        // Check to see if the problem can be split in two.
        CommonMiddle hm = halfMatch(text1, text2);
        if (hm != null)
        {
            // A half-match was found, sort out the return data.
            // Send both pairs off for separate processing.
            List diffs_a = main(hm.getLeftStart(), hm.getRightStart(), checklines);
            List diffs_b = main(hm.getLeftEnd(), hm.getRightEnd(), checklines);
            // Merge the results.
            diffs = diffs_a;
            diffs.add(new Difference(EditType.EQUAL, hm.getCommonality()));
            diffs.addAll(diffs_b);
            return diffs;
        }

        // Perform a real diff.
        if (checklines && text1.length() + text2.length() < 250)
        {
            checklines = false; // Too trivial for the overhead.
        }

        ArrayList linearray = null;
        if (checklines)
        {
            // Scan the text on a line-by-line basis first.
            Object[] b = linesToChars(text1, text2);
            text1 = (String) b[0];
            text2 = (String) b[1];
            linearray = (ArrayList) b[2];
        }

        diffs = map(text1, text2);
        if (diffs == null)
        {
            // No acceptable result.
            diffs = new ArrayList();
            diffs.add(new Difference(EditType.DELETE, text1));
            diffs.add(new Difference(EditType.INSERT, text2));
        }

        if (checklines)
        {
            // Convert the diff back to original text.
            charsToLines(diffs, linearray);
            // Eliminate freak matches (e.g. blank lines)
            cleanupSemantic(diffs);

            // Rediff any replacement blocks, this time character-by-character.
            // Add a dummy entry at the end.
            diffs.add(new Difference(EditType.EQUAL, "")); //$NON-NLS-1$
            int count_delete = 0;
            int count_insert = 0;
            String text_delete = ""; //$NON-NLS-1$
            String text_insert = ""; //$NON-NLS-1$
            ListIterator pointer = diffs.listIterator();
            Difference thisDiff = (Difference) pointer.next();
            while (thisDiff != null)
            {
                if (thisDiff.getEditType() == EditType.INSERT)
                {
                    count_insert++;
                    text_insert += thisDiff.getText();
                }
                else if (thisDiff.getEditType() == EditType.DELETE)
                {
                    count_delete++;
                    text_delete += thisDiff.getText();
                }
                else
                {
                    // Upon reaching an equality, check for prior redundancies.
                    if (count_delete >= 1 && count_insert >= 1)
                    {
                        // Delete the offending records and add the merged ones.
                        pointer.previous();
                        for (int j = 0; j < count_delete + count_insert; j++)
                        {
                            pointer.previous();
                            pointer.remove();
                        }
                        List newDiffs = main(text_delete, text_insert, false);
                        Iterator iter = newDiffs.iterator();
                        while (iter.hasNext())
                        {
                            pointer.add(iter.next());
                        }
                    }
                    count_insert = 0;
                    count_delete = 0;
                    text_delete = ""; //$NON-NLS-1$
                    text_insert = ""; //$NON-NLS-1$
                }
                thisDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
            }
            diffs.remove(diffs.size() - 1); // Remove the dummy entry at the end.
        }
        return diffs;
    }

    /**
     * Split two texts into a list of strings.  Reduce the texts to a string of
     * hashes where each Unicode character represents one line.
     * @param text1 First string
     * @param text2 Second string
     * @return Three element Object array, containing the encoded text1, the
     *     encoded text2 and the List of unique strings.  The zeroth element
     *     of the List of unique strings is intentionally blank.
     */
    public static Object[] linesToChars(String text1, String text2)
    {
        List linearray = new ArrayList();
        Map linehash = new HashMap();
        // e.g. linearray[4] == "Hello\n"
        // e.g. linehash.get("Hello\n") == 4

        // "\x00" is a valid character, but various debuggers don't like it.
        // So we'll insert a junk entry to avoid generating a null character.
        linearray.add(""); //$NON-NLS-1$

        String chars1 = linesToCharsMunge(text1, linearray, linehash);
        String chars2 = linesToCharsMunge(text2, linearray, linehash);
        return new Object[]
        {
                        chars1, chars2, linearray
        };
    }

    /**
     * Split a text into a list of strings.  Reduce the texts to a string of
     * hashes where each Unicode character represents one line.
     * @param text String to encode
     * @param linearray List of unique strings
     * @param linehash Map of strings to indices
     * @return Encoded string
     */
    private static String linesToCharsMunge(String text, List linearray, Map linehash)
    {
        int i;
        String line;
        String chars = ""; //$NON-NLS-1$
        String work = text;
        // text.split('\n') would work fine, but would temporarily double our
        // memory footprint for minimal speed improvement.
        while (work.length() != 0)
        {
            i = work.indexOf('\n');
            if (i == -1)
            {
                i = work.length() - 1;
            }
            line = work.substring(0, i + 1);
            work = work.substring(i + 1);
            if (linehash.containsKey(line))
            {
                Integer charInt = (Integer) linehash.get(line);
                chars += String.valueOf((char) charInt.intValue());
            }
            else
            {
                linearray.add(line);
                linehash.put(line, new Integer(linearray.size() - 1));
                chars += String.valueOf((char) (linearray.size() - 1));
            }
        }
        return chars;
    }

    /**
     * Rehydrate the text in a diff from a string of line hashes to real lines of
     * text.
     * @param diffs LinkedList of Diff objects
     * @param linearray List of unique strings
     */
    public static void charsToLines(List diffs, List linearray)
    {
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

    /**
     * Explore the intersection points between the two texts.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     * @return LinkedList of Diff objects or null if no diff available
     */
    public static List map(String text1, String text2)
    {
        long ms_end = System.currentTimeMillis() + (long) (TIMEOUT * 1000);
        int max_d = (text1.length() + text2.length()) / 2;
        List v_map1 = new ArrayList();
        List v_map2 = new ArrayList();
        Map v1 = new HashMap();
        Map v2 = new HashMap();
        v1.put(new Integer(1), new Integer(0));
        v2.put(new Integer(1), new Integer(0));
        int x;
        int y;
        String footstep; // Used to track overlapping paths.
        Map footsteps = new HashMap();
        boolean done = false;
        // If the total number of characters is odd, then the front path will
        // collide with the reverse path.
        boolean front = (text1.length() + text2.length()) % 2 == 1;
        for (int d = 0; d < max_d; d++)
        {
            // Bail out if timeout reached.
            if (TIMEOUT > 0 && System.currentTimeMillis() > ms_end)
            {
                return null;
            }

            // Walk the front path one step.
            v_map1.add(new HashSet()); // Adds at index 'd'.
            for (int k = -d; k <= d; k += 2)
            {
                Integer kPlus1Key = new Integer(k + 1);
                Integer kPlus1Value = (Integer) v1.get(kPlus1Key);
                Integer kMinus1Key = new Integer(k + 1);
                Integer kMinus1Value = (Integer) v1.get(kMinus1Key);
                if (k == -d || k != d && kMinus1Value.intValue() < kPlus1Value.intValue())
                {
                    x = kPlus1Value.intValue();
                }
                else
                {
                    x = kMinus1Value.intValue() + 1;
                }
                y = x - k;
                footstep = x + "," + y; //$NON-NLS-1$
                if (front && (footsteps.containsKey(footstep)))
                {
                    done = true;
                }
                if (!front)
                {
                    footsteps.put(footstep, new Integer(d));
                }
                while (!done && x < text1.length() && y < text2.length() && text1.charAt(x) == text2.charAt(y))
                {
                    x++;
                    y++;
                    footstep = x + "," + y; //$NON-NLS-1$
                    if (front && footsteps.containsKey(footstep))
                    {
                        done = true;
                    }
                    if (!front)
                    {
                        footsteps.put(footstep, new Integer(d));
                    }
                }
                v1.put(new Integer(k), new Integer(x));
                Set s = (Set) v_map1.get(d);
                s.add(x + "," + y); //$NON-NLS-1$
                if (done)
                {
                    // Front path ran over reverse path.
                    Integer footstepValue = (Integer) footsteps.get(footstep);
                    v_map2 = v_map2.subList(0, footstepValue.intValue() + 1);
                    List a = path1(v_map1, text1.substring(0, x), text2.substring(0, y));
                    a.addAll(path2(v_map2, text1.substring(x), text2.substring(y)));
                    return a;
                }
            }

            // Walk the reverse path one step.
            v_map2.add(new HashSet()); // Adds at index 'd'.
            for (int k = -d; k <= d; k += 2)
            {
                Integer kPlus1Key = new Integer(k + 1);
                Integer kPlus1Value = (Integer) v2.get(kPlus1Key);
                Integer kMinus1Key = new Integer(k + 1);
                Integer kMinus1Value = (Integer) v2.get(kMinus1Key);
                if (k == -d || k != d && kMinus1Value.intValue() < kPlus1Value.intValue())
                {
                    x = kPlus1Value.intValue();
                }
                else
                {
                    x = kMinus1Value.intValue() + 1;
                }
                y = x - k;
                footstep = (text1.length() - x) + "," + (text2.length() - y); //$NON-NLS-1$
                if (!front && (footsteps.containsKey(footstep)))
                {
                    done = true;
                }
                if (front)
                {
                    footsteps.put(footstep, new Integer(d));
                }
                while (!done && x < text1.length() && y < text2.length() && text1.charAt(text1.length() - x - 1) == text2.charAt(text2.length() - y - 1))
                {
                    x++;
                    y++;
                    footstep = (text1.length() - x) + "," + (text2.length() - y); //$NON-NLS-1$
                    if (!front && (footsteps.containsKey(footstep)))
                    {
                        done = true;
                    }
                    if (front)
                    {
                        footsteps.put(footstep, new Integer(d));
                    }
                }

                v2.put(new Integer(k), new Integer(x));
                Set s = (Set) v_map2.get(d);
                s.add(x + "," + y); //$NON-NLS-1$
                if (done)
                {
                    // Reverse path ran over front path.
                    Integer footstepValue = (Integer) footsteps.get(footstep);
                    v_map1 = v_map1.subList(0, footstepValue.intValue() + 1);
                    List a = path1(v_map1, text1.substring(0, text1.length() - x), text2.substring(0, text2.length() - y));
                    a.addAll(path2(v_map2, text1.substring(text1.length() - x), text2.substring(text2.length() - y)));
                    return a;
                }
            }
        }

        // Number of diffs equals number of characters, no commonality at all.
        return null;
    }

    /**
     * Work from the middle back to the start to determine the path.
     * @param v_map List of path sets.
     * @param text1 Old string fragment to be diffed
     * @param text2 New string fragment to be diffed
     * @return List of Diff objects
     */
    public static List path1(List v_map, String text1, String text2)
    {
        List path = new ArrayList();
        int x = text1.length();
        int y = text2.length();
        EditType last_op = null;
        for (int d = v_map.size() - 2; d >= 0; d--)
        {
            while (true)
            {
                Set set = (Set) v_map.get(d);
                if (set.contains((x - 1) + "," + y)) //$NON-NLS-1$
                {
                    x--;
                    if (last_op == EditType.DELETE)
                    {
                        Difference firstDiff = (Difference) path.get(0);
                        firstDiff.prependText(text1.charAt(x));
                    }
                    else
                    {
                        path.add(0, new Difference(EditType.DELETE, text1.substring(x, x + 1)));
                    }
                    last_op = EditType.DELETE;
                    break;
                }
                else if (set.contains(x + "," + (y - 1))) //$NON-NLS-1$
                {
                    y--;
                    if (last_op == EditType.INSERT)
                    {
                        Difference firstDiff = (Difference) path.get(0);
                        firstDiff.prependText(text2.charAt(y));
                    }
                    else
                    {
                        path.add(0, new Difference(EditType.INSERT, text2.substring(y, y + 1)));
                    }
                    last_op = EditType.INSERT;
                    break;
                }
                else
                {
                    x--;
                    y--;
                    assert text1.charAt(x) == text2.charAt(y) : "No diagonal.  Can't happen. (diff_path1)"; //$NON-NLS-1$
                    if (last_op == EditType.EQUAL)
                    {
                        Difference firstDiff = (Difference) path.get(0);
                        firstDiff.prependText(text1.charAt(x));
                    }
                    else
                    {
                        path.add(0, new Difference(EditType.EQUAL, text1.substring(x, x + 1)));
                    }
                    last_op = EditType.EQUAL;
                }
            }
        }
        return path;
    }

    /**
     * Work from the middle back to the end to determine the path.
     * @param v_map List of path sets.
     * @param text1 Old string fragment to be diffed
     * @param text2 New string fragment to be diffed
     * @return List of Diff objects
     */
    public static List path2(List v_map, String text1, String text2)
    {
        List path = new ArrayList();
        int x = text1.length();
        int y = text2.length();
        EditType last_op = null;
        for (int d = v_map.size() - 2; d >= 0; d--)
        {
            while (true)
            {
                Set set = (Set) v_map.get(d);
                if (set.contains((x - 1) + "," + y)) //$NON-NLS-1$
                {
                    x--;
                    if (last_op == EditType.DELETE)
                    {
                        Difference lastDiff = (Difference) path.get(path.size() - 1);
                        lastDiff.appendText(text1.charAt(text1.length() - x - 1));
                    }
                    else
                    {
                        path.add(new Difference(EditType.DELETE, text1.substring(text1.length() - x - 1, text1.length() - x)));
                    }
                    last_op = EditType.DELETE;
                    break;
                }
                else if (set.contains(x + "," + (y - 1))) //$NON-NLS-1$
                {
                    y--;
                    if (last_op == EditType.INSERT)
                    {
                        Difference lastDiff = (Difference) path.get(path.size() - 1);
                        lastDiff.appendText(text2.charAt(text2.length() - y - 1));
                    }
                    else
                    {
                        path.add(new Difference(EditType.INSERT, text2.substring(text2.length() - y - 1, text2.length() - y)));
                    }
                    last_op = EditType.INSERT;
                    break;
                }
                else
                {
                    x--;
                    y--;
                    assert text1.charAt(text1.length() - x - 1) == text2.charAt(text2.length() - y - 1) : "No diagonal.  Can't happen. (diff_path2)"; //$NON-NLS-1$

                    if (last_op == EditType.EQUAL)
                    {
                        Difference lastDiff = (Difference) path.get(path.size() - 1);
                        lastDiff.appendText(text1.charAt(text1.length() - x - 1));
                    }
                    else
                    {
                        path.add(new Difference(EditType.EQUAL, text1.substring(text1.length() - x - 1, text1.length() - x)));
                    }
                    last_op = EditType.EQUAL;
                }
            }
        }
        return path;
    }

    /**
     * Trim off common prefix
     * @param text1 First string
     * @param text2 Second string
     * @return The number of characters common to the start of each string.
     */
    public static int commonPrefix(String text1, String text2)
    {
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
            pointermid = (pointermax - pointermin) / 2 + pointermin;
        }
        return pointermid;
    }

    /**
     * Trim off common suffix
     * @param text1 First string
     * @param text2 Second string
     * @return The number of characters common to the end of each string.
     */
    public static int commonSuffix(String text1, String text2)
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
            pointermid = (pointermax - pointermin) / 2 + pointermin;
        }
        return pointermid;
    }

    /**
     * Do the two texts share a substring which is at least half the length of the
     * longer text?
     * @param text1 First string
     * @param text2 Second string
     * @return Five element String array, containing the prefix of text1, the
     *     suffix of text1, the prefix of text2, the suffix of text2 and the
     *     common middle.  Or null if there was no match.
     */
    public static CommonMiddle halfMatch(String text1, String text2)
    {
        String longtext = text1.length() > text2.length() ? text1 : text2;
        String shorttext = text1.length() > text2.length() ? text2 : text1;
        int longtextLength = longtext.length();
        if (longtextLength < 10 || shorttext.length() < 1)
        {
            return null; // Pointless.
        }

        // First check if the second quarter is the seed for a half-match.
        CommonMiddle hm1 = halfMatchI(longtext, shorttext, (int) Math.ceil(longtextLength / 4));
        // Check again based on the third quarter.
        CommonMiddle hm2 = halfMatchI(longtext, shorttext, (int) Math.ceil(longtextLength / 2));
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
        else
        // Both matched.  Select the longest.
        {
            hm = hm1.getCommonality().length() > hm2.getCommonality().length() ? hm1 : hm2;
        }

        // A half-match was found, sort out the return data.
        if (text1.length() > text2.length())
        {
            return hm;
        }
        return new CommonMiddle(hm.getRightEnd(), hm.getRightEnd(), hm.getCommonality(), hm.getLeftStart(), hm.getRightStart());
    }

    /**
     * Does a substring of shorttext exist within longtext such that the substring
     * is at least half the length of longtext?
     * @param longtext Longer string
     * @param shorttext Shorter string
     * @param i Start index of quarter length substring within longtext
     * @return Five element String array, containing the prefix of longtext, the
     *     suffix of longtext, the prefix of shorttext, the suffix of shorttext
     *     and the common middle.  Or null if there was no match.
     */
    private static CommonMiddle halfMatchI(String longtext, String shorttext, int i)
    {
        // Start with a 1/4 length substring at position i as a seed.
        String seed = longtext.substring(i, i + (longtext.length() / 4));
        int j = -1;
        String best_common = ""; //$NON-NLS-1$
        String best_longtext_a = ""; //$NON-NLS-1$
        String best_longtext_b = ""; //$NON-NLS-1$
        String best_shorttext_a = ""; //$NON-NLS-1$
        String best_shorttext_b = ""; //$NON-NLS-1$
        while ((j = shorttext.indexOf(seed, j + 1)) != -1)
        {
            int prefixLength = Diff.commonPrefix(longtext.substring(i), shorttext.substring(j));
            int suffixLength = Diff.commonSuffix(longtext.substring(0, i), shorttext.substring(0, j));
            if (best_common.length() < (prefixLength + suffixLength))
            {
                best_common = shorttext.substring(j - suffixLength, j) + shorttext.substring(j, j + prefixLength);
                best_longtext_a = longtext.substring(0, i - suffixLength);
                best_longtext_b = longtext.substring(i + prefixLength);
                best_shorttext_a = shorttext.substring(0, j - suffixLength);
                best_shorttext_b = shorttext.substring(j + prefixLength);
            }
        }

        if (best_common.length() >= longtext.length() / 2)
        {
            return new CommonMiddle(best_longtext_a, best_longtext_b, best_common, best_shorttext_a, best_shorttext_b);
        }

        return null;
    }

    /**
     * Reduce the number of edits by eliminating semantically trivial equalities.
     * @param diffs LinkedList of Diff objects
     */
    public static void cleanupSemantic(List diffs)
    {
        boolean changes = false;
        Stack equalities = new Stack(); // Stack of indices where equalities are found.
        String lastequality = ""; //$NON-NLS-1$ // Always equal to equalities.lastElement().getText()
        int length_changes1 = 0; // Number of characters that changed prior to the equality.
        int length_changes2 = 0; // Number of characters that changed after the equality.
        ListIterator pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        while (curDiff != null)
        {
            EditType editType = curDiff.getEditType();
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
                int lastLen = lastequality != null ? lastequality.length() : 0;
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
                    equalities.pop(); // Throw away the equality we just deleted;
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
            Diff.cleanupMerge(diffs);
        }
    }

    /**
     * Reduce the number of edits by eliminating operationally trivial equalities.
     * @param diffs LinkedList of Diff objects
     */
    public static void cleanupEfficiency(List diffs)
    {
        if (diffs.isEmpty())
        {
            return;
        }

        boolean changes = false;
        Stack equalities = new Stack(); // Stack of indices where equalities are found.
        String lastequality = null; // Always equal to equalities.lastElement().getText();
        int pre_ins = 0; // Is there an insertion operation before the last equality.
        int pre_del = 0; // Is there an deletion operation before the last equality.
        int post_ins = 0; // Is there an insertion operation after the last equality.
        int post_del = 0; // Is there an deletion operation after the last equality.

        ListIterator pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        Difference safeDiff = curDiff; // The last Diff that is known to be unsplitable.

        while (curDiff != null)
        {
            EditType diff_type = curDiff.getEditType();
            if (EditType.EQUAL.equals(diff_type)) // equality found
            {
                if (curDiff.getText().length() < EDIT_COST && (post_ins + post_del) > 0)
                {
                    // Candidate found.
                    equalities.push(curDiff);
                    pre_ins = post_ins;
                    pre_del = post_del;
                    lastequality = curDiff.getText();
                }
                else
                {
                    // Not a candidate, and can never become one.
                    equalities.clear();
                    lastequality = ""; //$NON-NLS-1$
                    safeDiff = curDiff;
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
                if (lastequality != null
                    && (((pre_ins + pre_del + post_ins + post_del) > 0) || ((lastequality.length() < EDIT_COST / 2) && (pre_ins + pre_del + post_ins + post_del) == 3)))
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
                    curDiff = new Difference(EditType.INSERT, lastequality);
                    pointer.add(curDiff);

                    equalities.pop(); // Throw away the equality we just deleted;
                    lastequality = null;
                    if (pre_ins == 1 && pre_del == 1)
                    {
                        // No changes made which could affect previous entry, keep going.
                        post_ins = 1;
                        post_del = 1;
                        equalities.clear();
                        safeDiff = curDiff;
                    }
                    else
                    {
                        if (!equalities.empty())
                        {
                            // Throw away the previous equality;
                            equalities.pop();
                        }
                        if (equalities.empty())
                        {
                            // There are no previous questionable equalities,
                            // walk back to the last known safe diff.
                            curDiff = safeDiff;
                        }
                        else
                        {
                            // There is an equality we can fall back to.
                            curDiff = (Difference) equalities.lastElement();
                        }
                        while (curDiff != pointer.previous())
                        {
                            // Intentionally empty loop.
                        }

                        post_ins = 0;
                        post_del = 0;
                    }
                    changes = true;
                }
            }
            curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        }

        if (changes)
        {
            Diff.cleanupMerge(diffs);
        }
    }

    /**
     * Reorder and merge like edit sections.  Merge equalities.
     * Any edit section can move as long as it doesn't cross an equality.
     * @param diffs LinkedList of Diff objects
     */
    public static void cleanupMerge(List diffs)
    {
        // Add a dummy entry at the end.
        diffs.add(new Difference(EditType.EQUAL, "")); //$NON-NLS-1$

        int count_delete = 0;
        int count_insert = 0;
        String text_delete = ""; //$NON-NLS-1$
        String text_insert = ""; //$NON-NLS-1$

        int commonLength = 0;

        ListIterator pointer = diffs.listIterator();
        Difference curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        Difference prevEqual = null;
        while (curDiff != null)
        {
            EditType diff_type = curDiff.getEditType();
            if (EditType.INSERT.equals(diff_type))
            {
                count_insert++;
                text_insert += curDiff.getText();
                prevEqual = null;
            }
            else if (EditType.DELETE.equals(diff_type))
            {
                count_delete++;
                text_delete += curDiff.getText();
                prevEqual = null;
            }
            else if (EditType.EQUAL.equals(diff_type))
            {
                // Upon reaching an equality, check for prior redundancies.
                if (count_delete != 0 || count_insert != 0)
                {
                    // Delete the offending records.
                    pointer.previous(); // Reverse direction.
                    while (count_delete-- > 0)
                    {
                        pointer.previous();
                        pointer.remove();
                    }
                    while (count_insert-- > 0)
                    {
                        pointer.previous();
                        pointer.remove();
                    }

                    if (count_delete != 0 && count_insert != 0)
                    {
                        // Factor out any common prefixies.
                        commonLength = Diff.commonPrefix(text_insert, text_delete);
                        if (commonLength > 0)
                        {
                            if (pointer.hasPrevious())
                            {
                                curDiff = (Difference) pointer.previous();
                                assert curDiff.getEditType() == EditType.EQUAL : "Previous diff should have been an equality."; //$NON-NLS-1$
                                curDiff.appendText(text_insert.substring(0, commonLength));
                                pointer.next();
                            }
                            else
                            {
                                pointer.add(new Difference(EditType.EQUAL, text_insert.substring(0, commonLength)));
                            }
                            text_insert = text_insert.substring(commonLength);
                            text_delete = text_delete.substring(commonLength);
                        }

                        // Factor out any common suffixies.
                        commonLength = Diff.commonSuffix(text_insert, text_delete);
                        if (commonLength > 0)
                        {
                            curDiff = (Difference) pointer.next();
                            curDiff.prependText(text_insert.substring(text_insert.length() - commonLength));
                            text_insert = text_insert.substring(0, text_insert.length() - commonLength);
                            text_delete = text_delete.substring(0, text_delete.length() - commonLength);
                            pointer.previous();
                        }
                    }

                    // Insert the merged records.
                    if (text_delete.length() != 0)
                    {
                        pointer.add(new Difference(EditType.DELETE, text_delete));
                    }

                    if (text_insert.length() != 0)
                    {
                        pointer.add(new Difference(EditType.INSERT, text_insert));
                    }

                    // Step forward to the equality.
                    curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
                }
                else if (prevEqual != null)
                {
                    // Merge this equality with the previous one.
                    prevEqual.appendText(curDiff.getText());
                    pointer.remove();
                    curDiff = (Difference) pointer.previous();
                    pointer.next(); // Forward direction
                }

                count_insert = 0;
                count_delete = 0;
                text_delete = ""; //$NON-NLS-1$
                text_insert = ""; //$NON-NLS-1$
                prevEqual = curDiff;
            }
            curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
        }

        Difference lastDiff = (Difference) diffs.get(diffs.size() - 1);
        if (lastDiff.getText().length() == 0)
        {
            diffs.remove(diffs.size() - 1); // Remove the dummy entry at the end.
        }
    }

    /**
     * Add an index to each Diff, represents where the Diff is located in text2.
     * e.g. [(DELETE, "h", 0), (INSERT, "c", 0), (EQUAL, "at", 1)]
     * @param diffs LinkedList of Diff objects
     */
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

    /**
     * loc is a location in text1, compute and return the equivalent location in
     * text2.
     * e.g. "The cat" vs "The big cat", 1->1, 5->8
     * @param diffs LinkedList of Diff objects
     * @param loc Location within text1
     * @return Location within text2
     */
    public static int xIndex(List diffs, int loc)
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

    /**
     * Convert a Diff list into a pretty HTML report.
     * @param diffs LinkedList of Diff objects
     * @return HTML representation
     */
    public static String prettyHtml(List diffs)
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
    public static final double TIMEOUT   = 1.0;

    /**
     * Cost of an empty edit operation in terms of edit characters.
     */
    public static final int    EDIT_COST = 4;

}
