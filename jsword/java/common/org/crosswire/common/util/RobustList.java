
package org.crosswire.common.util;

import java.io.Serializable;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
* This is a version of LinkedList that is not fail-fast.
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
*/
public class RobustList implements Serializable
{
    Entry head = null;
    Entry foot = null;
    int size = 0;
    Object sync = new Object();

    void debug(String title)
    {
        log.debug(title);
        log.debug(" head ="+head);
        log.debug(" foot ="+foot);
        int i = 0;
        Entry e = head;
        while (e != null)
        {
            log.debug(" index="+i);
            e.debug();
            e = e.next;
            i++;
        }
    }

    /**
    * Does this list contains the specified element?
    * @param o element whose presence in this list is to be tested.
    * @return true if this list contains the specified element.
    */
    public boolean contains(Object o)
    {
        return indexOf(o) != -1;
    }

    /**
    * Returns the number of elements in this list.
    * @return the number of elements in this list.
    */
    public int size()
    {
        return size;
    }

    /**
    * Appends the specified element to the end of this list.
    * @param o element to be appended to this list.
    */
    public void addElement(Object o)
    {
        // debug("pre-add "+o);
        new Entry(o);
        // debug("post-add "+o);
    }

    /**
    * Removes the element at the specified position in this list.  Shifts any
    * subsequent elements to the left (subtracts one from their indices).
    * Returns the element that was removed from the list.
    * @param index the index of the element to removed.
    * @return the element previously at the specified position.
    */
    public void removeElement(int index)
    {
        debug("pre-remove "+index);
        Entry e = findEntry(index);
        e.remove();
        debug("post-remove "+index);
    }

    /**
    * Removes the first occurrence of the specified element in this list.  If
    * the list does not contain the element, it is unchanged.
    * @param o element to be removed from this list, if present.
    * @return true if the list contained the specified element.
    */
    public boolean removeElement(Object o)
    {
        // debug("pre-remove "+o);
        if (o == null)
        {
            Entry e = head;
            while (e != null)
            {
                if (e.object == null)
                {
                    e.remove();
                    // debug("post-remove "+o);
                    return true;
                }

                e = e.next;
            }
        }
        else
        {
            Entry e = head;
            while (e != null)
            {
                if (o.equals(e.object))
                {
                    e.remove();
                    // debug("post-remove "+o);
                    return true;
                }

                e = e.next;
            }
        }

        // debug("post-remove fail "+o);
        return false;
    }

    /**
    * Removes all of the elements from this list.
    */
    public void clear()
    {
        debug("pre-clear");
        head = foot = null;
        size = 0;
        debug("post-clear");
    }

    /**
    * Returns the element at the specified position in this list.
    * @param index index of element to return.
    * @return the element at the specified position in this list.
    */
    public Object elementAt(int index)
    {
        return findEntry(index).object;
    }

    /**
    * Return the indexed entry.
    */
    private Entry findEntry(int index)
    {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: "+index+ ", Size: "+size);

        Entry e;
        if (index < size/2)
        {
            e = head;
            for (int i=0; i!=index; i++)
                e = e.next;
        }
        else
        {
            e = foot;
            for (int i=size-1; i!=index; i--)
                e = e.prev;
        }

        return e;
    }

    /**
    * Returns the index in this list of the first occurrence of the
    * specified element, or -1 if the List does not contain this element.
    * @param o element to search for.
    * @return the index of the first occurrence or -1
    */
    public int indexOf(Object o)
    {
        int index = 0;
        if (o == null)
        {
            Entry e = head;
            while (e != null)
            {
                if (e.object == null)
                    return index;

                e = e.next;
                index++;
            }
        }
        else
        {
            Entry e = head;
            while (e != null)
            {
                if (o.equals(e.object))
                    return index;

                e = e.next;
                index++;
            }
        }

        return -1;
    }

    /**
    * Returns a list-iterator of the elements in this list
    * @return a ListIterator of the elements in this list
    */
    public Enumeration elements()
    {
        // debug("pre-enumerate");
        return new RobustListEnumeration();
    }

    private class RobustListEnumeration implements Enumeration
    {
        private Entry next;

        RobustListEnumeration()
        {
            next = head;
        }

        public boolean hasMoreElements()
        {
            return next != null;
        }

        public Object nextElement()
        {
            // next.debug();
            Object retcode = next.object;
            next = next.next;
            return retcode;
        }
    }

    private class Entry
    {
        Object object;
        Entry next;
        Entry prev;

        Entry(Object object)
        {
            this.object = object;
            this.next = null;
            this.prev = foot;

            if (head == null)
                head = this;

            if (foot != null)
                foot.next = this;
            foot = this;

            size++;
        }

        void remove()
        {
            if (this == foot)
            {
                if (prev != null)
                    prev.next = null;

                foot = prev;
            }

            if (this == head)
            {
                if (next != null)
                    next.prev = null;

                head = next;
            }

            if (prev != null)
                prev.next = next;

            if (next != null)
                next.prev = prev;

            size--;
        }

        void debug()
        {
            log.debug("  prev="+prev);
            log.debug("  this="+this);
            log.debug("  next="+next);
            log.debug("   obje="+object);
        }
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger("util.util");
}
