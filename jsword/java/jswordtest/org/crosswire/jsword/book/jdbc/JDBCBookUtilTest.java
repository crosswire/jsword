
package org.crosswire.jsword.book.jdbc;

import org.crosswire.jsword.book.BookParentTst;

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
public class JDBCBookUtilTest extends BookParentTst
{
    public JDBCBookUtilTest(String s)
    {
        super(s);
    }

    public void testChop() throws Exception
    {
        assertEquals(JDBCBibleUtil.chop1("123(456)789", "(", ")"), "123789");
        assertEquals(JDBCBibleUtil.chop1("(123456)789", "(", ")"), "789");
        assertEquals(JDBCBibleUtil.chop1("123(456789)", "(", ")"), "123");
        assertEquals(JDBCBibleUtil.chop1("(123456789)", "(", ")"), "");
        assertEquals(JDBCBibleUtil.chop1("123()456789", "(", ")"), "123456789");
        assertEquals(JDBCBibleUtil.chop1("()123456789()", "(", ")"), "123456789");
        assertEquals(JDBCBibleUtil.chop1("()123456789()", "(", ")"), "123456789");
        assertEquals(JDBCBibleUtil.chop1("()()123456789()()", "(", ")"), "123456789");
        assertEquals(JDBCBibleUtil.chop1("()()1234()56789()()", "(", ")"), "123456789");
        assertEquals(JDBCBibleUtil.chop1("(123)(123)1234(123)(123)56789(123)(123)", "(", ")"), "123456789");
        try{ JDBCBibleUtil.chop1("12(34(56)78)9", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
        try{ JDBCBibleUtil.chop1("12(3456789", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
        try{ JDBCBibleUtil.chop1("12)3456789", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
        try{ JDBCBibleUtil.chop1("12(3)4(56789", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
        try{ JDBCBibleUtil.chop1("12(3)4)56789", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
    }
}
