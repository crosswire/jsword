
package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * For creating a new window.
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
public class FileNewAction extends DesktopAbstractAction
{
    public FileNewAction(Desktop tools)
    {
        super(tools,
              "New Window",
              "toolbarButtonGraphics/general/New16.gif",
              "toolbarButtonGraphics/general/New24.gif",
              "New Window", "Open a new Bible View window",
              'N', KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK, false));
    }

    public void actionPerformed(ActionEvent ev)
    {
        BibleViewPane view = new BibleViewPane();

        if (!getDesktop().addBibleViewPane(view))
        {
            JOptionPane.showMessageDialog(getDesktop(), "You can't add windows in this view.\nUse the View menu to switch to MDI mode or TDI mode to view multiple passages.");
            return;
        }
    }
}
