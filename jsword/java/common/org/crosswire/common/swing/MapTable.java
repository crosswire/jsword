package org.crosswire.common.swing;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A simple table that renders text, potentially multiline.
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
 * @author DM Smith [ dmsmith555 at hotmail dot com]
 * @version $Id$
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
        if (initialized && !(dm instanceof MapTableModel))
        {
            throw new IllegalArgumentException(Msg.ERROR_TABLE_MODEL.toString());
        }

        super.setModel(dm);
        setDefaultRenderer();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.JTable#setDefaultRenderer(java.lang.Class, javax.swing.table.TableCellRenderer)
     */
    public void setDefaultRenderer(Class columnclass, TableCellRenderer renderer)
    {
        if (!(renderer instanceof MapCellRenderer))
        {
            throw new IllegalArgumentException(Msg.ERROR_CELL_RENDER.toString());
        }

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
                setDefaultRenderer(getColumnClass(c), tcr);
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
                Component comp = prepareRenderer(tcr, row, col);
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
     * <code>tcr</code> is a shared renderer that renders potentially
     * mulitline text.
     */
    private static final TableCellRenderer tcr = new MapCellRenderer();
    
    /**
     * <code>initialized</code> indicates that a TableCellRenderer
     * can be set in setModel.
     */
    private boolean initialized;
}
