/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2015 - 2016
 */
package org.crosswire.common.util;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Defines a List that maintains the uniqueness of elements
 *
 * @param <E> The type of the element in this ListSet.
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class ListSet<E> extends AbstractCollection<E> implements Set<E> {

    /**
     * Constructs a new, empty list set.
     */
    public ListSet() {
        this(null);
    }

    /**
     * Constructs a new, empty list set, sorted according to the specified
     * comparator.  All elements inserted into the set must be <i>mutually
     * comparable</i> by the specified comparator: {@code comparator.compare(e1,
     * e2)} must not throw a {@code ClassCastException} for any elements
     * {@code e1} and {@code e2} in the set.  If the user attempts to add
     * an element to the set that violates this constraint, the
     * {@code add} call will throw a {@code ClassCastException}.
     *
     * @param comparator the comparator that will be used to order this set.
     *        If {@code null}, the {@linkplain Comparable natural
     *        ordering} of the elements will be used.
     */
    public ListSet(Comparator<? super E> comparator) {
        super();
        list = new ArrayList();
        set  = new TreeSet(comparator);
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public boolean add(E e) {
        boolean added = set.add(e);
        if (added) {
            list.add(e);
        }
        return added;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = set.remove(o);
        if (removed) {
            list.remove(o);
        }
        return removed;
    }

    public E remove(int index) {
        E t = list.get(index);
        remove(t);
        return t;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.containsAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean removed = false;
        Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            E next = it.next();
            if (!c.contains(next)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void clear() {
        set.clear();
        list.clear();
    }

    public E get(int index) {
        return list.get(index);
    }

    public E get(int index, E defaultValue) {
        E value = list.get(index);
        return value != null ? value : defaultValue;
    }

    @Override
    public Iterator<E> iterator() {
        // Need to properly implement remove
        return new Iterator<E>() {
            public boolean hasNext() {
                return itr.hasNext();
            }

            public E next() {
                E next = itr.next();
                current = next;
                return next;
            }

            public void remove() {
                itr.remove();
                set.remove(current);
                current = null;
            }

            private Iterator<E> itr = list.iterator();
            private E current;
        };
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    protected List<E> list;
    protected Set<E> set;
}
