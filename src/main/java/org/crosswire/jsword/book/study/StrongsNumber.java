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
package org.crosswire.jsword.book.study;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Strong's Number is either Greek or Hebrew, where the actual numbers for
 * each start at 1. This class can parse Strong's Numbers that begin with G, g,
 * H or h and are immediately followed by a number. That number can have leading
 * 0's. It can be followed by an OSISref extension of !a, !b, which is ignored.
 * 
 * <p>
 * The canonical representation of the number is a G or H followed by 4 digits,
 * with leading 0's as needed.
 * </p>
 * 
 * <p>
 * Numbers that exist:
 * </p>
 * <ul>
 * <li>Hebrew: 1-8674</li>
 * <li>Greek: 1-5624 (but not 1418, 2717, 3203-3302, 4452)</li>
 * </ul>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class StrongsNumber {
    /**
     * Build an immutable Strong's Number.
     * 
     * @param input
     *            a string that needs to be parsed.
     */
    public StrongsNumber(String input) {
        valid = parse(input);
    }

    /**
     * Build an immutable Strong's Number. 
     * 
     * @param language
     * @param strongsNumber
     */
    public StrongsNumber(char language, short strongsNumber) {
        this(language, strongsNumber, null);
    }

    /**
     * Build an immutable Strong's Number.
     * 
     * @param language
     * @param strongsNumber
     * @param part 
     */
    public StrongsNumber(char language, short strongsNumber, String part) {
        this.language = language;
        this.strongsNumber = strongsNumber;
        this.part = part;
        valid = isValid();
    }

    /**
     * Return the canonical form of a Strong's Number, without the part.
     * 
     * @return the strongsNumber
     */
    public String getStrongsNumber() {
        StringBuilder buf = new StringBuilder(5);
        buf.append(language);
        buf.append(ZERO_PAD.format(strongsNumber));
        return buf.toString();
    }

    /**
     * Return the canonical form of a Strong's Number, with the part, if any
     * 
     * @return the strongsNumber
     */
    public String getFullStrongsNumber() {
        StringBuilder buf = new StringBuilder(5);
        buf.append(language);
        buf.append(ZERO_PAD.format(strongsNumber));
        if (part != null) {
            buf.append(part);
        }
        return buf.toString();
    }

    /**
     * @return true if the Strong's number is for Greek
     */
    public boolean isGreek() {
        return language == 'G';
    }

    /**
     * @return true if the Strong's number is for Hebrew
     */
    public boolean isHebrew() {
        return language == 'H';
    }

    /**
     * @return true if this Strong's number is identified by a sub part
     */
    public boolean isPart() {
        return part != null;
    }

    /**
     * Validates the number portion of this StrongsNumber.
     * <ul>
     * <li>Hebrew Strong's numbers are in the range of: 1-8674.</li>
     * <li>Greek Strong's numbers in the range of: 1-5624
     * (but not 1418, 2717, 3203-3302, 4452).</li>
     * </ul>
     * 
     * @return true if the Strong's number is in range.
     */
    public boolean isValid() {
        if (!valid) {
            return false;
        }

        // Dig deeper.
        // The valid flag when set by parse indicates whether it had problems.
        if (language == 'H'
                && strongsNumber >= 1
                && strongsNumber <= 8674)
        {
            return true;
        }

        if (language == 'G'
                && ((strongsNumber >= 1 && strongsNumber < 1418)
                        || (strongsNumber > 1418 && strongsNumber < 2717)
                        || (strongsNumber > 2717 && strongsNumber < 3203)
                        || (strongsNumber > 3302 && strongsNumber <= 5624)))
        {
            return true;
        }

        valid = false;
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 31 + language;
        return 31 * result + strongsNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final StrongsNumber other = (StrongsNumber) obj;

        return language == other.language && strongsNumber == other.strongsNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getStrongsNumber();
    }

    /**
     * Do the actual parsing.
     * 
     * @param input
     * @return true when the input looks like a Strong's Number
     */
    private boolean parse(String input) {
        String text = input;
        language = 'U';
        strongsNumber = 9999;
        part = "";

        // Does it match
        Matcher m = STRONGS_PATTERN.matcher(text);
        if (!m.lookingAt()) {
            return false;
        }

        String lang = m.group(1);
        language = lang.charAt(0);
        switch (language) {
        case 'g':
            language = 'G';
            break;
        case 'h':
            language = 'H';
            break;
        default:
            // pass through
        }

        // Get the number after the G or H
        try {
            strongsNumber = Integer.parseInt(m.group(2));
        } catch (NumberFormatException e) {
            strongsNumber = 0; // An invalid Strong's Number
            return false;
        }

        // FYI: OSIS refers to what follows a ! as a grain
        part = m.group(3);
        return true;
    }

    /**
     * Whether it is Greek (G) or Hebrew (H).
     */
    private char language;

    /**
     * The Strong's Number.
     */
    private int strongsNumber;

    /**
     * The part if any.
     */
    private String part;

    /*
     * The value is valid.
     */
    private boolean valid;

    /**
     * The pattern of an acceptable Strong's number.
     */
    private static final Pattern STRONGS_PATTERN = Pattern.compile("([GgHh])([0-9]*)!?([A-Za-z]+)?");
    private static final DecimalFormat ZERO_PAD = new DecimalFormat("0000");
}
