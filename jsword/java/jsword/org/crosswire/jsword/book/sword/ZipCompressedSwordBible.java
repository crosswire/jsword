
package org.crosswire.jsword.book.sword;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Verse;

/**
 * A stub for the Compressed sword Bible backend.
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author Joe Walker [joe at eireneh dot com]
 * @author The Sword project (don't know who - no credits in original files (canon.h))
 * @version $Id$
 */
public class ZipCompressedSwordBible extends SwordBible
{
    /**
     * Simple ctor
     */
    public ZipCompressedSwordBible(SwordBibleMetaData sbmd, SwordConfig config) throws BookException
    {
        super(sbmd, config);
        log.warn("No support for zip compression in "+config.getName()+" desire="+(++desire_zip));

        backend = new RawBackend();

        //backend.init(config);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.SwordBible#getText(org.crosswire.jsword.passage.Verse)
     */
    public String getText(Verse verse) throws BookException
    {
        if (true) return "test backend";
        try
        {
            byte[] buffer = backend.getRawText(verse);
            ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
            ZipInputStream zin = new ZipInputStream(bin);
            InputStreamReader isin = new InputStreamReader(zin);
            BufferedReader in = new BufferedReader(isin);
            StringBuffer out = new StringBuffer();
            
            while (true)
            {
                String line = in.readLine();
                if (line == null)
                    break;
            
                out.append(line);
            }
            
            return out.toString();
        }
        catch (IOException ex)
        {
            throw new BookException("sword_uncompress", ex);
        }
    }

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(ZipCompressedSwordBible.class);

    /**
     * So we know wha the demand for this is
     */
    private static int desire_zip = 0;

    /**
     * Disk reader
     */
    private RawBackend backend;
}
