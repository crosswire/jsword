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
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StringUtilTest extends TestCase
{
    public StringUtilTest(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testRead() throws Exception
    {
        PipedReader in = new PipedReader();
        PipedWriter pout = new PipedWriter(in);
        PrintWriter out = new PrintWriter(pout, true);
        out.println("a b c d e"); //$NON-NLS-1$
        out.println("f g h i j"); //$NON-NLS-1$
        out.close();
        pout.close();
        assertEquals(StringUtil.read(in), "a b c d e" + StringUtil.NEWLINE + "f g h i j" + StringUtil.NEWLINE); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testGetInitials() throws Exception
    {
        assertEquals(StringUtil.getInitials("Church of England"), "CoE"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(StringUtil.getInitials("Java DataBase Connectivity"), "JDC"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(StringUtil.getInitials(""), ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testCreateTitle() throws Exception
    {
        assertEquals(StringUtil.createTitle("OneTwo"), "One Two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(StringUtil.createTitle("one_two"), "One Two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(StringUtil.createTitle("ONeTWo"), "ONe TWo"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(StringUtil.createTitle("One_Two"), "One Two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(StringUtil.createTitle("One _Two"), "One Two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(StringUtil.createTitle("one  _Two"), "One Two"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}