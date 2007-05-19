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
 * BookData is the assembler of the osis that is returned by the filters.
 * As such it puts that into an OSIS document. When several books are
 * supplied, it gets the data from each and puts it into a parallel or
 * interlinear view.
 * Note: it is critical that all the books are able to understand the same key.
 * That does not mean that each has to have content for each key. Missing keys
 * are represented by empty cells.
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
        assert book != null;
        assert key != null;

        this.key = key;

        Book defaultBible = Defaults.getBible();
        if (defaultBible != null &&
            BookCategory.BIBLE.equals(book.getBookCategory()) &&
            !defaultBible.equals(book)
           )
        {
            books = new Book[2];
            books[0] = defaultBible;
            books[1] = book;
        }
        else
        {
            books = new Book[1];
            books[0] = book;
        }
    }

    /**
     * Create BookData for multiple books.
     */
    public BookData(Book[] books, Key key)
    {
        assert books != null && books.length > 0;
        assert key != null;

        this.books = (Book[]) books.clone();
        this.key = key;
    }

    /**
     * Accessor for the root OSIS element
     */
    public Element getOsis() throws BookException
    {
        if (osis == null)
        {
            // TODO(DMS): Determine the proper representation of the OSISWork name for multiple books.
            osis = OSISUtil.createOsisFramework(books[0].getBookMetaData());
            Element text = osis.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);
            Element div = getOsisContent();
            text.addContent(div);
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
        return books[0];
    }

    /**
     * The key used to obtain data from one or more books.
     * @return Returns the key.
     */
    public Key getKey()
    {
        return key;
    }

    private Element getOsisContent() throws BookException
    {
        Element div = OSISUtil.factory().createDiv();

        if (books.length == 1)
        {
            Iterator iter = books[0].getOsisIterator(key, false);
            while (iter.hasNext())
            {
                Content content = (Content) iter.next();
                div.addContent(content);
            }
        }
        else
        {
            Iterator[] iters = new Iterator[books.length];
            for (int i = 0; i < books.length; i++)
            {
                iters[i] = books[i].getOsisIterator(key, true);
            }

            Content content = null;
            Element table = OSISUtil.factory().createTable();
            Element row = null;
            Element cell = null;
            int cellCount = 0;
            int rowCount = 0;
            while (true)
            {
                cellCount = 0;

                row = OSISUtil.factory().createRow();

                for (int i = 0; i < iters.length; i++)
                {
                    cell = OSISUtil.factory().createCell();
                    row.addContent(cell);
                    if (iters[i].hasNext())
                    {
                        content = (Content) iters[i].next();
                        cell.addContent(content);
                        cellCount++;
                    }
                }

                if (cellCount == 0)
                {
                    break;
                }

                table.addContent(row);
                rowCount++;
            }
            if (rowCount > 0)
            {
                div.addContent(table);
            }
        }

        return div;
    }

    /**
     * What key was used to create this data
     */
    private Key key;

    /**
     * The books to which the key should be applied.
     */
    private Book[] books;

    /**
     * The complete osis container for the element
     */
    private Element osis;
}
