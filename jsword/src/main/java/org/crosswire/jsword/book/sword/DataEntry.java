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
 * Copyright: 2008
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.book.DataPolice;

/**
 * Data entry represents an entry in a Data file.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DataEntry
{
    /**
     * Construct a data entry.
     * 
     * @param name A name used for diagnostics.
     * @param data The data for this entry.
     * @param charset The character encoding for this entry.
     */
    public DataEntry(String name, byte[] data, String charset)
    {
        this.name    = name;
        this.data    = data;
        this.charset = charset;
    }

    /**
     * Get the key from this DataEntry.
     * @param name A diagnostic name. Either the module initials or the expected key name.
     * @param charset The character encoding for the String
     * @return the key
     */
    public String getKey()
    {
        if (key == null)
        {
            keyEnd = SwordUtil.findByte(data, SEPARATOR);

            if (keyEnd < 0)
            {
                DataPolice.report("Failed to find key. name='" + name + "' data='" + data + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return ""; //$NON-NLS-1$
            }

            key = SwordUtil.decode(name, data, keyEnd, charset).trim();
            
            // for some weird reason plain text dictionaries
            // all get \ added to the ends of the index entries.
            if (key.endsWith("\\")) //$NON-NLS-1$
            {
                key = key.substring(0, key.length() - 1);
            }
        }

        return key;
    }

    public boolean isLinkEntry()
    {
        String linkCheck = SwordUtil.decode(name, data, getKeyEnd() + 1, 5, charset);
        return "@LINK".equals(linkCheck); //$NON-NLS-1$
    }

    /**
     * Get the link target for this entry. One entry can be chained to another.
     * If the entry is not linked then it is an error to call this method.
     * 
     * @return the key to look up
     * @see isLinkEntry
     */
    public String getLinkTarget()
    {
        // 6 represents the length of "@LINK" + 1 to skip the last separator.
        return SwordUtil.decode(name, data, getKeyEnd() + 6, data.length - (getLinkEnd() + 1), charset).trim();
    }

    /**
     * Get the raw text from this entry.
     * 
     * @return the raw text
     */
    public String getRawText()
    {
        int textEnd = getKeyEnd() + 1;
        return SwordUtil.decode(name, data, textEnd, data.length - textEnd, charset).trim();
    }

    /**
     * Get the position of the first \n in the data. This represents the end of the key
     * and the start of the rest of the data.
     * 
     * @return the end of the key or -1 if not found.
     */
    private int getKeyEnd()
    {
        if (keyEnd == 0)
        {
            keyEnd = SwordUtil.findByte(data, SEPARATOR);
        }
        return keyEnd;
    }

    /**
     * Get the position of the second \n in the data. This represents the end of the link
     * and the start of the rest of the data.
     * 
     * @return the end of the link or -1 if not found.
     */
    private int getLinkEnd()
    {
        if (linkEnd == 0)
        {
            linkEnd = SwordUtil.findByte(data, getKeyEnd() + 1, SEPARATOR);
        }
        return linkEnd;
    }

    /**
     * Used to separate the key name from the key value
     * Note: it may be \r\n or just \n, so only need \n.
     * ^M=CR=13=0x0d=\r
     * ^J=LF=10=0x0a=\n
     */
    private static final byte SEPARATOR = 10;

    /**
     * A diagnostic name.
     */
    private String            name;

    /**
     * The data entry as it comes out of the data file.
     */
    private byte[]            data;

    /**
     * The character set of the data entry.
     */
    private String            charset;

    /**
     * The key in the data entry.
     */
    private String            key;

    /**
     * The index of the separator between the key and the rest of the stuff.
     */
    private int               keyEnd;

    /**
     * The index of the separator between the link and the rest of the stuff.
     */
    private int               linkEnd;
}
