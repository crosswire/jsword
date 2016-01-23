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
 * © CrossWire Bible Society, 2009 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;

import org.crosswire.common.util.IniSection;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom2.Element;
import org.junit.Assert;
import org.junit.Test;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author mbergmann
 * @author DM Smith
 */
public class ConfigEntryTableTest {

    @Test
    public void testCreateConfigEntryTableInstance() {
        ConfigEntryTable table = new ConfigEntryTable("TestBook", true);
        Assert.assertNotNull(table);
    }

    // TODO(DMS): make this test use mocks or setup its own environment
    @Test
    public void failingAddConfigEntry() {
        String modName = "TestBook";
        IniSection table = new IniSection(modName);
        Assert.assertNotNull(table);

        table.add(BookMetaData.KEY_LANG, "de");
        Assert.assertEquals("de", table.get(BookMetaData.KEY_LANG));
        FeatureType feature = FeatureType.STRONGS_NUMBERS;
        table.add(SwordBookMetaData.KEY_FEATURE, FeatureType.STRONGS_NUMBERS.toString());
        if (table.containsValue(SwordBookMetaData.KEY_FEATURE, feature.toString())) {
            Assert.assertTrue("Should have Strongs", true);
        } else {
            // Many "features" are GlobalOptionFilters, which in the Sword C++ API
            // indicate a class to use for filtering.
            // These mostly have the source type prepended to the feature
            StringBuilder buffer = new StringBuilder(table.get(SwordBookMetaData.KEY_SOURCE_TYPE));
            buffer.append(feature);
            if (table.containsValue(SwordBookMetaData.KEY_GLOBAL_OPTION_FILTER, buffer.toString())) {
                Assert.assertTrue("Should have Strongs", true);
            } else {
                // But some do not
                Assert.assertTrue("Should have Strongs",  table.containsValue(SwordBookMetaData.KEY_GLOBAL_OPTION_FILTER, feature.toString()));
            }
        }
        Book book = Books.installed().getBook("KJV");
        Assert.assertTrue("Should have Strongs", book.getBookMetaData().hasFeature(FeatureType.STRONGS_NUMBERS));

        try {
            Key key = book.getKey("Gen 1:1");
            BookData data = new BookData(book, key);
            try {
                Element osis = data.getOsisFragment();
                String strongsNumbers = OSISUtil.getStrongsNumbers(osis);
                Assert.assertTrue("No Strongs in KJV", strongsNumbers.length() > 0);
            } catch (BookException ex) {
                Assert.fail("Should have Gen 1:1 data");
            }
        } catch (NoSuchKeyException ex) {
            Assert.fail("Should have Gen 1:1 key");
        }
    }

    @Test
    public void testSaveConfigEntryTable() {
        String modName = "TestBook";
        IniSection table = new IniSection(modName);
        Assert.assertNotNull(table);

        table.add(BookMetaData.KEY_LANG, "de");
        String lang = table.get(BookMetaData.KEY_LANG);
        Assert.assertNotNull(lang);
        Assert.assertEquals(lang, "de");

        File configFile = new File("testconfig.conf");
        try {
            table.save(configFile, "UTF-8");
        } catch (IOException e) {
            Assert.assertTrue(false);
        } finally {
            configFile.delete();
        }
    }
}
