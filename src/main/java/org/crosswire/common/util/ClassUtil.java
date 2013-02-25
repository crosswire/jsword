/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.common.util;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.LoggerFactory;

/**
 * Various Java Class Utilities.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class ClassUtil {
    /**
     * Prevent instantiation
     */
    private ClassUtil() {
    }

    /**
     * Gets the Class for the className in a way that works well for extensions.
     * See: http://www.javageeks.com/Papers/ClassForName/ClassForName.pdf
     * 
     * @param className
     *            the class to get
     * @return the found Class
     * @throws ClassNotFoundException
     */
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    /**
     * This function finds the first matching filename for a Java class file
     * from the classpath, if none is found it returns null.
     */
    public static String findClasspathEntry(String classname, String classpath) {
        String full = null;

        String[] paths = StringUtil.split(classpath, File.pathSeparator);
        for (int i = 0; i < paths.length; i++) {
            // Search the jar
            if (paths[i].endsWith(EXTENSION_ZIP) || paths[i].endsWith(EXTENSION_JAR)) {
                ZipFile zip = null;
                try {
                    String fileName = classname.replace(',', '/') + EXTENSION_CLASS;
                    zip = new ZipFile(paths[i]);
                    ZipEntry entry = zip.getEntry(fileName);

                    if (entry != null && !entry.isDirectory()) {
                        if (full != null && !full.equals(fileName)) {
                            log.warn("Warning duplicate {} found: {} and {}", classname, full, paths[i]);
                        } else {
                            full = paths[i];
                        }
                    }
                } catch (IOException ex) {
                    // If that zip file failed, then ignore it and move on.
                    log.warn("Missing zip file for {} and {}", classname, paths[i]);
                } finally {
                    IOUtil.close(zip);
                }
            } else {
                StringBuilder path = new StringBuilder(256);

                // Search for the file
                String extra = classname.replace('.', File.separatorChar);

                path.append(paths[i]);
                if (paths[i].charAt(paths[i].length() - 1) != File.separatorChar) {
                    path.append(File.separatorChar);
                }

                path.append(extra);
                path.append(EXTENSION_CLASS);
                String fileName = path.toString();

                if (new File(fileName).isFile()) {
                    if (full != null && !full.equals(fileName)) {
                        log.warn("Warning duplicate {} found: {} and {}", classname, full, paths[i]);
                    } else {
                        full = paths[i];
                    }
                }
            }
        }

        return full;
    }

    /**
     * This function find the first matching filename for a Java class file from
     * the classpath, if none is found it returns null.
     */
    public static String findClasspathEntry(String classname) {
        String classpath = System.getProperty("java.class.path", "");
        return findClasspathEntry(classname, classpath);
    }

    /**
     * <p>
     * Gets the class name minus the package name for an <code>Object</code>.
     * </p>
     * 
     * @param object
     *            the class to get the short name for, may be null
     * @param valueIfNull
     *            the value to return if null
     * @return the class name of the object without the package name, or the
     *         null value
     */
    public static String getShortClassName(Object object, String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortClassName(object.getClass().getName());
    }

    /**
     * <p>
     * Gets the class name minus the package name from a <code>Class</code>.
     * </p>
     * 
     * @param cls
     *            the class to get the short name for, must not be
     *            <code>null</code>
     * @return the class name without the package name
     * @throws IllegalArgumentException
     *             if the class is <code>null</code>
     */
    public static String getShortClassName(Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }
        return getShortClassName(cls.getName());
    }

    /**
     * <p>
     * Gets the class name minus the package name from a String.
     * </p>
     * 
     * <p>
     * The string passed in is assumed to be a class name - it is not checked.
     * </p>
     * 
     * @param className
     *            the className to get the short name for, must not be empty or
     *            <code>null</code>
     * @return the class name of the class without the package name
     * @throws IllegalArgumentException
     *             if the className is empty
     */
    public static String getShortClassName(String className) {
        if (className == null || className.length() == 0) {
            throw new IllegalArgumentException("The class name must not be empty");
        }
        char[] chars = className.toCharArray();
        int lastDot = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == PACKAGE_SEPARATOR_CHAR) {
                lastDot = i + 1;
            } else if (chars[i] == INNER_CLASS_SEPARATOR_CHAR) {
                chars[i] = PACKAGE_SEPARATOR_CHAR;
            }
        }
        return new String(chars, lastDot, chars.length - lastDot);
    }

    /**
     * <p>
     * The package separator character: <code>&#x2e;</code>.
     * </p>
     */
    private static final char PACKAGE_SEPARATOR_CHAR = '.';

    /**
     * <p>
     * The inner class separator character: <code>$</code>.
     * </p>
     */
    private static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    private static final String EXTENSION_CLASS = ".class";
    private static final String EXTENSION_JAR = ".jar";
    private static final String EXTENSION_ZIP = ".zip";

    /**
     * The log stream
     */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ClassUtil.class);
}
