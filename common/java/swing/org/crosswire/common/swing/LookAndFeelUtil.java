package org.crosswire.common.swing;

import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author DM Smith [dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public class LookAndFeelUtil
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
            UIManager.setLookAndFeel(systemLAF);
            defaultLAF = Class.forName(systemLAF);
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
    }
}
