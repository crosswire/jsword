
package org.crosswire.jsword.book.raw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.crosswire.jsword.passage.BibleInfo;

/**
 * A WordInstsMem provides access to the list of word ids that make up
 * a Passage. The central interface is an Eumeration over the words in
 * the given verse.
 * <p>We should probably avoid cacheing at this level since there are
 * other Bibles that could do with cacheing.
 *
 * <p>The layout of the underlying file probably has a lot in common with
 * the WordResource class, instead of an array of ascii bytes for each
 * index, we have a get of integers in bytes for an index. Techniques like
 * capitalizing the first letter to indicate the start of a new word will
 * not work here, so perhaps we should no do them in WordResource either?
 *
 * <p>The other difference with the WordResource class is that there is no
 * inherent meaning in having abimelech after aaron, whereas having
 * Gen 1:2 after Gen 1:1 makes perfect sense.
 * Inheritance will make use of these similarities, however we need to
 * remember that there are some important conceptual differences.
 *
 * <p>In the AV there are 790790 words, in the NIV there are 726111 words.
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
public class WordInstsMem extends InstsMem
{
    /**
     * Basic constructor
     * @param raw Reference to the RawBible that is using us
     * @param create Should we start all over again
     */
    public WordInstsMem(RawBible raw, boolean create) throws Exception
    {
        super(raw, "wordinst.idx", create);
    }

    /**
     * Load the Resource from a stream
     * @param in The stream to read from
     */
    public void load(InputStream in) throws IOException, ClassNotFoundException
    {
        DataInputStream din = new DataInputStream(in);

        byte[] asig = new byte[6];
        din.readFully(asig);
        String ssig = new String(asig);
        if (!ssig.equals("RAW:WI"))
            throw new IOException("This file is not a WordInst file");

        for (int i=0; i<BibleInfo.versesInBible(); i++)
        {
            int insts = din.readByte();
            array[i] = new int[insts];
            for (int j=0; j<insts; j++)
            {
                array[i][j] = din.readShort();
            }
        }

        din.close();
    }

    /**
     * Ensure that all changes to the index of words are written to a stream
     * @param out The stream to write to
     */
    public void save(OutputStream out) throws IOException
    {
        DataOutputStream dout = new DataOutputStream(out);

        dout.writeBytes("RAW:WI");

        for (int i=0; i<BibleInfo.versesInBible(); i++)
        {
            if (array[i] == null)
            {
                dout.writeByte(0);
            }
            else
            {
                dout.writeByte(array[i].length);
                for (int j=0; j<array[i].length; j++)
                {
                    dout.writeShort(array[i][j]);
                }
            }
        }

        dout.close();
    }
}
