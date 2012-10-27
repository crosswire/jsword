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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.crypt.Sapphire;
import org.crosswire.common.util.IOUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.jdom.Content;

/**
 * A generic way to read data from disk for later formatting.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class AbstractBackend<T extends OpenFileState> implements StatefulFileBackedBackend<T> {
 
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
     * @param processor
     *            processor that executes before/after the content is read from
     *            disk or another kind of backend
     * @return String The data for the verse in question
     * @throws BookException
     *             If the data can not be read.
     */
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key)
     */
    public List<Content> readToOsis(Key key, RawTextToXmlProcessor processor) throws BookException {

        final List<Content> content = new ArrayList<Content>();

        // FIXME(CJB) behaviour has changed from previously where not finding OT
        // or NT did not throw exception
        T openFileState = null;

        try {
            openFileState = initState();
            switch (this.bmd.getKeyType()) {
            case LIST:
            case TREE:
                readNormalOsis(key, processor, content, openFileState);
                break;
            case VERSE:
                readPassageOsis(key, processor, content, openFileState);
                break;
            default:
                throw new BookException("Book has unsupported type of key");
            }

            return content;
        } finally {
            IOUtil.close(openFileState);
        }
    }

    private void readNormalOsis(Key key, RawTextToXmlProcessor processor, List<Content> content, T openFileState) throws BookException {
        // simply lookup the key and process the relevant information
        Iterator<Key> iterator = key.iterator();

        while (iterator.hasNext()) {
            Key next = iterator.next();
            String rawText;
            try {
                rawText = readRawContent(openFileState, next, next.getName());
                processor.postVerse(next, content, rawText);
            } catch (IOException e) {
                // failed to process key 'next'
                throwFailedKeyException(key, next, e);
            }
        }
    }

    /**
     * Reads a passage as OSIS
     * 
     * @param key
     *            the given key
     * @param processor
     *            a processor for which to do things with
     * @param content
     *            a list of content to be appended to (i.e. the OSIS data)
     * @param openFileState
     *            the open file state, from which we read things
     * @throws BookException
     *             a book exception if we failed to read the book
     */
    private Verse readPassageOsis(Key key, RawTextToXmlProcessor processor, final List<Content> content, T openFileState) throws BookException {
        Verse currentVerse = null;
        try {
            
            
            final Passage ref = (key instanceof Passage ? (Passage) key : KeyUtil.getPassage(key, getVersification()));
            final Iterator<Key> rit = ref.rangeIterator(RestrictionType.CHAPTER);
            while (rit.hasNext()) {
                VerseRange range = (VerseRange) rit.next();
                processor.preRange(range, content);

                // FIXME(CJB): can this now be optmized since we can calculate
                // the buffer size of what to read?
                // now iterate through all verses in range
                for (Key verseInRange : range) {
                    currentVerse = KeyUtil.getVerse(verseInRange, getVersification());
                    final String keyName = verseInRange.getName();
                    String rawText = readRawContent(openFileState, currentVerse, keyName);
                    processor.postVerse(verseInRange, content, rawText);
                }
            }
        } catch (IOException e) {
            throwFailedKeyException(key, currentVerse, e);
        }
        return currentVerse;
    }

    /**
     * If non-null, currentKey is used to throw the exception, other, masterKey
     * is used instead, which will be more general.
     * 
     * @param masterKey
     *            the key containing currentKey
     * @param currentKey
     *            the currentKey
     * @param e
     *            the exception that occured
     * @throws BookException
     *             always thrown, a {@link BookException}
     */
    private void throwFailedKeyException(Key masterKey, Key currentKey, IOException e) throws BookException {
        // TRANSLATOR: Common error condition: The file could not be read.
        // There can be many reasons.
        // {0} is a placeholder for the key.
        if (currentKey == null) {
            throw new BookException(JSMsg.gettext("Error reading {0}", masterKey.getName()), e);
        }
        throw new BookException(JSMsg.gettext("Error reading {0}", currentKey.getName()), e);
    }

    /**
     * Create the directory to hold the Book if it does not exist.
     * 
     * @throws IOException
     * @throws BookException
     */
    public void create() throws IOException, BookException {
        File dataPath = new File(SwordUtil.getExpandedDataPath(getBookMetaData()));
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

    public Versification getVersification() {
        if(this.versificationSystem == null) {
            this.versificationSystem = Versifications.instance().getVersification((String) getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION)); 
        }
        return versificationSystem;
    }
    
    private SwordBookMetaData bmd;
    private Versification versificationSystem;
}
