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
package org.crosswire.jsword.book.search.parse;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class CustomTokenizerTest extends TestCase
{
    public CustomTokenizerTest(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testGetStringArray() throws Exception
    {
        Map commands = IndexSearcher.getWordMap();
        List output = null;

        output = CustomTokenizer.tokenize("/ aaron , moses", commands); //$NON-NLS-1$
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.size(), 4);

        output = CustomTokenizer.tokenize("/aaron+moses", commands); //$NON-NLS-1$
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.size(), 4);

        output = CustomTokenizer.tokenize("&aaron-moses", commands); //$NON-NLS-1$
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.RemoveCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.size(), 4);

        output = CustomTokenizer.tokenize("/aaron~5+moses", commands); //$NON-NLS-1$
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.BlurCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.get(4).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(5).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.size(), 6);

        output = CustomTokenizer.tokenize("  /  aaron  ~   5    +     moses   ", commands); //$NON-NLS-1$
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.BlurCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.get(4).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(5).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.size(), 6);

        output = CustomTokenizer.tokenize("  /  aaron  ~   5    +     moses   ", commands); //$NON-NLS-1$
        Iterator it = output.iterator();
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord"); //$NON-NLS-1$
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.BlurCommandWord"); //$NON-NLS-1$
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord"); //$NON-NLS-1$
        assertTrue(it.hasNext());
        assertEquals(it.next().getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertTrue(!it.hasNext());

        // This is not actually a legal search string ... however the parser should get it right
        output = CustomTokenizer.tokenize("&~5-/", commands); //$NON-NLS-1$
        assertEquals(output.get(0).getClass().getName(), "org.crosswire.jsword.book.search.parse.RetainCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(1).getClass().getName(), "org.crosswire.jsword.book.search.parse.BlurCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(2).getClass().getName(), "org.crosswire.jsword.book.search.parse.DefaultWord"); //$NON-NLS-1$
        assertEquals(output.get(3).getClass().getName(), "org.crosswire.jsword.book.search.parse.RemoveCommandWord"); //$NON-NLS-1$
        assertEquals(output.get(4).getClass().getName(), "org.crosswire.jsword.book.search.parse.AddCommandWord"); //$NON-NLS-1$
        assertEquals(output.size(), 5);
    }
}