
package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.crosswire.jsword.view.swing.book.BibleViewPane;

/**
 * close all the passage windows.
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
public class FileCloseAllAction extends DesktopAbstractAction
{
    public FileCloseAllAction(Desktop tools)
    {
        super(tools,
              "Close All",
              null,
              null,
              "Close All Passages", "Close all the passages.",
              'L', null);
    }

    public void actionPerformed(ActionEvent ev)
    {
        Iterator it = getDesktop().iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            if (!getDesktop().removeBibleViewPane(view))
            {
                JOptionPane.showMessageDialog(getDesktop(), "You must switch to MDI or TDI to close a passage window.");
                return;
            }
        }
    }
}
