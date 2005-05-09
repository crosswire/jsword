package org.crosswire.jsword.book.filter.gbf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterException;
import org.crosswire.jsword.passage.Key;
import org.jdom.Element;

/**
 * Filter to convert GBF data to OSIS format.
 * 
 * The best place to go for more information about the GBF spec that I have
 * found is: <a href="http://ebible.org/bible/gbf.htm">http://ebible.org/bible/gbf.htm</a>
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
public class GBFFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.Filter#toOSIS(org.crosswire.jsword.book.filter.BookDataListener, java.lang.String)
     */
    public List toOSIS(Key key, String plain) throws FilterException
    {
        DataPolice.setKey(key);
        Element ele = OSISUtil.factory().createDiv();
        LinkedList stack = new LinkedList();
        stack.addFirst(ele);

        List taglist = parseTags(plain.trim());
        while (true)
        {
            if (taglist.isEmpty())
            {
                break;
            }

            Tag tag = (Tag) taglist.remove(0);
            tag.updateOsisStack(stack);
        }

        stack.removeFirst();
        DataPolice.setKey(null);
        return ele.removeContent();
    }

    /**
     * Turn the string into a list of tags in the order that they appear in the
     * original string.
     */
    private List parseTags(String remains)
    {
        List taglist = new ArrayList();

        while (true)
        {
            int ltpos = remains.indexOf('<');
            int gtpos = remains.indexOf('>');

            if (ltpos == -1 && gtpos == -1)
            {
                // no more tags to decode
                taglist.add(GBFTagBuilders.getTextTag(remains));
                remains = null;
                break;
            }

            // check that we don't have unmatched tags
            if (ltpos == -1 || gtpos == -1)
            {
                DataPolice.report("ignoring unmatched '<' or '>' in gbf: " + remains); //$NON-NLS-1$
                taglist.add(GBFTagBuilders.getTextTag(remains));
                remains = null;
                break;
            }

            // check that the tags are in a sensible order
            if (ltpos > gtpos)
            {
                DataPolice.report("ignoring transposed '<' or '>' in gbf: " + remains); //$NON-NLS-1$
                taglist.add(GBFTagBuilders.getTextTag(remains));
                remains = null;
                break;
            }

            // generate tags
            String start = remains.substring(0, ltpos);
            int strLen = start.length();
            if (strLen > 0)
            {
                int beginIndex = 0;
                boolean inSepStr = SEPARATORS.indexOf(start.charAt(0)) >= 0;
                // split words from seperators...
                // e.g., "a b c? e g." -> "a b c", "? ", "e g."
                //       "a b c<tag> e g." -> "a b c", tag, " ", "e g."
                for (int i = 1; inSepStr && i < strLen; i++)
                {
                    char currentChar = start.charAt(i);
                    if (!(SEPARATORS.indexOf(currentChar) >= 0))
                    {
                        taglist.add(GBFTagBuilders.getTextTag(start.substring(beginIndex, i)));
                        beginIndex = i;
                        inSepStr = false;
                    }
                }

                if (beginIndex < strLen)
                {
                    taglist.add(GBFTagBuilders.getTextTag(start.substring(beginIndex)));
                }
            }

            String tag = remains.substring(ltpos + 1, gtpos);
            int length = tag.length();
            if (length > 0)
            {
                Tag reply = GBFTagBuilders.getTag(tag);
                if (reply != null)
                {
                    taglist.add(reply);
                }
            }

            remains = remains.substring(gtpos + 1);
        }

        return taglist;
    }

    private static final String SEPARATORS = " ,:;.?!"; //$NON-NLS-1$

}
