
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class PassageUtilTest extends TestCase
{
    public PassageUtilTest(String s)
    {
        super(s);
    }

    protected void setUp()
    {
    }

    protected void tearDown()
    {
    }

    public void testOther() throws Exception
    {
        // Need to add:
        /*
        log("PassageUtil.[s|g]etBlurRestriction()");
        log("PassageUtil.isValidBlurRestriction()");
        log("PassageUtil.getBlurRestrictions()");
        log("PassageUtil.isValidCase()");
        log("PassageUtil.getCases()");
        log("PassageUtil.isValidAccuracy()");
        Should there be getAccuracies()
        */
    }

    public void testGetCase() throws Exception
    {
        assertEquals(PassageUtil.getCase("FRED"), PassageConstants.CASE_UPPER); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("F-ED"), PassageConstants.CASE_UPPER); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("F00D"), PassageConstants.CASE_UPPER); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("fred"), PassageConstants.CASE_LOWER); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("f-ed"), PassageConstants.CASE_LOWER); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("f00d"), PassageConstants.CASE_LOWER); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("Fred"), PassageConstants.CASE_SENTANCE); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("F-ed"), PassageConstants.CASE_SENTANCE); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("F00d"), PassageConstants.CASE_SENTANCE); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("fRED"), PassageConstants.CASE_MIXED); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("frED"), PassageConstants.CASE_MIXED); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("freD"), PassageConstants.CASE_MIXED); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase("LORD's"), PassageConstants.CASE_MIXED); //$NON-NLS-1$
        assertEquals(PassageUtil.getCase(""), PassageConstants.CASE_LOWER); //$NON-NLS-1$
        try { PassageUtil.getCase(null); fail(); }
        catch (NullPointerException ex) { }
        // The results of this are undefined so
        // assertEquals(PassageUtil.getCase("FreD"), PassageConstants.CASE_SENTANCE);
    }

    public void testSetCase() throws Exception
    {
        assertEquals(PassageUtil.setCase("FRED", PassageConstants.CASE_UPPER), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("Fred", PassageConstants.CASE_UPPER), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("fred", PassageConstants.CASE_UPPER), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("frED", PassageConstants.CASE_UPPER), "FRED"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("fr00", PassageConstants.CASE_UPPER), "FR00"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("fr=_", PassageConstants.CASE_UPPER), "FR=_"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("FRED", PassageConstants.CASE_LOWER), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("Fred", PassageConstants.CASE_LOWER), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("fred", PassageConstants.CASE_LOWER), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("frED", PassageConstants.CASE_LOWER), "fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("fr00", PassageConstants.CASE_LOWER), "fr00"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("fr=_", PassageConstants.CASE_LOWER), "fr=_"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("FRED", PassageConstants.CASE_SENTANCE), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("Fred", PassageConstants.CASE_SENTANCE), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("fred", PassageConstants.CASE_SENTANCE), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("frED", PassageConstants.CASE_SENTANCE), "Fred"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("fr00", PassageConstants.CASE_SENTANCE), "Fr00"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("fr=_", PassageConstants.CASE_SENTANCE), "Fr=_"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("lord's", PassageConstants.CASE_MIXED), "LORD's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("LORD's", PassageConstants.CASE_MIXED), "LORD's"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("no-one", PassageConstants.CASE_LOWER), "no-one"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("no-one", PassageConstants.CASE_UPPER), "NO-ONE"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("no-one", PassageConstants.CASE_SENTANCE), "No-one"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("xx-one", PassageConstants.CASE_LOWER), "xx-one"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("xx-one", PassageConstants.CASE_UPPER), "XX-ONE"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("xx-one", PassageConstants.CASE_SENTANCE), "Xx-One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("god-inspired", PassageConstants.CASE_SENTANCE), "God-inspired"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("god-breathed", PassageConstants.CASE_SENTANCE), "God-breathed"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("maher-shalal-hash-baz", PassageConstants.CASE_SENTANCE), "Maher-Shalal-Hash-Baz"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("", PassageConstants.CASE_LOWER), ""); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("", PassageConstants.CASE_UPPER), ""); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.setCase("", PassageConstants.CASE_SENTANCE), ""); //$NON-NLS-1$ //$NON-NLS-2$
        try { PassageUtil.setCase("god-inspired", PassageConstants.CASE_MIXED); fail(); } //$NON-NLS-1$
        catch (IllegalArgumentException ex) { }
        try { PassageUtil.setCase("fred", -1); fail(); } //$NON-NLS-1$
        catch (IllegalArgumentException ex) { }
        try { PassageUtil.setCase("fred", 4); fail(); } //$NON-NLS-1$
        catch (IllegalArgumentException ex) { }
    }

    public void testPersistentNaming() throws Exception
    {
        boolean stored_naming = PassageUtil.isPersistentNaming();
        PassageUtil.setPersistentNaming(false);
        assertTrue(!PassageUtil.isPersistentNaming());
        assertEquals(new Verse("Genesis 1 1").toString(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("Gen 1 1").toString(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("Genesis 1:1").toString(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("Gen 1 1").toString(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("g 1 1").toString(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("g").toString(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("G:1:1").toString(), "Gen 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        PassageUtil.setPersistentNaming(true);
        assertTrue(PassageUtil.isPersistentNaming());
        assertEquals(new Verse("Genesis 1 1").toString(), "Genesis 1 1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("Gen 1 1").toString(), "Gen 1 1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("Genesis 1:1").toString(), "Genesis 1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("Gen 1 1").toString(), "Gen 1 1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("g 1 1").toString(), "g 1 1"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("g").toString(), "g"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new Verse("G:1:1").toString(), "G:1:1"); //$NON-NLS-1$ //$NON-NLS-2$
        PassageUtil.setPersistentNaming(stored_naming);
    }

    public void testTokenize() throws Exception
    {
        /*
        log("PassageUtil.tokenize()");
        String[] temp = PassageUtil.tokenize(" one  two three ", " ");
        assertEquals(temp.length, 3);
        assertEquals(temp[0], "one");
        assertEquals(temp[1], "two");
        assertEquals(temp[2], "three");
        */
    }

    public void testToSentenceCase() throws Exception
    {
        assertEquals(PassageUtil.toSentenceCase("one"), "One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.toSentenceCase("one two"), "One two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.toSentenceCase("ONE"), "One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.toSentenceCase("ONE TWO"), "One two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.toSentenceCase("onE"), "One"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.toSentenceCase("onE twO"), "One two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.toSentenceCase("12345"), "12345"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.toSentenceCase("1 two"), "1 two"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(PassageUtil.toSentenceCase("1 TWO"), "1 two"); //$NON-NLS-1$ //$NON-NLS-2$
        try { PassageUtil.toSentenceCase(null); fail(); }
        catch (NullPointerException ex) { }
    }

    public void testBinary() throws Exception
    {
        byte[] buffer;
        int[] index;

        index = new int[] { 0 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(1, PassageUtil.toBinary(buffer, 0, 0, 0));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageUtil.fromBinary(buffer, index, 0));
        assertEquals(1, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(1, PassageUtil.toBinary(buffer, 0, 0, 1));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageUtil.fromBinary(buffer, index, 1));
        assertEquals(1, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(1, PassageUtil.toBinary(buffer, 0, 0, 255));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageUtil.fromBinary(buffer, index, 255));
        assertEquals(1, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(2, PassageUtil.toBinary(buffer, 0, 0, 256));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageUtil.fromBinary(buffer, index, 256));
        assertEquals(2, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(2, PassageUtil.toBinary(buffer, 0, 0, 65535));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageUtil.fromBinary(buffer, index, 65535));
        assertEquals(2, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(3, PassageUtil.toBinary(buffer, 0, 0, 65536));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageUtil.fromBinary(buffer, index, 65536));
        assertEquals(3, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(3, PassageUtil.toBinary(buffer, 0, 0, 16777215));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], -1);
        assertEquals(0, PassageUtil.fromBinary(buffer, index, 16777215));
        assertEquals(3, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(4, PassageUtil.toBinary(buffer, 0, 0, 16777216));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(0, PassageUtil.fromBinary(buffer, index, 16777216));
        assertEquals(4, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(4, PassageUtil.toBinary(buffer, 0, 0, 2147483647));
        assertEquals(buffer[0], 0);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(0, PassageUtil.fromBinary(buffer, index, 2147483647));
        assertEquals(4, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, };
        assertEquals(1, PassageUtil.toBinary(buffer, 0, 1, 1));
        assertEquals(buffer[0], 1);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(1, PassageUtil.fromBinary(buffer, index, 1));
        assertEquals(1, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, };
        assertEquals(1, PassageUtil.toBinary(buffer, 0, 255, 255));
        assertEquals(buffer[0], -1);
        assertEquals(buffer[1], 0);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(255, PassageUtil.fromBinary(buffer, index, 255));
        assertEquals(1, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, };
        assertEquals(2, PassageUtil.toBinary(buffer, 0, 65535, 65535));
        assertEquals(buffer[0], -1);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], 0);
        assertEquals(buffer[3], 0);
        assertEquals(65535, PassageUtil.fromBinary(buffer, index, 65535));
        assertEquals(2, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, };
        assertEquals(3, PassageUtil.toBinary(buffer, 0, 16777215, 16777215));
        assertEquals(buffer[0], -1);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], 0);
        assertEquals(16777215, PassageUtil.fromBinary(buffer, index, 16777215));
        assertEquals(3, index[0]);

        index = new int[] { 0 };
        buffer = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, };
        assertEquals(4, PassageUtil.toBinary(buffer, 0, 2147483647, 2147483647));
        assertEquals(buffer[0], 127);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], -1);
        assertEquals(buffer[3], -1);
        assertEquals(2147483647, PassageUtil.fromBinary(buffer, index, 2147483647));
        assertEquals(4, index[0]);

        index = new int[] { 2 };
        buffer = new byte[] { (byte) -1, (byte) -1, (byte) -1, (byte) -1, };
        assertEquals(1, PassageUtil.toBinary(buffer, 2, 10, 11));
        assertEquals(buffer[0], -1);
        assertEquals(buffer[1], -1);
        assertEquals(buffer[2], 10);
        assertEquals(buffer[3], -1);
        assertEquals(10, PassageUtil.fromBinary(buffer, index, 11));
        assertEquals(3, index[0]);

        for (int i=0; i<2147400000; i+=10000)
        {
            index = new int[] { 0 };
            buffer = new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 0, };
            assertEquals(4, PassageUtil.toBinary(buffer, 0, i, 2147483647));
            assertEquals(i, PassageUtil.fromBinary(buffer, index, 2147483647));
            assertEquals(4, index[0]);
        }
    }

    public void testBinaryRepresentation() throws Exception
    {
        Passage gen1_135 = PassageFactory.createPassage("Gen 1:1, Gen 1:3, Gen 1:5"); //$NON-NLS-1$
        Passage exo2a_3b = PassageFactory.createPassage("Exo 2:1-10, Exo 3:1-11"); //$NON-NLS-1$
        Passage gen_rev = PassageFactory.createPassage("Gen 1:1-Rev 22:21"); //$NON-NLS-1$
        Passage hard = PassageFactory.createPassage();
        Passage empty = PassageFactory.createPassage();
        for (int i=1; i<BibleInfo.versesInBible(); i+=10)
        {
            hard.add(new Verse(i));
        }
        byte[] temp = PassageUtil.toBinaryRepresentation(gen1_135);
        Passage gen1_135_copy = PassageUtil.fromBinaryRepresentation(temp);
        assertEquals(gen1_135_copy, gen1_135);
        temp = PassageUtil.toBinaryRepresentation(exo2a_3b);
        Passage exo2a_3b_copy = PassageUtil.fromBinaryRepresentation(temp);
        assertEquals(exo2a_3b_copy, exo2a_3b);
        temp = PassageUtil.toBinaryRepresentation(gen_rev);
        Passage gen_rev_copy = PassageUtil.fromBinaryRepresentation(temp);
        assertEquals(gen_rev_copy, gen_rev);
        temp = PassageUtil.toBinaryRepresentation(hard);
        Passage hard_copy = PassageUtil.fromBinaryRepresentation(temp);
        assertEquals(hard_copy, hard);
        temp = PassageUtil.toBinaryRepresentation(empty);
        Passage empty_copy = PassageUtil.fromBinaryRepresentation(temp);
        assertEquals(empty_copy, empty);
    }
}
