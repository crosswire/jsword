/**
 * Distribution License:
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/llgpl.html
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

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 * A simple implementation of ListModel that is backed by a List.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ListListModel extends AbstractListModel implements ListModel
{
    /**
     * Constructor for ListListModel.
     */
    public ListListModel(List list)
    {
        this.list = list;
    }

    /**
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        return list.size();
    }

    /**
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        return list.get(index);
    }

    private List list;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3977863977273209144L;
}
