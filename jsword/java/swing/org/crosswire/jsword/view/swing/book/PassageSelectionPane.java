
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreePath;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageEvent;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageListener;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.view.swing.passage.PassageListModel;
import org.crosswire.jsword.view.swing.passage.WholeBibleTreeModel;
import org.crosswire.jsword.view.swing.passage.WholeBibleTreeNode;

/**
 * A nice way to select passages.
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
public class PassageSelectionPane extends JPanel
{
    /**
     * Constructor for PassageSelectionPane.
     */
    public PassageSelectionPane()
    {
        jbInit();
    }

    private void jbInit()
    {
        tre_all.setModel(new WholeBibleTreeModel());
        tre_all.setShowsRootHandles(true);
        tre_all.setRootVisible(false);
        lbl_all.setDisplayedMnemonic('T');
        lbl_all.setLabelFor(tre_all);
        lbl_all.setText("Bible Tree:");
        scr_all.getViewport().add(tre_all, null);

        btn_add.setMnemonic('>');
        btn_add.setText(">>>");
        btn_add.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                addTreeToCurrent();
            }
        });
        btn_del.setMnemonic('<');
        btn_del.setText("<<<");
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

        lbl_display.setDisplayedMnemonic('V');
        lbl_display.setLabelFor(txt_display);
        lbl_display.setText("Verses:");
        txt_display.getDocument().addDocumentListener(new CustomDocumentEvent());
        btn_go.setText("Done");
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                close();
            }
        });
        pnl_display.setLayout(new BorderLayout());
        pnl_display.add(btn_go, BorderLayout.EAST);
        pnl_display.add(txt_display, BorderLayout.CENTER);
        pnl_display.add(lbl_display, BorderLayout.WEST);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridBagLayout());
        this.add(lbl_all, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(scr_all, new GridBagConstraints(0, 1, 1, 4, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 10, 2), 0, 0));
        this.add(pnl_space1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(btn_del, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(btn_add, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        this.add(pnl_space2, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(lbl_sel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(scr_sel, new GridBagConstraints(2, 1, 1, 4, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 10, 5), 0, 0));
        this.add(pnl_display, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * Setup the various models
     */
    public void setPassage(String refstr)
    {
        try
        {
            ref = PassageFactory.createPassage(refstr);

            lst_sel.setModel(new PassageListModel(ref, PassageListModel.LIST_RANGES));

            ref.addPassageListener(new CustomPassageListener());
        }
        catch (NoSuchVerseException ex)
        {
            lst_sel.setEnabled(false);
            tre_all.setEnabled(false);
            btn_add.setEnabled(false);
            btn_del.setEnabled(false);
        }
    }

    /**
     * Called whenever the passage changes to update the text box.
     */
    private void updateTextDisplay()
    {
        if (changing)
            return;

        changing = true;
        txt_display.setText(ref.getName());
        changing = false;
    }

    /**
     * Called whenever the text box changes to update the list
     */
    private void updateList()
    {
        if (changing)
            return;

        changing = true;
        String refstr = txt_display.getText();
        Passage temp = null;
        try
        {
            temp = PassageFactory.createPassage(refstr);
            ref.clear();
            ref.addAll(temp);
            
            lst_sel.setEnabled(true);
            btn_go.setEnabled(true);
        }
        catch (NoSuchVerseException ex)
        {
            lst_sel.setEnabled(false);
            btn_go.setEnabled(false);
        }
        changing = false;
    }

    /**
     * A method to be exposed by our children
     * @param parent The component to which to attach the new dialog
     * @param title The title for the new dialog
     * @param modal
     */
    public String showInDialog(Component parent, String title, boolean modal, String refstr)
    {
        setPassage(refstr);

        if (dlg_main == null)
        {
            dlg_main = new JDialog(JOptionPane.getFrameForComponent(parent));
            dlg_main.getContentPane().add(this);
            dlg_main.getRootPane().setDefaultButton(btn_go);
            dlg_main.setTitle(title);
            dlg_main.setModal(modal);
            dlg_main.addWindowListener(new WindowAdapter()
            {
                public void windowClosed(WindowEvent ev) { close(); }
            });
        }

        GuiUtil.centerWindow(dlg_main);
        dlg_main.pack();
        dlg_main.setVisible(true);

        return txt_display.getText();
    }

    /**
     * Add from the tree to the list
     */
    protected void addTreeToCurrent()
    {
        TreePath[] selected = tre_all.getSelectionPaths();
        for (int i=0; i<selected.length; i++)
        {
            WholeBibleTreeNode node = (WholeBibleTreeNode) selected[i].getLastPathComponent();
            VerseRange range = node.getVerseRange();
            ref.add(range);
        }
    }

    /**
     * Remove the selected items from the list
     */
    protected void deleteFromCurrent()
    {
        Object[] selected = lst_sel.getSelectedValues();
        for (int i=0; i<selected.length; i++)
        {
            VerseRange range = (VerseRange) selected[i];
            ref.remove(range);
        }
    }

    /**
     * Close this dialog
     */
    protected void close()
    {
        if (dlg_main != null)
        {
            dlg_main.dispose();
            dlg_main = null;
        }
    }

    private JDialog dlg_main;
    private Passage ref;
    private boolean changing = false;

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
    private JButton btn_go = new JButton();

    /**
     * Update the list whenever the textbox changes
     */
    private class CustomDocumentEvent implements DocumentListener
    {
        /**
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent ev)
        {
            updateList();
        }

        /**
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent ev)
        {
            updateList();
        }

        /**
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent ev)
        {
            updateList();
        }
    }

    /**
     * To update the textbox when the passage changes
     */
    private class CustomPassageListener implements PassageListener
    {
        /**
         * @see org.crosswire.jsword.passage.PassageListener#versesAdded(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesAdded(PassageEvent ev)
        {
            updateTextDisplay();
        }

        /**
         * @see org.crosswire.jsword.passage.PassageListener#versesRemoved(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesRemoved(PassageEvent ev)
        {
            updateTextDisplay();
        }

        /**
         * @see org.crosswire.jsword.passage.PassageListener#versesChanged(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesChanged(PassageEvent ev)
        {
            updateTextDisplay();
        }
    }
}
