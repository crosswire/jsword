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
 * Copyright: 2008-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

import org.crosswire.common.crypt.Sapphire;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data entry represents an entry in a Data file. The entry consists of a key
 * and an optional payload.
 * <p>The payload may be:</p>
 * <ul>
 * <li>the content, that is raw text</li>
 * <li>an alias (@LINK) for another entry</li>
 * <li>a block locator</li>
 * </ul>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DataEntry {
    /**
     * Construct a data entry.
     * 
     * @param name
     *            A name used for diagnostics.
     * @param data
     *            The data for this entry.
     * @param charset
     *            The character encoding for this entry.
     */
    public DataEntry(String name, byte[] data, String charset) {
        this.name = name;
        this.data = data.clone();
        this.charset = charset;
        // The key always ends with \n, typically \r\n
        this.keyEnd = SwordUtil.findByte(this.data, SEPARATOR);
    }

    /**
     * Get the name, that is, the diagnostic label, for this DataEntry.
     * 
     * @return the diagnostic name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the charset in which the data is encoded.
     * @return this entry's charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Get the key from this DataEntry.
     * 
     * @return the key
     */
    public String getKey() {
        if (key == null) {
            if (keyEnd < 0) {
                log.error("Failed to find key. name='" + name + "'");
                return "";
            }

            // The key may have whitespace, including \r on the end,
            // that is not actually part of the key.
            key = SwordUtil.decode(name, data, keyEnd, charset).trim();

            // for some weird reason plain text dictionaries
            // all get \ added to the ends of the index entries.
            if (key.endsWith("\\")) {
                key = key.substring(0, key.length() - 1);
            }
        }

        return key;
    }

    /**
     * Determine whether this entry is an alias for another.
     * 
     * @return whether this is an alias entry
     */
    public boolean isLinkEntry() {
        // 6 represents the length of "@LINK" when keyEnd is -1
        return keyEnd + 6 < data.length
                && data[keyEnd + 1] == '@'
                && data[keyEnd + 2] == 'L'
                && data[keyEnd + 3] == 'I'
                && data[keyEnd + 4] == 'N'
                && data[keyEnd + 5] == 'K';
    }

    /**
     * Get the link target for this entry. One entry can be chained to another.
     * If the entry is not linked then it is an error to call this method.
     * 
     * @return the key to look up
     * @see #isLinkEntry()
     */
    public String getLinkTarget() {
        // 6 represents the length of "@LINK" + 1 to skip the last separator.
        int linkStart = keyEnd + 6;
        int len = getLinkEnd() - linkStart + 1;
        return SwordUtil.decode(name, data, linkStart, len, charset).trim();
    }

    /**
     * Get the raw text from this entry.
     * 
     * @param cipherKey
     *            the key, if any, to (un)lock the text
     * @return the raw text
     */
    public String getRawText(byte[] cipherKey) {
        int textStart = keyEnd + 1;
        cipher(cipherKey, textStart);
        return SwordUtil.decode(name, data, textStart, data.length - textStart, charset).trim();
    }

    /**
     * Get the block start and entry position.
     * 
     * @return the index of the block
     */
    public DataIndex getBlockIndex() {
        int start = keyEnd + 1;
        return new DataIndex(SwordUtil.decodeLittleEndian32(data, start), SwordUtil.decodeLittleEndian32(data, start + 4));
    }

    /**
     * Get the position of the second \n in the data. This represents the end of
     * the link and the start of the rest of the data.
     * 
     * @return the end of the link or -1 if not found.
     */
    private int getLinkEnd() {
        if (linkEnd == 0) {
            linkEnd = SwordUtil.findByte(data, keyEnd + 1, SEPARATOR);
            if (linkEnd == -1) {
                linkEnd = data.length - 1;
            }
        }
        return linkEnd;
    }

    /**
     * Decipher/Encipher the data in place, if there is a cipher key.
     * 
     * @param cipherKey
     *            the key to the cipher
     */
    public void cipher(byte[] cipherKey, int offset) {
        if (cipherKey != null && cipherKey.length > 0) {
            Sapphire cipherEngine = new Sapphire(cipherKey);
            for (int i = offset; i < data.length; i++) {
                data[i] = cipherEngine.cipher(data[i]);
            }
            // destroy any evidence!
            cipherEngine.burn();
        }
    }

    /**
     * Used to separate the key name from the key value Note: it may be \r\n or
     * just \n, so only need \n. ^M=CR=13=0x0d=\r ^J=LF=10=0x0a=\n
     */
    private static final byte SEPARATOR = 10;

    /**
     * A diagnostic name.
     */
    private String name;

    /**
     * The data entry as it comes out of the data file.
     */
    private byte[] data;

    /**
     * The character set of the data entry.
     */
    private String charset;

    /**
     * The key in the data entry.
     */
    private String key;

    /**
     * The index of the separator between the key and the payload.
     */
    private int keyEnd;

    /**
     * The index of the separator between the link and the payload.
     */
    private int linkEnd;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(DataEntry.class);
}
