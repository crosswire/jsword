/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.book.raw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * PuncItemsMem is almost identical to WordItemsMem, but the Dictionary is
 * much smaller, there are almost certainly less than 256 different
 * intra-word punctuation sets, so we will only need 1 byte per word
 * instead of 2.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PuncItemsMem extends ItemsMem
{
    /**
     * Create a PuncItemsMem from a File that contains the dictionary.
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     */
    public PuncItemsMem(RawBook raw, boolean create) throws Exception
    {
        super(raw, RawConstants.FILE_PUNC_ITEM, create);
    }

    /**
     * Create a PuncItemsMem from a File that contains the dictionary.
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     * @param messages We append stuff here if something went wrong
     */
    public PuncItemsMem(RawBook raw, boolean create, StringBuffer messages)
    {
        super(raw, RawConstants.FILE_PUNC_ITEM, create, messages);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.ItemsMem#getMaxItems()
     */
    public int getMaxItems()
    {
        return 1000;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#load(java.io.InputStream)
     */
    public void load(InputStream in) throws IOException
    {
        DataInputStream din = new DataInputStream(in);

        byte[] asig = new byte[6];
        din.readFully(asig);

        String ssig = new String(asig);
        assert ssig.equals(RawConstants.SIG_PUNC_ITEM);

        count = din.readInt();
        hash = new Hashtable(count);
        array = new String[count];

        for (int i=0; i<count; i++)
        {
            byte wordlen = din.readByte();
            byte[] aword = new byte[wordlen];
            din.readFully(aword);
            String word = new String(aword);

            hash.put(word, new Integer(i));
            array[i] = word;
        }

        din.close();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#save(java.io.OutputStream)
     */
    public void save(OutputStream out) throws IOException
    {
        DataOutputStream dout = new DataOutputStream(out);

        dout.writeBytes(RawConstants.SIG_PUNC_ITEM);
        dout.writeInt(hash.size());

        for (int i=0; i<hash.size(); i++)
        {
            dout.writeByte(array[i].length());
            dout.writeBytes(array[i]);
        }

        dout.close();
    }
}
