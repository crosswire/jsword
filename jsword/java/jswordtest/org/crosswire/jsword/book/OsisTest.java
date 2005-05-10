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
package org.crosswire.jsword.book;

import junit.framework.TestCase;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class OsisTest extends TestCase
{
    public OsisTest(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testManual() throws Exception
    {
        Element seg = OSISUtil.factory().createSeg();
        seg.addContent("In the beginning God created the heaven and the earth."); //$NON-NLS-1$

        Element verse = OSISUtil.factory().createVerse();
        verse.setAttribute(OSISUtil.ATTRIBUTE_VERSE_OSISID, "Gen.1.1"); //$NON-NLS-1$
        verse.addContent(seg);

        Element div = OSISUtil.factory().createDiv();
        div.setAttribute("type", "chapter"); //$NON-NLS-1$ //$NON-NLS-2$
        div.setAttribute("osisID", "Gen.1.1"); //$NON-NLS-1$ //$NON-NLS-2$
        div.addContent(verse);

        Element work = OSISUtil.factory().createWork();

        Element header = OSISUtil.factory().createHeader();
        header.addContent(work);

        Element osistext = OSISUtil.factory().createOsisText();
        osistext.setAttribute(OSISUtil.ATTRIBUTE_OSISTEXT_OSISIDWORK, "Bible.KJV"); //$NON-NLS-1$
        osistext.addContent(header);
        osistext.addContent(div);

        Element blank = OSISUtil.factory().createOsis();
        blank.addContent(osistext);

        // create a Marshaller and marshal to System.out
        outputter.output(blank, System.out);
    }

    private XMLOutputter outputter = new XMLOutputter();
}
