package org.crosswire.common.swing;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * A MapCellRenderer that renders multiline text.
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
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class MapCellRenderer extends JTextArea implements TableCellRenderer
{
    /**
     * Create a MapCellRenderer
     */
    public MapCellRenderer()
    {
        super();
        // LATER(DM): wrapping requires the recomputation of row height.
        // This would require grabbing wrapping events
        // and for the MapTable to listen for them and to adjust row height.
        // Not sure this is worth the effort.
        // setLineWrap(true);
        // setWrapStyleWord(true);
    }

    /**
     * Overrides <code>JComponent.setForeground</code> to assign
     * the unselected-foreground color to the specified color.
     *
     * @param c set the foreground color to this value
     */
    public void setForeground(Color c)
    {
        super.setForeground(c);
        unselectedForeground = c;
    }

    /**
     * Overrides <code>JComponent.setBackground</code> to assign
     * the unselected-background color to the specified color.
     *
     * @param c set the background color to this value
     */
    public void setBackground(Color c)
    {
        super.setBackground(c);
        unselectedBackground = c;
    }

    /**
     * Notification from the <code>UIManager</code> that the look and feel
     * [L&F] has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     *
     * @see javax.swing.JComponent#updateUI()
     */
    public void updateUI()
    {
        super.updateUI();
        setForeground(null);
        setBackground(null);
    }

    /**
     * Returns the default table cell renderer.
     * @param table the <code>JTable</code>
     * @param value the value to assign to the cell at <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (isSelected)
        {
            super.setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        }
        else
        {
            super.setForeground((unselectedForeground != null)
                            ? unselectedForeground
                            : table.getForeground());
            super.setBackground((unselectedBackground != null)
                            ? unselectedBackground
                            : table.getBackground());
        }

        setFont(table.getFont());

        if (hasFocus)
        {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); //$NON-NLS-1$
            if (table.isCellEditable(row, column))
            {
                super.setForeground(UIManager.getColor("Table.focusCellForeground")); //$NON-NLS-1$
                super.setBackground(UIManager.getColor("Table.focusCellBackground")); //$NON-NLS-1$
            }
        }
        else
        {
            setBorder(noFocusBorder);
        }

        setText(value == null ? "" : value.toString()); //$NON-NLS-1$

        return this;
    }

    /**
     * <code>noFocusBorder</code> is used to present the cell as with
     * the DefaultTableCellRenderer
     */
    private static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    /**
     * We need a place to store the color the JTextArea should be returned
     * to after its foreground and background colors have been set
     * to the selection background color.
     * <code>unselectedForeground</code> is used to present the cell as with
     * the DefaultTableCellRenderer
     */
    private Color unselectedForeground;

    /**
     * <code>unselectedBackground</code> is used to present the cell as with
     * the DefaultTableCellRenderer
     */
    private Color unselectedBackground;
}
