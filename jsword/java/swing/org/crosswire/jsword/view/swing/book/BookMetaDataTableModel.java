package org.crosswire.jsword.view.swing.book;

import javax.swing.table.AbstractTableModel;

import org.crosswire.jsword.book.BookMetaData;

/**
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
public class BookMetaDataTableModel extends AbstractTableModel
{
    /**
     * Simple ctor
     */
    public BookMetaDataTableModel()
    {
        bmd = null;
    }

    /**
     * Simple ctor with default BookMetaData
     */
    public BookMetaDataTableModel(BookMetaData bmd)
    {
        this.bmd = bmd;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount()
    {
        return COLS.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount()
    {
        return ROWS.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int col)
    {
        return Object.class;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col)
    {
        switch (col)
        {
        case 0:
            return ROWS[row];

        case 1:
            if (bmd == null)
            {
                return "";
            }

            switch (row)
            {
            case 0:
                return bmd.getName();
            case 1:
                return bmd.getDriverName();
            case 2:
                return bmd.getEdition();
            case 3:
                return bmd.getInitials();
            case 4:
                return bmd.getFirstPublished();
            default:
                return null;
            }

        default:
            return null;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int row, int col)
    {
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int col)
    {
        return COLS[col];
    }

    /**
     * @return Returns the bmd.
     */
    public BookMetaData getBookMetaData()
    {
        return bmd;
    }

    /**
     * @param bmd The bmd to set.
     */
    public void setBookMetaData(BookMetaData bmd)
    {
        if (bmd != this.bmd)
        {
            this.bmd = bmd;
            fireTableStructureChanged();
        }
    }

    /**
     * The meta data that we are displaying
     */
    private BookMetaData bmd;

    /**
     * The column names
     */
    private static final String[] COLS = new String[]
    {
        "Property",
        "Value",
    };

    /**
     * The column names
     */
    private static final String[] ROWS = new String[]
    {
        "Name",
        "Driver",
        "Edition",
        "Initials",
        "First Published",
    };
}
