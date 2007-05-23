package org.crosswire.common.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatchEntry
{
    //  Constructor for a patch object.
    public PatchEntry()
    {
        this.diffs = new ArrayList();
        this.leftStart = 0;
        this.rightStart = 0;
        this.leftLength = 0;
        this.rightLength = 0;
    }

    //  Constructor for a patch object.
    public PatchEntry(String patchText)
    {
        this();
        fromText(patchText);
    }

    /**
     * @return the leftStart
     */
    public int getLeftStart()
    {
        return leftStart;
    }

    /**
     * @param leftStart the leftStart to set
     */
    public void setLeftStart(int start1)
    {
        this.leftStart = start1;
    }

    /**
     * @param adjustment the adjustment to leftStart
     */
    public void adjustLeftStart(int adjustment)
    {
        this.leftStart += adjustment;
    }

    /**
     * @return the rightStart
     */
    public int getRightStart()
    {
        return rightStart;
    }

    /**
     * @param rightStart the rightStart to set
     */
    public void setRightStart(int start2)
    {
        this.rightStart = start2;
    }

    /**
     * @param adjustment the adjustment to rightStart
     */
    public void adjustRightStart(int adjustment)
    {
        this.rightStart += adjustment;
    }

    /**
     * @return the leftLength
     */
    public int getLeftLength()
    {
        return leftLength;
    }

    /**
     * @param leftLength the leftLength to set
     */
    public void setLeftLength(int length1)
    {
        this.leftLength = length1;
    }

    /**
     * @param adjustment the adjustment to leftLength
     */
    public void adjustLength1(int adjustment)
    {
        this.leftLength += adjustment;
    }

    /**
     * @return the rightLength
     */
    public int getRightLength()
    {
        return rightLength;
    }

    /**
     * @param rightLength the rightLength to set
     */
    public void setRightLength(int length2)
    {
        this.rightLength = length2;
    }

    /**
     * @param adjustment the adjustment to rightLength
     */
    public void adjustLength2(int adjustment)
    {
        this.rightLength += adjustment;
    }

    //  Emmulate GNU diff's format.
    //  Header: @@ -382,8 +481,9 @@
    //  Indicies are printed as 1-based, not 0-based.
    public String toString()
    {
        StringBuffer txt = new StringBuffer();
        txt.append("@@ -"); //$NON-NLS-1$
        txt.append(getCoordinates(leftStart, leftLength));
        txt.append(" +"); //$NON-NLS-1$
        txt.append(getCoordinates(rightStart, rightLength));
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
    /**
     * Parse a textual representation of a patch entry and populate this patch entry.
     * @param input Text representation of this patch entry
     * @return this patch entry
     */
    public PatchEntry fromText(String input)
    {
        diffs.clear();
        String[] text = newlinePattern.split(input);
        char sign = '\0';
        String line = ""; //$NON-NLS-1$

        Matcher matcher = patchPattern.matcher(text[0]);
        matcher.matches();
        assert matcher.groupCount() == 4 : "Invalid patch string:\n" + text[0]; //$NON-NLS-1$
        // m = text[0].match(/^@@ -(\d+),?(\d*) \+(\d+),?(\d*) @@$/);

        leftStart = Integer.parseInt(matcher.group(1));

        if (matcher.group(2).length() == 0)
        {
            leftStart--;
            leftLength = 1;
        }
        else if (matcher.group(2).charAt(0) == '0')
        {
            setLeftLength(0);
        }
        else
        {
            leftStart--;
            leftLength =Integer.parseInt(matcher.group(2));
        }

        rightStart = Integer.parseInt(matcher.group(3));
        if (matcher.group(4).length() == 0)
        {
            rightStart--;
            rightLength = 1;
        }
        else if (matcher.group(4).charAt(0) == '0')
        {
            rightLength = 0;
        }
        else
        {
            rightStart--;
            rightLength = Integer.parseInt(matcher.group(4));
        }

        for (int lineCount = 1; lineCount < text.length; lineCount++)
        {
            if (text[lineCount].length() > 0)
            {
                sign = text[lineCount].charAt(0);
                line = text[lineCount].substring(1);
                diffs.add(new Difference(EditType.fromSymbol(sign), line));
            }
        }
        return this;
    }

    //  Compute and return the source text (all equalities and deletions).
    public String getLeftText()
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
    public String getRightText()
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

    public void addContext(String text)
    {
        int maxPatternLength = new Match().maxPatternLength();
        int padding = 0;
        String pattern = text.substring(rightStart, rightStart + leftLength);
        int textLength = text.length();

        // Increase the context until we're unique
        // (but don't let the pattern expand beyond the maximum length our Locator can handle).
        int end = maxPatternLength - PatchEntry.margin - PatchEntry.margin;
        while (text.indexOf(pattern) != text.lastIndexOf(pattern) && pattern.length() < end)
        {
            padding += PatchEntry.margin;
            pattern = text.substring(Math.max(0, rightStart - padding), Math.min(textLength, rightStart + leftLength + padding));
        }

        // Add one chunk for good luck.
        padding += PatchEntry.margin;

        // Add the prefix.
        String prefix = text.substring(Math.max(0, rightStart - padding), rightStart);
        int prefixLength = prefix.length();
        if (prefixLength > 0)
        {
            diffs.add(0, new Difference(EditType.EQUAL, prefix));
        }

        // Add the suffix
        String suffix = text.substring(rightStart + leftLength, Math.min(textLength, rightStart + leftLength + padding));
        int suffixLength = suffix.length();
        if (suffixLength > 0)
        {
            diffs.add(new Difference(EditType.EQUAL, suffix));
        }

        // Roll back the start points.
        leftStart -= prefixLength;
        rightStart -= prefixLength;

        // Extend the lengths.
        leftLength += prefixLength + suffixLength;
        rightLength += prefixLength + suffixLength;
    }

    public void addDifference(Difference diff)
    {
        diffs.add(diff);
    }

    public int getDifferenceCount()
    {
        return diffs.size();
    }

    public boolean hasDifferences()
    {
        return diffs.size() != 0;
    }

    public Iterator iterator()
    {
        return diffs.iterator();
    }

    public Difference getFirstDifference()
    {
        if (diffs.size() == 0)
        {
            return null;
        }
        return (Difference) diffs.get(0);
    }

    public Difference removeFirstDifference()
    {
        if (diffs.size() == 0)
        {
            return null;
        }
        return (Difference) diffs.remove(0);
    }

    public Difference getLastDifference()
    {
        if (diffs.size() == 0)
        {
            return null;
        }
        return (Difference) diffs.get(diffs.size() - 1);
    }

    protected void setDifferences(List newDiffs)
    {
        diffs = newDiffs;
    }

    /**
     * @param newMargin the margin to set
     */
    public static void setMargin(int newMargin)
    {
        PatchEntry.margin = newMargin;
    }

    /**
     * @return the margin
     */
    public static int getMargin()
    {
        return margin;
    }

    private String getCoordinates(int start, int length)
    {
        StringBuffer buf = new StringBuffer();

        if (length == 0)
        {
            buf.append(start);
            buf.append(",0"); //$NON-NLS-1$
        }
        else if (length == 1)
        {
            buf.append(leftStart + 1);
        }
        else
        {
            buf.append(start + 1);
            buf.append(',');
            buf.append(length);
        }

        return buf.toString();
    }

    /**
     * Chunk size for context length.
     */
    private static final int MARGIN = 4;
    private static int margin = MARGIN;
    private static Pattern newlinePattern = Pattern.compile("\n"); //$NON-NLS-1$
    private static Pattern patchPattern = Pattern.compile("^@@ -(\\d+),?(\\d*) \\+(\\d+),?(\\d*) @@$"); //$NON-NLS-1$

    private List diffs;
    private int leftStart;
    private int rightStart;
    private int leftLength;
    private int rightLength;
}
