
package org.crosswire.jsword.view.swing.book;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.BookMetaData;

/**
 * A custom list view that paints icons alongside the words. This is a
 * simple modification of DeafultListCellRenderer
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
public class BookListCellRenderer extends JLabel implements ListCellRenderer
{
    /**
     * Constructs a default renderer object for an item in a list.
     */
    public BookListCellRenderer()
    {
        if (no_focus == null)
        {
            no_focus = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        }

        setOpaque(true);
        setBorder(no_focus);
    }

    /**
     * This is the only method defined by ListCellRenderer.  We just
     * reconfigure the Jlabel each time we're called.
     * @param list The JLists that we are part of
     * @param value Value to display
     * @param index Cell index
     * @param selected Is the cell selected
     * @param focus Does the list and the cell have the focus
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus)
    {
        if (selected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        BookMetaData bmd = (BookMetaData) value;
        if (bmd == null)
        {
            setText("None");
            setToolTipText(null);
            setIcon(null);
            setEnabled(false);
            return this;
        }
        else
        {
            setText(bmd.getName());
            setToolTipText(bmd.getFullName());
            setIcon(ICON_SMALL);

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder(focus ? UIManager.getBorder("List.focusCellHighlightBorder") : no_focus);

            return this;
        }
    }

    /**
     * The small version icon
     */
    private static final ImageIcon ICON_SMALL = GuiUtil.getIcon("images/Passage16.gif");

    /**
     * border if we do not have focus
     */
    private static Border no_focus;
}
