
package org.crosswire.common.swing;

import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextComponent;
import java.awt.Toolkit;
import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.text.JTextComponent;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.util.Project;

/**
 * Various Gui Utilities.
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
public class GuiUtil
{
    /**
     * Returns the Icon associated with the name from the resources.
     * The resouce should be in the path.
     * @param name Name of the icon file i.e., help16.gif
     * @return the name of the image or null if the icon is not found.
     */
    public static ImageIcon getIcon(String name)
    {
        try
        {
            URL url = Project.resource().getResource(name);
            return new ImageIcon(url);
        }
        catch (MalformedURLException ex)
        {
            log.warn("Failed to find resource name='"+name+"'");
            return null;
        }
    }

    /**
     * Find the parent window.
     * @param com a component to find the frame of.
     * @return The parent Window
     */
    public static Window getWindow(Component com)
    {
        Component temp = com;

        while (!(temp instanceof Window))
        {
            temp = temp.getParent();
            if (temp == null) return null;
        }

        return (Window) temp;
    }

    /**
     * Find the parent Frame
     * @param com a component to find the frame of.
     * @return The parent Frame
     */
    public static Frame getFrame(Component com)
    {
        Component temp = com;

        while (!(temp instanceof Frame))
        {
            temp = temp.getParent();
            if (temp == null) return null;
        }

        return (Frame) temp;
    }

    /**
     * Find the parent Frame
     * @param com a component to find the frame of.
     * @return The parent Frame
     */
    public static Dialog getDialog(Component com)
    {
        Component temp = com;

        while (!(temp instanceof Dialog))
        {
            temp = temp.getParent();
            if (temp == null) return null;
        }

        return (Dialog) temp;
    }

    /**
     * Move the specified window to the centre of the screen
     * @param win The window to be moved
     */
    public static void centerWindow(Window win)
    {
        Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();

        // If the window is wider than the screen, clip it
        if (screen_dim.width < win.getSize().width)
        {
            win.setSize(screen_dim.width, win.getSize().height);
        }

        // If the window is taller than the screen, clip it
        if (screen_dim.height < win.getSize().height)
        {
            win.setSize(win.getSize().width, screen_dim.height);
        }

        // Center Frame, Dialogue or Window on screen
        int x = (screen_dim.width - win.getSize().width) / 2;
        int y = (screen_dim.height - win.getSize().height) / 2;
        win.setLocation(x, y);
    }

    /**
     * Maximize the specified window. It would be good if we could detect
     * where the taskbar was and not obscure it ...
     * @param win The window to be moved
     */
    public static void maximizeWindow(Window win)
    {
        Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
        win.setLocation(0, 0);
        win.setSize(screen_dim);
    }

    /**
     * A more restricted version of pack() for component responding to live
     * component tweaks.
     * Assuming that the window already has a sensible on screen size, do a
     * pack, but don't let the window grow or shrink by more than 10%.
     * @param win The window to be packed
     */
    public static void restrainedRePack(Window win)
    {
        Dimension orig = win.getSize();
        Dimension max = new Dimension((int) (orig.width * 1.1), (int) (orig.height * 1.1));
        Dimension min = new Dimension((int) (orig.width / 1.1), (int) (orig.height / 1.1));

        win.pack();

        // If the window is wider than 110% of its original size, clip it
        if (win.getSize().width > max.width)
        {
            win.setSize(max.width, win.getSize().height);
        }

        // If the window is taller than 110% of its original size, clip it
        if (win.getSize().height > max.height)
        {
            win.setSize(win.getSize().width, max.height);
        }

        // If the window is narrower than 90% of its original size, grow it
        if (win.getSize().width < min.width)
        {
            win.setSize(min.width, win.getSize().height);
        }

        // If the window is shorter than 90% of its original size, grow it
        if (win.getSize().height < min.height)
        {
            win.setSize(win.getSize().width, min.height);
        }

        Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();

        // If the window is wider than the screen, clip it
        if (screen_dim.width < win.getSize().width)
        {
            win.setSize(screen_dim.width, win.getSize().height);
        }

        // If the window is taller than the screen, clip it
        if (screen_dim.height < win.getSize().height)
        {
            win.setSize(win.getSize().width, screen_dim.height);
        }

        win.invalidate();
        win.validate();

        // log.log(Level.INFO, "Failure", ex);
        // log.fine("Size was "+orig);
        // log.fine("Size is "+win.getSize());
    }

    /**
     * A more restricted version of pack() when the component is being pack()ed
     * for the first time.
     * Since this is a 'first time only' pack we are only concerned with screen
     * size, and not any growths/shrinkages like restrainedRePack(Window).
     * @param win The window to be packed
     * @param maxx The maximum fraction (0.0 to 1.0) of the screen to be taken
     * up horizontally (-1 means no restrictions to the horizontal alterations)
     * @param maxy The maximum fraction (0.0 to 1.0) of the screen to be taken
     * up vertically (-1 means no restrictions to the vertical alterations)
     * @see GuiUtil#restrainedRePack(Window)
     */
    public static void restrainedPack(Window win, float maxx, float maxy)
    {
        win.pack();

        Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();

        // If the window is wider than the screen, clip it
        if (maxx != -1 && win.getSize().width > (screen_dim.width * maxx))
        {
            win.setSize((int) (screen_dim.width * maxx), win.getSize().height);
        }

        // If the window is taller than the screen, clip it
        if (maxy != -1 && win.getSize().height > (screen_dim.height * maxy))
        {
            win.setSize(win.getSize().width, (int) (screen_dim.height * maxy));
        }

        win.invalidate();
        win.validate();

        // log.log(Level.INFO, "Failure", ex);
        // log.fine("Size was "+orig);
        // log.fine("Size is "+win.getSize());
    }

    /**
     * Set the size of a component
     */
    public static void enforceMinimumSize(Component comp, int min_width, int min_height)
    {
        if (comp.getSize().width < min_width)
        {
            comp.setSize(min_width, comp.getSize().height);
        }

        if (comp.getSize().height < min_height)
        {
            comp.setSize(comp.getSize().width, min_height);
        }
    }

    /**
     * Attempts to get the text from a generic Component.
     * The Components that we can get some text from include:
     * <li> JTextComponent
     * <li> JLabel
     * <li> AbstractButton
     * <li> JComboBox
     * <li> JToolTip
     * <li> TextComponent
     * <li> Button
     * <li> Label
     * <li> JScrollPane (recurse using the View)
     * The others are done using toString()
     * @param comp The object containing the needed text.
     */
    public static String getText(Component comp)
    {
        if (comp instanceof JTextComponent)
            return ((JTextComponent) comp).getText();

        if (comp instanceof JLabel)
            return ((JLabel) comp).getText();

        if (comp instanceof AbstractButton)
            return ((AbstractButton) comp).getText();

        if (comp instanceof JComboBox)
            return ((JComboBox) comp).getSelectedItem().toString();

        if (comp instanceof JToolTip)
            return ((JToolTip) comp).getTipText();

        if (comp instanceof TextComponent)
            return ((TextComponent) comp).getText();

        if (comp instanceof Button)
            return ((Button) comp).getLabel();

        if (comp instanceof Label)
            return ((Label) comp).getText();

        if (comp instanceof JScrollPane)
        {
            JScrollPane scr = (JScrollPane) comp;
            Component sub = scr.getViewport().getView();
            if (sub != null) return getText(sub);
        }

        return comp.toString();
    }

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(GuiUtil.class);
}
