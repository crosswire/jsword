
package org.crosswire.common.swing;

import java.awt.*;
import javax.swing.*;

import org.crosswire.common.swing.*;
import org.crosswire.common.swing.data.*;

/**
 * SystemPropertiesPane displays the current values of the properties
 * file as returned by <code>System.getProperties();</code>.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
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

    /* GUI Components */
    private HashtableTableModel mdl_props = new HashtableTableModel(System.getProperties());
    private JScrollPane scr_props = new JScrollPane();
    private JTable tbl_props = new JTable();
}