package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;

/**
 * A Simple pane to hold log messages to aid debugging.
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
public class LogPane extends EirPanel
{
    /**
     * Create a fault log window
     */
    public LogPane()
    {
        init();
    }

    /**
     * Create the GUI
     */
    private void init()
    {
        pnl_shelf = new ExceptionShelf();

        this.setLayout(new BorderLayout());
        this.add(pnl_shelf, BorderLayout.NORTH);
        //pnl_log.add(pnl_status, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * Create an 'open' Action
     */
    public static Action createOpenAction(Component parent)
    {
        return new OpenAction(parent);
    }

    /**
     * An Action to open a new one of these
     */
    public static class OpenAction extends ComponentAbstractAction
    {
        /**
         * Simple ctor
         */
        public OpenAction(Component comp)
        {
            super(comp,
                  "Problem History ...",
                  "toolbarButtonGraphics/general/History16.gif",
                  "toolbarButtonGraphics/general/History24.gif",
                  "Problem History", "Display list of captured problems.",
                  'P', null);
        }
    
        public void actionPerformed(ActionEvent ev)
        {
            LogPane pnl_log = new LogPane();
            pnl_log.showInDialog(getComponent());
        }
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Fault Log", false);
    }

    private ExceptionShelf pnl_shelf = null;
}
