
package org.crosswire.jsword.book.data;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.crosswire.common.util.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class OSISFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.Filter#toOSIS(org.crosswire.jsword.book.data.BookDataListener, java.lang.String)
     */
    public void toOSIS(BookDataListener li, String plain) throws FilterException
    {
        try
        {
            // create a root element to house our document fragment
            StringReader in = new StringReader("<root>"+plain+"</root>");
            InputSource is = new InputSource(in);
            
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            CustomHandler handler = new CustomHandler(li);
            parser.parse(is, handler);
        }
        catch (Exception ex)
        {
            throw new FilterException(Msg.THML_BADTOKEN, ex);
        }
    }

    private static final String TAG_FOO = "foo";

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(OSISFilter.class);

    /**
     * To convert SAX events into OSIS events
     */
    private static class CustomHandler extends DefaultHandler
    {
        /**
         * Simple ctor
         */
        public CustomHandler(BookDataListener li)
        {
            this.li = li;
        }

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException
        {
            if (localname == null)
            {
                log.warn("localname is null");
                return;
            }

            if (localname.equals(TAG_FOO))
            {
                li.startTitle();
            }
        }
        
        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        public void characters(char[] data, int offset, int length) throws SAXException
        {
            String text = new String(data, offset, length);

            log.debug("found text="+text);
            li.addText(text);
        }

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String uri, String localname, String qname) throws SAXException
        {
            if (localname == null)
            {
                log.warn("localname is null");
                return;
            }

            if (localname.equals(TAG_FOO))
            {
                li.endTitle();
            }
        }

        private BookDataListener li;
    }
}
