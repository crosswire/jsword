
package org.crosswire.common.swing.config;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.Enumeration;
import java.util.Vector;

import org.crosswire.common.config.choices.AbstractChoice;
import org.crosswire.common.swing.GuiConvert;
import org.crosswire.common.swing.GuiUtil;

/**
* DefaultFontChoice allows the setting of a default font for all
* windows in the app. There is some experimental code that changes fonts
* on the fly but that is currently disabled.
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
public class DefaultFontChoice extends AbstractChoice
{
    /** Generalized read String from the Properties file */
    public String getString()
    {
        return GuiConvert.font2String(font);
    }

    /** Is this an allowed value */
    public void setString(String value)
    {
        font = GuiConvert.string2Font(value);
        // setGlobalFont(font);
    }

    /** Override this to check and note any change */
    public String getType()
    {
        return "font";
    }

    /**
    * Some help text
    */
    public String getHelpText()
    {
        return "Use this selector to make changes to the display font";
    }

    /** The font */
    private Font font = new Font("Serif", Font.PLAIN, 8);

    /**
    * Make the specified Font the current
    * @param font The Font to install
    */
    public static void setGlobalFont(Font font)
    {
        // Re-jig all the frames
        Enumeration en = windows.elements();
        while (en.hasMoreElements())
        {
            Component comp = (Component) en.nextElement();
            setFontRecurse(comp, font);
            if (comp instanceof Window)
                GuiUtil.restrainedPack((Window) comp);
        }
    }

    /**
    * Make the specified Font the current
    * @param font The Font to install
    */
    public static void setFontRecurse(Component comp, Font font)
    {
        comp.setFont(font);

        if (comp instanceof Container)
        {
            Container cntr = (Container) comp;
            Component[] comps = cntr.getComponents();

            for (int i=0; i<comps.length; i++)
            {
                setFontRecurse(comps[i], font);
            }
        }
    }

    /**
    * Add a Frame to the list that need to be updated
    * when the PLAF changes.
    * @param window The frame to be registered
    */
    public static void addWindow(Window window)
    {
        // Should we add ourselves as a ComponentListener?
        // Probably not. Knowning what is registered may
        // then be complex.

        windows.addElement(window);
        // window.addContainerListener(new CustomContainerListener());
    }

    /**
    * Remove a Frame from the list that need to be updated
    * when the PLAF changes.
    * @param frame The frame to be de-registered
    */
    public static void removeWindow(Window window)
    {
        windows.removeElement(window);
    }

    /**
    * Test
    */
    public static class CustomContainerListener implements ContainerListener
    {
        /** Constructor */
        public CustomContainerListener()
        {
        }

        /** Someone added something */
        public void componentAdded(ContainerEvent ev)
        {
            // log.fine("ADDED: "+ev.getChild().getClass());
        }

        /** Someone added something */
        public void componentRemoved(ContainerEvent ev)
        {
            // log.fine("REMOVED: "+ev.getChild().getClass());
        }
    }

    /** The frames to update */
    private static transient Vector windows = new Vector();
}
