package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.crosswire.common.swing.EdgeBorder;
import org.crosswire.common.swing.FormPane;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;

/**
 * A mutable view of Fields setting array.
 * <p>A few of the ideas in this code came from an article in the JDJ about
 * configuration. However the Config package has a number of huge
 * differences, the biggest being what it does with its config info. The
 * JDJ article assumed that you'd only ever want to edit a properties file
 * and that the rest of the app didn't care much, and that the tree style
 * view was the only one you would ever need. This package is a re-write
 * that addresses these shortcomings and others.
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
public class WizardConfigEditor extends AbstractConfigEditor
{
    /**
     * Now this wasn't created with JBuilder but maybe, just maybe, by
     * calling my method this, JBuilder may grok it.
     */
    protected void jbInit()
    {
        JPanel panel = new JPanel();

        deck.setLayout(layout);

        // We need to Enumerate thru the Model names not the Path names in the
        // deck because the deck is a Hashtable that re-orders them.
        Iterator it = config.getNames();
        while (it.hasNext())
        {
            String key = (String) it.next();

            int last_dot = key.lastIndexOf('.');
            String path = key.substring(0, last_dot);

            FormPane card = (FormPane) decks.get(path);
            if (card.getParent() == null)
            {
                JScrollPane scroll = new JScrollPane(card);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                deck.add(path, scroll);
                wcards++;

                // The name for the title bar
                names.add(StringUtils.replace(path, ".", " "));
            }
        }

        title.setIcon(task);
        title.setFont(new Font(getFont().getName(), Font.PLAIN, 16));
        title.setPreferredSize(new Dimension(30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        title.setBackground(Color.gray);
        title.setForeground(Color.white);
        title.setOpaque(true);
        title.setText(names.get(1) + " Properties (1 out of "+wcards+")");

        deck.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Use this if you want to have the tree touch the bottom. Then add
        // the button panel to content.South
        // JPanel content = new JPanel();
        // content.setLayout(new BorderLayout());
        // content.add("Center", deck);

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panel.add("North", title);
        panel.add("Center", deck);

        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add("Center", panel);
        add("South", getButtonPane());

        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Now this wasn't created with JBuilder but maybe just maybe by
     * calling my method this, JBuilder may grok it.
     */
    protected void updateTree()
    {
    }

    /**
     * A Config panel does not have buttons. These are they.
     * @return A button panel
     */
    protected JComponent getButtonPane()
    {
        // relabel the buttons
        cancel.setText("Cancel");
        apply.setText("Next");
        ok.setText("Finish");

        JPanel buttons = new JPanel();
        JPanel retcode = new JPanel();

        buttons.setLayout(new GridLayout(1, 2, 10, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttons.add(help);
        buttons.add(cancel);
        buttons.add(back);
        buttons.add(apply);
        buttons.add(ok);

        help.setEnabled(false);
        back.setEnabled(false);

        help.setMnemonic('H');
        cancel.setMnemonic('C');
        back.setMnemonic('B');
        apply.setMnemonic('N');
        ok.setMnemonic('F');

        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                hideDialog();
            }
        });
        back.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                move(-1);
            }
        });
        apply.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                move(1);
            }
        });
        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                try
                {
                    screenToLocal();
                    al.actionPerformed(ev);
                    hideDialog();
                }
                catch (Exception ex)
                {
                    Reporter.informUser(this, ex);
                }
            }
        });

        retcode.setBorder(new EdgeBorder(SwingConstants.NORTH));
        retcode.setLayout(new BorderLayout(10, 10));
        retcode.add("East", buttons);

        return retcode;
    }

    /**
     * Set a new card to be visible
     */
    protected void move(int dirn)
    {
        if (dirn == -1 && posn > 0)
        {
            layout.previous(deck);
            posn--;
        }

        if (dirn == 1 && posn < (wcards-1))
        {
            layout.next(deck);
            posn++;
        }

        title.setText(names.get(posn) + " Properties ("+(posn+1)+" out of "+wcards+")");

        back.setEnabled(posn != 0);
        apply.setEnabled(posn != (wcards-1));

        if (posn == wcards-1)
        {
            dialog.getRootPane().setDefaultButton(ok);
        }
        else
        {
            dialog.getRootPane().setDefaultButton(apply);
        }
    }

    /**
     * Create a dialog to house a TreeConfig component using the default
     * set of Fields. This version just sets the default button to next
     * @param parent A component to use to find a frame to use as a dialog parent
     */
    public void showDialog(Component parent)
    {
        dialog = new JDialog((JFrame) SwingUtilities.getRoot(parent));
        // NOTE: when we tried dynamic laf update, dialog needed special treatment
        //LookAndFeelUtil.addComponentToUpdate(dialog);

        dialog.getRootPane().setDefaultButton(apply);
        dialog.getContentPane().add(this);
        dialog.setTitle(config.getTitle());
        dialog.setSize(800, 500);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);

        // Why is this only available in Frames?
        // dialog.setIconImage(task_small);

        log.debug("Modal fails on SunOS, take care. os.name="+System.getProperty("os.name"));
        if (!"SunOS".equals(System.getProperty("os.name")))
        {
            dialog.dispose();
            dialog = null;
        }
    }

    /**
     * The current position
     */
    private int posn = 0;

    /**
     * The number of cards
     */
    private int wcards = 0;

    /**
     * The list of path names
     */
    private List names = new ArrayList();

    /**
     * The title for the config panels
     */
    private JLabel title = new JLabel("Properties", SwingConstants.LEFT);

    /**
     * Contains the configuration panels
     */
    private JPanel deck = new JPanel();

    /**
     * Layout for the config panels
     */
    private CardLayout layout = new CardLayout();

    /**
     * The Ok button
     */
    private JButton ok = new JButton("OK");

    /**
     * The cancel button
     */
    private JButton cancel = new JButton("Cancel");

    /**
     * The apply button
     */
    private JButton apply = new JButton("Apply");

    /**
     * The help button
     */
    private JButton help = new JButton("Help");

    /**
     * The Back button
     */
    private JButton back = new JButton("Back");

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(WizardConfigEditor.class);
}