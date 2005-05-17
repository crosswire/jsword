/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.swing;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A simple table that renders text, potentially multiline.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public class MapTable extends JTable
{
    /**
     * Constructor for a MapTable
     */
    public MapTable()
    {
        this(null);
    }

    /**
     * Constructor for a MapTable
     * @param mtm
     */
    public MapTable(MapTableModel mtm)
    {
        super(mtm);
        initialized = true;
        setDefaultRenderer();
    }

    /* (non-Javadoc)
     * @see javax.swing.JTable#setModel(javax.swing.table.TableModel)
     */
    public void setModel(TableModel dm)
    {
        assert !initialized || dm instanceof MapTableModel;

        super.setModel(dm);
        setDefaultRenderer();
    }

    /* (non-Javadoc)
     * @see javax.swing.JTable#setDefaultRenderer(java.lang.Class, javax.swing.table.TableCellRenderer)
     */
    public void setDefaultRenderer(Class columnclass, TableCellRenderer renderer)
    {
        assert renderer instanceof MapCellRenderer;

        super.setDefaultRenderer(columnclass, renderer);
    }

    /**
     * Sets the default renderer for all cells to a MapCellRenderer.
     * The default renderers must be created before setDefaultRenderer is
     * called. This is done in JTable after setModel is called.
     */
    private void setDefaultRenderer()
    {
        if (initialized  && (getModel() instanceof MapTableModel))
        {
            for (int c = 0; c < getColumnCount(); c++)
            {
                setDefaultRenderer(getColumnClass(c), TCR);
            }

            adjustRowHeight();
        }
    }

    /**
     * Set the height of the row to show all of the rendered object.
     * The height of a row is set to the preferred height
     * of the tallest cell in that row.
     */
    private void adjustRowHeight()
    {
        // Get the current default height for all rows
        int height = getRowHeight();
        int rowcount = getRowCount();
        int colcount = getColumnCount();
        int margin = getRowMargin();

        for (int row = 0; row < rowcount; row++)
        {
            // Determine highest cell in the row
            int highest = height;
            for (int col = 0; col < colcount; col++)
            {
                Component comp = prepareRenderer(TCR, row, col);
                highest = Math.max(highest, comp.getPreferredSize().height + 2 * margin);
            }

            // Now set the row height using the preferred height
            if (getRowHeight(row) != highest)
            {
                setRowHeight(row, highest);
            }
        }
    }

    /**
     * <code>TCR</code> is a shared renderer that renders potentially
     * mulitline text.
     */
    private static final TableCellRenderer TCR = new MapCellRenderer();

    /**
     * <code>initialized</code> indicates that a TableCellRenderer
     * can be set in setModel.
     */
    private boolean initialized;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3906091143962965817L;
}
