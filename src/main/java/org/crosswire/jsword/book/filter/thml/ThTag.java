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
 * THML Tag to process the th element.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class ThTag extends AbstractTag {
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#getTagName()
     */
    public String getTagName() {
        return "th";
    }

    @Override
    public Element processTag(Book book, Key key, Element ele, Attributes attrs) {
        Element cell = OSISUtil.factory().createCell();
        cell.setAttribute(OSISUtil.ATTRIBUTE_TABLE_ROLE, OSISUtil.TABLE_ROLE_LABEL);
        if (attrs != null) {
            String rows = attrs.getValue("rowspan");
            if (rows != null) {
                cell.setAttribute(OSISUtil.ATTRIBUTE_CELL_ROWS, rows);
            }
            String cols = attrs.getValue("colspan");
            if (cols != null) {
                cell.setAttribute(OSISUtil.ATTRIBUTE_CELL_COLS, cols);
            }
        }

        if (ele != null) {
            ele.addContent(cell);
        }

        return cell;
    }
}
