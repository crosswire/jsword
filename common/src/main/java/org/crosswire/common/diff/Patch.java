package org.crosswire.common.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patch
{
    //  Constructor for a patch object.
    public Patch()
    {
        this.diffs = new ArrayList();
        this.start1 = 0;
        this.start2 = 0;
        this.length1 = 0;
        this.length2 = 0;
    }

    //  Emmulate GNU diff's format.
    //  Header: @@ -382,8 +481,9 @@
    //  Indicies are printed as 1-based, not 0-based.
    public String toString()
    {
        StringBuffer txt = new StringBuffer();
        txt.append("@@ -"); //$NON-NLS-1$
        txt.append(getCoordinates(start1, length1));
        txt.append(" +"); //$NON-NLS-1$
        txt.append(getCoordinates(start2, length2));
        txt.append(" @@\n"); //$NON-NLS-1$

        Iterator iter = diffs.iterator();
        while (iter.hasNext())
        {
            Difference diff = (Difference) iter.next();
            txt.append(diff.getEditType().getSymbol());
            txt.append(diff.getText());
            txt.append('\n');
        }
        return txt.toString();
    }

    private String getCoordinates(int start, int length)
    {
        StringBuffer buf = new StringBuffer();

        buf.append(start);
        if (length == 0)
        {
            buf.append(start);
            buf.append(".0"); //$NON-NLS-1$
        }
        else if (length == 1)
        {
            buf.append(start1 + 1);
        }
        else
        {
            buf.append(start + 1);
            buf.append(',');
            buf.append(length);
        }

        return buf.toString();
    }

    //  Compute and return the source text (all equalities and deletions).
    private String getText1()
    {
        StringBuffer txt = new StringBuffer();
        Iterator iter = diffs.iterator();
        while (iter.hasNext())
        {
            Difference diff = (Difference) iter.next();
            if (!EditType.INSERT.equals(diff.getEditType()))
            {
                txt.append(diff.getText());
            }
        }
        return txt.toString();
    }

    // Compute and return the destination text (all equalities and insertions).
    private String getText2()
    {
        StringBuffer txt = new StringBuffer();
        Iterator iter = diffs.iterator();
        while (iter.hasNext())
        {
            Difference diff = (Difference) iter.next();
            if (!EditType.DELETE.equals(diff.getEditType()))
            {
                txt.append(diff.getText());
            }
        }
        return txt.toString();
    }


    private void addContext(String text)
    {
        String pattern = text.substring(start2, start2+length1);
        int padding = 0;

        // Increase the context until we're unique (but don't let the pattern expand beyond Match.MAXBITS).
        int end = Match.MAXBITS - Patch.MARGIN - Patch.MARGIN;
        while (text.indexOf(pattern) != text.lastIndexOf(pattern) && pattern.length() < end)
        {
            padding += Patch.MARGIN;
            pattern = text.substring(start2 - padding, start2 + length1 + padding);
        }

        // Add one chunk for good luck.
        padding += Patch.MARGIN;

        // Add the prefix.
        String prefix = text.substring(start2 - padding, start2);
        int prefixLength = prefix.length();
        if (prefixLength > 0)
        {
            diffs.add(0, new Difference(EditType.EQUAL, prefix));
        }

        // Add the suffix
        String suffix = text.substring(start2 + length1, start2 + length1 + padding);
        int suffixLength = suffix.length();
        if (suffixLength > 0)
        {
            diffs.add(new Difference(EditType.EQUAL, suffix));
        }

        // Roll back the start points.
        start1 -= prefixLength;
        start2 -= prefixLength;

        // Extend the lengths.
        length1 += prefixLength + suffixLength;
        length2 += prefixLength + suffixLength;
    }

    // Compute a list of patches to turn text1 into text2.
    public static List make(String text1, String text2, List diffList)
    {
        List patches = new ArrayList();
        List diffs = diffList;

        // Use diff if provided, otherwise compute it ourselves.
        if (diffs == null)
        {
            diffs = Diff.main(text1, text2, true);
            if (diffs.size() > 2)
            {
                Diff.cleanup_semantic(diffs);
                Diff.cleanup_efficiency(diffs);
            }
        }

        if (diffs.size() == 0)
        {
            return patches; // Get rid of the null case.
        }

        Patch patch = new Patch();
        int char_count1 = 0; // Number of characters into the text1 string.
        int char_count2 = 0; // Number of characters into the text2 string.
        String prepatch_text = text1; // Recreate the patches to determine context info.
        String postpatch_text = text1;
        Iterator iter = diffs.iterator();
        int x = 0;
        while (iter.hasNext())
        {
            Difference diff = (Difference) iter.next();
            EditType diff_type = diff.getEditType();
            String diff_text = diff.getText();
            int len = diff_text.length();

            if (patch.diffs.size() == 0 && !EditType.EQUAL.equals(diff_type))
            {
                // A new patch starts here.
                patch.start1 = char_count1;
                patch.start2 = char_count2;
            }

            if (EditType.INSERT.equals(diff_type))
            {
                // Insertion
                patch.diffs.add(diff);
                patch.length2 += len;
                postpatch_text = postpatch_text.substring(0, char_count2) + diff_text + postpatch_text.substring(char_count2);
            }
            else if (EditType.DELETE.equals(diff_type))
            {
                // Deletion.
                patch.length1 += len;
                patch.diffs.add(diff);
                postpatch_text = postpatch_text.substring(0, char_count2) + postpatch_text.substring(char_count2 + len);
            }
            else if (EditType.EQUAL.equals(diff_type) && len <= 2 * Patch.MARGIN && patch.diffs.size() != 0 && diffs.size() != x + 1)
            {
                // Small equality inside a patch.
                patch.diffs.add(diff);
                patch.length1 += len;
                patch.length2 += len;
            }

            if (EditType.EQUAL.equals(diff_type) && len >= 2 * Patch.MARGIN) {
                // Time for a new patch.
                if (patch.diffs.size() != 0)
                {
                    patch.addContext(prepatch_text);
                    patches.add(patch);
                    patch = new Patch();
                    prepatch_text = postpatch_text;
                }
            }

            // Update the current character count.
            if (!EditType.INSERT.equals(diff_type))
            {
                char_count1 += len;
            }

            if (!EditType.DELETE.equals(diff_type))
            {
                char_count2 += len;
            }

            x++;
        }

        // Pick up the leftover patch if not empty.
        if (patch.diffs.size() != 0) {
            patch.addContext(prepatch_text);
            patches.add(patch);
        }

        return patches;
    }


    //  Merge a set of patches onto the text.
    //  Return a patched text, as well as a list of true/false values indicating which patches were applied.
    public static PatchResults apply(List patches, String text)
    {
        patches = Patch.splitmax(patches);
        List results = new ArrayList();
        int delta = 0;
        int expected_loc = 0;
        int start_loc = -1;
        String text1 = ""; //$NON-NLS-1$
        String text2 = ""; //$NON-NLS-1$
        List diff;
        Difference mod = null;
        int index1 = 0;
        int index2 = 0;
        for (int x = 0; x < patches.size(); x++)
        {
            Patch curPatch = (Patch) patches.get(x);
            expected_loc = curPatch.start2 + delta;
            text1 = curPatch.getText1();
            start_loc = Match.main(text, text1, expected_loc);
            if (start_loc == -1)
            {
                // No match found.  :(
                results.add(Boolean.FALSE);
            }
            else
            {
                // Found a match.  :)
                results.add(Boolean.TRUE);
                delta = start_loc - expected_loc;
                text2 = text.substring(start_loc, start_loc + text1.length());
                if (text1 == text2)
                {
                    // Perfect match, just shove the replacement text in.
                    text = text.substring(0, start_loc) + curPatch.getText2() + text.substring(start_loc + text1.length());
                }
                else
                {
                    // Imperfect match.  Run a diff to get a framework of equivalent indicies.
                    diff = Diff.main(text1, text2, false);
                    index1 = 0;
                    for (int y = 0; y < curPatch.diffs.size(); y++) {
                        mod = (Difference) curPatch.diffs.get(y);
                        EditType diff_type = mod.getEditType();
                        if (!EditType.EQUAL.equals(diff_type))
                        {
                            index2 = Diff.xindex(diff, index1);
                        }

                        if (EditType.INSERT.equals(diff_type)) // Insertion
                        {
                            text = text.substring(0, start_loc + index2) + mod.getText() + text.substring(start_loc + index2);
                        }
                        else if (EditType.DELETE.equals(diff_type)) // Deletion
                        {
                            text = text.substring(0, start_loc + index2) + text.substring(start_loc + Diff.xindex(diff, index1 + mod.getText().length()));
                        }

                        if (!EditType.DELETE.equals(diff_type))
                        {
                            index1 += mod.getText().length();
                        }
                    }
                }
            }
        }
        return new PatchResults(text, results);
    }


    //Look through the patches and break up any which are longer than the maximum limit of the match algorithm.
    static public List splitmax(List patches)
    {
        Patch bigpatch = null;
        Patch patch = null;
        int patch_size = 0;
        EditType diff_type = null;
        int start1 = 0;
        int start2 = 0;
        String diff_text = ""; //$NON-NLS-1$
        String precontext = ""; //$NON-NLS-1$
        String postcontext = ""; //$NON-NLS-1$
        boolean empty = true;
        for (int x = 0; x < patches.size(); x++)
        {
            Patch curPatch = (Patch) patches.get(x);
            if (curPatch.length1 > Match.MAXBITS)
            {
                bigpatch = curPatch;
                // Remove the big old patch.
                patches.remove(x);
                patch_size = Match.MAXBITS;
                start1 = bigpatch.start1;
                start2 = bigpatch.start2;
                precontext = ""; //$NON-NLS-1$
                while (bigpatch.diffs.size() != 0)
                {
                    // Create one of several smaller patches.
                    patch = new Patch();
                    empty = true;

                    int len = precontext.length();
                    patch.start1 = start1 - len;
                    patch.start2 = start2 - len;
                    if (len > 0)
                    {
                        patch.length1 = len;
                        patch.length2 = len;
                        patch.diffs.add(new Difference(EditType.EQUAL, precontext));
                    }

                    while (bigpatch.diffs.size() != 0 && patch.length1 < patch_size - Patch.MARGIN)
                    {
                        Difference bigDiff = (Difference) bigpatch.diffs.get(0);
                        diff_type = bigDiff.getEditType();
                        diff_text = bigDiff.getText();
                        if (EditType.INSERT.equals(diff_type))
                        {
                            // Insertions are harmless.
                            len = diff_text.length();
                            patch.length2 += len;
                            start2 += len;
                            patch.diffs.add(bigpatch.diffs.remove(0));
                            empty = false;
                        }
                        else
                        {
                            // Deletion or equality.  Only take as much as we can stomach.
                            diff_text = diff_text.substring(0, patch_size - patch.length1 - Patch.MARGIN);
                            len = diff_text.length();
                            patch.length1 += len;
                            start1 += len;
                            if (EditType.EQUAL.equals(diff_type))
                            {
                                patch.length2 += len;
                                start2 += len;
                            }
                            else
                            {
                                empty = false;
                            }

                            patch.diffs.add(new Difference(diff_type, diff_text));

                            if (diff_text.equals(bigDiff.getText()))
                            {
                                bigpatch.diffs.remove(0);
                            }
                            else
                            {
                                bigDiff.setText(bigDiff.getText().substring(len));
                            }
                        }
                    }

                    // Compute the head context for the next patch.
                    precontext = patch.getText2();
                    precontext = precontext.substring(precontext.length() - Patch.MARGIN);

                    // Append the end context for this patch.
                    postcontext = bigpatch.getText1().substring(0, Patch.MARGIN);
                    if (postcontext.length() > 0)
                    {
                        patch.length1 += postcontext.length();
                        patch.length2 += postcontext.length();
                        if (patch.diffs.size() > 0 && EditType.EQUAL.equals(((Difference) patch.diffs.get(patch.diffs.size() - 1)).getEditType()))
                        {
                            Difference diff = (Difference) patch.diffs.get(patch.diffs.size() - 1);
                            diff.appendText(postcontext);
                        }
                        else
                        {
                            patch.diffs.add(new Difference(EditType.EQUAL, postcontext));
                        }
                    }

                    if (!empty)
                    {
                        patches.add(x++, patch);
                    }
                }
            }
        }

        return patches;
    }

    // Take a list of patches and return a textual representation.
    public static String toText(List patches) {
        StringBuffer text = new StringBuffer();
        Iterator iter = patches.iterator();
        while (iter.hasNext())
        {
            text.append(iter.next());
        }
        return text.toString();
    }

    // Take a textual representation of patches and return a list of patch objects.
    public static List fromText(String input)
    {
        List patches = new ArrayList();
        String[] text = input.split("\n"); //$NON-NLS-1$
        Patch patch = null;
        char sign = '\0';
        String line = ""; //$NON-NLS-1$

        int lineCount = 0;
        while (lineCount < text.length)
        {
            Matcher matcher = patchPattern.matcher(text[lineCount]);
            if (!matcher.find())
            {
                throw new RuntimeException("Invalid patch string:\n"+text[lineCount]); //$NON-NLS-1$
            }
            // m = text[0].match(/^@@ -(\d+),?(\d*) \+(\d+),?(\d*) @@$/);


            patch = new Patch();
            patches.add(patch);
            patch.start1 = Integer.parseInt(matcher.group(1));

            if (matcher.group(2).length() == 0)
            {
                patch.start1--;
                patch.length1 = 1;
            }
            else if (matcher.group(2).charAt(0) == '0')
            {
                patch.length1 = 0;
            }
            else
            {
                patch.start1--;
                patch.length1 = Integer.parseInt(matcher.group(2));
            }

            patch.start2 = Integer.parseInt(matcher.group(3));
            if (matcher.group(4).length() == 0)
            {
                patch.start2--;
                patch.length2 = 1;
            }
            else if (matcher.group(4).charAt(0) == '0')
            {
                patch.length2 = 0;
            }
            else
            {
                patch.start2--;
                patch.length2 = Integer.parseInt(matcher.group(4));
            }
            lineCount++;

            while (lineCount < text.length)
            {
                if (text[lineCount].length() > 0)
                {
                    sign = text[lineCount].charAt(0);
                    line = text[lineCount].substring(1);
                    if (sign == '-')
                    {
                        // Deletion.
                        patch.diffs.add(new Difference(EditType.DELETE, line));
                    }
                    else if (sign == '+')
                    {
                        // Insertion.
                        patch.diffs.add(new Difference(EditType.INSERT, line));
                    }
                    else if (sign == ' ')
                    {
                        // Minor equality.
                        patch.diffs.add(new Difference(EditType.EQUAL, line));
                    }
                    else if (sign == '@')
                    {
                        // Start of next patch.
                        break;
                    }
                    else
                    {
                        // What!!!
                        throw new RuntimeException("Invalid patch mode: '"+sign+"'\n"+line); //$NON-NLS-1$ //$NON-NLS-2$
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
        public PatchResults(String text, List results)
        {
            this.text = text;
            this.results = results;
        }

        /**
         * @return the results
         */
        public List getResults()
        {
            return results;
        }

        /**
         * @return the text
         */
        public String getText()
        {
            return text;
        }

        private String text;
        private List results;
    }
    /**
     * Chunk size for context length.
     */
    public static final int MARGIN = 4;

    private List diffs;
    private int start1;
    private int start2;
    private int length1;
    private int length2;
    
    private static String patchRE = "^@@ -(\\d+),?(\\d*) \\+(\\d+),?(\\d*) @@$"; //$NON-NLS-1$
    private static Pattern patchPattern = Pattern.compile(patchRE);

}
