
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
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.crosswire.common.config.Config;
import org.crosswire.common.swing.EdgeBorder;
import org.crosswire.common.swing.FormPane;
import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;

/**
 * A mutable view of Fields setting array.
 * <p>A few of the ideas in this code came from an article in the JDJ about
 * configuration. However the Config package has a number of huge
 * differences, the biggest being what it does with its config info. The
 * JDJ article assumed that you'd only ever want to edit a properties file
 * and that the rest of the app didn't care much, and that the tree style
 * view was the only one you would ever need. This package is a re-write
 * that addresses these shortcomings and others.
 * <p>The JDJ article uses a <code>DeckLayout</code> instead of the
 * <code>java.awt.CardLayout</code> because there are supposedly some focus
 * problems in the CardLayout code. I have not noticed these, and so I have
 * used the more standard CardLayout, however a copy of the DeckLayout code
 * is in the <code>com.barclaycard.swing</code> package. Maybe we should remove it
 * from here - the redistribution status of it is not clear.
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
public class WizardConfigPane extends PanelConfigPane
{
    /**
     * Create a WizardConfig panel Field set
     * @param config The set of Fields us display
     */
    public WizardConfigPane(Config config)
    {
        super(config);
    }

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
        Enumeration en = config.getNames();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();

            int last_dot = key.lastIndexOf('.');
            String path = key.substring(0, last_dot);

            FormPane card = (FormPane) decks.get(path);
            if (card.getParent() == null)
            {
                JScrollPane scroll = new JScrollPane(card);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                deck.add(path, scroll);
                cards++;

                // The name for the title bar
                names.addElement(StringUtil.swap(path, ".", " "));
            }
        }

        title.setIcon(task);
        title.setFont(new Font(getFont().getName(), Font.PLAIN, 16));
        title.setPreferredSize(new Dimension(30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        title.setBackground(Color.gray);
        title.setForeground(Color.white);
        title.setOpaque(true);
        title.setText(names.elementAt(1) + " Properties (1 out of "+cards+")");

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

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { hideDialog(); }
        });
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { move(-1); }
        });
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { move(1); }
        });
        ok.addActionListener(new ActionListener() {
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

        retcode.setBorder(new EdgeBorder(EdgeBorder.NORTH));
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

        if (dirn == 1 && posn < (cards-1))
        {
            layout.next(deck);
            posn++;
        }

        title.setText(names.elementAt(posn) + " Properties ("+(posn+1)+" out of "+cards+")");

        back.setEnabled(posn != 0);
        apply.setEnabled(posn != (cards-1));

        if (posn == cards-1)
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
        LookAndFeelUtil.addComponentToUpdate(dialog);

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

    /** The current position */
    private int posn = 0;

    /** The number of cards */
    private int cards = 0;

    /** The list of path names */
    private Vector names = new Vector();

    /** The title for the config panels */
    private JLabel title = new JLabel("Properties", JLabel.LEFT);

    /** Contains the configuration panels */
    private JPanel deck = new JPanel();

    /** Layout for the config panels */
    private CardLayout layout = new CardLayout();

    /** The Back button */
    private JButton back = new JButton("Back");

    /** The log stream */
    protected static Logger log = Logger.getLogger(WizardConfigPane.class);
}

