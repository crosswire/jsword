package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.crosswire.common.swing.EdgeBorder;

/**
 * A pane that contains ok, cancel and apply buttons.
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
public class ButtonPane extends JPanel
{
    /**
     * Simple ctor
     */
    public ButtonPane(ButtonPaneListener li)
    {
        this.li = li;
        init();
    }

    /**
     * GUI init.
     */
    protected void init()
    {
        // PENDING: find some way to do default buttons
        //dialog.getRootPane().setDefaultButton(ok);

        buttons.setLayout(new GridLayout(1, 2, 10, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttons.add(ok);
        buttons.add(cancel);
        buttons.add(apply);
        buttons.add(help);
        help.setEnabled(false);

        ok.setMnemonic('O');
        cancel.setMnemonic('C');
        apply.setMnemonic('A');
        help.setMnemonic('H');

        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                li.okPressed(ev);
            }
        });
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                li.cancelPressed(ev);
            }
        });
        apply.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                li.applyPressed(ev);
            }
        });

        this.setBorder(new EdgeBorder(SwingConstants.NORTH));
        this.setLayout(new BorderLayout(10, 10));
        this.add(BorderLayout.EAST, buttons);
    }

    /**
     * A panel so we can right justify
     */
    private JPanel buttons = new JPanel();

    /**
     * PENDING: turn this into a [add|remove]ButtonPaneListener thing
     */
    protected ButtonPaneListener li;

    /**
     * The Ok button
     */
    private JButton ok = new JButton(Msg.OK.toString());

    /**
     * The cancel button
     */
    private JButton cancel = new JButton(Msg.CANCEL.toString());

    /**
     * The apply button
     */
    private JButton apply = new JButton(Msg.APPLY.toString());

    /**
     * The help button
     */
    private JButton help = new JButton(Msg.HELP.toString());
}
