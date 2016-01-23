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

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Element;
import org.xml.sax.Attributes;

/**
 * THML Tag to process the div element.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class DivTag extends AbstractTag {
    /**
     * Create an div tag
     */
    public DivTag() {
        super();
        this.level = 0;
    }

    /**
     * Create an div tag of the given level
     * 
     * @param level
     */
    public DivTag(int level) {
        super();
        this.level = level;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#getTagName()
     */
    public String getTagName() {
        if (level == 0) {
            return "div";
        }
        return "div" + level;
    }

    @Override
    public Element processTag(Book book, Key key, Element ele, Attributes attrs) {
        // See if there are variant readings e.g. WHNU Mat 1.9
        String typeAttr = attrs.getValue("type");
        if ("variant".equals(typeAttr)) {
            Element seg = OSISUtil.factory().createSeg();
            seg.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.VARIANT_TYPE);
            String classAttr = attrs.getValue("class");
            if (classAttr != null) {
                seg.setAttribute(OSISUtil.OSIS_ATTR_SUBTYPE, OSISUtil.VARIANT_CLASS + '-' + classAttr);
            }

            if (ele != null) {
                ele.addContent(seg);
            }

            return seg;
        }

        Element div = OSISUtil.factory().createDiv();

        if (ele != null) {
            ele.addContent(div);
        }

        return div;
    }

    /**
     * The level of the division
     */
    private int level;
}
