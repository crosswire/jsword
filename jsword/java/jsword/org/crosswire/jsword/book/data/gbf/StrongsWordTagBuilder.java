package org.crosswire.jsword.book.data.gbf;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.data.JAXBUtil;
import org.crosswire.jsword.osis.W;

/**
 * Tag syntax: word<WHxxxx> or word<WGxxxx>.
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
public class StrongsWordTagBuilder implements TagBuilder
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.gbf.TagBuilder#createTag(java.lang.String)
     */
    public Tag createTag(final String tagname)
    {
        if (!tagname.startsWith("WH") && !tagname.startsWith("WG"))
        {
            return null;
        }

        return new Tag()
        {
            /* (non-Javadoc)
             * @see org.crosswire.jsword.book.data.gbf.Tag#updateOsisStack(java.util.Stack)
             */
            public void updateOsisStack(LinkedList stack) throws JAXBException
            {
                String name = tagname.trim();

                Element ele = (Element) stack.get(0);
                List list = JAXBUtil.getList(ele);
                if (list.isEmpty())
                {
                    log.error("Source has problem for tag <" + name + ">.");
                    return;
                }
                int lastIndex = list.size() - 1;
                Object prevObj = list.get(lastIndex);
                W word = null;

                if (prevObj instanceof String)
                {
                    word = JAXBUtil.factory().createW();
                    word.getContent().add(prevObj);
                    list.set(lastIndex, word);
                }
                else if (prevObj instanceof W)
                {
                    word = (W) prevObj;
                }
                else
                {
                    log.error("Source has problem for tag <" + name + ">.");
                    return;
                }

                String existingLemma = word.getLemma();
                StringBuffer newLemma = new StringBuffer();

                if (existingLemma != null && existingLemma.length() > 0)
                {
                    newLemma.append(existingLemma).append('|');
                }
                newLemma.append("x-Strongs:").append(name.substring(2));
                word.setLemma(newLemma.toString());
            }
        };
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(StrongsWordTagBuilder.class);
}
