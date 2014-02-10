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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005 - 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.common.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;

import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith
 */
public class StringUtilTest {

    @Test
    public void testRead() throws IOException {
        PipedReader in = new PipedReader();
        PipedWriter pout = new PipedWriter(in);
        PrintWriter out = new PrintWriter(pout, true);
        out.println("a b c d e");
        out.println("f g h i j");
        out.close();
        pout.close();
        assertEquals("a b c d e" + StringUtil.NEWLINE + "f g h i j" + StringUtil.NEWLINE, StringUtil.read(in));
    }

    @Test
    public void testGetInitials() {
        assertEquals("CoE", StringUtil.getInitials("Church of England"));
        assertEquals("JDC", StringUtil.getInitials("Java DataBase Connectivity"));
        assertEquals("", StringUtil.getInitials(""));
    }

    @Test
    public void testCreateTitle() {
        assertEquals("One Two", StringUtil.createTitle("OneTwo"));
        assertEquals("One Two", StringUtil.createTitle("one_two"));
        assertEquals("ONe TWo", StringUtil.createTitle("ONeTWo"));
        assertEquals("One Two", StringUtil.createTitle("One_Two"));
        assertEquals("One Two", StringUtil.createTitle("One _Two"));
        assertEquals("One Two", StringUtil.createTitle("one  _Two"));
    }
}
