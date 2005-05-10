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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Verse;

/**
 * InstsMem is a Base implementation of the Insts interface using the in
 * memory model (Mem).
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class InstsMem extends Mem implements Insts
{
    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     */
    public InstsMem(RawBook raw, String leafname, boolean create) throws IOException
    {
        super(raw, leafname, create);
    }

    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     * @param messages We append stuff here if something went wrong
     */
    public InstsMem(RawBook raw, String leafname, boolean create, StringBuffer messages)
    {
        super(raw, leafname, create, messages);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#init()
     */
    public void init()
    {
        array = new int[BibleInfo.versesInBible()][];
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Insts#getIndexes(org.crosswire.jsword.passage.Verse)
     */
    public int[] getIndexes(Verse verse)
    {
        return array[verse.getOrdinal()-1];
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Insts#getIndexes(int)
     */
    public int[] getIndexes(int ordinal)
    {
        return array[ordinal-1];
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Insts#setIndexes(int[], org.crosswire.jsword.passage.Verse)
     */
    public void setIndexes(int[] indexes, Verse verse)
    {
        array[verse.getOrdinal()-1] = indexes;
    }

    /**
     * The store of data
     */
    protected int[][] array;
}
