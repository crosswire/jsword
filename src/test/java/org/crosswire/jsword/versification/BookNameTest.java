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
package org.crosswire.jsword.versification;

import org.crosswire.jsword.book.CaseType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test of BookName
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public class BookNameTest {
    private CaseType storedCase;

    @Before
    public void setUp() {
        storedCase = BookName.getDefaultCase();
    }

    @After
    public void tearDown() {
        BookName.setCase(storedCase);
    }

    @Test
    public void testCase() {
        BookName.setCase(CaseType.LOWER);
        Assert.assertEquals(CaseType.LOWER, BookName.getDefaultCase());

        BookName.setCase(CaseType.UPPER);
        Assert.assertEquals(CaseType.UPPER, BookName.getDefaultCase());

        BookName.setCase(CaseType.SENTENCE);
        Assert.assertEquals(CaseType.SENTENCE, BookName.getDefaultCase());
    }

    @Test
    public void testGetBookJogger() throws Exception {
        Assert.assertEquals("Gen", BibleBook.GEN.getOSIS());
        Assert.assertEquals("Exod", BibleBook.EXOD.getOSIS());
        Assert.assertEquals("Rev", BibleBook.REV.getOSIS());
    }

}
