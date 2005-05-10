/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * .
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class FileUtil
{
    /**
     * Prevent Instansiation
     */
    private FileUtil()
    {
    }

    /**
     * Deletes a file or a directory and all of its contents
     * @param file or directory to delete
     * @return the list of files that could not be deleted
     */
    public static List delete(File file)
    {
        List failures = new ArrayList();
        if (file.isDirectory())
        {
            deleteContents(file, failures);
        }
        if (!file.delete())
        {
            failures.add(file);
        }
        return failures;
    }

    /**
     * Recursive delete files.
     * @param dirPath  directory of files to delete
     * @param failures the list of files that could not be deleted
    */
    private static void deleteContents(File dirPath, List failures)
    {
        String[] ls = dirPath.list();

        for (int idx = 0; idx < ls.length; idx++)
        {
            File file = new File(dirPath, ls[idx]);
            if (file.isDirectory())
            {
                deleteContents(file, failures);
            }
            if (!file.delete())
            {
                failures.add(file);
            }
        }
    }

    /**
     * Extension for java files
     */
    public static final String EXTENSION_JAVA = ".java"; //$NON-NLS-1$

    /**
     * Extension for properties files
     */
    public static final String EXTENSION_PROPERTIES = ".properties"; //$NON-NLS-1$

    /**
     * Extension for XSLT files
     */
    public static final String EXTENSION_XSLT = ".xsl"; //$NON-NLS-1$

    /**
     * Extension for XML files
     */
    public static final String EXTENSION_XML = ".xml"; //$NON-NLS-1$

    /**
     * Modes for opening random access files
     */
    public static final String MODE_READ = "r"; //$NON-NLS-1$

    /**
     * Modes for opening random access files
     */
    public static final String MODE_WRITE = "rw"; //$NON-NLS-1$
}
