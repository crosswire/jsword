/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.filter.thml;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Element;
import org.xml.sax.Attributes;

/**
 * THML Tag interface - there should be one implementation of this class for
 * each THML tag.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface Tag {
    /**
     * What element does this class represent. For example the Tag that
     * represents the font element would return the string "font".
     * @return the element's tag name
     */
    String getTagName();

    /**
     * Make changes to the specified OSIS element given the attributes passed in
     * the source document.
     * 
     * @param book the book
     * @param key the key
     * @param ele
     *            The OSIS element to use as a parent
     * @param attrs
     *            The source document attributes.
     * @return the element to which content is attached
     */
    Element processTag(Book book, Key key, Element ele, Attributes attrs);

    /**
     * Do additional processing of the tag after the element has been created.
     * 
     * @param book the book
     * @param key the key
     * @param ele
     *            the created element to process
     */
    void processContent(Book book, Key key, Element ele);
}
