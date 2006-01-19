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
package org.crosswire.jsword.book.jdbc;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;

/**
 * JDBCBook was getting a bit long winded, so I took all the static
 * methods and parcled them off here.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JDBCBibleUtil
{
    /**
     * Ensure that we can't be instansiated
     */
    private JDBCBibleUtil()
    {
    }

    /**
     * Removes a character from a String
     * @param orig The string to parse.
     * @param x The char to remove
     * @param y The char to replace it with
     * @return The string with the char replaced
     */
    protected static String swapChar(String orig, char x, char y)
    {
        StringBuffer retcode = new StringBuffer(orig);
        int start = 0;

        while (true)
        {
            start = orig.indexOf(x, start+1);
            if (start == -1) break;
            retcode.setCharAt(start, y);
        }

        return retcode.toString();
    }

    /**
     * Some fancy footwork we need to do to get rid of strongs numbers
     */
    protected static String processText(String text)
    {
        text = chop1(text, "{", "}"); //$NON-NLS-1$ //$NON-NLS-2$
        text = chop2(text, "<", ">"); //$NON-NLS-1$ //$NON-NLS-2$
        text = chop2(text, "(", ")"); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll("[", ""); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll("]", ""); //$NON-NLS-1$ //$NON-NLS-2$

        text = text.replaceAll("    ", " "); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll("   ", " "); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll("  ", " "); //$NON-NLS-1$ //$NON-NLS-2$

        text = text.replaceAll(" ,", ","); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll(" .", "."); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll(" !", "!"); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll(" ?", "?"); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll(" :", ":"); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll(" ;", ";"); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll(" '", "'"); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll(" )", ")"); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll(" -", "-"); //$NON-NLS-1$ //$NON-NLS-2$

        text = text.trim();

        return text;
    }

    /**
     * Strips the text between a pair of delimitters. For example:
     * <code>chop("123(456)789", "(", ")") = "123789"</code>
     * Delimiters currently do not nest. So:
     * <code>chop("12(34(56)78)9", "(", ")") = Exception</code>
     */
    protected static String chop1(String orig, String start_delim, String end_delim)
    {
        while (true)
        {
            int next_start = orig.indexOf(start_delim);
            int next_end = orig.indexOf(end_delim);

            if (next_start == -1)
            {
                if (next_end == -1)
                {
                    break;
                }
                throw new IllegalArgumentException(Msg.DELIM_UNMATCHED.toString());
            }

            if (next_end == -1)
            {
                throw new IllegalArgumentException(Msg.DELIM_NESTED.toString());
            }

            orig = orig.substring(0, next_start)
                 + orig.substring(next_end+end_delim.length());
        }

        return orig;
    }

    /**
     * Strips the text between a pair of delimitters. For example:
     * <code>chop("123(456)789", "(", ")") = "123789"</code>
     * Delimiters currently do not nest. So:
     * <code>chop("12(34(56)78)9", "(", ")") = Exception</code>
     */
    protected static String chop2(String orig, String start_delim, String end_delim)
    {
        try
        {
            int skip_start = 0;
            int skip_end = 0;

            while (true)
            {
                // Find the next start and end delimitters, ensure that
                // the end delimitters is after the start one.
                int next_start = orig.indexOf(start_delim, skip_start);
                skip_end = Math.max(skip_end, next_start);
                int next_end = orig.indexOf(end_delim, skip_end);

                // If there are no more give up
                if (next_start == -1 || next_end == -1)
                {
                    break;
                }

                // The text to be considered for chopping out
                String chopped_text = orig.substring(next_start+start_delim.length(), next_end);

                // Check to see that what we are chopping out really is a number
                try
                {
                    Integer.parseInt(chopped_text);

                    orig = orig.substring(0, next_start) + orig.substring(next_end+end_delim.length());
                }
                catch (NumberFormatException ex)
                {
                    // It is not a number so we best leave it in
                    skip_start = next_start + 1;
                    skip_end = next_end + 1;
                }
            }

            return orig;
        }
        catch (StringIndexOutOfBoundsException ex)
        {
            log.warn("orig="+orig+" end_delim="+end_delim+" end_delim="+end_delim); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            Reporter.informUser(JDBCBibleUtil.class, ex);
            return Msg.ERROR.toString();
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(JDBCBibleUtil.class);
}
