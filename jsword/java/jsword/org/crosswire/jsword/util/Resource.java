
package org.crosswire.jsword.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.crosswire.common.util.PropertiesUtil;
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
    /**
     * Singleton controlled by Project.
     * <p>The biggest job is trying to work out which resource bundle to
     * load to work out where the config and data files are stored.
     * We construct a name from the projectname, hostname and any other
     * info and then try to use that.
     */
    protected Resource()
    {
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
     * Get and load a properties file from the classpath and a few other places
     * into a Properties object.
     * @param subject The name of the desired resource (without any extension)
     * @return The project root as a URL
     */
    public Properties getProperties(String subject) throws IOException, MalformedURLException
    {
        Properties prop = new Properties();
        String lookup = "/"+subject+".properties";
        InputStream in = getResourceAsStream(lookup);
        if (in != null)
        {
            log.debug("Loading "+lookup+" from classpath: [OK]");
            PropertiesUtil.load(prop, in);
        }
        else
        {
            log.debug("Loading "+lookup+" from classpath: [NOT FOUND]");
        }

        String path = System.getProperty("user.home") + File.separator + ".jsword/" + subject;
        File file = new File(path);
        if (file.isFile() && file.canRead())
        {
            log.debug("Loading "+lookup+" from ~/.jsword/" + lookup + ": [OK]");
            in = new FileInputStream(file);
            PropertiesUtil.load(prop, in);
        }
        else
        {
            log.debug("Loading "+lookup+" from ~/.jsword/" + lookup + ": [NOT FOUND]");
        }

        return prop;
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
     * Get the root of the project installation.
     * @return The project root as a URL
     */
    public URL getPropertiesURL(String subject) throws IOException, MalformedURLException
    {
        String search = subject+".properties";
        URL reply = getResource(search);
        if (reply == null)
            throw new MalformedURLException("Failed to find "+search);

        return reply;
    }

    /**
     * Generic resource URL fetcher. One way or the other we'll find it!
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
            reply = getClass().getClassLoader().getSystemResource(search);

        if (reply == null)
            reply = getClass().getClassLoader().getSystemResource("/"+search);

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
     * Generic utility to read a file list from an index.proerties file
     */
    private String[] getIndex(String index) throws IOException
    {
        String search = index+"/index.properties";
        InputStream in = getResourceAsStream(search);
        if (in == null)
            return new String[0];

        Properties prop = new Properties();
        PropertiesUtil.load(prop, in);

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

    /** The log stream */
    protected static Logger log = Logger.getLogger(Resource.class);
}
