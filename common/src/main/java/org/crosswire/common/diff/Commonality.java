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

/**
 * A Commonality is shared text at the beginning, middle or end of two strings.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Commonality
{
    /**
     * This is a utility class, therefore the constructor is private.
     */
    private Commonality()
    {
    }

    /**
     * Find the length of a common prefix.
     * @param source First string
     * @param target Second string
     * @return The number of characters common to the start of each string.
     */
    public static int prefix(final String source, final String target)
    {
        int pointermin = 0;
        int pointermax = Math.min(source.length(), target.length());
        int pointermid = pointermax;
        while (pointermin < pointermid)
        {
            if (source.regionMatches(0, target, 0, pointermid))
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
     * Find the length of a common suffix.
     * @param source First string
     * @param target Second string
     * @return The number of characters common to the end of each string.
     */
    public static int suffix(final String source, final String target)
    {
        int pointermin = 0;
        int pointermax = Math.min(source.length(), target.length());
        int pointermid = pointermax;
        while (pointermin < pointermid)
        {
            if (source.regionMatches(source.length() - pointermid, target, target.length() - pointermid, pointermid))
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
     * @param source Baseline string
     * @param target Changed string
     * @return a CommonMiddle  Or null if there was no match.
     */
    public static CommonMiddle halfMatch(final String source, final String target)
    {
        int sourceLength = source.length();
        int targetLength = target.length();
        String longText = sourceLength > targetLength ? source : target;
        String shortText = sourceLength > targetLength ? target : source;
        int longTextLength = Math.max(sourceLength, targetLength);
        if (longTextLength < 10 || shortText.length() < 1)
        {
            return null; // Pointless.
        }

        // First check if the second quarter is the seed for a half-match.
        CommonMiddle hm1 = halfMatch(longText, shortText, (int) Math.ceil(longTextLength / 4));
        // Check again based on the third quarter.
        CommonMiddle hm2 = halfMatch(longText, shortText, (int) Math.ceil(longTextLength / 2));
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
        {
            // Both matched.  Select the longest.
            hm = hm1.getCommonality().length() > hm2.getCommonality().length() ? hm1 : hm2;
        }

        // A half-match was found, sort out the return data.
        if (sourceLength > targetLength)
        {
            return hm;
        }

        return new CommonMiddle(hm.getTargetPrefix(), hm.getTargetSuffix(), hm.getSourcePrefix(), hm.getSourceSuffix(), hm.getCommonality());
    }

    /**
     * Does a substring of shortText exist within longText such that the substring
     * is at least half the length of longText?
     * @param longText Longer string
     * @param shortText Shorter string
     * @param startIndex Start index of quarter length substring within longText
     * @return Five element String array, containing the prefix of longText, the
     *     suffix of longText, the prefix of shortText, the suffix of shortText
     *     and the common middle.  Or null if there was no match.
     */
    private static CommonMiddle halfMatch(final String longText, final String shortText, final int startIndex)
    {
        // Start with a 1/4 length substring at position i as a seed.
        String seed = longText.substring(startIndex, startIndex + (longText.length() / 4));
        int j = -1;
        String common = ""; //$NON-NLS-1$
        String longTextPrefix = ""; //$NON-NLS-1$
        String longTextSuffix = ""; //$NON-NLS-1$
        String shortTextPrefix = ""; //$NON-NLS-1$
        String shortTextSuffix = ""; //$NON-NLS-1$
        while ((j = shortText.indexOf(seed, j + 1)) != -1)
        {
            int prefixLength = Commonality.prefix(longText.substring(startIndex), shortText.substring(j));
            int suffixLength = Commonality.suffix(longText.substring(0, startIndex), shortText.substring(0, j));
            if (common.length() < (prefixLength + suffixLength))
            {
                common = shortText.substring(j - suffixLength, j) + shortText.substring(j, j + prefixLength);
                longTextPrefix = longText.substring(0, startIndex - suffixLength);
                longTextSuffix = longText.substring(startIndex + prefixLength);
                shortTextPrefix = shortText.substring(0, j - suffixLength);
                shortTextSuffix = shortText.substring(j + prefixLength);
            }
        }

        if (common.length() >= longText.length() / 2)
        {
            return new CommonMiddle(longTextPrefix, longTextSuffix, shortTextPrefix, shortTextSuffix, common);
        }

        return null;
    }

}
