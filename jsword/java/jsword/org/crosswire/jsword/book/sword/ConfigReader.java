package org.crosswire.jsword.book.sword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A utility class for loading Sword module config data.
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @version $Id$
 */
public class ConfigReader
{
    private BufferedReader bufferedReader;
    private Map table = new HashMap();

    public ConfigReader(InputStream is) throws IOException
    {
        InputStreamReader r = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(r);
        this.bufferedReader = br;

        String currentLine = "";
        while (currentLine != null)
        {
            currentLine = bufferedReader.readLine();
            if (currentLine != null)
                parseLine(currentLine);
        }
    }

    /**
     * Method parseLine.
     * @param currentLine
     */
    private void parseLine(String currentLine)
    {
        // don't use a tokenizer - a value may have an =
        int eqIdx = currentLine.indexOf('=');
        if (eqIdx >= 0 && eqIdx < currentLine.length() - 1)
        {
            String key = currentLine.substring(0, eqIdx).trim();
            if (key.length() > 0)
            {
                String value = currentLine.substring(eqIdx + 1);
                // check to see if there already values for this key...
                ArrayList list = (ArrayList) table.get(key);
                if (list != null)
                {
                    list.add(value);
                }
                else
                {
                    list = new ArrayList();
                    list.add(value);
                    table.put(key, list);
                }
            }
        }
    }

    /**
     * Returns an Enumeration of all the keys found in the config file.
     */
    public Iterator getKeys()
    {
        return table.keySet().iterator();
    }

    /**
     * Returns only one value for the key (for cases where only one value is expected).
     */
    public String getFirstValue(String key)
    {
        ArrayList list = (ArrayList) table.get(key);
        if (list == null)
        {
            return null;
        }

        return (String) list.get(0);
    }

    /**
     * Returns all values for the key (for cases where many values are expected).
     */
    public Iterator getAllValues(String key)
    {
        ArrayList list = (ArrayList) table.get(key);
        if (list == null)
        {
            return Collections.EMPTY_LIST.iterator();
        }

        return list.iterator();
    }

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
