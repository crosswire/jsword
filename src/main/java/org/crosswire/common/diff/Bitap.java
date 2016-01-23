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

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of the Bitap algorithm for finding a "fuzzy" location of a
 * match.
 * 
 * Based on the LGPL Diff_Match_Patch v1.5 javascript of Neil Fraser, Copyright (C) 2006<br>
 * <a href="http://neil.fraser.name/software/diff_match_patch/">http://neil.fraser.name/software/diff_match_patch/</a>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class Bitap implements Locator {
    /**
     * Locate the best instance of 'pattern' in 'text' near 'loc'.
     * 
     * @param text
     *            The text to search
     * @param pattern
     *            The pattern to search for
     * @param loc
     *            The location to search around
     */
    public Bitap(String text, String pattern, int loc) {
        this.text = text;
        this.pattern = pattern;
        this.loc = loc;
        alphabet = new HashMap<Character, Integer>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.diff.Locator#maxPatternLength()
     */
    public int maxPatternLength() {
        return MAXBITS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.diff.Locator#locate()
     */
    public int locate() {
        // Initialize the alphabet.
        alphabet();

        // Coerce the text length between reasonable maximums and minimums.
        scoreTextLength = Math.max(text.length(), Bitap.minLength);
        scoreTextLength = Math.min(scoreTextLength, Bitap.maxLength);

        // Highest score beyond which we give up.
        double scoreThreshold = Bitap.threshold;

        // Is there a nearby exact match? (speedup)
        int bestLoc = text.indexOf(pattern, loc);
        if (bestLoc != -1) {
            scoreThreshold = Math.min(bitapScore(0, bestLoc), scoreThreshold);
        }

        // What about in the other direction? (speedup)
        bestLoc = text.lastIndexOf(pattern, loc + pattern.length());
        if (bestLoc != -1) {
            scoreThreshold = Math.min(bitapScore(0, bestLoc), scoreThreshold);
        }

        // Initialize the bit arrays.
        int matchmask = (int) Math.pow(2, pattern.length() - 1);

        bestLoc = -1;

        int binMin;
        int binMid;
        int binMax = Math.max(loc + loc, text.length());
        int[] lastrd = new int[0];
        for (int d = 0; d < pattern.length(); d++) {
            // Scan for the best match; each iteration allows for one more
            // error.
            int[] rd = new int[text.length()];

            // Run a binary search to determine how far from 'loc' we can stray
            // at this error level.
            binMin = loc;
            binMid = binMax;
            while (binMin < binMid) {
                if (bitapScore(d, binMid) < scoreThreshold) {
                    binMin = binMid;
                } else {
                    binMax = binMid;
                }
                binMid = (binMax - binMin) / 2 + binMin;
            }

            binMax = binMid; // Use the result from this iteration as the
            // maximum for the next.
            int start = Math.max(0, loc - (binMid - loc) - 1);
            int finish = Math.min(text.length() - 1, pattern.length() + binMid);

            if (text.charAt(finish) == pattern.charAt(pattern.length() - 1)) {
                rd[finish] = (int) Math.pow(2, d + 1) - 1;
            } else {
                rd[finish] = (int) Math.pow(2, d) - 1;
            }

            for (int j = finish - 1; j >= start; j--) {
                Character curChar = Character.valueOf(text.charAt(j));
                int mask = alphabet.containsKey(curChar) ? alphabet.get(curChar).intValue() : 0;
                if (d == 0) { // First pass: exact match.
                    rd[j] = ((rd[j + 1] << 1) | 1) & mask;
                } else { // Subsequent passes: fuzzy match.
                    rd[j] = ((rd[j + 1] << 1) | 1) & mask | ((lastrd[j + 1] << 1) | 1) | ((lastrd[j] << 1) | 1) | lastrd[j + 1];
                }

                if ((rd[j] & matchmask) != 0) {
                    double score = bitapScore(d, j);
                    // This match will almost certainly be better than any
                    // existing match. But check anyway.
                    if (score <= scoreThreshold) {
                        // Told you so.
                        scoreThreshold = score;
                        bestLoc = j;
                        if (j > loc) {
                            // When passing loc, don't exceed our current
                            // distance from loc.
                            start = Math.max(0, loc - (j - loc));
                        } else {
                            // Already passed loc, downhill from here on in.
                            break;
                        }
                    }
                }
            }

            if (bitapScore(d + 1, loc) > scoreThreshold) // No hope for a
            // (better) match at
            // greater error
            // levels.
            {
                // No hope for a (better) match at greater error levels.
                break;
            }

            lastrd = rd;
        }

        return bestLoc;
    }

    protected Map<Character, Integer> getAlphabet() {
        return alphabet;
    }

    /**
     * Compute and return the score for a match with e errors and x location.
     * 
     * @param e
     *            Number of errors in match
     * @param x
     *            Location of match
     * @return Overall score for match
     */
    private double bitapScore(int e, int x) {
        // Compute and return the score for a match with e errors and x
        // location.
        int d = Math.abs(loc - x);
        return (e / (float) pattern.length() / Bitap.balance) + (d / (float) scoreTextLength / (1.0 - Bitap.balance));
    }

    // Initialize the alphabet for the Bitap algorithm.
    protected void alphabet() {
        int len = pattern.length();

        assert len <= Bitap.MAXBITS : "Pattern too long for this application.";

        for (int i = 0; i < len; i++) {
            Character c = Character.valueOf(pattern.charAt(i));
            Integer value = alphabet.get(c);
            int mask = value == null ? 0 : value.intValue();
            mask |= (int) Math.pow(2, len - i - 1);
            alphabet.put(c, Integer.valueOf(mask));
        }
    }

    public static void setBalance(float newBalance) {
        balance = newBalance;
    }

    public static void setThreshold(float newThreshold) {
        threshold = newThreshold;
    }

    public static void setMinLength(int newMinLength) {
        minLength = newMinLength;
    }

    public static void setMaxLength(int newMaxLength) {
        maxLength = newMaxLength;
    }

    /**
     * The maximum number of bits in an int. Change this to 64 if long is used
     * by alphabet.
     */
    private static final int MAXBITS = 32;

    /**
     * Tweak the relative importance (0.0 = accuracy, 1.0 = proximity)
     */
    private static final float BALANCE = 0.5f;
    private static float balance = BALANCE;

    /**
     * At what point is no match declared (0.0 = perfection, 1.0 = very loose)
     */
    private static final float THRESHOLD = 0.5f;
    private static float threshold = THRESHOLD;

    /**
     * The min and max cutoffs used when computing text lengths.
     */
    private static final int MINLENGTH = 100;
    private static int minLength = MINLENGTH;
    private static final int MAXLENGTH = 1000;
    private static int maxLength = MAXLENGTH;

    /**
     * The text to search.
     */
    private String text;

    /**
     * The pattern to find in the text.
     */
    private String pattern;

    /**
     * The location in text to focus the search.
     */
    private int loc;

    /**
     * The length of the string constrained between MINLENGHT and MAXLENGTH
     */
    private int scoreTextLength;

    /**
     * Alphabet is the compiled representation of the pattern.
     */
    private Map<Character, Integer> alphabet;
}
