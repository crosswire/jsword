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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.sword;

import java.io.File;

import org.crosswire.common.activate.Activatable;
import org.crosswire.common.crypt.Sapphire;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;

/**
 * A generic way to read data from disk for later formatting.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractBackend implements Activatable
{
    /**
     * Construct a minimal backend
     * @param sbmd
     * @param location
     */
    public AbstractBackend(SwordBookMetaData sbmd, File location)
    {
        bmd = sbmd;
        rootPath = location;
    }

    /**
     * @return Returns the root path.
     */
    public File getRootPath()
    {
        return rootPath;
    }

    /**
     * @return Returns the Sword BookMetaData.
     */
    public SwordBookMetaData getBookMetaData()
    {
        return bmd;
    }

    /**
     * Decipher the data in place, if it is enciphered and there
     * is a key to unlock it.
     * @param data the data to unlock
     */
    public void decipher(byte[] data)
    {
        String cipherKeyString = getBookMetaData().getProperty(ConfigEntryType.CIPHER_KEY);
        if (cipherKeyString != null)
        {
            Sapphire cipherEngine = new Sapphire(cipherKeyString.getBytes());
            for (int i = 0; i < data.length; i++)
            {
                data[i] = cipherEngine.cipher(data[i]);
            }
        }
    }

    /**
     * Initialise a AbstractBackend before use. This method needs to call addKey() a
     * number of times on SwordDictionary
     */
    public abstract Key readIndex();

    /**
     * Get the bytes alotted for the given verse
     * @param key The key to fetch
     * @return String The data for the verse in question
     * @throws BookException If the data can not be read.
     */
    public abstract String getRawText(Key key) throws BookException;

    /**
     * Returns whether this AbstractBackend is implemented.
     * @return true if this AbstractBackend is implemented.
     */
    public abstract boolean isSupported();

    private SwordBookMetaData bmd;
    private File rootPath;
}
