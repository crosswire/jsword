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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.diff.Diff;
import org.crosswire.common.diff.DiffCleanup;
import org.crosswire.common.diff.Difference;
import org.crosswire.common.util.Language;
import org.crosswire.common.xml.JDOMSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.passage.Key;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;

/**
 * BookData is the assembler of the OSIS that is returned by the filters. As
 * such it puts that into an OSIS document. When several books are supplied, it
 * gets the data from each and puts it into a parallel or interlinear view.
 * Note: it is critical that all the books are able to understand the same key.
 * That does not mean that each has to have content for each key. Missing keys
 * are represented by empty cells.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BookData implements BookProvider {
    /**
     * Ctor
     */
    public BookData(Book book, Key key) {
        assert book != null;
        assert key != null;

        this.key = key;

        books = new Book[1];
        books[0] = book;
    }

    /**
     * Create BookData for multiple books.
     */
    public BookData(Book[] books, Key key, boolean compare) {
        assert books != null && books.length > 0;
        assert key != null;

        this.books = books.clone();
        this.key = key;
        this.comparingBooks = compare;
    }

    /**
     * Accessor for the root OSIS element
     */
    public Element getOsis() throws BookException {
        if (osis == null) {
            // TODO(DMS): Determine the proper representation of the OSISWork
            // name for multiple books.
            osis = OSISUtil.createOsisFramework(getFirstBook().getBookMetaData());
            Element text = osis.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);
            Element div = getOsisFragment();
            text.addContent(div);
        }

        return osis;
    }

    /**
     * Accessor for the root OSIS element
     */
    public Element getOsisFragment() throws BookException {
        if (fragment == null) {
            fragment = getOsisContent();
        }

        return fragment;
    }

    /**
     * Output the current data as a SAX stream.
     * 
     * @return A way of posting SAX events
     */
    public SAXEventProvider getSAXEventProvider() throws BookException {
        // If the fragment is already in a document, then use that.
        Element frag = getOsisFragment();
        Document doc = frag.getDocument();
        if (doc == null) {
            doc = new Document(frag);
        }
        return new JDOMSAXEventProvider(doc);
    }

    /**
     * Who created this data.
     * 
     * @return Returns the book.
     */
    public Book[] getBooks() {
        return books == null ? null : (Book[]) books.clone();
    }

    /**
     * Get the first book.
     */
    public Book getFirstBook() {
        return books != null && books.length > 0 ? books[0] : null;
    }

    /**
     * The key used to obtain data from one or more books.
     * 
     * @return Returns the key.
     */
    public Key getKey() {
        return key;
    }

    /**
     * @return whether the books should be compared.
     */
    public boolean isComparingBooks() {
        return comparingBooks;
    }

    private Element getOsisContent() throws BookException {
        Element div = OSISUtil.factory().createDiv();

        if (books.length == 1) {
            Iterator<Content> iter = books[0].getOsisIterator(key, false);
            while (iter.hasNext()) {
                Content content = iter.next();
                div.addContent(content);
            }
        } else {
            Element table = OSISUtil.factory().createTable();
            Element row = OSISUtil.factory().createRow();
            Element cell = null;

            table.addContent(row);

            Iterator<Content>[] iters = new Iterator[books.length];
            boolean[] showDiffs = new boolean[books.length - 1];
            boolean doDiffs = false;

            for (int i = 0; i < books.length; i++) {
                Book book = books[i];

                cell = OSISUtil.factory().createHeaderCell();

                if (i > 0) {
                    Book prevBook = books[i - 1];
                    BookCategory category = book.getBookCategory();

                    BookCategory prevCategory = prevBook.getBookCategory();
                    String prevName = prevBook.getInitials();
                    showDiffs[i - 1] = comparingBooks && BookCategory.BIBLE.equals(category) && category.equals(prevCategory)
                            && book.getLanguage().equals(prevBook.getLanguage()) && !book.getInitials().equals(prevName);

                    if (showDiffs[i - 1]) {
                        doDiffs = true;
                        StringBuilder buf = new StringBuilder(prevBook.getInitials());
                        buf.append(" ==> ");
                        buf.append(book.getInitials());

                        cell.addContent(OSISUtil.factory().createText(buf.toString()));
                        row.addContent(cell);
                        cell = OSISUtil.factory().createHeaderCell();
                    }
                }

                cell.addContent(OSISUtil.factory().createText(book.getInitials()));
                row.addContent(cell);

                iters[i] = book.getOsisIterator(key, true);
            }

            Content content = null;

            int cellCount = 0;
            int rowCount = 0;
            while (true) {
                cellCount = 0;

                row = OSISUtil.factory().createRow();

                String lastText = "";

                for (int i = 0; i < iters.length; i++) {
                    Book book = books[i];
                    cell = OSISUtil.factory().createCell();
                    Language lang = (Language) book.getProperty(BookMetaData.KEY_XML_LANG);
                    cell.setAttribute(OSISUtil.OSIS_ATTR_LANG, lang.getCode(), Namespace.XML_NAMESPACE);

                    row.addContent(cell);

                    StringBuilder newText = new StringBuilder(doDiffs ? 32 : 0);

                    if (iters[i].hasNext()) {
                        List<Content> contents = new ArrayList<Content>(1);

                        do {
                            content = iters[i].next();
                            contents.add(content);
                            addText(doDiffs, newText, content);
                        } while (!isNextVerse(content));

                        if (doDiffs) {
                            String thisText = newText.toString();
                            if (unaccenter != null) {
                                thisText = unaccenter.unaccent(thisText);
                            }

                            if (i > 0 && showDiffs[i - 1]) {
                                List<Difference> diffs = new Diff(lastText, thisText, false).compare();
                                DiffCleanup.cleanupSemantic(diffs);
                                cell.addContent(OSISUtil.diffToOsis(diffs));

                                // Since we used that cell create another
                                cell = OSISUtil.factory().createCell();
                                lang = (Language) book.getProperty(BookMetaData.KEY_XML_LANG);
                                cell.setAttribute(OSISUtil.OSIS_ATTR_LANG, lang.getCode(), Namespace.XML_NAMESPACE);
                                row.addContent(cell);
                            }
                            lastText = thisText;
                        }
                        cell.addContent(contents);
                        cellCount++;
                    }
                }

                if (cellCount == 0) {
                    break;
                }

                table.addContent(row);
                rowCount++;
            }
            if (rowCount > 0) {
                div.addContent(table);
            }
        }

        return div;
    }

    private boolean isNextVerse(Content content) {
        if (content instanceof Element) {
            return OSISUtil.OSIS_ELEMENT_VERSE.equals(((Element) content).getName());
        }

        return false;
    }

    private void addText(boolean doDiffs, StringBuilder newText, Content content) {
        if (doDiffs) {
            // if we already have content, let's add a space to avoid chaining words together
            if (newText.length() != 0) {
                newText.append(' ');
            }

            if (content instanceof Element) {
                newText.append(OSISUtil.getCanonicalText((Element) content));
            } else if (content instanceof Text) {
                newText.append(((Text) content).getText());
            }
        }
    }

    /**
     * @param unaccenter the unaccenter to set
     */
    public void setUnaccenter(UnAccenter unaccenter) {
        this.unaccenter = unaccenter;
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
     * Whether the Books should be compared.
     */
    private boolean comparingBooks;

    /**
     * The complete OSIS container for the element
     */
    private Element osis;

    /**
     * Just the element
     */
    private Element fragment;

    private UnAccenter unaccenter;
}
