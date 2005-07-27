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
package org.crosswire.jsword.book.raw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.crosswire.jsword.passage.BibleInfo;

/**
 * A CaseInstsMem provides access to the list of case ids that
 * make up a Passage. The central interface is an array of cases
 * of words in the given verse.
 * <p>This is different from WordInsts and PuncInsts
 * in that there is no CaseInsts. This is because there are only
 * 4 cases worthy of note, and they are all well defined in
 * PassageUtil.
 * <p>Storing these 4 cases takes 2 bits per word, 4 words per byte.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class CaseInstsMem extends InstsMem
{
    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     */
    public CaseInstsMem(RawBook raw, boolean create) throws Exception
    {
        super(raw, RawConstants.FILE_CASE_INST, create);
    }

    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     * @param messages We append stuff here if something went wrong
     */
    public CaseInstsMem(RawBook raw, boolean create, StringBuffer messages)
    {
        super(raw, RawConstants.FILE_CASE_INST, create, messages);
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

        assert ssig.equals(RawConstants.SIG_CASE_INST);

        for (int i=0; i<BibleInfo.versesInBible(); i++)
        {
            int insts = din.readByte();
            array[i] = new int[insts];
            for (int j=0; j<insts; j+=4)
            {
                byte b = din.readByte();

                for (int k=0; k<4; k++)
                {
                    if (j+k < array[i].length)
                    {
                        array[i][j+k] = (b >> (6-(2*k))) & 3;
                    }
                }
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

        dout.writeBytes(RawConstants.SIG_CASE_INST);

        for (int i=0; i<BibleInfo.versesInBible(); i++)
        {
            if (array[i] == null)
            {
                dout.writeByte(0);
            }
            else
            {
                dout.writeByte(array[i].length);
                for (int j=0; j<array[i].length; j+=4)
                {
                    byte b = 0;

                    for (int k=0; k<4; k++)
                    {
                        if (j+k < array[i].length)
                        {
                            b += array[i][j+k] << (6-(2*k));
                        }
                    }

                    dout.writeByte(b);
                }
            }
        }

        dout.close();
    }
}

