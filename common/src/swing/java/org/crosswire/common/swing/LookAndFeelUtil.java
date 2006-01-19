/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
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

import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.crosswire.common.swing.plaf.MetalLFCustoms;
import org.crosswire.common.swing.plaf.OtherLFCustoms;
import org.crosswire.common.swing.plaf.WindowsLFCustoms;

/**
 * LookAndFeelUtil declares the Choices and actions
 * needed to dynamically change the look and feel (PLAF) and to add new
 * PLAFs without needing to restart.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @author Willie Thean [williethean at yahoo dot com]
 */
public final class LookAndFeelUtil
{
    /**
     * Prevent Instansiation
     */
    private LookAndFeelUtil()
    {
    }

    /**
     * Establish the system look and feel
     */
    public static void initialize()
    {
        // Calling any method in this package will force the
        // static initializer to be called.
    }

    /**
     * The Options customization
     */
    public static Class getLookAndFeel()
    {
        if (currentLAF == null)
        {
            return defaultLAF;
        }
        return currentLAF;
    }

    /**
     * Set the look and feel to a new class.
     */
    public static void setLookAndFeel(Class newLaFClass) throws InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
    {
        LookAndFeel laf = (LookAndFeel) newLaFClass.newInstance();

        // newLaFClass is null if the user enters a bogus value
        if (currentLAF != null && !currentLAF.equals(newLaFClass))
        {
            JOptionPane.showMessageDialog(null, Msg.PLAF_CHANGE);
        }
        else
        {
            UIManager.setLookAndFeel(laf);
        }

        currentLAF = newLaFClass;
    }

    /**
     * The current PLAF
     */
    private static Class currentLAF;

    /**
     * The default PLAF (and the default value)
     */
    private static Class defaultLAF;

    /**
     * Setup the default PLAF
     */
    static
    {
        defaultLAF = MetalLookAndFeel.class;
        String systemLAF = UIManager.getSystemLookAndFeelClassName();
        try
        {
            // GTKLookAndFeel and MotiflLookAndFeel doesn't look good currently.
            // Only WindowsLookAndFeel and MetalLookAndFeel looks fine.
            // Not sure how AquaLookAndFeel looks, so allow it for now.
            // NOTE(DM): test with the gtk laf before allowing it.
            if (systemLAF.indexOf("WindowsLookAndFeel") != -1 ||  //$NON-NLS-1$
                systemLAF.indexOf("AquaLookAndfeel") != -1) //$NON-NLS-1$
            {
                UIManager.setLookAndFeel(systemLAF);
                // MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); //$NON-NLS-1$
                // UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                defaultLAF = Class.forName(systemLAF);
            }
        }
        catch (ClassNotFoundException e)
        {
            assert false;
        }
        catch (InstantiationException e)
        {
            assert false;
        }
        catch (IllegalAccessException e)
        {
            assert false;
        }
        catch (UnsupportedLookAndFeelException e)
        {
            assert false;
        }

        customizeBDLookandFeel();
    }

    private static void customizeBDLookandFeel()
    {
        String currentLF = UIManager.getLookAndFeel().getClass().getName();

        if (currentLF.indexOf("MetalLookAndFeel") != -1) //$NON-NLS-1$
        {
            new MetalLFCustoms().initUIDefaults();
        }
        else if (currentLF.indexOf("WindowsLookAndFeel") != -1) //$NON-NLS-1$
        {
            new WindowsLFCustoms().initUIDefaults();
        }
        else
        {
            new OtherLFCustoms().initUIDefaults();
        }
    }
}
