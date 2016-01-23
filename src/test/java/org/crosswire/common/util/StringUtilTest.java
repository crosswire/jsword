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
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.util;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
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
        Assert.assertEquals("a b c d e" + StringUtil.NEWLINE + "f g h i j" + StringUtil.NEWLINE, StringUtil.read(in));
    }

    @Test
    public void testGetInitials() {
        Assert.assertEquals("CoE", StringUtil.getInitials("Church of England"));
        Assert.assertEquals("JDC", StringUtil.getInitials("Java DataBase Connectivity"));
        Assert.assertEquals("", StringUtil.getInitials(""));
    }

    @Test
    public void testCreateTitle() {
        Assert.assertEquals("One Two", StringUtil.createTitle("OneTwo"));
        Assert.assertEquals("One Two", StringUtil.createTitle("one_two"));
        Assert.assertEquals("ONe TWo", StringUtil.createTitle("ONeTWo"));
        Assert.assertEquals("One Two", StringUtil.createTitle("One_Two"));
        Assert.assertEquals("One Two", StringUtil.createTitle("One _Two"));
        Assert.assertEquals("One Two", StringUtil.createTitle("one  _Two"));
    }

    @Test
    public void testSplitWhitespace() {
        Assert.assertArrayEquals("split null on ws", new String[] {}, StringUtil.split(null));
        Assert.assertArrayEquals("split empty on ws", new String[] {}, StringUtil.split(""));
        Assert.assertArrayEquals("split parts on middle ws", new String[] {"abc", "def"}, StringUtil.split("abc def"));
        Assert.assertArrayEquals("split parts on many ws", new String[] {"abc", "def"}, StringUtil.split("abc  def"));
        Assert.assertArrayEquals("split parts on surrounding ws", new String[] {"abc"}, StringUtil.split(" abc "));
        Assert.assertArrayEquals("split parts on tab and new line ws", new String[] {"a", "b", "c"}, StringUtil.split("a\tb\nc"));
    }

    @Test
    public void testSplitWhitespaceMax() {
        Assert.assertArrayEquals("split null on ws", new String[] {}, StringUtil.split(null, 1));
        Assert.assertArrayEquals("split empty on ws", new String[] {}, StringUtil.split("", 1));
        Assert.assertArrayEquals("split parts on middle ws", new String[] {"abc"}, StringUtil.split("abc def", 1));
        Assert.assertArrayEquals("split parts on many ws", new String[] {"abc"}, StringUtil.split("abc  def", 1));
        Assert.assertArrayEquals("split parts on surrounding ws", new String[] {"abc"}, StringUtil.split(" abc ", 1));
        Assert.assertArrayEquals("split parts on tab and new line ws", new String[] {"a", "b"}, StringUtil.split("a\tb\nc", 2));
    }

    @Test
    public void testSplitChar() {
        Assert.assertArrayEquals("split null on .", new String[] {}, StringUtil.split(null, '.'));
        Assert.assertArrayEquals("split empty on .", new String[] {}, StringUtil.split("", '.'));
        Assert.assertArrayEquals("split parts on .", new String[]{"a", "b", "c"}, StringUtil.split("a.b.c", '.'));
        Assert.assertArrayEquals("split adjacent on .", new String[]{"a", "b", "c"}, StringUtil.split("a..b.c", '.'));
        Assert.assertArrayEquals("split adjacent leading .", new String[]{"a", "b", "c"}, StringUtil.split(".a..b.c", '.'));
        Assert.assertArrayEquals("split adjacent trailing .", new String[]{"a", "b", "c"}, StringUtil.split(".a..b.c.", '.'));
        Assert.assertArrayEquals("split nothing on .", new String[]{"a:b:c"}, StringUtil.split("a:b:c", '.'));
    }

    @Test
    public void testSplitAllChar() {
        Assert.assertArrayEquals("splitAll null on .", new String[]{}, StringUtil.splitAll(null, '.'));
        Assert.assertArrayEquals("splitAll empty on .", new String[]{}, StringUtil.splitAll("", '.'));
        Assert.assertArrayEquals("splitAll parts on .", new String[]{"a", "b", "c"}, StringUtil.splitAll("a.b.c", '.'));
        Assert.assertArrayEquals("splitAll adjacent on .", new String[]{"a", "", "b", "c"}, StringUtil.splitAll("a..b.c", '.'));
        Assert.assertArrayEquals("splitAll nothing on .", new String[]{"a:b:c"}, StringUtil.splitAll("a:b:c", '.'));
    }

    @Test
    public void testSplitAllCharMax() {
        Assert.assertArrayEquals("splitAll all null on .", new String[]{}, StringUtil.splitAll(null, '.', 0));
        Assert.assertArrayEquals("splitAll all empty on .", new String[]{}, StringUtil.splitAll("", '.', 0));
        Assert.assertArrayEquals("splitAll all parts on .", new String[]{"a", "b", "c"}, StringUtil.splitAll("a.b.c", '.', 0));
        Assert.assertArrayEquals("splitAll all adjacent on .", new String[]{"a", "", "b", "c"}, StringUtil.splitAll("a..b.c", '.', 0));
        Assert.assertArrayEquals("splitAll all nothing on .", new String[]{"a:b:c"}, StringUtil.splitAll("a:b:c", '.', 0));
        Assert.assertArrayEquals("splitAll max null on .", new String[]{}, StringUtil.splitAll(null, '.', Integer.MAX_VALUE));
        Assert.assertArrayEquals("splitAll max empty on .", new String[]{}, StringUtil.splitAll("", '.', Integer.MAX_VALUE));
        Assert.assertArrayEquals("splitAll max parts on .", new String[]{"a", "b", "c"}, StringUtil.splitAll("a.b.c", '.', Integer.MAX_VALUE));
        Assert.assertArrayEquals("splitAll max adjacent on .", new String[]{"a", "", "b", "c"}, StringUtil.splitAll("a..b.c", '.', Integer.MAX_VALUE));
        Assert.assertArrayEquals("splitAll max nothing on .", new String[]{"a:b:c"}, StringUtil.splitAll("a:b:c", '.', Integer.MAX_VALUE));
        Assert.assertArrayEquals("splitAll 3 null on .", new String[]{}, StringUtil.splitAll(null, '.', 3));
        Assert.assertArrayEquals("splitAll 3 empty on .", new String[]{}, StringUtil.splitAll("", '.', 3));
        Assert.assertArrayEquals("splitAll 3 parts on .", new String[]{"a", "b", "c"}, StringUtil.splitAll("a.b.c", '.', 3));
        Assert.assertArrayEquals("splitAll 3 adjacent on .", new String[]{"a", "", "b"}, StringUtil.splitAll("a..b.c", '.', 3));
        Assert.assertArrayEquals("splitAll 3 nothing on .", new String[]{"a:b:c"}, StringUtil.splitAll("a:b:c", '.', 3));
        Assert.assertArrayEquals("splitAll 2 null on .", new String[]{}, StringUtil.splitAll(null, '.', 2));
        Assert.assertArrayEquals("splitAll 2 empty on .", new String[]{}, StringUtil.splitAll("", '.', 2));
        Assert.assertArrayEquals("splitAll 2 parts on .", new String[]{"a", "b"}, StringUtil.splitAll("a.b.c", '.', 2));
        Assert.assertArrayEquals("splitAll 2 adjacent on .", new String[]{"a", ""}, StringUtil.splitAll("a..b.c", '.', 2));
        Assert.assertArrayEquals("splitAll 2 nothing on .", new String[]{"a:b:c"}, StringUtil.splitAll("a:b:c", '.', 2));
        Assert.assertArrayEquals("splitAll 1 null on .", new String[]{}, StringUtil.splitAll(null, '.', 1));
        Assert.assertArrayEquals("splitAll 1 empty on .", new String[]{}, StringUtil.splitAll("", '.', 1));
        Assert.assertArrayEquals("splitAll 1 parts on .", new String[]{"a"}, StringUtil.splitAll("a.b.c", '.', 1));
        Assert.assertArrayEquals("splitAll 1 adjacent on .", new String[]{"a"}, StringUtil.splitAll("a..b.c", '.', 1));
        Assert.assertArrayEquals("splitAll 1 nothing on .", new String[]{"a:b:c"}, StringUtil.splitAll("a:b:c", '.', 1));
    }

    @Ignore
    @Test
    public void bench() {

        String string = "a/b/c/d/e/f/g/h/i/j/asd/asdas/dasdjasodjoa/sjd/oajs/djoasjd/as/odj/jaowdj/oajw/odj/aojwd/oja/owjd/oja/wjdoja/wdj/awjdojaw/odj/oawjd/oja/wjdoawjdojaw/d/dff";
        int n = 1000000;

        long start;

        for (int t = 1; t <= 3; ++t) {
            System.out.println("Run #" + t);

            start = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                Pattern.compile("/").split(string, 0);
            }
            System.out.println("Java 6 string.split: " + (System.currentTimeMillis() - start));

            Pattern splitter = Pattern.compile("/");
            start = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                splitter.split(string);
            }
            System.out.println("Pattern.split: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                StringTokenizer s = new StringTokenizer(string, "/");
                while (s.hasMoreElements()) {
                    s.nextElement();
                }
            }
            System.out.println("StringTokenizer: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                string.split("/");
            }
            System.out.println("Java 7 string.split: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                StringUtil.splitAll(string, '/');
            }
            System.out.println("StringUtil.splitAll: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                StringUtil.splitAll(string, '/', Integer.MAX_VALUE);
            }
            System.out.println("StringUtil.splitAll max: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                StringUtil.split(string, '/');
            }
            System.out.println("StringUtil.split char: " + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            for (int i = 0; i < n; i++) {
                StringUtil.split(string, '/', Integer.MAX_VALUE);
            }
            System.out.println("StringUtil.split char max: " + (System.currentTimeMillis() - start));

            System.out.println("");
        }
    }
}
