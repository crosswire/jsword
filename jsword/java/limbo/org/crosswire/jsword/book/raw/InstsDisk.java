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
 * ID: $Id$
 */
package org.crosswire.jsword.book.raw;

import java.io.IOException;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Verse;

/**
 * InstsDisk is a Base implementation of the Insts interface using the in
 * on disk model (Disk).
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class InstsDisk extends Disk implements Insts
{
    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     */
    public InstsDisk(RawBook raw, String leafname, boolean create) throws IOException
    {
        ctor(raw, leafname, create);
    }

    /**
     * Create a WordResource from a File that contains the dictionary.
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     * @param messages We append stuff here if something went wrong
     */
    public InstsDisk(RawBook raw, String leafname, boolean create, StringBuffer messages)
    {
        try
        {
            ctor(raw, leafname, create);
        }
        catch (Exception ex)
        {
            messages.append(""+ex); //$NON-NLS-1$
        }
    }

    /**
     * This really should be a constructor, however the StringBuffer ctor
     * wants to trap and muffle exceptions.
     * |I can't do this:
     * <code>try { this(...) } ...</code>
     * @param newraw Reference to the RawBook that is using us
     * @param newleafname The leaf name to read/write
     * @param newcreate Should we start all over again
     */
    private void ctor(RawBook newraw, String newleafname, boolean newcreate) throws IOException
    {
        this.raw = newraw;
        this.leafname = newleafname;
        this.create = newcreate;

        index = new long[BibleInfo.versesInBible()];

        if (newcreate)
        {
            save();
        }
        else
        {
            load();
        }
    }

    /**
     * Load the Resource from a named file
     */
    public abstract void load() throws IOException;

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Insts#save()
     */
    public void save()
    {
        assert false : "You must use a WordInstsMem to write data"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Insts#getIndexes(org.crosswire.jsword.passage.Verse)
     */
    public int[] getIndexes(Verse verse)
    {
        return getIndexes(verse.getOrdinal());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Insts#setIndexes(int[], org.crosswire.jsword.passage.Verse)
     */
    public void setIndexes(int[] indexes, Verse verse)
    {
        assert false : "You must use a PuncInstsMem to write data"; //$NON-NLS-1$
    }

    /**
     * The file offsets
     */
    protected long[] index;

    /**
     * Are we allowed to create new indexes
     */
    protected boolean create;

    /**
     * The leafname of the file read
     */
    protected String leafname;

    /**
     * The RawBook co-ordinated the various classes that cache the files
     */
    protected RawBook raw;
}
