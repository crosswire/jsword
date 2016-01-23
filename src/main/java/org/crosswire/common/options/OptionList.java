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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.common.options;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An OptionList contains an ordered set of Options. The primary ability of an
 * OptionList is to find the matches for an Option.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class OptionList {
    public OptionList() {
        longOptions = new LinkedHashMap<String, Option>();
        shortOptions = new LinkedHashMap<String, Option>();
    }

    /**
     * Adds an Option to the end of this OptionList. It is an error with
     * "undefined" behavior for an Option's short or long name to already be
     * known.
     * 
     * @param option the option to append
     */
    public void add(Option option) {
        char shortName = option.getShortName();
        String longName = option.getLongName();
        if (shortName != '\u0000') {
            String optionName = Character.toString(shortName);
            assert !shortOptions.containsKey(optionName) : optionName + " already present";
            shortOptions.put(optionName, option);
        }

        if (longName != null) {
            assert !longOptions.containsKey(longName) : longName + " already present";
            longOptions.put(longName, option);
        }
    }

    /**
     * Get a list of Options that match the Option's long name. Return all
     * Options where the key is a prefix of its long name. If there is an exact
     * match then it is at the head of the list. It is up to the program to
     * decide how to handle ambiguity.
     * 
     * @param key
     *            the input to match
     * @return a list of all matches, or an empty list
     */
    public List<Option> getLongOptions(String key) {
        List<Option> matches = new ArrayList<Option>();
        if (longOptions.containsKey(key)) {
            matches.add(longOptions.get(key));
        }

        for (Map.Entry<String, Option> entry : longOptions.entrySet()) {
            String entryKey = entry.getKey();
            Option entryValue = entry.getValue();
            if (entryKey.startsWith(key) && !matches.contains(entryValue)) {
                matches.add(entryValue);
            }
        }

        return matches;
    }

    /**
     * Get the Option that matches the key on the Option's short name.
     * 
     * @param key
     *            the input to match
     * @return the matching Option, null otherwise.
     */
    public Option getShortOption(char key) {
        String optionName = Character.toString(key);

        Option match = null;
        if (shortOptions.containsKey(optionName)) {
            match = shortOptions.get(optionName);
        }

        return match;
    }

    /**
     * Get a list of Options that match the Option's short or long name.
     * Obviously, if the key is longer than a single character it won't match a
     * short name. Return all Options where the key is a prefix of its long
     * name. If there is an exact match then it is at the head of the list. It
     * is up to the program to decide how to handle ambiguity.
     * 
     * @param key
     *            the input to match
     * @return a list of all matches, or an empty list
     */
    public List<Option> getOptions(String key) {
        List<Option> matches = new ArrayList<Option>();
        if (key.length() == 1) {
            Option match = getShortOption(key.charAt(0));
            if (match != null) {
                matches.add(match);
            }
        }

        for (Option match : getLongOptions(key)) {
            if (!matches.contains(match)) {
                matches.add(match);
            }
        }

        return matches;
    }

    private Map<String, Option> shortOptions;
    private Map<String, Option> longOptions;
}
