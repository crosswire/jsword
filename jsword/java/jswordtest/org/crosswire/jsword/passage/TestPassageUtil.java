
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
public class TestPassageUtil extends TestCase
{
    public TestPassageUtil(String s)
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
        assertEquals(PassageUtil.getCase("FRED"), Passage.CASE_UPPER);
        assertEquals(PassageUtil.getCase("F-ED"), Passage.CASE_UPPER);
        assertEquals(PassageUtil.getCase("F00D"), Passage.CASE_UPPER);
        assertEquals(PassageUtil.getCase("fred"), Passage.CASE_LOWER);
        assertEquals(PassageUtil.getCase("f-ed"), Passage.CASE_LOWER);
        assertEquals(PassageUtil.getCase("f00d"), Passage.CASE_LOWER);
        assertEquals(PassageUtil.getCase("Fred"), Passage.CASE_SENTANCE);
        assertEquals(PassageUtil.getCase("F-ed"), Passage.CASE_SENTANCE);
        assertEquals(PassageUtil.getCase("F00d"), Passage.CASE_SENTANCE);
        assertEquals(PassageUtil.getCase("fRED"), Passage.CASE_MIXED);
        assertEquals(PassageUtil.getCase("frED"), Passage.CASE_MIXED);
        assertEquals(PassageUtil.getCase("freD"), Passage.CASE_MIXED);
        assertEquals(PassageUtil.getCase("LORD's"), Passage.CASE_MIXED);
        assertEquals(PassageUtil.getCase(""), Passage.CASE_LOWER);
        try { PassageUtil.getCase(null); fail(); }
        catch (NullPointerException ex) { }
        // The results of this are undefined so
        // assertEquals(PassageUtil.getCase("FreD"), Passage.CASE_SENTANCE);
    }

    public void testSetCase() throws Exception
    {
        assertEquals(PassageUtil.setCase("FRED", Passage.CASE_UPPER), "FRED");
        assertEquals(PassageUtil.setCase("Fred", Passage.CASE_UPPER), "FRED");
        assertEquals(PassageUtil.setCase("fred", Passage.CASE_UPPER), "FRED");
        assertEquals(PassageUtil.setCase("frED", Passage.CASE_UPPER), "FRED");
        assertEquals(PassageUtil.setCase("fr00", Passage.CASE_UPPER), "FR00");
        assertEquals(PassageUtil.setCase("fr=_", Passage.CASE_UPPER), "FR=_");
        assertEquals(PassageUtil.setCase("FRED", Passage.CASE_LOWER), "fred");
        assertEquals(PassageUtil.setCase("Fred", Passage.CASE_LOWER), "fred");
        assertEquals(PassageUtil.setCase("fred", Passage.CASE_LOWER), "fred");
        assertEquals(PassageUtil.setCase("frED", Passage.CASE_LOWER), "fred");
        assertEquals(PassageUtil.setCase("fr00", Passage.CASE_LOWER), "fr00");
        assertEquals(PassageUtil.setCase("fr=_", Passage.CASE_LOWER), "fr=_");
        assertEquals(PassageUtil.setCase("FRED", Passage.CASE_SENTANCE), "Fred");
        assertEquals(PassageUtil.setCase("Fred", Passage.CASE_SENTANCE), "Fred");
        assertEquals(PassageUtil.setCase("fred", Passage.CASE_SENTANCE), "Fred");
        assertEquals(PassageUtil.setCase("frED", Passage.CASE_SENTANCE), "Fred");
        assertEquals(PassageUtil.setCase("fr00", Passage.CASE_SENTANCE), "Fr00");
        assertEquals(PassageUtil.setCase("fr=_", Passage.CASE_SENTANCE), "Fr=_");
        assertEquals(PassageUtil.setCase("lord's", Passage.CASE_MIXED), "LORD's");
        assertEquals(PassageUtil.setCase("LORD's", Passage.CASE_MIXED), "LORD's");
        assertEquals(PassageUtil.setCase("no-one", Passage.CASE_LOWER), "no-one");
        assertEquals(PassageUtil.setCase("no-one", Passage.CASE_UPPER), "NO-ONE");
        assertEquals(PassageUtil.setCase("no-one", Passage.CASE_SENTANCE), "No-one");
        assertEquals(PassageUtil.setCase("xx-one", Passage.CASE_LOWER), "xx-one");
        assertEquals(PassageUtil.setCase("xx-one", Passage.CASE_UPPER), "XX-ONE");
        assertEquals(PassageUtil.setCase("xx-one", Passage.CASE_SENTANCE), "Xx-One");
        assertEquals(PassageUtil.setCase("god-inspired", Passage.CASE_SENTANCE), "God-inspired");
        assertEquals(PassageUtil.setCase("god-breathed", Passage.CASE_SENTANCE), "God-breathed");
        assertEquals(PassageUtil.setCase("maher-shalal-hash-baz", Passage.CASE_SENTANCE), "Maher-Shalal-Hash-Baz");
        assertEquals(PassageUtil.setCase("", Passage.CASE_LOWER), "");
        assertEquals(PassageUtil.setCase("", Passage.CASE_UPPER), "");
        assertEquals(PassageUtil.setCase("", Passage.CASE_SENTANCE), "");
        try { PassageUtil.setCase("god-inspired", Passage.CASE_MIXED); fail(); }
        catch (IllegalArgumentException ex) { }
        try { PassageUtil.setCase("fred", -1); fail(); }
        catch (IllegalArgumentException ex) { }
        try { PassageUtil.setCase("fred", 4); fail(); }
        catch (IllegalArgumentException ex) { }
    }

    public void testPersistentNaming() throws Exception
    {
        boolean stored_naming = PassageUtil.isPersistentNaming();
        PassageUtil.setPersistentNaming(false);
        assertTrue(!PassageUtil.isPersistentNaming());
        assertEquals(new Verse("Genesis 1 1").toString(), "Gen 1:1");
        assertEquals(new Verse("Gen 1 1").toString(), "Gen 1:1");
        assertEquals(new Verse("Genesis 1:1").toString(), "Gen 1:1");
        assertEquals(new Verse("Gen 1 1").toString(), "Gen 1:1");
        assertEquals(new Verse("g 1 1").toString(), "Gen 1:1");
        assertEquals(new Verse("g").toString(), "Gen 1:1");
        assertEquals(new Verse("G:1:1").toString(), "Gen 1:1");
        PassageUtil.setPersistentNaming(true);
        assertTrue(PassageUtil.isPersistentNaming());
        assertEquals(new Verse("Genesis 1 1").toString(), "Genesis 1 1");
        assertEquals(new Verse("Gen 1 1").toString(), "Gen 1 1");
        assertEquals(new Verse("Genesis 1:1").toString(), "Genesis 1:1");
        assertEquals(new Verse("Gen 1 1").toString(), "Gen 1 1");
        assertEquals(new Verse("g 1 1").toString(), "g 1 1");
        assertEquals(new Verse("g").toString(), "g");
        assertEquals(new Verse("G:1:1").toString(), "G:1:1");
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

    public void testToSentanceCase() throws Exception
    {
        assertEquals(PassageUtil.toSentanceCase("one"), "One");
        assertEquals(PassageUtil.toSentanceCase("one two"), "One two");
        assertEquals(PassageUtil.toSentanceCase("ONE"), "One");
        assertEquals(PassageUtil.toSentanceCase("ONE TWO"), "One two");
        assertEquals(PassageUtil.toSentanceCase("onE"), "One");
        assertEquals(PassageUtil.toSentanceCase("onE twO"), "One two");
        assertEquals(PassageUtil.toSentanceCase("12345"), "12345");
        assertEquals(PassageUtil.toSentanceCase("1 two"), "1 two");
        assertEquals(PassageUtil.toSentanceCase("1 TWO"), "1 two");
        try { PassageUtil.toSentanceCase(null); fail(); }
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
        Passage gen1_135 = PassageFactory.createPassage("Gen 1:1, Gen 1:3, Gen 1:5");
        Passage exo2a_3b = PassageFactory.createPassage("Exo 2:1-10, Exo 3:1-11");
        Passage gen_rev = PassageFactory.createPassage("Gen 1:1-Rev 22:21");
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
