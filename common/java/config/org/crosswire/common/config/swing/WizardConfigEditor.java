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
public class WizardConfigEditor extends AbstractConfigEditor implements ActionListener
{
    private static final String NEXT = "WizardNext"; //$NON-NLS-1$
    private static final String CANCEL = "WizardCancel"; //$NON-NLS-1$
    private static final String FINISH = "WizardFinish"; //$NON-NLS-1$
    private static final String HELP = "WizardHelp"; //$NON-NLS-1$
    private static final String BACK = "WizardBack"; //$NON-NLS-1$

    /**
     * <br />Danger - this method is not called by the TreeConfigEditor
     * constructor, it is called by the AbstractConfigEditor constructor so
     * any field initializers will be called AFTER THIS METHOD EXECUTES
     * so don't use field initializers.
     */
    protected void initialize()
    {
        actions = ButtonActionFactory.instance();
        actions.addActionListener(this);

        names = new ArrayList();
        layout = new CardLayout();
        deck = new JPanel(layout);

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
                names.add(StringUtils.replaceChars(path, '.', ' '));  //$NON-NLS-1$//$NON-NLS-2$
            }
        }

        title = new JLabel(Msg.PROPERTIES.toString(), SwingConstants.LEFT);
        title.setIcon(task);
        title.setFont(new Font(getFont().getName(), Font.PLAIN, 16));
        title.setPreferredSize(new Dimension(30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        title.setBackground(Color.gray);
        title.setForeground(Color.white);
        title.setOpaque(true);
        title.setText(names.get(1) + Msg.PROPERTIES_POSN.toString(new Object[] { new Integer(1), new Integer(wcards) }));

        deck.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Use this if you want to have the tree touch the bottom. Then add
        // the button panel to content.South
        // JPanel content = new JPanel();
        // content.setLayout(new BorderLayout());
        // content.add(BorderLayout.CENTER, deck);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panel.add(title, BorderLayout.PAGE_START);
        panel.add(deck, BorderLayout.CENTER);

        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(panel, BorderLayout.CENTER);
        add(getButtonPane(), BorderLayout.PAGE_END);

        SwingUtilities.updateComponentTreeUI(this);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        actions.actionPerformed(e, this);
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
    private JComponent getButtonPane()
    {

        finish = new JButton(actions.getAction(FINISH));
        next = new JButton(actions.getAction(NEXT));

        JPanel buttons = new JPanel();

        buttons.setLayout(new GridLayout(1, 2, 10, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttons.add(new JButton(actions.getAction(HELP)));
        buttons.add(new JButton(actions.getAction(CANCEL)));
        buttons.add(new JButton(actions.getAction(BACK)));
        buttons.add(next);
        buttons.add(finish);

        actions.getAction(HELP).setEnabled(false);
        actions.getAction(BACK).setEnabled(false);

        JPanel retcode = new JPanel(new BorderLayout(10, 10));

        retcode.setBorder(new EdgeBorder(SwingConstants.NORTH));
        retcode.add(buttons, BorderLayout.LINE_END);

        return retcode;
    }

    protected void doWizardCancel()
    {
        hideDialog();
    }

    protected void doWizardHelp()
    {
        
    }

    protected void doWizardBack()
    {
        move(-1);
    }

    protected void doWizardNext()
    {
        move(1);
    }

    protected void doWizardFinish(ActionEvent ev)
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

    /**
     * Set a new card to be visible
     */
    private void move(int dirn)
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

        title.setText(names.get(posn) + Msg.PROPERTIES_POSN.toString(new Object[] { new Integer(posn+1), new Integer(wcards) }));

        actions.getAction(BACK).setEnabled(posn != 0);
        actions.getAction(NEXT).setEnabled(posn != (wcards-1));

        if (posn == wcards-1)
        {
            dialog.getRootPane().setDefaultButton(finish);
        }
        else
        {
            dialog.getRootPane().setDefaultButton(next);
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

        dialog.getRootPane().setDefaultButton(next);
        dialog.getContentPane().add(this);
        dialog.setTitle(config.getTitle());
        dialog.setSize(800, 500);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);

        // Why is this only available in Frames?
        // dialog.setIconImage(task_small);

        log.debug("Modal fails on SunOS, take care. os.name="+System.getProperty("os.name"));  //$NON-NLS-1$//$NON-NLS-2$
        if (!"SunOS".equals(System.getProperty("os.name")))  //$NON-NLS-1$//$NON-NLS-2$
        {
            dialog.dispose();
            dialog = null;
        }
    }

    private ButtonActionFactory actions;
    
    /**
     * The current position
     */
    private int posn;

    /**
     * The number of cards
     */
    private int wcards;

    /**
     * The list of path names
     */
    private List names;

    /**
     * The title for the config panels
     */
    private JLabel title;

    /**
     * Contains the configuration panels
     */
    private JPanel deck;

    /**
     * Layout for the config panels
     */
    private CardLayout layout;

    /**
     * The Ok button
     */
    private JButton finish;

    /**
     * The next button
     */
    private JButton next;


    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(WizardConfigEditor.class);
}
