
package org.crosswire.common.util;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;

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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class TestStringUtil extends TestCase
{
    public TestStringUtil(String s)
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
        out.println("a b c d e");
        out.println("f g h i j");
        out.close();
        pout.close();
        assertEquals(StringUtil.read(in), "a b c d e"+StringUtil.NEWLINE+"f g h i j"+StringUtil.NEWLINE);
    }

    public void testSetLength() throws Exception
    {
        assertEquals(StringUtil.setLengthRightPad("12345", 5), "12345");
        assertEquals(StringUtil.setLengthRightPad("1234567890", 5), "12345");
        assertEquals(StringUtil.setLengthRightPad("123", 5), "123  ");

        assertEquals(StringUtil.setLengthLeftPad("12345", 5), "12345");
        assertEquals(StringUtil.setLengthLeftPad("1234567890", 5), "12345");
        assertEquals(StringUtil.setLengthLeftPad("123", 5), "  123");
    }

    public void testShorten() throws Exception
    {
        assertEquals(StringUtil.shorten("12345", 5), "12345");
        assertEquals(StringUtil.shorten("1234567890", 5), "12...");
        assertEquals(StringUtil.shorten("123", 5), "123");
    }

    public void testGetInitials() throws Exception
    {
        assertEquals(StringUtil.getInitials("Church of England"), "CoE");
        assertEquals(StringUtil.getInitials("Java DataBase Connectivity"), "JDC");
        assertEquals(StringUtil.getInitials(""), "");
    }

    public void testGetCapitals() throws Exception
    {
        assertEquals(StringUtil.getCapitals("Church of England"), "CE");
        assertEquals(StringUtil.getCapitals("Java DataBase Connectivity"), "JDBC");
        assertEquals(StringUtil.getCapitals(""), "");
    }

    public void testCreateTitle() throws Exception
    {
        assertEquals(StringUtil.createTitle("OneTwo"), "One Two");
        assertEquals(StringUtil.createTitle("one_two"), "One Two");
        assertEquals(StringUtil.createTitle("ONeTWo"), "ONe TWo");
        assertEquals(StringUtil.createTitle("One_Two"), "One Two");
        assertEquals(StringUtil.createTitle("One _Two"), "One Two");
        assertEquals(StringUtil.createTitle("one  _Two"), "One Two");
    }

    public void testCreateJavaName() throws Exception
    {
        assertEquals(StringUtil.createJavaName("one  _Two"), "OneTwo");
        assertEquals(StringUtil.createJavaName("one_two"), "OneTwo");
        assertEquals(StringUtil.createJavaName("onetwo"), "Onetwo");
        assertEquals(StringUtil.createJavaName("ONetwo"), "ONetwo");
    }
}
