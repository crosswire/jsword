/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

/**
 * .
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class QuickHelpDialog extends JDialog
{
    /**
     * This is the default constructor
     */
    public QuickHelpDialog(Frame owner, String title, String helpText)
    {
        super(owner);

        initialize();

        txtHelp.setText(helpText);
        this.setTitle(title);
    }

    /**
     * This method initializes the GUI
     */
    private void initialize()
    {
        actions = new ActionFactory(QuickHelpDialog.class, this);

        txtHelp = new JEditorPane();
        txtHelp.setEditable(false);
        txtHelp.setEditorKit(new HTMLEditorKit());
        txtHelp.setMargin(new Insets(5, 5, 0, 5));
        txtHelp.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent ev)
            {
                close();
            }
        });

        JScrollPane scrHelp = new JScrollPane();
        scrHelp.setViewportView(txtHelp);
        scrHelp.setBorder(null);

        JButton btnOK = new JButton(actions.getAction(OK));
        JPanel pnlOK = new JPanel();
        pnlOK.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnlOK.add(btnOK, null);
        pnlOK.setBackground(Color.WHITE);
        pnlOK.setOpaque(true);

        JPanel pnlHelp = new JPanel();
        pnlHelp.setLayout(new BorderLayout());
        pnlHelp.add(scrHelp, BorderLayout.CENTER);
        pnlHelp.add(pnlOK, BorderLayout.SOUTH);

        // TODO(joe): Make this more generic
        this.setSize(650, 200);
        this.setModal(true);
        this.setContentPane(pnlHelp);
        this.getRootPane().setDefaultButton(btnOK);
        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent ev)
            {
                close();
            }
        });
    }

    /* (non-Javadoc)
     * @see java.awt.Component#setVisible(boolean)
     */
    public void setVisible(boolean visible)
    {
        if (visible)
        {
            GuiUtil.centerWindow(this);
        }

        super.setVisible(visible);
    }

    /**
     * Someone clicked OK
     */
    public void doOK()
    {
        close();
    }

    /**
     * 
     */
    public void close()
    {
        setVisible(false);
    }

    private static final String OK = "OK"; //$NON-NLS-1$

    private transient ActionFactory actions;

    private JEditorPane txtHelp;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3690752899747557426L;
}
