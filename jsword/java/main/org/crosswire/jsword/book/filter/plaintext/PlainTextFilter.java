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
package org.crosswire.jsword.book.filter.plaintext;

import java.util.List;

import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.passage.Key;
import org.jdom.Element;

/**
 * Filter to convert plain text to OSIS format.
 * Plain text is nothing more than lines without markup.
 * Unfortunately, it often uses whitespace for markup.
 * We will use OSIS lb to mark lines.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PlainTextFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.Filter#toOSIS(org.crosswire.jsword.book.filter.BookDataListener, java.lang.String)
     */
    public List toOSIS(Key key, String plain)
    {
        OSISUtil.ObjectFactory factory = OSISUtil.factory();
        Element ele = factory.createDiv();

        String[] lines = StringUtil.splitAll(plain, '\n');
        int lastIndex = lines.length - 1;
        for (int i = 0; i < lastIndex; i++)
        {
            // TODO(DMS): Preserve whitespace, in a smart manner.
            ele.addContent(lines[i]);
            ele.addContent(factory.createLB());
        }
        // Don't add a line break after the last line.
        if (lastIndex >= 0)
        {
            ele.addContent(lines[lastIndex]);
        }

        return ele.removeContent();
    }
}
