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
 * © CrossWire Bible Society, 2014 - 2016
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;

/**
 * A NullBackend is not attached to resources.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class NullBackend implements Backend {

    public NullBackend() {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#getBookMetaData()
     */
    public SwordBookMetaData getBookMetaData() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#decipher(byte[])
     */
    public void decipher(byte[] data) {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#encipher(byte[])
     */
    public void encipher(byte[] data) {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#readIndex()
     */
    public Key readIndex() {
        return new DefaultKeyList();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key) throws BookException {
        return "";
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(Key alias, Key source) throws BookException {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#size(org.crosswire.jsword.passage.Key)
     */
    public int getRawTextLength(Key key) {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#getGlobalKeyList()
     */
    public Key getGlobalKeyList() throws BookException {
        return new DefaultKeyList();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#readToOsis(org.crosswire.jsword.passage.Key, org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor)
     */
    public List readToOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
        return new ArrayList();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.Backend#create()
     */
    public void create() throws IOException, BookException {
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

}
