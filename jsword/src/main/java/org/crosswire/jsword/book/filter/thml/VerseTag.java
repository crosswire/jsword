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
 * ID: $Id: PTag.java 1466 2007-07-02 02:48:09Z dmsmith $
 */
package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.OSISUtil;
import org.jdom.Element;
import org.xml.sax.Attributes;

/**
 * THML Tag to process the verse element. Note: the verse element surrounds
 * poetry.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class VerseTag extends AbstractTag {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.filter.thml.Tag#getTagName()
     */
    public String getTagName() {
        return "verse";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.filter.thml.Tag#processTag(org.jdom.Element,
     * org.xml.sax.Attributes)
     */
    /* @Override */
    public Element processTag(Element ele, Attributes attrs) {
        Element p = OSISUtil.factory().createLG();

        if (ele != null) {
            ele.addContent(p);
        }

        return p;
    }
}
