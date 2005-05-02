package org.crosswire.jsword.book.search.lucene;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A query can have a optional range specifier and an optional blur specifier.
 * The range specifier can be +[range], -[range] or just [range].
 * This must stand at the beginning of the query and may be surrounded by whitespace.
 * The blur specifier is either ~ or ~n, where ~ means adjacent verses,
 * but ~n means to blur by n verses.
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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class QueryBuilder
{
    /**
     * Prevent Instansiation
     */
    private QueryBuilder()
    {
    }

    /**
     * Tokenize a query into a list of Tokens.
     * @param sought The text to parse
     * @return A List of selected Tokens
     */
    public static List tokenize(String sought)
    {
        List output = new ArrayList();
        if (sought == null || sought.length()  == 0)
        {
            return output;
        }

        int i = 0;

        Query query = null;
        Query range = null;
        String rangeModifier = null;
        // Look for a range +[...], -[...], or [...]
        Matcher rangeMatcher = RANGE_PATTERN.matcher(sought);
        if (rangeMatcher.find())
        {
            rangeModifier = rangeMatcher.group(1);
            range = new RangeQuery(rangeMatcher.group(2));
            sought = sought.substring(rangeMatcher.end() - 1);
        }

        // Look for a blur ~n
        Matcher blurMatcher = BLUR_PATTERN.matcher(sought);
        if (blurMatcher.find())
        {
            int blurFactor = 1;
            // Did we have ~ or ~n?
            if (blurMatcher.groupCount() > 0)
            {
                blurFactor = Integer.valueOf(blurMatcher.group(1)).intValue();
            }
            Query left = new BaseQuery(sought.substring(i, blurMatcher.start()));
            Query right = new BaseQuery(sought.substring(blurMatcher.end()));
            query = new BlurQuery(left, right, blurFactor);
        }
        else
        {
            query = new BaseQuery(sought);
        }

        if (range != null)
        {
            if (rangeModifier == null)
            {
                output.add(query);
                output.add(range);
            }
            else if (rangeModifier.charAt(0) == '+')
            {
                output.add(new AndQuery(query, range));
            }
            else
            {
                // AndNot needs to be after what it is restricting
                output.add(new AndNotQuery(query, range));
            }
        }
        else
        {
            output.add(query);
        }
        return output;
    }

    /**
     * The pattern of a range. This is anything that is
     * contained between a leading [] (but not containing a [ or ]),
     * with a + or - optional prefix,
     * perhaps surrounded by whitespace.
     */
    private static final Pattern RANGE_PATTERN = Pattern.compile("^\\s*([-+]?)\\[([^\\[\\]]+)\\]\\s*"); //$NON-NLS-1$

    /**
     * The pattern of a blur. ~, optionally followed by a number, representing the number of verses
     */
    private static final Pattern BLUR_PATTERN = Pattern.compile("\\s~(\\d*)?\\s"); //$NON-NLS-1$

}
