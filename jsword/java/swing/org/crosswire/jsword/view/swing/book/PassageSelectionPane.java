
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageEvent;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageListener;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.view.swing.passage.PassageListModel;
import org.crosswire.jsword.view.swing.passage.WholeBibleTreeModel;
import org.crosswire.jsword.view.swing.passage.WholeBibleTreeNode;

/**
 * A JPanel (or dialog) that presents a interactive GUI way to select passages.
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
public class PassageSelectionPane extends JPanel
{
    /**
     * Constructor for PassageSelectionPane.
     */
    public PassageSelectionPane()
    {
        try
        {
            URL url_good = ResourceUtil.getResource("toolbarButtonGraphics/general/About24.gif"); //$NON-NLS-1$
            if (url_good != null)
            {
                ico_good = new ImageIcon(url_good);
            }
            
            URL url_bad = ResourceUtil.getResource("toolbarButtonGraphics/general/Stop24.gif"); //$NON-NLS-1$
            if (url_bad != null)
            {
                ico_bad = new ImageIcon(url_bad);
            }
        }
        catch (MalformedURLException ex)
        {
            assert false : ex;
        }

        jbInit();
    }

    /**
     * GUI init
     */
    private void jbInit()
    {
        lbl_all.setDisplayedMnemonic('T');
        lbl_all.setLabelFor(tre_all);
        lbl_all.setText("Bible Tree:");
        scr_all.getViewport().add(tre_all, null);
        tre_all.setModel(new WholeBibleTreeModel());
        tre_all.setShowsRootHandles(true);
        tre_all.setRootVisible(false);
        tre_all.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                treeSelected();
            }
        });

        btn_add.setText("Add");
        btn_add.setMnemonic('A');
        btn_add.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                addTreeToCurrent();
            }
        });
        btn_del.setText("Delete");
        btn_del.setMnemonic('D');
        btn_del.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                deleteFromCurrent();
            }
        });

        lbl_sel.setDisplayedMnemonic('S');
        lbl_sel.setLabelFor(lst_sel);
        lbl_sel.setText("Selected Verses:");
        scr_sel.getViewport().add(lst_sel, null);
        lst_sel.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                listSelected();
            }
        });

        lbl_display.setDisplayedMnemonic('V');
        lbl_display.setLabelFor(txt_display);
        lbl_display.setText("Verses: ");
        txt_display.getDocument().addDocumentListener(new CustomDocumentEvent());
        pnl_display.setLayout(new BorderLayout());
        pnl_display.add(txt_display, BorderLayout.CENTER);
        pnl_display.add(lbl_display, BorderLayout.WEST);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridBagLayout());
        this.add(lbl_all, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 5), 0, 0));
        this.add(scr_all, new GridBagConstraints(0, 1, 1, 4, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 10, 2), 0, 0));
        this.add(pnl_space1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(btn_del, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(btn_add, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(pnl_space2, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(lbl_sel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));
        this.add(scr_sel, new GridBagConstraints(2, 1, 1, 4, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 2, 10, 10), 0, 0));
        this.add(lbl_message, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
        this.add(pnl_display, new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
    }

    /**
     * Called whenever the passage changes to update the text box.
     */
    protected void copyListToText()
    {
        if (changing)
        {
            return;
        }

        changing = true;
        txt_display.setText(ref.getName());
        updateMessageSummary();
        changing = false;
    }

    /**
     * Called whenever the text box changes to update the list
     */
    protected void copyTextToList()
    {
        if (changing)
        {
            return;
        }

        changing = true;
        String refstr = txt_display.getText();
        Passage temp = null;
        try
        {
            temp = PassageFactory.createPassage(refstr);
            ref.clear();
            ref.addAll(temp);

            setValidPassage(true);
            updateMessageSummary();
        }
        catch (NoSuchVerseException ex)
        {
            setValidPassage(false);
            updateMessage(ex);
        }
        changing = false;
    }

    /**
     * Update the UI when the validity of the passage changes
     * @param valid
     */
    private void setValidPassage(boolean valid)
    {
        lst_sel.setEnabled(valid);
        tre_all.setEnabled(valid);
        btn_add.setEnabled(valid);
        btn_del.setEnabled(valid);
    }

    /**
     * Write out an error message to the message label
     * @param ex
     */
    private void updateMessage(NoSuchVerseException ex)
    {
        lbl_message.setText("Error: "+ex.getMessage());
        lbl_message.setIcon(ico_bad);
    }

    /**
     * Write out an summary message to the message label
     */
    private void updateMessageSummary()
    {
        lbl_message.setText("Summary: "+ref.getOverview());
        lbl_message.setIcon(ico_good);
    }

    /**
     * Open us in a new (optionally modal) dialog window
     * @param parent The component to which to attach the new dialog
     * @param title The title for the new dialog
     * @param modal
     */
    public String showInDialog(Component parent, String title, boolean modal, String refstr)
    {
        try
        {
            ref = PassageFactory.createPassage(refstr);

            txt_display.setText(refstr);
            lst_sel.setModel(new PassageListModel(ref, PassageListModel.LIST_RANGES, PassageConstants.RESTRICT_CHAPTER));

            ref.addPassageListener(new CustomPassageListener());
            updateMessageSummary();
        }
        catch (NoSuchVerseException ex)
        {
            setValidPassage(false);
            updateMessage(ex);
        }

        // Make sure the add/delete buttons start right
        treeSelected();
        listSelected();

        final JDialog dlg_main = new JDialog(JOptionPane.getFrameForComponent(parent));
        JButton btn_go = new JButton();
        JPanel pnl_action = new JPanel();
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        bailout = true;

        btn_go.setText("Done");
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                bailout = false;
                dlg_main.dispose();
            }
        });

        pnl_action.setLayout(new BorderLayout());
        pnl_action.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 20));
        pnl_action.add(btn_go, BorderLayout.EAST);

        ActionListener closer = new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                dlg_main.dispose();
            }
        };

        dlg_main.getContentPane().setLayout(new BorderLayout());
        dlg_main.getContentPane().add(this, BorderLayout.CENTER);
        dlg_main.getContentPane().add(pnl_action, BorderLayout.SOUTH);
        dlg_main.getRootPane().setDefaultButton(btn_go);
        dlg_main.getRootPane().registerKeyboardAction(closer, esc, JComponent.WHEN_IN_FOCUSED_WINDOW);
        dlg_main.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlg_main.setTitle(title);
        dlg_main.setModal(modal);

        GuiUtil.restrainedPack(dlg_main, 0.5f, 0.75f);
        GuiUtil.centerWindow(dlg_main);
        dlg_main.setVisible(true);

        if (bailout)
        {
            return null;
        }
        else
        {
            return txt_display.getText();
        }
    }

    /**
     * Add from the tree to the list
     */
    protected void addTreeToCurrent()
    {
        TreePath[] selected = tre_all.getSelectionPaths();
        if (selected != null)
        {
            for (int i=0; i<selected.length; i++)
            {
                WholeBibleTreeNode node = (WholeBibleTreeNode) selected[i].getLastPathComponent();
                VerseRange range = node.getVerseRange();
                ref.add(range);
            }
        }
    }

    /**
     * Remove the selected items from the list
     */
    protected void deleteFromCurrent()
    {
        Object[] selected = lst_sel.getSelectedValues();
        if (selected != null)
        {
            for (int i=0; i<selected.length; i++)
            {
                VerseRange range = (VerseRange) selected[i];
                ref.remove(range);
            }
        }
    }

    /**
     * The tree selection has changed
     */
    protected void treeSelected()
    {
        TreePath[] selected = tre_all.getSelectionPaths();
        btn_add.setEnabled(selected != null && selected.length > 0);
    }

    /**
     * List selection has changed
     */
    protected void listSelected()
    {
        Object[] selected = lst_sel.getSelectedValues();
        btn_del.setEnabled(selected != null && selected.length > 0);
    }

    /**
     * If escape was pressed we don't want to update the parent
     */
    protected boolean bailout = false;
    
    /**
     * Prevent us getting in an event cascade loop
     */
    private boolean changing = false;

    /**
     * The psaage we are editing
     */
    private Passage ref;

    /*
     * GUI Components
     */
    private Icon ico_good;
    private Icon ico_bad;
    private JScrollPane scr_all = new JScrollPane();
    private JScrollPane scr_sel = new JScrollPane();
    private JLabel lbl_all = new JLabel();
    private JLabel lbl_sel = new JLabel();
    private JButton btn_del = new JButton();
    private JButton btn_add = new JButton();
    private JTree tre_all = new JTree();
    private JList lst_sel = new JList();
    private JPanel pnl_space1 = new JPanel();
    private JPanel pnl_space2 = new JPanel();
    private JPanel pnl_display = new JPanel();
    private JLabel lbl_display = new JLabel();
    private JTextField txt_display = new JTextField();
    private JLabel lbl_message = new JLabel();

    /**
     * Update the list whenever the textbox changes
     */
    private class CustomDocumentEvent implements DocumentListener
    {
        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent ev)
        {
            copyTextToList();
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent ev)
        {
            copyTextToList();
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent ev)
        {
            copyTextToList();
        }
    }

    /**
     * To update the textbox when the passage changes
     */
    private class CustomPassageListener implements PassageListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageListener#versesAdded(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesAdded(PassageEvent ev)
        {
            copyListToText();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageListener#versesRemoved(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesRemoved(PassageEvent ev)
        {
            copyListToText();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageListener#versesChanged(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesChanged(PassageEvent ev)
        {
            copyListToText();
        }
    }
}
