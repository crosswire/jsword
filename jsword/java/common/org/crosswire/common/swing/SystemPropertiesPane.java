
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.crosswire.common.swing.data.HashtableTableModel;

/**
 * SystemPropertiesPane displays the current values of the properties
 * file as returned by <code>System.getProperties();</code>.
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
public class SystemPropertiesPane extends EirPanel
{
    /**
     * Basic Constructor
     */
    public SystemPropertiesPane()
    {
        jbInit();
    }

    /**
     * Build the GUI
     */
    private void jbInit()
    {
        scr_props.getViewport().add(tbl_props, null);
        tbl_props.setModel(mdl_props);

        this.setLayout(new BorderLayout());
        this.add(scr_props, BorderLayout.CENTER);
    }

    /**
     * Show this compnent in a new dialog;
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "System Properties", true);
    }

    /**
     * Create an 'open' Action
     */
    public static Action createOpenAction(Component parent)
    {
        return new OpenAction(parent);
    }

    /**
     * An Action to open a new SystemPropertiesPane
     */
    public static class OpenAction extends ComponentAbstractAction
    {
        public OpenAction(Component comp)
        {
            super(comp,
                  "System Information ...",
                  "/toolbarButtonGraphics/general/Information16.gif",
                  "/toolbarButtonGraphics/general/Information24.gif",
                  "System Information", "Display system configuration information.",
                  'I', null);
        }
    
        public void actionPerformed(ActionEvent ev)
        {
            SystemPropertiesPane pnl_props = new SystemPropertiesPane();
            pnl_props.showInDialog(getComponent());
        }
    }

    /* GUI Components */
    private HashtableTableModel mdl_props = new HashtableTableModel(System.getProperties());
    private JScrollPane scr_props = new JScrollPane();
    private JTable tbl_props = new JTable();
}
