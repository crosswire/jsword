
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import org.crosswire.jsword.passage.Books;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;

/**
 * PuncInstsDisk is like PuncInstsMem however the entire block of data is
 * not stored in memory, it is simply indexed and left on disk.
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
public class PuncInstsDisk extends InstsDisk
{
    /**
     * Basic constructor
     * @param raw Reference to the RawBible that is using us
     * @param create Should we start all over again
     */
    public PuncInstsDisk(RawBible raw, boolean create) throws Exception
    {
        super(raw, "puncinst.idx", create);
    }

    /**
     * Create a WordResource from a File that contains the dictionary.
     * @param raw Reference to the RawBible that is using us
     * @param create Should we start all over again
     * @param messages We append stuff here if something went wrong
     */
    public PuncInstsDisk(RawBible raw, boolean create, StringBuffer messages)
    {
        super(raw, "puncinst.idx", create, messages);
    }

    /**
     * Load the Resource from a named file
     */
    public void load() throws IOException, ClassNotFoundException, MalformedURLException
    {
        URL url = NetUtil.lengthenURL(raw.getLocalURLBibleMetaData().getURL(), leafname);
        raf = new RandomAccessFile(url.getFile(), "r");

        byte[] asig = new byte[6];
        raf.readFully(asig);
        String ssig = new String(asig);
        if (!ssig.equals("RAW:PI"))
            throw new IOException("This file is not a PuncInst file");

        for (int i=0; i<Books.versesInBible(); i++)
        {
            index[i] = raf.getFilePointer();

            // skip over the data
            int insts = raf.readByte();
            for (int j=0; j<insts; j++)
                raf.readShort();
        }
    }

    /**
     * Retrieve an ordered list of the words in a Verse
     * @param verse The Verse to retrieve words for
     * @return An array of word indexes
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

    /** The random access file */
    protected RandomAccessFile raf;
}
