package org.crosswire.jsword.book.filter.gbf;

import java.util.ArrayList;
import java.util.List;

import org.crosswire.jsword.book.filter.ConversionLogger;

/**
 * A factory for Tags generated from a string.
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
public class Parser
{
    /**
     * Simple ctor
     */
    public Parser(String plain)
    {
        int lastIndex = plain.length() - 1;
        if (lastIndex >= 0 && plain.charAt(lastIndex) == ((char) 13))
        {
            plain = plain.substring(0, lastIndex);
        }
        remains = plain;
    }

    /**
     * Get Next tags in the string
     */
    public Tag getNextTag()
    {
        if (retval.isEmpty())
        {
            if (remains == null)
            {
                return null;
            }

            parseNextTag();
        }
        return (Tag) retval.remove(0);
    }

    /**
     * 
     */
    private void parseNextTag()
    {
        if (remains == null)
        {
            return;
        }

        int ltpos = remains.indexOf('<');
        int gtpos = remains.indexOf('>');

        if (ltpos == -1 && gtpos == -1)
        {
            // no more tags to decode
            retval.add(createText(remains));
            remains = null;
            return;
        }

        // check that we don't have unmatched tags
        if (ltpos == -1 || gtpos == -1)
        {
            ConversionLogger.report("ignoring unmatched '<' or '>' in gbf: " + remains);
            retval.add(createText(remains));
            remains = null;
            return;
        }

        // check that the tags are in a sensible order
        if (ltpos > gtpos)
        {
            ConversionLogger.report("ignoring transposed '<' or '>' in gbf: " + remains);
            retval.add(createText(remains));
            remains = null;
            return;
        }

        // generate tags
        String start = remains.substring(0, ltpos);
        int strLen = start.length();
        if (strLen > 0)
        {
            int beginIndex = 0;
            boolean inSepStr = isSeperator(start.charAt(0));
            // split words from seperators...
            // e.g., "a b c? e g." -> "a b c", "? ", "e g."
            //       "a b c<tag> e g." -> "a b c", tag, " ", "e g."
            for (int i = 1; inSepStr && i < strLen; i++)
            {
                char currentChar = start.charAt(i);
                if (!isSeperator(currentChar))
                {
                    retval.add(createText(start.substring(beginIndex, i)));
                    beginIndex = i;
                    inSepStr = false;
                }
            }

            if (beginIndex < strLen)
            {
                retval.add(createText(start.substring(beginIndex)));
            }
        }

        String tag = remains.substring(ltpos + 1, gtpos);
        if (tag.length() > 0)
        {
            retval.add(createTag(tag));
        }

        remains = remains.substring(gtpos + 1);
    }

    /**
     * 
     */
    private boolean isSeperator(char c)
    {
        final String seperators = " ,:;.?!";
        return seperators.indexOf(c) >= 0;
    }

    /**
     * 
     */
    private Tag createTag(String tagname)
    {
        for (int i=0; i<BUILDERS.length; i++)
        {
            Tag tag = BUILDERS[i].createTag(tagname);
            if (tag != null)
            {
                return tag;
            }
        }

        return UNKNOWN.createTag(tagname);
    }

    /**
     * Create a text tag which might involve some fancy parsing
     */
    private static Tag createText(String text)
    {
        return TEXT.createTag(text);
    }

    private String remains;
    private List retval = new ArrayList();

    private static final TagBuilder TEXT = new TextTagBuilder();
    private static final TagBuilder UNKNOWN = new UnknownTagBuilder();
    private static final TagBuilder[] BUILDERS = new TagBuilder[]
    {
        new PsalmTitleTagBuilder(),
        new TitleTagBuilder(),
        new JustifyTagBuilder(),
        new HeaderTagBuilder(),
        new EndOfLineTagBuilder(),
        new FootnoteTagBuilder(),
        new CrossRefTagBuilder(),
        new PoetryTagBuilder(),
        new ItalicTagBuilder(),
        new UnderlineTagBuilder(),
        new RedLetterTagBuilder(),
        new OTQuoteTagBuilder(),
        new TextFootnoteTagBuilder(),
        new ParagraphTagBuilder(),
        new StrongsMorphTagBuilder(),
        new StrongsWordTagBuilder(),
    };
}
