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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * A SAXEventProvider that provides SAX events from a String.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class StringSAXEventProvider implements SAXEventProvider {
    /**
     * Simple ctor
     * 
     * @param xmlstr the xml as a string
     * @throws ParserConfigurationException when there is a parser configuration problem
     * @throws SAXException when there is a SAX problem
     */
    public StringSAXEventProvider(String xmlstr) throws ParserConfigurationException, SAXException {
        this.xmlstr = xmlstr;

        SAXParserFactory fact = SAXParserFactory.newInstance();
        SAXParser parser = fact.newSAXParser();

        reader = parser.getXMLReader();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.SAXEventProvider#provideSAXEvents(org.xml.sax.ContentHandler)
     */
    public void provideSAXEvents(ContentHandler handler) throws SAXException {
        try {
            StringReader sr = new StringReader(xmlstr);
            InputSource is = new InputSource(sr);

            reader.setContentHandler(handler);
            reader.parse(is);
        } catch (IOException ex) {
            throw new SAXException(ex);
        }
    }

    /**
     * The SAX parser
     */
    private XMLReader reader;

    /**
     * The source of XML data
     */
    private String xmlstr;
}
