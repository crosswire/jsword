package org.crosswire.common.xml;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class XMLUtilTest extends TestCase
{
    public void testCleanInvalidCharacters()
    {
        assertNull(XMLUtil.cleanInvalidCharacters(null));

        assertEquals("", XMLUtil.cleanInvalidCharacters(""));
        assertEquals(" one two three four five ", XMLUtil.cleanInvalidCharacters(" one two three four five "));
        assertEquals("!\"£$%^&*() -=_+", XMLUtil.cleanInvalidCharacters("!\"£$%^&*() -=_+"));
        assertEquals("{}[]:@~;'#<>?,./", XMLUtil.cleanInvalidCharacters("{}[]:@~;\'#<>?,./"));
        assertEquals("ONETWOTHREEZZ", XMLUtil.cleanInvalidCharacters("ONETWOTHREEZZ"));
        assertEquals("1234567890", XMLUtil.cleanInvalidCharacters("1234567890"));
        assertEquals("\u00a0\u20ac", XMLUtil.cleanInvalidCharacters("\u00a0\u20ac"));
        assertEquals("nul-:bel-:tab-\t:cr-\r:lf-\n:last-:space-\u0020:", XMLUtil.cleanInvalidCharacters("nul-\u0000:bel-\u0007:tab-\t:cr-\r:lf-\n:last-\u001f:space-\u0020:"));
    }
    
    public void testCleanAllEntities()
    {
        assertNull(XMLUtil.cleanAllEntities(null));

        assertEquals("", XMLUtil.cleanAllEntities(""));
        assertEquals("aa", XMLUtil.cleanAllEntities("aa"));
        assertEquals("<aa>", XMLUtil.cleanAllEntities("<aa>"));
        assertEquals("<aa>aa", XMLUtil.cleanAllEntities("<aa>aa"));
        assertEquals("<aa>aa;aa", XMLUtil.cleanAllEntities("<aa>aa;aa"));
        assertEquals(";", XMLUtil.cleanAllEntities(";"));

        assertEquals("aa &#38; aa", XMLUtil.cleanAllEntities("aa &amp; aa"));
        assertEquals("aa &#38; aa", XMLUtil.cleanAllEntities("aa &amp aa"));
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &a-mp aa"));
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &am; aa"));
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &am aa"));
        assertEquals("aa &#38;", XMLUtil.cleanAllEntities("aa &amp;"));
        assertEquals("aa &#38;", XMLUtil.cleanAllEntities("aa &amp"));
        assertEquals("aa ", XMLUtil.cleanAllEntities("aa &am;"));
        assertEquals("aa ", XMLUtil.cleanAllEntities("aa &am"));
        assertEquals("aa ", XMLUtil.cleanAllEntities("aa &a"));
        assertEquals("aa ", XMLUtil.cleanAllEntities("aa &"));

        assertEquals("aa &#160; aa", XMLUtil.cleanAllEntities("aa &nbsp; aa"));
        assertEquals("aa &#160; aa", XMLUtil.cleanAllEntities("aa &nbsp aa"));
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &nb-sp aa"));
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &nb; aa"));
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &nb aa"));

        assertEquals("-&#38;-&#160;-&#60;-&#62;-&#34;-&#163;-&#8364;-", XMLUtil.cleanAllEntities("-&amp;-&nbsp;-&lt;-&gt;-&quot;-&pound;-&euro;-"));
    }

    public void testCleanAllTags()
    {
        assertNull(XMLUtil.cleanAllTags(null));

        assertEquals("", XMLUtil.cleanAllTags(""));
        assertEquals("aa", XMLUtil.cleanAllTags("aa"));
        assertEquals("aa &amp; aa", XMLUtil.cleanAllTags("aa &amp; aa"));

        assertEquals("", XMLUtil.cleanAllTags("<a>"));
        assertEquals("", XMLUtil.cleanAllTags("<aa>"));
        assertEquals("", XMLUtil.cleanAllTags("</aa>"));
        assertEquals("", XMLUtil.cleanAllTags("<aa wibble=\"wobble\">"));
        assertEquals("keep", XMLUtil.cleanAllTags("<aa>keep</aa>"));
        assertEquals("keep", XMLUtil.cleanAllTags("<aa>keep<aa>"));
        assertEquals("keep", XMLUtil.cleanAllTags("<aa>ke<aa>ep"));
        assertEquals("keep", XMLUtil.cleanAllTags("ke<aa><aa>ep"));
        assertEquals("keep keep", XMLUtil.cleanAllTags("ke<aa><aa>ep<bb> <cc>ke<aa><aa>ep"));

        assertEquals("", XMLUtil.cleanAllTags("<"));
        assertEquals("", XMLUtil.cleanAllTags("<a"));
        assertEquals("", XMLUtil.cleanAllTags("<aa"));
        assertEquals("", XMLUtil.cleanAllTags("<aa;"));
        assertEquals("", XMLUtil.cleanAllTags("<\\"));
        assertEquals("", XMLUtil.cleanAllTags("<\\a"));
        assertEquals("", XMLUtil.cleanAllTags("<\\aa"));
        assertEquals("", XMLUtil.cleanAllTags("<\\aa;"));
        assertEquals("", XMLUtil.cleanAllTags("< "));
        assertEquals("", XMLUtil.cleanAllTags("< a"));
        assertEquals("", XMLUtil.cleanAllTags("< aa"));
        assertEquals("", XMLUtil.cleanAllTags("< aa;"));
        assertEquals("", XMLUtil.cleanAllTags("< aa>"));
        assertEquals("keep", XMLUtil.cleanAllTags("keep<"));
        assertEquals("keep", XMLUtil.cleanAllTags("keep<a"));
        assertEquals("keep", XMLUtil.cleanAllTags("keep<aa"));
        assertEquals("keep", XMLUtil.cleanAllTags("keep<aa dont=\"want\""));
        assertEquals("keep", XMLUtil.cleanAllTags("keep<aa dont=\"want\" keep"));
        assertEquals("keep", XMLUtil.cleanAllTags("keep<aa dont=\"want\" keep>"));
    }
}
