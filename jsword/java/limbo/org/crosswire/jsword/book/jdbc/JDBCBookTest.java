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
package org.crosswire.jsword.book.jdbc;

import org.crosswire.jsword.book.BookParentTst;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JDBCBookTest extends BookParentTst
{
    public JDBCBookTest(String s)
    {
        super(s);
    }

    public void testVerseOrdinal() throws Exception
    {
        /* LATER: JDBCBook is in limbo
        for (int i=0; i<bibles.length; i++)
        {
            if (!(bibles[i] instanceof JDBCBook))
                continue;

            // JDBCBook ver = (JDBCBook) bibles[i];

            for (int b=1; b<=BibleInfo.booksInBible(); b++)
            {
                //int max_chapter = BibleInfo.chaptersInBook(b);
                //int max_verse = BibleInfo.versesInChapter(b, max_chapter);
    
                //test(db.verseOrdinal(b, 1, 1), BibleInfo.verseOrdinal(b, 1, 1));
                //test(db.verseOrdinal(b, max_chapter, max_verse), BibleInfo.verseOrdinal(b, max_chapter, max_verse));
    
                // Like to do this, but MS can't cope. :-(
                // for (int c=1; c<=BibleInfo.chaptersIn(b); c++)
                //   for (int v=1; v<=BibleInfo.versesIn(b, c); v++)
            }
        }
        */
    }
}
