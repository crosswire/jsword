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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageEvent;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageListener;
import org.crosswire.jsword.view.swing.passage.PassageListModel;
import org.crosswire.jsword.view.swing.passage.PassageTreeModel;

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
        scr_all.getViewport().add(tre_all, null);

        btn_del.setMnemonic('<');
        btn_del.setText("<");
        btn_add.setMnemonic('>');
        btn_add.setText(">");

        box_act.add(btn_add);
        box_act.add(Box.createVerticalStrut(5));
        box_act.add(btn_del);

        scr_sel.getViewport().add(lst_sel, null);

        pnl_picker.setLayout(new GridBagLayout());
        pnl_picker.add(scr_sel, new GridBagConstraints(2, 0, 1, 1, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 0), 0, 0));
        pnl_picker.add(box_act, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        pnl_picker.add(scr_all, new GridBagConstraints(0, 0, 1, 1, 0.5, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        btn_go.setText("Done");
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                close();
            }
        });

        pnl_display.setLayout(new BorderLayout(5, 5));
        pnl_display.add(txt_display, BorderLayout.CENTER);
        pnl_display.add(btn_go, BorderLayout.EAST);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new BorderLayout(5, 5));
        this.add(pnl_picker, BorderLayout.CENTER);
        this.add(pnl_display, BorderLayout.SOUTH);
    }

    /**
     * Setup the various models
     */
    public void setPassage(String refstr)
    {
        try
        {
            ref = PassageFactory.createPassage(refstr);

            tre_all.setModel(new PassageTreeModel(all, tre_all));
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
        txt_display.setText(ref.getName());
    }

    /**
     * A method to be exposed by our children
     * @param parent The component to which to attach the new dialog
     * @param title The title for the new dialog
     * @param modal
     */
    public void showInDialog(Component parent, String title, boolean modal, String refstr)
    {
        setPassage(refstr);

        if (dlg_main != null)
        {
            dlg_main.setVisible(true);
            return;
        }

        dlg_main = new JDialog(JOptionPane.getFrameForComponent(parent));
        dlg_main.getContentPane().add(this);
        dlg_main.setTitle(title);
        dlg_main.setModal(modal);
        dlg_main.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev) { close(); }
        });

        dlg_main.pack();
        dlg_main.setVisible(true);
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
    private Passage all = PassageFactory.getWholeBiblePassage();

    private JPanel pnl_picker = new JPanel();
    private JPanel pnl_display = new JPanel();
    private JTextField txt_display = new JTextField();
    private JButton btn_go = new JButton();
    private JScrollPane scr_all = new JScrollPane();
    private Box box_act = new Box(BoxLayout.Y_AXIS);
    private JScrollPane scr_sel = new JScrollPane();
    private JList lst_sel = new JList();
    private JButton btn_del = new JButton();
    private JButton btn_add = new JButton();
    private JTree tre_all = new JTree();

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
