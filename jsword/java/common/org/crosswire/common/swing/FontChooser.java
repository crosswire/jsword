package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * FontChooserBean allows the user to select a font in a similar way to a
 * FileSelectionDialog.
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
public class FontChooser extends JPanel
{
    /**
     * Create a FontChooser.
     */
    public FontChooser()
    {
        ItemListener changer = new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                fireStateChange();
            }
        };

        font = defaultFont.getFont();
        name.setModel(new CustomComboBoxModel());
        name.setRenderer(new CustomListCellRenderer());
        name.setSelectedItem(font.deriveFont(Font.PLAIN, RENDERED_FONT_SIZE));
        name.addItemListener(changer);

        for (int i = 5; i < 20; i++)
        {
            size.addItem(new Integer(i));
        }

        size.setSelectedItem(new Integer(font.getSize()));
        size.addItemListener(changer);

        bold.setSelected(font.isBold());
        bold.addItemListener(changer);

        italic.setSelected(font.isItalic());
        italic.addItemListener(changer);

        setLayout(new GridLayout(2, 2));

        add(name);
        add(size);
        add(bold);
        add(italic);
    }

    /**
     * Display a FontChooser in a dialog
     */
    public static Font showDialog(Component parent, String title, Font initial)
    {
        JPanel buttons = new JPanel();
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        Component root = SwingUtilities.getRoot(parent);
        final FontChooser fontc = new FontChooser();

        // JDK: For some reason we can't do this in the version of Swing that we are on
        // it is only available in the JDK1.2.2 implementation
        // fontc.dialog = (root instanceof JFrame)
        //              ? new JDialog((JFrame) root, title, true)
        //              : new JDialog((JDialog) root, title, true);
        fontc.dialog = new JDialog((JFrame) root, title, true);

        // Not sure if this is the right thing to do?
        fontc.name.setSelectedItem(initial);

        buttons.setLayout(new FlowLayout());
        buttons.add(ok);
        buttons.add(cancel);

        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ex)
            {
                fontc.dialog.setVisible(false);
            }
        });
        cancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ex)
            {
                fontc.dialog.setVisible(false);
                fontc.font = null;
            }
        });

        fontc.setBorder(BorderFactory.createTitledBorder("Select Font"));

        fontc.dialog.getRootPane().setDefaultButton(ok);
        fontc.dialog.getContentPane().setLayout(new BorderLayout());
        fontc.dialog.getContentPane().add(fontc, BorderLayout.NORTH);
        fontc.dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
        fontc.dialog.setSize(800, 500);
        fontc.dialog.pack();
        GuiUtil.centerWindow(fontc.dialog);
        fontc.dialog.setVisible(true);

        // Why is this only available in Frames?
        // dialog.setIconImage(task_small);

        fontc.dialog.dispose();

        return fontc.font;
    }

    /**
     * Set the Font displayed
     * @param newFont The current Font
     */
    public void setStyle(Font newFont)
    {
        suppressEvents = true;

        if (newFont == null)
        {
            return;
        }

        CustomComboBoxModel model = (CustomComboBoxModel) name.getModel();
        model.setSelectedItem(newFont.deriveFont(Font.PLAIN, RENDERED_FONT_SIZE));

        bold.setSelected(newFont.isBold());
        italic.setSelected(newFont.isItalic());
        size.setSelectedItem(new Integer(font.getSize()));

        suppressEvents = false;
        fireStateChange();
    }

    /**
     * @return The currently selected font
     */
    public Font getStyle()
    {
        Font selected = (Font) name.getSelectedItem();

        if (selected == null)
        {
            return defaultFont.getFont();
        }

        int font_style = (bold.isSelected() ? Font.BOLD : Font.PLAIN) | (italic.isSelected() ? Font.ITALIC : Font.PLAIN);
        int font_size = ((Integer) size.getSelectedItem()).intValue();

        return selected.deriveFont(font_style, font_size);
    }

    /**
     * Interface for people to be notified of changes to the
     * current Font.
     * @param li The new listener class
     */
    public void addPropertyChangeListener(PropertyChangeListener li)
    {
        listeners.addPropertyChangeListener(li);
    }

    /**
     * Interface for people to be notified of changes to the
     * current Font.
     * @param li The listener class to be deleted
     */
    public void removePropertyChangeListener(PropertyChangeListener li)
    {
        listeners.removePropertyChangeListener(li);
    }

    /**
     * When something chenages we must inform out listeners.
     */
    protected void fireStateChange()
    {
        Font old = font;
        font = getStyle();

        if (!suppressEvents)
        {
            listeners.firePropertyChange("style", old, font);
        }
    }

    /**
     * Model for the font style drop down
     */
    static class CustomComboBoxModel extends AbstractListModel implements ComboBoxModel
    {
        /**
         * Create a custom data model for a JComboBox
         */
        protected CustomComboBoxModel()
        {
            String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            // For older JDKs use: font_names = getToolkit().getFontList();
            fonts = new Font[names.length];

            for (int i = 0; i < fonts.length; i++)
            {
                fonts[i] = new Font(names[i], Font.PLAIN, RENDERED_FONT_SIZE);
//                System.err.println("Font name = " + fonts[i].getName()
//                        + " Style = " + fonts[i].getStyle()
//                        + " Size = " + fonts[i].getSize());
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
         */
        public void setSelectedItem(Object selection)
        {
            this.selection = selection;
            fireContentsChanged(this, -1, -1);
        }

        /* (non-Javadoc)
         * @see javax.swing.ComboBoxModel#getSelectedItem()
         */
        public Object getSelectedItem()
        {
            return selection;
        }

        /* (non-Javadoc)
         * @see javax.swing.ListModel#getSize()
         */
        public int getSize()
        {
            return fonts.length;
        }

        /* (non-Javadoc)
         * @see javax.swing.ListModel#getElementAt(int)
         */
        public Object getElementAt(int index)
        {
            return fonts[index];
        }

        /**
         * An array of the fonts themselves
         */
        private Font[] fonts = null;

        /**
         * The currently selected item
         */
        private Object selection;
    }

    /**
     * An extension of JLabel that resets it's font so that
     * it can be used to render the items in a JComboBox
     */
    private static class CustomListCellRenderer extends DefaultListCellRenderer
    {
        /* (non-Javadoc)
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        public Component getListCellRendererComponent(JList listbox, Object value, int index, boolean selected, boolean focus)
        {
            if (value == null)
            {
                setText("<null>");
                setFont(defaultFont.getFont());
            }
            else
            {
                Font afont = (Font) value;
                setText(afont.getFamily());
                setFont(afont);
            }

            return this;
        }
    }

    /**
     * A label that we can use to get defaults
     */
    protected static JLabel defaultFont = new JLabel();

    /**
     * The dialog box
     */
    protected JDialog dialog = null;

    /**
     * People that want to know about font changes
     */
    protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    /**
     * The current font
     */
    protected Font font = null;

    /**
     * The default size of the rendered font
     */
    private static final int RENDERED_FONT_SIZE = 16;

    /**
     * The choice of font name
     */
    protected JComboBox name = new JComboBox();

    /**
     * Bold font?
     */
    protected JCheckBox bold = new JCheckBox("Bold");

    /**
     * Italic font?
     */
    protected JCheckBox italic = new JCheckBox("Italic");

    /**
     * The font size
     */
    protected JComboBox size = new JComboBox();

    /**
     * Are we doing some processing, that makes us not want to send events?
     */
    protected boolean suppressEvents;
}
