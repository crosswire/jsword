package org.crosswire.common.config.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.Config;
import org.crosswire.common.config.ConfigEvent;
import org.crosswire.common.config.ConfigListener;
import org.crosswire.common.swing.FormPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;

/**
 * Page of a Config.
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
public abstract class AbstractConfigEditor extends JPanel implements ConfigEditor, ButtonPaneListener
{
    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.ConfigEditor#init(org.crosswire.common.config.Config)
     */
    public void construct(Config aConfig)
    {
        this.config = aConfig;

        initializeGUI();

        config.addConfigListener(new ConfigListener()
        {
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
        Iterator it = config.getNames();
        while (it.hasNext())
        {
            String key = (String) it.next();
            Choice model = config.getChoice(key);

            addChoice(key, model);
        }

        updateTree();

        SwingUtilities.updateComponentTreeUI(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.ConfigEditor#showDialog(java.awt.Component, java.awt.event.ActionListener)
     */
    public void showDialog(Component parent, ActionListener newal)
    {
        this.al = newal;

        if (dialog == null)
        {
            dialog = new JDialog((JFrame) SwingUtilities.getRoot(parent));
            // NOTE: when we tried dynamic laf update, dialog needed special treatment
            // LookAndFeelUtil.addComponentToUpdate(dialog);

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
     * Create the GUI
     */
    protected abstract void initializeGUI();

    /**
     * Update the tree structure
     */
    protected abstract void updateTree();

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.ButtonPaneListener#ok(java.awt.event.ActionEvent)
     */
    public void okPressed(ActionEvent ev)
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

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.ButtonPaneListener#cancel(java.awt.event.ActionEvent)
     */
    public void cancelPressed(ActionEvent ev)
    {
        hideDialog();
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.config.swing.ButtonPaneListener#apply(java.awt.event.ActionEvent)
     */
    public void applyPressed(ActionEvent ev)
    {
        try
        {
            screenToLocal();
            al.actionPerformed(ev);
            if (dialog != null)
            {
                dialog.pack();
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
    protected void addChoice(String key, Choice model)
    {
        String path = Config.getPath(model.getFullPath());

        try
        {
            // Check if we want to display this option
            Field field = FieldMap.getField(model);
            fields.put(key, field);

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

            String name = Config.getLeaf(model.getFullPath()) + ':';
            card.addEntry(name, comp);

            // Fill in the current value
            String value = config.getLocal(key);
            field.setValue(value);
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
        String path = Config.getPath(model.getFullPath());

        try
        {
            Field field = (Field) fields.get(key);
            if (field != null)
            {
                fields.remove(field);
                FormPane card = (FormPane) decks.get(path);

                // Remove field from card.
                String name = Config.getLeaf(model.getFullPath()) + ':';
                card.removeEntry(name);

                if (card.isEmpty())
                {
                    decks.remove(card);
                }
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Close any open dialogs
     */
    protected void hideDialog()
    {
        if (dialog != null)
        {
            // NOTE: when we tried dynamic laf update, dialog needed special treatment
            //LookAndFeelUtil.removeComponentToUpdate(dialog);
            dialog.setVisible(false);
        }
    }

    /**
     * Take the data displayed on screen an copy it to the local
     * storage area.
     */
    protected void screenToLocal()
    {
        Iterator it = config.getNames();
        while (it.hasNext())
        {
            try
            {
                String key = (String) it.next();
                Field field = (Field) fields.get(key);
                String value = field.getValue();

                if (value == null)
                {
                    log.error("null value from key=" + key); //$NON-NLS-1$
                }

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
    protected void localToScreen()
    {
        Iterator it = config.getNames();
        while (it.hasNext())
        {
            try
            {
                String key = (String) it.next();

                Field field = (Field) fields.get(key);
                String value = config.getLocal(key);

                if (field == null)
                {
                    log.error("Null field from key=" + key + ", skipping setting value=" + value); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    field.setValue(value);
                }
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AbstractConfigEditor.class);

    /**
     * How many cards have we created - we only need a tree if there are 2 or more cards
     */
    protected int cards;

    /**
     * Action when the user clicks on accept
     */
    protected ActionListener al;

    /**
     * The class that represents the Fields that we display
     */
    protected Config config;

    /**
     * The dialog that we are displayed in
     */
    protected JDialog dialog;

    /**
     * A fast way to get at the configuration panels
     */
    protected Map decks = new HashMap();

    /**
     * The set of fields that we are displaying
     */
    protected Map fields = new HashMap();

    /**
     * The large task icon
     */
    protected static final ImageIcon TASK_ICON_LARGE = GuiUtil.getIcon("toolbarButtonGraphics/general/Preferences24.gif"); //$NON-NLS-1$

    /**
     * The small task icon
     */
    protected static final ImageIcon TASK_ICON_SMALL = GuiUtil.getIcon("toolbarButtonGraphics/general/Preferences16.gif"); //$NON-NLS-1$
}
