package org.crosswire.common.util;

import java.net.URL;

/**
 * CWClassLoader extends the regular class loader by using ResourceUtil
 * to find resources. This is needed so that ResourceBundle can find
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
 * @author DM Smith [dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public class CWClassLoader extends ClassLoader
{
    /**
     * Creates a class loader that finds resources
     * for the supplied class using ResourceUtil
     */
    public CWClassLoader()
    {
        owner = getCallingClass();
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
            resource = ResourceUtil.findHomeResource(search);
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

        // Let's let the ResourceUtil try to find it
        if (resource == null)
        {
            resource = ResourceUtil.findResource(search);
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
     * @param search
     * @return
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
     * @param search
     * @return
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

    /*
     * When called from the constructor it will return the class
     * calling the constructor.
     *
     */
    private static Class getCallingClass()
    {
        return resolver.getClassContext()[CALL_CONTEXT_OFFSET];
    }

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
    private static ClassLoader pickLoader(final ClassLoader loader1, final ClassLoader loader2)
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
                for (ClassLoader curloader = loader2 ; curloader != null; curloader = curloader.getParent())
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
     * A helper class to make the call stack visible.
     */
    private static final class ClassResolver extends SecurityManager
    {
        protected Class[] getClassContext()
        {
            return super.getClassContext();
        }

    }

    private static final int CALL_CONTEXT_OFFSET = 3; // may need to change if this class is redesigned

    private static final ClassResolver resolver;

    static
    {
        try
        {
            resolver = new ClassResolver();
        }
        catch (SecurityException se)
        {
            throw new RuntimeException("CWClassLoader: could not create ClassResolver: ", se);
        }
    }

    private Class owner;
}
