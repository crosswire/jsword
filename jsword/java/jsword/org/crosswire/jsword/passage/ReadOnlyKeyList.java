package org.crosswire.jsword.passage;

import java.util.Iterator;

/**
 * A read-only wrapper around any writable implementation of KeyList.
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
public class ReadOnlyKeyList implements KeyList
{
    /**
     * Simple ctor
     */
    public ReadOnlyKeyList(KeyList keys, boolean ignore)
    {
        this.keys = keys;
        this.ignore = ignore;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#size()
     */
    public int size()
    {
        return keys.size();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#isEmpty()
     */
    public boolean isEmpty()
    {
        return keys.isEmpty();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key)
    {
        return keys.contains(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#iterator()
     */
    public Iterator iterator()
    {
        return keys.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#add(org.crosswire.jsword.passage.Key)
     */
    public void add(Key key)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.KEYLIST_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#remove(org.crosswire.jsword.passage.Key)
     */
    public void remove(Key key)
    {
        if (ignore)
        {
            return;
        }

        throw new IllegalStateException(Msg.KEYLIST_READONLY.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#clear()
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
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o)
    {
        return keys.compareTo(o);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#get(int)
     */
    public Key get(int index)
    {
        return keys.get(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#indexOf(org.crosswire.jsword.passage.Key)
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

    /**
     * Do we ignore write requests or throw?
     */
    private boolean ignore;

    /**
     * The KeyList to which we proxy
     */
    private KeyList keys;
}
