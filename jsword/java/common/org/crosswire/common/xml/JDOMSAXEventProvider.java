
package org.crosswire.common.xml;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.SAXOutputter;
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
public class JDOMSAXEventProvider implements SAXEventProvider
{
    /**
     * Simple constructor
     */
    public JDOMSAXEventProvider(Document doc)
    {
        this.doc = doc;
    }

    /**
     * Serialize the JDOM Document
     * @see org.crosswire.common.xml.SAXEventProvider#provideSAXEvents(ContentHandler)
     */
    public void provideSAXEvents(ContentHandler handler) throws SAXException
    {
        try
        {
            SAXOutputter output = new SAXOutputter(handler);
            output.output(doc);
        }
        catch (JDOMException ex)
        {
            throw new SAXException(ex);
        }
    }

    /**
     * The document to work from
     */
    private Document doc;
}
