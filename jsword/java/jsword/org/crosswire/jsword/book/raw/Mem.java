
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.crosswire.common.util.NetUtil;

/**
 * Mem is the root of all the data sources that load their data fully into
 * memory at init time. This is fairly fast but very memory hungry.
 * <p>There is code here to implememt compressed data files, however this
 * makes load time very very slow, instead of just slow, so it is all
 * commented out.</p>
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
public abstract class Mem
{
    /**
    * Create a WordResource from a File that contains the dictionary.
    * @param raw Reference to the RawBible that is using us
    * @param leafname The leaf name to read/write
    * @param create Should we start all over again
    */
    public Mem(RawBible raw, String leafname, boolean create) throws Exception
    {
        ctor(raw, leafname, create);
    }

    /**
    * Create a WordResource from a File that contains the dictionary.
    * @param raw Reference to the RawBible that is using us
    * @param leafname The leaf name to read/write
    * @param create Should we start all over again
    * @param messages We append stuff here if something went wrong
    */
    public Mem(RawBible raw, String leafname, boolean create, StringBuffer messages)
    {
        try
        {
            ctor(raw, leafname, create);
        }
        catch (Exception ex)
        {
            messages.append(""+ex);
        }
    }

    /**
    * This really should be a constructor, however the StringBuffer ctor
    * wants to trap and muffle exceptions.
    * I can't do this:
    * <code>try { this(...) } ...</code>
    * @param raw Reference to the RawBible that is using us
    * @param leafname The leaf name to read/write
    * @param create Should we start all over again
    */
    private void ctor(RawBible raw, String leafname, boolean create) throws Exception
    {
        this.raw = raw;
        this.leafname = leafname;
        this.create = create;

        init();
        if (create)
        {
            save();
        }
        else
        {
            load();
        }
    }

    /**
    * Start all over again and clear the decks for more data.
    */
    public abstract void init();

    /**
    * Load the Resource from a stream
    * @param in The stream to read from
    */
    public abstract void load(InputStream in) throws IOException, ClassNotFoundException;

    /**
    * Load the Resource from a named file
    */
    public void load() throws IOException, ClassNotFoundException
    {
        URL url = NetUtil.lengthenURL(raw.getLocalURLBibleMetaData().getURL(), leafname);

        // For the pkzip version
        //String filename = raw.getDir()+leafname+".zip";

        // For the gzip version
        //String filename = raw.getDir()+leafname+".gz";

        InputStream in = url.openStream();

        // For the pkzip version
        //ZipInputStream in = new ZipInputStream(new FileInputStream(filename));
        //ZipEntry entry = in.getNextEntry();
        //if (entry == null) throw new IOException("Empty ZIP file");

        // For the gzip version
        //GZIPInputStream in = new GZIPInputStream(new FileInputStream(filename));

        load(in);

        in.close();
    }

    /**
    * Ensure that all changes to the index of words are written to a
    * stream
    * @param out The stream to write to
    */
    public abstract void save(OutputStream out) throws IOException;

    /**
    * Ensure that all changes to the index of words are written to disk
    */
    public void save() throws IOException
    {
        URL url = NetUtil.lengthenURL(raw.getLocalURLBibleMetaData().getURL(), leafname);

        // For the pkzip version
        //String filename = raw.getDir()+leafname+".zip";

        // For the gzip version
        //String filename = raw.getDir()+leafname+".gz";

        OutputStream out = NetUtil.getOutputStream(url);

        // For the pkzip version
        //ZipOutputStream out = new ZipOutputStream(new FileOutputStream(filename));
        //out.putNextEntry(new ZipEntry(leafname));

        // For the gzip version
        //GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(filename));

        save(out);

        // For both zip versions
        //out.finish();

        // For the pkzip version
        //out.closeEntry();

        out.close();
    }

    /** Are we allowed to create new indexes */
    protected boolean create;

    /** The leafname of the file read */
    protected String leafname;

    /** The RawBible co-ordinated the various classes that cache the files */
    protected RawBible raw;
}
