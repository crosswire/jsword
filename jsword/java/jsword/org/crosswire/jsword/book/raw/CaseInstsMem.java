
package org.crosswire.jsword.book.raw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.crosswire.jsword.passage.Books;

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
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @see PassageUtil#getCase(String)
* @see PassageUtil#setCase(String,int)
* @author Joe Walker
* @version D0.I0.T0
*/
public class CaseInstsMem extends InstsMem
{
    /**
    * Basic constructor
    * @param raw Reference to the RawBible that is using us
    * @param filename The leaf name to read/write
    * @param create Should we start all over again
    */
    public CaseInstsMem(RawBible raw, boolean create) throws Exception
    {
        super(raw, "caseinst.idx", create);
    }

    /**
    * Basic constructor
    * @param raw Reference to the RawBible that is using us
    * @param filename The leaf name to read/write
    * @param create Should we start all over again
    * @param messages We append stuff here if something went wrong
    */
    public CaseInstsMem(RawBible raw, boolean create, StringBuffer messages)
    {
        super(raw, "caseinst.idx", create, messages);
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
        if (!ssig.equals("RAW:CI"))
            throw new IOException("This file is not a CaseInst file");

        for (int i=0; i<Books.versesInBible(); i++)
        {
            int insts = din.readByte();
            array[i] = new int[insts];
            for (int j=0; j<insts; j+=4)
            {
                byte b = din.readByte();

                for (int k=0; k<4; k++)
                {
                    if (j+k < array[i].length)
                        array[i][j+k] = (b >> (6-(2*k))) & 3;
                }
            }
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

        dout.writeBytes("RAW:CI");

        for (int i=0; i<Books.versesInBible(); i++)
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
                            b += array[i][j+k] << (6-(2*k));
                    }

                    dout.writeByte(b);
                }
            }
        }

        dout.close();
    }
}

