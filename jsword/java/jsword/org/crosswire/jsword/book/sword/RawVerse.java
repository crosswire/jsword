
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

/**
 * Code for class 'RawVerse'- a module that reads raw text files.
 * ot and nt using indexs ??.bks ??.cps ??.vss and provides lookup and parsing
 * functions based on class VerseKey
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
public class RawVerse
{
    /** constant for the introduction */
    public static final int TESTAMENT_INTRO = 0;

    /** constant for the old testament */
    public static final int TESTAMENT_OLD = 1;

    /** constant for the new testament */
    public static final int TESTAMENT_NEW = 2;

    /**
     * RawVerse Constructor - Initializes data for instance of RawVerse
     * @param path - path of the directory where data and index files are located.
     *		be sure to include the trailing separator (e.g. '/' or '\')
     *		(e.g. 'modules/texts/rawtext/webster/')
     */
    public RawVerse(String path) throws FileNotFoundException
    {
        idx_raf[TESTAMENT_OLD] = new RandomAccessFile(path + "ot.vss", "r");
        idx_raf[TESTAMENT_NEW] = new RandomAccessFile(path + "nt.vss", "r");
        txt_raf[TESTAMENT_OLD] = new RandomAccessFile(path + "ot", "r");
        txt_raf[TESTAMENT_NEW] = new RandomAccessFile(path + "nt", "r");

        // The original had a dtor that did the equiv of .close()ing the above
        // I'm not sure that there is a delete type ability in Book.java and
        // the finalizer for RandomAccessFile will do it anyway so for the
        // moment I'm going to ignore this.

        // The original also stored the path, but I don't think it ever used it

        // The original also kept an instance count, which went unused (and I
        // noticed in a few other places so it is either c&p or a pattern?
        // Either way the assumption that there is only one of a static is not
        // safe in many java environments (servlets, ejbs at least) so I've
        // deleted it
    }

    /**
     * Finds the offset of the key verse from the indexes
     * @param testament testament to find (0 - Bible/module introduction)
     * @param idxoff offset into .vss
     * @param start address to store the starting offset
     * @param size address to store the size of the entry
     */
    public Location findOffset(int testament, long idxoff) throws IOException
    {
        Location loc = new Location();

        // There was a bodge here to move testament around if someone wanted
        // to read the intro? We just have the set of static finals above
        //  if (testament == 0)
        //      testament = idx_raf[1] == null ? 1 : 2;

        // There was a test here to check ensure that is idx_raf[testament-1]
        // was null then we returned an default Location (of 0,0). However
        // This seems like papering over any errors so I have left it out for
        // the time being

        // I've now totally re-written this because we did have byte-sex
        // problems. The file is little endian, and we read big endianly.

        // read the next 6 byes.
        idx_raf[testament].seek(idxoff*6);
        byte[] read = new byte[6];
        idx_raf[testament].readFully(read);
        int[] temp = new int[6];

        for (int i=0; i<temp.length; i++)
        {
            temp[i] = read[i] >= 0 ? read[i] : 256 + read[i];
            log.debug("temp["+i+"]="+temp[i]);
        }

        loc.start = (temp[3] << 24) | (temp[2] << 16) | (temp[1] << 8) | temp[0];
        loc.size = (temp[5] << 8) | temp[4];

        // the original lseek used SEEK_SET. This is the only option in Java
        // The *6 is because we use 4 bytes for the offset, and 2 for the length
        // There used to be some code at the start of the method like:
        //   idxoff *= 6;
        // But itn't good to alter parameters and here is the only place that
        // it is used.

        // There was some BIGENDIAN swapping stuff here. To be honest I
        // can't be bothered to think about whether or not this is needed
        // right now.
        // *start = lelong(*start);
        // *size  = leshort(*size);

        // There was also some code here to patch over any errors if you
        // could only read one of the 2 bytes from above. I'm not sure that
        // that is a good idea, so I've left it out.

        return loc;
    }

    /**
     * Gets text at a given offset.
     * @param testament testament file to search in (0 - Old; 1 - New)
     * @param loc Where to read from
     */
    public String getText(int testament, Location loc) throws IOException
    {
        // The original had the size param as an unsigned short.
        // It also used SEEK_SET as above (default in Java)

        byte[] buffer = new byte[loc.size];

        txt_raf[testament].seek(loc.start);
        txt_raf[testament].read(buffer);

        // We should probably think about encodings here?
        return new String(buffer);
    }

    /**
     * Prepares the text before returning it to external objects
     * @param buf buffer where text is stored and where to store the prep'd text
     */
    protected String prepText(String text)
    {
        StringBuffer buf = new StringBuffer(text);

        boolean space = false;
        boolean cr = false;
        boolean realdata = false;
        char nlcnt = 0;

        int to = 0;
        for (int from=0; from<buf.length(); from++)
        {
            switch (buf.charAt(from))
            {
            case 10:
                if (!realdata)
                    continue;

                space = (cr) ? false : true;
                cr = false;
                nlcnt++;
                if (nlcnt > 1)
                {
                    // buf.setCharAt(to++, nl);
                    buf.setCharAt(to++, '\n');
                    // nlcnt = 0;
                }
                continue;

            case 13:
                if (!realdata)
                    continue;

                buf.setCharAt(to++, '\n');
                space = false;
                cr = true;
                continue;
            }

            realdata = true;
            nlcnt = 0;

            if (space)
            {
                space = false;
                if (buf.charAt(from) != ' ')
                {
                    buf.setCharAt(to++, ' ');
                    from--;
                    continue;
                }
            }
            buf.setCharAt(to++, buf.charAt(from));
        }

        // This next line just ensured that we were null terminated.
        //   buf.setCharAt(to, '\0');

        // There followed a lot of code that stomed \o to the end of the
        // string if there was whitespace there. trim() is easier.

        return buf.toString().trim();
    }

    /**
     * Sets text for current offset
     * @param testament testament to find (0 - Bible/module introduction)
     * @param idxoff offset into .vss
     * @param buf buffer to store
     */
    protected void setText(int testament, long idxoff, String buf) throws IOException
    {
        // As in getText() we don't alter the formal parameter
        //   idxoff *= 6;

        // As in getText() There was some messing around with testament
        //  if (testament == 0)
        //      testament = idx_raf[1] == null ? 1 : 2;

        // outsize started off being unsigned
        // and it looks like "unsigned short size;" is not used
        short outsize = (short) buf.length();

        // There was some more BIGENDIAN nonsense here. Again ignoring the
        // MACOSX bits it looked like:
        //   start = lelong(start);
        //   outsize  = leshort(size);
        // I've also moved things around very slightly, the endian bits came
        // just before the writeShort();

        idx_raf[testament].seek(idxoff*6);
        long start = idx_raf[testament].readLong();
        idx_raf[testament].writeShort(outsize);

        // There is some encoding stuff to be thought about here
        byte[] data = buf.getBytes();

        txt_raf[testament].seek(start);
        txt_raf[testament].write(data);
    }

    /**
     * Creates new module files
     * @param path Directory to store module files
     */
    public static void createModule(String path) throws IOException
    {
        truncate(path + "ot.vss");
        truncate(path + "nt.vss");
        truncate(path + "ot");
        truncate(path + "nt");

        // I'm not at all sure what these did. I'd guess they wrote data to
        // the files we just created? But how they'd neatly (or otherwise) go
        // about this is beyond me right now.
        //   RawVerse rv(path);
        //   VerseKey mykey("Rev 22:21");
    }

    /**
     * Create an empty file, deleting what was there
     */
    private static void truncate(String filename) throws IOException
    {
        // The original code did something like this. I recon this basically
        // deleted and recreated (empty) the named file.
        //   unlink(buf);
        //   fd = FileMgr::systemFileMgr.open(buf, O_CREAT|O_WRONLY|O_BINARY, S_IREAD|S_IWRITE);
        //   FileMgr::systemFileMgr.close(fd);

        File file = new File(filename);

        file.delete();
        file.createNewFile();
    }

    /**
     * There has to be a better method than this.
     * findoffset() returned a start and and offset, and multiple return values
     * are not possible in Java.
     * It seems to me that returning start and size from a public i/f represents
     * showing our callers more than we should and I expect that the solution
     * lies in a thorough sorting out if the interface, but I want to keep
     * the methods unchanged as reasonable right now.
     */
    public class Location
    {
        /** Where does the data start */
        public long start = 0;

        /** The data length. Is short long enough? the original was unsigned short */
        public int size = 0;

        /**
         * Debug only
         */
        public String toString()
        {
            return "start="+start+", size="+size;
        }
    }

    /**
     * A test program
     */
    public static void main(String[] args)
    {
        try
        {
            // To start with I'm going to hard code the path
            String path = "/usr/apps/sword/modules/texts/rawtext/kjv/";

            RawVerse verse = new RawVerse(path);
            Location loc = verse.findOffset(RawVerse.TESTAMENT_NEW, 6);
            String pre = verse.getText(RawVerse.TESTAMENT_NEW, loc);

            log.debug("loc="+loc);
            log.debug("pre="+pre);
            log.debug("post="+verse.prepText(pre));
        }
        catch (Exception ex)
        {
            log.info("Failure", ex);
        }
    }

    /** The array of index files */
    private RandomAccessFile[] idx_raf = new RandomAccessFile[3];

    /** The array of data files */
    private RandomAccessFile[] txt_raf = new RandomAccessFile[3];

    /** The log stream */
    protected static Logger log = Logger.getLogger(RawVerse.class);
}

