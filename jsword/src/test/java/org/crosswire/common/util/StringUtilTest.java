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
package org.crosswire.common.util;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StringUtilTest extends TestCase {
    public StringUtilTest(String s) {
        super(s);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public void testRead() throws IOException {
        PipedReader in = new PipedReader();
        PipedWriter pout = new PipedWriter(in);
        PrintWriter out = new PrintWriter(pout, true);
        out.println("a b c d e");
        out.println("f g h i j");
        out.close();
        pout.close();
        assertEquals(StringUtil.read(in), "a b c d e" + StringUtil.NEWLINE + "f g h i j" + StringUtil.NEWLINE);
    }

    public void testGetInitials() {
        assertEquals(StringUtil.getInitials("Church of England"), "CoE");
        assertEquals(StringUtil.getInitials("Java DataBase Connectivity"), "JDC");
        assertEquals(StringUtil.getInitials(""), "");
    }

    public void testCreateTitle() {
        assertEquals(StringUtil.createTitle("OneTwo"), "One Two");
        assertEquals(StringUtil.createTitle("one_two"), "One Two");
        assertEquals(StringUtil.createTitle("ONeTWo"), "ONe TWo");
        assertEquals(StringUtil.createTitle("One_Two"), "One Two");
        assertEquals(StringUtil.createTitle("One _Two"), "One Two");
        assertEquals(StringUtil.createTitle("one  _Two"), "One Two");
    }
}
