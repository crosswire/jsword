
package org.crosswire.jsword.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.URLFilter;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Accessor for various resources available in jar files or in the filesystem
 * in general.
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
public class Resource
{
    public static final String XSLT_EXTENSION = ".xsl";
    public static final String PROPERTIES_EXTENSION = ".properties";
    public static final String PROJECT_DIRECTORY = ".jsword";

    /**
     * Singleton controlled by Project.
     * <p>The biggest job is trying to work out which resource bundle to
     * load to work out where the config and data files are stored.
     * We construct a name from the projectname, hostname and any other
     * info and then try to use that.
     */
    protected Resource()
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
    }

    /**
     * Get a list of sylesheets for a given subject.
     * @return The project root as a URL
     * @see NetUtil#list(URL, URLFilter)
     */
    public String[] getStyles(String subject)
    {
        try
        {
            String search = "xsl/"+subject+"/"+NetUtil.INDEX_FILE;
            URL index = getResource(search);
            return NetUtil.listByIndexFile(index, new URLFilter()
            {
                public boolean accept(String name)
                {
                    return name.endsWith(XSLT_EXTENSION);
                }
            });
        }
        catch (IOException ex)
        {
            return new String[0];
        }
    }

    /**
     * To get at xsl documents in wherever they may be.
     * @return The requested resource
     * @throws IOException if there is a problem reading the file
     * @throws MalformedURLException if the resource can not be found
     */
    public InputStream getStyleInputStream(String subject, String name) throws IOException, MalformedURLException
    {
        String path = "xsl/"+subject+"/"+name;
        return getResourceAsStream(path);
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
            URL index = getResource(search);
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
        InputStream in = getResourceAsStream(lookup);

        Properties prop = new Properties();
        prop.load(in);

        log.debug("Loaded "+name+" from classpath: [OK]");
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
            log.debug("Loaded "+subject+PROPERTIES_EXTENSION+" from writable area (ignoring resources): [OK]");
            return prop;
        }
        catch (IOException ex)
        {
            // If not then rely on the static version
            Properties prop = getResourceProperties(subject);
            log.debug("Loaded "+subject+PROPERTIES_EXTENSION+" from classpath: [OK]");
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
        InputStream in = getResourceAsStream(lookup);

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
     * @param A moniker for the are to write to. This will be converted into a
     * directory name.
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
     * @param class The class or interface to find implementors of.
     * @return Class[] The list of implementing classes.
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
     * Get the preferred implementor of some interface or abstract class.
     * This is currently done by looking up a properties file by the name of
     * the given class, and assuming that the "default" key is an implemention
     * of said class. Warnings are given otherwise.
     * @param class The class or interface to find an implementation of.
     * @return Class The configured implementing class.
     * @throws MalformedURLException if the properties file can not be found
     * @throws IOException if there is a problem reading the found file
     * @throws ClassNotFoundException if the read contents are not found
     * @throws ClassCastException if the read contents are not valid
     * @see Resource#getImplementors(Class)
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
     * @return The project root as a URL
     * @param subject The name of the desired resource (without any extension)
     * @return The requested resource
     * @throws IOException if there is a problem reading the file
     * @throws JDOMException If the resource is not valid XML
     * @throws MalformedURLException if the resource can not be found
     */
    public Document getDocument(String subject) throws JDOMException, IOException, MalformedURLException
    {
        String resource = subject+".xml";
        InputStream in = getResourceAsStream(resource);

        log.debug("Loading "+subject+".xml from classpath: [OK]");
        SAXBuilder builder = new SAXBuilder(true);
        return builder.build(in);
    }

    /**
     * Generic resource URL fetcher. One way or the other we'll find it!
     * I'm fairly sure some of these do the same thing, but which and how they
     * change on various JDK's is complex, and it seems simpler to take the
     * shotgun approach.
     * @param search The name of the resource (without a leading /) to find
     * @return The requested resource
     * @throws MalformedURLException if the resource can not be found
     */
    public URL getResource(String search) throws MalformedURLException
    {
        if (search.startsWith("/"))
            log.warn("getResource("+search+") starts with a /. More chance of success if it doesn't");

        URL reply = getClass().getResource(search);
        if (reply != null)
        {
            log.debug("getResource("+search+") = "+reply+" using getClass().getResource(search);");
            return reply;
        }

        reply = getClass().getResource("/"+search);
        if (reply != null)
        {
            log.debug("getResource("+search+") = "+reply+" using getClass().getResource(/search);");
            return reply;
        }

        reply = getClass().getClassLoader().getResource(search);
        if (reply != null)
        {
            log.debug("getResource("+search+") = "+reply+" using getClass().getClassLoader().getResource(search);");
            return reply;
        }

        reply = getClass().getClassLoader().getResource("/"+search);
        if (reply != null)
        {
            log.debug("getResource("+search+") = "+reply+" using getClass().getClassLoader().getResource(/search);");
            return reply;
        }

        reply = ClassLoader.getSystemResource(search);
        if (reply != null)
        {
            log.debug("getResource("+search+") = "+reply+" using ClassLoader.getSystemResource(search);");
            return reply;
        }

        reply = ClassLoader.getSystemResource("/"+search);
        if (reply != null)
        {
            log.debug("getResource("+search+") = "+reply+" using ClassLoader.getSystemResource(/search);");
            return reply;
        }

        throw new MalformedURLException("Can't find resource: "+search);
    }

    /**
     * Generic resource URL fetcher
     * @return The requested resource
     * @throws IOException if there is a problem reading the file
     * @throws MalformedURLException if the resource can not be found
     */
    public InputStream getResourceAsStream(String search) throws IOException, MalformedURLException
    {
        URL url = getResource(search);
        return url.openStream();
    }

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(Resource.class);
}
