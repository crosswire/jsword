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
package org.crosswire.jsword.book;

import java.util.Iterator;

import org.crosswire.common.xml.JDOMSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.passage.Key;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Basic section of BookData.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookData
{
    /**
     * Ctor
     */
    public BookData(Book book, Key key)
    {
        this.book = book;
        this.key = key;
    }

    /**
     * Accessor for the root OSIS element
     */
    public Element getOsis() throws BookException
    {
        if (osis == null)
        {
            osis = OSISUtil.createOsisFramework(book.getBookMetaData());
            Element text = osis.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);
            Element div = OSISUtil.factory().createDiv();
            text.addContent(div);

            Iterator iter = book.getOsisIterator(key, false);
            while (iter.hasNext())
            {
                Content content = (Content) iter.next();
                div.addContent(content);
            }
        }

        return osis;
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
    public SAXEventProvider getSAXEventProvider() throws BookException
    {
        return new JDOMSAXEventProvider(new Document(getOsis()));
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
     * The complete osis container for the element
     */
    private Element osis;
}
