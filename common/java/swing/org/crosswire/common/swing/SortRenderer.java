/*
 * Distribution Licence:
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details.
 * The License is available on the internet at:
 *     http://www.gnu.org/copyleft/gpl.html,
 * or by writing to:
 *     Free Software Foundation, Inc.
 *     59 Temple Place - Suite 330
 *     Boston, MA 02111-1307, USA
 * 
 * The copyright to this program is held by it's authors
 * Copyright: 2004
 */
package org.crosswire.common.swing;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * A SortRenderer indicates the column that is sorted by italizing it.
 * 
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public class SortRenderer extends DefaultTableCellRenderer
{

    /**
     * Field pressedColumn
     */
    private TableColumn pressedColumn;
    /**
     * Field model
     */
    private RowTableModel model;

    /**
     * Constructor for SortRenderer
     * @param stm SegmentTableModel
     */
    public SortRenderer(RowTableModel stm)
    {
        model = stm;
        pressedColumn = null;
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Method getTableCellRendererComponent
     * @param table JTable
     * @param value Object
     * @param isSelected boolean
     * @param hasFocus boolean
     * @param row int
     * @param column int
     * @return Component
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (table != null)
        {
            setToolTipText(model.getHeaderToolTip(column));
            final JTableHeader header = table.getTableHeader();
            final TableColumn tableColumn = table.getColumnModel().getColumn(column);
            if (header != null)
            {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                final Font headerFont = header.getFont();
                if (tableColumn == pressedColumn)
                {
                    setFont(headerFont.deriveFont(Font.ITALIC));
                }
                else
                {
                    setFont(headerFont);
                }
            }
        }

        setText((value == null) ? "" : value.toString()); //$NON-NLS-1$
        setBorder(UIManager.getBorder("TableHeader.cellBorder")); //$NON-NLS-1$
        return this;
    }

    /**
     * Method getPressedColumn
     * @return the table column
     */
    public TableColumn getPressedColumn()
    {
        return pressedColumn;
    }

    /**
     * Method setPressedColumn
     * @param tc the table column
     */
    public void setPressedColumn(TableColumn tc)
    {
        pressedColumn = tc;
    }

}
