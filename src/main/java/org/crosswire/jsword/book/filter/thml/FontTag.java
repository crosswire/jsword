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
package org.crosswire.jsword.book.filter.thml;

import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Element;
import org.xml.sax.Attributes;

/**
 * THML Tag to process the font element.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class FontTag extends AbstractTag {
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#getTagName()
     */
    public String getTagName() {
        return "font";
    }

    @Override
    public Element processTag(Book book, Key key, Element ele, Attributes attrs) {
        Element seg = OSISUtil.factory().createSeg();
        StringBuilder buf = new StringBuilder();

        String color = attrs.getValue("color");
        if (color != null) {
            buf.append(OSISUtil.SEG_COLORPREFIX);
            buf.append(color);
            buf.append(';');
        }

        String size = attrs.getValue("size");
        if (size != null) {
            buf.append(OSISUtil.SEG_SIZEPREFIX);
            buf.append(size);
            buf.append(';');
        }

        String type = buf.toString();
        if (type.length() > 0) {
            seg.setAttribute(OSISUtil.OSIS_ATTR_TYPE, type);
        } else {
            DataPolice.report(book, key, "Missing color/size attribute.");
            XMLUtil.debugSAXAttributes(attrs);
        }

        if (ele != null) {
            ele.addContent(seg);
        }

        return seg;
    }
}
