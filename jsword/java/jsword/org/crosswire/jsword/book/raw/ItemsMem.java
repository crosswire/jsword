
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * ItemsMem is a Base implementation of the Items interface using the in
 * memory model (Mem).
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
public abstract class ItemsMem extends Mem implements Items
{
    /**
     * Create a WordResource from a File that contains the dictionary.
     * @param raw Reference to the RawBible that is using us
     * @param create Should we start all over again
     */
    public ItemsMem(RawBible raw, String leafname, boolean create) throws IOException
    {
        super(raw, leafname, create);
    }

    /**
     * Create a WordResource from a File that contains the dictionary.
     * @param raw Reference to the RawBible that is using us
     * @param create Should we start all over again
     * @param messages We append stuff here if something went wrong
     */
    public ItemsMem(RawBible raw, String leafname, boolean create, StringBuffer messages)
    {
        super(raw, leafname, create, messages);
    }

    /**
     * How many items are there in this index?
     * @return The number of items that we must remember
     */
    public abstract int getMaxItems();

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#init()
     */
    public void init()
    {
        hash = new HashMap(getMaxItems());
        array = new String[getMaxItems()];
    }

    /**
     * Load the Resource from a stream. This has been renamed from the
     * default load() to ensure that the custom versions are called.
     * @param in The stream to read from
     */
    protected void defaultLoad(InputStream in) throws IOException, ClassNotFoundException
    {
        ObjectInputStream obj_in = new ObjectInputStream(in);

        hash = (Hashtable) obj_in.readObject();
        array = (String[]) obj_in.readObject();
        count = obj_in.readInt();
        obj_in.close();
    }

    /**
     * Ensure that all changes to the index of words are written to a
     * stream. This has been renamed from the default save() to ensure
     * that the custom versions are called.
     * @param out The stream to write to
     */
    protected void defaultSave(OutputStream out) throws IOException
    {
        ObjectOutputStream obj_out = new ObjectOutputStream(out);

        obj_out.writeObject(hash);
        obj_out.writeObject(array);
        obj_out.writeInt(count);
        obj_out.close();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Items#iterator()
     */
    public Iterator iterator()
    {
        return hash.keySet().iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Items#getItem(int)
     */
    public String getItem(int index) throws NoSuchResourceException
    {
        try
        {
            return array[index];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            return "#"+index+"#";
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Items#getIndex(java.lang.String)
     */
    public int getIndex(String data)
    {
        Object obj = hash.get(data);
        if (obj != null)
            return ((Integer) obj).intValue();

        if (create)
        {
            // So we have to add the word in
            array[count] = data;
            hash.put(data, new Integer(count));

            return count++;
        }
        else
        {
            return -1;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Items#getIndex(java.lang.String[])
     */
    public int[] getIndex(String[] data)
    {
        int len = data.length;
        int[] indexes = new int[len];

        for (int i=0; i<len; i++)
        {
            indexes[i] = getIndex(data[i]);
        }

        return indexes;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Items#size()
     */
    public int size()
    {
        return hash.size();
    }

    /**
     * Map of word to their indexes
     */
    protected Map hash;

    /**
     * Converting indexes to Words - this is about the number of words in the Bible
     */
    protected String[] array;

    /**
     * The number of items so far
     */
    protected int count = 0;
}
