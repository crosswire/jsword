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

import junit.framework.TestCase;

import org.crosswire.jsword.book.search.Grammar;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DictionaryTest extends TestCase
{
    public DictionaryTest(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testGetRoot() throws Exception
    {
        assertEquals(Grammar.getRoot("joseph"), "joseph"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(Grammar.getRoot("joseph's"), "joseph"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(Grammar.getRoot("walker"), "walk"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(Grammar.getRoot("walked"), "walk"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(Grammar.getRoot("walks"), "walk"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(Grammar.getRoot("boxes"), "box"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testIsSmallWord() throws Exception
    {
        assertTrue(Grammar.isSmallWord("the")); //$NON-NLS-1$
        assertTrue(Grammar.isSmallWord("and")); //$NON-NLS-1$
        assertTrue(!Grammar.isSmallWord("lord")); //$NON-NLS-1$
        assertTrue(!Grammar.isSmallWord("god")); //$NON-NLS-1$
        assertTrue(Grammar.isSmallWord("o")); //$NON-NLS-1$
        assertTrue(!Grammar.isSmallWord("nothing")); //$NON-NLS-1$
        assertTrue(Grammar.isSmallWord(" the ")); //$NON-NLS-1$
        assertTrue(Grammar.isSmallWord(" and ")); //$NON-NLS-1$
        assertTrue(!Grammar.isSmallWord(" lord ")); //$NON-NLS-1$
        assertTrue(!Grammar.isSmallWord(" god ")); //$NON-NLS-1$
        assertTrue(Grammar.isSmallWord(" o ")); //$NON-NLS-1$
        assertTrue(!Grammar.isSmallWord(" nothing ")); //$NON-NLS-1$
        assertTrue(Grammar.isSmallWord("")); //$NON-NLS-1$
        assertTrue(Grammar.isSmallWord(" ")); //$NON-NLS-1$
        assertTrue(Grammar.isSmallWord("  ")); //$NON-NLS-1$
    }

    public void testStripSmallWords() throws Exception
    {
        String[] temp = Grammar.stripSmallWords(new String[] { "i", "am", "but", "nothing", "o", "the", "lord", "god", "and", "", }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
        assertEquals(temp[0], "nothing"); //$NON-NLS-1$
        assertEquals(temp[1], "lord"); //$NON-NLS-1$
        assertEquals(temp[2], "god"); //$NON-NLS-1$
        assertEquals(temp.length, 3);
    }

    public void testTokenizeWithoutSmallWords() throws Exception
    {
        String[] temp = Grammar.tokenizeWithoutSmallWords("i am but nothing o the lord god and ", " "); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(temp[0], "nothing"); //$NON-NLS-1$
        assertEquals(temp[1], "lord"); //$NON-NLS-1$
        assertEquals(temp[2], "god"); //$NON-NLS-1$
        assertEquals(temp.length, 3);
    }
}
