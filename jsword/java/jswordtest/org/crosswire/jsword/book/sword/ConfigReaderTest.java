package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import junit.framework.TestCase;

/**
 * JUnit test.
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class ConfigReaderTest extends TestCase
{

    public static void main(String[] args)
    {
        // A little test
        // PENGING(mark): (ask joe about a ${project.root}/misc/testdata dir - this (and many other tests) are data dependant)
        try
        {
            File f = new File("/usr/share/sword/mods.d/kjv.conf");
            FileInputStream fis = new FileInputStream(f);
            ConfigReader reader = new ConfigReader(fis);

            Iterator kit = reader.getKeys();
            while (kit.hasNext())
            {
                String key = (String) kit.next();
                Iterator vit = reader.getAllValues(key);
                while (vit.hasNext())
                {
                    String value = (String) vit.next();
                    System.out.println(key + " = " + value);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
