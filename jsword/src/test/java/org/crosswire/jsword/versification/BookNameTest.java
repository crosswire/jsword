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

    @Override
    protected void setUp() {
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
        assertEquals("Genesis", BibleBook.GEN.getLongName());
        assertEquals("Revelation of John", BibleBook.REV.getLongName());

        BookName.setCase(CaseType.LOWER);
        assertEquals("genesis", BibleBook.GEN.getLongName());
        assertEquals("revelation of john", BibleBook.REV.getLongName());

        BookName.setCase(CaseType.UPPER);
        assertEquals("GENESIS", BibleBook.GEN.getLongName());
        assertEquals("REVELATION OF JOHN", BibleBook.REV.getLongName());

    }

    public void testGetShortBookName() throws Exception {
        BookName.setCase(CaseType.SENTENCE);
        assertEquals("Gen", BibleBook.GEN.getShortName());
        assertEquals("Exo", BibleBook.EXOD.getShortName());
        assertEquals("Judg", BibleBook.JUDG.getShortName());
        assertEquals("Mal", BibleBook.MAL.getShortName());
        assertEquals("Mat", BibleBook.MATT.getShortName());
        assertEquals("Phili", BibleBook.PHIL.getShortName());
        assertEquals("Phile", BibleBook.PHLM.getShortName());
        assertEquals("Jude", BibleBook.JUDE.getShortName());
        assertEquals("Rev", BibleBook.REV.getShortName());

        BookName.setCase(CaseType.LOWER);
        assertEquals("gen", BibleBook.GEN.getShortName());
        assertEquals("exo", BibleBook.EXOD.getShortName());
        assertEquals("judg", BibleBook.JUDG.getShortName());
        assertEquals("mal", BibleBook.MAL.getShortName());
        assertEquals("mat", BibleBook.MATT.getShortName());
        assertEquals("phili", BibleBook.PHIL.getShortName());
        assertEquals("phile", BibleBook.PHLM.getShortName());
        assertEquals("jude", BibleBook.JUDE.getShortName());
        assertEquals("rev", BibleBook.REV.getShortName());

        BookName.setCase(CaseType.UPPER);
        assertEquals("GEN", BibleBook.GEN.getShortName());
        assertEquals("EXO", BibleBook.EXOD.getShortName());
        assertEquals("JUDG", BibleBook.JUDG.getShortName());
        assertEquals("MAL", BibleBook.MAL.getShortName());
        assertEquals("MAT", BibleBook.MATT.getShortName());
        assertEquals("PHILI", BibleBook.PHIL.getShortName());
        assertEquals("PHILE", BibleBook.PHLM.getShortName());
        assertEquals("JUDE", BibleBook.JUDE.getShortName());
        assertEquals("REV", BibleBook.REV.getShortName());
    }

    public void testGetBookJogger() throws Exception {
        assertEquals("Gen", BibleBook.GEN.getOSIS());
        assertEquals("Exod", BibleBook.EXOD.getOSIS());
        assertEquals("Rev", BibleBook.REV.getOSIS());
    }

}
