/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.filter.gbf;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 * Filter to convert GBF data to OSIS format.
 * 
 * The best place to go for more information about the GBF spec is:
 * <a href="http://ebible.org/bible/gbf.htm">http://ebible.org/bible/gbf.htm</a>
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class GBFFilter implements SourceFilter {
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.Filter#toOSIS(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public List<Content> toOSIS(Book book, Key key, String plain) {
        Element ele = OSISUtil.factory().createDiv();
        LinkedList<Content> stack = new LinkedList<Content>();
        stack.addFirst(ele);

        List<Tag> taglist = parseTags(book, key, plain.trim());
        while (true) {
            if (taglist.isEmpty()) {
                break;
            }

            Tag tag = taglist.remove(0);
            tag.updateOsisStack(book, key, stack);
        }

        stack.removeFirst();
        return ele.removeContent();
    }

    @Override
    public GBFFilter clone() {
        GBFFilter clone = null;
        try {
            clone = (GBFFilter) super.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }

    /**
     * Turn the string into a list of tags in the order that they appear in the
     * original string.
     */
    private List<Tag> parseTags(Book book, Key key, String aRemains) {
        String remains = aRemains;
        List<Tag> taglist = new ArrayList<Tag>();

        // A GBF code is of the form <XY...> or <Xy...>
        // where the first letter is always capitalized and
        // the second letter indicates an open or close tag.
        // Upper letters are open, lower are close.
        // The ... is optional and represents an argument.
        // Sometimes the argument is preceded by a space.
        // In GBF it is legal to have < and > otherwise.
        // In at least one module, GerLut1545, << ... >> is used for quotes.
        while (true) {
            int ltpos = remains.indexOf('<');
            int gtpos = remains.indexOf('>', ltpos + 1);

            // check whether we have unmatched < and >, or no tags at all
            // If so then we don't have a tag in the remaining.
            if (ltpos == -1 || gtpos == -1) {
                // If the first letter after < is an upper case letter
                // then report it as a potential problem
                if (ltpos >= 0
                        && ltpos < remains.length() + 1
                        && Character.isUpperCase(remains.charAt(ltpos + 1)))
                {
                    DataPolice.report(book, key, "Possible bad GBF tag" + remains);
                }
                if (gtpos != -1 && ltpos >= 0) {
                    DataPolice.report(book, key, "Possible bad GBF tag" + remains);
                }
                int pos = Math.max(ltpos, gtpos) + 1;
                // If there were not any <, > or either ended the string
                // then we only have text.
                if (pos == 0 || pos == remains.length()) {
                    taglist.add(GBFTagBuilders.getTextTag(remains));
                    break;
                }
                taglist.add(GBFTagBuilders.getTextTag(remains.substring(0, pos)));
                remains = remains.substring(pos);
                continue;
            }

            // If the character after the < is not an upper case letter
            // then we don't have GBF.
            // So, create a text tag that ends with the found >.
            // Note that in JST, there are spurious html tags and
            // this will treat them as valid GBF text.
            char firstChar = remains.charAt(ltpos + 1);
            if (!Character.isUpperCase(firstChar)) {
                taglist.add(GBFTagBuilders.getTextTag(remains.substring(0, gtpos + 1)));
                remains = remains.substring(gtpos + 1);
                continue;
            }

            // generate tags
            String start = remains.substring(0, ltpos);
            int strLen = start.length();
            if (strLen > 0) {
                int beginIndex = 0;
                boolean inSepStr = SEPARATORS.indexOf(start.charAt(0)) >= 0;
                // split words from separators...
                // e.g., "a b c? e g." -> "a b c", "? ", "e g."
                // "a b c<tag> e g." -> "a b c", tag, " ", "e g."
                for (int i = 1; inSepStr && i < strLen; i++) {
                    char currentChar = start.charAt(i);
                    if (!(SEPARATORS.indexOf(currentChar) >= 0)) {
                        taglist.add(GBFTagBuilders.getTextTag(start.substring(beginIndex, i)));
                        beginIndex = i;
                        inSepStr = false;
                    }
                }

                if (beginIndex < strLen) {
                    taglist.add(GBFTagBuilders.getTextTag(start.substring(beginIndex)));
                }
            }

            String tag = remains.substring(ltpos + 1, gtpos);
            int length = tag.length();
            if (length > 0) {
                Tag reply = GBFTagBuilders.getTag(book, key, tag);
                if (reply != null) {
                    taglist.add(reply);
                }
            }

            remains = remains.substring(gtpos + 1);
        }

        return taglist;
    }

    private static final String SEPARATORS = " ,:;.?!";

}
