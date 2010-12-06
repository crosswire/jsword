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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic class of String utils. It would be good if we could put this stuff
 * in java.lang ...
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class StringUtil {
    /**
     * Prevent instantiation
     */
    private StringUtil() {
    }

    /**
     * The newline character
     */
    public static final String NEWLINE = System.getProperty("line.separator", "\r\n");

    /**
     * This method reads an InputStream <b>In its entirety</b>, and passes The
     * text back as a string. If you are reading from a source that can block
     * then be prepared for a long wait for this to return.
     * 
     * @param in
     *            The Stream to read from.
     * @return A string containing all the text from the Stream.
     */
    public static String read(Reader in) throws IOException {
        StringBuilder retcode = new StringBuilder();
        BufferedReader din = new BufferedReader(in);

        while (true) {
            String line = din.readLine();

            if (line == null) {
                break;
            }

            retcode.append(line);
            retcode.append(NEWLINE);
        }

        return retcode.toString();
    }

    /**
     * This function creates a readable title from a variable name type input.
     * For example calling: StringUtil.createTitle("one_two") = "One Two"
     * StringUtil.createTitle("oneTwo") = "One Two"
     */
    public static String createTitle(String variable) {
        StringBuilder retcode = new StringBuilder();
        boolean lastlower = false;
        boolean lastspace = true;

        for (int i = 0; i < variable.length(); i++) {
            char c = variable.charAt(i);

            if (lastlower && Character.isUpperCase(c) && !lastspace) {
                retcode.append(' ');
            }

            lastlower = !Character.isUpperCase(c);

            if (lastspace) {
                c = Character.toUpperCase(c);
            }

            if (c == '_') {
                c = ' ';
            }

            if (!lastspace || c != ' ') {
                retcode.append(c);
            }

            lastspace = c == ' ';
        }

        return retcode.toString();
    }

    /**
     * For example getInitials("Java DataBase Connectivity") = "JDC" and
     * getInitials("Church of England") = "CoE".
     * 
     * @param sentence
     *            The phrase from which to get the initial letters.
     * @return The initial letters in the given words.
     */
    public static String getInitials(String sentence) {
        String[] words = StringUtil.split(sentence);

        StringBuilder retcode = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            char first = 0;
            for (int j = 0; first == 0 && j < word.length(); j++) {
                char c = word.charAt(j);
                if (Character.isLetter(c)) {
                    first = c;
                }
            }

            if (first != 0) {
                retcode.append(first);
            }
        }

        return retcode.toString();
    }

    /**
     * <p>
     * Splits the provided text into an array, using whitespace as the
     * separator. Whitespace is defined by {@link Character#isWhitespace(char)}.
     * </p>
     * 
     * <p>
     * The separator is not included in the returned String array. Adjacent
     * separators are treated as one separator.
     * </p>
     * 
     * <p>
     * A <code>null</code> input String returns <code>null</code>.
     * </p>
     * 
     * <pre>
     * StringUtils.split(null)       = null
     * StringUtils.split(&quot;&quot;)         = []
     * StringUtils.split(&quot;abc def&quot;)  = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtils.split(&quot;abc  def&quot;) = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtils.split(&quot; abc &quot;)    = [&quot;abc&quot;]
     * </pre>
     * 
     * @param str
     *            the String to parse, may be null
     * @return an array of parsed Strings, <code>null</code> if null String
     *         input
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * <p>
     * Splits the provided text into an array, separator specified. This is an
     * alternative to using StringTokenizer.
     * </p>
     * 
     * <p>
     * The separator is not included in the returned String array. Adjacent
     * separators are treated as one separator.
     * </p>
     * 
     * <p>
     * A <code>null</code> input String returns <code>null</code>.
     * </p>
     * 
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split(&quot;&quot;, *)           = []
     * StringUtils.split(&quot;a.b.c&quot;, '.')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtils.split(&quot;a..b.c&quot;, '.')   = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtils.split(&quot;a:b:c&quot;, '.')    = [&quot;a:b:c&quot;]
     * StringUtils.split(&quot;a\tb\nc&quot;, null) = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtils.split(&quot;a b c&quot;, ' ')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * </pre>
     * 
     * @param str
     *            the String to parse, may be null
     * @param separatorChar
     *            the character used as the delimiter, <code>null</code> splits
     *            on whitespace
     * @return an array of parsed Strings
     * @since 2.0
     */
    public static String[] split(String str, char separatorChar) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        List list = new ArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            i++;
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * <p>
     * Splits the provided text into an array, separator specified. This is an
     * alternative to using StringTokenizer.
     * </p>
     * 
     * <p>
     * The separator is not included in the returned String array. Adjacent
     * separators are treated individually.
     * </p>
     * 
     * <p>
     * A <code>null</code> input String returns <code>null</code>.
     * </p>
     * 
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split(&quot;&quot;, *)           = []
     * StringUtils.split(&quot;a.b.c&quot;, '.')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtils.split(&quot;a..b.c&quot;, '.')   = [&quot;a&quot;, &quot;&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtils.split(&quot;a:b:c&quot;, '.')    = [&quot;a:b:c&quot;]
     * StringUtils.split(&quot;a\tb\nc&quot;, null) = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtils.split(&quot;a b c&quot;, ' ')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * </pre>
     * 
     * @param str
     *            the String to parse, may be null
     * @param separatorChar
     *            the character used as the delimiter, <code>null</code> splits
     *            on whitespace
     * @return an array of parsed Strings
     * @since 2.0
     */
    public static String[] splitAll(String str, char separatorChar) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        List list = new ArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                list.add(str.substring(start, i));
                start = ++i;
                match = false;
                continue;
            }
            match = true;
            i++;
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * <p>
     * Splits the provided text into an array, separator specified. This is an
     * alternative to using StringTokenizer.
     * </p>
     * 
     * <p>
     * The separator is not included in the returned String array. Adjacent
     * separators are treated individually.
     * </p>
     * 
     * <p>
     * A <code>null</code> input String returns <code>null</code>.
     * </p>
     * 
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split(&quot;&quot;, *)           = []
     * StringUtils.split(&quot;a.b.c&quot;, '.')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtils.split(&quot;a..b.c&quot;, '.')   = [&quot;a&quot;, &quot;&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtils.split(&quot;a:b:c&quot;, '.')    = [&quot;a:b:c&quot;]
     * StringUtils.split(&quot;a b c&quot;, ' ')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * </pre>
     * 
     * @param str
     *            the String to parse, may be null
     * @param separatorChar
     *            the character used as the delimiter
     * @param max
     *            the maximum number of elements to include in the array. A zero
     *            or negative value implies no limit
     * @return an array of parsed Strings
     * @since 2.0
     */
    public static String[] splitAll(String str, char separatorChar, int max) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (sizePlus1++ == max) {
                    i = len;
                }
                list.add(str.substring(start, i));
                start = ++i;
                match = false;
                continue;
            }
            match = true;
            i++;
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * <p>
     * Splits the provided text into an array, separators specified. This is an
     * alternative to using StringTokenizer.
     * </p>
     * 
     * <p>
     * The separator is not included in the returned String array. Adjacent
     * separators are treated as one separator.
     * </p>
     * 
     * <p>
     * A <code>null</code> input String returns <code>null</code>. A
     * <code>null</code> separatorChars splits on whitespace.
     * </p>
     * 
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split(&quot;&quot;, *)           = []
     * StringUtils.split(&quot;abc def&quot;, null) = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtils.split(&quot;abc def&quot;, &quot; &quot;)  = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtils.split(&quot;abc  def&quot;, &quot; &quot;) = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtils.split(&quot;ab:cd:ef&quot;, &quot;:&quot;) = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * </pre>
     * 
     * @param str
     *            the String to parse, may be null
     * @param separatorChars
     *            the characters used as the delimiters, <code>null</code>
     *            splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String
     *         input
     */
    public static String[] split(String str, String separatorChars) {
        return split(str, separatorChars, -1);
    }

    /**
     * <p>
     * Splits the provided text into an array, separators specified. This is an
     * alternative to using StringTokenizer.
     * </p>
     * 
     * <p>
     * The separator is not included in the returned String array. Adjacent
     * separators are treated as one separator.
     * </p>
     * 
     * <p>
     * A <code>null</code> input String returns <code>null</code>. A
     * <code>null</code> separatorChars splits on whitespace.
     * </p>
     * 
     * <pre>
     * StringUtils.split(null, *, *)            = null
     * StringUtils.split(&quot;&quot;, *, *)              = []
     * StringUtils.split(&quot;ab de fg&quot;, null, 0)   = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtils.split(&quot;ab   de fg&quot;, null, 0) = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtils.split(&quot;ab:cd:ef&quot;, &quot;:&quot;, 0)    = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtils.split(&quot;ab:cd:ef&quot;, &quot;:&quot;, 2)    = [&quot;ab&quot;, &quot;cd:ef&quot;]
     * </pre>
     * 
     * @param str
     *            the String to parse, may be null
     * @param separatorChars
     *            the characters used as the delimiters, <code>null</code>
     *            splits on whitespace
     * @param max
     *            the maximum number of elements to include in the array. A zero
     *            or negative value implies no limit
     * @return an array of parsed Strings
     */
    public static String[] split(String str, String separatorChars, int max) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        int len = str.length();
        if (len == 0) {
            return (String[]) EMPTY_STRING_ARRAY.clone();
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimize 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing
     * the provided list of elements.
     * </p>
     * 
     * <p>
     * No delimiter is added before or after the list. A <code>null</code>
     * separator is the same as an empty String (""). Null objects or empty
     * strings within the array are represented by empty strings.
     * </p>
     * 
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = &quot;&quot;
     * StringUtils.join([null], *)              = &quot;&quot;
     * StringUtils.join([&quot;a&quot;, &quot;b&quot;, &quot;c&quot;], &quot;--&quot;)  = &quot;a--b--c&quot;
     * StringUtils.join([&quot;a&quot;, &quot;b&quot;, &quot;c&quot;], null)  = &quot;abc&quot;
     * StringUtils.join([&quot;a&quot;, &quot;b&quot;, &quot;c&quot;], &quot;&quot;)    = &quot;abc&quot;
     * StringUtils.join([null, &quot;&quot;, &quot;a&quot;], ',')   = &quot;,,a&quot;
     * </pre>
     * 
     * @param array
     *            the array of values to join together, may be null
     * @param aSeparator
     *            the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(Object[] array, String aSeparator) {
        String separator = aSeparator;
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        int arraySize = array.length;

        // ArraySize == 0: Len = 0
        // ArraySize > 0: Len = NofStrings *(len(firstString) + len(separator))
        // (Assuming that all Strings are roughly equally long)
        int bufSize = arraySize == 0 ? 0 : arraySize * ((array[0] == null ? 16 : array[0].toString().length()) + separator.length());

        StringBuilder buf = new StringBuilder(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * Returns a string representation of the contents of the specified array.
     * If the array contains other arrays as elements, they are converted to
     * strings by the {@link Object#toString} method inherited from
     * <tt>Object</tt>, which describes their <i>identities</i> rather than
     * their contents.
     * 
     * <p>
     * The value returned by this method is equal to the value that would be
     * returned by <tt>Arrays.asList(a).toString()</tt>, unless <tt>a</tt> is
     * <tt>null</tt>, in which case <tt>"null"</tt> is returned.
     * 
     * <p>
     * This is borrowed from Java 1.5, but uses StringBuilder.
     * 
     * @param a
     *            the array whose string representation to return
     * @return a string representation of <tt>a</tt>
     * @since 1.5
     */
    public static String toString(Object[] a) {
        if (a == null) {
            return "null";
        }

        if (a.length == 0) {
            return "[]";
        }

        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < a.length; i++) {
            if (i == 0) {
                buf.append('[');
            } else {
                buf.append(", ");
            }

            buf.append(String.valueOf(a[i]));
        }

        buf.append("]");
        return buf.toString();
    }

    /**
     * An empty immutable <code>String</code> array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

}
