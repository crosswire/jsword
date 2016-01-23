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
package org.crosswire.common.util;

import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * CWClassLoader extends the regular class loader by using looking in more
 * places. This is needed so that ResourceBundle can find resources that are not
 * held in the same package as the class. This is expressed as a list of
 * locations, called homes, that the program will look in.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class CWClassLoader extends ClassLoader {
    /**
     * Creates a class loader that finds resources for the supplied class that
     * may not be in the class' package. You can use this within base classes by
     * passing getClass() to load resources for a derived class.
     * 
     * @param resourceOwner
     *            is the owner of the resource
     */
    CWClassLoader(Class<?> resourceOwner) {
        owner = resourceOwner;
    }

    /**
     * Creates a class loader that finds resources for the calling class that
     * may not be in the class' package. Use this only within classes that are
     * directly looking up their resources.
     */
    CWClassLoader() {
        owner = CallContext.getCallingClass();
    }

    /**
     * Creates a privileged class loader that finds resources for the supplied
     * class that may not be in the class' package. You can use this within base
     * classes by passing getClass() to load resources for a derived class.
     * 
     * @param resourceOwner
     *            is the owner of the resource
     * @return the CrossWire Class Loader
     */
    public static CWClassLoader instance(Class<?> resourceOwner) {
        return AccessController.doPrivileged(new PrivilegedLoader<CWClassLoader>(resourceOwner));
    }

    /**
     * Creates a privileged class loader that finds resources for the calling
     * class that may not be in the class' package. Use this only within classes
     * that are directly looking up their resources.
     * 
     * @return the CrossWire Class Loader
     */
    public static CWClassLoader instance() {
        Class<? extends Object> resourceOwner = CallContext.getCallingClass();
        return instance(resourceOwner);
    }

    /* (non-Javadoc)
     * @see java.lang.ClassLoader#findResource(java.lang.String)
     */
    @Override
    public URL findResource(String search) {
        URL resource = null;
        if (search == null || search.length() == 0) {
            return resource;
        }

        // First look for it with an absolute path
        // This allows developer overrides
        if (search.charAt(0) != '/') {
            resource = findResource('/' + search);
        }

        if (resource == null) {
            // Look for it in the class's package.
            String newsearch = adjustPackageSearch(search);
            if (!search.equals(newsearch)) {
                resource = findResource(newsearch);
            }
        }

        // Sometimes it comes in with '/' inside of it.
        // So look for it as a file with '.' in the name
        // This is the form that will find files in the resource.jar
        if (resource == null) {
            // Look for it in the class's package.
            String newsearch = adjustPathSearch(search);
            if (!search.equals(newsearch)) {
                resource = findResource(newsearch);
            }
        }

        // See if it can be found in a home directory
        if (resource == null) {
            URI homeResource = CWClassLoader.findHomeResource(search);
            if (homeResource != null) {
                resource = NetUtil.toURL(homeResource);
            }
        }

        // See if it can be found by its own class.
        if (resource == null) {
            resource = owner.getResource(search);
        }

        // Try the appropriate class loader
        if (resource == null) {
            resource = getClassLoader().getResource(search);
        }

        // Try the bootstrap and the system loader
        if (resource == null) {
            resource = ClassLoader.getSystemResource(search);
        }

        // For good measure let the super class try to find it.
        if (resource == null) {
            resource = super.findResource(search);
        }

        return resource;
    }

    /**
     * Prefix the search with a package prefix, if not already. Skip a leading
     * '/' if present.
     * 
     * @param aSearch the search to adjust
     * @return the adjusted search
     */
    private String adjustPackageSearch(String aSearch) {
        String search = aSearch;
        // If it has embedded '/' there is nothing to do.
        if (search.indexOf('/', 1) == -1) {
            String className = owner.getName();
            String pkgPrefix = className.substring(0, className.lastIndexOf('.') + 1);

            if (search.charAt(0) == '/') {
                String part = search.substring(1);
                if (!part.startsWith(pkgPrefix)) {
                    search = '/' + pkgPrefix + part;
                }
            } else {
                if (!search.startsWith(pkgPrefix)) {
                    search = pkgPrefix + search;
                }
            }
        }

        return search;
    }

    /**
     * Change all but a leading '/' to '.'
     * 
     * @param aSearch the search to adjust
     * @return the adjusted search
     */
    private String adjustPathSearch(String aSearch) {
        String search = aSearch;
        if (search.indexOf('/', 1) != -1) {
            // Change all but a leading '/' to '.'
            if (search.charAt(0) == '/') {
                search = '/' + search.substring(1).replace('/', '.');
            } else {
                search = search.replace('/', '.');
            }
        }
        return search;
    }

    /**
     * Pick the best class loader
     * 
     * @return the class loader
     */
    public ClassLoader getClassLoader() {
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
     * 
     * @param loader1 a class loader to consider
     * @param loader2 a class loader to consider
     * @return one of the class loaders
     */
    private static ClassLoader pickLoader(ClassLoader loader1, ClassLoader loader2) {
        ClassLoader loader = loader2;
        if (loader1 != loader2) {
            loader = loader1;
            if (loader1 == null) {
                loader = loader2;
            } else {
                // Is loader2 a descendant of loader1?
                // It is if we can walk up to the top and find it.
                for (ClassLoader curloader = loader2; curloader != null; curloader = curloader.getParent()) {
                    if (curloader == loader1) {
                        loader = loader2;
                        break;
                    }
                }
            }
        }
        return loader;
    }

    /**
     * If the application has set the homes, it will return the application's
     * requested home directory, otherwise it returns null.
     * 
     * @param i get the i-th home
     * @return Returns the home.
     */
    public static synchronized URI getHome(int i) {
        if (i > 0 && i < homes.length) {
            return homes[i];
        }
        return null;
    }

    /**
     * Establish the applications home directory from where additional resources
     * can be found. URL is expected to end with the directory name, not '/'.
     * 
     * @param newHomes
     *            The home to set.
     */
    public static synchronized void setHome(URI[] newHomes) {
        homes = new URI[newHomes.length];
        System.arraycopy(newHomes, 0, homes, 0, newHomes.length);
    }

    /**
     * Look for the resource in the home directories, returning the first
     * readable file.
     * 
     * @param search
     *            must be non-null, non-empty
     * @return the URI of the home of the resource
     */
    public static URI findHomeResource(String search) {
        if (homes == null) {
            return null;
        }

        for (int i = 0; i < homes.length; i++) {
            URI homeURI = homes[i];

            URI override = NetUtil.lengthenURI(homeURI, search);

            // Make sure the file exists and can be read
            if (NetUtil.canRead(override)) {
                return override;
            }
        }

        return null;
    }

    /**
     * PrivilegedLoader creates a CWClassLoader if it is able to obtain java
     * security permissions to do so.
     * 
     * @param <T> the type
     */
    private static class PrivilegedLoader<T> implements PrivilegedAction<T> {
        /**
         * Creates a privileged class loader that finds resources for the
         * supplied class that may not be in the class' package. You can use
         * this within base classes by passing getClass() to load resources for
         * a derived class.
         * 
         * @param resourceOwner
         *            is the owner of the resource
         */
        PrivilegedLoader(Class<?> resourceOwner) {
            owningClass = resourceOwner;
        }

        /* (non-Javadoc)
         * @see java.security.PrivilegedAction#run()
         */
        public T run() {
            return (T) new CWClassLoader(owningClass);
        }

        private Class<?> owningClass;
    }

    /**
     * The class to which the resources belong
     */
    private Class<?> owner;

    /**
     * Notion of a project's home from where additional resources can be found.
     */
    private static URI[] homes;
}
