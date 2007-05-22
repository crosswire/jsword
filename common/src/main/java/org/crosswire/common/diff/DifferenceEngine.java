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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds a map of a baseline/source text and a changed/target text, navigating it to determine differences.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DifferenceEngine
{
    /**
     * Find the differences between two texts.  Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     * @param checkLines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff
     */
    public DifferenceEngine(final String text1, final String text2)
    {
        this.text1 = text1;
        this.text2 = text2;
    }

    /**
     * Explore the intersection points between the two texts.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     * @return List of Difference objects or null if no diff available
     */
    public List generate()
    {
        long msEnd = System.currentTimeMillis() + (long) (TIMEOUT * 1000);
        int maxD = (text1.length() + text2.length()) / 2;
        List vMap1 = new ArrayList();
        List vMap2 = new ArrayList();
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
        for (int d = 0; d < maxD; d++)
        {
            // Bail out if timeout reached.
            if (TIMEOUT > 0 && System.currentTimeMillis() > msEnd)
            {
                return null;
            }

            // Walk the front path one step.
            vMap1.add(new HashSet()); // Adds at index 'd'.
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
                Set s = (Set) vMap1.get(d);
                s.add(x + "," + y); //$NON-NLS-1$
                if (done)
                {
                    // Front path ran over reverse path.
                    Integer footstepValue = (Integer) footsteps.get(footstep);
                    vMap2 = vMap2.subList(0, footstepValue.intValue() + 1);
                    List a = path1(vMap1, text1.substring(0, x), text2.substring(0, y));
                    a.addAll(path2(vMap2, text1.substring(x), text2.substring(y)));
                    return a;
                }
            }

            // Walk the reverse path one step.
            vMap2.add(new HashSet()); // Adds at index 'd'.
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
                Set s = (Set) vMap2.get(d);
                s.add(x + "," + y); //$NON-NLS-1$
                if (done)
                {
                    // Reverse path ran over front path.
                    Integer footstepValue = (Integer) footsteps.get(footstep);
                    vMap1 = vMap1.subList(0, footstepValue.intValue() + 1);
                    List a = path1(vMap1, text1.substring(0, text1.length() - x), text2.substring(0, text2.length() - y));
                    a.addAll(path2(vMap2, text1.substring(text1.length() - x), text2.substring(text2.length() - y)));
                    return a;
                }
            }
        }

        // Number of diffs equals number of characters, no commonality at all.
        return null;
    }

    /**
     * Work from the middle back to the start to determine the path.
     * @param vMap List of path sets.
     * @param left Old string fragment to be diffed
     * @param right New string fragment to be diffed
     * @return List of Difference objects
     */
    private List path1(final List vMap, final String left, final String right)
    {
        List path = new ArrayList();
        int x = left.length();
        int y = right.length();
        EditType lastEditType = null;
        for (int d = vMap.size() - 2; d >= 0; d--)
        {
            while (true)
            {
                Set set = (Set) vMap.get(d);
                if (set.contains((x - 1) + "," + y)) //$NON-NLS-1$
                {
                    x--;
                    if (EditType.DELETE.equals(lastEditType))
                    {
                        Difference firstDiff = (Difference) path.get(0);
                        firstDiff.prependText(left.charAt(x));
                    }
                    else
                    {
                        path.add(0, new Difference(EditType.DELETE, left.substring(x, x + 1)));
                    }
                    lastEditType = EditType.DELETE;
                    break;
                }
                else if (set.contains(x + "," + (y - 1))) //$NON-NLS-1$
                {
                    y--;
                    if (EditType.INSERT.equals(lastEditType))
                    {
                        Difference firstDiff = (Difference) path.get(0);
                        firstDiff.prependText(right.charAt(y));
                    }
                    else
                    {
                        path.add(0, new Difference(EditType.INSERT, right.substring(y, y + 1)));
                    }
                    lastEditType = EditType.INSERT;
                    break;
                }
                else
                {
                    x--;
                    y--;
                    assert left.charAt(x) == right.charAt(y) : "No diagonal.  Can't happen. (path1)"; //$NON-NLS-1$
                    if (EditType.EQUAL.equals(lastEditType))
                    {
                        Difference firstDiff = (Difference) path.get(0);
                        firstDiff.prependText(left.charAt(x));
                    }
                    else
                    {
                        path.add(0, new Difference(EditType.EQUAL, left.substring(x, x + 1)));
                    }
                    lastEditType = EditType.EQUAL;
                }
            }
        }
        return path;
    }

    /**
     * Work from the middle back to the end to determine the path.
     * @param vMap List of path sets.
     * @param left Old string fragment to be diffed
     * @param right New string fragment to be diffed
     * @return List of Difference objects
     */
    private List path2(final List vMap, final String left, final String right)
    {
        List path = new ArrayList();
        int x = left.length();
        int y = right.length();
        EditType lastEditType = null;
        for (int d = vMap.size() - 2; d >= 0; d--)
        {
            while (true)
            {
                Set set = (Set) vMap.get(d);
                if (set.contains((x - 1) + "," + y)) //$NON-NLS-1$
                {
                    x--;
                    if (EditType.DELETE.equals(lastEditType))
                    {
                        Difference lastDiff = (Difference) path.get(path.size() - 1);
                        lastDiff.appendText(left.charAt(left.length() - x - 1));
                    }
                    else
                    {
                        path.add(new Difference(EditType.DELETE, left.substring(left.length() - x - 1, left.length() - x)));
                    }
                    lastEditType = EditType.DELETE;
                    break;
                }
                else if (set.contains(x + "," + (y - 1))) //$NON-NLS-1$
                {
                    y--;
                    if (EditType.INSERT.equals(lastEditType))
                    {
                        Difference lastDiff = (Difference) path.get(path.size() - 1);
                        lastDiff.appendText(right.charAt(right.length() - y - 1));
                    }
                    else
                    {
                        path.add(new Difference(EditType.INSERT, right.substring(right.length() - y - 1, right.length() - y)));
                    }
                    lastEditType = EditType.INSERT;
                    break;
                }
                else
                {
                    x--;
                    y--;
                    assert left.charAt(left.length() - x - 1) == right.charAt(right.length() - y - 1) : "No diagonal.  Can't happen. (path2)"; //$NON-NLS-1$

                    if (EditType.EQUAL.equals(lastEditType))
                    {
                        Difference lastDiff = (Difference) path.get(path.size() - 1);
                        lastDiff.appendText(left.charAt(left.length() - x - 1));
                    }
                    else
                    {
                        path.add(new Difference(EditType.EQUAL, left.substring(left.length() - x - 1, left.length() - x)));
                    }
                    lastEditType = EditType.EQUAL;
                }
            }
        }
        return path;
    }

    /**
     * Number of seconds to map a diff before giving up.  (0 for infinity)
     */
    private static final double TIMEOUT   = 1.0;

    /**
     * The baseline text.
     */
    private String text1;

    /**
     * The changed text.
     */
    private String text2;
}
