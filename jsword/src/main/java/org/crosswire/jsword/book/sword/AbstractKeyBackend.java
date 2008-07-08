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
 * Copyright: 2008
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: RawLDBackend.java 1794 2008-04-11 10:48:41Z dmsmith $
 */
package org.crosswire.jsword.book.sword;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.RestrictionType;

/**
 * A Backend that can be used as a global key list.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class AbstractKeyBackend extends AbstractBackend implements Key
{
    /**
     * Simple ctor
     * @param datasize We need to know how many bytes in the size portion of the index
     */
    public AbstractKeyBackend(SwordBookMetaData sbmd)
    {
        super(sbmd);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#canHaveChildren()
     */
    public boolean canHaveChildren()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getChildCount()
     */
    public int getChildCount()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    /* @Override */
    public boolean isEmpty()
    {
        return getCardinality() == 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    /* @Override */
    public boolean contains(Key key)
    {
        return indexOf(key) > 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#iterator()
     */
    public Iterator iterator()
    {
        return new Iterator() {

            /* (non-Javadoc)
             * @see java.util.Iterator#hasNext()
             */
            public boolean hasNext()
            {
                return here < count;
            }

            /* (non-Javadoc)
             * @see java.util.Iterator#next()
             */
            public Object next() throws NoSuchElementException
            {
                if (here >= count)
                {
                    throw new NoSuchElementException();
                }
                return get(here++);
            }

            /* (non-Javadoc)
             * @see java.util.Iterator#remove()
             */
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            private int here;
            private int count = getCardinality();
        };
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#add(org.crosswire.jsword.passage.Key)
     */
    public void addAll(Key key)
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#remove(org.crosswire.jsword.passage.Key)
     */
    public void removeAll(Key key)
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#clear()
     */
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getParent()
     */
    public Key getParent()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        try
        {
            super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            assert false : e;
        }
        return this;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName()
     */
    public String getName()
    {
        return getBookMetaData().getInitials();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage.Key)
     */
    public String getName(Key base)
    {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisID()
     */
    public String getOsisID()
    {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOsisRef()
     */
    public String getOsisRef()
    {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getRootName()
     */
    public String getRootName()
    {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retainAll(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key)
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    /* @Override */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
        {
            return false;
        }

        // Check that that is the same as this
        // Don't use instanceOf since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        return compareTo(obj) == 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        Key that = (Key) obj;

        if (this == that)
        {
            return 0;
        }

        if (that == null)
        {
            // he is empty, we are not so he is greater
            return -1;
        }


        int ret = this.getName().compareTo(that.getName());

        if (ret != 0)
        {
            return ret;
        }

        // Compare the contents.
        Iterator thisIter = this.iterator();
        Iterator thatIter = that.iterator();

        Key thisfirst = null;
        Key thatfirst = null;

        if (thisIter.hasNext())
        {
            thisfirst = (Key) thisIter.next();
        }

        if (thatIter.hasNext())
        {
            thatfirst = (Key) thatIter.next();
        }

        if (thisfirst == null)
        {
            if (thatfirst == null)
            {
                // we are both empty, and rank the same
                return 0;
            }
            // i am empty, he is not so we are greater
            return 1;
        }

        if (thatfirst == null)
        {
            // he is empty, we are not so he is greater
            return -1;
        }

        return thisfirst.getName().compareTo(thatfirst.getName());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#blur(int)
     */
    public void blur(int by, RestrictionType restrict)
    {
    }
}
