
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
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class PuncItemsMem extends ItemsMem
{
    /**
     * Create a PuncItemsMem from a File that contains the dictionary.
     * @param raw Reference to the RawBible that is using us
     * @param filename The leaf name to read/write
     * @param create Should we start all over again
     */
    public PuncItemsMem(RawBible raw, boolean create) throws Exception
    {
        super(raw, "punc.idx", create);
    }

    /**
     * Create a PuncItemsMem from a File that contains the dictionary.
     * @param raw Reference to the RawBible that is using us
     * @param filename The leaf name to read/write
     * @param create Should we start all over again
     * @param messages We append stuff here if something went wrong
     */
    public PuncItemsMem(RawBible raw, boolean create, StringBuffer messages)
    {
        super(raw, "punc.idx", create, messages);
    }

    /**
     * How many items are there in this index?
     * @return The number of items that we must remember
     */
    public int getMaxItems()
    {
        return 1000;
    }

    /**
     * Load the Resource from a stream
     * @param in The stream to read from
     */
    public void load(InputStream in) throws IOException
    {
        DataInputStream din = new DataInputStream(in);

        byte[] asig = new byte[6];
        din.readFully(asig);
        String ssig = new String(asig);
        if (!ssig.equals("RAW:PR"))
            throw new IOException("This file is not a Punc file");

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

    /**
     * Ensure that all changes to the index of words are written to a
     * stream
     * @param out The stream to write to
     */
    public void save(OutputStream out) throws IOException
    {
        DataOutputStream dout = new DataOutputStream(out);

        dout.writeBytes("RAW:PR");
        dout.writeInt(hash.size());

        for (int i=0; i<hash.size(); i++)
        {
            dout.writeByte(array[i].length());
            dout.writeBytes(array[i]);
        }

        dout.close();
    }
}
