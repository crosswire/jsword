
package org.crosswire.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;

/**
 * A generic class of String utils.
 * It would be good if we could put this stuff in java.lang ...
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public final class StringUtil
{
    /** The newline character */
    public static final String NEWLINE = System.getProperty("line.separator", "\r\n");

    /**
     * This method reads an InputStream <b>In its entirety</b>, and passes
     * The text back as a string. If you are reading from a source that can
     * block then be preapred for a long wait for this to return.
     * @param in The Stream to read from.
     * @return A string containing all the text from the Stream.
     */
    public static String read(Reader in) throws IOException
    {
        StringBuffer retcode = new StringBuffer();
        String line = "";
        BufferedReader din = new BufferedReader(in);

        while (true)
        {
            line = din.readLine();

            if (line == null)
                break;

            retcode.append(line);
            retcode.append(NEWLINE);
        }

        return retcode.toString();
    }

    /**
     * Ensure a string is of a fixed length by truncating it or
     * by adding spaces until it is.
     * @param str The string to check
     * @param len The number of characters needed
     */
    public static String setLengthRightPad(String str, int len)
    {
        int diff = len - str.length();

        if (diff == 0)
        {
            return str;
        }

        if (diff < 0)
        {
            return str.substring(0, len);
        }
        else
        {
            return StringUtils.rightPad(str, len);
        }
    }

    /**
     * Ensure a string is of a fixed length by truncating it or
     * by adding spaces until it is.
     * @param str The string to check
     * @param len The number of characters needed
     */
    public static String setLengthLeftPad(String str, int len)
    {
        int diff = len - str.length();

        if (diff == 0)
        {
            return str;
        }

        if (diff < 0)
        {
            return str.substring(0, len);
        }
        else
        {
            return StringUtils.leftPad(str, len);
        }
    }

    /**
     * Like setLength() however this method only shortens strings that are too
     * long, and it shortens them in a human friendly way, currently this is
     * limited to adding "..." to show that it has been shortened, but we could
     * implement a fancy remove spaces/vowels algorythm.
     * @param str The string to check
     * @param len The number of characters needed
     */
    public static String shorten(String str, int len)
    {
        if (str.length() <= len)
            return str;

        return str.substring(0, len-3) + "...";
    }

    /**
     * This function creates a readable title from a
     * variable name type input. For example calling:
     *   StringUtil.createTitle("one_two") = "One Two"
     *   StringUtil.createTitle("oneTwo") = "One Two"
     */
    public static String createTitle(String variable)
    {
        StringBuffer retcode = new StringBuffer();
        boolean lastlower = false;
        boolean lastspace = true;

        for (int i=0; i<variable.length(); i++)
        {
            char c = variable.charAt(i);

            if (lastlower && Character.isUpperCase(c) && !lastspace)
            {
                retcode.append(' ');
            }

            lastlower = !Character.isUpperCase(c);

            if (lastspace)
            {
                c = Character.toUpperCase(c);
            }

            if (c == '_')
            {
                c = ' ';
            }

            if (!lastspace || c != ' ')
            {
                retcode.append(c);
            }

            lastspace = (c == ' ');
        }

        return "" + retcode;
    }

    /**
     * For example getInitials("Java DataBase Connectivity") = "JDC" and
     * getInitials("Church of England") = "CoE".
     * @param words The phrase from which to get the initial letters.
     * @return The initial letters in the given words.
     */
    public static String getInitials(String words)
    {
        String[] worda = StringUtils.split(words);

        StringBuffer retcode = new StringBuffer();
        for (int i=0; i<worda.length; i++)
        {
            retcode.append(worda[i].charAt(0));
        }

        return retcode.toString();
    }

    /**
     * For example getCapitals("Java DataBase Connectivity") = "JDBC" and
     * getCapitals("Church of England") = "CE".
     * A character is tested for capitalness using Character.isUpperCase
     * @param words The phrase from which to get the capital letters.
     * @return The capital letters in the given words.
     * @see #getInitials(String)
     */
    public static String getCapitals(String words)
    {
        StringBuffer retcode = new StringBuffer();

        for (int i=0; i<words.length(); i++)
        {
            char c = words.charAt(i);
            if (Character.isUpperCase(c))
                retcode.append(c);
        }

        return retcode.toString();
    }

    /**
     * This function creates a Java style name from a
     * variable name type input. For example calling:
     *   StringUtil.createTitle("one_two") = "OneTwo"
     *   StringUtil.createTitle("oneTwo") = "OneTwo"
     */
    public static String createJavaName(String variable)
    {
        StringBuffer retcode = new StringBuffer();
        boolean newword = true;

        for (int i=0; i<variable.length(); i++)
        {
            char c = variable.charAt(i);

            if (Character.isLetterOrDigit(c))
            {
                if (newword)
                {
                    retcode.append(Character.toUpperCase(c));
                }
                else
                {
                    retcode.append(c);
                }
            }

            newword = !Character.isLetter(c);
        }

        return retcode.toString();
    }
}

