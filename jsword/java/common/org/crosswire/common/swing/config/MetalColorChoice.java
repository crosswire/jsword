
package org.crosswire.common.swing.config;

import javax.swing.plaf.metal.MetalLookAndFeel;

import org.crosswire.common.config.choices.AbstractChoice;
import org.crosswire.common.swing.CustomMetalTheme;
import org.crosswire.common.swing.GuiConvert;

/**
* The MetalColorChoice creates a Choice that
* controls the Color that Metal uses to display it's stuff.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
*/
public class MetalColorChoice extends AbstractChoice
{
    /** Read the save setting from DetailedExceptionPane */
    public String getString()
    {
        return GuiConvert.color2String(theme.getThemeColor());
    }

    /** Save the save setting to UserLevel */
    public void setString(String value)
    {
        theme.setThemeColor(GuiConvert.string2Color(value));
        MetalLookAndFeel.setCurrentTheme(theme);
        LookAndFeelChoices.resetWindows();
    }

    /** Some help text */
    public String getHelpText()
    {
        return "What Color would you like the Java Look and Feel to be based on.";
    }

    /** The component to display */
    public String getType()
    {
        return "color";
    }

    /** The component to display */
    public Object getTypeOptions()
    {
        return "Base Colour";
    }

    /** The theme that metal uses */
    private static CustomMetalTheme theme = new CustomMetalTheme();
}

