
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;

/**
 * A ParaInstsMem provides access to the list of paragraphs that
 * punctuate the Bible.
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class ParaInstsMem extends InstsMem
{
    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     */
    public ParaInstsMem(RawBook raw, boolean create) throws IOException
    {
        super(raw, "parainst.idx", create);
    }

    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     */
    public ParaInstsMem(RawBook raw, boolean create, StringBuffer messages)
    {
        super(raw, "parainst.idx", create, messages);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#init()
     */
    public void init()
    {
        ref = PassageFactory.createPassage();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#load(java.io.InputStream)
     */
    public void load(InputStream in) throws IOException
    {
        ObjectInputStream oin = new ObjectInputStream(in);

        byte[] asig = new byte[6];
        oin.readFully(asig);
        String ssig = new String(asig);
        if (!ssig.equals("RAW:AI"))
            throw new IOException("This file is not a ParaInst file");

        try
        {
            ref = (Passage) oin.readObject();
        }
        catch (ClassNotFoundException ex)
        {
            throw new IOException("Class not found: "+ex.getMessage());
        }

        oin.close();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#save(java.io.OutputStream)
     */
    public void save(OutputStream out) throws IOException
    {
        ObjectOutputStream oout = new ObjectOutputStream(out);

        oout.writeBytes("RAW:AI");
        oout.writeObject(ref);

        oout.close();
    }

    /**
     * Set the new paragraph status for a verse
     * @param para The paragraph status
     * @param verse The Verse to set data on
     */
    public void setPara(boolean para, Verse verse)
    {
        if (para)
        {
            ref.add(verse);
        }
        else
        {
            ref.remove(verse);
        }
    }

    /**
     * Set the new paragraph status for a verse.
     * If the load failed then we treat each verse as a new paragraph
     * @param verse The Verse to get data on
     */
    public boolean getPara(Verse verse)
    {
        return ref.contains(verse);
    }

    /**
     * The storage of the Para markers
     */
    private Passage ref;
}

