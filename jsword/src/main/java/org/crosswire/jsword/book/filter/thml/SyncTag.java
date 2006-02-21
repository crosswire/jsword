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

import java.util.List;

import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;
import org.xml.sax.Attributes;

/**
 * THML Tag to process the sync element. A sync tag is always empty and
 * immediately follows what it marks. With types of Strongs and morph
 * these are to become w elements that surround the word that they modify.
 * This requires that we find the last text element and surround it with
 * a w element. If the last text element is already surrounded with a w
 * element then this is added to it. As a simplifying assumption, we will
 * assume that the text element is not contained by anything except perhaps
 * by a w element.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SyncTag extends AbstractTag
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
            List siblings = ele.getContent();
            int size = siblings.size();
            if (size == 0)
            {
                return null;
            }
            Content lastEle = (Content) siblings.get(size - 1);
            if (lastEle instanceof Text)
            {
                Element w = OSISUtil.factory().createW();
                w.setAttribute(OSISUtil.ATTRIBUTE_W_LEMMA, OSISUtil.LEMMA_STRONGS + value);
                siblings.set(size - 1, w);
                w.addContent(lastEle);
            }
            else if (lastEle instanceof Element)
            {
                Element wEle = (Element) lastEle;
                if (wEle.getName().equals(OSISUtil.OSIS_ELEMENT_W))
                {
                    StringBuffer buf = new StringBuffer();
                    String strongsAttr = wEle.getAttributeValue(OSISUtil.ATTRIBUTE_W_LEMMA);
                    if (strongsAttr != null)
                    {
                        buf.append(strongsAttr);
                        buf.append(' ');
                    }
                    buf.append(OSISUtil.LEMMA_STRONGS);
                    buf.append(value);
                    wEle.setAttribute(OSISUtil.ATTRIBUTE_W_LEMMA, buf.toString());
                }
            }
            return null;
        }

        if ("morph".equals(type)) //$NON-NLS-1$
        {
            List siblings = ele.getContent();
            int size = siblings.size();
            if (size == 0)
            {
                return null;
            }
            Content lastEle = (Content) siblings.get(size - 1);
            if (lastEle instanceof Text)
            {
                Element w = OSISUtil.factory().createW();
                w.setAttribute(OSISUtil.ATTRIBUTE_W_MORPH, OSISUtil.MORPH_ROBINSONS + value);
                siblings.set(size - 1, w);
                w.addContent(lastEle);
            }
            else if (lastEle instanceof Element)
            {
                Element wEle = (Element) lastEle;
                if (wEle.getName().equals(OSISUtil.OSIS_ELEMENT_W))
                {
                    StringBuffer buf = new StringBuffer();
                    String strongsAttr = wEle.getAttributeValue(OSISUtil.ATTRIBUTE_W_MORPH);
                    if (strongsAttr != null)
                    {
                        buf.append(strongsAttr);
                        buf.append(' ');
                    }
                    buf.append(OSISUtil.MORPH_ROBINSONS);
                    buf.append(value);
                    wEle.setAttribute(OSISUtil.ATTRIBUTE_W_MORPH, buf.toString());
                }
            }
            return null;
        }

        if ("Dict".equals(type)) //$NON-NLS-1$
        {
            Element div = OSISUtil.factory().createDiv();
            div.setAttribute(OSISUtil.OSIS_ATTR_OSISID, "dict://" + value); //$NON-NLS-1$
            ele.addContent(div);
            return div;
        }

        DataPolice.report("sync tag has type=" + type + " when value=" + value); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }
}
