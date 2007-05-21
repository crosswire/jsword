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

import java.util.HashMap;
import java.util.Map;

/**
 * Computes the difference between two texts to create a patch.
 * Applies the patch onto another text, allowing for errors.
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Match
{
    // Locate the best instance of 'pattern' in 'text' near 'loc'.
    public static int main(String text, String pattern, int loc)
    {
        if (text.length() == 0) {
            // Nothing to match.
            return -1;
        } 
        
        if (text.equals(pattern))
        {
            // Shortcut (potentially not guaranteed by the algorithm)
            return 0;
        }

        int newLoc = Math.max(0, Math.min(loc, text.length() - pattern.length()));
        if (!text.substring(newLoc, newLoc + pattern.length()).equals(pattern))
        {
            // Do a fuzzy compare.
            return Match.bitap(text, pattern, newLoc);
        }

        // else Perfect match at the perfect spot!  (Includes case of null pattern)
        return newLoc;
    }


    // Locate the best instance of 'pattern' in 'text' near 'loc' using the Bitap algorithm.
    private static int bitap(String text, String pattern, int loc)
    {
        // Initialise the alphabet.
        Map s = Match.alphabet(pattern);

        int score_text_length = text.length();
        // Coerce the text length between reasonable maximums and minimums.
        score_text_length = Math.max(score_text_length, Match.MINLENGTH);
        score_text_length = Math.min(score_text_length, Match.MAXLENGTH);

        // Highest score beyond which we give up.
        double score_threshold = Match.THRESHOLD;

        // Is there a nearby exact match? (speedup)
        int best_loc = text.indexOf(pattern, loc);
        if (best_loc != -1)
        {
            score_threshold = Math.min(Match.bitap_score(pattern, score_text_length, loc, 0, best_loc), score_threshold);
        }

        
       // What about in the other direction? (speedup)
        best_loc = text.lastIndexOf(pattern, loc + pattern.length());
        if (best_loc != -1)
        {
            score_threshold = Math.min(Match.bitap_score(pattern, score_text_length, loc, 0, best_loc), score_threshold);
        }

        // Initialise the bit arrays.
        int matchmask = (int) Math.pow(2, pattern.length() - 1);

        best_loc = -1;

        int bin_min;
        int bin_mid;
        int bin_max = Math.max(loc + loc, text.length());
        int[] last_rd = new int[0];
        for (int d = 0; d < pattern.length(); d++)
        {
            // Scan for the best match; each iteration allows for one more error.
            int[] rd = new int[text.length()];

            // Run a binary search to determine how far from 'loc' we can stray at this error level.
            bin_min = loc;
            bin_mid = bin_max;
            while (bin_min < bin_mid)
            {
                if (Match.bitap_score(pattern, score_text_length, loc, d, bin_mid) < score_threshold)
                {
                    bin_min = bin_mid;
                }
                else
                {
                    bin_max = bin_mid;
                }
                bin_mid = (int) Math.floor((bin_max - bin_min) / 2 + bin_min);
            }

            bin_max = bin_mid; // Use the result from this iteration as the maximum for the next.
            int start = Math.max(0, loc - (bin_mid - loc) - 1);
            int finish = Math.min(text.length() - 1, pattern.length() + bin_mid);

            if (text.charAt(finish) == pattern.charAt(pattern.length() - 1))
            {
                rd[finish] = (int) Math.pow(2, d + 1) - 1;
            }
            else
            {
                rd[finish] = (int) Math.pow(2, d) - 1;
            }

            for (int j = finish - 1; j >= start; j--)
            {
                int mask = ((Integer) s.get(new Character(text.charAt(j)))).intValue();
                if (d == 0) // First pass: exact match.
                {
                    rd[j] = ((rd[j + 1] << 1) | 1) & mask;
                }
                else // Subsequent passes: fuzzy match.
                {
                    rd[j] = ((rd[j + 1] << 1) | 1) & mask | ((last_rd[j + 1] << 1) | 1) | ((last_rd[j] << 1) | 1) | last_rd[j + 1];
                }

                if ((rd[j] & matchmask) != 0)
                {
                    double score = Match.bitap_score(pattern, score_text_length, loc, d, j);
                    // This match will almost certainly be better than any existing match.  But check anyway.
                    if (score <= score_threshold)
                    {
                        // Told you so.
                        score_threshold = score;
                        best_loc = j;
                        if (j > loc)
                        {
                            // When passing loc, don't exceed our current distance from loc.
                            start = Math.max(0, loc - (j - loc));
                        }
                        else
                        {
                            // Already passed loc, downhill from here on in.
                            break;
                        }
                    }
                }
            }

            if (Match.bitap_score(pattern, score_text_length, loc, d + 1, loc) > score_threshold) // No hope for a (better) match at greater error levels.
            {
                break;
            }

            last_rd = rd;
        }

        return best_loc;
    }

    private static double bitap_score(String pattern, int score_text_length, int loc, int e, int x)
    {
        // Compute and return the score for a match with e errors and x location.
        int d = Math.abs(loc - x);
        return (e / pattern.length() / Match.BALANCE) + (d / score_text_length / (1.0 - Match.BALANCE));
    }

    // Initialize the alphabet for the Bitap algorithm.
    private static Map alphabet(String pattern)
    {
        int len = pattern.length();

        assert len <= Match.MAXBITS;

        Map map = new HashMap();
        for (int i = 0; i < len; i++)
        {
            Character c = new Character(pattern.charAt(i));
            Integer value = (Integer) map.get(c);
            int mask = value == null ? 0 : value.intValue();
            mask |= (int) Math.pow(2, len - i - 1);
            map.put(c, new Integer(mask));
        }
        return map;
    }

    /**
     * The maximum number of bits in an int.
     * Change this to 64 if long is used by alphabet.
     */
    public static final int MAXBITS = 32;

    /**
     * Tweak the relative importance (0.0 = accuracy, 1.0 = proximity)
     */
    public static final double BALANCE = 0.5;

    /**
     * At what point is no match declared (0.0 = perfection, 1.0 = very loose)
     */
    public static final double THRESHOLD = 0.5;

    /**
     * The min and max cutoffs used when computing text lengths.
     */
    public static final int MINLENGTH = 100;
    public static final int MAXLENGTH = 1000;


}