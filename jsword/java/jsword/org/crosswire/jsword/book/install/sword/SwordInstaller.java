package org.crosswire.jsword.book.install.sword;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.basic.AbstractBookList;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.sword.ModuleType;
import org.crosswire.jsword.book.sword.SwordBookDriver;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.book.sword.SwordConstants;
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
public class SwordInstaller extends AbstractBookList implements Installer, Comparable
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getIndex()
     */
    public List getBookMetaDatas()
    {
        try
        {
            if (!loaded)
            {
                loadCachedIndex();
            }

            List mutable = new ArrayList();
            mutable.addAll(entries.values());
            return Collections.unmodifiableList(mutable);
        }
        catch (InstallException ex)
        {
            log.error("Failed to reload cached index file", ex); //$NON-NLS-1$
            return new ArrayList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#install(java.lang.String)
     */
    public void install(BookMetaData bmd)
    {
        if (!(bmd instanceof SwordBookMetaData))
        {
            assert false;
            return;
        }

        // Is the book already installed? Then nothing to do.
        if (Books.installed().getBookMetaData(bmd.getName()) != null)
        {
            Reporter.informUser(this, "Module already installed: "+bmd.getName());
            return;
        }

        final SwordBookMetaData sbmd = (SwordBookMetaData) bmd;

        Reporter.informUser(this, "Installing module: "+sbmd.getName());

        // So now we know what we want to install - all we need to do
        // is installer.install(name) however we are doing it in the
        // background so we create a job for it.
        final Thread worker = new Thread("DisplayPreLoader")
        {
            public void run()
            {
                URL predicturl = Project.instance().getWritablePropertiesURL("sword-install"); //$NON-NLS-1$
                Job job = JobManager.createJob("Install Module: "+sbmd.getName(), predicturl, this, true);

                try
                {
                    job.setProgress("Init");

                    ModuleType type = sbmd.getModuleType();
                    String modpath = type.getInstallDirectory();
                    String destname = modpath + File.separator + sbmd.getInternalName();

                    File dldir = SwordBookDriver.getDownloadDir();
                    File moddir = new File(dldir, SwordConstants.DIR_DATA);
                    File fulldir = new File(moddir, destname);
                    fulldir.mkdirs();
                    URL desturl = new URL(NetUtil.PROTOCOL_FILE, null, fulldir.getAbsolutePath());

                    downloadAll(job, host, USERNAME, PASSWORD, directory + File.separator + SwordConstants.DIR_DATA + File.separator + destname, desturl);

                    job.setProgress("Copying config file");
                    File confdir = new File(dldir, SwordConstants.DIR_CONF);
                    confdir.mkdirs();
                    File conf = new File(confdir, sbmd.getInternalName()+".conf");
                    URL configurl = new URL(NetUtil.PROTOCOL_FILE, null, conf.getAbsolutePath());
                    sbmd.save(configurl);

                    SwordBookDriver.registerNewBook(sbmd, dldir);
                    
                    // inform the user that we are done
                    Reporter.informUser(this, "Finished installing module: "+sbmd.getName());
                }
                catch (Exception ex)
                {
                    Reporter.informUser(this, ex);
                    job.ignoreTimings();
                }
                finally
                {
                    job.done();
                }
            }
        };

        // this actually starts the thread off
        worker.setPriority(Thread.MIN_PRIORITY);
        worker.start();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#reloadIndex()
     */
    public void reloadIndex() throws InstallException
    {
        URL scratchfile = getCachedIndexFile();
        download(host, USERNAME, PASSWORD, directory, FILE_LIST_GZ, scratchfile);
        loaded = false;
    }

    /**
     * Load the cached index file into memory
     */
    private void loadCachedIndex() throws InstallException
    {
        entries.clear();

        URL cache = getCachedIndexFile();
        if (!NetUtil.isFile(cache))
        {
            log.info("Missing cache file: "+cache.toExternalForm()); //$NON-NLS-1$
        }
        else
        {
            try
            {
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

                    String internal = entry.getName();
                    if (!entry.isDirectory())
                    {
                        try
                        {
                            int size = (int) entry.getSize();
                            byte[] buffer = new byte[size];
                            tin.read(buffer);

                            if (internal.endsWith(".conf"))
                            {
                                internal = internal.substring(0, internal.length() - 5);
                            }
                            if (internal.startsWith(SwordConstants.DIR_CONF+File.separator))
                            {
                                internal = internal.substring(7);
                            }

                            Reader rin = new InputStreamReader(new ByteArrayInputStream(buffer));
                            SwordBookMetaData sbmd = new SwordBookMetaData(rin, internal);

                            if (sbmd.isSupported())
                            {
                                entries.put(sbmd.getName(), sbmd);
                            }
                        }
                        catch (Exception ex)
                        {
                            log.warn("Failed to load config for entry: "+internal, ex); //$NON-NLS-1$
                        }
                    }
                }

                tin.close();
                gin.close();
                in.close();
                loaded = true;
            }
            catch (IOException ex)
            {
                throw new InstallException(Msg.CACHE_ERROR, ex);
            }
        }
    }

    /**
     * The URL for the cached index file for this installer
     */
    private URL getCachedIndexFile() throws InstallException
    {
        try
        {
            URL scratchdir = Project.instance().getTempScratchSpace(getTempFileExtension(host, directory), false);
            return NetUtil.lengthenURL(scratchdir, FILE_LIST_GZ);
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.URL_FAILED, ex);
        }
    }

    /**
     * What are we using as a temp filename?
     */
    private static String getTempFileExtension(String host, String directory)
    {
        return DOWNLOAD_PREFIX + host + directory.replace('/', '_'); //$NON-NLS-1$
    }

    /**
     * Utility to download a file by FTP from a remote site
     * @param site The place to download from
     * @param user The user that does the downloading
     * @param password The password of the above user
     * @param dir The directory from which to download the file
     * @param file The file to download
     * @throws InstallException
     */
    private static void download(String site, String user, String password, String dir, String file, URL dest) throws InstallException
    {
        FTPClient ftp = new FTPClient();

        try
        {
            ftpInit(ftp, site, user, password, dir);

            // Check the download directory exists
            URL parent = NetUtil.shortenURL(dest, FILE_LIST_GZ);
            NetUtil.makeDirectory(parent);

            // Download the index file
            OutputStream out = NetUtil.getOutputStream(dest);

            ftp.retrieveFile(file, out);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                String text = ftp.getReplyString();
                throw new InstallException(Msg.DOWNLOAD_REFUSED, new Object[] { FILE_LIST_GZ, new Integer(reply), text });
            }
            out.close();
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.UNKNOWN_ERROR, ex);
        }
        finally
        {
            disconnect(ftp);
        }
    }

    /**
     * Utility to download a file by FTP from a remote site
     * @param site The place to download from
     * @param user The user that does the downloading
     * @param password The password of the above user
     * @param dir The directory from which to download the file
     * @throws InstallException
     */
    protected static void downloadAll(Job job, String site, String user, String password, String dir, URL destdir) throws InstallException
    {
        FTPClient ftp = new FTPClient();

        try
        {
            job.setProgress("Logging on to remote site");
            ftpInit(ftp, site, user, password, dir);

            job.setProgress("Downloading files");
            downloadContents(destdir, ftp);
        }
        catch (InstallException ex)
        {
            throw ex;
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.UNKNOWN_ERROR, ex);
        }
        finally
        {
            disconnect(ftp);
        }
    }

    /**
     * Recursively download the contents of the current ftp directory
     * to the given url
     */
    private static void downloadContents(URL destdir, FTPClient ftp) throws IOException, InstallException
    {
        FTPFile[] files = ftp.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            String name = files[i].getName();
            URL child = NetUtil.lengthenURL(destdir, name);

            if (files[i].isFile())
            {
                OutputStream out = NetUtil.getOutputStream(child);

                ftp.retrieveFile(name, out);

                int reply = ftp.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply))
                {
                    String text = ftp.getReplyString();
                    throw new InstallException(Msg.DOWNLOAD_REFUSED, new Object[] { FILE_LIST_GZ, new Integer(reply), text });
                }
                out.close();
            }
            else
            {
                downloadContents(child, ftp);
            }
        }
    }

    /**
     * Simple tway to connect to a remote site in preparation for a file listing
     * or a download.
     */
    private static void ftpInit(FTPClient ftp, String site, String user, String password, String dir) throws IOException, InstallException
    {
        log.info("Connecting to site=" + site + " dir=" + dir); //$NON-NLS-1$ //$NON-NLS-2$

        // First connect
        ftp.connect(site);

        log.info(ftp.getReplyString());
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            String text = ftp.getReplyString();
            throw new InstallException(Msg.CONNECT_REFUSED, new Object[] { site, new Integer(reply), text });
        }

        // Authenticate
        ftp.login(user, password);

        log.info(ftp.getReplyString());
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            String text = ftp.getReplyString();
            throw new InstallException(Msg.AUTH_REFUSED, new Object[] { user, new Integer(reply), text });
        }

        // Change directory
        ftp.changeWorkingDirectory(dir);

        log.info(ftp.getReplyString());
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            String text = ftp.getReplyString();
            throw new InstallException(Msg.CWD_REFUSED, new Object[] { dir, new Integer(reply), text });
        }

        ftp.setFileType(FTP.BINARY_FILE_TYPE);
    }

    /**
     * Silently close an ftp connection, ignoring any exceptions
     */
    private static void disconnect(FTPClient ftp)
    {
        if (ftp.isConnected())
        {
            try
            {
                ftp.disconnect();
            }
            catch (IOException ex2)
            {
                log.error("disconnect error", ex2); //$NON-NLS-1$
            }
        }
    }

    /**
     * @return Returns the directory.
     */
    public String getDirectory()
    {
        return directory;
    }

    /**
     * @param directory The directory to set.
     */
    public void setDirectory(String directory)
    {
        this.directory = directory;
        loaded = false;
    }

    /**
     * @return Returns the host.
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host The host to set.
     */
    public void setHost(String host)
    {
        this.host = host;
        loaded = false;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return Returns the username.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getURL()
     */
    public String getURL()
    {
        return PROTOCOL_SWORD+":"+username+":"+password+"@"+host+directory; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * Like getURL() except that we skip the password for display purposes.
     * @see SwordInstaller#getURL()
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return PROTOCOL_SWORD+":"+username+"@"+host+directory; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof SwordInstaller))
        {
            return false;
        }
        SwordInstaller that = (SwordInstaller) object;

        if (!equals(this.host, that.host))
        {
            return false;
        }

        if (!equals(this.directory, that.directory))
        {
            return false;
        }

        if (!equals(this.password, that.password))
        {
            return false;
        }

        if (!equals(this.username, that.username))
        {
            return false;
        }

        return true;
    }

    /**
     * Quick utility to check to see if 2 (potentially null) strings are equal
     */
    private boolean equals(String string1, String string2)
    {
        if (string1 == null)
        {
            return string2 == null;
        }
        return string1.equals(string2);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return host.hashCode() + directory.hashCode() + username.hashCode() + password.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object object)
    {
        SwordInstaller myClass = (SwordInstaller) object;

        return new CompareToBuilder()
            .append(this.host, myClass.host)
            .append(this.directory, myClass.directory)
            .toComparison();
    }

    /**
     * Do we need to reload the index file
     */
    private boolean loaded = false;

    /**
     * The remote hostname.
     */
    protected String host;

    /**
     * The remote username for a valid account on the <code>host</code>.
     */
    private String username = "anonymous"; //$NON-NLS-1$

    /**
     * The password to go with <code>username</code>.
     */
    private String password = "jsword@crosswire.com"; //$NON-NLS-1$

    /**
     * The directory containing modules on the <code>host</code>.
     */
    protected String directory = "/"; //$NON-NLS-1$

    /**
     * A map of the entries in this download area
     */
    protected Map entries = new HashMap();

    /**
     * The default anon username
     */
    private static final String USERNAME = "anonymous"; //$NON-NLS-1$

    /**
     * The default anon password
     */
    private static final String PASSWORD = "anon@anon.com"; //$NON-NLS-1$

    /**
     * The sword index file
     */
    private static final String FILE_LIST_GZ = "mods.d.tar.gz"; //$NON-NLS-1$

    /**
     * We need to be ablee to provide a URL as part of the API
     */
    private static final String PROTOCOL_SWORD = "sword"; //$NON-NLS-1$

    /**
     * When we cache a download index
     */
    private static final String DOWNLOAD_PREFIX = "download-"; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordInstaller.class);
}
