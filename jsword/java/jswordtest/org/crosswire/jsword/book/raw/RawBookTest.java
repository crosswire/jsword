
package org.crosswire.jsword.book.raw;

import org.crosswire.jsword.book.BookAdvancedParentTst;
import org.crosswire.jsword.passage.Verse;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
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
            if (!(bibles[i] instanceof RawBook))
                continue;

            RawBook raw = (RawBook) bibles[i];

            Items words = raw.getWords();
            Insts wordinsts = raw.getWordData();
    
            int in = words.getIndex("in");
            int th = words.getIndex("the");
            int be = words.getIndex("beginning");
            int go = words.getIndex("god");
            int cr = words.getIndex("created");
            assertEquals(in, words.getIndex("in"));
            assertEquals(th, words.getIndex("the"));
            assertEquals(be, words.getIndex("beginning"));
            assertEquals(go, words.getIndex("god"));
            assertEquals(cr, words.getIndex("created"));
            assertTrue(in != th);
            assertTrue(in != be);
            assertTrue(in != go);
            assertTrue(in != cr);
            assertTrue(th != be);
            assertTrue(th != go);
            assertTrue(th != cr);
            assertTrue(be != go);
            assertTrue(be != cr);
            assertTrue(go != cr);
            int g2 = words.getIndex("gods");
            int g3 = words.getIndex("god");
            int g4 = words.getIndex("god's");
            int g5 = words.getIndex("godly");
            int g6 = words.getIndex("good");
            assertEquals(g2, words.getIndex("gods"));
            assertEquals(g3, words.getIndex("god"));
            assertEquals(g4, words.getIndex("god's"));
            assertEquals(g5, words.getIndex("godly"));
            assertEquals(g6, words.getIndex("good"));
            assertTrue(go != g2);
            assertEquals(go, g3);
            assertTrue(go != g4);
            assertTrue(go != g5);
            assertTrue(go != g6);
            assertTrue(g2 != g3);
            assertTrue(g2 != g4);
            assertTrue(g2 != g5);
            assertTrue(g2 != g6);
            assertTrue(g3 != g4);
            assertTrue(g3 != g5);
            assertTrue(g3 != g6);
            assertTrue(g4 != g5);
            assertTrue(g4 != g6);
            assertTrue(g5 != g6);
    
            int[] idx = words.getIndex(new String[] { "in", "the", "beginning", "did", "god" });
            assertEquals(idx[0], in);
            assertEquals(idx[1], th);
            assertEquals(idx[2], be);
            assertEquals(idx[3], words.getIndex("did"));
            assertEquals(idx[4], go);
    
            /*
            Enumeration en = words.getEnumeration();
            while (en.hasMoreElements())
            {
                String word = (String) en.nextElement();
                int index = words.getIndex(word);
                String word2 = words.getItem(index);
                assertEquals(word, word2);
            }
            */
    
            assertEquals("in", words.getItem(in));
            assertEquals("the", words.getItem(th));
            assertEquals("beginning", words.getItem(be));
            assertEquals("god", words.getItem(go));
            assertEquals("created", words.getItem(cr));
            assertEquals("good", words.getItem(g6));
    
            int[] widx = new int[] { 0, 1, 2, 3, 4 };
            wordinsts.setIndexes(widx, new Verse(1));
    
            int[] widx2 = wordinsts.getIndexes(new Verse(1));
            assertEquals(widx, widx2);
    
            // I'm making these tests do for the Punc[Inst]Resource
            // classes as they are to similar.
        }
    }
}

