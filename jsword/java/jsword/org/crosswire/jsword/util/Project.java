package org.crosswire.jsword.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.URLFilter;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * The Project class looks after the source of project files.
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
public class Project
{
    /**
     * System property to let people re-direct where the project directory is stored
     */
    private static final String PROP_HOMEDIR = "jsword.bible.dir"; //$NON-NLS-1$

    /**
     * A file so we know if we have the right versions directory
     */
    private static final String FILE_LOCATOR = "locator.properties"; //$NON-NLS-1$

    /**
     * Properties settings file
     */
    private static final String FILE_PROJECT = "project.properties"; //$NON-NLS-1$

    /**
     * Resources subdir for readings sets
     */
    private static final String DIR_READINGS = "readings"; //$NON-NLS-1$

    /**
     * The cache of downloaded files inside the project directory
     */
    private static final String DIR_NETCACHE = "netcache"; //$NON-NLS-1$

    /**
     * Versions subdirectory of the project directory
     */
    private static final String DIR_VERSIONS = "versions"; //$NON-NLS-1$

    /**
     * The JSword user settings directory
     */
    private static final String DIR_PROJECT = ".jsword"; //$NON-NLS-1$

    /**
     * Accessor for the resource singleton.
     */
    public static Project instance()
    {
        return instance;
    }

    /**
     * The filesystem resources
     */
    private static Project instance = new Project();

    /**
     * Prevent Instansiation.
     * <p>The biggest job is trying to work out which resource bundle to
     * load to work out where the config and data files are stored.
     * We construct a name from the projectname, hostname and any other
     * info and then try to use that.
     */
    private Project()
    {
        try
        {
            String path = System.getProperty("user.home") + File.separator + DIR_PROJECT; //$NON-NLS-1$
            home = new URL(NetUtil.PROTOCOL_FILE, null, path);
            ResourceUtil.setHome(home);
            base = NetUtil.lengthenURL(home, "/"); //$NON-NLS-1$
        }
        catch (MalformedURLException ex)
        {
            log.fatal("Failed to create home directory URL", ex); //$NON-NLS-1$
            assert false : ex;
        }

        try
        {
            URL urlcache = getTempScratchSpace(DIR_NETCACHE, true);
            File filecache = new File(urlcache.getFile());
            NetUtil.setURLCacheDir(filecache);
        }
        catch (IOException ex)
        {
            // This isn't fatal, it just means that NetUtil will try to use $TMP
            // in place of a more permanent solution.
            log.warn("Failed to get directory for NetUtil.setURLCacheDir()", ex); //$NON-NLS-1$
        }

        try
        {
            InputStream in = ResourceUtil.getResourceAsStream(FILE_PROJECT);
            projprop.load(in);
        }
        catch (IOException ex)
        {
            log.warn("Failed to load project.properties file", ex); //$NON-NLS-1$
        }
    }

    /**
     * The version of this project.
     */
    public String getVersion()
    {
        return projprop.getProperty("version", "development"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * The name of this project.
     * @return the project's name
     */
    public String getName()
    {
        return projprop.getProperty("product", "J-Sword"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Get a list of readings sets for a given subject.
     * @return The project root as a URL
     * @see NetUtil#list(URL, URLFilter)
     */
    public String[] getInstalledReadingsSets()
    {
        try
        {
            String search = DIR_READINGS + NetUtil.SEPARATOR + NetUtil.INDEX_FILE;
            URL index = ResourceUtil.getResource(search);
            return NetUtil.listByIndexFile(index, new URLFilter()
            {
                public boolean accept(String name)
                {
                    return name.endsWith(FileUtil.EXTENSION_PROPERTIES);
                }
            });
        }
        catch (IOException ex)
        {
            return new String[0];
        }
    }

    /**
     * Get a list of readings sets for a given subject.
     * @return The project root as a URL
     * @see NetUtil#list(URL, URLFilter)
     */
    public Properties getReadingsSet(String name) throws IOException
    {
        String lookup = DIR_READINGS + NetUtil.SEPARATOR + name;
        InputStream in = ResourceUtil.getResourceAsStream(lookup);

        Properties prop = new Properties();
        prop.load(in);

        //log.debug("Loaded "+name+" from classpath: [OK]");
        return prop;
    }

    /**
     * Get and load a properties file from the writable area or if that
     * fails from the classpath (where a default ought to be stored)
     * @param subject The name of the desired resource (without any extension)
     * @return The found and loaded properties file
     * @throws IOException if the resource can not be loaded
     */
    public Properties getProperties(String subject) throws IOException
    {
        return ResourceUtil.getProperties(subject);
    }

    /**
     * Get a the URL of a (potentially non-existant) properties file that we can
     * write to. This method of aquiring properties files is preferred over
     * getResourceProperties() as this iw writable and can take into account
     * user preferences.
     * This method makes no promise that the URL returned is valid. It is
     * totally untested, so reading may well cause errors.
     * @param subject The name (minus the .properties extension)
     * @return The resource as a URL
     */
    public URL getWritablePropertiesURL(String subject)
    {
        return NetUtil.lengthenURL(base, subject+FileUtil.EXTENSION_PROPERTIES);
    }

    /**
     * When we need a directory to write stuff to.
     * <p>This directory should be used as a cache for something that could also
     * be got at runtime by some other means.
     * <p>So it is not for config data, and not for program files. If someone
     * were to delete all the files in this directory while you weren't looking
     * then life should not stop, but should carry on albeit with a slower
     * service.
     * <p>This method may well return null if we are running in a restricted
     * environment, so callers of this method should be prepared for that
     * eventuallity too.
     * <p>As a result of these limitations it could be OK to use {@link File} in
     * place of {@link URL} (which is the norm for this project), however there
     * doesn't seem to be a good reason to relax this rule here.
     * @param subject A moniker for the are to write to. This will be converted into a directory name.
     * @return A file: URL pointing at a local writable directory.
     */
    public URL getTempScratchSpace(String subject, boolean create) throws IOException
    {
        URL temp = NetUtil.lengthenURL(base, subject);
        
        if (create && !NetUtil.isDirectory(temp))
        {
            NetUtil.makeDirectory(temp);
        }

        return temp;
    }

    /**
     * Get and load an XML file from the classpath and a few other places
     * into a JDOM Document object.
     * @param subject The name of the desired resource (without any extension)
     * @return The requested resource
     * @throws IOException if there is a problem reading the file
     * @throws JDOMException If the resource is not valid XML
     * @throws MalformedURLException if the resource can not be found
     */
    public Document getDocument(String subject) throws JDOMException, IOException
    {
        String resource = subject + FileUtil.EXTENSION_XML;
        InputStream in = ResourceUtil.getResourceAsStream(resource);

        log.debug("Loading "+subject+".xml from classpath: [OK]"); //$NON-NLS-1$ //$NON-NLS-2$
        SAXBuilder builder = new SAXBuilder(true);
        return builder.build(in);
    }

    /**
     * Search for versions directories
     */
    public URL findBibleRoot(String subdir) throws MalformedURLException
    {
        URL root = null;

        // First see if there is a System property that can help us out
        String sysprop = System.getProperty(PROP_HOMEDIR);
        log.debug("Testing system property "+PROP_HOMEDIR+"="+sysprop); //$NON-NLS-1$ //$NON-NLS-2$

        if (sysprop != null)
        {
            URL found = NetUtil.lengthenURL(new URL(NetUtil.PROTOCOL_FILE, null, sysprop), DIR_VERSIONS);
            URL test = NetUtil.lengthenURL(found, FILE_LOCATOR);

            if (NetUtil.isFile(test))
            {
                log.debug("Found BibleRoot using system property jsword.bible.dir at "+test); //$NON-NLS-1$
                root = found;
            }
            else
            {
                log.warn("Missing jsword.bible.dir under: "+test.toExternalForm()); //$NON-NLS-1$
            }
        }

        // If not then try a wild guess
        if (root == null)
        {
            URL found = ResourceUtil.getResource(DIR_VERSIONS + File.separator + FILE_LOCATOR);
            URL test = NetUtil.shortenURL(found, FILE_LOCATOR);
            if (NetUtil.isFile(test))
            {
                log.debug("Found BibleRoot from current directory: "+test.toExternalForm()); //$NON-NLS-1$
                root = test;
            }
            else
            {
                log.warn("Missing BibleRoot from current directory: "+test.toExternalForm()); //$NON-NLS-1$
            }
        }

        if (root == null)
        {
            return null;
        }
        else
        {
            return NetUtil.lengthenURL(root, subdir);
        }
    }

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    public static class IsDirectoryURLFilter implements URLFilter
    {
        /**
         * Simple ctor
         */
        public IsDirectoryURLFilter(URL parent)
        {
            this.parent = parent;
        }

        /* (non-Javadoc)
         * @see org.crosswire.common.util.URLFilter#accept(java.lang.String)
         */
        public boolean accept(String name)
        {
            return NetUtil.isDirectory(NetUtil.lengthenURL(parent, name));
        }

        private URL parent;
    }

    /**
     * The home for this application
     */
    private URL home;

    /**
     * The home w/ trailing '/' for this application
     */
    private URL base;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Project.class);

    /**
     * The project properties file containing things like the release version number.
     */
    private Properties projprop = new Properties();
}
