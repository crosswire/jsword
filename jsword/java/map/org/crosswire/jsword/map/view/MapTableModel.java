
package org.crosswire.jsword.map.view;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.crosswire.jsword.map.model.Map;
import org.crosswire.jsword.map.model.MapEvent;
import org.crosswire.jsword.map.model.MapListener;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;

/**
 * A MapTableModel takes an underlying map and represents it as a
 * TableModel.
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
public class MapTableModel extends AbstractTableModel
{
    /**
     * Basic constructor
     */
    public MapTableModel(Map map)
    {
        setMap(map);
    }

    /**
     * Basic constructor
     */
    public MapTableModel()
    {
        setMap(null);
    }

    /**
     * Setup a new map to view
     * @param map The new map to model
     */
    public void setMap(Map map)
    {
        if (map != null)
            map.removeMapListener(cml);

        this.map = map;

        if (map != null)
        {
            map.addMapListener(cml);
            cols = map.getDimensions() + 3;
        }
        else
        {
            cols = 1;
        }

        fireTableDataChanged();
    }

    /**
     * Get the map being viewed
     * @return The current map
     */
    public Map getMap()
    {
        return map;
    }

    /**
     * Returns the number of records in the map
     * @return the number or rows in the model
     * @see #getColumnCount
     */
    public int getRowCount()
    {
        return cib;
    }

    /**
     * Returns the number of columns in the map
     * @return the number or columns in the model
     * @see #getRowCount
     */
    public int getColumnCount()
    {
        return cols;
    }

    /**
     * Returns the name of the column at <i>columnIndex</i>. This is used
     * to initialize the table's column header name.  Note, this name does
     * not need to be unique. Two columns on a table can have the same name.
     * @param col The index of column
     * @return the name of the column
     */
    public String getColumnName(int col)
    {
        switch (col)
        {
        case 0:
            return "Name";
        case 1:
            return "Book";
        case 2:
            return "Chapter";
        case 3:
            return "X Position";
        case 4:
            return "Y Position";
        case 5:
            return "Z Position";
        default:
            return "Dimension "+(col-3);
        }
    }

    /**
     * Returns the lowest common denominator Class in the column.  This is used
     * by the table to set up a default renderer and editor for the column.
     * @return the common ancestor class of the object values in the model.
     */
    public Class getColumnClass(int col)
    {
        switch (col)
        {
        case 0:
            return VerseRange.class;
        case 1:
        case 2:
            return Integer.class;
        default:
            return Float.class;
        }
    }

    /**
     * Returns true if the cell at <I>row</I> and <I>col</I> is editable.
     * Otherwise, setValueAt() on the cell will not change the value of
     * that cell.
     * @param row The row whose value is to be looked up
     * @param col The column whose value is to be looked up
     * @return rue if the cell is editable.
     * @see #setValueAt
     */
    public boolean isCellEditable(int row, int col)
    {
        return col > 2;
    }

    /**
     * Returns an attribute value for the cell at <I>col</I> and
     * <I>row</I>.
     * @param row The row whose value is to be looked up
     * @param col The column whose value is to be looked up
     * @return The value Object at the specified cell
     */
    public Object getValueAt(int row, int col)
    {
        try
        {
            // convert the row number (=verse ordinal - 1)
            // to a book and chapter number.
            int count = row + 1;
            int b = 1;
            int c = 1;
            while (b <= BibleInfo.booksInBible())
            {
                if (count <= BibleInfo.chaptersInBook(b))
                {
                    c = count;
                    break;
                }

                count -= BibleInfo.chaptersInBook(b);
                b++;
            }

            switch (col)
            {
            case 0:
                VerseRange vr = new VerseRange(new Verse(b, c, 1), new Verse(b, c, BibleInfo.versesInChapter(b, c)));
                return vr;
            case 1:
                return new Integer(b);
            case 2:
                return new Integer(c);
            default:
                float f = map.getPositionDimension(b, c, col-3);
                return new Float(f);
            }
        }
        catch (NoSuchVerseException ex)
        {
            Reporter.informUser(this, ex);

            switch (col)
            {
            case 0:
                return new VerseRange();
            case 1:
                return new Integer(0);
            case 2:
                return new Integer(0);
            default:
                return new Float(0.0f);
            }
        }
    }

    /**
     * Sets an attribute value for the record in the cell at
     * <I>col</I> and <I>row</I>. <I>val</I> is the new value.
     * @param val The new value
     * @param row The row whose value is to be changed
     * @param col The column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    public void setValueAt(Object val, int row, int col)
    {
        try
        {
            float f = Float.valueOf(val.toString()).floatValue();
            Verse v = new Verse(row+1);
    
            if (col > 2)
            {
                map.setPositionDimension(v.getBook(), v.getChapter(), col-1, f);
            }
        }
        catch (NoSuchVerseException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /** The map that we are viewing */
    private Map map;

    /** The number of rows */
    private static int cib = BibleInfo.chaptersInBible();

    /** The number of columns */
    private static int cols;

    /** The map listener */
    private CustomMapListener cml = new CustomMapListener();

    /**
     * Sync the map and the table
     */
    class CustomMapListener implements MapListener
    {
        /**
         * This method is called to indicate that a node on the map has
         * moved.
         * @param ev Describes the change
         */
        public void mapChanged(final MapEvent ev)
        {
            if (!SwingUtilities.isEventDispatchThread())
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        mapChanged(ev);
                    }
                });
                return;
            }

            try
            {
                int book = ev.getChangedBook();
                int chapter = ev.getChangedChapter();
                int ord = BibleInfo.verseOrdinal(book, chapter, 1);
                int row = ord - 1;
                
                fireTableCellUpdated(row, 2);
                fireTableCellUpdated(row, 3);
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
        }

        /**
         * This method is called to indicate that the whole map has changed
         * @param ev Describes the change
         */
        public void mapRewritten(final MapEvent ev)
        {
            if (!SwingUtilities.isEventDispatchThread())
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        mapRewritten(ev);
                    }
                });
                return;
            }

            fireTableDataChanged();
        }
    }
}
