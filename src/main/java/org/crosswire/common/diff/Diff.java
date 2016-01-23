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
import java.util.List;
import java.util.ListIterator;

/**
 * Computes the difference between two texts to create a list of differences.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class Diff {
    /**
     * Construct an object that can find the differences between two texts. Run
     * a faster slightly less optimal diff. This constructor allows the
     * 'checkLines' to be optional. Most of the time checkLines is wanted, so
     * default to true.
     * 
     * @param source
     *            Old string to be diffed
     * @param target
     *            New string to be diffed
     */
    public Diff(final String source, final String target) {
        this(source, target, true);
    }

    /**
     * Construct an object that can find the differences between two texts.
     * 
     * @param source
     *            Old string to be diffed
     * @param target
     *            New string to be diffed
     * @param checkLines
     *            Speedup flag. If false, then don't run a line-level diff first
     *            to identify the changed areas. If true, then run a faster
     *            slightly less optimal diff
     */
    public Diff(final String source, final String target, final boolean checkLines) {
        this.source = source;
        this.target = target;
        this.checkLines = checkLines;
    }

    /**
     * Find the differences between two texts. Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     * 
     * @return List of Difference objects
     */
    public List<Difference> compare() {
        // Check for equality (speedup)
        List<Difference> diffs;
        if (source.equals(target)) {
            diffs = new ArrayList<Difference>();
            diffs.add(new Difference(EditType.EQUAL, source));
            return diffs;
        }

        // Trim off common prefix (speedup)
        int commonLength = Commonality.prefix(source, target);
        String commonPrefix = source.substring(0, commonLength);
        source = source.substring(commonLength);
        target = target.substring(commonLength);

        // Trim off common suffix (speedup)
        commonLength = Commonality.suffix(source, target);
        String commonSuffix = source.substring(source.length() - commonLength);
        source = source.substring(0, source.length() - commonLength);
        target = target.substring(0, target.length() - commonLength);

        // Compute the diff on the middle block
        diffs = compute();

        // Restore the prefix and suffix
        if (!"".equals(commonPrefix)) {
            diffs.add(0, new Difference(EditType.EQUAL, commonPrefix));
        }

        if (!"".equals(commonSuffix)) {
            diffs.add(new Difference(EditType.EQUAL, commonSuffix));
        }

        DiffCleanup.cleanupMerge(diffs);

        return diffs;
    }

    /**
     * Find the differences between two texts.
     * 
     * @return List of Difference objects
     */
    private List<Difference> compute() {
        List<Difference> diffs = new ArrayList<Difference>();

        if ("".equals(source)) {
            // Just add some text (speedup)
            diffs.add(new Difference(EditType.INSERT, target));
            return diffs;
        }

        if ("".equals(target)) {
            // Just delete some text (speedup)
            diffs.add(new Difference(EditType.DELETE, source));
            return diffs;
        }

        String longText = source.length() > target.length() ? source : target;
        String shortText = source.length() > target.length() ? target : source;
        int i = longText.indexOf(shortText);
        if (i != -1) {
            // Shorter text is inside the longer text (speedup)
            EditType editType = (source.length() > target.length()) ? EditType.DELETE : EditType.INSERT;
            diffs.add(new Difference(editType, longText.substring(0, i)));
            diffs.add(new Difference(EditType.EQUAL, shortText));
            diffs.add(new Difference(editType, longText.substring(i + shortText.length())));
            return diffs;
        }

        // Check to see if the problem can be split in two.
        CommonMiddle middleMatch = Commonality.halfMatch(source, target);
        if (middleMatch != null) {
            // A half-match was found, sort out the return data.
            // Send both pairs off for separate processing.
            Diff startDiff = new Diff(middleMatch.getSourcePrefix(), middleMatch.getTargetPrefix(), checkLines);
            Diff endDiff = new Diff(middleMatch.getSourceSuffix(), middleMatch.getTargetSuffix(), checkLines);
            // Merge the results.
            diffs = startDiff.compare();
            diffs.add(new Difference(EditType.EQUAL, middleMatch.getCommonality()));
            diffs.addAll(endDiff.compare());
            return diffs;
        }

        // Perform a real diff.
        if (checkLines && source.length() + target.length() < 250) {
            checkLines = false; // Too trivial for the overhead.
        }

        LineMap lineMap = null;
        if (checkLines) {
            // Scan the text on a line-by-line basis first.
            lineMap = new LineMap(source, target);
            source = lineMap.getSourceMap();
            target = lineMap.getTargetMap();
        }

        diffs = new DifferenceEngine(source, target).generate();

        if (diffs == null) {
            // No acceptable result.
            diffs = new ArrayList<Difference>();
            diffs.add(new Difference(EditType.DELETE, source));
            diffs.add(new Difference(EditType.INSERT, target));
        }

        if (checkLines && lineMap != null) {
            // Convert the diff back to original text.
            lineMap.restore(diffs);
            // Eliminate freak matches (e.g. blank lines)
            DiffCleanup.cleanupSemantic(diffs);

            // Rediff any replacement blocks, this time character-by-character.
            // Add a dummy entry at the end.
            diffs.add(new Difference(EditType.EQUAL, ""));
            int countDeletes = 0;
            int countInserts = 0;
            StringBuilder textDelete = new StringBuilder();
            StringBuilder textInsert = new StringBuilder();
            ListIterator<Difference> pointer = diffs.listIterator();
            Difference curDiff = pointer.next();
            while (curDiff != null) {
                EditType editType = curDiff.getEditType();
                if (EditType.INSERT.equals(editType)) {
                    countInserts++;
                    textInsert.append(curDiff.getText());
                } else if (EditType.DELETE.equals(editType)) {
                    countDeletes++;
                    textDelete.append(curDiff.getText());
                } else {
                    // Upon reaching an equality, check for prior redundancies.
                    if (countDeletes >= 1 && countInserts >= 1) {
                        // Delete the offending records and add the merged ones.
                        pointer.previous();
                        for (int j = 0; j < countDeletes + countInserts; j++) {
                            pointer.previous();
                            pointer.remove();
                        }
                        Diff newDiff = new Diff(textDelete.toString(), textInsert.toString(), false);
                        for (Difference diff : newDiff.compare()) {
                            pointer.add(diff);
                        }
                    }
                    countInserts = 0;
                    countDeletes = 0;
                    textDelete.delete(0, textDelete.length());
                    textInsert.delete(0, textInsert.length());
                }
                curDiff = pointer.hasNext() ? pointer.next() : null;
            }
            diffs.remove(diffs.size() - 1); // Remove the dummy entry at the
            // end.
        }
        return diffs;
    }

    /**
     * loc is a location in source, compute and return the equivalent location
     * in target. e.g. "The cat" vs "The big cat", 1-&gt;1, 5-&gt;8
     * 
     * @param diffs
     *            List of Difference objects
     * @param loc
     *            Location within source
     * @return Location within target
     */
    public int xIndex(final List<Difference> diffs, final int loc) {
        int chars1 = 0;
        int chars2 = 0;
        int lastChars1 = 0;
        int lastChars2 = 0;
        Difference lastDiff = null;
        for (Difference diff : diffs) {
            EditType editType = diff.getEditType();

            // Equality or deletion?
            if (!EditType.INSERT.equals(editType)) {
                chars1 += diff.getText().length();
            }

            // Equality or insertion?
            if (!EditType.DELETE.equals(editType)) {
                chars2 += diff.getText().length();
            }

            // Overshot the location?
            if (chars1 > loc) {
                lastDiff = diff;
                break;
            }
            lastChars1 = chars1;
            lastChars2 = chars2;
        }

        // Was the location was deleted?
        if (lastDiff != null && EditType.DELETE.equals(lastDiff.getEditType())) {
            return lastChars2;
        }

        // Add the remaining character length.
        return lastChars2 + (loc - lastChars1);
    }

    /**
     * Convert a Difference list into a pretty HTML report.
     * 
     * @param diffs
     *            List of Difference objects
     * @return HTML representation
     */
    public String prettyHtml(List<Difference> diffs) {
        StringBuilder buf = new StringBuilder();
        for (int x = 0; x < diffs.size(); x++) {
            Difference diff = diffs.get(x);
            EditType editType = diff.getEditType(); // Mode (delete, equal,
            // insert)
            String text = diff.getText(); // Text of change.
            // TODO(DMS): Do replacements
            // text = text.replace(/&/g, "&amp;");
            // text = text.replace(/</g, "&lt;");
            // text = text.replace(/>/g, "&gt;");
            // text = text.replace(/\n/g, "&para;<BR>");
            if (EditType.DELETE.equals(editType)) {
                buf.append("<del style=\"background:#FFE6E6;\">");
                buf.append(text);
                buf.append("</del>");
            } else if (EditType.INSERT.equals(editType)) {
                buf.append("<ins style=\"background:#E6FFE6;\">");
                buf.append(text);
                buf.append("</ins>");
            } else {
                buf.append("<span>");
                buf.append(text);
                buf.append("</span>");
            }
        }
        return buf.toString();
    }

    /**
     * The baseline text.
     */
    private String source;

    /**
     * The changed text.
     */
    private String target;

    /**
     * Whether a slightly faster less optimal diff should be run.
     */
    private boolean checkLines;
}
