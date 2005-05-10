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
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.jdom.Element;
import org.xml.sax.Attributes;

/**
 * THML Tag to process the scripRef element.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ScripRefTag implements Tag
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#getTagName()
     */
    public String getTagName()
    {
        return "scripRef"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.thml.Tag#processTag(org.jdom.Element, org.xml.sax.Attributes)
     */
    public Element processTag(Element ele, Attributes attrs)
    {
        Element reference = null;

        String refstr = attrs.getValue("passage"); //$NON-NLS-1$
        if (refstr != null)
        {
            reference = OSISUtil.factory().createReference();
            try
            {
                Passage ref = (Passage) keyf.getKey(refstr);
                String osisname = ref.getOSISName();
                reference.setAttribute(OSISUtil.ATTRIBUTE_REFERENCE_OSISREF, osisname);
            }
            catch (NoSuchKeyException ex)
            {
                DataPolice.report("Unparsable passage:" + refstr + " due to " + ex.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            }
            ele.addContent(reference);
        }
        else
        {
            // Don't lose the text
            reference = OSISUtil.factory().createDiv();
            // NOTE(joe): is it right to ignore a missing passage
            //DataPolice.report("Missing passage.");
            //XMLUtil.debugSAXAttributes(attrs);
        }

        return reference;
    }

    /**
     * To convert strings into Biblical keys
     */
    protected KeyFactory keyf = PassageKeyFactory.instance();
}
