
package org.crosswire.jsword.view.swing.passage;

import java.awt.Component;
import java.io.Serializable;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.VerseRange;

/**
 * Renders a Passage in a JList.
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
public class PassageListCellRenderer implements ListCellRenderer, Serializable
{
    /**
     * Constructs a default renderer object for an item in a list.
     */
    public PassageListCellRenderer()
    {
        border = new EmptyBorder(1, 1, 1, 1);

        label.setBorder(border);
        label.setOpaque(true);
        label.setIcon(GuiUtil.getIcon("images/Passage16.gif"));
    }

    /**
     * Customize something to display the Passage component
     * @return The customized component
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus)
    {
        if (selected)
        {
            label.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        }
        else
        {
            label.setBackground(list.getBackground());
            label.setForeground(list.getForeground());
        }

        if (value instanceof VerseRange)
        {
            try
            {
                VerseRange range = (VerseRange) value;
                String text = (String) hash.get(range);

                Passage ref = PassageFactory.createPassage();
                ref.add(range);
                if (text == null)
                {
                    BookData bdata = Defaults.getBibleMetaData().getBible().getData(ref);
                    String simple = bdata.getPlainText();
                    text = "<html><b>"+range.getName()+"</b> "+simple;
                    hash.put(range, text);
                }

                label.setText(text);
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
                label.setText("Error");
            }
        }
        else
        {
            label.setText((value == null) ? "" : value.toString());
        }

        label.setEnabled(list.isEnabled());
        label.setFont(list.getFont());
        label.setBorder((focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : border);

        return label;
    }

    /** The label to display if the item is not selected */
    private JLabel label = new JLabel();

    /** The border if the label is selected */
    private Border border;

    /** A cache of Bible texts */
    private Hashtable hash = new Hashtable();
}

