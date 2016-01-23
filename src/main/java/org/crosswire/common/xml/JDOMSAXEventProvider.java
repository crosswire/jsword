/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.xml;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.SAXOutputter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A SAXEventProvider that provides SAX events from a JDOM Document.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class JDOMSAXEventProvider implements SAXEventProvider {
    /**
     * Simple constructor
     * 
     * @param doc the document
     */
    public JDOMSAXEventProvider(Document doc) {
        this.doc = doc;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.SAXEventProvider#provideSAXEvents(org.xml.sax.ContentHandler)
     */
    public void provideSAXEvents(ContentHandler handler) throws SAXException {
        try {
            SAXOutputter output = new SAXOutputter(handler);
            output.output(doc);
        } catch (JDOMException ex) {
            throw new SAXException(ex);
        }
    }

    /**
     * The document to work from
     */
    private Document doc;
}
