package org.crosswire.jsword.book;

import java.util.Iterator;
import java.util.List;

import org.crosswire.common.xml.JDOMSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.passage.Key;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXHandler;
import org.xml.sax.SAXException;

/**
 * Basic section of BookData.
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
public class BookData
{
    /**
     * Ctor
     */
    public BookData(Element osis, Book book, Key key)
    {
        this.osis = osis;
        this.book = book;
        this.key = key;
    }

    /**
     * Create a BibleData from a SAXEventProvider
     */
    public BookData(SAXEventProvider provider, Book book, Key key) throws SAXException
    {
        SAXHandler handler = new SAXHandler();
        provider.provideSAXEvents(handler);
        this.osis = handler.getDocument().getRootElement();
        this.book = book;
        this.key = key;
    }

    /**
     * Accessor for the root OSIS element
     */
    public Element getOsis()
    {
        return osis;
    }

    /**
     * A simplified plain text version of the data in this document with all
     * the markup stripped out.
     * @return The Bible text without markup
     */
    public String getPlainText()
    {
        StringBuffer buffer = new StringBuffer();

        Element osisText = getOsis().getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);
        List divs = osisText.getChildren(OSISUtil.OSIS_ELEMENT_DIV);

        for (Iterator oit = divs.iterator(); oit.hasNext(); )
        {
            Element div = (Element) oit.next();

            Iterator dit = div.getContent().iterator();
            while (dit.hasNext())
            {
                Object data = dit.next();
                if (data instanceof Element)
                {
                    Element ele = (Element) data;
                    if (ele.getName().equals(OSISUtil.OSIS_ELEMENT_VERSE))
                    {
                        String txt = OSISUtil.getPlainText((Element) data);
                        buffer.append(txt);
                    }
                }
            }
        }

        return buffer.toString().trim();
    }

    /**
     * Check that a BibleData is valid.
     * Currently, this does nothing, and isn't used. it was broken when we used
     * JAXB, however it wasn't much use then becuase JAXB did a lot to keep the
     * document valid anyway. Under JDOM there is more point, but I don't think
     * JDOM supports this out of the box.
     */
    public void validate()
    {
    }

    /**
     * Output the current data as a SAX stream.
     * @return A way of posting SAX events
     */
    public SAXEventProvider getSAXEventProvider()
    {
        return new JDOMSAXEventProvider(new Document(osis));
    }

    /**
     * Who created this data.
     * @return Returns the book.
     */
    public Book getBook()
    {
        return book;
    }

    /**
     * What key was used to create this data.
     * It should be true that bookdata.getBook().getBookData(bookdata.getKey())
     * equals (but not necessarily ==) the original bookdata.
     * @return Returns the key.
     */
    public Key getKey()
    {
        return key;
    }

    /**
     * Who created this data
     */
    private Book book;

    /**
     * What key was used to create this data
     */
    private Key key;

    /**
     * The root where we read data from
     */
    private Element osis;
}
