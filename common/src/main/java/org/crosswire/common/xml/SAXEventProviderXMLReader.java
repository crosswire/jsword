/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * A helper to aid people working with a SAXEventProvider.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SAXEventProviderXMLReader implements XMLReader {
    /**
     * Constructor SAXEventProviderXMLReader.
     */
    public SAXEventProviderXMLReader(SAXEventProvider docIn) {
        this.docIn = docIn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#getFeature(java.lang.String)
     */
    public boolean getFeature(String arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#setFeature(java.lang.String, boolean)
     */
    public void setFeature(String arg0, boolean arg1) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#getProperty(java.lang.String)
     */
    public Object getProperty(String arg0) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#setProperty(java.lang.String,
     * java.lang.Object)
     */
    public void setProperty(String arg0, Object arg1) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
     */
    public void setEntityResolver(EntityResolver entities) {
        this.entities = entities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#getEntityResolver()
     */
    public EntityResolver getEntityResolver() {
        return entities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
     */
    public void setDTDHandler(DTDHandler dtds) {
        this.dtds = dtds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#getDTDHandler()
     */
    public DTDHandler getDTDHandler() {
        return dtds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler content) {
        this.content = content;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#getContentHandler()
     */
    public ContentHandler getContentHandler() {
        return content;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
     */
    public void setErrorHandler(ErrorHandler errors) {
        this.errors = errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#getErrorHandler()
     */
    public ErrorHandler getErrorHandler() {
        return errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    public void parse(InputSource is) throws SAXException {
        if (!(is instanceof SAXEventProviderInputSource)) {
            throw new SAXException("SAXEventProviderInputSource required"); //$NON-NLS-1$
        }

        docIn.provideSAXEvents(getContentHandler());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.XMLReader#parse(java.lang.String)
     */
    public void parse(String arg0) throws SAXException {
        throw new SAXException("SAXEventProviderInputSource required"); //$NON-NLS-1$
    }

    private SAXEventProvider docIn;
    private ErrorHandler errors;
    private ContentHandler content;
    private DTDHandler dtds;
    private EntityResolver entities;
}
