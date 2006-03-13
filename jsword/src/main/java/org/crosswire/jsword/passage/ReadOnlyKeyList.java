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
 * ID: $Id$
 */
package org.crosswire.jsword.passage;

import java.util.Iterator;

/**
 * A read-only wrapper around any writable implementation of Key.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ReadOnlyKeyList implements Key
{
    /**
     * Simple ctor
     */
    public ReadOnlyKeyList(Key keys, boolean ignore)
    {
        this.keys = keys;
        this.ignore = ignore;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#size()
     */
    public int getChildCount()
    {
        return keys.getChildCount();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isLeaf()
     */
    public boolean canHaveChildren()
    {
        return keys.canHaveChildren();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    public boolean isEmpty()
    {
        return keys.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key)
    {
        return keys.contains(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator<Key> iterator()
    {
        return keys.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#add(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.KEYLIST_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#remove(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.KEYLIST_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retain(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.KEYLIST_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear()
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.KEYLIST_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName()
     */
    public String getName()
    {
        return keys.getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOSISRef()
     */
    public String getOsisRef()
    {
        return keys.getOsisRef();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOSISId()
     */
    public String getOsisID()
    {
        return keys.getOsisID();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Key o)
    {
        return keys.compareTo(o);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#get(int)
     */
    public Key get(int index)
    {
        return keys.get(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#indexOf(org.crosswire.jsword.passage.Key)
     */
    public int indexOf(Key that)
    {
        return keys.indexOf(that);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent()
    {
        return keys.getParent();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int)
     */
    public void blur(int by, RestrictionType restrict)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.KEYLIST_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        ReadOnlyKeyList clone = null;
        try
        {
            clone = (ReadOnlyKeyList) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            assert false : e;
        }
        clone.keys = (Key) keys.clone();
        return clone;
    }

    /**
     * Do we ignore write requests or throw?
     */
    private boolean ignore;

    /**
     * The Key to which we proxy
     */
    private Key keys;
}
