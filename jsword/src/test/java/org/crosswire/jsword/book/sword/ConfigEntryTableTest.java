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
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.crosswire.common.util.Language;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author mbergmann
 */
public class ConfigEntryTableTest extends TestCase
{

    public void testCreateConfigEntryTableInstance()
    {
        ConfigEntryTable table = new ConfigEntryTable("TestBook"); //$NON-NLS-1$
        assertNotNull(table);
    }

    public void testAddConfigEntry()
    {
        ConfigEntryTable table = new ConfigEntryTable("TestBook"); //$NON-NLS-1$
        assertNotNull(table);

        table.add(ConfigEntryType.LANG, "de"); //$NON-NLS-1$
        assertEquals(table.getValue(ConfigEntryType.LANG), "de"); //$NON-NLS-1$
    }

    public void testSaveConfigEntryTable()
    {
        ConfigEntryTable table = new ConfigEntryTable("TestBook"); //$NON-NLS-1$
        assertNotNull(table);

        table.add(ConfigEntryType.LANG, "de"); //$NON-NLS-1$
        Language lang = (Language) table.getValue(ConfigEntryType.LANG);
        assertNotNull(lang);
        assertEquals(lang.getCode(), "de"); //$NON-NLS-1$
        table.add(ConfigEntryType.INITIALS, "TestBook"); //$NON-NLS-1$
        assertEquals(table.getValue(ConfigEntryType.INITIALS), "TestBook"); //$NON-NLS-1$

        File configFile = new File("testconfig.conf"); //$NON-NLS-1$
        try
        {
            table.save(configFile);
        }
        catch (IOException e)
        {
            assertTrue(false);
        }
        finally
        {
            configFile.delete();
        }
    }
}
