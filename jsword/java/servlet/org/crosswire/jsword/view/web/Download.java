
package org.crosswire.jsword.view.web;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A helper for the download.jsp page.
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
public class Download
{
    /**
     * Set needed variables
     */
    public static void init(String directory, String prefix) throws IOException
    {
        dir = new File(directory);
        if (!dir.isDirectory())
        {
            throw new IOException(directory+" is not a directory");
        }
        
        Download.prefix = prefix;
    }
    
    /**
     * Get an Iterator over all the Downloads in the specified Directory
     */
    public static Iterator getDownloads()
    {
        File[] files = dir.listFiles(new FileFilter()
        {
            public boolean accept(File file)
            {
                return file.canWrite() && file.getName().endsWith(EXTENSIONS[0]);
            }
        });

        List reply = new ArrayList();
        for (int i = 0; i < files.length; i++)
        {
            try
            {
                reply.add(new Download(files[i]));
            }
            catch (ParseException ex)
            {
                log.error("Ignoring file: "+files[i], ex);
            }
        }

        return reply.iterator();
    }

    /**
     * Use init() and then getDownloads() rather than the ctor.
     */
    private Download(File file) throws ParseException
    {
        String whole = file.getName();

        int dash = whole.indexOf('-');
        if (dash == -1)
        {
            throw new ParseException("Missing -", 0);
        }

        Date date = dfin.parse(whole.substring(dash));
        datestr = dfout.format(date);

        this.base = whole.substring(0, whole.length() - EXTENSIONS[0].length());
    }
        
    /**
     * The date as a String
     */
    public String getDateString()
    {
        return datestr;
    }
        
    /**
     * The URL as a String
     */
    public String getURLString(String extension)
    {
        return prefix + base + extension;
    }
        
    /**
     * The file size as a string
     */
    public String getSizeString(String extension)
    {
        File file = new File(dir, base + extension);
        float size = (float) file.length() / (1024F * 1024F);
        return nf.format(size) + "Mb";
    }

    private String base;
    private String datestr;

    protected static String prefix;

    protected static File dir;

    public static final String BIN_ZIP = "-bin.zip"; 
    public static final String BIN_TGZ = "-bin.tgz"; 
    public static final String SRC_ZIP = "-src.zip"; 
    public static final String SRC_TGZ = "-src.tgz"; 
    public static final String DOC_ZIP = "-doc.zip"; 
    public static final String DOC_TGZ = "-doc.tgz"; 

    public static String[] EXTENSIONS = 
    {
        BIN_ZIP,
        BIN_TGZ,
        SRC_ZIP,
        SRC_TGZ,
        DOC_ZIP,
        DOC_TGZ,
    };

    protected static final NumberFormat nf = NumberFormat.getNumberInstance();
    protected static final DateFormat dfin = new SimpleDateFormat("yyyyMMdd");
    protected static final DateFormat dfout = new SimpleDateFormat("dd MMM yyyy");

    static
    {
        nf.setMaximumFractionDigits(1);
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger(Download.class);
}
