
package org.crosswire.jsword.util;

import junit.framework.TestCase;

import org.crosswire.jsword.util.Style;

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
public class StyleTest extends TestCase
{
    public StyleTest(String s)
    {
        super(s);
    }

    private Style style = null;

    protected void setUp() throws Exception
    {
        style = new Style("test");
    }

    protected void tearDown() throws Exception
    {
    }

    public void testGetStyles() throws Exception
    {
        String[] names = style.getStyles();
        assertEquals(names.length, 2);
        assertEquals(names[0], "test.xsl");
        assertEquals(names[1], "test2.xsl");
    }

    public void testGetFilename() throws Exception
    {
        /*
        assertEquals(style.getFilename("test"),
             "S:\\Joe\\Devt\\DoE" + File.separator +
             "lib" + File.separator +
             "styles" + File.separator +
             "test" + File.separator +
             "test.xsl");
        */
    }
}
