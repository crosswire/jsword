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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public final class StringUtil
{
    /**
     * Prevent Instansiation
     */
    private StringUtil()
    {
    }

    /**
     * The newline character
     */
    public static final String NEWLINE = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

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
        String line = ""; //$NON-NLS-1$
        BufferedReader din = new BufferedReader(in);

        while (true)
        {
            line = din.readLine();

            if (line == null)
            {
                break;
            }

            retcode.append(line);
            retcode.append(NEWLINE);
        }

        return retcode.toString();
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

        return "" + retcode; //$NON-NLS-1$
    }

    /**
     * For example getInitials("Java DataBase Connectivity") = "JDC" and
     * getInitials("Church of England") = "CoE".
     * @param sentence The phrase from which to get the initial letters.
     * @return The initial letters in the given words.
     */
    public static String getInitials(String sentence)
    {
        String[] words = StringUtils.split(sentence);

        StringBuffer retcode = new StringBuffer();
        for (int i=0; i<words.length; i++)
        {
            String word = words[i];

            char first = 0;
            for (int j = 0; first == 0 && j < word.length(); j++)
            {
                char c = word.charAt(j);
                if (Character.isLetter(c))
                {
                    first = c;
                }
            }

            if (first != 0)
            {
                retcode.append(first);
            }
        }

        return retcode.toString();
    }
}
