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
package org.crosswire.common.swing;

import java.awt.Color;
import java.awt.Font;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;

/**
 * Conversions between various types and Strings.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class GuiConvert
{
    /**
     * We don't want anyone doing this ...
     */
    private GuiConvert()
    {
    }

    /**
     * Convert a String to a Font
     * @param value the thing to convert
     * @return the converted data
     */
    public static Font string2Font(String value)
    {
        if (value == null || value.equals("")) //$NON-NLS-1$
        {
            return null;
        }

        String[] values = StringUtil.split(value, ","); //$NON-NLS-1$
        if (values.length != 3)
        {
            log.warn("Illegal font name: " + value); //$NON-NLS-1$
            return null;
        }

        return new Font(values[0], Integer.parseInt(values[1]), Integer.parseInt(values[2]));
    }

    /**
     * Convert a Font to a String
     * @param font the thing to convert
     * @return the converted data
     */
    public static String font2String(Font font)
    {
        if (font == null)
        {
            return ""; //$NON-NLS-1$
        }

        return font.getName() + "," + font.getStyle() + "," + font.getSize(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Convert a String to a Color
     * @param value the thing to convert
     * @return the converted data
     */
    public static Color string2Color(String value)
    {
        if (value == null || value.equals("")) //$NON-NLS-1$
        {
            return null;
        }

        if (value.length() != 7)
        {
            log.warn("Illegal colour name: " + value); //$NON-NLS-1$
            return null;
        }

        // log.fine("input=" + value);
        String red = value.substring(1, 3);
        String green = value.substring(3, 5);
        String blue = value.substring(5, 7);
        // log.fine("red=" + red + " green=" + green + " blue=" + blue);

        return new Color(Integer.parseInt(red, 16),
                         Integer.parseInt(green, 16),
                         Integer.parseInt(blue, 16));
    }

    /**
     * Convert a Color to a String
     * @param color the thing to convert
     * @return the converted data
     */
    public static String color2String(Color color)
    {
        if (color == null)
        {
            return ""; //$NON-NLS-1$
        }

        String red = "00" + Integer.toHexString(color.getRed()); //$NON-NLS-1$
        String green = "00" + Integer.toHexString(color.getGreen()); //$NON-NLS-1$
        String blue = "00" + Integer.toHexString(color.getBlue()); //$NON-NLS-1$

        red = red.substring(red.length() - 2);
        green = green.substring(green.length() - 2);
        blue = blue.substring(blue.length() - 2);

        return "#" + red + green + blue; //$NON-NLS-1$
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(GuiConvert.class);
}
