
package org.crosswire.jsword.book.data;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.crosswire.common.util.Logger;
import org.xml.sax.InputSource;

/**
 * Filter to convert an OSIS XML string to OSIS format.
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
public class OSISFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.Filter#toOSIS(org.crosswire.jsword.book.data.BookDataListener, java.lang.String)
     */
    public void toOSIS(Element ele, String plain) throws DataException
    {
        if (unm == null)
        {
            try
            {
                unm = JAXBUtil.getJAXBContext().createUnmarshaller();
            }
            catch (JAXBException ex)
            {
                throw new DataException(Msg.OSIS_INIT, ex);
            }
        }

        try
        {
            // create a root element to house our document fragment
            StringReader in = new StringReader("<div>"+plain+"</div>");
            InputSource is = new InputSource(in);

            Element data = (Element) unm.unmarshal(is);

            // data is the div we added above so the input was a well formed
            // XML so we need to add the content of the div and not the div
            // itself

            List host = JAXBUtil.getList(ele);
            List content = JAXBUtil.getList(data);

            host.addAll(content);
        }
        catch (Exception ex)
        {
            log.warn("parse failed", ex);

            List list = JAXBUtil.getList(ele);
            list.add("Errors exist in the source module: " + ex.getMessage());

            try
            {
                list.add(JAXBUtil.factory().createP());
            }
            catch (Exception ex2)
            {
                log.warn("createP() failed", ex2);
            }

            list.add(plain);
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(OSISFilter.class);

    /**
     * The JAXB unmarshaller
     */
    private Unmarshaller unm = null;
}
