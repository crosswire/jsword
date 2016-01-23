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
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.crosswire.common.util.Filter;

/**
 * BookSet represents a collection of descriptions about Books which may be
 * subsetted into other BookMetaDataSets. Each set is naturally ordered.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BookSet extends ArrayList<Book> implements Set<Book> {
    public BookSet() {
    }

    public BookSet(Collection<Book> books) {
        this();
        addAll(books);
    }

    /**
     * Gets the sorted set of all keys which can be used for groupings. These
     * are all the property keys across the BookMetaDatas in this list.
     * 
     * @return the set of all keys which can be used for grouping.
     */
    public Set<String> getGroups() {
        Set<String> results = new TreeSet<String>();
        for (Book book : this) {
            results.addAll(book.getPropertyKeys());
        }
        return results;
    }

    /**
     * Get the sorted set of all values for a particular key. If there is a
     * BookMetaData that does not have a value for that key, then null will be
     * in the set. This can be use to categorize books that don't have that key.
     * For example, "Language" will return all the languages for this
     * BookMetaDataList and null for which the language is unknown.
     * 
     * @param key the property key
     * @return the values for a particular key.
     */
    public Set<Object> getGroup(String key) {
        Set<Object> results = new TreeSet<Object>();
        for (Book book : this) {
            Object property = book.getProperty(key);
            if (property != null) {
                results.add(property);
            }
        }
        return results;
    }

    public BookSet filter(String key, Object value) {
        return filter(new GroupFilter(key, value));
    }

    /**
     * Get a set of books that satisfy the condition imposed by the filter.
     * 
     * @param filter the condition on which to select books
     * @return the set of matching books
     */
    public BookSet filter(Filter<Book> filter) {
        // create a copy of the list and
        // remove everything that fails the test.
        BookSet listSet = (BookSet) clone();
        Iterator<Book> iter = listSet.iterator();
        while (iter.hasNext()) {
            Book obj = iter.next();
            if (!filter.test(obj)) {
                iter.remove();
            }
        }
        return listSet;
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, Book element) {
        // ignore the requested index
        add(element);
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#add(java.lang.Object)
     */
    @Override
    public final boolean add(Book book) {
        // Add the item only if it is not in the list.
        // Add it into the list so that it is in sorted order.
        int pos = Collections.binarySearch(this, book);
        if (pos < 0) {
            super.add(-pos - 1, book);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#addAll(java.util.Collection)
     */
    @Override
    public final boolean addAll(Collection<? extends Book> c) {
        // Might be better to add the list to the end
        // and then sort the list.
        // This can be revisited if the list performs badly.
        boolean added = false;
        for (Book book : c) {
            if (add(book)) {
                added = true;
            }
        }
        return added;
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#addAll(int, java.util.Collection)
     */
    @Override
    public final boolean addAll(int index, Collection<? extends Book> c) {
        // Ignore the index
        return addAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.ArrayList#set(int, java.lang.Object)
     */
    @Override
    public Book set(int index, Book element) {
        // remove the item at the index (keep it to return it),
        // then insert the item into the sorted list.
        Book item = remove(index);
        add(element);
        return item;
    }

    /**
     * GroupFilter does the SQL traditional group by.
     */
    private static final class GroupFilter implements Filter<Book> {
        GroupFilter(String aKey, Object aValue) {
            key = aKey;
            value = aValue;
        }

        public boolean test(Book book) {
            String property = book.getProperty(key);
            return property != null && property.equals(value);
        }

        private String key;
        private Object value;
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258688806185154867L;

}
