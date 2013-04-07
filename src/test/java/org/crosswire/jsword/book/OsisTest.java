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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book;

import junit.framework.TestCase;

import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import java.io.ByteArrayOutputStream;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class OsisTest extends TestCase {
    public OsisTest(String s) {
        super(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
    }

    public void testManual() throws Exception {
        Element seg = OSISUtil.factory().createSeg();
        seg.addContent("In the beginning God created the heaven and the earth.");

        Element verse = OSISUtil.factory().createVerse();
        verse.setAttribute(OSISUtil.OSIS_ATTR_OSISID, "Gen.1.1");
        verse.addContent(seg);

        Element div = OSISUtil.factory().createDiv();
        div.setAttribute("type", "chapter");
        div.setAttribute("osisID", "Gen.1.1");
        div.addContent(verse);

        Element work = OSISUtil.factory().createWork();

        Element header = OSISUtil.factory().createHeader();
        header.addContent(work);

        Element osistext = OSISUtil.factory().createOsisText();
        osistext.setAttribute(OSISUtil.ATTRIBUTE_OSISTEXT_OSISIDWORK, "Bible.KJV");
        osistext.addContent(header);
        osistext.addContent(div);

        Element blank = OSISUtil.factory().createOsis();
        blank.addContent(osistext);

        // create a Marshaller and marshal to System.out
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        outputter.output(blank, out);
        assertTrue(out.toString().trim().length() > 0);
    }

    private XMLOutputter outputter = new XMLOutputter();
}
