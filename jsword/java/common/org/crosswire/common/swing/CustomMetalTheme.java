
package org.crosswire.common.swing;

import java.awt.*;

import javax.swing.plaf.metal.*;
import javax.swing.plaf.*;
import javax.swing.*;

/**
* This class describes a customizable default Metal Theme.
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
public class CustomMetalTheme extends MetalTheme
{
    public CustomMetalTheme()
    {
        // note the properties listed here can currently be used by people
        // providing runtimes to hint what fonts are good.  For example the bold
        // dialog font looks bad on a Mac, so Apple could use this property to hint at a good font.
        // However, we don't promise to support these forever.  We may move to getting these
        // from the swing.properties file, or elsewhere.

        try
        {
            control_font = new FontUIResource(Font.getFont("swing.plaf.metal.controlFont", new Font("Dialog", Font.BOLD, 12)));
            system_font = new FontUIResource(Font.getFont("swing.plaf.metal.systemFont", new Font("Dialog", Font.PLAIN, 12)));
            user_font = new FontUIResource(Font.getFont("swing.plaf.metal.userFont", new Font("Dialog", Font.PLAIN, 12)));
            small_font = new FontUIResource(Font.getFont("swing.plaf.metal.smallFont", new Font("Dialog", Font.PLAIN, 10)));
        }
        catch (Exception ex)
        {
            control_font = new FontUIResource("Dialog", Font.BOLD, 12);
            system_font =  new FontUIResource("Dialog", Font.PLAIN, 12);
            user_font =  new FontUIResource("Dialog", Font.PLAIN, 12);
            small_font = new FontUIResource("Dialog", Font.PLAIN, 10);
        }
    }

    public String getName()
    {
        return "Stainless Steel";
    }

    public void setThemeColor(Color base3)
    {
        Color base2 = base3.darker();
        Color base1 = base2.darker();

        primary1 = new ColorUIResource(base1.getRed(), base1.getGreen(), base1.getBlue());
        primary2 = new ColorUIResource(base2.getRed(), base2.getGreen(), base2.getBlue());
        primary3 = new ColorUIResource(base3.getRed(), base3.getGreen(), base3.getBlue());
    }

    public Color getThemeColor()
    {
        return primary3;
    }

    // these are blue in Metal Default Theme
    protected ColorUIResource getPrimary1()     { return primary1; }
    protected ColorUIResource getPrimary2()     { return primary2; }
    protected ColorUIResource getPrimary3()     { return primary3; }

    // these are gray in Metal Default Theme
    protected ColorUIResource getSecondary1()   { return secondary1; }
    protected ColorUIResource getSecondary2()   { return secondary2; }
    protected ColorUIResource getSecondary3()   { return secondary3; }

    public FontUIResource getControlTextFont()  { return control_font; }
    public FontUIResource getSystemTextFont()   { return system_font; }
    public FontUIResource getUserTextFont()     { return user_font; }
    public FontUIResource getMenuTextFont()     { return control_font; }
    public FontUIResource getWindowTitleFont()  { return control_font; }
    public FontUIResource getSubTextFont()      { return small_font; }

    private ColorUIResource primary1 = new ColorUIResource(102, 102, 153);
    private ColorUIResource primary2 = new ColorUIResource(153, 153, 204);
    private ColorUIResource primary3 = new ColorUIResource(204, 204, 255);

    private final ColorUIResource secondary1 = new ColorUIResource(102, 102, 102);
    private final ColorUIResource secondary2 = new ColorUIResource(153, 153, 153);
    private final ColorUIResource secondary3 = new ColorUIResource(204, 204, 204);

    private FontUIResource control_font;
    private FontUIResource system_font;
    private FontUIResource user_font;
    private FontUIResource small_font;
}
