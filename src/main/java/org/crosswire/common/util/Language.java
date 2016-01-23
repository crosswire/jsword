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
package org.crosswire.common.util;

import java.util.Locale;

/**
 * An immutable Language by specification. The specifier consists of up to three parts:
 * <ul>
 * <li>LL - An iso639-2 or iso639-3 language code</li>
 * <li>SSSS - A 4-letter iso15924 script code</li>
 * <li>CC - A 2-letter iso3166 country code</li>
 * </ul>
 * Note: This is a subset of the BCP-47 standard.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class Language implements Comparable<Language> {
    /**
     * The default language code is en for English.
     */
    public static final String DEFAULT_LANG_CODE = "en";

    /**
     * The language code for invalid language specifications is und for Undetermined.
     */
    public static final String UNKNOWN_LANG_CODE = "und";

    /**
     * The default language is English.
     */
    public static final Language DEFAULT_LANG = new Language(DEFAULT_LANG_CODE);


    /**
     * A single language defined by an ISO-639 code. If the code is null or
     * empty then it is considered to be DEFAULT_LANG (that is, English).
     * 
     * @param specification
     *            the specifier for the particular language
     */
    public Language(String specification) {
        given = specification;
        parse(given);
    }

    /**
     * The specification that was given might not be be the one that
     * ultimately gets the name.
     * 
     * @return the specification that was originally given.
     */
    public String getGivenSpecification() {
        return given;
    }

    /**
     * The specification that was given might not be be the one that
     * ultimately gets the name.
     * 
     * @return the specification that was used to find the name.
     */
    public String getFoundSpecification() {
        getName();
        return found;
    }

    /**
     * Determine whether this language is valid.
     * <ul>
     * <li>LL - An iso639-2 or iso639-3 language code</li>
     * <li>SSSS - A 4-letter iso15924 script code</li>
     * <li>CC - A 2-letter iso3166 country code</li>
     * </ul>
     * 
     * @return true if the language is valid.
     */
    public boolean isValidLanguage() {
        getName();
        return valid;
    }

    /**
     * Get the iso639 language code.
     * 
     * @return the code for the language in lower case.
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the iso15924 script for the language. May be null.
     * 
     * @return the code for the script in Title case.
     */
    public String getScript() {
        return script;
    }

    /**
     * Get the iso3166 script for the language. May be null.
     * 
     * @return the code for the country in UPPER case.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Get the localized language name.
     * 
     * @return the name of the language
     */
    public String getName() {
        // Note: This is not quite thread safe. Unless name is volatile.
        // But it will just do the work multiple times.
        if (name == null) {
            boolean more = true;
            // Code is the ultimate fallback
            String result = code;
            String lookup = code;

            StringBuilder sb = new StringBuilder();
            // The lookup is as follows.
            // There is always a code
            // If all parts are specified then use that
            if (script != null && country != null) {
                sb.append(code);
                sb.append('-');
                sb.append(script);
                sb.append('-');
                sb.append(country);
                lookup = sb.toString();
                result = Languages.getName(lookup);
                more = lookup.equals(result);
            }

            // If script is specified it has precedence over country
            if (more && script != null) {
                sb.setLength(0);
                sb.append(code);
                sb.append('-');
                sb.append(script);
                lookup = sb.toString();
                result = Languages.getName(lookup);
                more = lookup.equals(result);
            }

            // If country was specified, check for that now.
            if (more && country != null) {
                sb.setLength(0);
                sb.append(code);
                sb.append('-');
                sb.append(country);
                lookup = sb.toString();
                result = Languages.getName(lookup);
                more = lookup.equals(result);
            }

            // Now check just the code.
            if (more) {
                lookup = code;
                result = Languages.getName(lookup);
                more = lookup.equals(result);
            }

            // Oops, the localized lookup failed.
            // See if Java has one.
            if (more) {
                lookup = code;
                result = new Locale(lookup).getDisplayLanguage();
                more = lookup.equals(result);
            }

            // Oops, Java doesn't have a clue
            // Look into our heavy handed listing
            if (more) {
                lookup = code;
                result = Languages.AllLanguages.getName(lookup);
                more = lookup.equals(result);
            }

            // Oops, didn't find it anywhere. Mark it as invalid.
            if (more) {
                valid = false;
            }
            // now that we are here go with what we last used and got
            found = lookup;
            // Assign name last to help with synchronization issues
            name = result;
        }
        return name;
    }

    /**
     * Determine whether this language is a Left-to-Right or a Right-to-Left
     * language. If the language has a script, it is used for the determination.
     * Otherwise, check the language.
     * <p>
     * Note: This is problematic. Languages do not have direction.
     * Scripts do. Further, there are over 7000 living languages, many of which
     * are written in Right-to-Left scripts and are not listed here.
     * </p>
     * 
     * @return true if the language is Left-to-Right.
     */
    public boolean isLeftToRight() {
        if (!knowsDirection) {
            ltor = !Languages.RtoL.isRtoL(script, code);
            knowsDirection = true;
        }
        return ltor;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (found == null) {
            getName();
        }
        return found.hashCode();
    }

    /* (non-Javadoc)
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

        final Language other = (Language) obj;

        return code.equals(other.code)  && compareStrings(script, other.script) && compareStrings(country, other.country);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Language o) {
        return getName().compareTo(o.getName());
    }

    /**
     * Split the specification on '-' into 1 to 3 parts.
     * 
     * @param spec the specification to parse
     */
    private void parse(String spec) {
        String specification = spec;
        if (specification == null) {
            specification = DEFAULT_LANG_CODE;
        }

        int len = specification.length();

        // It used to be that SWORD modules used x- and X- as a language prefix
        // for minority languages. Now that we have a complete iso639 spec,
        // SWORD does not use it.
        if (len < 2 || specification.charAt(0) == '-' || specification.charAt(1) == '-') {
            valid = false;
            code = UNKNOWN_LANG_CODE;
            return;
        }

        // Obvious optimization of the most common case: only the language code is given
        if (len <= 3) {
            code = CanonicalUtils.getLanguage(specification, 0, len);
        }

        int partLen = 0;
        int start = 0;
        int split;
        for (split = 2; split < len; ++split) {
            char c = specification.charAt(split);
            if (c == '-') {
                break;
            }
        }
        code = CanonicalUtils.getLanguage(specification, start, split);
        partLen = split - start;
        valid = partLen == 2 || partLen == 3;
        start = split + 1;

        // Get the second part. It is either a script or a country code
        if (split < len) {
            for (split = start; split < len; ++split) {
                char c = specification.charAt(split);
                if (c == '-') {
                    break;
                }
            }
            partLen = split - start;
            if (partLen == 4) {
                script = CanonicalUtils.getScript(specification, start, split);
            } else if (partLen == 2) {
                country = CanonicalUtils.getCountry(specification, start, split);
            } else {
                valid = false;
            }
            start = split + 1;
        }

        // Get the third part, if any. It can only be a country code.
        if (country == null && split < len) {
            for (split = start; split < len; ++split) {
                char c = specification.charAt(split);
                if (c == '-') {
                    break;
                }
            }
            partLen = split - start;
            if (partLen == 2) {
                country = CanonicalUtils.getCountry(specification, start, split);
            } else {
                valid = false;
            }
            start = split + 1;
        }

        if (start <= len) {
            valid = false;
        }
    }

    /**
     * Equal if both a and b are the same.
     * 
     * @param a a string to compare
     * @param b a string to compare
     * @return true if both are the same.
     */
    private boolean compareStrings(String a, String b) {
        return (a == null && b == null) || (a != null && a.equals(b));
    }

    /**
     * Converts substrings to the canonical representation for language code, script and country.
     */
    private static final class CanonicalUtils {
        /**
         * Utility class. Private constructor.
         */
        private CanonicalUtils() {
        }

        /**
         * The iso639 language code's canonical form is lower case.
         * 
         * @param specification
         *            the bcp47 specification of the language
         * @param start
         *            the start of the code
         * @param end
         *            the position of the character following the code
         * @return the canonical representation for the code
         */
        public static String getLanguage(String specification, int start, int end) {

            // An empty string means no work
            if (start == end) {
                return null;
            }

            // Avoid construction by analyzing the string
            // to see if it is already LanguageCase.
            // Find the first character that is not LanguageCase
            int first;
            for (first = start; first < end && isLowerASCII(specification.charAt(first)); ++first) {
                continue; // keep going
            }

            // If we get to the end of the string then it is CountryCase
            if (first == end) {
                return specification.substring(start, end);
            }

            // Bummer, we need to do work
            int len = end - start;
            char[] buf = new char[len];
            int i = 0;
            for (int j = start; j < end; ++j) {
                buf[i++] = j < first ? specification.charAt(j) : toLowerASCII(specification.charAt(j));
            }
            return new String(buf);
        }

        /**
         * The iso3166 country code's canonical form is upper case.
         * 
         * @param specification
         *            the bcp47 specification of the language
         * @param start
         *            the start of the code
         * @param end
         *            the position of the character following the code
         * @return the canonical representation for the code
         */
        public static String getCountry(String specification, int start, int end) {

            // An empty string means no work
            if (start == end) {
                return null;
            }

            // Avoid construction by analyzing the string
            // to see if it is already CountryCase.
            // Find the first character that is not CountryCase
            int first;
            for (first = start; first < end && isUpperASCII(specification.charAt(first)); ++first) {
                continue; // keep going
            }

            // If we get to the end of the string then it is CountryCase
            if (first == end) {
                return specification.substring(start, end);
            }

            // Bummer, we need to do work
            int len = end - start;
            char[] buf = new char[len];
            int i = 0;
            for (int j = start; j < end; ++j) {
                buf[i++] = j < first ? specification.charAt(j) : toUpperASCII(specification.charAt(j));
            }
            return new String(buf);
        }

        /**
         * The iso15924 script code's canonical form is title case.
         * 
         * @param specification
         *            the bcp47 specification of the language
         * @param start
         *            the start of the code
         * @param end
         *            the position of the character following the code
         * @return the canonical representation for the code
         */
        public static String getScript(String specification, int start, int end) {

            // An empty string means no work
            if (start == end) {
                return null;
            }

            // Avoid construction by analyzing the string
            // to see if it is already ScriptCase.
            // Find the first character that is not ScriptCase
            int first = start;
            if (isUpperASCII(specification.charAt(start))) {
                for (first = start + 1; first < end && isLowerASCII(specification.charAt(first)); ++first) {
                    continue; // keep going
                }

                // If we get to the end of the string then it is ScriptCase
                if (first == end) {
                    return specification.substring(start, end);
                }
            }

            // Bummer, we need to do work.
            int len = end - start;
            char[] buf = new char[len];
            buf[0] = first == start ? toUpperASCII(specification.charAt(first)) : specification.charAt(first);
            int i = 1;
            for (int j = start + 1; j < end; ++j) {
                buf[i++] = j < first ? specification.charAt(j) : toLowerASCII(specification.charAt(j));
            }
            return new String(buf);
        }

        /**
         * Determine whether the character is one of A-Z.
         * 
         * @param c the character to examine
         * @return true if it is in A-Z
         */
        private static boolean isUpperASCII(char c) {
            return c >= 'A' && c <= 'Z';
        }

        /**
         * Determine whether the character is one of a-z.
         * 
         * @param c the character to examine
         * @return true if it is in a-z
         */
        private static boolean isLowerASCII(char c) {
            return c >= 'a' && c <= 'z';
        }

        /**
         * Convert a character, in in a-z to its upper case value, otherwise leave it alone.
         * 
         * @param c the character to convert, if in a-z
         * @return the upper case ASCII representation of the character or the character itself.
         */
        private static char toUpperASCII(char c) {
            return isLowerASCII(c) ? (char) (c - 32) : c;
        }

        /**
         * Convert a character, in in A-Z to its lower case value, otherwise leave it alone.
         * 
         * @param c the character to convert, if in A-Z
         * @return the lower case ASCII representation of the character or the character itself.
         */
        private static char toLowerASCII(char c) {
            return isUpperASCII(c) ? (char) (c + 32) : c;
        }
    }

    /**
     * The original specification provided by the user.
     */
    private String given;
    /**
     * The effective specification.
     */
    private String found;
    /**
     * The lower case iso639 language code. 
     */
    private String code;
    /**
     * The Title case iso15924 script code.
     */
    private String script;
    /**
     * The UPPER case iso3166 country code. 
     */
    private String country;
    /**
     * The name as defined by Languages. 
     */
    private String name;
    /**
     * Flag to store whether the code is valid.
     */
    private boolean valid;
    private boolean knowsDirection;
    private boolean ltor;
}
