
package org.crosswire.common.swing.config;

import java.awt.Component;
import java.awt.Window;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.crosswire.common.config.choices.ClassChoices;
import org.crosswire.common.swing.GuiUtil;

/**
 * LookAndFeelChoices declares the Choices and actions
 * needed to dynamically change the look and feel (PLAF) and to add new
 * PLAFs without needing to restart.
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
public class LookAndFeelChoices extends ClassChoices
{
    /**
     * Changing the look of the config dialog
     */
    public LookAndFeelChoices() throws ClassNotFoundException
    {
        super(LookAndFeel.class, defaults);
    }

    /**
     * The Options customization
     */
    protected Class getCurrentClass()
    {
        return current;
    }

    /**
     * The Options customization
     */
    protected void setCurrentClass(Class new_class) throws InstantiationException, IllegalAccessException
    {
        current = new_class;

        LookAndFeel laf = (LookAndFeel) current.newInstance();
        setLookAndFeel(laf);
    }

    /**
     * Make the specified PLAF the current
     * @param plaf The PLAf to install
     */
    public static void setLookAndFeel(LookAndFeel plaf)
    {
        // The global new setting
        try
        {
            UIManager.setLookAndFeel(plaf);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Invalid Look and Feel name");
        }

        resetWindows();
    }

    /**
     * Make all the windows fall into line with the current look
     */
    public static void resetWindows()
    {
        // Re-jig all the frames
        Enumeration en = windows.elements();
        while (en.hasMoreElements())
        {
            Component comp = (Component) en.nextElement();
            SwingUtilities.updateComponentTreeUI(comp);

            if (comp instanceof Window)
                GuiUtil.restrainedPack((Window) comp);
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

    /** The frames to update */
    private static transient Vector windows = new Vector();

    /** The current PLAF (and the default value) */
    private static Class current = javax.swing.plaf.metal.MetalLookAndFeel.class;

    /** The default Configs */
    private static Hashtable defaults = new Hashtable();

    /**
     * Setup the defaults Hashtable
     */
    static
    {
        defaults.put("Java", "javax.swing.plaf.metal.MetalLookAndFeel");
        defaults.put("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        defaults.put("Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }
}
