package org.crosswire.jsword.passage;
/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: LZSSBackend.java 1143 2006-10-04 22:07:23 -0400 (Wed, 04 Oct 2006) dmsmith $
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;

/**
 * A Key that knows where the data is in the real file.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class TreeKey extends AbstractKeyList
{
    /**
     * Setup with the key name and positions of data in the file
     */
    TreeKey(String name, Key parent)
    {
        super(name);
        this.parent = parent;
        this.children = new ArrayList();
    }

    /**
     * Setup with the key name. Use solely for searching.
     */
    TreeKey(String text)
    {
        this(text, null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount()
    {
        return children.size();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getCardinality()
     */
    public int getCardinality()
    {
        int cardinality = 1; // count this node
        Iterator iter = children.iterator();
        while (iter.hasNext())
        {
            Key child = (Key) iter.next();
            cardinality += child.getCardinality();
        }

        return cardinality;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    /* @Override */
    public boolean isEmpty()
    {
        return children.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    /* @Override */
    public boolean contains(Key key)
    {
        if (children.contains(key))
        {
            return true;
        }

        Iterator iter = children.iterator();
        while (iter.hasNext())
        {
            Key child = (Key) iter.next();
            if (child.contains(key))
            {
                return true;
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator iterator()
    {
        return children.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#add(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key)
    {
        children.add(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#remove(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key)
    {
        children.remove(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear()
    {
        children.clear();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index)
    {
        return (Key) children.get(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that)
    {
        return children.indexOf(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent()
    {
        return parent;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int)
     */
    public void blur(int by, RestrictionType restrict)
    {
        log.warn("attempt to blur a non-blur-able list"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        return super.clone();
    }

    private Key parent;

    private List children;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -6560408145705717977L;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(TreeKey.class);
}
