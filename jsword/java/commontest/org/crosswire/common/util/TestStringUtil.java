
package org.crosswire.common.util;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.util.Vector;

import junit.framework.TestCase;

/**
 * JUnit Test.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
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

    public void testNullIfBlank() throws Exception
    {
        assertEquals(null, StringUtil.nullIfBlank(""));
        assertEquals(null, StringUtil.nullIfBlank(null));
        assertEquals("fred", StringUtil.nullIfBlank("fred"));
        assertEquals(" ", StringUtil.nullIfBlank(" "));
        assertEquals("0", StringUtil.nullIfBlank("0"));
        assertEquals("\u0000", StringUtil.nullIfBlank("\u0000"));
        assertEquals("\u00000", StringUtil.nullIfBlank("\u00000"));
    }

    public void testBlankIfNull() throws Exception
    {
        assertEquals("", StringUtil.blankIfNull(""));
        assertEquals("", StringUtil.blankIfNull(null));
        assertEquals("fred", StringUtil.blankIfNull("fred"));
        assertEquals(" ", StringUtil.blankIfNull(" "));
        assertEquals("0", StringUtil.blankIfNull("0"));
        assertEquals("\u0000", StringUtil.blankIfNull("\u0000"));
        assertEquals("\u00000", StringUtil.blankIfNull("\u00000"));
    }

    public void testTokenize() throws Exception
    {
        assertEquals(StringUtil.cat(StringUtil.tokenize("a b c d e f"), "-"), "a-b-c-d-e-f");
        assertEquals(StringUtil.cat(StringUtil.tokenize("a-b-c-d-e-f", "-"), "="), "a=b=c=d=e=f");
        assertEquals(StringUtil.cat(StringUtil.tokenize("a-b-c-'d-e-'-f", "-", "\"'"), "=") ,"a=b=c=d-e-=f");
    }

    public void testRemoveChar() throws Exception
    {
        assertEquals(StringUtil.removeChar("a=b=c=d=e=f", '='), "abcdef");
        assertEquals(StringUtil.removeChars("a=b=c-d-e-f", "=-"), "abcdef");
    }

    public void testCat() throws Exception
    {
        assertEquals(StringUtil.cat(StringUtil.tokenize("a b c d e"), "-"), "a-b-c-d-e");
        assertEquals(StringUtil.cat(StringUtil.tokenize("a b c d e"), 2, "-"), "c-d-e");
        assertEquals(StringUtil.cat(StringUtil.tokenize("a b c d e"), 2, 3, "-"), "c-d");
    }

    public void testChop() throws Exception
    {
        assertEquals(StringUtil.chop("123(456)789", "(", ")"), "123789");
        assertEquals(StringUtil.chop("(123456)789", "(", ")"), "789");
        assertEquals(StringUtil.chop("123(456789)", "(", ")"), "123");
        assertEquals(StringUtil.chop("(123456789)", "(", ")"), "");
        assertEquals(StringUtil.chop("123()456789", "(", ")"), "123456789");
        assertEquals(StringUtil.chop("()123456789()", "(", ")"), "123456789");
        assertEquals(StringUtil.chop("()123456789()", "(", ")"), "123456789");
        assertEquals(StringUtil.chop("()()123456789()()", "(", ")"), "123456789");
        assertEquals(StringUtil.chop("()()1234()56789()()", "(", ")"), "123456789");
        assertEquals(StringUtil.chop("(123)(123)1234(123)(123)56789(123)(123)", "(", ")"), "123456789");
        try{ StringUtil.chop("12(34(56)78)9", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
        try{ StringUtil.chop("12(3456789", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
        try{ StringUtil.chop("12)3456789", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
        try{ StringUtil.chop("12(3)4(56789", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
        try{ StringUtil.chop("12(3)4)56789", "(", ")"); fail(); }
        catch (IllegalArgumentException ex) { }
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
        assertEquals(StringUtil.read(in), "a b c d e"+StringUtil.getNewline()+"f g h i j"+StringUtil.getNewline());
    }

    public void testGetStringArrayFromVector() throws Exception
    {
        Vector vec = new Vector();
        vec.addElement("a b c d e");
        vec.addElement("f g h i j");
        assertEquals(StringUtil.cat(StringUtil.getStringArray(vec), "*"), "a b c d e*f g h i j");
    }

    public void testGetStringArrayFromObject() throws Exception
    {
        Object[] objs = new Object[] { "a b c d e", "f g h i j"};
        assertEquals(StringUtil.cat(StringUtil.getStringArray(objs), "*"), "a b c d e*f g h i j");
    }

    public void testChain() throws Exception
    {
        assertEquals(StringUtil.chain(3, "-"), "---");
        assertEquals(StringUtil.chain(15, "-oO0Oo-"), "-oO0Oo--oO0Oo--");
    }

    public void testCountInstancesOf() throws Exception
    {
        assertEquals(StringUtil.countInstancesOf("-oO0Oo-", 'o'), 2);
    }

    public void testParseInt() throws Exception
    {
        assertEquals(StringUtil.parseInt("0", -1), 0);
        assertEquals(StringUtil.parseInt("Hello", -1), -1);
        assertEquals(StringUtil.parseInt("-1", 0), -1);
    }

    public void testSetLength() throws Exception
    {
        assertEquals(StringUtil.setLength("12345", 5), "12345");
        assertEquals(StringUtil.setLength("1234567890", 5), "12345");
        assertEquals(StringUtil.setLength("123", 5), "123  ");
    }

    public void testShorten() throws Exception
    {
        assertEquals(StringUtil.shorten("12345", 5), "12345");
        assertEquals(StringUtil.shorten("1234567890", 5), "12...");
        assertEquals(StringUtil.shorten("123", 5), "123");
    }

    public void testSwap() throws Exception
    {
        assertEquals(StringUtil.swap("a12645", "6", "3"), "a12345");
        assertEquals(StringUtil.swap("b12665", "66", "34"), "b12345");
        assertEquals(StringUtil.swap("c1265", "6", "34"), "c12345");
        assertEquals(StringUtil.swap("d126645", "66", "3"), "d12345");
        assertEquals(StringUtil.swap("e1264512645", "6", "3"), "e1234512345");
        assertEquals(StringUtil.swap("f1266512665", "66", "34"), "f1234512345");
        assertEquals(StringUtil.swap("g12651265", "6", "34"), "g1234512345");
        assertEquals(StringUtil.swap("h126645126645", "66", "3"), "h1234512345");
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

    public void testFileNameToJavaPackage() throws Exception
    {
        assertEquals(StringUtil.fileNameToJavaPackage("C:\\src\\java\\lang\\Object.class", "C:\\src"), "java.lang.Object");
        assertEquals(StringUtil.fileNameToJavaPackage("C:\\src\\fred\\Bing.class", "C:\\src"), "fred.Bing");
        assertEquals(StringUtil.fileNameToJavaPackage("C:\\fred\\Bing.class", "nonesense"), "fred.Bing");
        assertEquals(StringUtil.fileNameToJavaPackage("C:\\src\\fred\\Bing.java", "C:\\src"), "fred.Bing");

        assertEquals(StringUtil.fileNameToJavaPackage("C:\\src\\fred\\Bing.java", "C:\\src\\"), "fred.Bing");
        assertEquals(StringUtil.fileNameToJavaPackage("C:\\src\\fred\\Bing.java", "c:\\src\\"), "fred.Bing");
        assertEquals(StringUtil.fileNameToJavaPackage("C:\\src\\fred\\Bing.java", "C:\\src\\;.;d:\\fred"), "fred.Bing");
        assertEquals(StringUtil.fileNameToJavaPackage("C:\\src\\fred\\Bing.java", ".;C:\\src\\;.;d:\\fred"), "fred.Bing");
        assertEquals(StringUtil.fileNameToJavaPackage("C:\\src\\fred\\Bing.java", ".;c:\\jdk\\lib\\classes.zip;C:\\src\\;.;d:\\fred"), "fred.Bing");
    }

    public void testToHexChar() throws Exception
    {
        assertEquals(StringUtil.toHexChar((byte) 0), '0');
        assertEquals(StringUtil.toHexChar((byte) 4), '4');
        assertEquals(StringUtil.toHexChar((byte) 9), '9');
        assertEquals(StringUtil.toHexChar((byte) 10), 'a');
        assertEquals(StringUtil.toHexChar((byte) 15), 'f');
        assertEquals(StringUtil.toHexChar((byte) 16), '0');
        assertEquals(StringUtil.toHexChar((byte) 31), 'f');
        assertEquals(StringUtil.toHexChar((byte) 32), '0');
    }

    public void testToHexString() throws Exception
    {
        assertEquals(StringUtil.toHexString(0), "0000");
        assertEquals(StringUtil.toHexString(4), "0004");
        assertEquals(StringUtil.toHexString(9), "0009");
        assertEquals(StringUtil.toHexString(10), "000a");
        assertEquals(StringUtil.toHexString(15), "000f");
        assertEquals(StringUtil.toHexString(16), "0010");
        assertEquals(StringUtil.toHexString(31), "001f");
        assertEquals(StringUtil.toHexString(32), "0020");
    }
}
