
package org.crosswire.jsword.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
     * Extension for XSLT files
     */
    public static final String XSLT_EXTENSION = ".xsl";

    /**
     * Extension for properties files
     */
    public static final String PROPERTIES_EXTENSION = ".properties";

    /**
     * The JSword user settings directory
     */
    public static final String PROJECT_DIRECTORY = ".jsword";

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
            URL urlcache = getTempScratchSpace("netcache");
            File filecache = new File(urlcache.getFile());
            NetUtil.setURLCacheDir(filecache);
        }
        catch (IOException ex)
        {
            // This isn't fatal, it just means that NetUtil will try to use $TMP
            // in place of a more permanent solution.
            log.warn("Failed to get directory for NetUtil.setURLCacheDir()", ex);
        }

        try
        {
            InputStream in = ResourceUtil.getResourceAsStream("project.properties");
            projprop.load(in);
        }
        catch (IOException ex)
        {
            log.warn("Failed to load project.properties file", ex);
        }
    }

    /**
     * The name of this project.
     */
    public String getVersion()
    {
        return projprop.getProperty("version", "development");
    }

    /**
     * The name of this project.
     */
    public String getName()
    {
        return projprop.getProperty("product", "J-Sword");
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
            String search = "readings/"+NetUtil.INDEX_FILE;
            URL index = ResourceUtil.getResource(search);
            return NetUtil.listByIndexFile(index, new URLFilter()
            {
                public boolean accept(String name)
                {
                    return name.endsWith(PROPERTIES_EXTENSION);
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
    public Properties getReadingsSet(String name) throws MalformedURLException, IOException
    {
        String lookup = "readings/"+name;
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
     * @throws MalformedURLException if the resource can not be found
     */
    public Properties getProperties(String subject) throws IOException, MalformedURLException
    {
        try
        {
            // Try for a writable version
            Properties prop = getWritableProperties(subject);
            //log.debug("Loaded "+subject+PROPERTIES_EXTENSION+" from writable area (ignoring resources): [OK]");
            return prop;
        }
        catch (IOException ex)
        {
            // If not then rely on the static version
            Properties prop = getResourceProperties(subject);
            //log.debug("Loaded "+subject+PROPERTIES_EXTENSION+" from classpath: [OK]");
            return prop;
        }
    }

    /**
     * Search the classpath for the specified properties file.
     * @param subject The name (minus the .properties extension)
     * @return The found and loaded properties file
     * @throws IOException if the resource can not be loaded
     * @throws MalformedURLException if the resource can not be found
     */
    public Properties getResourceProperties(String subject) throws IOException, MalformedURLException
    {
        String lookup = subject+PROPERTIES_EXTENSION;
        InputStream in = ResourceUtil.getResourceAsStream(lookup);

        Properties prop = new Properties();
        prop.load(in);
        return prop;
    }

    /**
     * Get a the URL of a (potentially non-existant) properties file that we can
     * write to. This method of aquiring properties files is preferred over
     * getResourceProperties() as this iw writable and can take into account
     * user preferences.
     * @param subject The name (minus the .properties extension)
     * @return The read properties file
     */
    public Properties getWritableProperties(String subject) throws IOException, MalformedURLException
    {
        URL url = getWritablePropertiesURL(subject);

        Properties prop = new Properties();
        prop.load(url.openStream());
        return prop;
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
     * @throws MalformedURLException Something went very wrong creating the URL
     */
    public URL getWritablePropertiesURL(String subject) throws MalformedURLException
    {
        URL base = getWritableBaseURL();
        return NetUtil.lengthenURL(base, subject+PROPERTIES_EXTENSION);
    }

    /**
     * Where can we write cache or config files?
     * @return URL of the users .jsword directory.
     */
    private URL getWritableBaseURL() throws MalformedURLException
    {
        String path = System.getProperty("user.home") + File.separator + PROJECT_DIRECTORY + File.separator;
        return new URL("file", null, path);
    }

    /**
     * When we need a directory to write stuff to.
     * <p>This directory should be used as a cache for something that could also
     * be got at runtime by some other means.
     * <p>So it is not for config data, and not for program files. If someone
     * were to delete all the files in this directory while you weren't looking
     * then life should not stop, but should carry on albeit with a slower
     * service.
     * <p>As a result of these limitations it could be OK to use {@link File} in
     * place of {@link URL} (which is the norm for this project), however there
     * doesn't seem to be a good reason to relax this rule here.
     * @param subject A moniker for the are to write to. This will be converted into a directory name.
     * @return A file: URL pointing at a local writable directory.
     */
    public URL getTempScratchSpace(String subject) throws IOException, MalformedURLException
    {
        URL base = getWritableBaseURL();
        URL temp = NetUtil.lengthenURL(base, subject);
        NetUtil.makeDirectory(temp);
        return temp;
    }

    /**
     * Get the known implementors of some interface or abstract class.
     * This is currently done by looking up a properties file by the name of
     * the given class, and assuming that values are implementors of said
     * class. Those that are not are warned, but ignored.
     * @param clazz The class or interface to find implementors of.
     * @return The list of implementing classes.
     */
    public Class[] getImplementors(Class clazz)
    {
        try
        {
            List matches = new ArrayList();
            Properties props = getProperties(clazz.getName());
            Iterator it = props.values().iterator();
            while (it.hasNext())
            {
                try
                {
                    String name = (String) it.next();
                    Class impl = Class.forName(name);
                    if (clazz.isAssignableFrom(impl))
                    {
                        matches.add(impl);
                    }
                    else
                    {
                        log.warn("Class "+impl.getName()+" does not implement "+clazz.getName()+". Ignoring.");
                    }
                }
                catch (Exception ex)
                {
                    log.warn("Failed to add class to list: "+clazz.getName(), ex);
                }            
            }

            log.debug("Found "+matches.size()+" implementors of "+clazz.getName());
            return (Class[]) matches.toArray(new Class[0]);
        }
        catch (Exception ex)
        {
            log.error("Failed to get any classes.", ex);
            return new Class[0];
        }
    }

    /**
     * Get a map of known implementors of some interface or abstract class.
     * This is currently done by looking up a properties file by the name of
     * the given class, and assuming that values are implementors of said
     * class. Those that are not are warned, but ignored. The reply is in the
     * form of a map of keys=strings, and values=classes in case you need to get
     * at the names given to the classes in the properties file.
     * @see Project#getImplementors(Class)
     * @param clazz The class or interface to find implementors of.
     * @return The map of implementing classes.
     */
    public Map getImplementorsMap(Class clazz)
    {
        Map matches = new HashMap();

        try
        {
            Properties props = getProperties(clazz.getName());
            Iterator it = props.keySet().iterator();
            while (it.hasNext())
            {
                try
                {
                    String key = (String) it.next();
                    String value = props.getProperty(key);
                    Class impl = Class.forName(value);
                    if (clazz.isAssignableFrom(impl))
                    {
                        matches.put(key, impl);
                    }
                    else
                    {
                        log.warn("Class "+impl.getName()+" does not implement "+clazz.getName()+". Ignoring.");
                    }
                }
                catch (Exception ex)
                {
                    log.warn("Failed to add class to list: "+clazz.getName(), ex);
                }            
            }

            log.debug("Found "+matches.size()+" implementors of "+clazz.getName());
        }
        catch (Exception ex)
        {
            log.error("Failed to get any classes.", ex);
        }

        return matches;
    }

    /**
     * Get the preferred implementor of some interface or abstract class.
     * This is currently done by looking up a properties file by the name of
     * the given class, and assuming that the "default" key is an implemention
     * of said class. Warnings are given otherwise.
     * @param clazz The class or interface to find an implementation of.
     * @return The configured implementing class.
     * @throws MalformedURLException if the properties file can not be found
     * @throws IOException if there is a problem reading the found file
     * @throws ClassNotFoundException if the read contents are not found
     * @throws ClassCastException if the read contents are not valid
     * @see Project#getImplementors(Class)
     */
    public Class getImplementor(Class clazz) throws MalformedURLException, IOException, ClassNotFoundException, ClassCastException
    {
        Properties props = getProperties(clazz.getName());
        String name = props.getProperty("default");
        
        Class impl = Class.forName(name);
        if (!clazz.isAssignableFrom(impl))
        {
            throw new ClassCastException("Class "+impl.getName()+" does not implement "+clazz.getName());
        }
        
        return impl;
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
    public Document getDocument(String subject) throws JDOMException, IOException, MalformedURLException
    {
        String resource = subject+".xml";
        InputStream in = ResourceUtil.getResourceAsStream(resource);

        log.debug("Loading "+subject+".xml from classpath: [OK]");
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
        String sysprop = System.getProperty("jsword.bible.dir");
        log.debug("Testing system property jsword.bible.dir="+sysprop);

        if (sysprop != null)
        {
            URL found = NetUtil.lengthenURL(new URL("file", null, sysprop), "versions");
            URL test = NetUtil.lengthenURL(found, "locator.properties");

            if (NetUtil.isFile(test))
            {
                log.debug("Found BibleRoot using system property jsword.bible.dir at "+test);
                root = found;
            }
            else
            {
                log.warn("Missing jsword.bible.dir under: "+test.toExternalForm());
            }
        }

        // If not then try a wild guess
        if (root == null)
        {
            URL found = ResourceUtil.getResource("versions/locator.properties");
            URL test = NetUtil.shortenURL(found, "locator.properties");
            if (NetUtil.isFile(test))
            {
                log.debug("Found BibleRoot from current directory: "+test.toExternalForm());
                root = test;
            }
            else
            {
                log.warn("Missing BibleRoot from current directory: "+test.toExternalForm());
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
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Project.class);

    /**
     * The project properties file containing things like the release version number.
     */
    private Properties projprop = new Properties();
}
