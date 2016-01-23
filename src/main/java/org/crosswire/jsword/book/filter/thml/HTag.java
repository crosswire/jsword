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
 * THML Tag to process the h1, h2, h3, h4, h5, and h6 elements.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class HTag extends AbstractTag {
    /**
     * Create an H tag of the given level
     * 
     * @param level
     */
    public HTag(int level) {
        super();
        this.level = level;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#getTagName()
     */
    public String getTagName() {
        return "h" + level;
    }

    @Override
    public Element processTag(Book book, Key key, Element ele, Attributes attrs) {
        Element title = OSISUtil.factory().createTitle();
        title.setAttribute(OSISUtil.OSIS_ATTR_LEVEL, Integer.toString(level));

        if (ele != null) {
            ele.addContent(title);
        }

        return ele;
    }

    /**
     * The level of the title
     */
    private int level;
}
