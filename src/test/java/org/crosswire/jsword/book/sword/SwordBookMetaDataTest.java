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
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.crosswire.jsword.book.Book;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

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
        ConfigEntryTable table = new ConfigEntryTable("TestBook");
        table.add(ConfigEntryType.LANG, "de");
        table.add(ConfigEntryType.INITIALS, "TestBook");
        table.add(ConfigEntryType.DESCRIPTION, "MyNewBook");
        table.add(ConfigEntryType.MOD_DRV, "RawFiles");
        try {
            table.save(configFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        swordBookMetaData = new SwordBookMetaData(null, MetaFile.Level.SWORD, configFile, "TestBook", new URI(""));
        mockBook = Mockito.mock(Book.class);
        swordBookMetaData.setCurrentBook(mockBook);
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

    @Test
    public void testCreateAndSaveIntoSidecarConf() throws IOException {
        assertNotNull(swordBookMetaData);

        //run test capturing the argument - save stuff twice, such that we prove the creation flow and the update flow
        ArgumentCaptor<SwordBookMetaData> captor = ArgumentCaptor.forClass(SwordBookMetaData.class);
        swordBookMetaData.save(ConfigEntryType.SHORT_COPYRIGHT, "ShortCopy", MetaFile.Level.JSWORD_WRITE);
        swordBookMetaData.save(ConfigEntryType.ABOUT, "About", MetaFile.Level.JSWORD_WRITE);

        //save an extra time, but to the sword config file this time. Again, to prove this works.
        //the chain at the result of all of this hasn't changed.
        swordBookMetaData.save(ConfigEntryType.COPYRIGHT, "Copyright", MetaFile.Level.SWORD);

        //check we ran the test correctly - this creates a new SBMD in the hierarchy.
        Mockito.verify(mockBook, Mockito.times(2)).setBookMetaData(captor.capture());
        SwordBookMetaData topLevel = captor.getValue();
        assertEquals(topLevel.getLevel(), MetaFile.Level.JSWORD_WRITE);
        assertEquals(topLevel.getParent(), swordBookMetaData);


        //now let's check a few different fields to prove this worked.
        assertEquals("ShortCopy", topLevel.getConfigEntryTable().getValue(ConfigEntryType.SHORT_COPYRIGHT));
        assertEquals("About", topLevel.getConfigEntryTable().getValue(ConfigEntryType.ABOUT));
        assertNull(topLevel.getConfigEntryTable().getValue(ConfigEntryType.COPYRIGHT));

        //same checks but in sword conf
        assertNull(topLevel.getParent().getConfigEntryTable().getValue(ConfigEntryType.SHORT_COPYRIGHT));
        assertNull(topLevel.getParent().getConfigEntryTable().getValue(ConfigEntryType.ABOUT));
        assertEquals("Copyright", topLevel.getParent().getConfigEntryTable().getValue(ConfigEntryType.COPYRIGHT));
    }
}
