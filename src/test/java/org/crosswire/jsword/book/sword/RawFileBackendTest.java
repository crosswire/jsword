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
 * Copyright: 2012 - 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.crosswire.common.util.IOUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.state.RawFileBackendState;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author mbergmann
 * @author DM Smith
 */
public class RawFileBackendTest {

    private final String modName = "TestComment";
    private File configFile = new File("testconfig.conf");
    private RawFileBackend backend = null;
    private Versification v11n;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        // AV11N(DMS): Update test to test all V11Ns
        v11n = Versifications.instance().getDefaultVersification();
        ConfigEntryTable table = new ConfigEntryTable(modName, true);
        table.add(ConfigEntryType.LANG, "de");
        table.add(ConfigEntryType.INITIALS, modName);
        table.add(ConfigEntryType.DESCRIPTION, "MyNewBook");
        table.add(ConfigEntryType.MOD_DRV, "RawFiles");
        table.add(ConfigEntryType.DATA_PATH, "test");
        try {
            table.save(configFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        SwordBookMetaData swordBookMetaData = new SwordBookMetaData(null, MetaFile.Level.SWORD, configFile, modName, new URI("file:///tmp"));
        backend = new RawFileBackend(swordBookMetaData, 2);
        backend.create();
    }

    @Test
    public void testSetRawText() throws IOException, BookException {
        Verse otVerse = new Verse(v11n, BibleBook.GEN, 3, 1);
        Verse otVerse2 = new Verse(v11n, BibleBook.LEV, 3, 5);
        Verse otVerse3 = new Verse(v11n, BibleBook.EXOD, 6, 4);
        Verse otVerse4 = new Verse(v11n, BibleBook.JUDG, 3, 1);
        Verse ntVerse = new Verse(v11n, BibleBook.PET2, 1, 2);
        Verse ntVerse2 = new Verse(v11n, BibleBook.PET1, 1, 2);
        Verse ntVerse3 = new Verse(v11n, BibleBook.REV, 22, 21);
        Verse ntVerse4 = new Verse(v11n, BibleBook.JOHN3, 1, 2);

        File dataPath = new File(SwordUtil.getExpandedDataPath(backend.getBookMetaData()));
        if (!dataPath.exists()) {
            backend.create();
        }

        RawFileBackendState state = null;
        try {
            state = backend.initState();

            backend.setRawText(state, otVerse, "Hello OT");
            backend.setRawText(state, otVerse2, "Hello OT2");
            backend.setRawText(state, otVerse3, "Hello OT3");
            backend.setRawText(state, otVerse4, "Hello OT4");
            backend.setRawText(state, ntVerse, "Hello NT");
            backend.setRawText(state, ntVerse2, "Hello NT2");
            backend.setRawText(state, ntVerse3, "Hello NT3");
            backend.setRawText(state, ntVerse4, "Hello NT4");

            assertEquals("Hello OT", backend.getRawText(state, otVerse));
            assertEquals("Hello OT2", backend.getRawText(state, otVerse2));
            assertEquals("Hello OT3", backend.getRawText(state, otVerse3));
            assertEquals("Hello OT4", backend.getRawText(state, otVerse4));
            assertEquals("Hello NT", backend.getRawText(state, ntVerse));
            assertEquals("Hello NT2", backend.getRawText(state, ntVerse2));
            assertEquals("Hello NT3", backend.getRawText(state, ntVerse3));
            assertEquals("Hello NT4", backend.getRawText(state, ntVerse4));
        } finally {
            IOUtil.close(state);
        }

    }

    @Test
    public void testSetAliasKey() throws NoSuchVerseException, IOException, BookException {
        Verse source = VerseFactory.fromString(v11n, "Gen 1:1");
        Verse alias1 = VerseFactory.fromString(v11n, "Gen 1:2");
        Verse alias2 = VerseFactory.fromString(v11n, "Gen 1:3");

        File dataPath = new File(SwordUtil.getExpandedDataPath(backend.getBookMetaData()));
        if (!dataPath.exists()) {
            backend.create();
        }

        RawFileBackendState state = null;
        try {
            state = backend.initState();
            backend.setRawText(state, source, "Hello Alias test!");
            backend.setAliasKey(state, alias1, source);
            backend.setAliasKey(state, alias2, source);

            assertEquals("Hello Alias test!", backend.getRawText(state, source));
            assertEquals("Hello Alias test!", backend.getRawText(state, alias1));
            assertEquals("Hello Alias test!", backend.getRawText(state, alias2));
        } finally {
            IOUtil.close(state);
        }
    }
}
