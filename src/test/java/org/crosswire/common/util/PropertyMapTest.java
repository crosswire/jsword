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
 * Copyright: 2007 - 2014
 *     The copyright to this program is held by its authors.
 *
 */
package org.crosswire.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.MissingResourceException;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Test
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class PropertyMapTest {
    @Before
    public void setUp() throws Exception {
        m = new PropertyMap();
    }

    @Test
    public void testBasic() {
        assertEquals("Test for an element not present", null, m.get("diddly"));
        m.put("diddly", "squat");
        assertEquals("Test for a present element", "squat", m.get("diddly"));
    }

    @Test
    public void testLoad() {
        InputStream in = null;
        try {
            in = ResourceUtil.getResourceAsStream(this.getClass(), "PropertyMap.properties");
            m.load(in);
        } catch (MissingResourceException e) {
            fail("Unable to find PropertyMap.properties");
        } catch (IOException e) {
            fail("Unable to read PropertyMap.properties");
        } finally {
            IOUtil.close(in);
        }
    }

    @Test
    public void testRead() {
        InputStream in = null;
        try {
            in = ResourceUtil.getResourceAsStream(this.getClass(), "PropertyMap.properties");
            m.load(in);
        } catch (MissingResourceException e) {
            fail("Unable to find PropertyMap.properties");
        } catch (IOException e) {
            fail("Unable to read PropertyMap.properties");
        } finally {
            IOUtil.close(in);
        }
        assertEquals("Only one element was in the file", 1, m.size());
        assertEquals("Test that the load worked", "I am", m.get("Here"));
    }

    @Test
    public void testSave() {
        m.put("Here", "Am I");
        URI uri = CWProject.instance().getWritableURI("test", FileUtil.EXTENSION_PROPERTIES);
        OutputStream out = null;
        try {
            out = NetUtil.getOutputStream(uri);
            m.store(out, "Test data can be deleted at any time");
        } catch (IOException e) {
            fail("Unable to save test.properties");
        } finally {
            IOUtil.close(out);
        }
    }

    @Test
    public void testReload() {
        assertEquals("The map is empty", 0, m.size());

        InputStream is = null;
        URI uri = CWProject.instance().getWritableURI("test", FileUtil.EXTENSION_PROPERTIES);
        try {
            is = NetUtil.getInputStream(uri);
            m.load(is);
        } catch (IOException e) {
            fail("Unable to reload test.properties");
        } finally {
            IOUtil.close(is);
        }
        assertEquals("Only one element was in the file", 1, m.size());
        assertEquals("Test that the save and reload worked", "Am I", m.get("Here"));
    }

    private PropertyMap m;
}
