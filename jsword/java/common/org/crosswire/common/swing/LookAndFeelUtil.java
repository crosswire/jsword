
package org.crosswire.common.swing;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.crosswire.common.util.Logger;

/**
 * LookAndFeelUtil declares the Choices and actions
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @version $Id$
 */
public class LookAndFeelUtil
{
    /**
     * The Options customization
     */
    public static Class getLookAndFeel()
    {
        return current;
    }

    /**
     * The Options customization
     */
    public static void setLookAndFeel(Class new_class) throws InstantiationException, IllegalAccessException
    {
        current = new_class;

        LookAndFeel laf = (LookAndFeel) current.newInstance();
        setLookAndFeel(laf);
    }

    /**
     * Make the specified PLAF the current
     * @param plaf The PLAF to install
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

        updateComponents();
    }

    /**
     * Make all the windows fall into line with the current look
     */
    public static void updateComponents()
    {
        // Re-jig all the frames
        Iterator it = windows.iterator();
        while (it.hasNext())
        {
            Component comp = (Component) it.next();
            SwingUtilities.updateComponentTreeUI(comp);

            if (comp instanceof Window)
                GuiUtil.restrainedRePack((Window) comp);
        }
    }

    /**
     * Add a Component to the list that need to be updated when the L&F changes.
     * In general you will only need to add Windows to this list because the
     * changes recurse down the hierachy, however if you have Components that
     * are temporarily (whenever a L&F change could occur) not part of this
     * hierachy then add them in also so they get updated with everything else.
     * when the PLAF changes.
     * @param window The frame to be registered
     */
    public static void addComponentToUpdate(Component comp)
    {
        // Should we add ourselves as a ComponentListener?
        // Probably not. Knowning what is registered may
        // then be complex.

        windows.add(comp);
        SwingUtilities.updateComponentTreeUI(comp);
        // window.addContainerListener(new CustomContainerListener());
    }

    /**
     * Remove a Frame from the list that need to be updated
     * when the PLAF changes.
     * @param frame The frame to be de-registered
    */
    public static void removeComponentToUpdate(Component comp)
    {
        windows.remove(comp);
    }

    /** The frames to update */
    private static transient List windows = new ArrayList();

    /** The current PLAF (and the default value) */
    private static Class current;

    /** The default Configs */
    //private static Hashtable defaults = new Hashtable();

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(LookAndFeelUtil.class);

    /**
     * Setup the defaults Hashtable
     */
    static
    { 
    	// try to set the default look and feel to the system default
    	try
    	{
			current = Class.forName(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException ex) 
		{
			log.warn("Failed to initialise system default LAF", ex);
			current = javax.swing.plaf.metal.MetalLookAndFeel.class;
		}
        /*
        Class[] impls = Project.resource().getImplementors(LookAndFeel.class);
        for (int i=0; i<impls.length; i++)
        {
            LookAndFeel lnf = (LookAndFeel) impls[i].newInstance();
            defaults.put(lnf.getName(), impls[i].getName());
        }
        */
    }
}
