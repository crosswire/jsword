
package org.crosswire.common.xml;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * A helper to aid people working with a SAXEventProvider.
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
public class SAXEventProviderXMLReader implements XMLReader
{
    /**
     * Constructor SAXEventProviderXMLReader.
     */
    public SAXEventProviderXMLReader(SAXEventProvider doc_in)
    {
        this.doc_in = doc_in;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#getFeature(java.lang.String)
     */
    public boolean getFeature(String arg0) throws SAXNotRecognizedException, SAXNotSupportedException
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#setFeature(java.lang.String, boolean)
     */
    public void setFeature(String arg0, boolean arg1) throws SAXNotRecognizedException, SAXNotSupportedException
    {
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#getProperty(java.lang.String)
     */
    public Object getProperty(String arg0) throws SAXNotRecognizedException, SAXNotSupportedException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#setProperty(java.lang.String, java.lang.Object)
     */
    public void setProperty(String arg0, Object arg1) throws SAXNotRecognizedException, SAXNotSupportedException
    {
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
     */
    public void setEntityResolver(EntityResolver entities)
    {
        this.entities = entities;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#getEntityResolver()
     */
    public EntityResolver getEntityResolver()
    {
        return entities;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
     */
    public void setDTDHandler(DTDHandler dtds)
    {
        this.dtds = dtds;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#getDTDHandler()
     */
    public DTDHandler getDTDHandler()
    {
        return dtds;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler content)
    {
        this.content = content;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#getContentHandler()
     */
    public ContentHandler getContentHandler()
    {
        return content;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
     */
    public void setErrorHandler(ErrorHandler errors)
    {
        this.errors = errors;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#getErrorHandler()
     */
    public ErrorHandler getErrorHandler()
    {
        return errors;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
     */
    public void parse(InputSource is) throws IOException, SAXException
    {
        if (!(is instanceof SAXEventProviderInputSource))
            throw new SAXException("SAXEventProviderInputSource required");
            
        doc_in.provideSAXEvents(getContentHandler());
    }

    /* (non-Javadoc)
     * @see org.xml.sax.XMLReader#parse(java.lang.String)
     */
    public void parse(String arg0) throws IOException, SAXException
    {
        throw new SAXException("SAXEventProviderInputSource required");
    }

    private SAXEventProvider doc_in;
    private ErrorHandler errors;
    private ContentHandler content;
    private DTDHandler dtds;
    private EntityResolver entities;
}
