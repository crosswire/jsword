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

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.crosswire.common.util.IniSection;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author mbergmann
 * @author DM Smith
 */
public class SwordBookMetaDataTest {
    Book mockBook;
    File configFile = new File("testconfig.conf");
    SwordBookMetaData swordBookMetaData = null;

    @Before
    public void setUp() throws Exception {
        String modName = "TestBook";
        IniSection table = new IniSection(modName);
        table.add(BookMetaData.KEY_LANG, "de");
        table.add(SwordBookMetaData.KEY_DESCRIPTION, "MyNewBook");
        table.add(SwordBookMetaData.KEY_MOD_DRV, "RawFiles");
        table.add(SwordBookMetaData.KEY_ENCODING, "UTF-8");
        try {
            table.save(configFile, "UTF-8");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        swordBookMetaData = new SwordBookMetaData(configFile, new URI(""));
        mockBook = Mockito.mock(Book.class);
        mockBook.setBookMetaData(swordBookMetaData);
        Mockito.when(mockBook.getBookMetaData()).thenReturn(swordBookMetaData);
    }

    @After
    public void tearDown() throws Exception {
        configFile.delete();
    }

    @Test
    public void testPropertiesAccessors() {
        assertNotNull(swordBookMetaData);
        assertEquals("MyNewBook", swordBookMetaData.getName());
        assertEquals("TestBook", swordBookMetaData.getInitials());
        assertNotNull(swordBookMetaData.getLanguage());
        assertEquals("de", swordBookMetaData.getLanguage().getCode());
    }

}
