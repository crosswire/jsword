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

        assertEquals("", XMLUtil.cleanInvalidCharacters("")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(" one two three four five ", XMLUtil.cleanInvalidCharacters(" one two three four five ")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("!\"$%^&*() -=_+", XMLUtil.cleanInvalidCharacters("!\"$%^&*() -=_+")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("{}[]:@~;'#<>?,./", XMLUtil.cleanInvalidCharacters("{}[]:@~;\'#<>?,./")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("ONETWOTHREEZZ", XMLUtil.cleanInvalidCharacters("ONETWOTHREEZZ")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("1234567890", XMLUtil.cleanInvalidCharacters("1234567890")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("\u00a0\u20ac", XMLUtil.cleanInvalidCharacters("\u00a0\u20ac")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("nul-:bel-:tab-\t:cr-\r:lf-\n:last-:space-\u0020:", XMLUtil.cleanInvalidCharacters("nul-\u0000:bel-\u0007:tab-\t:cr-\r:lf-\n:last-\u001f:space-\u0020:")); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testCleanAllEntities()
    {
        assertNull(XMLUtil.cleanAllEntities(null));

        assertEquals("", XMLUtil.cleanAllEntities("")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa", XMLUtil.cleanAllEntities("aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("<aa>", XMLUtil.cleanAllEntities("<aa>")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("<aa>aa", XMLUtil.cleanAllEntities("<aa>aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("<aa>aa;aa", XMLUtil.cleanAllEntities("<aa>aa;aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(";", XMLUtil.cleanAllEntities(";")); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("aa &#38; aa", XMLUtil.cleanAllEntities("aa &amp; aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa &#38; aa", XMLUtil.cleanAllEntities("aa &amp aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &a-mp aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &am; aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &am aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa &#38;", XMLUtil.cleanAllEntities("aa &amp;")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa &#38;", XMLUtil.cleanAllEntities("aa &amp")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa ", XMLUtil.cleanAllEntities("aa &am;")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa ", XMLUtil.cleanAllEntities("aa &am")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa ", XMLUtil.cleanAllEntities("aa &a")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa ", XMLUtil.cleanAllEntities("aa &")); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("aa &#160; aa", XMLUtil.cleanAllEntities("aa &nbsp; aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa &#160; aa", XMLUtil.cleanAllEntities("aa &nbsp aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &nb-sp aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &nb; aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa  aa", XMLUtil.cleanAllEntities("aa &nb aa")); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("-&#38;-&#160;-&#60;-&#62;-&#34;-&#163;-&#8364;-", XMLUtil.cleanAllEntities("-&amp;-&nbsp;-&lt;-&gt;-&quot;-&pound;-&euro;-")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testCleanAllTags()
    {
        assertNull(XMLUtil.cleanAllTags(null));

        assertEquals("", XMLUtil.cleanAllTags("")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa", XMLUtil.cleanAllTags("aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("aa &amp; aa", XMLUtil.cleanAllTags("aa &amp; aa")); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("", XMLUtil.cleanAllTags("<a>")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("<aa>")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("</aa>")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("<aa wibble=\"wobble\">")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("<aa>keep</aa>")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("<aa>keep<aa>")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("<aa>ke<aa>ep")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("ke<aa><aa>ep")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep keep", XMLUtil.cleanAllTags("ke<aa><aa>ep<bb> <cc>ke<aa><aa>ep")); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("", XMLUtil.cleanAllTags("<")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("<a")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("<aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("<aa;")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("<\\")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("<\\a")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("<\\aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("<\\aa;")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("< ")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("< a")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("< aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("< aa;")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("", XMLUtil.cleanAllTags("< aa>")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("keep<")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("keep<a")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("keep<aa")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("keep<aa dont=\"want\"")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("keep<aa dont=\"want\" keep")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("keep", XMLUtil.cleanAllTags("keep<aa dont=\"want\" keep>")); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
