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
package org.crosswire.jsword.book.filter.thml;

import org.jdom.Element;
import org.xml.sax.Attributes;

/**
 * The AbstractTag ignores the tag.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class AbstractTag implements Tag {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.filter.thml.Tag#processTag(org.jdom.Element,
     * org.xml.sax.Attributes)
     */
    public Element processTag(Element ele, Attributes attrs) {
        // Ignore the tag
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.filter.thml.Tag#processContent(org.jdom.Element
     * )
     */
    public void processContent(Element ele) {
        // By default do nothing
    }

}
