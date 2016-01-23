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
package org.crosswire.jsword.book;

import java.util.List;

import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.VerseRangeFactory;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.system.Versifications;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class BooksTest {
    protected Key[] gen11;
    protected BookMetaData[] bmds;
    protected Book[] bibles;

    //protected Class[] ignorebibles = {};

    @Before
    public void setUp() throws Exception {
        List<Book> lbmds = Books.installed().getBooks(BookFilters.getOnlyBibles());
        int numBibles = lbmds.size();
        bibles = new Book[numBibles];
        bmds = new BookMetaData[numBibles];
        gen11 = new Key[numBibles];

        int i = 0;
        for (Book book : lbmds) {
            bibles[i] = book;
            bmds[i] = book.getBookMetaData();
            gen11[i] = book.getKey("Gen 1:1");
            i++;
        }
    }

    @Test
    public void testGetBible() {
        for (int i = 0; i < bibles.length; i++) {
            Book bible = bibles[i];
            Assert.assertTrue(bible != null);
        }
    }

    @Test
    public void testGetBibleMetaData() {
        for (int i = 0; i < bibles.length; i++) {
            Book bible = bibles[i];
            BookMetaData bmd = bible.getBookMetaData();
            Assert.assertEquals(bmds[i], bmd);
        }
    }

    @Test
    public void testMetaData() {
        for (int i = 0; i < bmds.length; i++) {
            BookMetaData bmd = bmds[i];

            Assert.assertTrue(bmd.getInitials() != null);
            Assert.assertTrue(bmd.getInitials().length() > 0);
            Assert.assertTrue(bmd.getName() != null);
            Assert.assertTrue(bmd.getName().length() > 0);
        }
    }

    @Test
    public void testGetBookMetaData() {
        for (int i = 0; i < bibles.length; i++) {
            Book bible = bibles[i];
            BookMetaData bmd = bible.getBookMetaData();
            Assert.assertEquals(bmds[i], bmd);
        }
    }

    @Test
    public void testGetDataKey() throws Exception {
        for (int i = 0; i < bibles.length; i++) {
            Book bible = bibles[i];
            Key key = bible.getKey("Gen 1:1");
            BookData data = new BookData(bible, key);
            Assert.assertNotNull(data);
        }
    }

    @Test
    public void testGetDataPassage() throws Exception {
        for (int i = 0; i < bibles.length; i++) {
            Book bible = bibles[i];
            BookData data = new BookData(bible, gen11[i]);
            Assert.assertNotNull(data.getOsisFragment());
        }
    }

    /** Bibles like TurNTB contain merged (linked) verses which are duplicated when chapters are displayed- see JS-224.
     * This tests the deduplication code in AbstractPassageBook.
     *
     * @throws Exception 
     */
    @Test
    public void testBookList() throws Exception {
        //part of the pre-requisites
        AbstractPassageBook esv = (AbstractPassageBook) Books.installed().getBook("ESV");
        Assert.assertTrue(esv.getBibleBooks().contains(BibleBook.ACTS));
    }


    /**
     * Bibles like TurNTB contain merged (linked) verses which are duplicated when chapters are displayed- see JS-224.
     * This tests the deduplication code in AbstractPassageBook.
     *
     * @throws Exception 
     */
    @Test
    public void testLinkedVersesNotDuplicated() throws Exception {
        Book turNTB = Books.installed().getBook("TurNTB");
        if (turNTB != null) {
            // Eph 2:4,5 are merged/linked in TurNTB
            Key eph245 = VerseRangeFactory.fromString(Versifications.instance().getVersification("KJV"), "Eph 2:4-5");
            BookData bookData = new BookData(turNTB, eph245);
            final Element osisFragment = bookData.getOsisFragment();

            final XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            String xml = xmlOutputter.outputString(osisFragment);

            Assert.assertTrue("Probable duplicate text", xml.length() < 300);
        }
    }

    /** 
     * Books like Josephus have hierarchical chapters.  Only the current chapter should be returned, not child chapters.
     * 
     * @throws Exception 
     */
    @Test
    public void testHierarchicalBook() throws Exception {
        Book josephus = Books.installed().getBook("Josephus");
        String section1Text = "THOSE who undertake to write histories";
        if (josephus != null) {
            // navigate down to the Preface
            Key theAntiquitiesOfTheJewsKey = josephus.getGlobalKeyList().get(1);
            Key prefaceKey = theAntiquitiesOfTheJewsKey.get(0);
            String prefaceText = josephus.getRawText(prefaceKey);
            Assert.assertFalse("Child keys returned in raw text", prefaceText.contains(section1Text));

            // Now attempt to parse a key but get the problem is that all child text is returned too 
            BookData bookData = new BookData(josephus, prefaceKey);
            final Element osisFragment = bookData.getOsisFragment();

            final XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            String prefaceXml = xmlOutputter.outputString(osisFragment);
            Assert.assertFalse("Child keys returned in xml", prefaceXml.contains(section1Text));
        }
    }
    /*
     * FIXME: These are only valid if all bibles are English public void
     * testGetFind() throws Exception { // This only checks that find() does
     * something vaguely sensible // I assume that find() just calls
     * findPassage(), where the real tests are for (int i = 0; i <
     * bibles.length; i++) { Book bible = bibles[i]; Key key = bible.find(new
     * Search("aaron", false)); Assert.assertNotNull("bible=" +
     * bible.getFullName(), key); } }
     * 
     * FIXME: These are only valid if all bibles are English public void
     * testFindPassage() throws Exception { for (int i = 0; i < bibles.length;
     * i++) { Book ver = bibles[i];
     * 
     * Key key = ver.find(new Search("aaron", false));
     * Assert.assertTrue(key != null); } }
     * 
     * FIXME: These are only valid if all bibles are English public void
     * testFindPassage2() throws Exception { for (int i = 0; i < bibles.length;
     * i++) { // Check that this is a type that we expect to return real Bible
     * data Book ver = bibles[i]; boolean skip = false; for (int j = 0; j <
     * ignorebibles.length; j++) { // if (ver instanceof fullbibles[j]) if
     * (ignorebibles[j].isAssignableFrom(ver.getClass())) skip = true; } if
     * (skip) continue; log.debug("thorough testing bible: {}", ver.getFullName());
     * 
     * Key key = ver.find(new Search("aaron", false)); Passage ref
     * = KeyUtil.getPassage(key); Assert.assertTrue(ref.countVerses() > 10); key =
     * ver.find(new Search("jerusalem", false)); ref =
     * KeyUtil.getPassage(key); Assert.assertTrue(ref.countVerses() > 10); key =
     * ver.find(new Search("god", false)); ref =
     * KeyUtil.getPassage(key); Assert.assertTrue(ref.countVerses() > 10); key =
     * ver.find(new Search("GOD", false)); ref =
     * KeyUtil.getPassage(key); Assert.assertTrue(ref.countVerses() > 10); key =
     * ver.find(new Search("brother's", false)); ref =
     * KeyUtil.getPassage(key); Assert.assertTrue(ref.countVerses() > 2); key =
     * ver.find(new Search("BROTHER'S", false)); ref =
     * KeyUtil.getPassage(key); Assert.assertTrue(ref.countVerses() > 2);
     * 
     * key = ver.find(new Search("maher-shalal-hash-baz", false));
     * ref = KeyUtil.getPassage(key); if (ref.isEmpty()) { key = ver.find(new
     * Search("mahershalalhashbaz", false)); ref =
     * KeyUtil.getPassage(key); } if (ref.isEmpty()) { key = ver.find(new
     * Search("maher*", false)); ref = KeyUtil.getPassage(key); }
     * Assert.assertEquals(ref.countVerses(), 2); Assert.assertEquals(ref.getVerseAt(0), new
     * Verse("Isa 8:1")); Assert.assertEquals(ref.getVerseAt(1), new
     * Verse("Isa 8:3"));
     * 
     * key = ver.find(new Search("MAHER-SHALAL-HASH-BAZ", false));
     * ref = KeyUtil.getPassage(key); if (ref.isEmpty()) { key = ver.find(new
     * Search("MAHERSHALALHASHBAZ", false)); ref =
     * KeyUtil.getPassage(key); } Assert.assertEquals(ref.countVerses(), 2);
     * Assert.assertEquals(ref.getVerseAt(0), new Verse("Isa 8:1"));
     * Assert.assertEquals(ref.getVerseAt(1), new Verse("Isa 8:3")); } }
     */
    /*
     * public void testGetStartsWith() throws Exception { for (int i=0;
     * i<bibles.length; i++) { Bible ver = bibles[i];
     * 
     * if (ver instanceof SearchableBible) { String[] sa =
     * Sentance2Util.toStringArray(((SearchableBible)
     * ver).getSearcher().getStartsWith("a")); Assert.assertTrue(sa != null); } } }
     * 
     * public void testGetStartsWith2() throws Exception { for (int i=0;
     * i<bibles.length; i++) { // Check that this is a type that we expect to
     * return real Bible data Bible origver = bibles[i]; boolean skip = false;
     * for (int j=0; j<ignorebibles.length; j++) { // if (ver instanceof
     * fullbibles[j]) if (origver.getClass().isAssignableFrom(ignorebibles[j]))
     * skip = true; } if (skip) continue;
     * log.debug("thorough testing bible: {}", origver.getBibleMetaData().getFullName());
     * 
     * if (origver instanceof SearchableBible) { SearchableBible ver =
     * (SearchableBible) origver; String[] sa =
     * Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("jos"));
     * Assert.assertTrue(sa.length > 5); sa =
     * Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("jerusale"));
     * Assert.assertEquals(sa[0], "jerusalem"); sa =
     * Sentance2Util.toStringArray(ver.getSearcher
     * ().getStartsWith("maher-shalal")); if (sa.length == 0) { sa =
     * Sentance2Util
     * .toStringArray(ver.getSearcher().getStartsWith("mahershalal"));
     * Assert.assertEquals(sa[0], "mahershalalhashbaz"); } else { Assert.assertEquals(sa[0],
     * "maher-shalal-hash-baz"); } Assert.assertEquals(sa.length, 1); sa =
     * Sentance2Util
     * .toStringArray(ver.getSearcher().getStartsWith("MAHER-SHALAL")); if
     * (sa.length == 0) { sa =
     * Sentance2Util.toStringArray(ver.getSearcher().getStartsWith
     * ("MAHERSHALAL")); Assert.assertEquals(sa[0], "mahershalalhashbaz"); } else {
     * Assert.assertEquals(sa[0], "maher-shalal-hash-baz"); } Assert.assertEquals(sa.length,
     * 1); sa =
     * Sentance2Util.toStringArray(ver.getSearcher().getStartsWith("XXX"));
     * Assert.assertEquals(sa.length, 0); } } }
     */
}
