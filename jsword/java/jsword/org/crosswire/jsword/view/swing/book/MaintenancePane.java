
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.common.config.Config;
import org.crosswire.common.config.swing.SwingConfig;
import org.crosswire.common.config.swing.TabbedConfigPane;
import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;

/**
 * Allows various maintenance procedures to be done on Bibles like
 * deletion, renaming, and viewing the notes that are associated with the
 * generation of a version.
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
public class MaintenancePane extends EirPanel
{
    /**
     * Create a new BibleMaintenance Panel
     */
    public MaintenancePane()
    {
        jbInit();
        updateButtons();
    }

    /**
     * Ensure that the buttons are enabled correctly
     */
    private void updateButtons()
    {
        boolean selected = (lst_versions.getSelectedIndex() != -1);
        btn_remove.setEnabled(selected);
        btn_props.setEnabled(selected);
        btn_rename.setEnabled(selected);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Version Maintenance", false);
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        scr_versions.getViewport().add(lst_versions, null);
        lst_versions.setModel(mdl_versions);
        lst_versions.setCellRenderer(new BibleListCellRenderer());
        lst_versions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lst_versions.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev) { updateButtons(); }
        });
        lst_versions.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent ev)
            {
                if (ev.getClickCount() == 2)
                    properties();
            }
        });

        btn_add.setText("Add ...");
        btn_add.setMnemonic('A');
        btn_add.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { add(); }
        });

        btn_remove.setText("Remove");
        btn_remove.setMnemonic('R');
        btn_remove.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { delete(); }
        });

        btn_props.setText("Properties ...");
        btn_props.setMnemonic('P');
        btn_props.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { properties(); }
        });

        btn_rename.setText("Rename");
        btn_rename.setMnemonic('R');
        btn_rename.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { rename(); }
        });

        lay_buttons.setAlignment(FlowLayout.RIGHT);
        pnl_buttons.setLayout(lay_buttons);
        pnl_buttons.add(btn_add, null);
        pnl_buttons.add(btn_remove, null);
        pnl_buttons.add(btn_props, null);
        pnl_buttons.add(btn_rename, null);

        this.setLayout(new BorderLayout());
        this.add(scr_versions, BorderLayout.CENTER);
        this.add(pnl_buttons, BorderLayout.SOUTH);
    }

    /**
     * Create a new Bible
     */
    public void add()
    {
        GeneratorPane vergen = new GeneratorPane();
        vergen.showInFrame(GuiUtil.getFrame(this));
    }

    /**
     * Rename the selected Bible
     */
    public void rename()
    {
        try
        {
            String old_name = getSelected();
            if (old_name == null)
            {
                JOptionPane.showMessageDialog(this,
                    "Please select a Bible to rename.",
                    "Delete Bible",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Object data = JOptionPane.showInputDialog(this,
                            "Enter new name for the Bible formerly known as "+old_name,
                            "Rename Bible",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            null,
                            old_name);

            if (data == null)
                return;

            String new_name = data.toString().trim();
            Bibles.renameBible(old_name, new_name);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Delete a selected Bible
     */
    public void delete()
    {
        try
        {
            String name = getSelected();
            if (name == null)
            {
                JOptionPane.showMessageDialog(this,
                    "Please select a Bible to delete.",
                    "Delete Bible",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete "+name+"?\nDeleted Bibles can not be recovered",
                "Delete Bible",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                Bibles.deleteBible(name);
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * View the properties for a selected Bible
     */
    public void properties()
    {
        // @todo: probably delete this method.
        // Note the calls to Bible.getProperties() and Bible.getPropertiesURL()
        // have been commented out because I am trying to simplify the Bible
        // interface and I'm not sure if these methods add much value and they
        // are only ever used here. This effectively makes this entire method
        // obsolete however I'm not taking it out just yet in case there is a
        // reason for it to be here.

        try
        {
            String name = getSelected();
            if (name == null)
            {
                JOptionPane.showMessageDialog(this,
                    "Please select a Bible to view notes on.",
                    "Delete Bible",
                    JOptionPane.INFORMATION_MESSAGE);

                return;
            }

            Bible version = Bibles.getBible(name);
            Config config = null; //version.getProperties();

            if (config == null)
            {
                JOptionPane.showMessageDialog(this,
                    "There are no options to configure for the "+name+" Bible.",
                    "Bible Properties",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                // The visuals for the display class
                SwingConfig.setDisplayClass(TabbedConfigPane.class);

                // Where do we save the choices?
                URL prop_url = null; //version.getPropertiesURL();

                if (prop_url == null)
                {
                    // Show the dialog, and don't bother to save.
                    SwingConfig.showDialog(config, this, new ActionListener() {
                        public void actionPerformed(ActionEvent ev) { }
                        });
                }
                else
                {
                    // Show the dialog, with a place to save to.
                    SwingConfig.showDialog(config, this, prop_url);
                }
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * What is the selected Bible name?
     * @return The version name or null if none is selected
     */
    private String getSelected()
    {
        Object selected = lst_versions.getSelectedValue();
        if (selected == null)
            return null;

        return mdl_versions.getBibleName(selected);

        /*
        // To have versions displayed as "name (driver)"
        String list = selected.toString();
        int space = list.indexOf(" ");
        return list.substring(0, space);
        */
    }

    /** The version list scroller */
    private JScrollPane scr_versions = new JScrollPane();

    /** The version list */
    private JList lst_versions = new JList();

    /** The BiblesModel for the list */
    private BiblesComboBoxModel mdl_versions = new BiblesComboBoxModel();

    /** The button bar */
    private JPanel pnl_buttons = new JPanel();

    /** View Notes button */
    private JButton btn_add = new JButton();

    /** View Notes button */
    private JButton btn_props = new JButton();

    /** Delete Bible button */
    private JButton btn_remove = new JButton();

    /** Rename Bible button */
    private JButton btn_rename = new JButton();

    /** Layout for the button bar */
    private FlowLayout lay_buttons = new FlowLayout();
}
