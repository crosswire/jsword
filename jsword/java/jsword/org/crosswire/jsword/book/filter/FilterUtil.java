package org.crosswire.jsword.book.filter;


/**
 * Utilities to help filters.
 *
 * <p>Both OSISFilter and THMLFilter need to report on strings that failed
 * parsing but don't want to output too much data. forOutput() helps.
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
public class FilterUtil
{
    /**
     * Prevent Instansiation
     */
    private FilterUtil()
    {
    }

    /**
     * Cut up the input data so it is OK to output in an error log
     */
    public static String forOutput(String data)
    {
        if (data.length() < MAX_OUTPUT_LEN)
        {
            return data;
        }
        String chopped = data.substring(0, MAX_OUTPUT_LEN);
        return chopped + Msg.TRUNCATED;
    }

    /**
     * Some XML strings are very long and we don't want to debug the lot
     */
    private static final int MAX_OUTPUT_LEN = 100;
}
