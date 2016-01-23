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
package org.crosswire.common.xml;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class XMLUtilTest {
    @Test
    public void testCleanAllEntities() {
        Assert.assertNull(XMLUtil.cleanAllEntities(null));

        Assert.assertEquals("", XMLUtil.cleanAllEntities(""));
        Assert.assertEquals("aa", XMLUtil.cleanAllEntities("aa"));
        Assert.assertEquals("<aa>", XMLUtil.cleanAllEntities("<aa>"));
        Assert.assertEquals("<aa>aa", XMLUtil.cleanAllEntities("<aa>aa"));
        Assert.assertEquals("<aa>aa;aa", XMLUtil.cleanAllEntities("<aa>aa;aa"));
        Assert.assertEquals(";", XMLUtil.cleanAllEntities(";"));

        Assert.assertEquals("aa &amp; aa", XMLUtil.cleanAllEntities("aa &amp; aa"));
        Assert.assertEquals("aa &amp;amp aa", XMLUtil.cleanAllEntities("aa &amp aa"));
        Assert.assertEquals("aa &amp;a-mp aa", XMLUtil.cleanAllEntities("aa &a-mp aa"));
        Assert.assertEquals("aa   aa", XMLUtil.cleanAllEntities("aa &am; aa"));
        Assert.assertEquals("aa &amp;am aa", XMLUtil.cleanAllEntities("aa &am aa"));
        Assert.assertEquals("aa &amp;", XMLUtil.cleanAllEntities("aa &amp;"));
        Assert.assertEquals("aa &amp;amp", XMLUtil.cleanAllEntities("aa &amp"));
        Assert.assertEquals("aa  ", XMLUtil.cleanAllEntities("aa &am;"));
        Assert.assertEquals("aa &amp;am", XMLUtil.cleanAllEntities("aa &am"));
        Assert.assertEquals("aa &amp;a", XMLUtil.cleanAllEntities("aa &a"));
        Assert.assertEquals("aa &amp;", XMLUtil.cleanAllEntities("aa &"));

        Assert.assertEquals("aa \u00A0 aa", XMLUtil.cleanAllEntities("aa &nbsp; aa"));
        Assert.assertEquals("aa &amp;nbsp aa", XMLUtil.cleanAllEntities("aa &nbsp aa"));
        Assert.assertEquals("aa &amp;nb-sp aa", XMLUtil.cleanAllEntities("aa &nb-sp aa"));
        Assert.assertEquals("aa   aa", XMLUtil.cleanAllEntities("aa &nb; aa"));
        Assert.assertEquals("aa &amp;nb aa", XMLUtil.cleanAllEntities("aa &nb aa"));

        Assert.assertEquals("-&amp;-\u00A0-&lt;-&gt;-&quot;-\u00A3-\u20AC-", XMLUtil.cleanAllEntities("-&amp;-&nbsp;-&lt;-&gt;-&quot;-&pound;-&euro;-"));
    }

    @Test
    public void testCleanAllTags() {
        Assert.assertNull(XMLUtil.cleanAllTags(null));

        Assert.assertEquals("", XMLUtil.cleanAllTags(""));
        Assert.assertEquals("aa", XMLUtil.cleanAllTags("aa"));
        Assert.assertEquals("aa &amp; aa", XMLUtil.cleanAllTags("aa &amp; aa"));

        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<a>"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<aa>"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("</aa>"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<aa wibble=\"wobble\">"));
        Assert.assertEquals(" keep ", XMLUtil.cleanAllTags("<aa>keep</aa>"));
        Assert.assertEquals(" keep ", XMLUtil.cleanAllTags("<aa>keep<aa>"));
        Assert.assertEquals(" ke ep", XMLUtil.cleanAllTags("<aa>ke<aa>ep"));
        Assert.assertEquals("ke  ep", XMLUtil.cleanAllTags("ke<aa><aa>ep"));
        Assert.assertEquals("ke  ep   ke  ep", XMLUtil.cleanAllTags("ke<aa><aa>ep<bb> <cc>ke<aa><aa>ep"));

        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<a"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<aa"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<aa;"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<\\"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<\\a"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<\\aa"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("<\\aa;"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("< "));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("< a"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("< aa"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("< aa;"));
        Assert.assertEquals(" ", XMLUtil.cleanAllTags("< aa>"));
        Assert.assertEquals("keep ", XMLUtil.cleanAllTags("keep<"));
        Assert.assertEquals("keep ", XMLUtil.cleanAllTags("keep<a"));
        Assert.assertEquals("keep ", XMLUtil.cleanAllTags("keep<aa"));
        Assert.assertEquals("keep ", XMLUtil.cleanAllTags("keep<aa dont=\"want\""));
        Assert.assertEquals("keep ", XMLUtil.cleanAllTags("keep<aa dont=\"want\" keep"));
        Assert.assertEquals("keep ", XMLUtil.cleanAllTags("keep<aa dont=\"want\" keep>"));
    }
}
