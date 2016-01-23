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
package org.crosswire.jsword.passage;

import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.versification.BookName;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class PassageUtilTest {
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
    @Before
    public void setUp() throws Exception {
        storedCase = BookName.getDefaultCase();
        BookName.setCase(CaseType.SENTENCE);
        fullName = BookName.isFullBookName();
        BookName.setFullBookName(false);
        persist = PassageUtil.isPersistentNaming();

        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getVersification("KJV");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        BookName.setCase(storedCase);
        BookName.setFullBookName(fullName);
        PassageUtil.setPersistentNaming(persist);
    }

    @Test
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

    @Test
    public void testBinary() {
        byte[] buffer;
        int[] index;

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 0, 0));
        Assert.assertEquals(buffer[0], 0);
        Assert.assertEquals(buffer[1], -1);
        Assert.assertEquals(buffer[2], -1);
        Assert.assertEquals(buffer[3], -1);
        Assert.assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 0));
        Assert.assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 0, 1));
        Assert.assertEquals(buffer[0], 0);
        Assert.assertEquals(buffer[1], -1);
        Assert.assertEquals(buffer[2], -1);
        Assert.assertEquals(buffer[3], -1);
        Assert.assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 1));
        Assert.assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 0, 255));
        Assert.assertEquals(buffer[0], 0);
        Assert.assertEquals(buffer[1], -1);
        Assert.assertEquals(buffer[2], -1);
        Assert.assertEquals(buffer[3], -1);
        Assert.assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 255));
        Assert.assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(2, PassageKeyFactory.toBinary(buffer, 0, 0, 256));
        Assert.assertEquals(buffer[0], 0);
        Assert.assertEquals(buffer[1], 0);
        Assert.assertEquals(buffer[2], -1);
        Assert.assertEquals(buffer[3], -1);
        Assert.assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 256));
        Assert.assertEquals(2, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(2, PassageKeyFactory.toBinary(buffer, 0, 0, 65535));
        Assert.assertEquals(buffer[0], 0);
        Assert.assertEquals(buffer[1], 0);
        Assert.assertEquals(buffer[2], -1);
        Assert.assertEquals(buffer[3], -1);
        Assert.assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 65535));
        Assert.assertEquals(2, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(3, PassageKeyFactory.toBinary(buffer, 0, 0, 65536));
        Assert.assertEquals(buffer[0], 0);
        Assert.assertEquals(buffer[1], 0);
        Assert.assertEquals(buffer[2], 0);
        Assert.assertEquals(buffer[3], -1);
        Assert.assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 65536));
        Assert.assertEquals(3, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(3, PassageKeyFactory.toBinary(buffer, 0, 0, 16777215));
        Assert.assertEquals(buffer[0], 0);
        Assert.assertEquals(buffer[1], 0);
        Assert.assertEquals(buffer[2], 0);
        Assert.assertEquals(buffer[3], -1);
        Assert.assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 16777215));
        Assert.assertEquals(3, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(4, PassageKeyFactory.toBinary(buffer, 0, 0, 16777216));
        Assert.assertEquals(buffer[0], 0);
        Assert.assertEquals(buffer[1], 0);
        Assert.assertEquals(buffer[2], 0);
        Assert.assertEquals(buffer[3], 0);
        Assert.assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 16777216));
        Assert.assertEquals(4, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(4, PassageKeyFactory.toBinary(buffer, 0, 0, 2147483647));
        Assert.assertEquals(buffer[0], 0);
        Assert.assertEquals(buffer[1], 0);
        Assert.assertEquals(buffer[2], 0);
        Assert.assertEquals(buffer[3], 0);
        Assert.assertEquals(0, PassageKeyFactory.fromBinary(buffer, index, 2147483647));
        Assert.assertEquals(4, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        Assert.assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 1, 1));
        Assert.assertEquals(buffer[0], 1);
        Assert.assertEquals(buffer[1], 0);
        Assert.assertEquals(buffer[2], 0);
        Assert.assertEquals(buffer[3], 0);
        Assert.assertEquals(1, PassageKeyFactory.fromBinary(buffer, index, 1));
        Assert.assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        Assert.assertEquals(1, PassageKeyFactory.toBinary(buffer, 0, 255, 255));
        Assert.assertEquals(buffer[0], -1);
        Assert.assertEquals(buffer[1], 0);
        Assert.assertEquals(buffer[2], 0);
        Assert.assertEquals(buffer[3], 0);
        Assert.assertEquals(255, PassageKeyFactory.fromBinary(buffer, index, 255));
        Assert.assertEquals(1, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        Assert.assertEquals(2, PassageKeyFactory.toBinary(buffer, 0, 65535, 65535));
        Assert.assertEquals(buffer[0], -1);
        Assert.assertEquals(buffer[1], -1);
        Assert.assertEquals(buffer[2], 0);
        Assert.assertEquals(buffer[3], 0);
        Assert.assertEquals(65535, PassageKeyFactory.fromBinary(buffer, index, 65535));
        Assert.assertEquals(2, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        Assert.assertEquals(3, PassageKeyFactory.toBinary(buffer, 0, 16777215, 16777215));
        Assert.assertEquals(buffer[0], -1);
        Assert.assertEquals(buffer[1], -1);
        Assert.assertEquals(buffer[2], -1);
        Assert.assertEquals(buffer[3], 0);
        Assert.assertEquals(16777215, PassageKeyFactory.fromBinary(buffer, index, 16777215));
        Assert.assertEquals(3, index[0]);

        index = new int[] {
            0
        };
        buffer = new byte[] {
                (byte) 0, (byte) 0, (byte) 0, (byte) 0,
        };
        Assert.assertEquals(4, PassageKeyFactory.toBinary(buffer, 0, 2147483647, 2147483647));
        Assert.assertEquals(buffer[0], 127);
        Assert.assertEquals(buffer[1], -1);
        Assert.assertEquals(buffer[2], -1);
        Assert.assertEquals(buffer[3], -1);
        Assert.assertEquals(2147483647, PassageKeyFactory.fromBinary(buffer, index, 2147483647));
        Assert.assertEquals(4, index[0]);

        index = new int[] {
            2
        };
        buffer = new byte[] {
                (byte) -1, (byte) -1, (byte) -1, (byte) -1,
        };
        Assert.assertEquals(1, PassageKeyFactory.toBinary(buffer, 2, 10, 11));
        Assert.assertEquals(buffer[0], -1);
        Assert.assertEquals(buffer[1], -1);
        Assert.assertEquals(buffer[2], 10);
        Assert.assertEquals(buffer[3], -1);
        Assert.assertEquals(10, PassageKeyFactory.fromBinary(buffer, index, 11));
        Assert.assertEquals(3, index[0]);

        for (int i = 0; i < 2147400000; i += 10000) {
            index = new int[] {
                0
            };
            buffer = new byte[] {
                    (byte) 0, (byte) 0, (byte) 0, (byte) 0,
            };
            Assert.assertEquals(4, PassageKeyFactory.toBinary(buffer, 0, i, 2147483647));
            Assert.assertEquals(i, PassageKeyFactory.fromBinary(buffer, index, 2147483647));
            Assert.assertEquals(4, index[0]);
        }
    }

    @Test
    public void testBinaryRepresentation() throws Exception {
        Passage genC1V135 = keyf.getKey(v11n, "Gen 1:1, Gen 1:3, Gen 1:5");
        Passage exo2aTo3b = keyf.getKey(v11n, "Exo 2:1-10, Exo 3:1-11");
        Passage gentoRev = keyf.getKey(v11n, "Gen 1:1-Rev 22:21");
        Passage hard = (Passage) keyf.createEmptyKeyList(v11n);
        Passage empty = (Passage) keyf.createEmptyKeyList(v11n);

        for (int i = 1; i < v11n.maximumOrdinal(); i += 10) {
            hard.add(v11n.decodeOrdinal(i));
        }

        byte[] temp = PassageKeyFactory.toBinaryRepresentation(genC1V135);
        Passage genC1V135Copy = PassageKeyFactory.fromBinaryRepresentation(temp);
        Assert.assertEquals(genC1V135Copy, genC1V135);
        temp = PassageKeyFactory.toBinaryRepresentation(exo2aTo3b);
        Passage exo2aTo3bCopy = PassageKeyFactory.fromBinaryRepresentation(temp);
        Assert.assertEquals(exo2aTo3bCopy, exo2aTo3b);
        temp = PassageKeyFactory.toBinaryRepresentation(gentoRev);
        Passage genToRevCopy = PassageKeyFactory.fromBinaryRepresentation(temp);
        Assert.assertEquals(genToRevCopy, gentoRev);
        temp = PassageKeyFactory.toBinaryRepresentation(hard);
        Passage hardCopy = PassageKeyFactory.fromBinaryRepresentation(temp);
        Assert.assertEquals(hardCopy, hard);
        temp = PassageKeyFactory.toBinaryRepresentation(empty);
        Passage emptyCopy = PassageKeyFactory.fromBinaryRepresentation(temp);
        Assert.assertEquals(emptyCopy, empty);
    }
}
