
package org.crosswire.jsword.book;

import junit.framework.TestCase;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class TestBible extends TestCase
{
    public TestBible(String s, String name)
    {
        super(s);
        this.name = name;
    }

    String name = null;
    Bible ver = null;

    protected void setUp() throws Exception
    {
        ver = Bibles.getBible(name);
    }

    protected void tearDown()
    {
    }

    public void testGetBible()
    {
        assertTrue(ver != null);
    }

    public void testGetMetaData() throws Exception
    {
        //assertEquals(name, ver.getMetaData().getName());
    }

    public void testFindPassage() throws Exception
    {
        Passage ref = ver.findPassage("aaron");
        assertTrue(ref.countVerses() > 10);
        ref = ver.findPassage("jerusalem");
        assertTrue(ref.countVerses() > 10);
        ref = ver.findPassage("god");
        assertTrue(ref.countVerses() > 10);
        ref = ver.findPassage("GOD");
        assertTrue(ref.countVerses() > 10);
        ref = ver.findPassage("brother's");
        assertTrue(ref.countVerses() > 2);
        ref = ver.findPassage("BROTHER'S");
        assertTrue(ref.countVerses() > 2);

        ref = ver.findPassage("maher-shalal-hash-baz");
        if (ref.isEmpty())
            ref = ver.findPassage("mahershalalhashbaz");
        assertEquals(ref.countVerses(), 2);
        assertEquals(ref.getVerseAt(0), new Verse("Isa 8:1"));
        assertEquals(ref.getVerseAt(1), new Verse("Isa 8:3"));
        ref = ver.findPassage("MAHER-SHALAL-HASH-BAZ");
        if (ref.isEmpty())
            ref = ver.findPassage("MAHERSHALALHASHBAZ");
        assertEquals(ref.countVerses(), 2);
        assertEquals(ref.getVerseAt(0), new Verse("Isa 8:1"));
        assertEquals(ref.getVerseAt(1), new Verse("Isa 8:3"));
    }

    public void testGetStartsWith() throws Exception
    {
        String[] sa = BookUtil.toStringArray(ver.getStartsWith("jos"));
        assertTrue(sa.length > 5);
        sa = BookUtil.toStringArray(ver.getStartsWith("jerusale"));
        assertEquals(sa[0], "jerusalem");
        sa = BookUtil.toStringArray(ver.getStartsWith("maher-shalal"));
        if (sa.length == 0)
        {
            sa = BookUtil.toStringArray(ver.getStartsWith("mahershalal"));
            assertEquals(sa[0], "mahershalalhashbaz");
        }
        else
        {
            assertEquals(sa[0], "maher-shalal-hash-baz");
        }
        assertEquals(sa.length, 1);
        sa = BookUtil.toStringArray(ver.getStartsWith("MAHER-SHALAL"));
        if (sa.length == 0)
        {
            sa = BookUtil.toStringArray(ver.getStartsWith("MAHERSHALAL"));
            assertEquals(sa[0], "mahershalalhashbaz");
        }
        else
        {
            assertEquals(sa[0], "maher-shalal-hash-baz");
        }
        assertEquals(sa.length, 1);
        sa = BookUtil.toStringArray(ver.getStartsWith("XXX"));
        assertEquals(sa.length, 0);
    }
}
