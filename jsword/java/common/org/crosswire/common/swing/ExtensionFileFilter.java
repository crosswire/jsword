package org.crosswire.common.swing;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * ExtensionFileFilter.
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
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class ExtensionFileFilter extends FileFilter
{
    /**
     * Basic constructor
     * @param extensions An array of allowed extensions without the .
     */
    public ExtensionFileFilter(String[] extensions)
    {
        this.extensions = extensions;
    }

    /**
     * Basic constructor
     * @param extensions An array of allowed extensions without the .
     * @param desc The description of this filter
     */
    public ExtensionFileFilter(String[] extensions, String desc)
    {
        this.desc = desc;
        this.extensions = extensions;
    }

    /**
     * Is the given file valid?
     * @param file The object to test
     */
    public boolean accept(File file)
    {
        if (file.isDirectory())
        {
            return true;
        }

        String extension = getExtension(file);

        for (int i=0; i<extensions.length; i++)
        {
            if (extension.equals(extensions[i]))
            {
                return true;
            }
        }

        return false;
    }

    /**
     *
     */
    public String getDescription()
    {
        if (desc != null)
        {
            return desc;
        }

        StringBuffer buff = new StringBuffer("("); //$NON-NLS-1$
        for (int i=0; i<extensions.length; i++)
        {
            if (i != 0)
            {
                buff.append(", "); //$NON-NLS-1$
            }

            buff.append(extensions[i]);
        }

        buff.append(")"); //$NON-NLS-1$

        return buff.toString();
    }

    /**
     * Get the extension of a file.
     */
    public static String getExtension(File file)
    {
        String name = file.getName();
        int idx = name.lastIndexOf('.');

        if (idx > 0 && idx < name.length()-1)
        {
            return name.substring(idx+1).toLowerCase();
        }

        return ""; //$NON-NLS-1$
    }

    /**
     * The description of this filter
     */
    private String desc;

    /**
     * The allowed files
     */
    private String[] extensions;
}
