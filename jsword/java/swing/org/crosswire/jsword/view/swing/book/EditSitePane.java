package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.IntrospectionException;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.install.InstallerFactory;

/**
 * An editor for the list of available update sites.
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
public class EditSitePane extends JPanel
{
    /**
     * This is the default constructor
     */
    public EditSitePane(InstallManager imanager)
    {
        this.imanager = imanager;

        mdlSite = new InstallManagerListModel(imanager);
        mdlType = new InstallerFactoryComboBoxModel(imanager);

        initialize();
        setState(STATE_DISPLAY, null);
        select();
    }

    /**
     * GUI init
     */
    private void initialize()
    {
        scrSite.add(lstSite, null);
        scrSite.getViewport().add(lstSite, null);
        lstSite.setModel(mdlSite);
        lstSite.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSite.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                select();
            }
        });

        btnAdd.setText("Add");
        btnAdd.setMnemonic('A');
        btnAdd.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                add();
            }
        });

        btnEdit.setText("Edit");
        btnEdit.setMnemonic('E');
        btnEdit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                edit();
            }
        });

        btnDelete.setText("Delete");
        btnDelete.setMnemonic('D');
        btnDelete.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                delete();
            }
        });

        pnlBtn1.add(btnAdd, null);
        pnlBtn1.add(btnEdit, null);
        pnlBtn1.add(btnDelete, null);

        pnlSite.setLayout(new BorderLayout());
        pnlSite.add(scrSite, BorderLayout.CENTER);
        pnlSite.add(pnlBtn1, BorderLayout.SOUTH);

        lblName.setText("Name:");
        lblName.setDisplayedMnemonic('N');
        lblName.setLabelFor(txtName);
        txtName.setColumns(10);
        txtName.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent ev)
            {
                siteUpdate();
            }

            public void insertUpdate(DocumentEvent ev)
            {
                siteUpdate();
            }

            public void removeUpdate(DocumentEvent ev)
            {
                siteUpdate();
            }
        });

        lblType.setText("Type:");
        lblType.setDisplayedMnemonic('Y');
        lblType.setLabelFor(cboType);
        cboType.setEditable(false);
        cboType.setModel(mdlType);
        cboType.setSelectedIndex(0);
        cboType.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                newType();
            }
        });

        lblMesg.setText(" ");

        btnReset.setText("Cancel");
        btnReset.setMnemonic('C');
        btnReset.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                reset();
            }
        });

        btnSave.setText("Save");
        btnSave.setMnemonic('V');
        btnSave.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                save();
            }
        });

        pnlBtn2.add(btnSave, null);
        pnlBtn2.add(btnReset, null);

        pnlMain.setLayout(new GridBagLayout());
        pnlMain.add(lblMesg, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        pnlMain.add(lblName, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        pnlMain.add(txtName, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
        pnlMain.add(lblType, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        pnlMain.add(cboType, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
        pnlMain.add(sepLine, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        pnlMain.add(pnlBean, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        pnlMain.add(pnlBtn2, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        sptMain.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptMain.setResizeWeight(0.0);
        sptMain.add(pnlSite, JSplitPane.LEFT);
        sptMain.add(pnlMain, JSplitPane.RIGHT);

        this.setLayout(new BorderLayout());
        this.add(sptMain, BorderLayout.CENTER);

        btnClose.setText("OK");
        btnClose.setMnemonic('O');
        btnClose.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                close();
            }
        });

        pnlAction.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlAction.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnlAction.add(btnClose, null);
    }

    /**
     * Open us in a new modal dialog window
     * @param parent The component to which to attach the new dialog
     */
    public void showInDialog(Component parent)
    {
        dlgMain = new JDialog(JOptionPane.getFrameForComponent(parent));

        ActionListener closer = new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                close();
            }
        };

        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        dlgMain.getContentPane().setLayout(new BorderLayout());
        dlgMain.getContentPane().add(new JPanel(), BorderLayout.NORTH);
        dlgMain.getContentPane().add(pnlAction, BorderLayout.SOUTH);
        dlgMain.getContentPane().add(this, BorderLayout.CENTER);
        dlgMain.getContentPane().add(new JPanel(), BorderLayout.EAST);
        dlgMain.getContentPane().add(new JPanel(), BorderLayout.WEST);
        dlgMain.getRootPane().setDefaultButton(btnClose);
        dlgMain.getRootPane().registerKeyboardAction(closer, esc, JComponent.WHEN_IN_FOCUSED_WINDOW);
        dlgMain.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlgMain.setTitle("Edit Update Sites");
        dlgMain.setModal(true);

        GuiUtil.restrainedPack(dlgMain, 0.5f, 0.75f);
        GuiUtil.centerWindow(dlgMain);
        dlgMain.setVisible(true);
    }

    /**
     * Close the window, and save the install manager state
     */
    protected void close()
    {
        imanager.save();
        dlgMain.dispose();
    }

    /**
     * The name field has been updated, so we need to check the entry is valid
     */
    protected void siteUpdate()
    {
        if (txtName.isEditable())
        {
            String name = txtName.getText().trim();
    
            if (name.length() == 0)
            {
                setState(STATE_EDIT_ERROR, "Missing site name");
                return;
            }
    
            if (imanager.getInstaller(name) != null)
            {
                setState(STATE_EDIT_ERROR, "Duplicate site name");
                return;
            }
    
            setState(STATE_EDIT_OK, "");
        }
    }

    /**
     * The installer type combo box has been changed
     */
    protected void newType()
    {
        if (userInitiated)
        {    
            String type = (String) cboType.getSelectedItem();
            InstallerFactory ifactory = imanager.getInstallerFactory(type);
            Installer installer = ifactory.createInstaller();

            setBean(installer);
        }
    }

    /**
     * Someone has picked a new installer
     */
    protected void select()
    {
        String name = (String) lstSite.getSelectedValue();
        if (name == null)
        {
            btnEdit.setEnabled(false);
            clear();
        }
        else
        {
            btnEdit.setEnabled(true);

            Installer installer = imanager.getInstaller(name);
            display(name, installer);
        }

        // Since setting the display undoes any work done to set the edit state
        // of the bean panel we need to redo it here. Since we are always in
        // display mode at this point, this is fairly easy.
        pnlBean.setEditable(false);
    }

    /**
     * Add a new installer to the list
     */
    protected void add()
    {
        newType();

        editName = null;
        editInstaller = null;

        // We need to call setState() to enable the text boxes so that
        // siteUpdate() works properly
        setState(STATE_EDIT_OK, null);
        siteUpdate();

        Window window = GuiUtil.getWindow(this);
        GuiUtil.restrainedRePack(window);
    }

    /**
     * Move the selected installer to the installer edit panel
     */
    protected void edit()
    {
        String name = (String) lstSite.getSelectedValue();
        if (name == null)
        {
            JOptionPane.showMessageDialog(this, "No selected site to edit", "No Site", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        editName = name;
        editInstaller = imanager.getInstaller(name);

        imanager.removeInstaller(name);

        setState(STATE_EDIT_OK, null);
        siteUpdate();

        txtName.grabFocus();
    }

    /**
     * Delete the selected installer from the list (on the left hand side)
     */
    protected void delete()
    {
        String name = (String) lstSite.getSelectedValue();
        if (name == null)
        {
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete "+name, "Delete site?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            imanager.removeInstaller(name);
        }

        clear();
        setState(STATE_DISPLAY, null);
    }

    /**
     * End editing the current installer
     */
    protected void reset()
    {
        if (editName != null)
        {
            imanager.addInstaller(editName, editInstaller);
        }

        clear();
        editName = null;
        editInstaller = null;

        setState(STATE_DISPLAY, "");
        select();
    }

    /**
     * Save the current installer to the list of installers
     */
    protected void save()
    {
        String name = txtName.getText();
        Installer installer = (Installer) pnlBean.getBean();
        imanager.addInstaller(name, installer);

        clear();
        editName = null;
        editInstaller = null;

        setState(STATE_DISPLAY, "");
        select();
    }

    /**
     * Set the various gui elements depending on the current edit mode
     */
    private void setState(int state, String message)
    {
        switch (state)
        {
        case STATE_DISPLAY:
            btnAdd.setEnabled(true);
            btnDelete.setEnabled(true);
            btnEdit.setEnabled(true);
            lstSite.setEnabled(true);

            btnReset.setEnabled(false);
            btnSave.setEnabled(false);

            btnClose.setEnabled(true);

            txtName.setEditable(false);
            cboType.setEnabled(false);
            pnlBean.setEditable(false);

            lblMesg.setIcon(null);
            break;

        case STATE_EDIT_OK:
        case STATE_EDIT_ERROR:
            btnAdd.setEnabled(false);
            btnDelete.setEnabled(false);
            btnEdit.setEnabled(false);
            lstSite.setEnabled(false);

            btnReset.setEnabled(true);
            btnSave.setEnabled(state == STATE_EDIT_OK);
            pnlBean.setEditable(true);

            btnClose.setEnabled(false);

            txtName.setEditable(true);
            cboType.setEnabled(true);
            pnlBean.setEditable(true);

            // TODO: lblMesg.setIcon(null);
            break;

        default:
            throw new LogicError();
        }

        if (message == null || message.trim().length() == 0)
        {
            lblMesg.setText(" ");
        }
        else
        {
            lblMesg.setText(message);
        }
    }

    /**
     * Set the display in the RHS to the given installer
     */
    private void display(String name, Installer installer)
    {
        txtName.setText(name);

        String type = imanager.getFactoryNameForInstaller(installer);
        userInitiated = false;
        cboType.setSelectedItem(type);
        userInitiated = true;

        setBean(installer);
    }

    /**
     * Clear the display in the RHS of any installers
     */
    private void clear()
    {
        try
        {
            txtName.setText("");
            pnlBean.setBean(null);
        }
        catch (IntrospectionException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Convenience method to allow us to change the type of the current
     * installer.
     * @param installer The new installer to introspect
     */
    private void setBean(Installer installer)
    {
        try
        {
            pnlBean.setBean(installer);
        }
        catch (IntrospectionException ex)
        {
            Reporter.informUser(this, ex);
        }

        Window window = GuiUtil.getWindow(this);
        GuiUtil.restrainedRePack(window);
    }

    /**
     * The state is viewing a site
     */
    private static final int STATE_DISPLAY = 0;

    /**
     * The state is editing a site (syntactically valid)
     */
    private static final int STATE_EDIT_OK = 1;

    /**
     * The state is editing a site (syntactically invalid)
     */
    private static final int STATE_EDIT_ERROR = 2;

    /**
     * The model that we are providing a view/controller for
     */
    private InstallManager imanager = null;

    /**
     * If we are editing an installer, we need to know it's original name
     * in case someone clicks cancel.
     */
    private String editName;

    /**
     * If we are editing an installer, we need to know it's original value
     * in case someone clicks cancel.
     */
    private Installer editInstaller;

    /**
     * Edits to the type combo box mean different things depending on
     * whether it was triggered by the user or the application.
     */
    private boolean userInitiated = true;

    /*
     * GUI Components for the list of sites
     */
    private JScrollPane scrSite = new JScrollPane();
    private JList lstSite = new JList();
    private JPanel pnlSite = new JPanel();
    private JButton btnAdd = new JButton();
    private JButton btnEdit = new JButton();
    private JButton btnDelete = new JButton();
    private JPanel pnlBtn1 = new JPanel();
    private ListModel mdlSite = null;

    /*
     * GUI Components for the site view/edit area
     */
    private JLabel lblMesg = new JLabel();
    private JSeparator sepLine = new JSeparator();
    private JLabel lblName = new JLabel();
    private JTextField txtName = new JTextField();
    private JLabel lblType = new JLabel();
    private JComboBox cboType = new JComboBox();
    private BeanPanel pnlBean = new BeanPanel();
    private ComboBoxModel mdlType = null;

    /*
     * GUI Components that bind the above together
     */
    private JSplitPane sptMain = new JSplitPane();
    private JPanel pnlMain = new JPanel();
    private JButton btnReset = new JButton();
    private JButton btnSave = new JButton();
    private JPanel pnlBtn2 = new JPanel();

    /*
     * Components for the dialog box including the button bar at the bottom.
     * These are separaed in this way in case this component is reused in a
     * larger context.
     */
    protected JDialog dlgMain = null;
    private JButton btnClose = new JButton();
    private JPanel pnlAction = new JPanel();
}
