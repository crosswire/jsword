
package org.crosswire.common.swing;

import java.awt.Color;
import java.awt.Font;

import org.apache.commons.lang.StringUtils;

/**
 * Conversions between various types and Strings.
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
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class GuiConvert
{
    /**
    * We don't want anyone doing this ...
    */
    private GuiConvert()
    {
    }

    /**
    * Convert a String to a Font
    * @param data the thing to convert
    * @return the converted data
    */
    public static Font string2Font(String value)
    {
        if (value == null || value.equals("")) return null;

        String[] values = StringUtils.split(value, ",");
        return new Font(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2]));
    }

    /**
    * Convert a Font to a String
    * @param data the thing to convert
    * @return the converted data
    */
    public static String font2String(Font font)
    {
        if (font == null || font.equals("")) return "";

        return font.getName()+","+font.getStyle()+","+font.getSize();
    }

    /**
    * Convert a String to a Color
    * @param data the thing to convert
    * @return the converted data
    */
    public static Color string2Color(String value)
    {
        if (value == null || value.equals("")) return null;

        // log.fine("input="+value);
        String red = value.substring(1, 3);
        String green = value.substring(3, 5);
        String blue = value.substring(5, 7);
        // log.fine("red="+red+" green="+green+" blue="+blue);

        return new Color(Integer.parseInt(red, 16),
                         Integer.parseInt(green, 16),
                         Integer.parseInt(blue, 16));
    }

    /**
    * Convert a Color to a String
    * @param data the thing to convert
    * @return the converted data
    */
    public static String color2String(Color color)
    {
        if (color == null) return "";

        String red = "00" + Integer.toHexString(color.getRed());
        String green = "00" + Integer.toHexString(color.getGreen());
        String blue = "00" + Integer.toHexString(color.getBlue());

        red = red.substring(red.length()-2);
        green = green.substring(green.length()-2);
        blue = blue.substring(blue.length()-2);

        return "#"+red+green+blue;
    }
}

