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
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
     * Return the text without any extra material.
     * @return The Book's text without markup
     */
    public String getCanonicalText()
    {
        return OSISUtil.getCanonicalText(getOsis());
    }

    /**
     * A simplified plain text version of the data in this document with all
     * the markup stripped out. This is not as simple as it seems.
     * @return The text without markup
     */
    public String getPlainText()
    {
        return OSISUtil.getPlainText(getOsis());
    }

    /**
     * Return just the Strong's numbers.
     * @return The Book's Strong's numbers as a space separated string.
     */
    public String getStrongsNumbers()
    {
        return OSISUtil.getStrongsNumbers(getOsis());
    }

    /**
     * Return just the scripture references in the book.
     * @return The Book's scripture references
     */
    public String getReferences()
    {
        return OSISUtil.getReferences(getOsis());
    }

    /**
     * Return just the notes in the book.
     * @return The Book's notes
     */
    public String getNotes()
    {
        return OSISUtil.getNotes(getOsis());
    }

    /**
     * Return just the headings, both canonical and non-canonical, in the book.
     * @return The Book's headings
     */
    public String getHeadings()
    {
        return OSISUtil.getHeadings(getOsis());
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
