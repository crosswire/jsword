
package org.crosswire.jsword.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.crosswire.common.util.NetUtil;
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
     * Get the root of the project installation.
     * @return The project root as a URL
     */
    public String[] getStyles(String subject) throws IOException
    {
        String index = "xsl/"+subject;
        return getIndex(index);
    }

    /**
     * Generic utility to read a file list from an index.proerties file.
     * PENDING(*): Is this sensible? I guess it is like this because it has to
     * work over webstart, but it is all very similar to NetUtil.list() however
     * this method does not make use of file: URLs.
     * @see org.crosswire.common.util.NetUtil#list(URL, URLFilter)
     */
    private String[] getIndex(String index) throws IOException
    {
        String search = index+"/index"+PROPERTIES_EXTENSION;
        InputStream in = getResourceAsStream(search);
        if (in == null)
            return new String[0];

        Properties prop = new Properties();
        prop.load(in);

        int i = 0;
        ArrayList list = new ArrayList();
        while (true)
        {
            i++;
            String line = prop.getProperty("index."+i);

            if (line == null) break;

            list.add(line);
        }

        return (String[]) list.toArray(new String[0]);
    }

    /**
     * To get at xsl documents in wherever they may be.
     */
    public InputStream getStyleInputStream(String subject, String name) throws IOException
    {
        String path = "xsl/"+subject+"/"+name+".xsl";
        InputStream in = getResourceAsStream(path);

        if (in == null)
            log.warn("Failed to find resource called "+path);

        return in;
    }

    /**
     * Get and load a properties file from the writable area or if that
     * fails from the classpath (where a default ought to be stored).
     * @param subject The name of the desired resource (without any extension)
     * @return The project root as a URL
     */
    public Properties getProperties(String subject) throws IOException, MalformedURLException
    {
        Properties prop;

        prop = getWritableProperties(subject);
        if (prop != null)
        {
            log.debug("Loaded "+subject+PROPERTIES_EXTENSION+" from writable area (ignoring resources): [OK]");
            return prop;
        }

        prop = getResourceProperties(subject);
        if (prop != null)
        {
            log.debug("Loaded "+subject+PROPERTIES_EXTENSION+" from classpath: [OK]");
            return prop;
        }

        log.debug("Loading "+subject+" from classpath: [NOT FOUND]");
        throw new FileNotFoundException("can't find properties: "+subject);
    }

    /**
     * Search the classpath for the specified properties file.
     * @param subject The name (minus the .properties extension)
     * @return The found and loaded properties file or null if not found.
     */
    public Properties getResourceProperties(String subject) throws IOException
    {
        String lookup = subject+PROPERTIES_EXTENSION;
        InputStream in = getResourceAsStream(lookup);

        if (in != null)
        {
            Properties prop = new Properties();
            prop.load(in);
            return prop;
        }

        return null;
    }

    /**
     * Get a the URL of a (potentially non-existant) properties file that we can
     * write to. This method of aquiring properties files is preferred over
     * getResourceProperties() as this iw writable and can take into account
     * user preferences.
     * @param subject The name (minus the .properties extension)
     * @return The project root as a URL
     */
    public Properties getWritableProperties(String subject) throws IOException, MalformedURLException
    {
        URL url = getWritablePropertiesURL(subject);

        if (NetUtil.isFile(url))
        {
            Properties prop = new Properties();
            prop.load(url.openStream());
            return prop;
        }
        
        return null;
    }

    /**
     * Get a the URL of a (potentially non-existant) properties file that we can
     * write to. This method of aquiring properties files is preferred over
     * getResourceProperties() as this iw writable and can take into account
     * user preferences.
     * @param subject The name (minus the .properties extension)
     * @return The project root as a URL
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
        String path = System.getProperty("user.home") + File.separator + PROJECT_DIRECTORY + "/";
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
    public URL getTempScratchSpace(String subject) throws IOException
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
     * Get and load an XML file from the classpath and a few other places
     * into a JDOM Document object.
     * @return The project root as a URL
     * @param subject The name of the desired resource (without any extension)
     * @return Document
     */
    public Document getDocument(String subject) throws JDOMException, IOException
    {
        String resource = subject+".xml";
        InputStream in = getResourceAsStream(resource);
        if (in == null)
            throw new IOException("Failed to find "+resource+" in classpath");

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
     * @return A URL of the found resource or null if we couldn't find one.
     */
    public URL getResource(String search)
    {
        URL reply = getClass().getResource(search);

        if (reply == null)
            reply = getClass().getResource("/"+search);

        if (reply == null)
            reply = getClass().getClassLoader().getResource(search);

        if (reply == null)
            reply = getClass().getClassLoader().getResource("/"+search);

        if (reply == null)
            reply = ClassLoader.getSystemResource(search);

        if (reply == null)
            reply = ClassLoader.getSystemResource("/"+search);

        return reply;
    }

    /**
     * Generic resource URL fetcher
     */
    public InputStream getResourceAsStream(String search) throws IOException
    {
        URL url = getResource(search);
        return url.openStream();
    }

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(Resource.class);
}
