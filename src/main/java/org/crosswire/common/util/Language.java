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
 * Copyright: 2007-2013
 *     The copyright to this program is held by it's authors.
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
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
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
        parse(specification);
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
            // The ultimate fall back is that we use the code for the name
            name = code;

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
                found = sb.toString();
                name = Languages.getName(found);
            }

            // If script is specified it has precedence over country
            if (code.equals(name) && script != null) {
                sb.setLength(0);
                sb.append(code);
                sb.append('-');
                sb.append(script);
                found = sb.toString();
                name = Languages.getName(found);
            }

            // If country was specified, check for that now.
            if (code.equals(name) && country != null) {
                sb.setLength(0);
                sb.append(code);
                sb.append('-');
                sb.append(country);
                found = sb.toString();
                name = Languages.getName(found);
            }

            // Now check just the code.
            if (code.equals(name)) {
                found = code;
                name = Languages.getName(code);                
            }

            // Oops, the localized lookup failed.
            // See if Java has one.
            if (code.equals(name)) {
                name = new Locale(code).getDisplayLanguage();
            }

            // Oops, Java doesn't have a clue
            // Look into our heavy handed listing
            if (code.equals(name)) {
                name = Languages.AllLanguages.getName(code);
            }
        }
        return name;
    }

    /**
     * The specification that was given might not be be the one that
     * ultimately gets the name.
     * 
     * @return the specification that was used to find the name.
     */
    public String getFoundSpecification()
    {
        getName();
        return found;
    }

    /**
     * Determine whether this language is a Left-to-Right or a Right-to-Left
     * language. Note: This is problematic. Languages do not have direction.
     * Scripts do. Further, there are over 7000 living languages, many of which
     * are written in Right-to-Left scripts and are not listed here.
     * 
     * @return true if the language is Left-to-Right.
     */
    public boolean isLeftToRight() {
        if (!knowsDirection) {
            // TODO(DMS): Improve this.
            ltor = !("he".equals(code)  || // Hebrew
                     "ar".equals(code)  || // Arabic
                     "fa".equals(code)  || // Farsi/Persian
                     "ur".equals(code)  || // Uighur
                     "uig".equals(code) || // Uighur, too
                     "syr".equals(code) || // Syriac
                     "iw".equals(code));   // Java's notion of Hebrew

            knowsDirection = true;
        }

        return ltor;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return code.hashCode();
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

        return code.equals(other.code);
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
        return getName().compareTo(o.toString());
    }

    private static boolean isUpperASCII(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static boolean isLowerASCII(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static char toUpperASCII(char c) {
        return isLowerASCII(c) ? (char) (c - 32) : c;
    }

    private static char toLowerASCII(char c) {
        return isUpperASCII(c) ? (char) (c + 32) : c;
    }

    /**
     * The iso639 language code's canonical form is lower case.
     * 
     * @param specification the bcp47 specification of the language
     * @param start the start of the code
     * @param end the position of the character following the code
     * @return the canonical representation for the code
     */
    private static String toCanonicalLanguage(String specification, int start, int end) {

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
     * @param specification the bcp47 specification of the language
     * @param start the start of the code
     * @param end the position of the character following the code
     * @return the canonical representation for the code
     */
    private static String toCanonicalCountry(String specification, int start, int end) {

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
     * @param specification the bcp47 specification of the language
     * @param start the start of the code
     * @param end the position of the character following the code
     * @return the canonical representation for the code
     */
    private static String toCanonicalScript(String specification, int start, int end) {

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
     * Split the specification on '-' into 1 to 3 parts.
     * @param spec
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

        int partLen = 0;
        int start = 0;
        int split;
        for (split = 2; split < len; ++split) {
            char c = specification.charAt(split);
            if (c == '-') {
                break;
            }
        }
        code = Language.toCanonicalLanguage(specification, start, split);
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
                script = Language.toCanonicalScript(specification, start, split);
            } else if (partLen == 2) {
                country = Language.toCanonicalCountry(specification, start, split);
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
                country = Language.toCanonicalCountry(specification, start, split);
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
     * The original specification provided by the user.
     */
    //private String specification;
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
