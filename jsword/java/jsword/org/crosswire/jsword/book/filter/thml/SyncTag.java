/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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

import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.jdom.Element;
import org.xml.sax.Attributes;

/**
 * THML Tag to process the sync element.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SyncTag implements Tag
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#getTagName()
     */
    public String getTagName()
    {
        return "sync"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#processTag(org.jdom.Element, org.xml.sax.Attributes)
     */
    public Element processTag(Element ele, Attributes attrs)
    {
        // Strongs reference
        String type = attrs.getValue("type"); //$NON-NLS-1$
        String value = attrs.getValue("value"); //$NON-NLS-1$

        if ("Strongs".equals(type)) //$NON-NLS-1$
        {
            Element w = OSISUtil.factory().createW();
            w.setAttribute(OSISUtil.ATTRIBUTE_W_LEMMA, OSISUtil.LEMMA_STRONGS + value);
            ele.addContent(w);
            return w;
        }

        if ("Dict".equals(type)) //$NON-NLS-1$
        {
            Element div = OSISUtil.factory().createDiv();
            div.setAttribute(OSISUtil.ATTRIBUTE_DIV_OSISID, "dict://" + value); //$NON-NLS-1$
            ele.addContent(div);
            return div;
        }

        if ("morph".equals(type)) //$NON-NLS-1$
        {
            Element div = OSISUtil.factory().createDiv();
            div.setAttribute(OSISUtil.ATTRIBUTE_DIV_OSISID, "morph://" + value); //$NON-NLS-1$
            ele.addContent(div);
            return div;
        }

        DataPolice.report("sync tag has type=" + type + " when value=" + value); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }
}
