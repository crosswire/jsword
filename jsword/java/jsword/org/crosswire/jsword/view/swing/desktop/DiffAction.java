
package org.crosswire.jsword.view.swing.desktop;

import java.awt.event.ActionEvent;

import org.crosswire.jsword.view.swing.book.ComparePane;

/**
 * The Bible diff tool.
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
public class DiffAction extends DesktopAbstractAction
{
    public DiffAction(Desktop tools)
    {
        super(tools,
              "Version Compare ...",
              "/toolbarButtonGraphics/media/Pause16.gif",
              "/toolbarButtonGraphics/media/Pause24.gif",
              "Compare Versions", "Compare text produced by different drivers.",
              'S', null);

        pnl_comp = new ComparePane();
    }

    public void actionPerformed(ActionEvent ev)
    {
        pnl_comp.showInDialog(getDesktop());
    }

    /** The Bible comparison tool */
    protected ComparePane pnl_comp = null;
}


