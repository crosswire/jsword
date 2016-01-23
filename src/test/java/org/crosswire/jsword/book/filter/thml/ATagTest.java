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
 * © CrossWire Bible Society, 2014 - 2016
 *
 */
package org.crosswire.jsword.book.filter.thml;

import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class ATagTest {

    @Test
    public void testReference() {
        try {
            THMLFilter thmlFilter = new THMLFilter();
            Book dummyBook = Books.installed().getBook("KJV");
            List<Content> out = thmlFilter.toOSIS(dummyBook, dummyBook.getKey("Gen.1.1"), "<a href=\"sword://StrongsRealGreek/01909\">1909</a>");
            Assert.assertEquals("THML reference not handled correctly", "<reference osisRef=\"sword://StrongsRealGreek/01909\">1909</reference>", new XMLOutputter().outputString((Element) out.get(0)));
        } catch (NoSuchKeyException e) {
            Assert.fail();
        }
    }

}
