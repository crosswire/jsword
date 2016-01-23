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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A PatchEntry is a single "instruction" in a Patch, consisting of a interval
 * over which differences are applied and the differences that should be
 * applied.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class PatchEntry implements Iterable<Difference> {
    // Constructor for a patch object.
    public PatchEntry() {
        this.diffs = new ArrayList<Difference>();
        this.sourceStart = 0;
        this.targetStart = 0;
        this.sourceLength = 0;
        this.targetLength = 0;
    }

    // Constructor for a patch object.
    public PatchEntry(String patchText) {
        this();
        fromText(patchText);
    }

    /**
     * @return the sourceStart
     */
    public int getSourceStart() {
        return sourceStart;
    }

    /**
     * @param start
     *            the sourceStart to set
     */
    public void setSourceStart(int start) {
        this.sourceStart = start;
    }

    /**
     * @param adjustment
     *            the adjustment to sourceStart
     */
    public void adjustSourceStart(int adjustment) {
        this.sourceStart += adjustment;
    }

    /**
     * @return the targetStart
     */
    public int getTargetStart() {
        return targetStart;
    }

    /**
     * @param start
     *            the targetStart to set
     */
    public void setTargetStart(int start) {
        this.targetStart = start;
    }

    /**
     * @param adjustment
     *            the adjustment to targetStart
     */
    public void adjustTargetStart(int adjustment) {
        this.targetStart += adjustment;
    }

    /**
     * @return the sourceLength
     */
    public int getSourceLength() {
        return sourceLength;
    }

    /**
     * @param length
     *            the sourceLength to set
     */
    public void setSourceLength(int length) {
        this.sourceLength = length;
    }

    /**
     * @param adjustment
     *            the adjustment to sourceLength
     */
    public void adjustSourceLength(int adjustment) {
        this.sourceLength += adjustment;
    }

    /**
     * @return the targetLength
     */
    public int getTargetLength() {
        return targetLength;
    }

    /**
     * @param length
     *            the targetLength to set
     */
    public void setTargetLength(int length) {
        this.targetLength = length;
    }

    /**
     * @param adjustment
     *            the adjustment to targetLength
     */
    public void adjustTargetLength(int adjustment) {
        this.targetLength += adjustment;
    }

    // Emulate GNU diff's format.
    // Header: @@ -382,8 +481,9 @@
    // Indices are printed as 1-based, not 0-based.
    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append("@@ -");
        txt.append(getCoordinates(sourceStart, sourceLength));
        txt.append(" +");
        txt.append(getCoordinates(targetStart, targetLength));
        txt.append(" @@\n");

        for (Difference diff : diffs) {
            txt.append(diff.getEditType().getSymbol());
            txt.append(encode(diff.getText()));
            txt.append('\n');
        }
        // String result = txt.toString();
        // // Replicate the JavaScript encodeURI() function (not including %20)
        // result = result.replace("%3D", "=").replace("%3B",
        // ";").replace("%27", "'")
        // .replace("%2C", ",").replace("%2F", "/").replace("%7E", "~")
        // .replace("%21", "!").replace("%40", "@").replace("%23", "#")
        // .replace("%24", "$").replace("%26", "&").replace("%28", "(")
        // .replace("%29", ")").replace("%2B", "+").replace("%3A", ":")
        // .replace("%3F", "?");
        return txt.toString();
    }

    /**
     * Parse a textual representation of a patch entry and populate this patch
     * entry.
     * 
     * @param input
     *            Text representation of this patch entry
     * @return this patch entry
     */
    public PatchEntry fromText(String input) {
        diffs.clear();
        String[] text = newlinePattern.split(input);
        char sign = '\0';
        String line = "";

        Matcher matcher = patchPattern.matcher(text[0]);
        matcher.matches();
        assert matcher.groupCount() == 4 : "Invalid patch string:\n" + text[0];
        // m = text[0].match(/^@@ -(\d+),?(\d*) \+(\d+),?(\d*) @@$/);

        sourceStart = Integer.parseInt(matcher.group(1));

        if (matcher.group(2).length() == 0) {
            sourceStart--;
            sourceLength = 1;
        } else if (matcher.group(2).charAt(0) == '0') {
            setSourceLength(0);
        } else {
            sourceStart--;
            sourceLength = Integer.parseInt(matcher.group(2));
        }

        targetStart = Integer.parseInt(matcher.group(3));
        if (matcher.group(4).length() == 0) {
            targetStart--;
            targetLength = 1;
        } else if (matcher.group(4).charAt(0) == '0') {
            targetLength = 0;
        } else {
            targetStart--;
            targetLength = Integer.parseInt(matcher.group(4));
        }

        for (int lineCount = 1; lineCount < text.length; lineCount++) {
            line = text[lineCount];
            if (line.length() > 0) {
                sign = line.charAt(0);
                line = decode(line.substring(1));
                diffs.add(new Difference(EditType.fromSymbol(sign), line));
            }
        }
        return this;
    }

    // Compute and return the source text (all equalities and deletions).
    public String getSourceText() {
        StringBuilder txt = new StringBuilder();
        for (Difference diff : diffs) {
            if (!EditType.INSERT.equals(diff.getEditType())) {
                txt.append(diff.getText());
            }
        }
        return txt.toString();
    }

    // Compute and return the destination text (all equalities and insertions).
    public String getTargetText() {
        StringBuilder txt = new StringBuilder();
        for (Difference diff : diffs) {
            if (!EditType.DELETE.equals(diff.getEditType())) {
                txt.append(diff.getText());
            }
        }
        return txt.toString();
    }

    public void addContext(String text) {
        int maxPatternLength = new Match().maxPatternLength();
        int padding = 0;
        String pattern = text.substring(targetStart, targetStart + sourceLength);
        int textLength = text.length();

        // Increase the context until we're unique
        // (but don't let the pattern expand beyond the maximum length our
        // Locator can handle).
        int end = maxPatternLength - PatchEntry.margin - PatchEntry.margin;
        while (text.indexOf(pattern) != text.lastIndexOf(pattern) && pattern.length() < end) {
            padding += PatchEntry.margin;
            pattern = text.substring(Math.max(0, targetStart - padding), Math.min(textLength, targetStart + sourceLength + padding));
        }

        // Add one chunk for good luck.
        padding += PatchEntry.margin;

        // Add the prefix.
        String prefix = text.substring(Math.max(0, targetStart - padding), targetStart);
        int prefixLength = prefix.length();
        if (prefixLength > 0) {
            diffs.add(0, new Difference(EditType.EQUAL, prefix));
        }

        // Add the suffix
        String suffix = text.substring(targetStart + sourceLength, Math.min(textLength, targetStart + sourceLength + padding));
        int suffixLength = suffix.length();
        if (suffixLength > 0) {
            diffs.add(new Difference(EditType.EQUAL, suffix));
        }

        // Roll back the start points.
        sourceStart -= prefixLength;
        targetStart -= prefixLength;

        // Extend the lengths.
        sourceLength += prefixLength + suffixLength;
        targetLength += prefixLength + suffixLength;
    }

    public void addDifference(Difference diff) {
        diffs.add(diff);
    }

    public int getDifferenceCount() {
        return diffs.size();
    }

    public boolean hasDifferences() {
        return !diffs.isEmpty();
    }

    public Iterator<Difference> iterator() {
        return diffs.iterator();
    }

    public Difference getFirstDifference() {
        if (diffs.isEmpty()) {
            return null;
        }
        return diffs.get(0);
    }

    public Difference removeFirstDifference() {
        if (diffs.isEmpty()) {
            return null;
        }
        return diffs.remove(0);
    }

    public Difference getLastDifference() {
        if (diffs.isEmpty()) {
            return null;
        }
        return diffs.get(diffs.size() - 1);
    }

    protected void setDifferences(List<Difference> newDiffs) {
        diffs = newDiffs;
    }

    /**
     * @param newMargin
     *            the margin to set
     */
    public static void setMargin(int newMargin) {
        PatchEntry.margin = newMargin;
    }

    /**
     * @return the margin
     */
    public static int getMargin() {
        return margin;
    }

    private String getCoordinates(int start, int length) {
        StringBuilder buf = new StringBuilder();

        if (length == 0) {
            buf.append(start);
            buf.append(",0");
        } else if (length == 1) {
            buf.append(sourceStart + 1);
        } else {
            buf.append(start + 1);
            buf.append(',');
            buf.append(length);
        }

        return buf.toString();
    }

    /**
     * This algorithm allows for \n to be included in a difference. Thus it
     * needs to be escaped. We will use URL encoding of \n. But this makes % a
     * meta-character, thus it needs to be encoded too.
     * 
     * @param str
     *            the unencoded string
     * @return the encoded string
     */
    private String encode(String str) {
        int strlen = str.length();
        StringBuilder buf = new StringBuilder(2 * strlen);
        for (int i = 0; i < strlen; i++) {
            char c = str.charAt(i);
            switch (c) {
            case '%':
                buf.append("%25");
                break;
            case '\n':
                buf.append("%0A");
                break;
            default:
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Undo encoding
     * 
     * @param str
     *            the encoded string
     * @return the unencoded string
     */
    private String decode(String str) {
        int strlen = str.length();
        StringBuilder buf = new StringBuilder(2 * strlen);
        int i = 0;
        for (i = 0; i < strlen; i++) {
            char c = str.charAt(i);
            if (c == '%') {
                if ("%0A".equals(str.substring(i, i + 3))) {
                    buf.append('\n');
                } else { // if ("%25".equals(str.substring(i, i + 3))
                    buf.append('%');
                }
                i += 2;
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * Chunk size for context length.
     */
    private static final int MARGIN = 4;
    private static int margin = MARGIN;
    private static Pattern newlinePattern = Pattern.compile("\n");
    private static Pattern patchPattern = Pattern.compile("^@@ -(\\d+),?(\\d*) \\+(\\d+),?(\\d*) @@$");

    private List<Difference> diffs;
    private int sourceStart;
    private int targetStart;
    private int sourceLength;
    private int targetLength;
}
