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
package org.crosswire.jsword.book.filter.plaintext;

import java.util.List;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 * Filter to convert plain text to OSIS format. Plain text is nothing more than
 * lines without markup. Unfortunately, it often uses whitespace for markup. We
 * will use OSIS lb to mark lines.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class PlainTextFilter implements SourceFilter {
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.Filter#toOSIS(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public List<Content> toOSIS(Book book, Key key, String plain) {
        OSISUtil.OSISFactory factory = OSISUtil.factory();
        Element ele = factory.createDiv();

        String[] lines = StringUtil.splitAll(plain, '\n');
        int lastIndex = lines.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            // TODO(DMS): Preserve whitespace, in a smart manner.
            ele.addContent(lines[i]);
            ele.addContent(factory.createLB());
        }
        // Don't add a line break after the last line.
        if (lastIndex >= 0) {
            ele.addContent(lines[lastIndex]);
        }

        return ele.removeContent();
    }

    @Override
    public PlainTextFilter clone() {
        PlainTextFilter clone = null;
        try {
            clone = (PlainTextFilter) super.clone();
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return clone;
    }
}
