
package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * close the current passage window.
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
 * @version $Id$
 */
public class FileCloseAction extends DesktopAbstractAction
{
    /**
     * Setup configuration
     */
    public FileCloseAction(Desktop tools)
    {
        super(tools,
              "Close",
              null,
              null,
              "Close Passages", "Close the current passage.",
              'C', KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_MASK, false));
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev)
    {
        BibleViewPane view = getDesktop().getSelectedBibleViewPane();
        getDesktop().removeBibleViewPane(view);
    }
}
