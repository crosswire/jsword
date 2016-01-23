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
import java.util.List;
import java.util.Map;

/**
 * LineMap is a heuristic algorithm that allows the differencing of a
 * representation of lines. A Diff of the source and target maps can be
 * reconstituted with restore.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class LineMap {
    /**
     * Split two texts into a list of strings. Reduce the texts to a string of
     * hashes where each Unicode character represents one line. The result is
     * that text1 is encoded into
     * 
     * @param source
     *            Baseline string
     * @param target
     *            Changed string
     */
    public LineMap(final String source, final String target) {
        // e.g. linearray[4] == "Hello\n"
        // e.g. linehash.get("Hello\n") == 4

        // "\x00" is a valid character, but various debuggers don't like it.
        // So we'll insert a junk entry to avoid generating a null character.
        lines = new ArrayList<String>();
        lines.add("");

        Map<String, Integer> linehash = new HashMap<String, Integer>();
        sourceMap = linesToCharsMunge(source, lines, linehash);
        targetMap = linesToCharsMunge(target, lines, linehash);
    }

    /**
     * Rehydrate the text in a diff from a string of line hashes to real lines
     * of text.
     * 
     * @param diffs
     *            List of Difference objects
     */
    public void restore(final List<?> diffs) {
        StringBuilder text = new StringBuilder();
        for (int x = 0; x < diffs.size(); x++) {
            Difference diff = (Difference) diffs.get(x);
            String chars = diff.getText();

            text.delete(0, text.length());
            for (int y = 0; y < chars.length(); y++) {
                text.append(lines.get(chars.charAt(y)));
            }

            diff.setText(text.toString());
        }
    }

    /**
     * @return the sourceMap
     */
    public String getSourceMap() {
        return sourceMap;
    }

    /**
     * @return the targetMap
     */
    public String getTargetMap() {
        return targetMap;
    }

    /**
     * @return the lines
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     * Split a text into a list of strings. Reduce the texts to a string of
     * hashes where each Unicode character represents one line.
     * 
     * @param text
     *            String to encode
     * @param linearray
     *            List of unique strings
     * @param linehash
     *            Map of strings to indices
     * @return Encoded string
     */
    private String linesToCharsMunge(final String text, List<String> linearray, Map<String, Integer> linehash) {
        StringBuilder buf = new StringBuilder();
        String work = text;
        // text.split('\n') would work fine, but would temporarily double our
        // memory footprint for minimal speed improvement.
        while (work.length() != 0) {
            int i = work.indexOf('\n');
            if (i == -1) {
                i = work.length() - 1;
            }
            String line = work.substring(0, i + 1);
            work = work.substring(i + 1);
            if (linehash.containsKey(line)) {
                Integer charInt = linehash.get(line);
                buf.append(String.valueOf((char) charInt.intValue()));
            } else {
                linearray.add(line);
                linehash.put(line, Integer.valueOf(linearray.size() - 1));
                buf.append(String.valueOf((char) (linearray.size() - 1)));
            }
        }
        return buf.toString();
    }

    /**
     * Each character in sourceMap provides an integer representation of the
     * line in the original.
     */
    private String sourceMap;

    /**
     * Each character in sourceMap provides an integer representation of the
     * line in the original.
     */
    private String targetMap;

    /**
     * The lines from the original. Useful for reconstitution.
     */
    private List<String> lines;
}
