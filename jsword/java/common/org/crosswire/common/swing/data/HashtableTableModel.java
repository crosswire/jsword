
package org.crosswire.common.swing.data;

import java.util.*;
import javax.swing.table.*;

/**
* TableModel using a Hashtable internally. Note that an AbstractTableModel
* (this is-a AbstractTableModel) reports changes to the data to the table
* itself. However since a Hashtable does not have a addChangeListener
* interface we can't do the same - SO if you change the Hashtable whilst
* we are displaying it then don't expect the changes to be automatically
* reflected in the JTable.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
* @version D8.I5.T0
*/
public class HashtableTableModel extends AbstractTableModel
{
    /**
    * Create an internal store from a 2D array
    * @param hash The table to model
    */
    public HashtableTableModel(Hashtable hash)
    {
        this.hash = hash;
    }

    /**
    * Accessor for our source data
    * @return The hashtable we are getting our data from
    */
    public Hashtable getHashtable()
    {
        return hash;
    }

    /**
    * Change the hashtable that we report on
    * @param hash The hashtable we are getting our data from
    */
    public void setHashtable(Hashtable hash)
    {
        this.hash = hash;
    }

    /**
    * How many Cols are there in this store
    * @return The number of columns - always 2 in this case
    */
    public int getColumnCount()
    {
        return 2;
    }

    /**
    * How many Rows are there in this store
    * @return the number of row in the TableModel = elements in the Hashtable
    */
    public int getRowCount()
    {
        return hash.size();
    }

    /**
    * Return the Object at row, col
    * @param row The element in the hash
    * @param col 1=keys, 2=values
    * @return The key/value of the given element
    */
    public Object getValueAt(int row, int col)
    {
        Enumeration en = (col == 0) ? hash.keys() : hash.elements();

        try
        {
            for (int i=0; i<row; i++)
                en.nextElement();

            return en.nextElement();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
    * An easy way to add stuff to the table
    */
    public void put(Object key, Object value)
    {
        hash.put(key, value);
        fireTableStructureChanged();
    }

    /**
    * An easy way to add stuff to the table
    */
    public void remove(Object key)
    {
        hash.remove(key);
        fireTableStructureChanged();
    }

    /**
    * Set the Object at row, coll
    * @param obj The key/value to set
    * @param row The element in the hash
    * @param col 1=keys, 2=values
    */
    public void setValueAt(Object obj, int row, int col)
    {
        Enumeration en = hash.keys();

        for (int i=0; i<row; i++)
            en.hasMoreElements();

        Object old_key = en.nextElement();
        Object old_val = hash.get(old_key);

        if (col == 0)
        {
            // Changing a key
            hash.remove(old_key);
            hash.put(obj, old_val);
        }
        else
        {
            // Changing a value
            hash.put(old_key, obj);
        }
    }

    /**
    * Can the specified cell be changed?
    * @param row The element in the hash
    * @param col 1=keys, 2=values
    */
    public boolean isCellEditable(int row, int col)
    {
        return true;
    }

    /**
    * Get the default class
    * @param col 1=keys, 2=values
    */
    public Class getColumnClass(int col)
    {
        return String.class;
    }

    /**
    * The name of the of the <code>col</code>th column
    * @param col The column index
    * @return The column name
    */
    public String getColumnName(int col)
    {
        return col_names[col];
    }

    /**
    * The name of the of the <code>col</code>th column
    * @param col The column index
    * @param name The column name
    */
    public void setColumnName(int col, String name)
    {
        col_names[col] = name;
    }

    /** The Hashtable that we are providing an interface to */
    private Hashtable hash;

    /** The default column names */
    private String[] col_names = new String[] { "Keys", "Values" };
}
