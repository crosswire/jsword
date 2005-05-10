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

import java.util.List;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * The RowTableModel defines the "model" behaviour for a RowTable.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RowTableModel extends AbstractTableModel
{
    /**
     * Builds a RowTable model for the provided (non-null) row list,
     * using the provided row column definition.
     * @param newList List
     */
    public RowTableModel(List newList, RowColumns aRowColumnModel)
    {
        list = new ArrayList();
        list.addAll(newList);

        rowColumnModel = aRowColumnModel;
        keys = rowColumnModel.getSortKeys();
        sortColumn = keys[0];
        allocate();
    }

    /**
     * Method getRowCount returns the number of rows in the list.
     * @return int
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount()
    {
        return (list == null) ? 0 : list.size();
    }

    /**
     * Method getColumnCount returns the number of columns in the table
     * @return int
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount()
    {
        return rowColumnModel.getCount();
    }

    /**
     * Method getValueAt returns the contents of a cell.
     * @param row int
     * @param column int
     * @return Object
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column)
    {
        return getCellValue(indexes[row], column);
    }

    /**
     * Method getCellValue Translates from a row index to a row object
     * and asks it for the appropriate cell value
     * @param rowIndex int
     * @param columnIndex int
     * @return Object
     */
    private Object getCellValue(int rowIndex, int columnIndex)
    {
        final Object obj = list.get(rowIndex);
        return rowColumnModel.getValueAt(obj, columnIndex);
    }

    /**
     * Method getColumnClass returns the class of the column
     * @param columnIndex int
     * @return Class
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int columnIndex)
    {
        return rowColumnModel.getClass(columnIndex);
    }

    /**
     * Method getHeaderToolTip returns the tooltip for the header of the column
     * @param columnIndex int
     * @return String
     */
    public String getHeaderToolTip(int columnIndex)
    {
        return rowColumnModel.getHeaderToolTip(columnIndex);
    }

    /**
     * Method getColumnName returns the header name for the column
     * @param columnIndex int
     * @return String
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex)
    {
        return rowColumnModel.getName(columnIndex);
    }

    /**
     * Method addRow adds a row to the table.
     * @param obj the row to add
     */
    public void addRow(Object obj)
    {
        list.add(obj);
        allocate();
        final int visibleRow = getRow(obj);
        fireTableRowsInserted(visibleRow, visibleRow);
    }

    /**
     * Method getRow retrieves a row from the table
     * @param rowIndex int
     * @return the row
     */
    public Object getRow(int rowIndex)
    {
        return list.get(indexes[rowIndex]);
    }

    /**
     * Method getRow finds the visible row index for a given row
     * @param obj the row
     * @return int
     */
    public int getRow(Object obj)
    {
        for (int i = 0; i < indexes.length; i++)
        {
            if (getRow(i).equals(obj))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Method removeRow removes a row from the model
     * and causes the display to update itself appropriately
     * @param obj the row to remove
     */
    public void removeRow(Object obj)
    {
        final int dataIndex = list.indexOf(obj);
        final int visibleIndex = getRow(obj);
        list.remove(dataIndex);
        fireTableRowsDeleted(visibleIndex, visibleIndex);
        allocate();
    }

    /**
     * Method updateRow causes the display to update itself appropriately.
     * Methods on rows are actually used to update the row
     * @param obj the row
     */
    public void updateRow(Object obj)
    {
        final int visibleIndex = getRow(obj);
        fireTableRowsUpdated(visibleIndex, visibleIndex);
    }

    public void reset()
    {
        allocate();
        fireTableDataChanged();
    }

    public void clear()
    {
        list.clear();
        allocate();
        fireTableDataChanged();
    }

    // Bubble Sort!!! Replace if performance is an issue.
    /**
     * Method sort
     * @param modelIndex int
     */
    public void sort(int modelIndex)
    {
        if (modelIndex != -1)
        {
            sortColumn = modelIndex;
        }
        final int rowCount = getRowCount();
        boolean changed = false;
        for (int i = 0; i < rowCount; i++)
        {
            for (int j = i + 1; j < rowCount; j++)
            {
                if (compareKeys(indexes[i], indexes[j], sortColumn) < 0)
                {
                    swap(i, j);
                    changed = true;
                }
            }
        }
        if (changed)
        {
            fireTableRowsUpdated(0, getRowCount());
        }
    }

    /**
     * Method swap
     * @param i int
     * @param j int
     */
    private void swap(int i, int j)
    {
        final int tmp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = tmp;
    }

    /**
     * Method compareKeys
     * @param i int
     * @param j int
     * @param column int
     * @return int
     */
    private int compareKeys(int i, int j, int column)
    {
        int cmp = compare(i, j, column);
        if (keys != null)
        {
            for (int k = 0; cmp == 0 && k < keys.length; k++)
            {
                if (k != column)
                {
                    cmp = compare(i, j, keys[k]);
                }
            }
        }
        return cmp;
    }

    /**
     * Method compare
     * @param i int
     * @param j int
     * @param column int
     * @return int
     */
    public int compare(int i, int j, int column)
    {
        final Object io = getCellValue(i, column);
        final Object jo = getCellValue(j, column);
        int cmp = 0;
        if (io.getClass().equals(jo.getClass()) && io instanceof Comparable)
        {
            cmp = ((Comparable) jo).compareTo(io);
        }
        else if (io instanceof Boolean)
        {
            cmp = io.toString().compareTo(jo.toString());
        }
        else
        {
            cmp = jo.toString().compareTo(io.toString());
        }

        return (cmp < 0) ? -1 : ((cmp > 0) ? 1 : 0);
    }

    /**
     * Method allocate
     */
    private void allocate()
    {
        final int rowCount = getRowCount();
        if (indexes == null || indexes.length != rowCount)
        {
            final int[] newData = new int[rowCount];
            for (int i = 0; i < rowCount; i++)
            {
                newData[i] = i;
            }
            indexes = newData;
            // Do the default or last sort
            sort(-1);
        }
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3761126033281463602L;

    /**
     * Field list contains the objects that can be worked upon
     */
    private List list;

    /**
     * Field columnModel provides the definition of the structure
     * of the table
     */
    private RowColumns rowColumnModel;

    /**
     * Field indexes provides a look-aside for the sorted view of the
     * table to the row list.
     */
    private int[] indexes;

    /**
     * Field keys provides the primary or composite key of the table.
     * It is a local optimization of columnModel.getSortKeys().
     */
    private int[] keys;

    /**
     * Field sortColumn indicates the column that was last sorted upon.
     * It is initialized the first value in keys, if present otherwise -1
     */
    private int sortColumn;
}
