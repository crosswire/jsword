
package org.crosswire.jsword.passage;

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
public class TestBooks extends TestCase
{
    public TestBooks(String s)
    {
        super(s);
    }

    private int stored_case;

    protected void setUp()
    {
        stored_case = Books.getCase();
    }

    protected void tearDown()
    {
        Books.setCase(stored_case);
    }

    public void testCase() throws Exception
    {
        Books.setCase(Passage.CASE_LOWER);
        assertEquals(Books.getCase(), Passage.CASE_LOWER);
        Books.setCase(Passage.CASE_UPPER);
        assertEquals(Books.getCase(), Passage.CASE_UPPER);
        Books.setCase(Passage.CASE_SENTANCE);
        assertEquals(Books.getCase(), Passage.CASE_SENTANCE);
        try { Books.setCase(Passage.CASE_MIXED); fail(); }
        catch (IllegalArgumentException ex) { }
        try { Books.setCase(-1); fail(); }
        catch (IllegalArgumentException ex) { }
    }

    public void testGetLongBookName() throws Exception
    {
        Books.setCase(Passage.CASE_SENTANCE);
        assertEquals(Books.getLongBookName(1), "Genesis");
        assertEquals(Books.getLongBookName(66), "Revelation");
        Books.setCase(Passage.CASE_LOWER);
        assertEquals(Books.getLongBookName(1), "genesis");
        assertEquals(Books.getLongBookName(66), "revelation");
        Books.setCase(Passage.CASE_UPPER);
        assertEquals(Books.getLongBookName(1), "GENESIS");
        assertEquals(Books.getLongBookName(66), "REVELATION");
        try { Books.getShortBookName(0); fail(); }
        catch (NoSuchVerseException ex) { }
        try { Books.getShortBookName(67); fail(); }
        catch (NoSuchVerseException ex) { }
    }

    public void testGetShortBookName() throws Exception
    {
        Books.setCase(Passage.CASE_SENTANCE);
        try { Books.getShortBookName(0); fail(); }
        catch (NoSuchVerseException ex) { }
        assertEquals(Books.getShortBookName(1), "Gen");
        assertEquals(Books.getShortBookName(7), "Judg");
        assertEquals(Books.getShortBookName(39), "Mal");
        assertEquals(Books.getShortBookName(40), "Mat");
        assertEquals(Books.getShortBookName(50), "Phili");
        assertEquals(Books.getShortBookName(57), "Phile");
        assertEquals(Books.getShortBookName(65), "Jude");
        assertEquals(Books.getShortBookName(66), "Rev");
        try { Books.getShortBookName(67); fail(); }
        catch (NoSuchVerseException ex) { }
        Books.setCase(Passage.CASE_LOWER);
        try { Books.getShortBookName(0); fail(); }
        catch (NoSuchVerseException ex) { }
        assertEquals(Books.getShortBookName(1), "gen");
        assertEquals(Books.getShortBookName(7), "judg");
        assertEquals(Books.getShortBookName(39), "mal");
        assertEquals(Books.getShortBookName(40), "mat");
        assertEquals(Books.getShortBookName(50), "phili");
        assertEquals(Books.getShortBookName(57), "phile");
        assertEquals(Books.getShortBookName(65), "jude");
        assertEquals(Books.getShortBookName(66), "rev");
        try { Books.getShortBookName(67); fail(); }
        catch (NoSuchVerseException ex) { }
        Books.setCase(Passage.CASE_UPPER);
        try { Books.getShortBookName(0); fail(); }
        catch (NoSuchVerseException ex) { }
        assertEquals(Books.getShortBookName(1), "GEN");
        assertEquals(Books.getShortBookName(7), "JUDG");
        assertEquals(Books.getShortBookName(39), "MAL");
        assertEquals(Books.getShortBookName(40), "MAT");
        assertEquals(Books.getShortBookName(50), "PHILI");
        assertEquals(Books.getShortBookName(57), "PHILE");
        assertEquals(Books.getShortBookName(65), "JUDE");
        assertEquals(Books.getShortBookName(66), "REV");
        try { Books.getShortBookName(67); fail(); }
        catch (NoSuchVerseException ex) { }
    }

    public void testGetBookJogger() throws Exception
    {
        assertEquals(Books.getBookJogger(1), "LT");
        assertEquals(Books.getBookJogger(66), "DB");
        try { Books.getBookJogger(0); fail(); }
        catch (NoSuchVerseException ex) { }
        try { Books.getBookJogger(67); fail(); }
        catch (NoSuchVerseException ex) { }
    }

    public void testGetNumberJogger() throws Exception
    {
        assertEquals(Books.getNumberJogger(0), "S");
        assertEquals(Books.getNumberJogger(1), "T");
        assertEquals(Books.getNumberJogger(9), "P");
        assertEquals(Books.getNumberJogger(10), "TS");
        assertEquals(Books.getNumberJogger(99), "PP");
        assertEquals(Books.getNumberJogger(100), "TSS");
        assertEquals(Books.getNumberJogger(65536), "GLLMG");
    }

    public void testGetBookNumber() throws Exception
    {
        assertEquals(Books.getBookNumber("Genesis"), 1);
        assertEquals(Books.getBookNumber("Gene"), 1);
        assertEquals(Books.getBookNumber("Gen"), 1);
        assertEquals(Books.getBookNumber("G"), 1);
        assertEquals(Books.getBookNumber("GEN"), 1);
        assertEquals(Books.getBookNumber("genesis"), 1);
        assertEquals(Books.getBookNumber("revelations"), 66);
        assertEquals(Books.getBookNumber("rev"), 66);
        assertEquals(Books.getBookNumber("phi"), 50);
        assertEquals(Books.getBookNumber("phil"), 50);
        assertEquals(Books.getBookNumber("phili"), 50);
        assertEquals(Books.getBookNumber("phile"), 57);
        try { Books.getBookNumber("b"); fail(); }
        catch (NoSuchVerseException ex) { }
        try { Books.getBookNumber("1"); fail(); }
        catch (NoSuchVerseException ex) { }
    }

    public void testIn() throws Exception
    {
        assertEquals(Books.versesInBook(1), 1533);
        assertEquals(Books.versesInBook(66),  404);
        // Counts using loops
        int viw_b = 0;
        int viw_c = 0;
        int ciw = 0;
        // For all the books
        for (int b=1; b<=Books.booksInBible(); b++)
        {
            // Count and check the verses in this book
            int vib = 0;
            for (int c=1; c<=Books.chaptersInBook(b); c++) vib += Books.versesInChapter(b, c);
            assertEquals(vib,  Books.versesInBook(b));

            // Continue the verse counts for the whole Bible
            for (int c=1; c<=Books.chaptersInBook(b); c++) viw_c += Books.versesInChapter(b, c);
            viw_b += Books.versesInBook(b);

            // Continue the chapter count for the whole Bible
            ciw += Books.chaptersInBook(b);
        }
        assertEquals(Books.versesInBible(),  viw_b);
        assertEquals(Books.versesInBible(),  viw_c);
        assertEquals(Books.chaptersInBible(),  ciw);
        assertEquals(Books.booksInBible(),  66);
    }

    public void testOrdinal() throws Exception
    {
        int first_verse_ord = 1;
        int last_verse_ord = 1;
        for (int b=1; b<=Books.booksInBible(); b++)
        {
            for (int c=1; c<=Books.chaptersInBook(b); c++)
            {
                last_verse_ord = first_verse_ord + Books.versesInChapter(b, c) - 1;

                assertEquals(first_verse_ord,  Books.verseOrdinal(b, c, 1));
                assertEquals(first_verse_ord,  Books.verseOrdinal(new int[]{b,c,1}));
                assertEquals(first_verse_ord+1,  Books.verseOrdinal(b, c, 2));
                assertEquals(first_verse_ord+1,  Books.verseOrdinal(new int[]{b,c,2}));
                assertEquals(last_verse_ord,  Books.verseOrdinal(b, c, Books.versesInChapter(b, c)));
                assertEquals(last_verse_ord,  Books.verseOrdinal(new int[]{b,c,Books.versesInChapter(b, c)}));

                assertEquals(Books.verseCount(new int[]{b,c,1}, Books.decodeOrdinal(first_verse_ord)),  1);
                assertEquals(Books.verseCount(new int[]{b,c,2}, Books.decodeOrdinal(first_verse_ord+1)),  1);
                assertEquals(Books.verseCount(new int[]{b,c,Books.versesInChapter(b, c)}, Books.decodeOrdinal(last_verse_ord)),  1);

                first_verse_ord += Books.versesInChapter(b, c);
            }
        }
    }

    public void testValidate() throws Exception
    {
        try { Books.validate(0, 1, 1); fail(); }
        catch (NoSuchVerseException ex) { }
        for (int b=1; b<Books.booksInBible(); b++)
        {
            try { Books.validate(b, 0, 1); fail(); }
            catch (NoSuchVerseException ex) { }
            try { Books.validate(b, 1, 0); fail(); }
            catch (NoSuchVerseException ex) { }

            for (int c=1; c<=Books.chaptersInBook(b); c++)
            {
                try { Books.validate(b, c, 0); fail(); }
                catch (NoSuchVerseException ex) { }

                for (int v=1; v<=Books.versesInChapter(b, c); v++)
                {
                    Books.validate(b, c, v);
                    // This fn is tested as exhaustivly as the validate(int, int, int)
                    // version however since is is only a bit of sugar this is OK
                    Books.validate(new int[] {b, c, v});
                }
                try { Books.validate(b, c, Books.versesInChapter(b, c)+1); fail(); }
                catch (NoSuchVerseException ex) { }
            }

            try { Books.validate(67, Books.chaptersInBook(b)+1, 1); fail(); }
            catch (NoSuchVerseException ex) { }
            try { Books.validate(67, 1, Books.versesInBook(b)+1); fail(); }
            catch (NoSuchVerseException ex) { }
        }
        try { Books.validate(67, 1, 1); fail(); }
        catch (NoSuchVerseException ex) { }
    }

    public void testPatch() throws Exception
    {
        int all = 1;
        for (int b=1; b<Books.booksInBible(); b++)
        {
            for (int c=1; c<=Books.chaptersInBook(b); c++)
            {
                for (int v=1; v<=Books.versesInChapter(b, c); v++)
                {
                    int[] simple = { 1, 1, all++ };
                    int[] complex = { b, c, v };
                    Books.patch(simple);

                    assertEquals(simple[Books.BOOK],  complex[Books.BOOK]);
                    assertEquals(simple[Books.CHAPTER],  complex[Books.CHAPTER]);
                    assertEquals(simple[Books.VERSE],  complex[Books.VERSE]);
                }
            }
        }
        int[] v111 = { 1, 1, 1 };
        assertEquals(Books.verseCount(v111, Books.patch(new int[]{1,1,1})),  1);
        assertEquals(Books.verseCount(v111, Books.patch(new int[]{1,1,0})),  1);
        assertEquals(Books.verseCount(v111, Books.patch(new int[]{1,0,1})),  1);
        assertEquals(Books.verseCount(v111, Books.patch(new int[]{1,0,0})),  1);
        assertEquals(Books.verseCount(v111, Books.patch(new int[]{0,1,1})),  1);
        assertEquals(Books.verseCount(v111, Books.patch(new int[]{0,1,0})),  1);
        assertEquals(Books.verseCount(v111, Books.patch(new int[]{0,0,1})),  1);
        assertEquals(Books.verseCount(v111, Books.patch(new int[]{0,0,0})),  1);
    }

    public void testVerseCount() throws Exception
    {
        int count_up = 0;
        int count_down = 31102;
        for (int b=1; b<Books.booksInBible(); b++)
        {
            for (int c=1; c<=Books.chaptersInBook(b); c++)
            {
                for (int v=1; v<=Books.versesInChapter(b, c); v++)
                {
                    int up = Books.verseCount(1, 1, 1, b, c, v);
                    int down = Books.verseCount(b, c, v, 66, 22, 21);

                    assertEquals(up,  ++count_up);
                    assertEquals(down,  count_down--);

                    assertEquals(Books.verseCount(new int[] {1, 1, 1}, new int[] {b, c, v}),  verseCountSlow(1, 1, 1, b, c, v));
                }
            }

            assertEquals(Books.verseCount(b, 1, 1, b, Books.chaptersInBook(b), Books.versesInChapter(b, Books.chaptersInBook(b))),  Books.versesInBook(b));
        }
        assertEquals(Books.verseCount(1, 1, 1, 2, 1, 1),  Books.versesInBook(1)+1);
        assertEquals(Books.verseCount(1, 1, 1, 1, 1, 10),  10);
    }

    public void testNames() throws Exception
    {
        assertEquals(Books.Names.Genesis,  1);
        assertEquals(Books.Names.Revelation,  66);
    }

    /**
     * This is code from Books that was needed only as part of testing, so I
     * Moved it here. How many verses between ref1 and ref2 (inclusive).
     * @param book1 The book part of the first reference.
     * @param chapter1 The chapter part of the first reference.
     * @param verse1 The verse part of the first reference.
     * @param book2 The book part of the second reference.
     * @param chapter2 The chapter part of the second reference.
     * @param verse2 The verse part of the second reference.
     * @exception NoSuchVerseException If either reference is illegal
     */
    protected int verseCountSlow(int book1, int chapter1, int verse1, int book2, int chapter2, int verse2) throws NoSuchVerseException
    {
        Books.validate(book1, chapter1, verse1);
        Books.validate(book2, chapter2, verse2);

        int count = 0;

        // If we are in different books, count the verses until the books are the same
        if (book1 != book2)
        {
            // 1st count to the end of the chapter
            count += Books.versesInChapter(book1, chapter1) - verse1 + 1;

            // Then count from the end of the chapter to the end of the book
            for (int c=chapter1+1; c<=Books.chaptersInBook(book1); c++)
                count += Books.versesInChapter(book1, c);

            // Then count until the books are the same
            for (int b=book1+1; b<book2; b++)
                count += Books.versesInBook(b);

            // The new position
            book1 = book2;
            chapter1 = 1;
            verse1 = 1;
        }

        // Count the verses in the chapters so that we are in the same chapter
        for (int c=chapter1; c<chapter2; c++)
            count += Books.versesInChapter(book2, c);

        // And finally the verses in the final chapter
        count += verse2 - verse1 + 1;

        return count;
    }
}
