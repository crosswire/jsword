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
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.crypt.Sapphire;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.book.sword.state.OpenFileStateManager;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom2.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic way to read data from disk for later formatting.
 *
 * @param <T> The type of the OpenFileState that this class extends.
 * @author Joe Walker
 * @author DM Smith
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 */
public abstract class AbstractBackend<T extends OpenFileState> implements StatefulFileBackedBackend<T>, Backend<T> {

    /**
     * Default constructor for the sake of serialization.
     */
    /* protected */
    public AbstractBackend() {
    }

    /**
     * Construct a minimal backend
     *
     * @param sbmd
     */
    public AbstractBackend(SwordBookMetaData sbmd) {
        bmd = sbmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#getBookMetaData()
     */
    public BookMetaData getBookMetaData() {
        return bmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#decipher(byte[])
     */
    public void decipher(byte[] data) {
        String cipherKeyString = getBookMetaData().getProperty(SwordBookMetaData.KEY_CIPHER_KEY);
        if (cipherKeyString != null) {
            Sapphire cipherEngine = new Sapphire(cipherKeyString.getBytes());
            for (int i = 0; i < data.length; i++) {
                data[i] = cipherEngine.cipher(data[i]);
            }
            // destroy any evidence!
            cipherEngine.burn();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#encipher(byte[])
     */
    public void encipher(byte[] data) {
        // Enciphering and deciphering are the same!
        decipher(data);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#readIndex()
     */
    public Key readIndex() {
        // TODO(dms): Eliminate readIndex by deriving GenBookBackend from
        // AbstractKeyBackend
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#contains(org.crosswire.jsword.passage.Key)
     */
    public abstract boolean contains(Key key);

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key) throws BookException {
        T state = null;
        try {
            state = initState();
            return readRawContent(state, key);
        } catch (IOException e) {
            throw new BookException("Unable to obtain raw content from backend for key='" + key + '\'', e);
        } finally {
            OpenFileStateManager.instance().release(state);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(Key alias, Key source) throws BookException {
        T state = null;
        try {
            state = initState();
            setAliasKey(state, alias, source);
        } catch (IOException e) {
            throw new BookException(JSOtherMsg.lookupText("Unable to save {0}.", alias.getOsisID()));
        } finally {
            OpenFileStateManager.instance().release(state);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#size(org.crosswire.jsword.passage.Key)
     */
    public int getRawTextLength(Key key) {
        try {
            String raw = getRawText(key);
            return raw == null ? 0 : raw.length();
        } catch (BookException e) {
            return 0;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#getGlobalKeyList()
     */
    public Key getGlobalKeyList() throws BookException {
        //by default, this is not implemented
        throw new UnsupportedOperationException("Fast global key list unsupported in this backend");
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#readToOsis(org.crosswire.jsword.passage.Key, org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor)
     */
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.AbstractBackend#getRawText(org.crosswire.jsword.passage.Key)
     */
    public List<Content> readToOsis(Key key, RawTextToXmlProcessor processor) throws BookException {

        final List<Content> content = new ArrayList<Content>();

        T openFileState = null;

        try {
            openFileState = initState();
            switch (this.bmd.getKeyType()) {
                case LIST:
                    readNormalOsis(key, processor, content, openFileState);
                    break;
                case TREE:
                    readNormalOsisSingleKey(key, processor, content, openFileState);
                    break;
                case VERSE:
                    readPassageOsis(key, processor, content, openFileState);
                    break;
                default:
                    throw new BookException("Book has unsupported type of key");
            }

            return content;
        } finally {
            OpenFileStateManager.instance().release(openFileState);
        }
    }

    private void readNormalOsis(Key key, RawTextToXmlProcessor processor, List<Content> content, T openFileState) throws BookException {
        // simply lookup the key and process the relevant information
        Iterator<Key> iterator = key.iterator();

        while (iterator.hasNext()) {
            Key next = iterator.next();
            String rawText;
            try {
                rawText = readRawContent(openFileState, next);
                processor.postVerse(next, content, rawText);
            } catch (IOException e) {
                // failed to process key 'next'
                throwFailedKeyException(key, next, e);
            }
        }
    }

    /**
     * Avoid using iterator for GenBook TreeKeys which would cause a GenBook nodes children to be appended to their parent 
     * e.g. the top level page would include the whole book and result in OOM error 
     */
    private void readNormalOsisSingleKey(Key key, RawTextToXmlProcessor processor, List<Content> content, T openFileState) throws BookException {
        String rawText;
        try {
            rawText = readRawContent(openFileState, key);
            processor.postVerse(key, content, rawText);
        } catch (IOException e) {
            // failed to process key
            throwFailedKeyException(key, key, e);
        }
    }

    /**
     * Reads a passage as OSIS
     *
     * @param key           the given key
     * @param processor     a processor for which to do things with
     * @param content       a list of content to be appended to (i.e. the OSIS data)
     * @param openFileState the open file state, from which we read things
     * @throws BookException a book exception if we failed to read the book
     */
    private Verse readPassageOsis(Key key, RawTextToXmlProcessor processor, final List<Content> content, T openFileState) throws BookException {
        Verse currentVerse = null;
        final Passage ref = KeyUtil.getPassage(key);
        final Iterator<VerseRange> rit = ref.rangeIterator(RestrictionType.CHAPTER);
        while (rit.hasNext()) {
            VerseRange range = rit.next();
            processor.preRange(range, content);

            // FIXME(CJB): can this now be optimized since we can calculate
            // the buffer size of what to read?
            // now iterate through all verses in range
            for (Key verseInRange : range) {
                currentVerse = KeyUtil.getVerse(verseInRange);
                try {
                    String rawText = readRawContent(openFileState, currentVerse);
                    processor.postVerse(verseInRange, content, rawText);
                } catch (IOException e) {
                    //some versifications have more verses than modules contain - so can't throw
                    //an error here...
                    LOGGER.debug(e.getMessage(), e);
                }
            }
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#create()
     */
    public void create() throws IOException, BookException {
        File dataPath = new File(SwordUtil.getExpandedDataPath(getBookMetaData()));
        if (!dataPath.exists() && !dataPath.mkdirs()) {
            throw new IOException("Unable to create module data path!");
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#isSupported()
     */
    public boolean isSupported() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#isWritable()
     */
    public boolean isWritable() {
        return false;
    }

    private SwordBookMetaData bmd;
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBackend.class);
}
