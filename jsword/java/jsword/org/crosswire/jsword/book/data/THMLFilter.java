
package org.crosswire.jsword.book.data;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.Element;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.crosswire.common.util.Logger;
import org.crosswire.common.xml.SAXUtil;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Item;
import org.crosswire.jsword.osis.Name;
import org.crosswire.jsword.osis.P;
import org.crosswire.jsword.osis.Seg;
import org.crosswire.jsword.osis.W;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
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
            StringReader in = new StringReader("<"+TAG_ROOT+">"+plain+"</"+TAG_ROOT+">");
            InputSource is = new InputSource(in);

            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            CustomHandler handler = new CustomHandler(ele);
            parser.parse(is, handler);
        }
        catch (SAXParseException ex)
        {
            throw new DataException(Msg.THML_BADTOKEN, ex, new Object[] { plain, ""+ex.getLineNumber(), ""+ex.getColumnNumber() });
        }
        catch (SAXException ex)
        {
            throw new DataException(Msg.THML_PARSE, ex, new Object[] { plain });
        }
        catch (ParserConfigurationException ex)
        {
            throw new DataException(Msg.THML_PARSE, ex, new Object[] { plain });
        }
        catch (IOException ex)
        {
            throw new DataException(Msg.THML_PARSE, ex, new Object[] { plain });
        }
    }

    private static final String TAG_ROOT = "root";
    private static final String TAG_BR = "br";
    private static final String TAG_I = "i";
    private static final String TAG_U = "i";
    private static final String TAG_TERM = "term";
    private static final String TAG_P = "p";
    private static final String TAG_SYNC = "sync";
    private static final String TAG_SCRIPREF = "scripRef";
    private static final String TAG_B = "b";
    private static final String TAG_FONT = "font";
    private static final String TAG_LI = "li";
    private static final String TAG_OL = "ol";

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
        public void startElement(String uri, String localname, String qname, Attributes attrs) throws SAXException
        {
            try
            {
                Element ele = (Element) stack.getFirst();

                if (qname.equals(TAG_ROOT))
                {
                    // We added this in the first place so ignore
                }
                else if (qname.equals(TAG_BR))
                {
                    // New line
                    P p = JAXBUtil.factory().createP();
                    JAXBUtil.getList(ele).add(p);
                }
                else if (qname.equals(TAG_I))
                {
                    // Italic
                    Seg seg = JAXBUtil.factory().createSeg();
                    seg.setType(SEG_ITALIC);
                    JAXBUtil.getList(ele).add(seg);
                }
                else if (qname.equals(TAG_B))
                {
                    // Bold
                    Seg seg = JAXBUtil.factory().createSeg();
                    seg.setType(SEG_BOLD);
                    JAXBUtil.getList(ele).add(seg);
                }
                else if (qname.equals(TAG_U))
                {
                    // Bold
                    Seg seg = JAXBUtil.factory().createSeg();
                    seg.setType(SEG_UNDERLINE);
                    JAXBUtil.getList(ele).add(seg);
                }
                else if (qname.equals(TAG_FONT))
                {
                    // Font
                    Seg seg = JAXBUtil.factory().createSeg();
                    String color = attrs.getValue("color");
                    if (color != null)
                    {
                        seg.setType(SEG_COLORPREFIX+color+";");
                    }
                    else
                    {
                        log.debug("Missing color attribute.");
                        SAXUtil.debugAttributes(attrs);
                    }
                    JAXBUtil.getList(ele).add(seg);
                }
                else if (qname.equals(TAG_TERM))
                {
                    // A term in a definition.
                    Name name = JAXBUtil.factory().createName();
                    JAXBUtil.getList(ele).add(name);
                }
                else if (qname.equals(TAG_P))
                {
                    // New line
                    P p = JAXBUtil.factory().createP();
                    JAXBUtil.getList(ele).add(p);
                }
                else if (qname.equals(TAG_LI))
                {
                    Item item = JAXBUtil.factory().createItem();
                    JAXBUtil.getList(ele).add(item);
                }
                else if (qname.equals(TAG_OL))
                {
                    org.crosswire.jsword.osis.List list = JAXBUtil.factory().createList();
                    JAXBUtil.getList(ele).add(list);
                }
                else if (qname.equals(TAG_SYNC))
                {
                    // Strongs reference
                    String type = attrs.getValue("type");
                    String value = attrs.getValue("value");
                    if ("Strongs".equals(type))
                    {
                        W w = JAXBUtil.factory().createW();
                        w.setLemma("x-Strongs:"+value);
                        JAXBUtil.getList(ele).add(w);
                    }
                    else if ("Dict".equals(type))
                    {
                        Div div = JAXBUtil.factory().createDiv();
                        div.setOsisID("dict://"+value);
                        JAXBUtil.getList(ele).add(div);
                    }
                    else
                    {
                        log.warn("sync tag has type="+type+" when value="+value);
                    }
                }
                else if (qname.equals(TAG_SCRIPREF))
                {
                    Div div = JAXBUtil.factory().createDiv();

                    String refstr = attrs.getValue("passage");
                    if (refstr != null)
                    {
                        try
                        {
                            Passage ref = PassageFactory.createPassage(refstr);
                            String osisname = ref.getOSISName();
                            div.setOsisID(osisname);
                        }
                        catch (NoSuchVerseException ex)
                        {
                            log.warn("Unparsable passage:"+refstr+" due to "+ex.getMessage());
                        }
                    }
                    else
                    {
                        log.warn("Missing passage.");
                        SAXUtil.debugAttributes(attrs);
                    }

                    JAXBUtil.getList(ele).add(div);
                }
                else
                {
                    log.warn("unknown thml element: "+localname+" qname="+qname);
                }
            }
            catch (JAXBException ex)
            {
                throw new SAXException(ex);
            }
        }
    
        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String uri, String localname, String qname) throws SAXException
        {
            try
            {
                if (qname.equals(TAG_ROOT))
                {
                    // We added this in the first place so ignore
                }
                else if (qname.equals(TAG_BR))
                {
                    // Processed on the way in so ignore
                }
                else if (qname.equals(TAG_I))
                {
                    // No need for special treatment on the way out?
                }
                else if (qname.equals(TAG_B))
                {
                    // No need for special treatment on the way out?
                }
                else if (qname.equals(TAG_FONT))
                {
                    // No need for special treatment on the way out?
                }
                else if (qname.equals(TAG_TERM))
                {
                    // No need for special treatment on the way out?
                }
                else if (qname.equals(TAG_P))
                {
                    // No need for special treatment on the way out?
                }
                else if (qname.equals(TAG_LI))
                {
                    // No need for special treatment on the way out?
                }
                else if (qname.equals(TAG_OL))
                {
                    // No need for special treatment on the way out?
                }
                else if (qname.equals(TAG_SYNC))
                {
                    // No need for special treatment on the way out?
                }
                else if (qname.equals(TAG_SCRIPREF))
                {
                    // No need for special treatment on the way out?
                }
                else
                {
                    // Warned on the way in so ignore
                }
            }
            catch (Exception ex)
            {
                throw new SAXException(ex);
            }
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
