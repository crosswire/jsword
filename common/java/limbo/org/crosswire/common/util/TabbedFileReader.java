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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * A TabbedFileReader reads a file consisting of lines with
 * tab separated columns.
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
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public class TabbedFileReader
{
    /**
     * This is a utility class, so it should not be constructed.
     *
     */
    private TabbedFileReader()
    {
    }

    /**
     * Process all the lines in the file.
     * @param fileName java.lang.String
     * @param columns int
     * @param lp lineProcessor
     * @throws IOException
     */
    // Ideas: Add an array of index positions giving the columns that are useful
    // Add an array of class types that the string is to become
    public static void read(String fileName, int columns, RowProcessor lp) throws IOException
    {
        URL fileURL = ResourceUtil.getResource(fileName);
        FileReader reader = null;
        try
        {
            reader = new FileReader(fileURL.getFile());
        }
        catch (IOException ex1)
        {
            reader = new FileReader(fileName);
        }

        // open the file
        BufferedReader in = new BufferedReader(reader);

        Object[] row = new Object[columns];

        // read the file a line at a time and send it to the
        // processor for processing
        while (true)
        {
            String line = in.readLine();
            if (line == null)
            {
                break;
            }

            // Split it on tabs
            int previousLoc = 0;
            int lastColumn = columns - 1;
            for (int col = 0; col < lastColumn; col++)
            {
                int loc = line.indexOf('\t', previousLoc);
                if (loc == -1)
                {
                    throw new ArrayIndexOutOfBoundsException();
                }
                row[col] = line.substring(previousLoc, loc);
                previousLoc = loc + 1;
            }
            row[lastColumn] = line.substring(previousLoc);
            lp.process(row);
        }
        // close the file
        in.close();
    }

}
