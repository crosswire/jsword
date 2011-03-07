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
import java.io.IOException;
import java.net.URI;

import org.crosswire.common.activate.Activatable;
import org.crosswire.common.crypt.Sapphire;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;

/**
 * A generic way to read data from disk for later formatting.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class AbstractBackend implements Activatable {
    /**
     * Default constructor for the sake of serialization.
     */
    /* protected */public AbstractBackend() {
    }

    /**
     * Construct a minimal backend
     * 
     * @param sbmd
     */
    public AbstractBackend(SwordBookMetaData sbmd) {
        bmd = sbmd;
    }

    /**
     * @return Returns the Sword BookMetaData.
     */
    public SwordBookMetaData getBookMetaData() {
        return bmd;
    }

    /**
     * Decipher the data in place, if it is enciphered and there is a key to
     * unlock it.
     * 
     * @param data
     *            the data to unlock
     */
    public void decipher(byte[] data) {
        String cipherKeyString = (String) getBookMetaData().getProperty(ConfigEntryType.CIPHER_KEY);
        if (cipherKeyString != null) {
            Sapphire cipherEngine = new Sapphire(cipherKeyString.getBytes());
            for (int i = 0; i < data.length; i++) {
                data[i] = cipherEngine.cipher(data[i]);
            }
            // destroy any evidence!
            cipherEngine.burn();
        }
    }

    /**
     * Encipher the data in place, if there is a key to unlock it.
     * 
     * @param data
     */
    public void encipher(byte[] data) {
        // Enciphering and deciphering are the same!
        decipher(data);
    }

    public URI getExpandedDataPath() throws BookException {
        URI loc = NetUtil.lengthenURI(bmd.getLibrary(), (String) bmd.getProperty(ConfigEntryType.DATA_PATH));

        if (loc == null) {
            // FIXME(DMS): missing parameter
            throw new BookException(JSOtherMsg.lookupText("Missing data files for old and new testaments in {0}."));
        }

        return loc;
    }

    /**
     * Initialize a AbstractBackend before use. This method needs to call
     * addKey() a number of times on GenBookBackend
     */
    public Key readIndex() {
        // TODO(dms): Eliminate readIndex by deriving GenBookBackend from
        // AbstractKeyBackend
        return null;
    }

    /**
     * Determine whether this Book contains the key in question
     * 
     * @param key
     *            The key whose presence is desired.
     * @return true if the Book contains the key
     */
    public abstract boolean contains(Key key);

    /**
     * Get the text allotted for the given entry
     * 
     * @param key
     *            The key to fetch
     * @return String The data for the verse in question
     * @throws BookException
     *             If the data can not be read.
     */
    public abstract String getRawText(Key key) throws BookException;

    /**
     * Set the text allotted for the given verse
     * 
     * @param key
     *            The key to set text to
     * @param text
     *            The text to be set for key
     * @throws BookException
     *             If the data can not be set.
     * @throws IOException
     *             If the module data path could not be created.
     */
    public abstract void setRawText(Key key, String text) throws BookException, IOException;

    /**
     * Sets alias for a comment on a verse range
     * I.e. setRawText() was for verse range Gen.1.1-3 then setAliasKey should be called for Gen.1.1.2 and Gen.1.1.3
     * @param alias Alias Key
     * @param source Source Key
     * @throws IOException Exception when anything goes wrong on writing the alias
     */
    public abstract void setAliasKey(Key alias, Key source) throws IOException;

    /**
     * Create the directory to hold the Book if it does not exist.
     * 
     * @throws IOException
     * @throws BookException
     */
    public void create() throws IOException, BookException {
        File dataPath = new File(getExpandedDataPath());
        if (!dataPath.exists() && !dataPath.mkdirs()) {
            throw new IOException("Unable to create module data path!");
        }
    }

    /**
     * Returns whether this AbstractBackend is implemented.
     * 
     * @return true if this AbstractBackend is implemented.
     */
    public boolean isSupported() {
        return true;
    }

    /**
     * A Backend is writable if the file system allows the underlying files to
     * be opened for writing and if the backend has implemented writing.
     * Ultimately, all drivers should allow writing. At this time writing is not
     * supported by backends, so abstract implementations should return false
     * and let specific implementations return true otherwise.
     * 
     * @return true if the book is writable
     */
    public boolean isWritable() {
        return false;
    }

    private SwordBookMetaData bmd;
}
