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
import java.awt.event.KeyAdapter;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

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
    public EditSitePane() throws InstallException
    {
        imanager = new InstallManager();
        mdlSite = new InstallManagerListModel(imanager);
        mdlType = new InstallerFactoryComboBoxModel(imanager);
        initialize();
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
        txtName.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent ev)
            {
                siteUpdate();
            }
        });

        lblType.setText("Type:");
        lblType.setDisplayedMnemonic('Y');
        lblType.setLabelFor(cboType);
        cboType.setEditable(false);
        cboType.setModel(mdlType);

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
        sptMain.setResizeWeight(0.5);
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
                dlgMain.dispose();
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
                dlgMain.dispose();
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
     * 
     */
    protected void siteUpdate()
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

    /**
     * 
     */
    protected void select()
    {
        String name = (String) lstSite.getSelectedValue();
        if (name == null)
        {
            return;
        }
        
        Installer installer = imanager.getInstaller(name);
        display(name, installer);
    }

    /**
     * 
     */
    private void display(String name, Installer installer)
    {
        try
        {
            txtName.setText(name);

            String type = imanager.getNameForInstaller(installer);
            cboType.setSelectedItem(type);
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
     * 
     */
    protected void add()
    {
        clear();
        siteUpdate();
    }

    /**
     * 
     */
    protected void edit()
    {
    }

    /**
     * 
     */
    protected void delete()
    {
        JOptionPane.showMessageDialog(this, "Delete not implemented");
    }

    /**
     * 
     */
    protected void reset()
    {
        clear();
        setState(STATE_DISPLAY, "");
    }

    /**
     * 
     */
    protected void save()
    {
        JOptionPane.showMessageDialog(this, "Save not implemented");
        setState(STATE_DISPLAY, "");
    }

    /**
     * 
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
     * 
     */
    private void setState(int state, String message)
    {
        switch (state)
        {
        case STATE_DISPLAY:
            btnAdd.setEnabled(true);
            btnDelete.setEnabled(true);
            btnReset.setEnabled(false);
            btnSave.setEnabled(true);
            btnClose.setEnabled(false);
            //btnCancel.setEnabled(true);
            // TODO: lblMesg.setIcon(null);
            txtName.setEditable(false);
            break;

        case STATE_EDIT_OK:
            btnAdd.setEnabled(false);
            btnDelete.setEnabled(false);
            btnClose.setEnabled(false);
            btnReset.setEnabled(false);
            btnSave.setEnabled(true);
            // TODO: lblMesg.setIcon(null);
            txtName.setEditable(true);
            break;

        case STATE_EDIT_ERROR:
            btnAdd.setEnabled(false);
            btnDelete.setEnabled(false);
            btnClose.setEnabled(false);
            btnReset.setEnabled(false);
            btnSave.setEnabled(true);
            // TODO: lblMesg.setIcon(null);
            txtName.setEditable(true);
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
