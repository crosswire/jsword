package org.crosswire.common.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A SAXEventProvider that provides SAX events from a JDOM Document.
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
public class JAXBSAXEventProvider implements SAXEventProvider
{
    /**
     * Simple constructor
     */
    public JAXBSAXEventProvider(JAXBContext jc, Object jaxbelement)
    {
        this.jc = jc;
        this.jaxbelement = jaxbelement;
    }

    /**
     * Serialize the JAXB element
     * @see org.crosswire.common.xml.SAXEventProvider#provideSAXEvents(ContentHandler)
     */
    public void provideSAXEvents(ContentHandler handler) throws SAXException
    {
        try
        {
            Marshaller m = jc.createMarshaller();
            m.marshal(jaxbelement, handler);
        }
        catch (JAXBException ex)
        {
            throw new SAXException(ex);
        }
    }

    /**
     * From which we get a Marshaller 
     */
    private JAXBContext jc;

    /**
     * The JAXB element to work from
     */
    private Object jaxbelement;
}
