package org.crosswire.jsword.book.data.gbf;

import java.util.LinkedList;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.data.JAXBUtil;
import org.crosswire.jsword.osis.Reference;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;

/**
 * Handle Footnotes: FR and Fr.
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
     * @see org.crosswire.jsword.book.data.gbf.TagBuilder#createTag(java.lang.String)
     */
    public Tag createTag(final String name)
    {
        if (name.startsWith("RX"))
        {
            return new Tag()
            {
                public void updateOsisStack(LinkedList stack) throws JAXBException
                {
                    Reference seg = JAXBUtil.factory().createReference();

                    String refstr = name.substring(2);
                    try
                    {
                        Passage ref = PassageFactory.createPassage(refstr);
                        seg.setOsisRef(ref.getOSISName());
                    }
                    catch (NoSuchVerseException ex)
                    {
                        log.warn("unable to parser reference: "+refstr);
                    }

                    Element current = (Element) stack.get(0);
                    JAXBUtil.getList(current).add(seg);
                    stack.addFirst(seg);
                }
            };
        }
    
        if (name.startsWith("Rx"))
        {
            return new Tag()
            {
                public void updateOsisStack(LinkedList stack) throws JAXBException
                {
                    stack.removeFirst();
                }
            };
        }
    
        return null;
    }        

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(CrossRefTagBuilder.class);
}
