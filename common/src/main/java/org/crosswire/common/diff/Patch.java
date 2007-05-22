package org.crosswire.common.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patch
{
    /**
     * Compute a list of patches to turn text1 into text2.
     * A set of diffs will be computed.
     * @param text1 Old text
     * @param text2 New text
     * @return List of PatchEntry objects.
     */
    public static List make(String text1, String text2)
    {
        List diffs = Diff.main(text1, text2, true);
        if (diffs.size() > 2)
        {
            Diff.cleanupSemantic(diffs);
            Diff.cleanupEfficiency(diffs);
        }
        return make(text1, text2, diffs);
    }

    /**
     * Compute a list of patches to turn text1 into text2.
     * Use the diffs provided.
     * @param text1 Old text
     * @param text2 New text
     * @param diffs Optional array of diff tuples for text1 to text2.
     * @return LinkedList of Patch objects.
     */
    public static List make(String text1, String text2, List diffList)
    {
        List patches = new ArrayList();
        List diffs = diffList;

        assert diffs != null;

        if (diffs.size() == 0)
        {
            return patches; // Get rid of the null case.
        }

        PatchEntry patch = new PatchEntry();
        int char_count1 = 0; // Number of characters into the text1 string.
        int char_count2 = 0; // Number of characters into the text2 string.
        // Recreate the patches to determine context info.
        String prepatch_text = text1;
        String postpatch_text = text1;
        Iterator iter = diffs.iterator();
        int x = 0;
        while (iter.hasNext())
        {
            Difference diff = (Difference) iter.next();
            EditType editType = diff.getEditType();
            String diffText = diff.getText();
            int len = diffText.length();

            if (!patch.hasDifferences() && !EditType.EQUAL.equals(editType))
            {
                // A new patch starts here.
                patch.setLeftStart(char_count1);
                patch.setRightStart(char_count2);
            }

            if (EditType.INSERT.equals(editType))
            {
                // Insertion
                patch.addDifference(diff);
                patch.adjustLength2(len);
                postpatch_text = postpatch_text.substring(0, char_count2) + diffText + postpatch_text.substring(char_count2);
            }
            else if (EditType.DELETE.equals(editType))
            {
                // Deletion.
                patch.adjustLength1(len);
                patch.addDifference(diff);
                postpatch_text = postpatch_text.substring(0, char_count2) + postpatch_text.substring(char_count2 + len);
            }
            else if (EditType.EQUAL.equals(editType) && len <= 2 * Patch.MARGIN && patch.hasDifferences() && diffs.size() != x + 1)
            {
                // Small equality inside a patch.
                patch.addDifference(diff);
                patch.adjustLength1(len);
                patch.adjustLength2(len);
            }

            if (EditType.EQUAL.equals(editType) && len >= 2 * Patch.MARGIN)
            {
                // Time for a new patch.
                if (patch.hasDifferences())
                {
                    patch.addContext(prepatch_text);
                    patches.add(patch);
                    patch = new PatchEntry();
                    prepatch_text = postpatch_text;
                }
            }

            // Update the current character count.
            if (!EditType.INSERT.equals(editType))
            {
                char_count1 += len;
            }

            if (!EditType.DELETE.equals(editType))
            {
                char_count2 += len;
            }

            x++;
        }

        // Pick up the leftover patch if not empty.
        if (patch.hasDifferences())
        {
            patch.addContext(prepatch_text);
            patches.add(patch);
        }

        return patches;
    }


    /**
     * Merge a set of patches onto the text.  Return a patched text, as well
     * as an array of true/false values indicating which patches were applied.
     * @param patches Array of patch objects
     * @param text Old text
     * @return the patch result
     */
    public static PatchResults apply(List patches, String text)
    {
        Patch.splitMax(patches);
        boolean[] results = new boolean[patches.size()];
        String resultText = text;
        int delta = 0;
        int expected_loc = 0;
        int start_loc = -1;
        String text1 = ""; //$NON-NLS-1$
        String text2 = ""; //$NON-NLS-1$
        List diffs;
        int index1 = 0;
        int index2 = 0;
        int x = 0;
        Iterator patchIter = patches.iterator();
        while (patchIter.hasNext())
        {
            PatchEntry aPatch = (PatchEntry) patchIter.next();
            expected_loc = aPatch.getRightStart() + delta;
            text1 = aPatch.getLeftText();
            start_loc = Match.main(resultText, text1, expected_loc);
            if (start_loc == -1)
            {
                // No match found.  :(
                results[x] = false;
            }
            else
            {
                // Found a match.  :)
                results[x] = true;
                delta = start_loc - expected_loc;
                text2 = resultText.substring(start_loc, start_loc + text1.length());
                if (text1.equals(text2))
                {
                    // Perfect match, just shove the replacement text in.
                    resultText = resultText.substring(0, start_loc) + aPatch.getRightText() + resultText.substring(start_loc + text1.length());
                }
                else
                {
                    // Imperfect match.  Run a diff to get a framework of equivalent indicies.
                    diffs = Diff.main(text1, text2, false);
                    index1 = 0;
                    Iterator diffIter = aPatch.iterator();
                    while (diffIter.hasNext())
                    {
                        Difference aDiff = (Difference) diffIter.next();
                        EditType editType = aDiff.getEditType();
                        if (!EditType.EQUAL.equals(editType))
                        {
                            index2 = Diff.xIndex(diffs, index1);
                        }

                        if (EditType.INSERT.equals(editType)) // Insertion
                        {
                            resultText = resultText.substring(0, start_loc + index2) + aDiff.getText() + resultText.substring(start_loc + index2);
                        }
                        else if (EditType.DELETE.equals(editType)) // Deletion
                        {
                            resultText = resultText.substring(0, start_loc + index2) + resultText.substring(start_loc + Diff.xIndex(diffs, index1 + aDiff.getText().length()));
                        }

                        if (!EditType.DELETE.equals(editType))
                        {
                            index1 += aDiff.getText().length();
                        }
                    }
                }
            }
        }
        return new PatchResults(resultText, results);
    }

    /**
     * Look through the patches and break up any which are longer than the maximum
     * limit of the match algorithm.
     * @param patches List of Patch objects.
     */
    public static void splitMax(List patches)
    {
        ListIterator pointer = patches.listIterator();
        PatchEntry bigpatch = pointer.hasNext() ? (PatchEntry) pointer.next() : null;
        while (bigpatch != null)
        {
            if (bigpatch.getLeftLength() <= Match.MAXBITS)
            {
                bigpatch = pointer.hasNext() ? (PatchEntry) pointer.next() : null;
            }

            // Remove the big old patch.
            pointer.remove();
            int patch_size = Match.MAXBITS;
            int start1 = bigpatch.getLeftStart();
            int start2 = bigpatch.getRightStart();
            String precontext = ""; //$NON-NLS-1$
            while (bigpatch.hasDifferences())
            {
                // Create one of several smaller patches.
                PatchEntry patch = new PatchEntry();
                boolean empty = true;

                int len = precontext.length();
                patch.setLeftStart(start1 - len);
                patch.setRightStart(start2 - len);
                if (len > 0)
                {
                    patch.setLeftLength(len);
                    patch.setRightLength(len);
                    patch.addDifference(new Difference(EditType.EQUAL, precontext));
                }

                while (bigpatch.hasDifferences() && patch.getLeftLength() < patch_size - Patch.MARGIN)
                {
                    Difference bigDiff = bigpatch.getFirstDifference();
                    EditType diff_type = bigDiff.getEditType();
                    String diff_text = bigDiff.getText();
                    if (EditType.INSERT.equals(diff_type))
                    {
                        // Insertions are harmless.
                        len = diff_text.length();
                        patch.adjustLength2(len);
                        start2 += len;
                        patch.addDifference(bigpatch.removeFirstDifference());
                        empty = false;
                    }
                    else
                    {
                        // Deletion or equality.  Only take as much as we can stomach.
                        len = diff_text.length();
                        diff_text = diff_text.substring(0, Math.min(len, patch_size - patch.getLeftLength() - Patch.MARGIN));
                        patch.adjustLength1(len);
                        start1 += len;
                        if (EditType.EQUAL.equals(diff_type))
                        {
                            patch.adjustLength2(len);
                            start2 += len;
                        }
                        else
                        {
                            empty = false;
                        }

                        patch.addDifference(new Difference(diff_type, diff_text));

                        if (diff_text.equals(bigDiff.getText()))
                        {
                            bigpatch.removeFirstDifference();
                        }
                        else
                        {
                            bigDiff.setText(bigDiff.getText().substring(len));
                        }
                    }
                }

                // Compute the head context for the next patch.
                precontext = patch.getRightText();
                precontext = precontext.substring(precontext.length() - Patch.MARGIN);

                // Append the end context for this patch.
                String postcontext = bigpatch.getLeftText().substring(0, Patch.MARGIN);
                if (postcontext.length() > 0)
                {
                    patch.adjustLength1(postcontext.length());
                    patch.adjustLength2(postcontext.length());
                    if (patch.getDifferenceCount() > 0 && EditType.EQUAL.equals(patch.getLastDifference().getEditType()))
                    {
                        Difference diff = patch.getLastDifference();
                        diff.appendText(postcontext);
                    }
                    else
                    {
                        patch.addDifference(new Difference(EditType.EQUAL, postcontext));
                    }
                }

                if (!empty)
                {
                    pointer.add(patch);
                }
            }

            bigpatch = pointer.hasNext() ? (PatchEntry) pointer.next() : null;
        }
    }

    /**
     * Take a list of patches and return a textual representation.
     * @param patches List of Patch objects.
     * @return Text representation of patches.
     */
    public static String toText(List patches)
    {
        StringBuffer text = new StringBuffer();
        Iterator iter = patches.iterator();
        while (iter.hasNext())
        {
            text.append(iter.next());
        }
        return text.toString();
    }

    /**
     * Parse a textual representation of patches and return a List of Patch
     * objects.
     * @param textline Text representation of patches
     * @return List of Patch objects
     */
    public static List fromText(String input)
    {
        List patches = new ArrayList();
        String[] text = input.split("\n"); //$NON-NLS-1$
        PatchEntry patch = null;
        char sign = '\0';
        String line = ""; //$NON-NLS-1$

        int lineCount = 0;
        while (lineCount < text.length)
        {
            Matcher matcher = patchPattern.matcher(text[lineCount]);
            matcher.matches();
            assert matcher.groupCount() == 4 : "Invalid patch string:\n" + text[lineCount]; //$NON-NLS-1$
            // m = text[0].match(/^@@ -(\d+),?(\d*) \+(\d+),?(\d*) @@$/);

            patch = new PatchEntry();
            patches.add(patch);
            patch.setLeftStart(Integer.parseInt(matcher.group(1)));

            if (matcher.group(2).length() == 0)
            {
                patch.adjustStart1(-1);
                patch.setLeftLength(1);
            }
            else if (matcher.group(2).charAt(0) == '0')
            {
                patch.setLeftLength(0);
            }
            else
            {
                patch.adjustStart1(-1);
                patch.setLeftLength(Integer.parseInt(matcher.group(2)));
            }

            patch.setRightStart(Integer.parseInt(matcher.group(3)));
            if (matcher.group(4).length() == 0)
            {
                patch.adjustStart2(-1);
                patch.setRightLength(1);
            }
            else if (matcher.group(4).charAt(0) == '0')
            {
                patch.setRightLength(0);
            }
            else
            {
                patch.adjustStart2(-1);
                patch.setRightLength(Integer.parseInt(matcher.group(4)));
            }
            lineCount++;

            consume:
                while (lineCount < text.length)
                {
                    if (text[lineCount].length() > 0)
                    {
                        sign = text[lineCount].charAt(0);
                        line = text[lineCount].substring(1);
                        switch (sign)
                        {
                            case '-': // Deletion.
                                patch.addDifference(new Difference(EditType.DELETE, line));
                                break;
                            case '+': // Insertion.
                                patch.addDifference(new Difference(EditType.INSERT, line));
                                break;
                            case ' ': // Minor equality.
                                patch.addDifference(new Difference(EditType.EQUAL, line));
                                break;
                            case '@': // start of next patch
                                break consume;
                            default: // What!!!
                                assert false : "Invalid patch mode: '" + sign + "'\n" + line; //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                    lineCount++;
                }
        }
        return patches;
    }

    public static class PatchResults
    {
        /**
         * @param text
         * @param results
         */
        public PatchResults(String text, boolean[] results)
        {
            this.text = text;
            this.results = (boolean[]) results.clone();
        }

        /**
         * @return the results
         */
        public boolean[] getResults()
        {
            return (boolean[]) results.clone();
        }

        /**
         * @return the text
         */
        public String getText()
        {
            return text;
        }

        private String text;
        private boolean[] results;
    }

    /**
     * Chunk size for context length.
     */
    public static final int MARGIN = 4;

    private static String patchRE = "^@@ -(\\d+),?(\\d*) \\+(\\d+),?(\\d*) @@$"; //$NON-NLS-1$
    private static Pattern patchPattern = Pattern.compile(patchRE);

}
