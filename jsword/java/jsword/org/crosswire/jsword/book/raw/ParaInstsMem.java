
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
* @author Joe Walker
* @version D0.I0.T0
*/
public class ParaInstsMem extends InstsMem
{
    /**
    * Basic constructor
    * @param raw Reference to the RawBible that is using us
    * @param filename The leaf name to read/write
    * @param create Should we start all over again
    */
    public ParaInstsMem(RawBible raw, boolean create) throws Exception
    {
        super(raw, "parainst.idx", create);
    }

    /**
    * Basic constructor
    * @param raw Reference to the RawBible that is using us
    * @param filename The leaf name to read/write
    * @param create Should we start all over again
    */
    public ParaInstsMem(RawBible raw, boolean create, StringBuffer messages)
    {
        super(raw, "parainst.idx", create, messages);
    }

    /**
    * Start all over again and clear the decks for more data.
    */
    public void init()
    {
        ref = PassageFactory.createPassage();
    }

    /**
    * Load the Resource from a stream
    * @param in The stream to read from
    */
    public void load(InputStream in) throws IOException, ClassNotFoundException
    {
        ObjectInputStream oin = new ObjectInputStream(in);

        byte[] asig = new byte[6];
        oin.readFully(asig);
        String ssig = new String(asig);
        if (!ssig.equals("RAW:AI"))
            throw new IOException("This file is not a ParaInst file");

        ref = (Passage) oin.readObject();

        oin.close();
    }

    /**
    * Ensure that all changes to the index of words are written to a
    * stream
    * @param out The stream to write to
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

    /** The storage of the Para markers */
    private Passage ref;
}

