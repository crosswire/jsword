package org.crosswire.jsword.book.install.sword;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksListener;
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
 * An implementation of Installer for reading data from Sword Web sites.
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
 * @author Mark Goodwin [goodwinster at gmail dot com]
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class HttpSwordInstaller extends AbstractBookList implements Installer, Comparable
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getURL()
     */
    public String getURL()
    {
        return PROTOCOL_WEB + ":" + host + directory; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBookMetaDatas()
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
     * @see org.crosswire.jsword.book.install.Installer#reloadIndex()
     */
    public void reloadIndex() throws InstallException
    {
        URL scratchfile = getCachedIndexFile();
        download(host, directory, FILE_LIST_GZ, scratchfile);
        loaded = false;
    }

    /**
     * Utility to download a file by FTP from a remote site
     * @param site The place to download from
     * @param dir The directory from which to download the file
     * @param file The file to download
     * @throws InstallException
     */
    private static void download(String site, String dir, String file, URL dest) throws InstallException
    {
        InputStream in = null;
        OutputStream out = null;
        try
        {
            URL url = new URL("http://" + site + dir + '/' + LIST_DIR + '/' + file);
            byte[] buf = new byte[4096];

            // Check the download directory exists
            URL parent = NetUtil.shortenURL(dest, FILE_LIST_GZ);
            NetUtil.makeDirectory(parent);

            // Download the index file
            out = NetUtil.getOutputStream(dest);
            in = url.openStream();
            for (int read = 0; -1 != (read = in.read(buf));)
            {
                out.write(buf, 0, read);
            }
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.UNKNOWN_ERROR, ex);
        }
        finally
        {
            if (null != in)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            if (null != out)
            {
                try
                {
                    out.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Utility to download a file by HTTP from a remote site
     * @param site The place to download from
     * @param user The user that does the downloading
     * @param password The password of the above user
     * @param dir The directory from which to download the file
     * @throws InstallException
     */
    protected static void downloadZip(Job job, String site, String dir, URL destdir) throws InstallException
    {
        InputStream in = null;
        OutputStream out = null;

        try
        {
            job.setProgress(Msg.JOB_DOWNLOADING.toString());
            URL zipurl = new URL("http://" + site + dir);
            File f = File.createTempFile("swd", "zip");
            out = new FileOutputStream(f);
            in = zipurl.openStream();
            byte[] buf = new byte[4096];
            for (int count = 0; -1 != (count = in.read(buf));)
            {
                out.write(buf, 0, count);
            }
            // unpack the zip.
            log.debug("The file is downlaoded!");
            ZipFile zf = new ZipFile(f);
            Enumeration entries = zf.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String entrypath = entry.getName();
                log.debug("entry is called " + entrypath);
                String filename = entrypath.substring(entrypath.lastIndexOf('/') + 1);
                URL child = NetUtil.lengthenURL(destdir, filename);
                OutputStream dataOut = NetUtil.getOutputStream(child);
                InputStream dataIn = zf.getInputStream(entry);
                for (int count = 0; -1 != (count = dataIn.read(buf));)
                {
                    dataOut.write(buf, 0, count);
                }
                dataOut.close();
            }
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.UNKNOWN_ERROR, ex);
        }
        finally
        {
            if (null != in)
            {
                try
                {
                    in.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
            if (null != out)
            {
                try
                {
                    out.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#install(org.crosswire.jsword.book.BookMetaData)
     */
    public void install(BookMetaData bmd) throws InstallException
    {
        if (!(bmd instanceof SwordBookMetaData))
        {
            assert false;
            return;
        }

        // Is the book already installed? Then nothing to do.
        if (Books.installed().getBookMetaData(bmd.getName()) != null)
        {
            Reporter.informUser(this, Msg.INSTALLED, bmd.getName());
            return;
        }

        final SwordBookMetaData sbmd = (SwordBookMetaData) bmd;

        Reporter.informUser(this, Msg.INSTALLING, sbmd.getName());

        // So now we know what we want to install - all we need to do
        // is installer.install(name) however we are doing it in the
        // background so we create a job for it.
        final Thread worker = new Thread("DisplayPreLoader") //$NON-NLS-1$
        {
            public void run()
            {
                URL predicturl = Project.instance().getWritablePropertiesURL("sword-install"); //$NON-NLS-1$
                Job job = JobManager.createJob(Msg.INSTALLING.toString(sbmd.getName()), predicturl, this, true);

                try
                {
                    job.setProgress(Msg.JOB_INIT.toString());

                    ModuleType type = sbmd.getModuleType();
                    String modpath = type.getInstallDirectory();
                    String destname = modpath + '/' + sbmd.getInternalName();

                    File dldir = SwordBookDriver.getDownloadDir();
                    File moddir = new File(dldir, SwordConstants.DIR_DATA);
                    File fulldir = new File(moddir, destname);
                    fulldir.mkdirs();
                    URL desturl = new URL(NetUtil.PROTOCOL_FILE, null, fulldir.getAbsolutePath());
                    String dir = directory;
                    downloadZip(job, host, directory + '/' + PACKAGE_DIR + '/' + sbmd.getInitials() + ZIP_SUFFIX, desturl);
                    String name = sbmd.getInitials();

                    job.setProgress(Msg.JOB_CONFIG.toString());
                    File confdir = new File(dldir, SwordConstants.DIR_CONF);
                    confdir.mkdirs();
                    File conf = new File(confdir, sbmd.getInternalName() + SwordConstants.EXTENSION_CONF);
                    URL configurl = new URL(NetUtil.PROTOCOL_FILE, null, conf.getAbsolutePath());
                    sbmd.save(configurl);

                    SwordBookDriver.registerNewBook(sbmd, dldir);

                    // inform the user that we are done
                    Reporter.informUser(this, Msg.INSTALL_DONE, sbmd.getName());
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
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0)
    {
        HttpSwordInstaller myClass = (HttpSwordInstaller) arg0;

        return new CompareToBuilder().append(this.host, myClass.host).append(this.directory, myClass.directory).toComparison();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#addBooksListener(org.crosswire.jsword.book.BooksListener)
     */
    public void addBooksListener(BooksListener li)
    {
        listeners.add(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#removeBooksListener(org.crosswire.jsword.book.BooksListener)
     */
    public void removeBooksListener(BooksListener li)
    {
        listeners.remove(li);
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
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof HttpSwordInstaller))
        {
            return false;
        }
        HttpSwordInstaller that = (HttpSwordInstaller) object;

        if (!equals(this.host, that.host))
        {
            return false;
        }

        if (!equals(this.directory, that.directory))
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

    /**
     * Load the cached index file into memory
     */
    private void loadCachedIndex() throws InstallException
    {
        entries.clear();

        URL cache = getCachedIndexFile();
        if (!NetUtil.isFile(cache))
        {
            log.info("Missing cache file: " + cache.toExternalForm()); //$NON-NLS-1$
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

                            if (internal.endsWith(SwordConstants.EXTENSION_CONF))
                            {
                                internal = internal.substring(0, internal.length() - 5);
                            }
                            if (internal.startsWith(SwordConstants.DIR_CONF + '/'))
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
                            log.warn("Failed to load config for entry: " + internal, ex); //$NON-NLS-1$
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
     * Do we need to reload the index file
     */
    private boolean loaded = false;

    /**
     * A map of the entries in this download area
     */
    protected Map entries = new HashMap();

    /**
     * The sword index file
     */
    private static final String FILE_LIST_GZ = "mods.d.tar.gz"; //$NON-NLS-1$

    /**
     * The relative path of the dir holding the index file
     */
    private static final String LIST_DIR = "raw";

    /**
     * The relative path of the dir holding the zip files
     */
    private static final String PACKAGE_DIR = "packages/rawzip";

    /**
     * The suffix of zip modules on this server
     */
    private static final String ZIP_SUFFIX = ".zip";

    /**
     * When we cache a download index
     */
    private static final String DOWNLOAD_PREFIX = "download-"; //$NON-NLS-1$

    private ArrayList listeners = new ArrayList();
    private String host;
    private String directory;

    private static final String PROTOCOL_WEB = "web";

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(HttpSwordInstaller.class);
}