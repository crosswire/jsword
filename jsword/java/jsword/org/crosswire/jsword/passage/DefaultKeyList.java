package org.crosswire.jsword.passage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;

/**
 * A default implementation of KeyList.
 * 
 * <p>This implementation uses <tt>java.util.TreeSet</tt> to store keys.
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
public class DefaultKeyList extends AbstractKeyList implements KeyList
{
    /**
     * Simple ctor
     */
    public DefaultKeyList()
    {
    }

    /**
     * Simple ctor
     */
    public DefaultKeyList(String name)
    {
        setName(name);
    }

    /**
     * Simple ctor
     */
    public DefaultKeyList(Key parent)
    {
        this.parent = parent;
    }

    /**
     * Simple ctor
     */
    public DefaultKeyList(Key parent, String name)
    {
        this.parent = parent;
        setName(name);
    }

    /**
     * A factory constructor to create a KeyList from a Key, buy casting if we
     * really have a KeyList or by new()ing and add()ing if not.
     */
    public static KeyList getKeyList(Key key)
    {
        if (key instanceof KeyList)
        {
            return (KeyList) key;
        }
        else
        {
            KeyList reply = new DefaultKeyList();
            reply.add(key);
            return reply;
        }
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
        keys.add(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#remove(org.crosswire.jsword.passage.Key)
     */
    public void remove(Key key)
    {
        keys.remove(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#clear()
     */
    public void clear()
    {
        keys.clear();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#get(int)
     */
    public Key get(int index)
    {
        return (Key) keys.get(index);
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
        return parent;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyList#blur(int)
     */
    public void blur(int by)
    {
        log.warn("attempt to blur a non-blur-able list"); //$NON-NLS-1$
    }

    /**
     * The parent of this key
     */
    private Key parent;

    /**
     * The store of Keys
     */
    private List keys = new ArrayList();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DefaultKeyList.class);
}