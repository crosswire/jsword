
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.crosswire.common.progress.swing.JobsViewPane;
import org.crosswire.common.swing.ComponentAbstractAction;
import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.swing.ExceptionShelf;

/**
 * AdvancedToolsPane is a window that contains various advanced user tools in
 * one place.
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
public class AdvancedToolsPane extends EirPanel
{
    /**
     * Basic constructor
     */
    public AdvancedToolsPane()
    {
        init();
    }

    /**
     * Build the GUI components
     */
    private void init()
    {
        pnl_hshelf.add(pnl_shelf, BorderLayout.NORTH);
        pnl_hshelf.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        tab_main.add(pnl_jobs, "Running Tasks");
        tab_main.add(pnl_hshelf, "Errors");
        //tab_main.add(pnl_logs, "Logs");

        this.setLayout(new BorderLayout(5, 5));
        this.add(tab_main, BorderLayout.CENTER);
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
        public OpenAction(Component comp)
        {
            super(comp,
                  "Advanced Tools ...",
                  "toolbarButtonGraphics/general/History16.gif",
                  "toolbarButtonGraphics/general/History24.gif",
                  "Show the advanced tools dialog", "Investigate tasks, errors and logs",
                  'V', null);
        }
    
        public void actionPerformed(ActionEvent ev)
        {
            AdvancedToolsPane atp = new AdvancedToolsPane();
            atp.showInDialog(getComponent());
        }
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Advanced Tools", false);
    }

    private ExceptionShelf pnl_shelf = new ExceptionShelf();
    private JPanel pnl_hshelf = new JPanel();
    private JobsViewPane pnl_jobs = new JobsViewPane();
    private JTabbedPane tab_main = new JTabbedPane();
    //private JPanel pnl_logs = new JPanel();
}
