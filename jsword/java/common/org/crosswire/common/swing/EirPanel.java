package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * EirPanel is an extension of JPanel that adds the ability to be visible in
 * a separate dialog.
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
public class EirPanel extends JPanel
{
    /**
     * A method to be exposed by our children
     * @param parent The component to which to attach the new dialog
     * @param title The title for the new dialog
     * @param modal
     */
    public void showInDialog(Component parent, String title, boolean modal)
    {
        if (dlg_main != null)
        {
            dlg_main.setVisible(true);
            return;
        }

        btn_ok = new JButton();
        btn_ok.setText("Close");
        btn_ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                close();
            }
        });

        pnl_buttons = new JPanel();
        pnl_buttons.add(btn_ok, null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        pnl_main = new JPanel();
        pnl_main.setLayout(new BorderLayout());
        pnl_main.add(pnl_buttons, BorderLayout.SOUTH);
        pnl_main.add(this, BorderLayout.CENTER);

        dlg_main = new JDialog(JOptionPane.getFrameForComponent(parent));
        dlg_main.getContentPane().add(pnl_main);
        dlg_main.setTitle(title);
        dlg_main.setModal(modal);
        dlg_main.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                close();
            }
        });
        dlg_main.pack();
        dlg_main.setVisible(true);
        dlg_main.setLocationRelativeTo(parent);
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
    private JPanel pnl_main;
    private JPanel pnl_buttons;
    private JButton btn_ok;
}
