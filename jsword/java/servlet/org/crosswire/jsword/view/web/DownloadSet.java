
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
import java.util.List;

import org.crosswire.common.util.Logger;

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
public class DownloadSet implements Comparable
{
    public static final String BIN_ZIP = "-bin.zip";
    public static final String BIN_TGZ = "-bin.tar.gz";
    public static final String SRC_ZIP = "-src.zip";
    public static final String SRC_TGZ = "-src.tar.gz";
    public static final String DOC_ZIP = "-doc.zip";
    public static final String DOC_TGZ = "-doc.tar.gz";

    /**
     * Get an Iterator over all the Downloads in the specified Directory
     */
    public static DownloadSet[] getDownloadSets(String localprefix, String webprefix, boolean datesort) throws IOException
    {
        File dir = new File(localprefix);
        if (!dir.isDirectory())
        {
            throw new IOException(localprefix+" is not a directory");
        }

        log.debug("dig "+localprefix);
        File[] files = dir.listFiles(new FileFilter()
        {
            public boolean accept(File file)
            {
                String name = file.getName();
                log.debug("found "+name);
                return file.canRead()
                    && name.startsWith(TEST_PREFIX)
                    && name.endsWith(TEST_SUFFIX);
            }
        });

        List reply = new ArrayList();
        for (int i = 0; i < files.length; i++)
        {
            String name = files[i].getName();
            log.debug("adding "+name);
            String setname = name.substring(TEST_PREFIX.length(), name.length() - TEST_SUFFIX.length());
            reply.add(new DownloadSet(localprefix, webprefix, setname, datesort));
        }

        return (DownloadSet[]) reply.toArray(new DownloadSet[reply.size()]);
    }

    /**
     * Create a set of downloads
     */
    private DownloadSet(String localprefix, String webprefix, String setname, boolean datesort)
    {
        this.localprefix = localprefix;
        this.webprefix = webprefix;
        this.setname = setname;
        this.datesort = datesort;

        log.debug("ctor "+webprefix);
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        if (!(obj instanceof DownloadSet))
        {
            log.error("Asked to compare to non DownloadSet");
            return 0;
        }
        
        DownloadSet that = (DownloadSet) obj;
        if (datesort)
        {
            try
            {
                Date thisdate = diskdf.parse(this.setname);
                Date thatdate = diskdf.parse(that.setname);
                return thatdate.compareTo(thisdate);
            }
            catch (ParseException ex)
            {
                log.error("Failed to parse dates", ex);
                return 0;
            }
        }
        else
        {
            return that.setname.compareTo(this.setname);
        }
    }

    /**
     * When was the set of files created (using the file name string)
     */
    public String getDateString() throws ParseException
    {
        Date date = diskdf.parse(setname);
        return userdf.format(date);
    }

    /**
     * What is the version number (using the file name string)
     */
    public String getVersionString()
    {
        return "Version "+setname;
    }

    /**
     * Get a short HTML string for the download link.
     * Purists would complain that this is UI specific code embedded where it
     * ought not be. So such I would argue - rewrite this so that it still
     * works (not easy given the JSP/XML use) and so that it is just as simple
     * and so that it can actually be reused in a more general UI.
     */
    public String getLinkString(String extension)
    {
        File file = new File(localprefix, TEST_PREFIX + setname + extension);
        String size = nf.format(((float) file.length()) / (1024F * 1024F));
        String reply = "<a href='"+ webprefix + "/" + TEST_PREFIX + setname + extension + "'>"+size+" Mb</a>";

        log.debug("link="+reply);

        return reply;
    }

    private boolean datesort;
    private String webprefix;
    private String localprefix;
    private String setname;
    private Date date;

    private static final String TEST_PREFIX = "jsword-";
    private static final String TEST_SUFFIX = BIN_ZIP;

    private static String[] EXTENSIONS = 
    {
        BIN_ZIP,
        BIN_TGZ,
        SRC_ZIP,
        SRC_TGZ,
        DOC_ZIP,
        DOC_TGZ,
    };

    private static final NumberFormat nf = NumberFormat.getNumberInstance();
    private static final DateFormat diskdf = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat userdf = new SimpleDateFormat("dd MMM yyyy");
    static
    {
        nf.setMaximumFractionDigits(2);
    }

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(DownloadSet.class);
}
