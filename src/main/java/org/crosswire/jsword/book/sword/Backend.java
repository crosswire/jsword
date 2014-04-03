package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.util.List;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.passage.Key;
import org.jdom2.Content;

public interface Backend<T extends OpenFileState> {

    /**
     * @return Returns the Sword BookMetaData.
     */
    public abstract SwordBookMetaData getBookMetaData();

    /**
     * Decipher the data in place, if it is enciphered and there is a key to
     * unlock it.
     *
     * @param data the data to unlock
     */
    public abstract void decipher(byte[] data);

    /**
     * Encipher the data in place, if there is a key to unlock it.
     *
     * @param data
     */
    public abstract void encipher(byte[] data);

    /**
     * Initialize a AbstractBackend before use. This method needs to call
     * addKey() a number of times on GenBookBackend
     * @deprecated no replacement
     */
    @Deprecated
    public abstract Key readIndex();

    /**
     * Determine whether this Book contains the key in question
     *
     * @param key The key whose presence is desired.
     * @return true if the Book contains the key
     */
    public abstract boolean contains(Key key);

    /**
     * Get the text as it is found in the Book for the given key
     * 
     * @param key the key for which the raw text is desired.
     * @return the text from the module
     * @throws BookException 
     */
    public abstract String getRawText(Key key) throws BookException;

    public abstract void setAliasKey(Key alias, Key source) throws BookException;

    /**
     * Determine the size of the raw data for the key in question.
     * This method may not be faster than getting the raw text and getting its size.
     *
     * @param key The key whose raw data length is desired.
     * @return The length of the raw data, 0 if not a valid key.
     */
    public abstract int getRawTextLength(Key key);

    /**
     * Gets the fast global key list, and if this operation is not supported, throws a {@link UnsupportedOperationException}
     *
     * @return the fast global key list
     * @throws BookException the book exception if for some reason the book failed to be read properly.
     */
    public abstract Key getGlobalKeyList() throws BookException;

    /**
     * Get the text allotted for the given entry
     *
     * @param key       The key to fetch
     * @param processor processor that executes before/after the content is read from
     *                  disk or another kind of backend
     * @return String The data for the verse in question
     * @throws BookException If the data can not be read.
     */
    public abstract List<Content> readToOsis(Key key, RawTextToXmlProcessor processor) throws BookException;

    /**
     * Create the directory to hold the Book if it does not exist.
     *
     * @throws IOException
     * @throws BookException
     */
    public abstract void create() throws IOException, BookException;

    /**
     * Returns whether this AbstractBackend is implemented.
     *
     * @return true if this AbstractBackend is implemented.
     */
    public abstract boolean isSupported();

    /**
     * A Backend is writable if the file system allows the underlying files to
     * be opened for writing and if the backend has implemented writing.
     * Ultimately, all drivers should allow writing. At this time writing is not
     * supported by most backends, so abstract implementations should return false
     * and let specific implementations return true otherwise.
     *
     * @return true if the book is writable
     */
    public abstract boolean isWritable();

}
