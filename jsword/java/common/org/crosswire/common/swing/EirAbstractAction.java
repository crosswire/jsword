
package org.crosswire.common.swing;

import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
 * Generic extension of AbstractAction for JSword.
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
public abstract class EirAbstractAction extends AbstractAction
{
    /**
     * Setup the defaults
     */
    public EirAbstractAction(String name,
                             String small_icon, String large_icon,
                             String short_desc, String long_desc,
                             int mnemonic, KeyStroke accel)
    {
        if (name != null)
            putValue(Action.NAME, name);

        // For JDK1.2/1.3/1.4 compatibility
        if (large_icon != null)
            putValue("LargeIcon"/*Action.LARGE_ICON*/, getIcon(large_icon));
        if (small_icon != null)
            putValue(Action.SMALL_ICON, getIcon(small_icon));

        if (short_desc != null)
            putValue(Action.SHORT_DESCRIPTION, short_desc);
        if (long_desc != null)
            putValue(Action.LONG_DESCRIPTION, long_desc);

        if (mnemonic != -1)
            putValue("MnemonicKey"/*Action.MNEMONIC_KEY*/, new Integer(mnemonic));
        if (accel != null)
            putValue("AcceleratorKey"/*Action.ACCELERATOR_KEY*/, accel);
    }

    /**
     * Returns the Icon associated with the name from the resources.
     * The resouce should be in the path.
     * @param name Name of the icon file i.e., help16.gif
     * @return the name of the image or null if the icon is not found.
     */
    public static ImageIcon getIcon(String name)
    {
        URL url = EirAbstractAction.class.getResource(name);
        if (url != null)
        {
            return new ImageIcon(url);
        }

        // This should be log.warning() however the log may not be configured
        System.out.println("Failed to find resource name='"+name+"'");

        return null;
    }
}
