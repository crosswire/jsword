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
package org.crosswire.jsword.book.raw;

import org.crosswire.jsword.book.BookAdvancedParentTst;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RawBookTest extends BookAdvancedParentTst
{
    public RawBookTest(String s)
    {
        super(s);
    }

    public void testRawUtil() throws Exception
    {
        for (int i=0; i<bibles.length; i++)
        {
// LATER: RawBook is in limbo
//            if (!(bibles[i] instanceof RawBook))
//                continue;
//
//            RawBook raw = (RawBook) bibles[i];
//
//            Items words = raw.getWords();
//            Insts wordinsts = raw.getWordData();
//
//            int in = words.getIndex("in"); //$NON-NLS-1$
//            int th = words.getIndex("the"); //$NON-NLS-1$
//            int be = words.getIndex("beginning"); //$NON-NLS-1$
//            int go = words.getIndex("god"); //$NON-NLS-1$
//            int cr = words.getIndex("created"); //$NON-NLS-1$
//            assertEquals(in, words.getIndex("in")); //$NON-NLS-1$
//            assertEquals(th, words.getIndex("the")); //$NON-NLS-1$
//            assertEquals(be, words.getIndex("beginning")); //$NON-NLS-1$
//            assertEquals(go, words.getIndex("god")); //$NON-NLS-1$
//            assertEquals(cr, words.getIndex("created")); //$NON-NLS-1$
//            assertTrue(in != th);
//            assertTrue(in != be);
//            assertTrue(in != go);
//            assertTrue(in != cr);
//            assertTrue(th != be);
//            assertTrue(th != go);
//            assertTrue(th != cr);
//            assertTrue(be != go);
//            assertTrue(be != cr);
//            assertTrue(go != cr);
//            int g2 = words.getIndex("gods"); //$NON-NLS-1$
//            int g3 = words.getIndex("god"); //$NON-NLS-1$
//            int g4 = words.getIndex("god's"); //$NON-NLS-1$
//            int g5 = words.getIndex("godly"); //$NON-NLS-1$
//            int g6 = words.getIndex("good"); //$NON-NLS-1$
//            assertEquals(g2, words.getIndex("gods")); //$NON-NLS-1$
//            assertEquals(g3, words.getIndex("god")); //$NON-NLS-1$
//            assertEquals(g4, words.getIndex("god's")); //$NON-NLS-1$
//            assertEquals(g5, words.getIndex("godly")); //$NON-NLS-1$
//            assertEquals(g6, words.getIndex("good")); //$NON-NLS-1$
//            assertTrue(go != g2);
//            assertEquals(go, g3);
//            assertTrue(go != g4);
//            assertTrue(go != g5);
//            assertTrue(go != g6);
//            assertTrue(g2 != g3);
//            assertTrue(g2 != g4);
//            assertTrue(g2 != g5);
//            assertTrue(g2 != g6);
//            assertTrue(g3 != g4);
//            assertTrue(g3 != g5);
//            assertTrue(g3 != g6);
//            assertTrue(g4 != g5);
//            assertTrue(g4 != g6);
//            assertTrue(g5 != g6);
//
//            int[] idx = words.getIndex(new String[] { "in", "the", "beginning", "did", "god" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
//            assertEquals(idx[0], in);
//            assertEquals(idx[1], th);
//            assertEquals(idx[2], be);
//            assertEquals(idx[3], words.getIndex("did")); //$NON-NLS-1$
//            assertEquals(idx[4], go);
//
//            /*
//            Enumeration en = words.getEnumeration();
//            while (en.hasMoreElements())
//            {
//                String word = (String) en.nextElement();
//                int index = words.getIndex(word);
//                String word2 = words.getItem(index);
//                assertEquals(word, word2);
//            }
//            */
//
//            assertEquals("in", words.getItem(in)); //$NON-NLS-1$
//            assertEquals("the", words.getItem(th)); //$NON-NLS-1$
//            assertEquals("beginning", words.getItem(be)); //$NON-NLS-1$
//            assertEquals("god", words.getItem(go)); //$NON-NLS-1$
//            assertEquals("created", words.getItem(cr)); //$NON-NLS-1$
//            assertEquals("good", words.getItem(g6)); //$NON-NLS-1$
//
//            int[] widx = new int[] { 0, 1, 2, 3, 4 };
//            wordinsts.setIndexes(widx, new Verse(1));
//
//            int[] widx2 = wordinsts.getIndexes(new Verse(1));
//            assertEquals(widx, widx2);
//
//            // I'm making these tests do for the Punc[Inst]Resource
//            // classes as they are to similar.
        }
    }
}

