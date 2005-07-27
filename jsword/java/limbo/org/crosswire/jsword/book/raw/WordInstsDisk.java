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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.passage.BibleInfo;

/**
 * WordInstsDisk is like WordInstsMem however the entire block of data is
 * not stored in memory, it is simply indexed and left on disk.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class WordInstsDisk extends InstsDisk
{
    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     */
    public WordInstsDisk(RawBook raw, boolean create) throws IOException
    {
        super(raw, RawConstants.FILE_WORD_INST, create);
    }

    /**
     * Create a WordResource from a File that contains the dictionary.
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     * @param messages We append stuff here if something went wrong
     */
    public WordInstsDisk(RawBook raw, boolean create, StringBuffer messages)
    {
        super(raw, RawConstants.FILE_WORD_INST, create, messages);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.InstsDisk#load()
     */
    public void load() throws IOException
    {
        URL url = NetUtil.lengthenURL(raw.getURL(), leafname);
        raf = new RandomAccessFile(url.getFile(), FileUtil.MODE_READ);

        byte[] asig = new byte[6];
        raf.readFully(asig);

        String ssig = new String(asig);
        assert ssig.equals(RawConstants.SIG_WORD_INST);

        for (int i=0; i<BibleInfo.versesInBible(); i++)
        {
            index[i] = raf.getFilePointer();

            // skip over the data
            int insts = raf.readByte();
            for (int j=0; j<insts; j++)
            {
                raf.readShort();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Insts#getIndexes(int)
     */
    public int[] getIndexes(int ordinal)
    {
        try
        {
            raf.seek(index[ordinal-1]);

            int insts = raf.readByte();
            int[] ret = new int[insts];

            for (int j=0; j<insts; j++)
            {
                ret[j] = raf.readShort();
            }

            return ret;
        }
        catch (IOException ex)
        {
            // This really shouldn't happen as we have already read the
            // entire file at init time, so this is probably OK?
            Reporter.informUser(this, ex);
            return new int[0];
        }
    }

    /**
     * The random access file
     */
    protected RandomAccessFile raf;
}
