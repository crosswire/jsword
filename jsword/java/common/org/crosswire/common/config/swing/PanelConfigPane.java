
package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.Config;
import org.crosswire.common.config.ConfigEvent;
import org.crosswire.common.config.ConfigListener;
import org.crosswire.common.swing.EdgeBorder;
import org.crosswire.common.swing.FormPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.config.LookAndFeelChoices;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.UserLevel;

/**
 * Some static methods for using the Config package.
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
public abstract class PanelConfigPane extends JPanel implements BaseConfig
{
    /**
     * Create a Config base with the set of Fields that it will
     * display.
     */
    public PanelConfigPane(Config config)
    {
        this.config = config;

        jbInit();

        config.addConfigListener(new ConfigListener() {
            public void choiceAdded(ConfigEvent ev)
            {
                addChoice(ev.getKey(), ev.getChoice());
                updateTree();
            }
            public void choiceRemoved(ConfigEvent ev)
            {
                removeChoice(ev.getKey(), ev.getChoice());
                updateTree();
            }
        });

        // For each of the Fields put it in a FieldPanel
        Enumeration en = config.getNames();
        while (en.hasMoreElements())
        {
            String key = (String) en.nextElement();
            Choice model = config.getChoice(key);

            addChoice(key, model);
        }

        updateTree();

        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Now this wasn't created with JBuilder but maybe, just maybe, by
     * calling my method this, JBuilder may grok it.
     */
    protected abstract void jbInit();

    /**
     * Update the tree structure
     */
    protected abstract void updateTree();

    /**
     * A Config panel does not have buttons. These are they.
     * @return A button panel
     */
    protected JComponent getButtonPane()
    {
        JPanel buttons = new JPanel();
        JPanel retcode = new JPanel();

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
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev)
            {
                hideDialog();
            }
        });
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev)
            {
                try
                {
                    screenToLocal();
                    al.actionPerformed(ev);
                    if (dialog != null) dialog.pack();
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
     * Add a Choice to our set of panels
     */
    protected void addChoice(String key, Choice model)
    {
        String path = Config.getPath(key);

        try
        {
            // Check if we want to display this option
            Field field = FieldMap.getField(model.getType(), model.getTypeOptions());
            fields.put(key, field);

            if (model.getUserLevel() <= UserLevel.getUserLevel())
            {
                // Get or create a FieldPanel
                FormPane card = (FormPane) decks.get(path);

                if (card == null)
                {
                    card = new FormPane();
                    decks.put(path, card);
                    cards++;
                }

                // Add the Field to the FieldPanel
                JComponent comp = field.getComponent();
                comp.setToolTipText(model.getHelpText());

                String name = Config.getLeaf(key) + ":";
                card.addEntry(name, comp);

                // Fill in the current value
                String value = config.getLocal(key);
                field.setValue(value);
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Add a Choice to our set of panels
     */
    protected void removeChoice(String key, Choice model)
    {
        String path = Config.getPath(key);

        try
        {
            Field field = (Field) fields.get(key);
            if (field != null)
            {
                fields.remove(field);
                FormPane card = (FormPane) decks.get(path);

                // Remove field from card.
                String name = Config.getLeaf(key) + ":";
                card.removeEntry(name);

                if (card.isEmpty())
                    decks.remove(card);
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Create a dialog to house a TreeConfig component
     * using the default set of Fields
     * @param parent A component to use to find a frame to use as a dialog parent
     */
    public void showDialog(Component parent, ActionListener al)
    {
        this.al = al;

        if (dialog == null)
        {
            dialog = new JDialog((JFrame) SwingUtilities.getRoot(parent));
            LookAndFeelChoices.addWindow(dialog);

            dialog.getRootPane().setDefaultButton(ok);
            dialog.getContentPane().add(this);

            // Why is this only available in Frames?
            // dialog.setIconImage(task_small);
        }

        // Update from config
        localToScreen();
        dialog.setTitle(config.getTitle());

        // size and position
        dialog.setSize(800, 500);
        dialog.pack();
        GuiUtil.centerWindow(dialog);
        dialog.setModal(true);

        // show
        dialog.setVisible(true);
    }

    /**
     * Close any open dialogs
     */
    public void hideDialog()
    {
        if (dialog != null)
        {
            LookAndFeelChoices.removeWindow(dialog);
            dialog.setVisible(false);
        }
    }

    /**
     * Take the data displayed on screen an copy it to the local
     * storage area.
     */
    public void screenToLocal()
    {
        Enumeration en = config.getNames();
        while (en.hasMoreElements())
        {
            try
            {
                String key = (String) en.nextElement();
                Field field = (Field) fields.get(key);
                String value = field.getValue();
                config.setLocal(key, value);
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * Take the data in the local storage area and copy it on screen.
     */
    public void localToScreen()
    {
        Enumeration en = config.getNames();
        while (en.hasMoreElements())
        {
            try
            {
                String key = (String) en.nextElement();
                Field field = (Field) fields.get(key);
                String value = config.getLocal(key);
                field.setValue(value);
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }
        }
    }

    /** How many cards have we created - we only need a tree if there are 2 or more cards */
    protected int cards = 0;

    /** Action when the user clicks on accept */
    protected ActionListener al;

    /** The class that represents the Fields that we display */
    protected Config config;

    /** The Ok button */
    protected JButton ok = new JButton("OK");

    /** The cancel button */
    protected JButton cancel = new JButton("Cancel");

    /** The apply button */
    protected JButton apply = new JButton("Apply");

    /** The help button */
    protected JButton help = new JButton("Help");

    /** The dialog that we are displayed in */
    protected JDialog dialog;

    /** A fast way to get at the configuration panels */
    protected Hashtable decks = new Hashtable();

    /** The set of fields that we are displaying */
    protected Hashtable fields = new Hashtable();

    /** The large task icon */
    protected static ImageIcon task = GuiUtil.loadImageIcon("org/crosswire/resources/task.gif");

    /** The small task icon */
    protected static ImageIcon task_small = GuiUtil.loadImageIcon("org/crosswire/resources/task_small.gif");
}
