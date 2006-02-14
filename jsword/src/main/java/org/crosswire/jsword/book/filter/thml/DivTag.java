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

import org.crosswire.jsword.book.OSISUtil;
import org.jdom.Element;
import org.xml.sax.Attributes;

/**
 * THML Tag to process the div element.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DivTag extends AbstractTag
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#getTagName()
     */
    public String getTagName()
    {
        return "div"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#processTag(org.jdom.Element, org.xml.sax.Attributes)
     */
    public Element processTag(Element ele, Attributes attrs)
    {
        // See if there are variant readings e.g. WHNU Mat 1.9
        String typeAttr = attrs.getValue("type"); //$NON-NLS-1$
        if ("variant".equals(typeAttr)) //$NON-NLS-1$
        {
            Element seg = OSISUtil.factory().createSeg();
            seg.setAttribute(OSISUtil.ATTRIBUTE_SEG_TYPE, OSISUtil.VARIANT_TYPE);
            String classAttr = attrs.getValue("class"); //$NON-NLS-1$
            if (classAttr != null)
            {
                seg.setAttribute(OSISUtil.ATTRIBUTE_SEG_SUBTYPE, OSISUtil.VARIANT_CLASS + classAttr);
            }
            ele.addContent(seg);
            return seg;
        }

        Element div = OSISUtil.factory().createDiv();
        ele.addContent(div);
        return div;
    }
}
