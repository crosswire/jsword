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
import java.net.URI;

import junit.framework.TestCase;

/**
 * A Raw File format that allows for each verse to have it's own storage.
 * 
 * @see gnu.lgpl.License for license details. The copyright to this program is
 *      held by it's authors.
 * @author mbergmann
 */
public class SwordBookMetaDataTest extends TestCase
{

    File              configFile        = new File("testconfig.conf"); //$NON-NLS-1$
    SwordBookMetaData swordBookMetaData = null;

    protected void setUp() throws Exception
    {
        ConfigEntryTable table = new ConfigEntryTable("TestBook"); //$NON-NLS-1$
        table.add(ConfigEntryType.LANG, "de"); //$NON-NLS-1$
        table.add(ConfigEntryType.INITIALS, "TestBook"); //$NON-NLS-1$
        table.add(ConfigEntryType.DESCRIPTION, "MyNewBook"); //$NON-NLS-1$
        table.add(ConfigEntryType.MOD_DRV, "RawFiles"); //$NON-NLS-1$
        try
        {
            table.save(configFile);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        swordBookMetaData = new SwordBookMetaData(configFile, "TestBook", new URI("")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    protected void tearDown() throws Exception
    {
        configFile.delete();
    }

    public void testPropertiesAccessors()
    {
        assertNotNull(swordBookMetaData);
        assertEquals(swordBookMetaData.getName(), "MyNewBook"); //$NON-NLS-1$
        assertEquals(swordBookMetaData.getInitials(), "TestBook"); //$NON-NLS-1$
        assertNotNull(swordBookMetaData.getLanguage());
        assertEquals(swordBookMetaData.getLanguage().getCode(), "de"); //$NON-NLS-1$
    }
}
