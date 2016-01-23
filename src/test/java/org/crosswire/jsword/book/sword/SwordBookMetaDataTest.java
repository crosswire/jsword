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
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.IniSection;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.MetaDataLocator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author mbergmann
 * @author DM Smith
 */
public class SwordBookMetaDataTest {
    private File configFile = new File("testconfig.conf");
    private SwordBookMetaData swordBookMetaData;

    @BeforeClass
    public static void classSetup() throws Exception {
        // CWProject is a static that has probably already been initialised without a FrontEnd so forcefully reset it
        Method resetMethod = CWProject.class.getDeclaredMethod("reset");
        resetMethod.setAccessible(true);
        resetMethod.invoke(CWProject.class);
    }

    @Before
    public void setUp() throws Exception {
        // required for setting ui properties
        CWProject.instance().setFrontendName("jsword-app");

        swordBookMetaData = createTestSwordBookMetaData();
    }

    @After
    public void tearDown() throws Exception {
        configFile.delete();
    }

    @Test
    public void testFrontEndSwordBookMetadataSetup() {
        // From the code: "The write location supersedes the read location" so don't worry about getReadableFrontendProjectDir (which is null) 
        Assert.assertNotNull(CWProject.instance().getWritableFrontendProjectDir());
    }

    @Test
    public void testPropertiesAccessors() {
        Assert.assertNotNull(swordBookMetaData);
        Assert.assertEquals("MyNewBook", swordBookMetaData.getName());
        Assert.assertEquals("TestBook", swordBookMetaData.getInitials());
        Assert.assertNotNull(swordBookMetaData.getLanguage());
        Assert.assertEquals("de", swordBookMetaData.getLanguage().getCode());
    }

    @Test
    public void testPutThenGetProperty() {
        // simple jsword property
        swordBookMetaData.putProperty("jswordkey", "jswordvalue");
        Assert.assertEquals("jswordvalue", swordBookMetaData.getProperty("jswordkey"));

        // UI property
        swordBookMetaData.putProperty("uikey", "uivalue", true);
        Assert.assertEquals("uivalue", swordBookMetaData.getProperty("uikey"));

        // Transient property
        swordBookMetaData.putProperty("transientkey", "transientvalue", MetaDataLocator.TRANSIENT);
        Assert.assertEquals("transientvalue", swordBookMetaData.getProperty("transientkey"));
    }

    @Test
    public void testPropertyPersistence() throws Exception {
        swordBookMetaData.putProperty("jswordkey", "jswordvalue");
        swordBookMetaData.putProperty("uikey", "uivalue", true);
        swordBookMetaData.putProperty("transientkey", "transientvalue", MetaDataLocator.TRANSIENT);

        // jsword and ui properties should be persisted, and therefore restored into another SwordBookMetaData but not transient properties
        SwordBookMetaData anotherSwordBookMetaData = createTestSwordBookMetaData();

        Assert.assertEquals("jswordvalue", anotherSwordBookMetaData.getProperty("jswordkey"));
        Assert.assertEquals("uivalue", anotherSwordBookMetaData.getProperty("uikey"));
        Assert.assertEquals(null, anotherSwordBookMetaData.getProperty("transientkey"));
    }

    private SwordBookMetaData createTestSwordBookMetaData() throws IOException, BookException, URISyntaxException {
        String modName = "TestBook";
        IniSection table = new IniSection(modName);
        table.add(BookMetaData.KEY_LANG, "de");
        table.add(SwordBookMetaData.KEY_DESCRIPTION, "MyNewBook");
        table.add(SwordBookMetaData.KEY_MOD_DRV, "RawFiles");
        table.add(SwordBookMetaData.KEY_DATA_PATH, "test");
        table.add(SwordBookMetaData.KEY_ENCODING, "UTF-8");
        try {
            table.save(configFile, "UTF-8");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        swordBookMetaData = new SwordBookMetaData(configFile, new URI("file:///tmp"));

        return swordBookMetaData;
    }
}
