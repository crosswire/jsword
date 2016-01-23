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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * .
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class FileUtil {
    /**
     * Prevent instantiation
     */
    private FileUtil() {
    }

    /**
     * Deletes a file or a directory and all of its contents
     * 
     * @param file
     *            or directory to delete
     * @return the list of files that could not be deleted
     */
    public static List<File> delete(File file) {
        List<File> failures = new ArrayList<File>();
        if (file.isDirectory()) {
            deleteContents(file, failures);
        }
        if (!file.delete()) {
            failures.add(file);
        }
        return failures;
    }

    /**
     * Recursive delete files.
     * 
     * @param dirPath
     *            directory of files to delete
     * @param failures
     *            the list of files that could not be deleted
     */
    private static void deleteContents(File dirPath, List<File> failures) {
        String[] ls = dirPath.list();

        for (int idx = 0; idx < ls.length; idx++) {
            File file = new File(dirPath, ls[idx]);
            if (file.isDirectory()) {
                deleteContents(file, failures);
            }
            if (!file.delete()) {
                failures.add(file);
            }
        }
    }

    /**
     * Extension for java files
     */
    public static final String EXTENSION_JAVA = ".java";

    /**
     * Extension for properties files
     */
    public static final String EXTENSION_PROPERTIES = ".properties";

    /**
     * Extension for plug-in files
     */
    public static final String EXTENSION_PLUGIN = ".plugin";

    /**
     * Extension for XSLT files
     */
    public static final String EXTENSION_XSLT = ".xsl";

    /**
     * Extension for XML files
     */
    public static final String EXTENSION_XML = ".xml";

    /**
     * Modes for opening random access files
     */
    public static final String MODE_READ = "r";

    /**
     * Modes for opening random access files
     */
    public static final String MODE_WRITE = "rw";
}
