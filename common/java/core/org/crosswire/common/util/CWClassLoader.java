package org.crosswire.common.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * CWClassLoader extends the regular class loader by using looking
 * in more places. This is needed so that ResourceBundle can find
 * resources that are not held in the same package as the class.
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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class CWClassLoader extends ClassLoader
{
    /**
     * Creates a class loader that finds resources
     * for the supplied class that may not be in the class' package.
     * You can use this within base classes by passing getClass()
     * to load resources for a derived class.
     * @param resourceOwner is the owner of the resource
     */
    public CWClassLoader(Class resourceOwner)
    {
        owner = resourceOwner;
    }

    /**
     * Creates a class loader that finds resources
     * for the calling class that may not be in the class' package.
     * Use this only within classes that are directly looking up their resources.
     */
    public CWClassLoader()
    {
        owner = CallContext.instance().getCallingClass();
    }

    /* (non-Javadoc)
     * @see java.lang.ClassLoader#findResource(java.lang.String)
     */
    public URL findResource(String search)
    {
        URL resource = null;
        if (search == null || search.length() == 0)
        {
            return resource;
        }

        // First look for it with an absolute path
        // This allows developer overrides
        if (search.charAt(0) != '/')
        {
            resource = findResource('/' + search);
        }

        if (resource == null)
        {
            // Look for it in the class's package.
            String newsearch = adjustPackageSearch(search);
            if (!search.equals(newsearch))
            {
                resource = findResource(newsearch);
            }
        }

        // Sometimes it comes in with '/' inside of it.
        // So look for it as a file with '.' in the name
        // This is the form that will find files in the resource.jar
        if (resource == null)
        {
            // Look for it in the class's package.
            String newsearch = adjustPathSearch(search);
            if (!search.equals(newsearch))
            {
                resource = findResource(newsearch);
            }
        }

        // See if it can be found in the home directory
        if (resource == null)
        {
            resource = CWClassLoader.findHomeResource(search);
        }

        // See if it can be found by its own class.
        if (resource == null)
        {
            resource = owner.getResource(search);
        }

        // Try the appropriate class loader
        if (resource == null)
        {
            resource = getClassLoader().getResource(search);
        }

        // Try the bootstrap and the system loader
        if (resource == null)
        {
            resource = ClassLoader.getSystemResource(search);
        }

        // For good measure let the super class try to find it.
        if (resource == null)
        {
            resource = super.findResource(search);
        }

        return resource;
    }

    /**
     * Prefix the search with a package prefix, if not already.
     * Skip a leading '/' if present.
     */
    private String adjustPackageSearch(String search)
    {
        // If it has embedded '/' there is nothing to do.
        if (search.indexOf('/', 1) == -1)
        {
            String className = owner.getName();
            String pkgPrefix = className.substring(0, className.lastIndexOf('.') + 1);

            if (search.charAt(0) == '/')
            {
                String part = search.substring(1);
                if (!part.startsWith(pkgPrefix))
                {
                    search = '/' + pkgPrefix + part;
                }
            }
            else
            {
                if (!search.startsWith(pkgPrefix))
                {
                    search = pkgPrefix + search;
                }
            }
        }

        return search;
    }

    /**
     * Change all but a leading '/' to '.'
     */
    private String adjustPathSearch(String search)
    {
        if (search.indexOf('/', 1) != -1)
        {
            // Change all but a leading '/' to '.'
            if (search.charAt(0) == '/')
            {
                search = '/' + search.substring(1).replace('/', '.');
            }
            else
            {
                search = search.replace('/', '.');
            }
        }
        return search;
    }

    /**
     *
     */
    public ClassLoader getClassLoader()
    {
        // Choose the child loader as it will use the parent if need be
        // If they are not related then choose the context loader
        ClassLoader loader = pickLoader(Thread.currentThread().getContextClassLoader(), owner.getClassLoader());
        return pickLoader(loader, ClassLoader.getSystemClassLoader());
    }

    /**
     * Returns 'true' if 'loader2' is a delegation child of 'loader1' [or if
     * 'loader1'=='loader2']. Of course, this works only for classloaders that
     * set their parent pointers correctly. 'null' is interpreted as the
     * primordial loader [i.e., everybody's parent].
     */
    private static ClassLoader pickLoader(ClassLoader loader1, ClassLoader loader2)
    {
        ClassLoader loader = loader2;
        if (loader1 != loader2)
        {
            loader = loader1;
            if (loader1 == null)
            {
                loader = loader2;
            }
            else
            {
                // Is loader2 a descendant of loader1?
                // It is if we can walk up to the top and find it.
                for (ClassLoader curloader = loader2; curloader != null; curloader = curloader.getParent())
                {
                    if (curloader == loader1)
                    {
                        loader = loader2;
                        break;
                    }
                }
            }
        }
        return loader;
    }

    /**
     * If the application has set the home, it will return
     * the application's home directory, otherwise it returns null.
     * @return Returns the home.
     */
    public static synchronized URL getHome()
    {
        try
        {
            if (home != null)
            {
                return new URL(home.getProtocol(), home.getHost(), home.getPort(), home.getFile());
            }
        }
        catch (MalformedURLException e)
        {
            assert false;
        }
        return home;
    }

    /**
     * Establish the applications home directory from where
     * additional resources can be found. URL is expected to
     * end with the directory name, not '/'.
     * @param newhome The home to set.
     */
    public static synchronized void setHome(URL newhome)
    {
        home = newhome;
    }

    /**
     * Look for the resource in the home directory
     * @param search must be non-null, non-empty
     */
    public static URL findHomeResource(String search)
    {
        URL reply = null;

        URL homeURL = getHome();

        // Look at the application's home first to allow overrides
        if (homeURL != null)
        {
            URL override = NetUtil.lengthenURL(homeURL, search);

            // Make sure the file exists and can be read
            File file = new File(override.getFile());
            if (file.canRead())
            {
                reply = override;
            }
        }

        return reply;
    }

    /**
     * The class to which the resources belong
     */
    private Class owner = null;

    /**
     * Notion of a project's home from where additional resources can be found.
     */
    private static URL home = null;
}
