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
import org.jdom2.Namespace;
import org.xml.sax.Attributes;

/**
 * THML Tag to process the foreign element.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class ForeignTag extends AbstractTag {
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#getTagName()
     */
    public String getTagName() {
        return "foreign";
    }

    @Override
    public Element processTag(Book book, Key key, Element ele, Attributes attrs) {
        Element div = OSISUtil.factory().createForeign();

        String lang = attrs.getValue("lang");
        if (lang != null) {
            // OSIS defines the long attribute as the one from the xml namespace
            div.setAttribute(OSISUtil.OSIS_ATTR_LANG, lang, Namespace.XML_NAMESPACE);
        }

        if (ele != null) {
            ele.addContent(div);
        }

        return div;
    }
}
