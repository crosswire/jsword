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

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.crosswire.common.util.StackTrace;

/**
 * To itterate over the full function names.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @see javax.swing.ListModel
 * @see javax.swing.JList
 */
public class StackTraceListModel implements ListModel
{
    /**
     * @param st
     */
    public StackTraceListModel(StackTrace st)
    {
        this.st = st;
    }

    /**
     * Returns the length of the list
     */
    public int getSize()
    {
        return st.countStackElements();
    }

    /**
     * Returns the value at the specified index
     */
    public Object getElementAt(int index)
    {
        return st.getFullFunctionName(index);
    }

    /**
     * Ignore this because the trace will not change
     */
    public void addListDataListener(ListDataListener li)
    {
    }

    /**
     * Ignore this because the trace will not change
     */
    public void removeListDataListener(ListDataListener li)
    {
    }

    private final StackTrace st;
}
