
package org.crosswire.jsword.book.jdbc;

import junit.framework.TestCase;

import org.crosswire.jsword.passage.Books;

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
public class TestJDBCBible extends TestCase
{
    public TestJDBCBible(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static void testVerseOrdinal() throws Exception
    {
        for (int b=1; b<=Books.booksInBible(); b++)
        {
            int max_chapter = Books.chaptersInBook(b);
            int max_verse = Books.versesInChapter(b, max_chapter);

            max_verse = max_verse;
            //test(db.verseOrdinal(b, 1, 1), Books.verseOrdinal(b, 1, 1));
            //test(db.verseOrdinal(b, max_chapter, max_verse), Books.verseOrdinal(b, max_chapter, max_verse));

            // Like to do this, but MS can't cope. :-(
            // for (int c=1; c<=Books.chaptersIn(b); c++)
            //   for (int v=1; v<=Books.versesIn(b, c); v++)
        }
    }
}
