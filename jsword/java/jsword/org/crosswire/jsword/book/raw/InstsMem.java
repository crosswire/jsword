
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.Verse;

/**
* InstsMem is a Base implementation of the Insts interface using the in
* memory model (Mem).
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
public abstract class InstsMem extends Mem implements Insts
{
    /**
    * Basic constructor
    * @param raw Reference to the RawBible that is using us
    * @param filename The leaf name to read/write
    * @param create Should we start all over again
    */
    public InstsMem(RawBible raw, String leafname, boolean create) throws Exception
    {
        super(raw, leafname, create);
    }

    /**
    * Basic constructor
    * @param raw Reference to the RawBible that is using us
    * @param filename The leaf name to read/write
    * @param create Should we start all over again
    * @param messages We append stuff here if something went wrong
    */
    public InstsMem(RawBible raw, String leafname, boolean create, StringBuffer messages)
    {
        super(raw, leafname, create, messages);
    }

    /**
    * Start all over again and clear the decks for more data.
    */
    public void init()
    {
        array = new int[Books.versesInBible()][];
    }

    /**
    * Load the Resource from a stream. This has been renamed from the
    * default load() to ensure that the custom versions are called.
    * @param in The stream to read from
    */
    protected void defaultLoad(InputStream in) throws IOException, ClassNotFoundException
    {
        ObjectInputStream obj_in = new ObjectInputStream(in);

        array = (int[][]) obj_in.readObject();
        obj_in.close();
    }

    /**
    * Ensure that all changes to the index of words are written to a
    * stream. This has been renamed from the default save() to ensure
    * that the custom versions are called.
    * @param out The stream to write to
    */
    public void defaultSave(OutputStream out) throws IOException
    {
        ObjectOutputStream obj_out = new ObjectOutputStream(out);

        obj_out.writeObject(array);
        obj_out.close();
    }

    /**
    * Retrieve an ordered list of the words in a Verse
    * @param verse The Verse to retrieve words for
    * @return An array of word indexes
    */
    public int[] getIndexes(Verse verse)
    {
        return array[verse.getOrdinal()-1];
    }

    /**
    * Retrieve an ordered list of the words in a Verse
    * @param verse The Verse to retrieve words for
    * @return An array of word indexes
    */
    public int[] getIndexes(int ordinal)
    {
        return array[ordinal-1];
    }

    /**
    * Set a list of word indexes as the test to a Verse
    * @param verse The Verse to set the words for
    * @param indexes The array of word indexes
    */
    public void setIndexes(int[] indexes, Verse verse)
    {
        array[verse.getOrdinal()-1] = indexes;
    }

    /** The store of data */
    protected int[][] array;
}

