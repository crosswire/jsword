/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.book.filter.thml;

import org.jdom.Element;
import org.xml.sax.Attributes;

/**
 * THML Tag interface - there should be one implementation of this class for
 * each THML tag.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface Tag
{
    /**
     * What element does this class represent.
     * For example the Tag that represents the &gtfont ...> element would return
     * the string "font".
     */
    public String getTagName();

    /**
     * Make changes to the specified OSIS element given the attributes passed
     * in the source document.
     * @param ele The OSIS element to use as a parent
     * @param attrs The source document attributes.
     * @return the element to which content is attached
     */
    public Element processTag(Element ele, Attributes attrs);
}
