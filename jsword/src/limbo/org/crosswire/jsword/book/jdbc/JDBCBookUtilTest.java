/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.jdbc;

import org.crosswire.jsword.book.BookParentTst;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JDBCBookUtilTest extends BookParentTst
{
    public JDBCBookUtilTest(String s)
    {
        super(s);
    }

    public void testChop() throws Exception
    {
// LATER: JDBCBook is in limbo
//        assertEquals(JDBCBibleUtil.chop1("123(456)789", "(", ")"), "123789"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        assertEquals(JDBCBibleUtil.chop1("(123456)789", "(", ")"), "789"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        assertEquals(JDBCBibleUtil.chop1("123(456789)", "(", ")"), "123"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        assertEquals(JDBCBibleUtil.chop1("(123456789)", "(", ")"), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        assertEquals(JDBCBibleUtil.chop1("123()456789", "(", ")"), "123456789"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        assertEquals(JDBCBibleUtil.chop1("()123456789()", "(", ")"), "123456789"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        assertEquals(JDBCBibleUtil.chop1("()123456789()", "(", ")"), "123456789"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        assertEquals(JDBCBibleUtil.chop1("()()123456789()()", "(", ")"), "123456789"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        assertEquals(JDBCBibleUtil.chop1("()()1234()56789()()", "(", ")"), "123456789"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        assertEquals(JDBCBibleUtil.chop1("(123)(123)1234(123)(123)56789(123)(123)", "(", ")"), "123456789"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//        try{ JDBCBibleUtil.chop1("12(34(56)78)9", "(", ")"); fail(); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        catch (IllegalArgumentException ex) { }
//        try{ JDBCBibleUtil.chop1("12(3456789", "(", ")"); fail(); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        catch (IllegalArgumentException ex) { }
//        try{ JDBCBibleUtil.chop1("12)3456789", "(", ")"); fail(); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        catch (IllegalArgumentException ex) { }
//        try{ JDBCBibleUtil.chop1("12(3)4(56789", "(", ")"); fail(); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        catch (IllegalArgumentException ex) { }
//        try{ JDBCBibleUtil.chop1("12(3)4)56789", "(", ")"); fail(); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//        catch (IllegalArgumentException ex) { }
    }
}
