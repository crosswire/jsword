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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2009 - 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.crosswire.common.util.Language;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom2.Element;
import org.junit.Test;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author mbergmann
 * @author DM Smith
 */
public class ConfigEntryTableTest {

    @Test
    public void testCreateConfigEntryTableInstance() {
        ConfigEntryTable table = new ConfigEntryTable("TestBook");
        assertNotNull(table);
    }

    // TODO: make this test use mocks or setup its own environment
    @Test
    public void failingAddConfigEntry() {
        ConfigEntryTable table = new ConfigEntryTable("TestBook");
        assertNotNull(table);

        table.add(ConfigEntryType.LANG, "de");
        assertEquals("de", ((Language) table.getValue(ConfigEntryType.LANG)).getCode());
        FeatureType feature = FeatureType.STRONGS_NUMBERS;
        table.add(ConfigEntryType.FEATURE, FeatureType.STRONGS_NUMBERS.toString());
        if (table.match(ConfigEntryType.FEATURE, feature.toString())) {
            assertTrue("Should have Strongs", true);
        } else {
            // Many "features" are GlobalOptionFilters, which in the Sword C++ API
            // indicate a class to use for filtering.
            // These mostly have the source type prepended to the feature
            StringBuilder buffer = new StringBuilder((String) table.getValue(ConfigEntryType.SOURCE_TYPE));
            buffer.append(feature);
            if (table.match(ConfigEntryType.GLOBAL_OPTION_FILTER, buffer.toString())) {
                assertTrue("Should have Strongs", true);
            } else {
                // But some do not
                assertTrue("Should have Strongs",  table.match(ConfigEntryType.GLOBAL_OPTION_FILTER, feature.toString()));
            }
        }
        Book book = Books.installed().getBook("KJV");
        assertTrue("Should have Strongs", book.getBookMetaData().hasFeature(FeatureType.STRONGS_NUMBERS));

        try {
            Key key = book.getKey("Gen 1:1");
            BookData data = new BookData(book, key);
            try {
                Element osis = data.getOsisFragment();
                String strongsNumbers = OSISUtil.getStrongsNumbers(osis);
                assertTrue("No Strongs in KJV", strongsNumbers.length()>0);
            } catch (BookException e) {
                fail("Should have Gen 1:1 data");
            }
        } catch (NoSuchKeyException e1) {
            fail("Should have Gen 1:1 key");
        }
    }

    @Test
    public void testSaveConfigEntryTable() {
        ConfigEntryTable table = new ConfigEntryTable("TestBook");
        assertNotNull(table);

        table.add(ConfigEntryType.LANG, "de");
        Language lang = (Language) table.getValue(ConfigEntryType.LANG);
        assertNotNull(lang);
        assertEquals(lang.getCode(), "de");
        table.add(ConfigEntryType.INITIALS, "TestBook");
        assertEquals(table.getValue(ConfigEntryType.INITIALS), "TestBook");

        File configFile = new File("testconfig.conf");
        try {
            table.save(configFile);
        } catch (IOException e) {
            assertTrue(false);
        } finally {
            configFile.delete();
        }
    }
}
