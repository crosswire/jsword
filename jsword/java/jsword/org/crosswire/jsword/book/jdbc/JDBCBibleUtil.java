
package org.crosswire.jsword.book.jdbc;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;

/**
 * JDBCBible was getting a bit long winded, so I took all the static
 * methods and parcled them off here.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version D0.I0.T0
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

        return ""+retcode;
    }

    /**
     * Some fancy footwork we need to do to get rid of strongs numbers
     */
    protected static String processText(String text)
    {
        text = StringUtil.chop(text, "{", "}");
        text = chop(text, "<", ">");
        text = chop(text, "(", ")");
        text = StringUtil.removeChar(text, '[');
        text = StringUtil.removeChar(text, ']');

        text = StringUtil.swap(text, "    ", " ");
        text = StringUtil.swap(text, "   ", " ");
        text = StringUtil.swap(text, "  ", " ");

        text = StringUtil.swap(text, " ,", ",");
        text = StringUtil.swap(text, " .", ".");
        text = StringUtil.swap(text, " !", "!");
        text = StringUtil.swap(text, " ?", "?");
        text = StringUtil.swap(text, " :", ":");
        text = StringUtil.swap(text, " ;", ";");
        text = StringUtil.swap(text, " '", "'");
        text = StringUtil.swap(text, " )", ")");
        text = StringUtil.swap(text, " -", "-");

        text = text.trim();

        return text;
    }

    /**
     * Strips the text between a pair of delimitters. For example:
     * <code>chop("123(456)789", "(", ")") = "123789"</code>
     * Delimiters currently do not nest. So:
     * <code>chop("12(34(56)78)9", "(", ")") = Exception</code>
     */
    protected static String chop(String orig, String start_delim, String end_delim)
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
                    break;

                // The text to be considered for chopping out
                String chopped_text = orig.substring(next_start+start_delim.length(),
                                                     next_end);

                // Check to see that what we are chopping out really is a number
                try
                {
                    Integer.parseInt(chopped_text);

                    orig = orig.substring(0, next_start) +
                           orig.substring(next_end+end_delim.length());
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
            log.warning("orig="+orig+" end_delim="+end_delim+" end_delim="+end_delim);
            Reporter.informUser(JDBCBibleUtil.class, ex);
            return "Error";
        }
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger("bible.book");
}
