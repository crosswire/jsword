
package org.crosswire.jsword.book.data;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.Element;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.crosswire.common.util.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Filter to convert THML to OSIS format.
 * <br/>I used the THML ref page: {@link http://www.ccel.org/ThML/ThML1.04.htm}
 * to work out what the tags meant.
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
public class THMLFilter implements Filter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.data.Filter#toOSIS(org.crosswire.jsword.book.data.BookDataListener, java.lang.String)
     */
    public void toOSIS(Element ele, String plain) throws DataException
    {
        try
        {
            // create a root element to house our document fragment
            StringReader in = new StringReader("<root>"+plain+"</root>");
            InputSource is = new InputSource(in);

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            CustomHandler handler = new CustomHandler(ele);
            parser.parse(is, handler);
        }
        catch (Exception ex)
        {
            throw new DataException(Msg.THML_BADTOKEN, ex);
        }
    }

    private static final String TAG_ROOT = "root";

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(THMLFilter.class);

    /**
     * To convert SAX events into OSIS events
     */
    private static class CustomHandler extends DefaultHandler
    {
        /**
         * Simple ctor
         */
        public CustomHandler(Element ele)
        {
            stack.addFirst(ele);
        }

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endDocument()
         */
        public void endDocument() throws SAXException
        {
            stack.removeFirst();
        }

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String uri, String localname, String qname, Attributes attr) throws SAXException
        {
            if (qname.equals(TAG_ROOT))
            {
                // we added this in the first place so ignore
                return;
            }

            log.warn("unknown thml element: "+localname+" qname="+qname);
        }
        
        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String uri, String localname, String qname) throws SAXException
        {
            if (qname.equals(TAG_ROOT))
            {
                // we added this in the first place so ignore
                return;
            }

            log.warn("unknown thml element: "+localname+" qname="+qname);
        }

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        public void characters(char[] data, int offset, int length) throws SAXException
        {
            // What we are adding to
            Element current = (Element) stack.getFirst();
            List list = JAXBUtil.getList(current); 

            // what we are adding
            String text = new String(data, offset, length);

            // If the last element in the list is a string then we should add
            // this string on to the end of it rather than add a new list item
            // because (probably as an atrifact of the HTML/XSL transform we get
            // a space inserted in the output even when 2 calls to this method
            // split a word.
            if (list.size() > 0)
            {
                Object last = list.get(list.size()-1);
                if (last instanceof String)
                {
                    list.remove(list.size()-1);
                    text = ((String) last) + text; 
                }
            }

            list.add(text);
        }

        private LinkedList stack = new LinkedList();
    }
}
