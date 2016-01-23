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
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.common.diff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builds a map of a baseline/source text and a changed/target text, navigating
 * it to determine differences.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class DifferenceEngine {

    /**
     * Empty Difference Engine, which won't find anything.
     */
    public DifferenceEngine() {
        this("", "");
    }

    /**
     * Find the differences between two texts. Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     * 
     * @param source
     *            Old string to be diffed
     * @param target
     *            New string to be diffed
     */
    public DifferenceEngine(final String source, final String target) {
        this.source = source;
        this.target = target;
        this.sourceLength = source.length();
        this.targetLength = target.length();
    }

    /**
     * Explore the intersection points between the two texts.
     * 
     * @return List of Difference objects or null if no diff available
     */
    public List<Difference> generate() {
        long msEnd = System.currentTimeMillis() + (long) (timeout * 1000);
        int maxD = (this.sourceLength + this.targetLength) / 2;
        List<Set<String>> vMap1 = new ArrayList<Set<String>>();
        List<Set<String>> vMap2 = new ArrayList<Set<String>>();
        Map<Integer, Integer> v1 = new HashMap<Integer, Integer>();
        Map<Integer, Integer> v2 = new HashMap<Integer, Integer>();
        v1.put(Integer.valueOf(1), Integer.valueOf(0));
        v2.put(Integer.valueOf(1), Integer.valueOf(0));
        int x;
        int y;
        String footstep; // Used to track overlapping paths.
        Map<String, Integer> footsteps = new HashMap<String, Integer>();
        boolean done = false;
        // If the total number of characters is odd, then the front path will
        // collide with the reverse path.
        boolean front = (this.sourceLength + this.targetLength) % 2 != 0;
        for (int d = 0; d < maxD; d++) {
            // Bail out if timeout reached.
            if (timeout > 0 && System.currentTimeMillis() > msEnd) {
                return null;
            }

            // Walk the front path one step.
            vMap1.add(new HashSet<String>()); // Adds at index 'd'.
            for (int k = -d; k <= d; k += 2) {
                Integer kPlus1Key = Integer.valueOf(k + 1);
                Integer kPlus1Value = v1.get(kPlus1Key);
                Integer kMinus1Key = Integer.valueOf(k - 1);
                Integer kMinus1Value = v1.get(kMinus1Key);
                if (k == -d || k != d && kMinus1Value.intValue() < kPlus1Value.intValue()) {
                    x = kPlus1Value.intValue();
                } else {
                    x = kMinus1Value.intValue() + 1;
                }
                y = x - k;
                footstep = x + "," + y;
                if (front && (footsteps.containsKey(footstep))) {
                    done = true;
                }
                if (!front) {
                    footsteps.put(footstep, Integer.valueOf(d));
                }
                while (!done && x < this.sourceLength && y < this.targetLength && source.charAt(x) == target.charAt(y)) {
                    x++;
                    y++;
                    footstep = x + "," + y;
                    if (front && footsteps.containsKey(footstep)) {
                        done = true;
                    }
                    if (!front) {
                        footsteps.put(footstep, Integer.valueOf(d));
                    }
                }
                v1.put(Integer.valueOf(k), Integer.valueOf(x));
                Set<String> s = vMap1.get(d);
                s.add(x + "," + y);
                if (done) {
                    // Front path ran over reverse path.
                    Integer footstepValue = footsteps.get(footstep);
                    vMap2 = vMap2.subList(0, footstepValue.intValue() + 1);
                    List<Difference> a = path1(vMap1, source.substring(0, x), target.substring(0, y));
                    a.addAll(path2(vMap2, source.substring(x), target.substring(y)));
                    return a;
                }
            }

            // Walk the reverse path one step.
            vMap2.add(new HashSet<String>()); // Adds at index 'd'.
            for (int k = -d; k <= d; k += 2) {
                Integer kPlus1Key = Integer.valueOf(k + 1);
                Integer kPlus1Value = v2.get(kPlus1Key);
                Integer kMinus1Key = Integer.valueOf(k - 1);
                Integer kMinus1Value = v2.get(kMinus1Key);
                if (k == -d || k != d && kMinus1Value.intValue() < kPlus1Value.intValue()) {
                    x = kPlus1Value.intValue();
                } else {
                    x = kMinus1Value.intValue() + 1;
                }
                y = x - k;
                footstep = (this.sourceLength - x) + "," + (this.targetLength - y);
                if (!front && (footsteps.containsKey(footstep))) {
                    done = true;
                }
                if (front) {
                    footsteps.put(footstep, Integer.valueOf(d));
                }
                while (!done && x < this.sourceLength && y < this.targetLength && source.charAt(this.sourceLength - x - 1) == target.charAt(this.targetLength - y - 1)) {
                    x++;
                    y++;
                    footstep = (this.sourceLength - x) + "," + (this.targetLength - y);
                    if (!front && (footsteps.containsKey(footstep))) {
                        done = true;
                    }
                    if (front) {
                        footsteps.put(footstep, Integer.valueOf(d));
                    }
                }

                v2.put(Integer.valueOf(k), Integer.valueOf(x));
                Set<String> s = vMap2.get(d);
                s.add(x + "," + y);
                if (done) {
                    // Reverse path ran over front path.
                    Integer footstepValue = footsteps.get(footstep);
                    vMap1 = vMap1.subList(0, footstepValue.intValue() + 1);
                    List<Difference> a = path1(vMap1, source.substring(0, this.sourceLength - x), target.substring(0, this.targetLength - y));
                    a.addAll(path2(vMap2, source.substring(this.sourceLength - x), target.substring(this.targetLength - y)));
                    return a;
                }
            }
        }

        // Number of diffs equals number of characters, no commonality at all.
        return null;
    }

    /**
     * Work from the middle back to the start to determine the path.
     * 
     * @param vMap
     *            List of path sets.
     * @param newSource
     *            Old string fragment to be diffed
     * @param newTarget
     *            New string fragment to be diffed
     * @return List of Difference objects
     */
    protected List<Difference> path1(final List<Set<String>> vMap, final String newSource, final String newTarget) {
        List<Difference> path = new ArrayList<Difference>();
        int x = newSource.length();
        int y = newTarget.length();
        EditType lastEditType = null;
        for (int d = vMap.size() - 2; d >= 0; d--) {
            while (true) {
                Set<String> set = vMap.get(d);
                if (set.contains((x - 1) + "," + y)) {
                    x--;
                    if (EditType.DELETE.equals(lastEditType)) {
                        Difference firstDiff = path.get(0);
                        firstDiff.prependText(newSource.charAt(x));
                    } else {
                        path.add(0, new Difference(EditType.DELETE, newSource.substring(x, x + 1)));
                    }
                    lastEditType = EditType.DELETE;
                    break;
                } else if (set.contains(x + "," + (y - 1))) {
                    y--;
                    if (EditType.INSERT.equals(lastEditType)) {
                        Difference firstDiff = path.get(0);
                        firstDiff.prependText(newTarget.charAt(y));
                    } else {
                        path.add(0, new Difference(EditType.INSERT, newTarget.substring(y, y + 1)));
                    }
                    lastEditType = EditType.INSERT;
                    break;
                } else {
                    x--;
                    y--;
                    assert newSource.charAt(x) == newTarget.charAt(y) : "No diagonal.  Can't happen. (path1)";
                    if (EditType.EQUAL.equals(lastEditType)) {
                        Difference firstDiff = path.get(0);
                        firstDiff.prependText(newSource.charAt(x));
                    } else {
                        path.add(0, new Difference(EditType.EQUAL, newSource.substring(x, x + 1)));
                    }
                    lastEditType = EditType.EQUAL;
                }
            }
        }
        return path;
    }

    /**
     * Work from the middle back to the end to determine the path.
     * 
     * @param vMap
     *            List of path sets.
     * @param newSource
     *            Old string fragment to be diffed
     * @param newTarget
     *            New string fragment to be diffed
     * @return List of Difference objects
     */
    protected List<Difference> path2(final List<Set<String>> vMap, final String newSource, final String newTarget) {
        List<Difference> path = new ArrayList<Difference>();

        //cached versions of length from immutable strings
        final int cachedNewSourceLength = newSource.length();
        final int cachedNewTargetLength = newTarget.length();

        int x = cachedNewSourceLength;
        int y = cachedNewTargetLength;
        EditType lastEditType = null;
        for (int d = vMap.size() - 2; d >= 0; d--) {
            while (true) {
                Set<String> set = vMap.get(d);
                if (set.contains((x - 1) + "," + y)) {
                    x--;
                    if (EditType.DELETE.equals(lastEditType)) {
                        Difference lastDiff = path.get(path.size() - 1);
                        lastDiff.appendText(newSource.charAt(cachedNewSourceLength - x - 1));
                    } else {
                        path.add(new Difference(EditType.DELETE, newSource.substring(cachedNewSourceLength - x - 1, cachedNewSourceLength - x)));
                    }
                    lastEditType = EditType.DELETE;
                    break;
                } else if (set.contains(x + "," + (y - 1))) {
                    y--;
                    if (EditType.INSERT.equals(lastEditType)) {
                        Difference lastDiff = path.get(path.size() - 1);
                        lastDiff.appendText(newTarget.charAt(cachedNewTargetLength - y - 1));
                    } else {
                        path.add(new Difference(EditType.INSERT, newTarget.substring(cachedNewTargetLength - y - 1, cachedNewTargetLength - y)));
                    }
                    lastEditType = EditType.INSERT;
                    break;
                } else {
                    x--;
                    y--;
                    assert newSource.charAt(cachedNewSourceLength - x - 1) == newTarget.charAt(cachedNewTargetLength - y - 1) : "No diagonal.  Can't happen. (path2)";

                    if (EditType.EQUAL.equals(lastEditType)) {
                        Difference lastDiff = path.get(path.size() - 1);
                        lastDiff.appendText(newSource.charAt(cachedNewSourceLength - x - 1));
                    } else {
                        path.add(new Difference(EditType.EQUAL, newSource.substring(cachedNewSourceLength - x - 1, cachedNewSourceLength - x)));
                    }
                    lastEditType = EditType.EQUAL;
                }
            }
        }
        return path;
    }

    /**
     * Set the timeout for the diff operation. The default is 1 second. Use 0
     * for infinity.
     * 
     * @param newTimeout the new timeout
     */
    public static void setTimeout(float newTimeout) {
        timeout = newTimeout;
    }

    /**
     * Number of seconds to map a diff before giving up. Use 0 for infinity.
     */
    private static final float TIMEOUT = 1.0f;
    private static float timeout = TIMEOUT;
    /**
     * Made final because we now rely on caching the string lengths (they could be cached
     * at method level if require non-final in future)
     * The baseline text.
     */
    private final String source;

    /**
     * The changed text.
     * 
     */
    private final String target;

    // cached versions of source and target length, 
    // as for roughly 1 call to generate(), was causing 117'000 calls to length()
    private final int targetLength;
    private final int sourceLength;

}
