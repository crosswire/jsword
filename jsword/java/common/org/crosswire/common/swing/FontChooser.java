
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Hashtable;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * FontChooserBean allows the user to select a font in a similar
 * way to a FileSelectionDialog.
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
public class FontChooser extends JPanel
{
    /**
    * Create a FontChooser.
    */
    public FontChooser()
    {
        FontChangeListener changer = new FontChangeListener();

        name.setModel(new CustomComboBoxModel());
        name.setRenderer(new TestCellRenderer(name));
        name.addItemListener(changer);

        for (int i=5; i<20; i++)
            size.addItem(new Integer(i));

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
        FontChooser fontc = new FontChooser();

        /*
        For some reason we can't do this in the version of Swing that we are on
        it is only available in the JDK1.2.2 implementation
        fontc.dialog = (root instanceof JFrame)
                     ? new JDialog((JFrame) root, title, true)
                     : new JDialog((JDialog) root, title, true);
        */
        fontc.dialog = new JDialog((JFrame) root, title, true);

        buttons.setLayout(new FlowLayout());
        buttons.add(ok);
        buttons.add(cancel);

        ok.addActionListener(fontc.new OKActionListener());
        cancel.addActionListener(fontc.new CancelActionListener());

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

    class OKActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent ex)
        {
            dialog.setVisible(false);
        }
    }

    class CancelActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent ex)
        {
            dialog.setVisible(false);
            font = null;
        }
    }

    /**
    * Set the Font displayed
    * @param font The current Font
    */
    public void setStyle(Font font)
    {
        suppress_events = true;

        CustomComboBoxModel model = (CustomComboBoxModel) name.getModel();
        model.setSelectedFont(font);

        bold.setSelected(font.isBold());
        italic.setSelected(font.isItalic());
        size.setSelectedItem(new Integer(font.getSize()));

        suppress_events = false;
        fireStateChange();
    }

    /**
    * @return The currently selected font
    */
    public Font getStyle()
    {
        Hashtable hash = (Hashtable) name.getSelectedItem();
        Font base_font = (Font) hash.get("font");

        int font_style = 0 | (bold.isSelected() ? Font.BOLD : 0) | (italic.isSelected() ? Font.ITALIC : 0);
        int font_size = ((Integer) size.getSelectedItem()).intValue();

        return new Font(base_font.getName(), font_style, font_size);
    }

    /**
    * Helper class to note changes in the Font
    */
    class FontChangeListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent ev)
        {
            fireStateChange();
        }
    }

    /**
    * Helper class to note changes in the Font
    */
    class NameListener implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent ev)
        {
            fireStateChange();
        }
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
        if (!suppress_events)
            listeners.firePropertyChange("style", font, font = getStyle());
    }

    /**
    * Model for the font style drop down
    */
    class CustomComboBoxModel extends AbstractListModel implements ComboBoxModel
    {
        /**
        * Create a custom data model for a JComboBox
        */
        public CustomComboBoxModel()
        {
            font_names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            // For older JDKs use: font_names = getToolkit().getFontList();
            fonts = new Font[font_names.length];
            cache = new Hashtable[font_names.length];

            for (int i=0; i<fonts.length; i++)
            {
                fonts[i] = new Font(font_names[i], 0, 16);
            }
        }

        /**
        * Make this object the current one
        * @param selection The list item
        */
        public void setSelectedItem(Object selection)
        {
            this.selection = selection;
            fireContentsChanged(this, -1, -1);
        }

        /**
        * Choose a Font from the available list
        * @param font The font to make current
        */
        public void setSelectedFont(Font font)
        {
            for (int i=0; i<fonts.length; i++)
            {
                if (font.equals(fonts[i]))
                {
                    setSelectedItem(getElementAt(i));
                }
            }

            fireContentsChanged(this, -1, -1);
        }

        /**
        * @return The currently selected Font
        */
        public Object getSelectedItem()
        {
            return selection;
        }

        /**
        * The number of fonts in the list
        * @return The font count
        */
        public int getSize()
        {
            return fonts.length;
        }

        /**
        * Get the font at a given offset
        * @param index The offset of the Font to retrieve
        * @return The selected font
        */
        public Object getElementAt(int index)
        {
            if (cache[index] != null)
            {
                return cache[index];
            }
            else
            {
                Hashtable result = new Hashtable();
                result.put("title", font_names[index]);
                result.put("font", fonts[index]);
                cache[index] = result;
                return result;
            }
        }

        /** An array of the names of all the available fonts */
        private String[] font_names = null;

        /** An array of the fonts them selves */
        private Font[] fonts = null;

        /** The currently selected item */
        private Object selection;

        /** To collect fonts and names together */
        private Hashtable[] cache;
    }

    /**
    * An extension of JLabel that resets it's font so that
    * it can be used to render the items in a JComboBox
    */
    class TestCellRenderer extends JLabel implements ListCellRenderer
    {
        /**
        * Create a specialized JLabel
        * @param combobox The thing we are custimizing for
        */
        public TestCellRenderer(JComboBox combobox)
        {
            this.combobox = combobox;
            setOpaque(true);
        }

        /**
        * Set ourselves up to render for this particular font
        * @param listbox The list being displyed by the ComboBox
        * @param value The hash created by the CustomComboBoxModel
        * @param index The item in the list to render
        * @param selected Is this item selected?
        * @param focus Are we pointing at the item?
        * @return <code>this</code> customized for the given item
        */
        public Component getListCellRendererComponent(JList listbox, Object value, int index, boolean selected, boolean focus)
        {
            Hashtable hash = (Hashtable) value;

            if (UIManager.getLookAndFeel().getName().equals("CDE/Motif"))   setOpaque(index != -1);
            else                                                            setOpaque(true);

            if (value == null)
            {
                setText("");
                setIcon(null);
            }
            else if (selected)
            {
                setBackground(UIManager.getColor("ComboBox.selectionBackground"));
                setForeground(UIManager.getColor("ComboBox.selectionForeground"));
            }
            else
            {
                setBackground(UIManager.getColor("ComboBox.background"));
                setForeground(UIManager.getColor("ComboBox.foreground"));
            }

            setText((String) hash.get("title"));
            setFont((Font) hash.get("font"));
            return this;
        }

        /** The thing we are rendering */
        private JComboBox combobox = null;
    }

    /** The dialog box */
    protected JDialog dialog = null;

    /** People that want to know about font changes */
    protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    /** The default font */
    protected Font font = new Font("Serif", Font.PLAIN, 10);

    /** The choice of font name */
    protected JComboBox name = new JComboBox();

    /** Bold font? */
    protected JCheckBox bold = new JCheckBox("Bold");

    /** Italic font? */
    protected JCheckBox italic = new JCheckBox("Italic");

    /** The font size */
    protected JComboBox size = new JComboBox();

    /** Are we doing some processing, that makes us not want to send events? */
    protected boolean suppress_events;
}
