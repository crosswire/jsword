package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import junit.framework.TestCase;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author mbergmann
 */
public class RawFileBackendTest extends TestCase {

    final String modName = "TestComment";
    File configFile = new File("testconfig.conf");
    RawFileBackend backend = null;

    @Override
    protected void setUp() throws Exception {
        ConfigEntryTable table = new ConfigEntryTable(modName);
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

        SwordBookMetaData swordBookMetaData = new SwordBookMetaData(configFile, modName, new URI("file:///tmp"));
        backend = new RawFileBackend(swordBookMetaData, 2);
    }

    @Override
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

        backend.setRawText(otVerse, "Hello OT");
        backend.setRawText(otVerse2, "Hello OT2");
        backend.setRawText(otVerse3, "Hello OT3");
        backend.setRawText(otVerse4, "Hello OT4");
        backend.setRawText(ntVerse, "Hello NT");
        backend.setRawText(ntVerse2, "Hello NT2");
        backend.setRawText(ntVerse3, "Hello NT3");
        backend.setRawText(ntVerse4, "Hello NT4");

        assertEquals(backend.getRawText(otVerse), "Hello OT");
        assertEquals(backend.getRawText(otVerse2), "Hello OT2");
        assertEquals(backend.getRawText(otVerse3), "Hello OT3");
        assertEquals(backend.getRawText(otVerse4), "Hello OT4");
        assertEquals(backend.getRawText(ntVerse), "Hello NT");
        assertEquals(backend.getRawText(ntVerse2), "Hello NT2");
        assertEquals(backend.getRawText(ntVerse3), "Hello NT3");
        assertEquals(backend.getRawText(ntVerse4), "Hello NT4");
    }

    public void testSetAliasKey() throws NoSuchVerseException, IOException, BookException {
        Verse source = VerseFactory.fromString("Gen 1:1");
        Verse alias1 = VerseFactory.fromString("Gen 1:2");
        Verse alias2 = VerseFactory.fromString("Gen 1:3");

        backend.setRawText(source, "Hello Alias test!");
        backend.setAliasKey(alias1, source);
        backend.setAliasKey(alias2, source);

        assertEquals(backend.getRawText(source), "Hello Alias test!");
        assertEquals(backend.getRawText(alias1), "Hello Alias test!");
        assertEquals(backend.getRawText(alias2), "Hello Alias test!");
    }
}
