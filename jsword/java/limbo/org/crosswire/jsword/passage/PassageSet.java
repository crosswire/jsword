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
package org.crosswire.jsword.passage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Since a Passage no longer implements Collection, a proxy
 * interface might be useful.
 * 
 * This is it. However it is not complete, and won't be until I know it is
 * needed.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PassageSet implements SortedSet
{
    /**
     * Construct a Collection proxy with a Passage to proxy to.
     * @param ref The real store od data
     */
    public PassageSet(Passage ref)
    {
        this.ref = ref;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#size()
     */
    public int size()
    {
        return ref.countVerses();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty()
    {
        return ref.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object o)
    {
        return ref.contains(AbstractPassage.toVerseRange(o));
    }

    /* (non-Javadoc)
     * @see java.util.Collection#iterator()
     */
    public Iterator iterator()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray()
    {
        if (ref instanceof RangedPassage)
        {
            // return ((RangedPassage) ref).store.toArray();
            return null;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    public Object[] toArray(Object[] arr)
    {
        if (ref instanceof RangedPassage)
        {
            return null;

            /* Maybe one day we'll be bothered to make this work
            if (arr instanceof Verse[])
            {
                try
                {
                    // The special case for Verse to de-scope VerseRanges
                    // Create a destination array of the correct size:
                    Verse[] retcode = new Verse[countVerses()];
                    int count = 0;

                    Enumeration en = rangeElements();
                    while (en.hasMoreElements())
                    {
                        // Fill the array with all the Verses
                        VerseRange range = (VerseRange) en.nextElement();

                        for (int i=0; i<range.getVerseCount(); i++)
                        {
                            retcode[count+1] = new Verse(range.getStart().getOrdinal()+i);
                        }
                        count += range.getVerseCount();
                    }

                    return retcode;
                }
                catch (NoSuchVerseException ex)
                {
                    throw new Error("Logic Error");
                }
            }
            else
            {
                return store.toArray(arr);
            }
            */
        }

        return null;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        boolean retcode = contains(o);
        ref.add(AbstractPassage.toVerseRange(o));
        return !retcode;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        boolean retcode = contains(o);
        ref.remove(AbstractPassage.toVerseRange(o));
        return !retcode;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public synchronized boolean containsAll(Collection col)
    {
        for (Iterator it = col.iterator(); it.hasNext(); )
        {
            Object element = it.next();
            VerseRange range = AbstractPassage.toVerseRange(element);
            if (!ref.contains(range))
            {
                return false;
            }
        }

        return true;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection col)
    {
        boolean modified = false;
        for (Iterator it = col.iterator(); it.hasNext(); )
        {
            Object element = it.next();
            VerseRange range = AbstractPassage.toVerseRange(element);
            if (!ref.contains(range))
            {
                modified = true;
            }

            ref.add(range);
        }

        return modified;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection col)
    {
        boolean modified = false;
        for (Iterator it = col.iterator(); it.hasNext(); )
        {
            Object element = it.next();
            VerseRange range = AbstractPassage.toVerseRange(element);
            if (ref.contains(range))
            {
                modified = true;
            }

            ref.remove(range);
        }

        return modified;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection col)
    {
        Passage temp = (Passage) keyf.createEmptyKeyList();

        for (Iterator it = col.iterator(); it.hasNext(); )
        {
            Object element = it.next();
            VerseRange range = AbstractPassage.toVerseRange(element);
            if (ref.contains(range))
            {
                temp.add(range);
            }
        }

        return !temp.equals(ref);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#clear()
     */
    public void clear()
    {
        ref.clear();
    }

    /* (non-Javadoc)
     * @see java.util.SortedSet#first()
     */
    public Object first()
    {
        return ref.getRangeAt(0, restrict);
    }

    /* (non-Javadoc)
     * @see java.util.SortedSet#last()
     */
    public Object last()
    {
        return ref.getRangeAt(ref.countRanges(restrict)-1, restrict);
    }

    /* (non-Javadoc)
     * @see java.util.SortedSet#comparator()
     */
    public Comparator comparator()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.SortedSet#headSet(java.lang.Object)
     */
    public SortedSet headSet(Object to)
    {
        // LATER(joe): implement
        // VerseRange range = AbstractPassage.toVerseRange(to);
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.SortedSet#tailSet(java.lang.Object)
     */
    public SortedSet tailSet(Object fromElement)
    {
        // LATER(joe): implement
        // VerseRange range = AbstractPassage.toVerseRange(to);
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.SortedSet#subSet(java.lang.Object, java.lang.Object)
     */
    public SortedSet subSet(Object fromElement, Object toElement)
    {
        // LATER(joe): implement
        // VerseRange range = AbstractPassage.toVerseRange(to);
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        return ref.equals(o);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return ref.hashCode();
    }

    /**
     * How we create Passages
     */
    private static KeyFactory keyf = PassageKeyFactory.instance();

    /**
     * What restrictions are we using which dividing the passage up
     */
    private static RestrictionType restrict = RestrictionType.CHAPTER;

    /**
     * The real store of data
     */
    private Passage ref = null;
}