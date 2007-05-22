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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Computes the difference between two texts to create a list of differences.
 * 
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
     * Run a faster slightly less optimal diff.
     * This method allows the 'checkLines' of main() to be optional.
     * Most of the time checkLines is wanted, so default to true.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     */
    public Diff(final String text1, final String text2)
    {
        this(text1, text2, true);
    }

    /**
     * Find the differences between two texts.  Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     * @param checkLines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff
     */
    public Diff(final String text1, final String text2, final boolean checkLines)
    {
        this.text1 = text1;
        this.text2 = text2;
        this.checkLines = checkLines;
    }

    /**
     * Find the differences between two texts.  Simplifies the problem by
     * stripping any common prefix or suffix off the texts before diffing.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     * @param checkLines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff
     * @return List of Difference objects
     */
    public List compare()
    {
        // Check for equality (speedup)
        List diffs;
        if (text1.equals(text2))
        {
            diffs = new ArrayList();
            diffs.add(new Difference(EditType.EQUAL, text1));
            return diffs;
        }

        // Trim off common prefix (speedup)
        int commonLength = Commonality.prefix(text1, text2);
        String commonPrefix = text1.substring(0, commonLength);
        text1 = text1.substring(commonLength);
        text2 = text2.substring(commonLength);

        // Trim off common suffix (speedup)
        commonLength = Commonality.suffix(text1, text2);
        String commonSuffix = text1.substring(text1.length() - commonLength);
        text1 = text1.substring(0, text1.length() - commonLength);
        text2 = text2.substring(0, text2.length() - commonLength);

        // Compute the diff on the middle block
        diffs = compute();

        // Restore the prefix and suffix
        if (!commonPrefix.equals("")) //$NON-NLS-1$
        {
            diffs.add(0, new Difference(EditType.EQUAL, commonPrefix));
        }

        if (!commonSuffix.equals("")) //$NON-NLS-1$
        {
            diffs.add(new Difference(EditType.EQUAL, commonSuffix));
        }

        DiffCleanup.cleanupMerge(diffs);

        return diffs;
    }

    /**
     * Find the differences between two texts.
     * @param text1 Old string to be diffed
     * @param text2 New string to be diffed
     * @param checkLines Speedup flag.  If false, then don't run a
     *     line-level diff first to identify the changed areas.
     *     If true, then run a faster slightly less optimal diff
     * @return List of Difference objects
     */
    private List compute()
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

        String longText = text1.length() > text2.length() ? text1 : text2;
        String shortText = text1.length() > text2.length() ? text2 : text1;
        int i = longText.indexOf(shortText);
        if (i != -1)
        {
            // Shorter text is inside the longer text (speedup)
            EditType editType = (text1.length() > text2.length()) ? EditType.DELETE : EditType.INSERT;
            diffs.add(new Difference(editType, longText.substring(0, i)));
            diffs.add(new Difference(EditType.EQUAL, shortText));
            diffs.add(new Difference(editType, longText.substring(i + shortText.length())));
            return diffs;
        }

        // Garbage collect
        longText = null;
        shortText = null;

        // Check to see if the problem can be split in two.
        CommonMiddle middleMatch = Commonality.halfMatch(text1, text2);
        if (middleMatch != null)
        {
            // A half-match was found, sort out the return data.
            // Send both pairs off for separate processing.
            Diff startDiff = new Diff(middleMatch.getSourceStart(), middleMatch.getTargetStart(), checkLines);
            Diff endDiff = new Diff(middleMatch.getSourceEnd(), middleMatch.getTargetEnd(), checkLines);
            // Merge the results.
            diffs = startDiff.compare();
            diffs.add(new Difference(EditType.EQUAL, middleMatch.getCommonality()));
            diffs.addAll(endDiff.compare());
            return diffs;
        }

        // Perform a real diff.
        if (checkLines && text1.length() + text2.length() < 250)
        {
            checkLines = false; // Too trivial for the overhead.
        }

        LineMap lineMap = null;
        if (checkLines)
        {
            // Scan the text on a line-by-line basis first.
            lineMap = new LineMap(text1, text2);
            text1 = lineMap.getSourceMap();
            text2 = lineMap.getTargetMap();
        }

        diffs = new DifferenceEngine(text1, text2).generate();

        if (diffs == null)
        {
            // No acceptable result.
            diffs = new ArrayList();
            diffs.add(new Difference(EditType.DELETE, text1));
            diffs.add(new Difference(EditType.INSERT, text2));
        }

        if (checkLines)
        {
            // Convert the diff back to original text.
            lineMap.restore(diffs);
            // Eliminate freak matches (e.g. blank lines)
            DiffCleanup.cleanupSemantic(diffs);

            // Rediff any replacement blocks, this time character-by-character.
            // Add a dummy entry at the end.
            diffs.add(new Difference(EditType.EQUAL, "")); //$NON-NLS-1$
            int countDeletes = 0;
            int countInserts = 0;
            String textDelete = ""; //$NON-NLS-1$
            String textInsert = ""; //$NON-NLS-1$
            ListIterator pointer = diffs.listIterator();
            Difference curDiff = (Difference) pointer.next();
            while (curDiff != null)
            {
                EditType editType = curDiff.getEditType();
                if (EditType.INSERT.equals(editType))
                {
                    countInserts++;
                    textInsert += curDiff.getText();
                }
                else if (EditType.DELETE.equals(editType))
                {
                    countDeletes++;
                    textDelete += curDiff.getText();
                }
                else
                {
                    // Upon reaching an equality, check for prior redundancies.
                    if (countDeletes >= 1 && countInserts >= 1)
                    {
                        // Delete the offending records and add the merged ones.
                        pointer.previous();
                        for (int j = 0; j < countDeletes + countInserts; j++)
                        {
                            pointer.previous();
                            pointer.remove();
                        }
                        Diff newDiff = new Diff(textDelete, textInsert, false);
                        Iterator iter = newDiff.compare().iterator();
                        while (iter.hasNext())
                        {
                            pointer.add(iter.next());
                        }
                    }
                    countInserts = 0;
                    countDeletes = 0;
                    textDelete = ""; //$NON-NLS-1$
                    textInsert = ""; //$NON-NLS-1$
                }
                curDiff = pointer.hasNext() ? (Difference) pointer.next() : null;
            }
            diffs.remove(diffs.size() - 1); // Remove the dummy entry at the end.
        }
        return diffs;
    }

    /**
     * loc is a location in text1, compute and return the equivalent location in
     * text2.
     * e.g. "The cat" vs "The big cat", 1->1, 5->8
     * @param diffs List of Difference objects
     * @param loc Location within text1
     * @return Location within text2
     */
    public int xIndex(final List diffs, final int loc)
    {
        int chars1 = 0;
        int chars2 = 0;
        int lastChars1 = 0;
        int lastChars2 = 0;
        Difference lastDiff = null;
        Iterator iter = diffs.iterator();
        while (iter.hasNext())
        {
            Difference diff = (Difference) iter.next();
            EditType editType = diff.getEditType();

            if (!EditType.INSERT.equals(editType)) // Equality or deletion.
            {
                chars1 += diff.getText().length();
            }

            if (!EditType.DELETE.equals(editType)) // Equality or insertion.
            {
                chars2 += diff.getText().length();
            }

            if (chars1 > loc) // Overshot the location.
            {
                lastDiff = diff;
                break;
            }
            lastChars1 = chars1;
            lastChars2 = chars2;
        }

        if (lastDiff != null && EditType.DELETE.equals(lastDiff.getEditType())) // The location was deleted.
        {
            return lastChars2;
        }

        // Add the remaining character length.
        return lastChars2 + (loc - lastChars1);
    }

    /**
     * Convert a Difference list into a pretty HTML report.
     * @param diffs List of Difference objects
     * @return HTML representation
     */
    public String prettyHtml(List diffs)
    {
        StringBuffer buf = new StringBuffer();
        for (int x = 0; x < diffs.size(); x++)
        {
            Difference diff = (Difference) diffs.get(x);
            EditType editType = diff.getEditType(); // Mode (delete, equal, insert)
            String text = diff.getText(); // Text of change.
            // TODO(DMS): Do replacements
            // text = text.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
            // text = text.replace(/\n/g, "&para;<BR>");
            if (EditType.DELETE.equals(editType))
            {
                buf.append("<del style=\"background:#FFE6E6;\">"); //$NON-NLS-1$
                buf.append(text);
                buf.append("</del>"); //$NON-NLS-1$
            }
            else if (EditType.INSERT.equals(editType))
            {
                buf.append("<ins style=\"background:#E6FFE6;\">"); //$NON-NLS-1$
                buf.append(text);
                buf.append("</ins>"); //$NON-NLS-1$
            }
            else
            {
                buf.append("<span>"); //$NON-NLS-1$
                buf.append(text);
                buf.append("</span>"); //$NON-NLS-1$
            }
        }
        return buf.toString();
    }

    /**
     * The baseline text.
     */
    private String text1;

    /**
     * The changed text.
     */
    private String text2;

    /**
     * Whether a slightly faster less optimal diff should be run.
     */
    private boolean checkLines;
}
