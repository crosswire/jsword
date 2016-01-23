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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

/**
 * Class to convert a SAX stream into a simple String.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class SerializingContentHandler implements ContentHandler {
    /**
     * Default ctor that does not insert newlines.
     */
    public SerializingContentHandler() {
        this(false);
    }

    /**
     * Default ctor that conditionally inserts newlines.
     * 
     * @param newlines whether newlines are desired
     */
    public SerializingContentHandler(boolean newlines) {
        this.newlines = newlines;
        this.buffer = new StringBuilder();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return buffer.toString();
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator locator) {
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() {
        // buffer.append("<?xml version=\"1.0\"?>");

        if (newlines) {
            buffer.append('\n');
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() {
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(String prefix, String uri) {
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String prefix) {
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localname, String qname, Attributes attrs) {
        buffer.append('<');
        if (qname != null) {
            buffer.append(qname);
        } else {
            buffer.append(localname);
        }

        for (int i = 0; i < attrs.getLength(); i++) {
            buffer.append(' ');
            buffer.append(XMLUtil.getAttributeName(attrs, i));
            buffer.append("=\"");
            buffer.append(attrs.getValue(i));
            buffer.append('\"');
        }

        buffer.append('>');

        if (newlines) {
            buffer.append('\n');
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localname, String qname) {
        buffer.append("</");
        if (qname != null) {
            buffer.append(qname);
        } else {
            buffer.append(localname);
        }

        buffer.append('>');

        if (newlines) {
            buffer.append('\n');
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] chars, int start, int length) {
        String s = new String(chars, start, length);
        buffer.append(s);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] chars, int start, int length) {
        String s = new String(chars, start, length);
        buffer.append(s);
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction(String target, String data) {
        buffer.append("<!");
        buffer.append(target);
        buffer.append(' ');
        buffer.append(data);
        buffer.append("!>");

        if (newlines) {
            buffer.append('\n');
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String name) {
    }

    private boolean newlines;
    private StringBuilder buffer;
}
