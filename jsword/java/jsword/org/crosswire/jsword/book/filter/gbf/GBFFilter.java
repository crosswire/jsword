package org.crosswire.jsword.book.filter.gbf;

import java.util.LinkedList;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;

import org.crosswire.jsword.book.filter.FilterException;
import org.crosswire.jsword.book.filter.Filter;

/**
 * Filter to convert GBF data to OSIS format.
 * 
 * The best place to go for more information about the GBF spec that I have
 * found is: <a href="http://ebible.org/bible/gbf.htm">http://ebible.org/bible/gbf.htm</a>
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
public class GBFFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.filter.Filter#toOSIS(org.crosswire.jsword.book.filter.BookDataListener, java.lang.String)
     */
    public void toOSIS(Element ele, String plain) throws FilterException
    {
        try
        {
            LinkedList stack = new LinkedList();
            stack.addFirst(ele);

            Parser parser = new Parser(plain);
            Tag tag = parser.getNextTag();
            while (tag != null)
            {
                tag.updateOsisStack(stack);
                tag = parser.getNextTag();
            }

            stack.removeFirst();
        }
        catch (JAXBException ex)
        {
            throw new FilterException(Msg.GBF_JAXB, ex);
        }
    }
}
