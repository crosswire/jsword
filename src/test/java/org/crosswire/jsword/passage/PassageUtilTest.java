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
package org.crosswire.jsword.passage;

import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.versification.BookName;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PassageUtilTest extends TestCase {
    public PassageUtilTest(String s) {
        super(s);
    }

    /**
     * How we create Passages
     */
    private static PassageKeyFactory keyf = PassageKeyFactory.instance();
    /** Control the output of names */
    private CaseType storedCase;
    private boolean fullName;
    private boolean persist;
    private Versification v11n;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        storedCase = BookName.getDefaultCase();
        BookName.setCase(CaseType.SENTENCE);
        fullName = BookName.isFullBookName();
        BookName.setFullBookName(false);
        persist = PassageUtil.isPersistentNaming();

        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getDefaultVersification();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        BookName.setCase(storedCase);
        BookName.setFullBookName(fullName);
        PassageUtil.setPersistentNaming(persist);
    }

    public void testOther() {
        // Need to add:
        /*
         * log("PassageUtil.[s|g]etBlurRestriction()");
         * log("PassageUtil.isValidBlurRestriction()");
         * log("PassageUtil.getBlurRestrictions()");
         * log("PassageUtil.isValidCase()"); log("PassageUtil.getCases()");
         * log("PassageUtil.isValidAccuracy()"); Should there be getAccuracies()
         */
    }

    public void testPersistentNaming() throws Exception {
        PassageUtil.setPersistentNaming(false);
        assertTrue(!PassageUtil.isPersistentNaming());
        assertEquals("Gen 1:1", VerseFactory.fromString(v11n, "Genesis 1 1").toString());
        assertEquals("Gen 1:1", VerseFactory.fromString(v11n, "Gen 1 1").toString());
        assertEquals("Gen 1:1", VerseFactory.fromString(v11n, "Genesis 1:1").toString());
        assertEquals("Gen 1:1", VerseFactory.fromString(v11n, "Gen 1 1").toString());
        assertEquals("Gal 1:1", VerseFactory.fromString(v11n, "g 1 1").toString());
        assertEquals("Gal 0:0", VerseFactory.fromString(v11n, "g").toString());
        assertEquals("Gal 1:1", VerseFactory.fromString(v11n, "G:1:1").toString());
        PassageUtil.setPersistentNaming(true);
        assertTrue(PassageUtil.isPersistentNaming());
        assertEquals("Genesis 1 1", VerseFactory.fromString(v11n, "Genesis 1 1").toString());
        assertEquals("Gen 1 1", VerseFactory.fromString(v11n, "Gen 1 1").toString());
        assertEquals("Genesis 1:1", VerseFactory.fromString(v11n, "Genesis 1:1").toString());
        assertEquals("Gen 1 1", VerseFactory.fromString(v11n, "Gen 1 1").toString());
        assertEquals("g 1 1", VerseFactory.fromString(v11n, "g 1 1").toString());
        assertEquals("g", VerseFactory.fromString(v11n, "g").toString());
        assertEquals("G:1:1", VerseFactory.fromString(v11n, "G:1:1").toString());
    }

    public void testBinary() {
        byte[] buffer;
        int[] index;

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 0, 0));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 0));
        assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 0, 1));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 1));
        assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 0, 255));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 255));
        assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(2, PassageKeyFactory.toBinary(buffer, 0, 0, 256));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 256));
        assertEquals(2, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(2, PassageKeyFactory.toBinary(buffer, 0, 0, 65535));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 65535));
        assertEquals(2, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(3, PassageKeyFactory.toBinary(buffer, 0, 0, 65536));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 65536));
        assertEquals(3, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(3, PassageKeyFactory.toBinary(buffer, 0, 0, 16777215));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 16777215));
        assertEquals(3, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(4, PassageKeyFactory.toBinary(buffer, 0, 0, 16777216));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 16777216));
        assertEquals(4, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(4, PassageKeyFactory.toBinary(buffer, 0, 0, 2147483647));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 2147483647));
        assertEquals(4, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 1, 1));
        assertEquals(buffer[0], 1);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(1, PassageKeyFactory.fromBinary(buffer, index, 1));
        assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 255, 255));
        assertEquals(buffer[0], -1);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(255, PassageKeyFactory.fromBinary(buffer, index, 255));
        assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        assertEquals(2, PassageKeyFactory.toBinary(buffer, 0, 65535, 65535));
        assertEquals(buffer[0], -1);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(65535, PassageKeyFactory.fromBinary(buffer, index, 65535));
        assertEquals(2, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        assertEquals(3, PassageKeyFactory.toBinary(buffer, 0, 16777215, 16777215));
        assertEquals(buffer[0], -1);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], 0);
        assertEquals(16777215, PassageKeyFactory.fromBinary(buffer, index, 16777215));
        assertEquals(3, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        assertEquals(4, PassageKeyFactory.toBinary(buffer, 0, 2147483647, 2147483647));
        assertEquals(buffer[0], 127);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(2147483647, PassageKeyFactory.fromBinary(buffer, index, 2147483647));
        assertEquals(4, index[0]);

        index = new int[] {
            2
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        assertEquals(1, PassageKeyFactory.toBinary(buffer, 2, 10, 11));
        assertEquals(buffer[0], -1);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], 10);
        assertEquals(buffer[3], -1);
        assertEquals(10, PassageKeyFactory.fromBinary(buffer, index, 11));
        assertEquals(3, index[0]);

        for (int i = 0; i < 2147400000; i += 10000) {
            index = new int[] {
                0
            };
            buffer = new byte[] {
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,
            };
            assertEquals(4, PassageKeyFactory.toBinary(buffer, 0, i, 2147483647));
            assertEquals(i, PassageKeyFactory.fromBinary(buffer, index, 2147483647));
            assertEquals(4, index[0]);
        }
    }

    public void testBinaryRepresentation() throws Exception {
        Passage gen1_135 = (Passage) keyf.getKey(v11n, "Gen 1:1, Gen 1:3, Gen 1:5");
        Passage exo2a_3b = (Passage) keyf.getKey(v11n, "Exo 2:1-10, Exo 3:1-11");
        Passage gen_rev = (Passage) keyf.getKey(v11n, "Gen 1:1-Rev 22:21");
        Passage hard = (Passage) keyf.createEmptyKeyList(v11n);
        Passage empty = (Passage) keyf.createEmptyKeyList(v11n);

        for (int i = 1; i < v11n.maximumOrdinal(); i += 10) {
            hard.add(v11n.decodeOrdinal(i));
        }

        byte[] temp = PassageKeyFactory.toBinaryRepresentation(gen1_135);
        Passage gen1_135_copy = PassageKeyFactory.fromBinaryRepresentation(temp);
        assertEquals(gen1_135_copy, gen1_135);
        temp = PassageKeyFactory.toBinaryRepresentation(exo2a_3b);
        Passage exo2a_3b_copy = PassageKeyFactory.fromBinaryRepresentation(temp);
        assertEquals(exo2a_3b_copy, exo2a_3b);
        temp = PassageKeyFactory.toBinaryRepresentation(gen_rev);
        Passage gen_rev_copy = PassageKeyFactory.fromBinaryRepresentation(temp);
        assertEquals(gen_rev_copy, gen_rev);
        temp = PassageKeyFactory.toBinaryRepresentation(hard);
        Passage hard_copy = PassageKeyFactory.fromBinaryRepresentation(temp);
        assertEquals(hard_copy, hard);
        temp = PassageKeyFactory.toBinaryRepresentation(empty);
        Passage empty_copy = PassageKeyFactory.fromBinaryRepresentation(temp);
        assertEquals(empty_copy, empty);
    }
}
