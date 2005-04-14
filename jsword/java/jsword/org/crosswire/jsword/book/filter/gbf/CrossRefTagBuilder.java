package org.crosswire.jsword.book.filter.gbf;

import java.util.LinkedList;

import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.jdom.Element;

/**
 * Handle Cross References: RX and Rx.
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
public class CrossRefTagBuilder implements TagBuilder
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.gbf.TagBuilder#createTag(java.lang.String)
     */
    public Tag createTag(final String name)
    {
        if (name.startsWith("RX")) //$NON-NLS-1$
        {
            return new Tag()
            {
                public void updateOsisStack(LinkedList stack)
                {
                    Element seg = OSISUtil.factory().createReference();

                    String refstr = name.substring(2);
                    try
                    {
                        Passage ref = (Passage) keyf.getKey(refstr);
                        seg.setAttribute(OSISUtil.ATTRIBUTE_REFERENCE_OSISREF, ref.getOSISName());
                    }
                    catch (NoSuchKeyException ex)
                    {
                        DataPolice.report("unable to parse reference: " + refstr); //$NON-NLS-1$
                    }

                    Element current = (Element) stack.get(0);
                    current.addContent(seg);
                    stack.addFirst(seg);
                }
            };
        }

        if (name.startsWith("Rx")) //$NON-NLS-1$
        {
            return new Tag()
            {
                public void updateOsisStack(LinkedList stack)
                {
                    stack.removeFirst();
                }
            };
        }

        return null;
    }

    /**
     * To convert strings into Biblical keys
     */
    protected KeyFactory keyf = PassageKeyFactory.instance();
}
