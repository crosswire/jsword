package org.crosswire.jsword.book.filter.thml;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;

import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.JAXBUtil;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.W;
import org.xml.sax.Attributes;

/**
 * THML Tag to process the sync element.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
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
     * @see org.crosswire.jsword.book.filter.thml.Tag#processTag(javax.xml.bind.Element, org.xml.sax.Attributes)
     */
    public void processTag(Element ele, Attributes attrs) throws JAXBException
    {
        // Strongs reference
        String type = attrs.getValue("type"); //$NON-NLS-1$
        String value = attrs.getValue("value"); //$NON-NLS-1$
        if ("Strongs".equals(type)) //$NON-NLS-1$
        {
            W w = JAXBUtil.factory().createW();
            w.setLemma(JAXBUtil.LEMMA_STRONGS+value);
            JAXBUtil.getList(ele).add(w);
        }
        else if ("Dict".equals(type)) //$NON-NLS-1$
        {
            Div div = JAXBUtil.factory().createDiv();
            div.setOsisID("dict://"+value); //$NON-NLS-1$
            JAXBUtil.getList(ele).add(div);
        }
        else if ("morph".equals(type)) //$NON-NLS-1$
        {
            Div div = JAXBUtil.factory().createDiv();
            div.setOsisID("morph://"+value); //$NON-NLS-1$
            JAXBUtil.getList(ele).add(div);
        }
        else
        {
            DataPolice.report("sync tag has type="+type+" when value="+value); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
