
package org.crosswire.jsword.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertiesUtil;
import org.crosswire.common.util.StringUtil;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Accessor for various resources available in jar files or in the filesystem
 * in general.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @author Keith Ralston
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
    protected Resource(String base)
    {
        try
        {
            // Default paths
            String homedir = System.getProperty("user.home") + File.separator + ".jsword";
            write = new URL("file", null, homedir);

            logs = NetUtil.lengthenURL(getWriteRoot(), "logs");
            NetUtil.makeDirectory(logs);

            URL log_url = NetUtil.lengthenURL(logs, "log.txt");
            log_out = new PrintWriter(NetUtil.getOutputStream(log_url, true));

            URL err_url = NetUtil.lengthenURL(logs, "err.txt");
            err_out = new PrintWriter(NetUtil.getOutputStream(err_url, true));

            cooks = NetUtil.lengthenURL(write, "cookies");
            NetUtil.makeDirectory(cooks);

            bibles = findBibleRoot();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        // This is a horrible hack. JBuilder4 refuses to pass a
        // thing in saying 'JBuilder' so to work out if we are
        // being run from inside jbuilder4 we check for the
        // class below. This class should only be present for the
        // embedded case.
        try
        {
            if (base == null)
            {
                Class.forName("com.borland.jbuilder.webserverglue.shtml.a");
                System.out.print("Detected JBuilder parent, using base=JBuilder");
                base = "JBuilder";
            }
        }
        catch (ClassNotFoundException ex)
        {
            // This just means that we are not inside jbuilder, carry on.
        }

        // Custom paths from: Directory.properties
        {
            String path = "Directory.properties";
            InputStream in = getClass().getClassLoader().getSystemResourceAsStream(path);
            load(in, path+" in classpath");
        }

        // Custom paths from: Directory<base>.properties
        if (base != null && base.length() > 0)
        {
            String path = "Directory"+base+".properties";
            InputStream in = getClass().getClassLoader().getSystemResourceAsStream(path);
            load(in, path+" in classpath");
        }

        // Custom paths from: $HOME/.jsword/Directory.properties
        {
            String path = System.getProperty("user.home") + File.separator + ".jsword/Directory.properties";
            load(path);
        }

        // Custom paths from: $HOME/.jsword/Directory<base>.properties
        if (base != null && base.length() > 0)
        {
            String path = System.getProperty("user.home") + File.separator + ".jsword/Directory"+base+".properties";
            load(path);
        }
    }

    /**
     * Get the root of the project installation.
     * @return The project root as a URL
     */
    public URL getBiblesRoot() throws MalformedURLException
    {
        return bibles;
    }

    /**
     * Get the root of the project installation.
     * @return The project root as a URL
     */
    public String[] getStyles(String subject) throws IOException, MalformedURLException
    {
        String index = "xsl/"+subject;
        return getIndex(index);
    }

    /**
     * To get at xsl documents in wherever they may be.
     */
    public InputStream getStyleInputStream(String subject, String name) throws MalformedURLException
    {
        String path = "xsl/"+subject+"/"+name+".xsl";
        return getClass().getClassLoader().getSystemResourceAsStream(path);
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
        InputStream in;

        in = getClass().getClassLoader().getSystemResourceAsStream(subject+".properties");
        if (in != null)
        {
            log.debug("Loading "+subject+".properties from classpath: [OK]");
            PropertiesUtil.load(prop, in);
        }
        else
        {
            log.debug("Loading "+subject+".properties from classpath: [NOT FOUND]");
        }

        String path = System.getProperty("user.home") + File.separator + ".jsword/" + subject + ".properties";
        File file = new File(path);
        if (file.isFile() && file.canRead())
        {
            log.debug("Loading "+subject+".properties from ~/.jsword/" + subject + ".properties: [OK]");
            in = new FileInputStream(file);
            PropertiesUtil.load(prop, in);
        }
        else
        {
            log.debug("Loading "+subject+".properties from ~/.jsword/" + subject + ".properties: [NOT FOUND]");
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
        InputStream in = getClass().getClassLoader().getSystemResourceAsStream(resource);
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
        URL reply;

        reply = getClass().getClassLoader().getSystemResource(subject+".properties");
        if (reply == null)
        {
            throw new MalformedURLException("Failed to find Desktop.properties");
        }

        return reply;
    }

    /**
     * Return a stream to which to write log messages
     */
    public PrintWriter getLogFileWriter() throws IOException, MalformedURLException
    {
        return log_out;
    }

    /**
     * Return a stream to which to write log messages
     */
    public PrintWriter getErrorFileWriter() throws IOException, MalformedURLException
    {
        return err_out;
    }

    /**
     * Get the root of the project installation.
     * @return The project root as a URL
     */
    public URL getUserHistory(String id) throws MalformedURLException
    {
        return NetUtil.lengthenURL(cooks, id+".properties");
    }

    /**
     * Get the root of the project installation.
     * @return The project root as a URL
     */
    public URL getUserSettings(String id) throws MalformedURLException
    {
        return NetUtil.lengthenURL(cooks, id+".log");
    }

    /**
     * Get the root of the project installation.
     * @return The project root as a URL
     */
    private URL getWriteRoot() throws MalformedURLException
    {
        return write;
    }

    /**
     * Generic utility to read a file list from an index.proerties file
     */
    private String[] getIndex(String index) throws IOException
    {
        InputStream in = getClass().getClassLoader().getSystemResourceAsStream(index+"/index.properties");
        if (in == null) return new String[0];

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

    /**
     * Load directory defaults from a current property file
     */
    private void load(String path)
    {
        try
        {
            InputStream in = new FileInputStream(path);
            if (in != null)
            {
                Properties prop = new Properties();
                PropertiesUtil.load(prop, in);
                log.debug("Trying "+path+": [Loading]");

                load(prop);
            }
            else
            {
                log.debug("Trying "+path+": [Skipping]");
            }
        }
        catch (IOException ex)
        {
            log.debug("Trying "+path+": [Skipping]");
        }
    }

    /**
     * Load directory defaults from a current property file
     */
    private void load(InputStream in, String name)
    {
        if (in != null)
        {
            try
            {
                Properties prop = new Properties();
                PropertiesUtil.load(prop, in);
                log.debug("Trying "+name+": [Loading]");

                load(prop);
            }
            catch (IOException ex)
            {
                log.debug("Trying "+name+": [Failed: "+ex+"]");
            }
        }
        else
        {
            log.debug("Trying "+name+": [Skipping]");
        }
    }

    /**
     * Load directory defaults from a current property file
     */
    private void load(Properties prop)
    {
        String temp;

        temp = prop.getProperty("logfile");
        if (temp != null && !temp.equals(""))
        {
            try
            {
                log_out = new PrintWriter(new FileWriter(temp));
                log.debug(" set logfile="+temp);
            }
            catch (IOException ex)
            {
                log.debug(" ignoring logfile="+temp);
            }
        }

        temp = prop.getProperty("errfile");
        if (temp != null && !temp.equals(""))
        {
            try
            {
                err_out = new PrintWriter(new FileWriter(temp));
                log.debug(" set errfile="+temp);
            }
            catch (IOException ex)
            {
                log.debug(" ignoring errfile="+temp);
            }
        }

        styles = translate(prop, "styles", styles);
        config = translate(prop, "config", config);
        cooks = translate(prop, "cooks", cooks);
        cooks = translate(prop, "cooks", cooks);
        write = translate(prop, "write", write);
        bibles = translate(prop, "versns", bibles);
    }

    /**
     * Quick method to help us turn a string into a URL or null if the
     * string we were passed was null
     */
    private URL translate(Properties prop, String name, URL deft)
    {
        String temp = prop.getProperty(name);

        try
        {
            if (temp != null && !temp.equals(""))
            {
                URL url = new URL(temp);
                log.debug(" set "+name+"="+temp+" (was "+deft+")");
                return url;
            }
        }
        catch (MalformedURLException ex)
        {
            log.debug(" ignoring as invalid "+name+"="+temp);
        }

        return null;
    }

    /**
     * Search for versions directories
     */
    private URL findBibleRoot() throws MalformedURLException
    {
        URL found;

        log.debug("Looking for Bibles:");

        // First see if there is a System property that can help us out
        String sysprop = System.getProperty("jsword.bible.dir");
        if (sysprop == null)
        {
            log.debug("-- Unset system property jsword.bible.dir");
        }
        else
        {
            found = findBibleRoot(new URL("file", null, sysprop));
            if (found == null)
            {
                log.debug("-- Not found from system property jsword.bible.dir at "+sysprop+"");
            }
            else
            {
                log.debug("-- Found from system property jsword.bible.dir at "+sysprop+"");
                return found;
            }
        }

        // First look in the classpath where we are situated
        URL base = getLocalClasspath(getClass());
        found = findBibleRoot(base);
        if (found == null)
        {
            log.debug("-- Not found in local classpath: "+base);
        }
        else
        {
            log.debug("-- Found in local classpath at "+found);
            return found;
        }

        // The go through the rest in turn
        String full = System.getProperty("java.class.path");
        log.debug("-- Trying other classpath entries using: "+full);
        String[] paths = StringUtil.tokenize(full, File.pathSeparator);
        for (int i=0; i<paths.length; i++)
        {
            try
            {
                base = new URL("file", null, paths[i]);
                found = findBibleRoot(base);
                if (found == null)
                {
                    log.debug("-- Not found at classpath component: "+paths[i]);
                }
                else
                {
                    log.debug("-- Found at classpath component: "+paths[i]);
                    return found;
                }
            }
            catch (MalformedURLException ex)
            {
                log.debug("-- Error trying classpath component: "+paths[i]+" "+ex);
            }
        }

        log.warn("No Bible source found.");
        return null;
    }

    /**
     * Look for versions directories around the given URL
     */
    private URL findBibleRoot(URL base)
    {
        URL test;

        // is there a versions dir as a subdir of base
        try
        {
            test = NetUtil.lengthenURL(base, "versions");
            if (NetUtil.isDirectory(test))
            {
                return test;
            }
        }
        catch (MalformedURLException ex)
        {
        }

        // remove the last leaf of the path
        String file = base.getFile();
        if (file.endsWith(File.separator))
            file = file.substring(0, file.length()-1);
        int lastslash = file.lastIndexOf(File.separator);
        if (lastslash == -1)
            file = ".";
        else
            file = file.substring(0, lastslash);

        // is there a versions dir as a subdir of that?
        try
        {
            URL temp = new URL(base.getProtocol(), base.getHost(), base.getPort(), file);
            test = NetUtil.lengthenURL(temp, "versions");
            if (NetUtil.isDirectory(test))
            {
                return test;
            }
        }
        catch (MalformedURLException ex)
        {
        }

        return null;
    }

    /**
     * This should probably live in a utility somewhere
     */
    private URL getLocalClasspath(Class source) throws MalformedURLException
    {
        String us_full = StringUtil.findClasspathEntry(getClass().getName());

        URL clazz = new URL("file", null, us_full);
        log.debug("getClassRoot()="+clazz);
        return clazz;
    }

    /** Where log files are written */
    private PrintWriter log_out = null;

    /** Where error logs are written */
    private PrintWriter err_out = null;

    /** The cached bibles root as a url */
    private URL bibles = null;

    /** The cached style root as a url */
    private URL styles = null;

    /** The cached config root as a url */
    private URL config = null;

    /** The cached cookie root as a url */
    private URL cooks = null;

    /** The cached log file root as a url */
    private URL logs = null;

    /** The cached write root as a url */
    private URL write = null;

    /** The log stream */
    protected static Logger log = Logger.getLogger(Resource.class);
}

/*
    /**
     * Get the root of the project installation.
     * @return The project root
     * @exception IllegalStateException If the app is incorrectly installed
     *
    private static URL getRoot() throws MalformedURLException
    {
        if (root != null)   return root;
        if (clazz == null)  getClassRoot();

        // First remove any trailing directory separators
        if (clazz.getFile().endsWith("classes") || clazz.getFile().endsWith("classes"+File.separator))
         {
            // This bit is easy
            root = NetUtil.shortenURL(clazz, "classes");

            log.debug("Found Root as "+root+" from classes directory.");
        }
        else
        {
            // So this is an archive
            String file = clazz.getFile();
            if (!file.endsWith(".jar") && !file.endsWith(".zip"))
                throw new MalformedURLException("Can't find project directory from classpath. Start file="+file);

            // remove the archive filename
            int last_slash = file.lastIndexOf(File.separator);
            String base = file.substring(0, last_slash);
            String remove = file.substring(last_slash);

            // remove the jars directory
            if (base.endsWith("jars"))
            {
                last_slash = base.lastIndexOf(File.separator);
                remove = file.substring(last_slash);
                base = file.substring(0, last_slash);
            }

            // remove the lib directory
            if (base.endsWith("lib"))
            {
                last_slash = base.lastIndexOf(File.separator);
                remove = file.substring(last_slash);
            }

            // actually create the URL - the result is the root directory
            root = NetUtil.shortenURL(clazz, remove);

            log.debug("Found Root as "+root+" from archive "+file);
        }

        return root;
    }

    /**
     * Get directory in which class files are stored.
     * HACK: I've not even started to make this work at Arachsys - I've
     * just put a hack in to check for a syatem property of user.home
     * ($HOME under unix) of /home/eireneh - and then fixed the answer for
     * this case.
     * @return The classes directory
     *
    private static URL getClassRoot() throws MalformedURLException
    {
        if (clazz != null)  return clazz;

        // On some systems (MVM included sometimes) this returns null to
        // indicate system class loader. The bad news is that on JDK 1.1
        // there is no way I know of of asking for the system ClassLoader
        ClassLoader loader = Project.class.getClassLoader();
        if (loader == null)
        {
            log.debug("getClassLoader failed. Using alternative version");
            return getClassRootAlternate();
        }

        String us_leaf = "/" + StringUtil.swap(Project.class.getName(), ".", "/") + ".class";

        // For some reason this does not work sometimes. getResource()
        // has a very long history of bugs.
        URL url = loader.getResource(us_leaf);
        if (url == null)
        {
            log.debug("getResource failed. Using alternative version");
            return getClassRootAlternate();
        }

        clazz = NetUtil.shortenURL(url, us_leaf);
        log.debug("getClassRoot()="+clazz);
        return clazz;
    }

    /** The cached root as a url *
    private static URL root = null;

    /** The cached classes root as a url *
    private static URL clazz = null;

    /** The cached lib root as a url *
    private static URL lib = null;
*/
