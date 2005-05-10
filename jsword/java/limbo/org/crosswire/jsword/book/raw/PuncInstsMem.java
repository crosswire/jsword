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

import org.crosswire.jsword.passage.BibleInfo;

/**
 * A PuncInstsMem provides access to the list of punctuation ids that
 * make up a Passage. The central interface is an Eumeration over the
 * words in the given verse.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PuncInstsMem extends InstsMem
{
    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     */
    public PuncInstsMem(RawBook raw, boolean create) throws Exception
    {
        super(raw, RawConstants.FILE_PUNC_INST, create);
    }

    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     * @param messages We append stuff here if something went wrong
     */
    public PuncInstsMem(RawBook raw, boolean create, StringBuffer messages)
    {
        super(raw, RawConstants.FILE_PUNC_INST, create, messages);
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
        assert ssig.equals(RawConstants.SIG_PUNC_INST);

        for (int i=0; i<BibleInfo.versesInBible(); i++)
        {
            int insts = din.readByte();
            array[i] = new int[insts];
            for (int j=0; j<insts; j++)
            {
                array[i][j] = din.readByte();
            }
        }

        din.close();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#save(java.io.OutputStream)
     */
    public void save(OutputStream out) throws IOException
    {
        DataOutputStream dout = new DataOutputStream(out);

        dout.writeBytes(RawConstants.SIG_PUNC_INST);

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
                    dout.writeByte(array[i][j]);
                }
            }
        }

        dout.close();
    }
}

