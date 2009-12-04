package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URI;

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
 * Copyright: 2009
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
import junit.framework.TestCase;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.*;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author mbergmann
 */
public class RawFileBackendTest extends TestCase {

    final String modName = "TestComment"; //$NON-NLS-1$
    File configFile = new File("testconfig.conf"); //$NON-NLS-1$
    RawFileBackend backend = null;

    protected void setUp() throws Exception {
        ConfigEntryTable table = new ConfigEntryTable(modName);
        table.add(ConfigEntryType.LANG, "de"); //$NON-NLS-1$
        table.add(ConfigEntryType.INITIALS, modName);
        table.add(ConfigEntryType.DESCRIPTION, "MyNewBook"); //$NON-NLS-1$
        table.add(ConfigEntryType.MOD_DRV, "RawFiles"); //$NON-NLS-1$
        table.add(ConfigEntryType.DATA_PATH, "test"); //$NON-NLS-1$
        try {
            table.save(configFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        SwordBookMetaData swordBookMetaData = new SwordBookMetaData(configFile, modName, new URI("file:///tmp")); //$NON-NLS-1$
        backend = new RawFileBackend(swordBookMetaData, 2);
    }

    protected void tearDown() throws Exception {
        // configFile.delete();
    }

    public void testCreate() throws IOException, BookException {
        backend.create();
    }

    public void testSetRawText() throws NoSuchVerseException, IOException, BookException {
        Verse otVerse = new Verse(1, 3, 1);
        Verse otVerse2 = new Verse(3, 3, 5);
        Verse otVerse3 = new Verse(2, 6, 4);
        Verse otVerse4 = new Verse(7, 3, 1);
        Verse ntVerse = new Verse(61, 1, 2);
        Verse ntVerse2 = new Verse(60, 1, 2);
        Verse ntVerse3 = new Verse(66, 22, 21);
        Verse ntVerse4 = new Verse(64, 1, 2);

        backend.setRawText(otVerse, "Hello OT"); //$NON-NLS-1$
        backend.setRawText(otVerse2, "Hello OT2"); //$NON-NLS-1$
        backend.setRawText(otVerse3, "Hello OT3"); //$NON-NLS-1$
        backend.setRawText(otVerse4, "Hello OT4"); //$NON-NLS-1$
        backend.setRawText(ntVerse, "Hello NT"); //$NON-NLS-1$
        backend.setRawText(ntVerse2, "Hello NT2"); //$NON-NLS-1$
        backend.setRawText(ntVerse3, "Hello NT3"); //$NON-NLS-1$
        backend.setRawText(ntVerse4, "Hello NT4"); //$NON-NLS-1$

        assertEquals(backend.getRawText(otVerse), "Hello OT"); //$NON-NLS-1$
        assertEquals(backend.getRawText(otVerse2), "Hello OT2"); //$NON-NLS-1$
        assertEquals(backend.getRawText(otVerse3), "Hello OT3"); //$NON-NLS-1$
        assertEquals(backend.getRawText(otVerse4), "Hello OT4"); //$NON-NLS-1$
        assertEquals(backend.getRawText(ntVerse), "Hello NT"); //$NON-NLS-1$
        assertEquals(backend.getRawText(ntVerse2), "Hello NT2"); //$NON-NLS-1$
        assertEquals(backend.getRawText(ntVerse3), "Hello NT3"); //$NON-NLS-1$
        assertEquals(backend.getRawText(ntVerse4), "Hello NT4"); //$NON-NLS-1$
    }

    public void testSetAliasKey() throws NoSuchVerseException, IOException, BookException {
        Verse source = VerseFactory.fromString("Gen 1:1"); //$NON-NLS-1$
        Verse alias1 = VerseFactory.fromString("Gen 1:2"); //$NON-NLS-1$
        Verse alias2 = VerseFactory.fromString("Gen 1:3"); //$NON-NLS-1$

        backend.setRawText(source, "Hello Alias test!"); //$NON-NLS-1$
        backend.setAliasKey(alias1, source);
        backend.setAliasKey(alias2, source);

        assertEquals(backend.getRawText(source), "Hello Alias test!"); //$NON-NLS-1$
        assertEquals(backend.getRawText(alias1), "Hello Alias test!"); //$NON-NLS-1$
        assertEquals(backend.getRawText(alias2), "Hello Alias test!"); //$NON-NLS-1$
    }
}
