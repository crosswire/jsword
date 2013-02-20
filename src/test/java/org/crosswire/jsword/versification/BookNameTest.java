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
 * Copyright: 2005 - 2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.versification;

import junit.framework.TestCase;

import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * JUnit test of BookName
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookNameTest extends TestCase {
    public BookNameTest(String s) {
        super(s);
    }

    private CaseType storedCase;
    private boolean fullName;
    private Versification v11n;

    @Override
    protected void setUp() {
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getDefaultVersification();
        storedCase = BookName.getDefaultCase();
        BookName.setCase(CaseType.SENTENCE);
        fullName = BookName.isFullBookName();
        BookName.setFullBookName(false);

    }

    @Override
    protected void tearDown() {
        BookName.setCase(storedCase);
        BookName.setFullBookName(fullName);
    }

    public void testCase() {
        BookName.setCase(CaseType.LOWER);
        assertEquals(CaseType.LOWER, BookName.getDefaultCase());

        BookName.setCase(CaseType.UPPER);
        assertEquals(CaseType.UPPER, BookName.getDefaultCase());

        BookName.setCase(CaseType.SENTENCE);
        assertEquals(CaseType.SENTENCE, BookName.getDefaultCase());
    }

    public void testGetLongBookName() throws Exception {
        BookName.setCase(CaseType.SENTENCE);
        assertEquals("Genesis", v11n.getLongName(BibleBook.GEN));
        assertEquals("Revelation of John", v11n.getLongName(BibleBook.REV));

        BookName.setCase(CaseType.LOWER);
        assertEquals("genesis", v11n.getLongName(BibleBook.GEN));
        assertEquals("revelation of john", v11n.getLongName(BibleBook.REV));

        BookName.setCase(CaseType.UPPER);
        assertEquals("GENESIS", v11n.getLongName(BibleBook.GEN));
        assertEquals("REVELATION OF JOHN", v11n.getLongName(BibleBook.REV));

    }

    public void testGetShortBookName() throws Exception {
        BookName.setCase(CaseType.SENTENCE);
        assertEquals("Gen", v11n.getShortName(BibleBook.GEN));
        assertEquals("Exo", v11n.getShortName(BibleBook.EXOD));
        assertEquals("Judg", v11n.getShortName(BibleBook.JUDG));
        assertEquals("Mal", v11n.getShortName(BibleBook.MAL));
        assertEquals("Mat", v11n.getShortName(BibleBook.MATT));
        assertEquals("Phili", v11n.getShortName(BibleBook.PHIL));
        assertEquals("Phile", v11n.getShortName(BibleBook.PHLM));
        assertEquals("Jude", v11n.getShortName(BibleBook.JUDE));
        assertEquals("Rev", v11n.getShortName(BibleBook.REV));

        BookName.setCase(CaseType.LOWER);
        assertEquals("gen", v11n.getShortName(BibleBook.GEN));
        assertEquals("exo", v11n.getShortName(BibleBook.EXOD));
        assertEquals("judg", v11n.getShortName(BibleBook.JUDG));
        assertEquals("mal", v11n.getShortName(BibleBook.MAL));
        assertEquals("mat", v11n.getShortName(BibleBook.MATT));
        assertEquals("phili", v11n.getShortName(BibleBook.PHIL));
        assertEquals("phile", v11n.getShortName(BibleBook.PHLM));
        assertEquals("jude", v11n.getShortName(BibleBook.JUDE));
        assertEquals("rev", v11n.getShortName(BibleBook.REV));

        BookName.setCase(CaseType.UPPER);
        assertEquals("GEN", v11n.getShortName(BibleBook.GEN));
        assertEquals("EXO", v11n.getShortName(BibleBook.EXOD));
        assertEquals("JUDG", v11n.getShortName(BibleBook.JUDG));
        assertEquals("MAL", v11n.getShortName(BibleBook.MAL));
        assertEquals("MAT", v11n.getShortName(BibleBook.MATT));
        assertEquals("PHILI", v11n.getShortName(BibleBook.PHIL));
        assertEquals("PHILE", v11n.getShortName(BibleBook.PHLM));
        assertEquals("JUDE", v11n.getShortName(BibleBook.JUDE));
        assertEquals("REV", v11n.getShortName(BibleBook.REV));
    }

    public void testGetBookJogger() throws Exception {
        assertEquals("Gen", BibleBook.GEN.getOSIS());
        assertEquals("Exod", BibleBook.EXOD.getOSIS());
        assertEquals("Rev", BibleBook.REV.getOSIS());
    }

}
