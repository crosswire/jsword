/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.install.sword;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.CollectionUtil;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.AbstractBookList;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilterIterator;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookSet;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.sword.Backend;
import org.crosswire.jsword.book.sword.NullBackend;
import org.crosswire.jsword.book.sword.SwordBook;
import org.crosswire.jsword.book.sword.SwordBookDriver;
import org.crosswire.jsword.book.sword.SwordBookMetaData;
import org.crosswire.jsword.book.sword.SwordBookPath;
import org.crosswire.jsword.book.sword.SwordConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AbstractSwordInstaller provides for the common implementation of derived classes.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public abstract class AbstractSwordInstaller extends AbstractBookList implements Installer, Comparable<AbstractSwordInstaller> {

    /**
     * Build a default AbstractSwordInstaller
     */
    public AbstractSwordInstaller() {
        super();
    }

    /**
     * Utility to download a file from a remote site
     * 
     * @param job
     *            The way of noting progress
     * @param dir
     *            The directory from which to download the file
     * @param file
     *            The file to download
     * @throws InstallException
     */
    protected abstract void download(Progress job, String dir, String file, URI dest) throws InstallException;

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getInstallerDefinition()
     */
    public String getInstallerDefinition() {
        StringBuilder buf = new StringBuilder(host);
        buf.append(',');
        buf.append(packageDirectory);
        buf.append(',');
        buf.append(catalogDirectory);
        buf.append(',');
        buf.append(indexDirectory);
        buf.append(',');
        if (proxyHost != null) {
            buf.append(proxyHost);
        }
        buf.append(',');
        if (proxyPort != null) {
            buf.append(proxyPort);
        }
        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#isNewer(org.crosswire.jsword.book.Book)
     */
    public boolean isNewer(final Book book) {
        SwordBookMetaData sbmd = (SwordBookMetaData) book.getBookMetaData();
        File conf = sbmd.getConfigFile();

        // The conf may not exist in our download dir.
        // In this case we say that it should not be downloaded again.
        if (conf == null || !conf.exists()) {
            return false;
        }

        URI configURI = NetUtil.getURI(conf);

        URI remote = toRemoteURI(book);
        return NetUtil.isNewer(remote, configURI, proxyHost, proxyPort);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBooks()
     */
    public List<Book> getBooks() {
        try {
            if (!loaded) {
                loadCachedIndex();
            }

            // We need to create a List from the Set returned by
            // entries.values() so the underlying list is not modified.
            return new ArrayList<Book>(entries.values());
        } catch (InstallException ex) {
            log.error("Failed to reload cached index file", ex);
            return new ArrayList<Book>();
        } catch (IOException ex) {
            log.error("Failed to reload cached index file", ex);
            return new ArrayList<Book>();
        } catch (BookException ex) {
            log.error("Failed to reload cached index file", ex);
            return new ArrayList<Book>();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBookList#getBooks(org.crosswire.jsword.book.BookFilter)
     */
    @Override
    public List<Book> getBooks(BookFilter filter) {
        List<Book> books = null;
        synchronized (this) {
            books = getBooks();
        }
        List<Book> temp = CollectionUtil.createList(new BookFilterIterator(books, filter));
        return new BookSet(temp);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getBook(java.lang.String)
     */
    public Book getBook(final String name) {
        List<Book> books = null;
        synchronized (this) {
            books = getBooks();
        }
        // Check name first
        // First check for exact matches
        for (Book book : books) {
            if (name.equals(book.getName())) {
                return book;
            }
        }

        // Next check for case-insensitive matches
        for (Book book : books) {
            if (name.equalsIgnoreCase(book.getName())) {
                return book;
            }
        }

        // Then check initials
        // First check for exact matches
        for (Book book : books) {
            BookMetaData bmd = book.getBookMetaData();
            if (name.equals(bmd.getInitials())) {
                return book;
            }
        }

        // Next check for case-insensitive matches
        for (Book book : books) {
            BookMetaData bmd = book.getBookMetaData();
            if (name.equalsIgnoreCase(bmd.getInitials())) {
                return book;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#install(org.crosswire.jsword.book.Book)
     */
    public void install(final Book book) {
        // // Is the book already installed? Then nothing to do.
        // if (Books.installed().getBook(book.getName()) != null)
        // {
        // return;
        // }
        //
        final SwordBookMetaData sbmd = (SwordBookMetaData) book.getBookMetaData();

        // TRANSLATOR: Progress label indicating the installation of a book. {0} is a placeholder for the name of the book.
        String jobName = JSMsg.gettext("Installing book: {0}", sbmd.getName());
        Progress job = JobManager.createJob(String.format(Progress.INSTALL_BOOK, book.getInitials()), jobName, Thread.currentThread());

        URI temp = null;
        try {
            // Don't bother setting a size, we'll do it later.
            job.beginJob(jobName);

            Thread.yield();

            // TRANSLATOR: Progress label indicating the Initialization of installing of a book.
            job.setSectionName(JSMsg.gettext("Initializing"));

            temp = NetUtil.getTemporaryURI("swd", ZIP_SUFFIX);

            download(job, packageDirectory, sbmd.getInitials() + ZIP_SUFFIX, temp);

            // Once the unzipping is started, we need to continue
            job.setCancelable(false);
            if (!job.isFinished()) {
                //copy into mods.d and modules under SWORD_HOME
                File dldir = SwordBookPath.getSwordDownloadDir();
                IOUtil.unpackZip(NetUtil.getAsFile(temp), dldir, true, SwordConstants.DIR_CONF, SwordConstants.DIR_DATA);

                //copy everything else into JSWORD_HOME
                File jswordHome = NetUtil.getAsFile(CWProject.instance().getWritableProjectDir());
                IOUtil.unpackZip(NetUtil.getAsFile(temp), jswordHome, false, SwordConstants.DIR_CONF, SwordConstants.DIR_DATA);
                // TRANSLATOR: Progress label for installing the conf file for a book.
                job.setSectionName(JSMsg.gettext("Copying config file"));
                sbmd.setLibrary(NetUtil.getURI(dldir));
                SwordBookDriver.registerNewBook(sbmd);
            }

        } catch (IOException e) {
            Reporter.informUser(this, e);
            job.cancel();
        } catch (InstallException e) {
            Reporter.informUser(this, e);
            job.cancel();
        } catch (BookException e) {
            Reporter.informUser(this, e);
            job.cancel();
        } finally {
            job.done();
            // tidy up after ourselves
            // This is a best effort. If for some reason it does not delete now
            // it will automatically be deleted when the JVM exits normally.
            if (temp != null) {
                try {
                    NetUtil.delete(temp);
                } catch (IOException e) {
                    log.warn("Error deleting temp download file.", e);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#reloadBookList()
     */
    public void reloadBookList() throws InstallException {
        // TRANSLATOR: Progress label for downloading one or more files.
        String jobName = JSMsg.gettext("Downloading files");
        Progress job = JobManager.createJob(Progress.RELOAD_BOOK_LIST, jobName, Thread.currentThread());
        job.beginJob(jobName);

        List<File> errors = null;
        try {
            URI cacheDir = getCachedIndexDir();
            URI confDir = NetUtil.lengthenURI(cacheDir, "mods.d.zip");
            URI cache = getCachedIndexFile();
            download(job, catalogDirectory, FILE_LIST_GZ, cache);
            // It cannot be cancelled from this point forward
            job.setCancelable(false);
            if (NetUtil.isFile(confDir)) {
                String confDirPath = confDir.getPath();
                String confDirPathOld = confDirPath + ".old";

                File dirConf = new File(confDirPath);
                File dirConfOld = new File(confDirPathOld);
                // Get rid of the old. It shouldn't be there, but just in case
                if (dirConfOld.exists()) {
                    FileUtil.delete(dirConfOld);
                }
                if (!dirConf.renameTo(dirConfOld)) {
                    errors = FileUtil.delete(new File(confDirPath));
                }
                // TODO(DM): list all that failed
                if (errors != null && !errors.isEmpty()) {
                    // TRANSLATOR: Common error condition: The file could not be deleted. There can be many reasons.
                    // {0} is a placeholder for the file.
                    throw new InstallException(JSMsg.gettext("Unable to delete: {0}", errors.get(0)));
                }

                unpack(cacheDir, cache);
                // Best effort to delete
                FileUtil.delete(dirConfOld);
            }

            loaded = false;
        } catch (InstallException ex) {
            job.cancel();
            throw ex;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            job.done();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#downloadSearchIndex(org.crosswire.jsword.book.Book, java.net.URI)
     */
    public void downloadSearchIndex(Book book, URI localDest) throws InstallException {
        // TRANSLATOR: Progress label for downloading one or more files.
        String jobName = JSMsg.gettext("Downloading files");
        Progress job = JobManager.createJob(String.format(Progress.DOWNLOAD_SEARCH_INDEX, book.getInitials()), jobName, Thread.currentThread());
        job.beginJob(jobName);

        try {
            download(job, packageDirectory + '/' + SEARCH_DIR, book.getInitials() + ZIP_SUFFIX, localDest);
        } catch (InstallException ex) {
            job.cancel();
            throw ex;
        } finally {
            job.done();
        }
    }

    /**
     * Unpack the cached index file to disk
     */
    protected void unpack(URI cacheDir, URI cache) throws IOException {
        InputStream fin = null;
        GzipCompressorInputStream gin = null;
        ArchiveInputStream tin = null;
        ArchiveOutputStream zout = null;

        try {
            // The following is faster if it is passed a file.
            zout = new ZipArchiveOutputStream(new File(cacheDir.getPath(), "mods.d.zip"));
            fin = NetUtil.getInputStream(cache);
            gin = new GzipCompressorInputStream(fin);
            tin = new TarArchiveInputStream(gin);
            while (true) {
                ArchiveEntry entry = tin.getNextEntry();
                if (entry == null) {
                    break;
                }

                // We'll create the mods.d directory elsewhere
                if (entry.isDirectory()) {
                    continue;
                }

                String path = entry.getName();

                if (!path.endsWith(SwordConstants.EXTENSION_CONF)) {
                    log.error("Not a SWORD config file: {}", path);
                    continue;
                }

                int size = (int) entry.getSize();
                // Every now and then an empty entry sneaks in
                if (size == 0) {
                    log.error("Empty entry: {}", path);
                    continue;
                }

                ArchiveEntry zipEntry = new ZipArchiveEntry(path);
                zout.putArchiveEntry(zipEntry);
                byte[] buffer = new byte[size];
                int n = tin.read(buffer);
                if (n != size) {
                    log.error("Error: Could not read {} bytes for {} from {}", Integer.toString(size), path, cache.getPath());
                }
                zout.write(buffer, 0, n);
                zout.closeArchiveEntry();
            }
        } finally {
            IOUtil.close(tin);
            IOUtil.close(gin);
            IOUtil.close(fin);
            IOUtil.close(zout);
        }
    }

    /**
     * Load the cached index file into memory
     */
    protected void loadCachedIndex() throws IOException, InstallException, BookException {
        // We need a sword book driver so the installer can use the driver
        // name to use in deciding where to put the index.
        BookDriver fake = SwordBookDriver.instance();
        Backend nullBackend = new NullBackend();

        entries.clear();

        URI cacheDir = getCachedIndexDir();
        URI confDir = NetUtil.lengthenURI(cacheDir, "mods.d.zip");
        URI cache = getCachedIndexFile();
        // If the cache file is missing, get a fresh copy
        if (!NetUtil.isFile(cache)) {
            reloadBookList();
        }

        // If it is not missing and mods.d doesn't exist
        // expand it
        // For backward compatibility.
        // It used to be that downloading the file didn't unpack it.
        // Now it does.
        if (!NetUtil.isFile(confDir)) {
            unpack(cacheDir, cache);
        }

        InputStream fin = null;
        ZipArchiveInputStream zin = null;
        try {
            fin = NetUtil.getInputStream(confDir);
            zin = new ZipArchiveInputStream(fin);
            while (true) {
                ArchiveEntry entry = zin.getNextZipEntry();
                if (entry == null) {
                    break;
                }

                // We'll create the mods.d directory elsewhere
                if (entry.isDirectory()) {
                    continue;
                }

                String path = entry.getName();

                if (!path.endsWith(SwordConstants.EXTENSION_CONF)) {
                    log.error("Not a SWORD config file: {}", path);
                    continue;
                }

                int size = (int) entry.getSize();
                // Every now and then an empty entry sneaks in
                if (size == 0) {
                    log.error("Empty entry: {}", path);
                    continue;
                }

                // Create a buffer of the right size
                byte[] buffer = new byte[size];
                // Repeatedly read until all has been read
                int offset = 0;
                while (offset < size) {
                    offset += zin.read(buffer, offset, size - offset);
                }

                if (offset != size) {
                    log.error("Error: Could not read {} bytes, instead {}, for {} from {}", Integer.toString(size), Integer.toString(offset), path, cache.getPath());
                }

                // Set the path to something that gives the path to the zip and the entry in the zip
                SwordBookMetaData sbmd = new SwordBookMetaData(buffer, confDir.getPath() + '!' + path);
                sbmd.setDriver(fake);

                // skip any book that is not supported.
                if (!sbmd.isSupported()) {
                    continue;
                }

                Book book = new SwordBook(sbmd, nullBackend);
                entries.put(book.getInitials() + book.getName(), book);
            }
        } finally {
            IOUtil.close(fin);
            IOUtil.close(zin);
        }
        loaded = true;
    }

    /** remove the cached book list to clear memory
     */
    public void close() {
        entries.clear();
        loaded = false;
    }

    /**
     * @return the catalogDirectory
     */
    public String getCatalogDirectory() {
        return catalogDirectory;
    }

    /**
     * @param catologDirectory
     *            the catologDirectory to set
     */
    public void setCatalogDirectory(String catologDirectory) {
        this.catalogDirectory = catologDirectory;
    }

    /**
     * @return Returns the directory.
     */
    public String getPackageDirectory() {
        return packageDirectory;
    }

    /**
     * @param newDirectory
     *            The directory to set.
     */
    public void setPackageDirectory(String newDirectory) {
        if (packageDirectory == null || !packageDirectory.equals(newDirectory)) {
            packageDirectory = newDirectory;
            loaded = false;
        }
    }

    /**
     * @return the indexDirectory
     */
    public String getIndexDirectory() {
        return indexDirectory;
    }

    /**
     * @param indexDirectory
     *            the indexDirectory to set
     */
    public void setIndexDirectory(String indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    /**
     * @return Returns the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @param newHost
     *            The host to set.
     */
    public void setHost(String newHost) {
        if (host == null || !host.equals(newHost)) {
            host = newHost;
            loaded = false;
        }
    }

    /**
     * @return Returns the proxyHost.
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * @param newProxyHost
     *            The proxyHost to set.
     */
    public void setProxyHost(String newProxyHost) {
        String pHost = null;
        if (newProxyHost != null && newProxyHost.length() > 0) {
            pHost = newProxyHost;
        }
        if (proxyHost == null || !proxyHost.equals(pHost)) {
            proxyHost = pHost;
            loaded = false;
        }
    }

    /**
     * @return Returns the proxyPort.
     */
    public Integer getProxyPort() {
        return proxyPort;
    }

    /**
     * @param newProxyPort
     *            The proxyPort to set.
     */
    public void setProxyPort(Integer newProxyPort) {
        if (proxyPort == null || !proxyPort.equals(newProxyPort)) {
            proxyPort = newProxyPort;
            loaded = false;
        }
    }

    /**
     * The URL for the cached index file for this installer
     */
    protected URI getCachedIndexDir() throws InstallException {
        try {
            return CWProject.instance().getWritableProjectSubdir(getTempFileExtension(host, catalogDirectory), true);
        } catch (IOException ex) {
            throw new InstallException(JSOtherMsg.lookupText("URL manipulation failed"), ex);
        }
    }

    /**
     * The URL for the cached index file for this installer
     */
    protected URI getCachedIndexFile() throws InstallException {
        return NetUtil.lengthenURI(getCachedIndexDir(), FILE_LIST_GZ);
    }

    /**
     * What are we using as a temp filename?
     */
    private static String getTempFileExtension(String host, String catalogDir) {
        return DOWNLOAD_PREFIX + host + catalogDir.replace('/', '_');
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AbstractSwordInstaller)) {
            return false;
        }
        AbstractSwordInstaller that = (AbstractSwordInstaller) object;

        if (!StringUtil.equals(this.host, that.host)) {
            return false;
        }

        if (!StringUtil.equals(this.packageDirectory, that.packageDirectory)) {
            return false;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(AbstractSwordInstaller myClass) {

        int ret = host.compareTo(myClass.host);
        if (ret != 0) {
            ret = packageDirectory.compareTo(myClass.packageDirectory);
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return host.hashCode() + packageDirectory.hashCode();
    }

    /**
     * A map of the books in this download area
     */
    protected Map<String, Book> entries = new HashMap<String, Book>();

    /**
     * The remote hostname.
     */
    protected String host;

    /**
     * The remote proxy hostname.
     */
    protected String proxyHost;

    /**
     * The remote proxy port.
     */
    protected Integer proxyPort;

    /**
     * The directory containing zipped books on the <code>host</code>.
     */
    protected String packageDirectory = "";

    /**
     * The directory containing the catalog of all books on the
     * <code>host</code>.
     */
    protected String catalogDirectory = "";

    /**
     * The directory containing the catalog of all books on the
     * <code>host</code>.
     */
    protected String indexDirectory = "";

    /**
     * Do we need to reload the index file
     */
    protected boolean loaded;

    /**
     * The sword index file
     */
    protected static final String FILE_LIST_GZ = "mods.d.tar.gz";

    /**
     * The sword conf directory
     */
    protected static final String CONF_DIR = "mods.d";

    /**
     * The suffix of zip books on this server
     */
    protected static final String ZIP_SUFFIX = ".zip";

    /**
     * The relative path of the dir holding the search index files
     */
    protected static final String SEARCH_DIR = "search/jsword/L1";

    /**
     * When we cache a download index
     */
    protected static final String DOWNLOAD_PREFIX = "download-";

    /**
     * The log stream
     */
    protected static final Logger log = LoggerFactory.getLogger(AbstractSwordInstaller.class);
}
