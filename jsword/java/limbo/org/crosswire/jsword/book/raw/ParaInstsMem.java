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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;

/**
 * A ParaInstsMem provides access to the list of paragraphs that
 * punctuate the Bible.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
        super(raw, RawConstants.FILE_PARA_INST, create);
    }

    /**
     * Basic constructor
     * @param raw Reference to the RawBook that is using us
     * @param create Should we start all over again
     */
    public ParaInstsMem(RawBook raw, boolean create, StringBuffer messages)
    {
        super(raw, RawConstants.FILE_PARA_INST, create, messages);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#init()
     */
    public void init()
    {
        key = raw.createEmptyKeyList();
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

        assert ssig.equals(RawConstants.SIG_PARA_INST);

        try
        {
            key = (Passage) oin.readObject();
        }
        catch (ClassNotFoundException ex)
        {
            throw new IOException(ex.getMessage());
        }

        oin.close();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.raw.Mem#save(java.io.OutputStream)
     */
    public void save(OutputStream out) throws IOException
    {
        ObjectOutputStream oout = new ObjectOutputStream(out);

        oout.writeBytes(RawConstants.SIG_PARA_INST);
        oout.writeObject(key);

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
            key.addAll(verse);
        }
        else
        {
            key.removeAll(verse);
        }
    }

    /**
     * Set the new paragraph status for a verse.
     * If the load failed then we treat each verse as a new paragraph
     * @param verse The Verse to get data on
     */
    public boolean getPara(Verse verse)
    {
        return key.contains(verse);
    }

    /**
     * The storage of the Para markers
     */
    private Key key;
}