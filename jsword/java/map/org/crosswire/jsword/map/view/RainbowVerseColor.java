
package org.crosswire.jsword.map.view;

import java.awt.Color;

/**
 * RainbowVerseColor is a simple Genesis=Red, Revelation=Blue rainbow all
 * painted on a black background. 
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
public class RainbowVerseColor implements VerseColor
{
    /**
    * What Color should we use to represent this verse
    * @param book The book number (Gen=1, Rev=66)
    * @param chapter The chapter number
    * @param verse The verse number
    * @return The Color for this verse
    */
    public Color getColor(int book, int chapter, int verse)
    {
        if (book < 33)
        {
            book = book - 1;

            int red   = 255 - ((book*255)/(32));
            int green = ((book*255)/(32));
            int blue  = 0;

            return new Color(red, green, blue);
        }
        else if (book == 33)
        {
            return new Color(0, 255, 0);
        }
        else
        {
            book = book - 34;

            int red   = 0;
            int green = 255 - ((book*255)/(32));
            int blue  = ((book*255)/(32));

            return new Color(red, green, blue);
        }
    }

    /**
    * What Color would set off the Verses painted on it
    * @return An appropriate background color
    */
    public Color getBackground()
    {
        return Color.black;
    }

    /**
    * What Color should text be painted in
    * @return An appropriate font color
    */
    public Color getForeground()
    {
        return Color.white;
    }

    /**
    * The name for display in a combo box
    */
    public String toString()
    {
        return "Rainbow";
    }
}
