package org.crosswire.jsword.book.install.sword;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.sword.SwordConfig;
import org.crosswire.jsword.util.Project;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

/**
 * An implementation of Installer for reading data from Sword FTP sites.
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
public class SwordInstaller implements Installer
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#init(java.lang.String)
     */
    public void init(String newName, String newUrl)
    {
        this.name = newName;
        this.url = newUrl;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getURL()
     */
    public String getURL()
    {
        return url;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getIndex()
     */
    public List getIndex()
    {
        try
        {
            loadCachedIndex();
            return new ArrayList(entries.keySet());
        }
        catch (InstallException ex)
        {
            log.error("Failed to reload cached index file", ex);
            return new ArrayList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#install(java.lang.String)
     */
    public void install(String entry)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#reloadIndex()
     */
    public List reloadIndex() throws InstallException
    {
        cacheRemoteFile();
        loadCachedIndex();
        return new ArrayList(entries.keySet());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getEntry(java.lang.String)
     */
    public Properties getEntry(String entry)
    {
        return (Properties) entries.get(entry);
    }

    /**
     * Load the index file from FTP and parse it
     */
    private void cacheRemoteFile() throws InstallException
    {
        String[] parts = url.split("/", 4);

        // part[0] is the 'protocol' which we don't care about
        // part[1] is the blank between the first 2 slashes
        String site = parts[2];
        String dir = "/" + parts[3];

        FTPClient ftp = new FTPClient();

        try
        {
            log.info("Connecting to site=" + site + " dir=" + dir);

            // First connect
            ftp.connect(site);

            log.info(ftp.getReplyString());
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                String text = ftp.getReplyString();
                disconnect(ftp);
                throw new InstallException(Msg.CONNECT_REFUSED, new Object[] { site, new Integer(reply), text });
            }

            // Authenticate
            String user = "anonymous";
            String password = "anon@anon.com";

            ftp.login(user, password);

            log.info(ftp.getReplyString());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                String text = ftp.getReplyString();
                disconnect(ftp);
                throw new InstallException(Msg.AUTH_REFUSED, new Object[] { user, new Integer(reply), text });
            }

            // Change directory
            ftp.changeWorkingDirectory(dir);

            log.info(ftp.getReplyString());
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                String text = ftp.getReplyString();
                disconnect(ftp);
                throw new InstallException(Msg.CWD_REFUSED, new Object[] { dir, new Integer(reply), text });
            }

            // Download the index file
            URL scratchfile = getCachedIndexFile(name);
            OutputStream out = NetUtil.getOutputStream(scratchfile);

            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.retrieveFile(FILE_LIST_GZ, out);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                String text = ftp.getReplyString();
                disconnect(ftp);
                throw new InstallException(Msg.DOWNLOAD_REFUSED, new Object[] { FILE_LIST_GZ, new Integer(reply), text });
            }
            out.close();
        }
        catch (IOException ex)
        {
            disconnect(ftp);
            throw new InstallException(Msg.UNKNOWN_ERROR, ex);
        }
    }

    /**
     * Load the cached index file into memory
     */
    private void loadCachedIndex() throws InstallException
    {
        try
        {
            entries.clear();

            URL cache = getCachedIndexFile(name);
            if (!NetUtil.isFile(cache))
            {
                return;
            }

            InputStream in = cache.openStream();
            GZIPInputStream gin = new GZIPInputStream(in);
            TarInputStream tin = new TarInputStream(gin);

            while (true)
            {
                TarEntry entry = tin.getNextEntry();
                if (entry == null)
                {
                    break;
                }
            
                if (!entry.isDirectory())
                {
                    int size = (int) entry.getSize();
                    byte[] buffer = new byte[size];
                    tin.read(buffer);
            
                    Reader rin = new InputStreamReader(new ByteArrayInputStream(buffer));
                    SwordConfig config = new SwordConfig(rin);
                    Properties prop = config.getProperties();
                    String desc = config.getDescription();
            
                    entries.put(desc, prop);
                }
            }
            
            tin.close();
            gin.close();
            in.close();
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.CACHE_ERROR, ex);
        }
    }

    /**
     * The URL for the cached index file for this installer
     */
    private static URL getCachedIndexFile(String name) throws IOException, MalformedURLException
    {
        URL scratchdir = Project.instance().getTempScratchSpace("download-" + name);
        return NetUtil.lengthenURL(scratchdir, FILE_LIST_GZ);
    }

    /**
     * Silently close an ftp connection, ignoring any exceptions
     */
    private void disconnect(FTPClient ftp)
    {
        if (ftp.isConnected())
        {
            try
            {
                ftp.disconnect();
            }
            catch (IOException ex2)
            {
                log.error("disconnect error", ex2);
            }
        }
    }

    /**
     * The source 'url', where the protocol is known to the InstallManager
     * @see org.crosswire.jsword.book.install.InstallManager
     */
    private String url;

    /**
     * The configured name as read by the InstallManager
     * @see org.crosswire.jsword.book.install.InstallManager
     */
    private String name;

    /**
     * A map of the entries in this download area
     */
    private Map entries = new HashMap();

    /**
     * The sword index file
     */
    private static final String FILE_LIST_GZ = "mods.d.tar.gz";

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordInstaller.class);
}
