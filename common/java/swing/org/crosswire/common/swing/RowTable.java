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
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Presents a table of items to a user in a table.
 * 
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public class RowTable extends JTable
{
    /**
     * Field ONE_STANDARD_CHARACTER
     */
    private static final String ONE_STANDARD_CHARACTER = "M"; //$NON-NLS-1$
    /**
     * Field TWO_STANDARD_CHARACTERS
     */
    private static final String TWO_STANDARD_CHARACTERS = "MM"; //$NON-NLS-1$
    /**
     * Field PADDING
     */
    private static final int PADDING = 3;

    /**
     * Constructor for RowTable
     * @param aList
     * @param columns
     */
    public RowTable(List aList, RowColumns columns)
    {
        super(new RowTableModel(aList, columns));
        setSortRenderer();

        // Don't display vertical lines in table
        //		getColumnModel().setColumnMargin(0);

        setColumnWidths(columns.getCharacterWidths(), columns.getFixedWidths());

        getTableHeader().addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                sort(getColumnModel().getColumnIndexAtX(e.getX()));
            }
        });
    }

    /**
     * Save the selection so it can be restored after sorting.
     * @param aTable
     * @return List
     */
    private List saveSelection(JTable aTable)
    {
        final ListSelectionModel lsm = aTable.getSelectionModel();
        final RowTableModel tm = (RowTableModel) aTable.getModel();
        final int first = lsm.getMinSelectionIndex();
        final int last = lsm.getMaxSelectionIndex();
        final List objs = new ArrayList();
        if (first != -1)
        {
            for (int i = first; i <= last; i++)
            {
                if (lsm.isSelectedIndex(i))
                {
                    objs.add(tm.getRow(i));
                }
            }
        }
        return objs;
    }

    /**
     * load the selections
     * @param aTable JTable
     * @param objs List
     */
    private void loadSelection(JTable aTable, List objs)
    {
        final ListSelectionModel lsm = aTable.getSelectionModel();
        final RowTableModel tm = (RowTableModel) aTable.getModel();
        // reset the selection
        Object obj = null;
        int where = -1;
        for (int i = 0; i < objs.size(); i++)
        {
            obj = objs.get(i);
            where = tm.getRow(obj);
            if (where != -1)
            {
                lsm.addSelectionInterval(where, where);
            }
        }
        scrollToVisible(aTable);
    }

    /**
     * Method scrollToVisible
     * @param aTable JTable
     */
    private void scrollToVisible(JTable aTable)
    {
        final ListSelectionModel lsm = aTable.getSelectionModel();
        final int first = lsm.getMinSelectionIndex();
        final int last = lsm.getMaxSelectionIndex();
        if (first != -1)
        {
            final Rectangle bounds = getRowBounds(aTable, first, last);
            if (isVerticallyVisible(aTable, bounds) == false)
            {
                // Is SwingUtilities.invokeLater needed ???
                aTable.scrollRectToVisible(bounds);
            }
        }
    }

    /**
     * Method selectRow
     * @param row int
     */
    public void selectRow(int row)
    {
        final ListSelectionModel lsm = getSelectionModel();
        lsm.clearSelection();
        lsm.setSelectionInterval(row, row);
        scrollToVisible(this);
    }

    /**
     * Method getRowBounds
     * @param table JTable
     * @param first int
     * @param last int
     * @return Rectangle
     */
    private Rectangle getRowBounds(JTable table, int first, int last)
    {
        Rectangle result = table.getCellRect(first, -1, true);
        result = result.union(table.getCellRect(last, -1, true));
        final Insets insets = table.getInsets();
        result.x = insets.left;
        result.width = table.getWidth() - insets.left - insets.right;
        return result;
    }

    /**
     * Method isVerticallyVisible
     * @param aTable JTable
     * @param r Rectangle
     * @return boolean
     */
    private boolean isVerticallyVisible(JTable aTable, Rectangle r)
    {
        final Rectangle visible = aTable.getVisibleRect();
        return visible.y <= r.y && visible.y + visible.height >= r.y + r.height;
    }

    /**
     * Method setColumnWidths
     * @param widths int[]
     * @param fixed boolean[]
     */
    private void setColumnWidths(int[] widths, boolean[] fixed)
    {
        final int mWidth = getStandardCharacterWidth();
        final TableColumnModel tcm = getColumnModel();
        // The << 1 accounts for two margins
        // The + PADDING accounts for an extra pixel on either side
        // and an extra pixel for between the columns
        //  that the text needs to not display ...
        final int margins = (tcm.getColumnMargin() << 1) + PADDING;
        TableColumn tc = null;
        int width = -1;
        for (int i = 0; i < widths.length; i++)
        {
            tc = tcm.getColumn(i);
            width = widths[i] * mWidth + margins;
            if (fixed[i])
            {
                tc.setMinWidth(width);
                tc.setMaxWidth(width);
            }
            else
            {
                tc.setPreferredWidth(width);
            }
        }
    }

    /**
     * Method setSortRenderer
     */
    private void setSortRenderer()
    {
        final TableCellRenderer sortRenderer = new SortRenderer((RowTableModel) getModel());
        //		TableCellRenderer rowRenderer = new RowRenderer();
        final TableColumnModel model = getColumnModel();
        final int colCount = model.getColumnCount();
        TableColumn tc = null;
        for (int i = 0; i < colCount; i++)
        {
            tc = model.getColumn(i);
            tc.setHeaderRenderer(sortRenderer);
        }
    }

    /**
     * Size each column to something reasonable
     * We do this by getting the width of the letter 'M"
     * from the default Table Header Renderer
     * and set the preferred width of the column
     * as the width of some number of 'M's.
     * @return int
     */
    private int getStandardCharacterWidth()
    {
        // The preferredSize of the component is more than just the character
        // So we remove the extra determining the delta
        // between one and two chars
        final JTableHeader th = getTableHeader();
        final TableCellRenderer renderer = th.getDefaultRenderer();
        Component comp = renderer.getTableCellRendererComponent(this, ONE_STANDARD_CHARACTER, false, false, 0, 0);
        final int oneStandardCharacterWidth = comp.getPreferredSize().width;
        comp = renderer.getTableCellRendererComponent(this, TWO_STANDARD_CHARACTERS, false, false, 0, 0);
        final int twoStandardCharactersWidth = comp.getPreferredSize().width;
        return twoStandardCharactersWidth - oneStandardCharacterWidth;
    }

    /**
     * Method addListSelectionListener
     * @param listener ListSelectionListener
     */
    public void addListSelectionListener(ListSelectionListener listener)
    {
        getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * Method getPreferredHeight
     * @param numRows int
     * @return int
     */
    public int getPreferredHeight(int numRows)
    {
        int newHeight = getRowHeight() * numRows;
        // The following may be needed for Java 1.4
        // newHeight += table.getIntercellSpacing().height * (numRows + 1);
        newHeight += getTableHeader().getPreferredSize().height;
        final Insets insets = getInsets();
        newHeight += insets.top + insets.bottom;
        return newHeight;
    }

    /**
     * Method sort
     * @param col int
     */
    public void sort(int col)
    {
        if (col != -1)
        {
            final TableColumnModel tcm = getColumnModel();
            final TableColumn tc = tcm.getColumn(col);
            final SortRenderer renderer = (SortRenderer) tc.getHeaderRenderer();
            renderer.setPressedColumn(tc);
        }
        final List objs = saveSelection(this);
        getSelectionModel().clearSelection();
        ((RowTableModel) getModel()).sort(convertColumnIndexToModel(col));
        loadSelection(this, objs);
    }

    public void reset()
    {
        final RowTableModel stm = (RowTableModel) getModel();
        final ListSelectionModel lsm = getSelectionModel();
        getSelectionModel().clearSelection();
        lsm.clearSelection();
        stm.reset();
    }

}