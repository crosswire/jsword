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
package org.crosswire.jsword.book;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.crosswire.common.util.Filter;
import org.crosswire.common.util.SortedListSet;

/**
 * BookSet represents a collection of descriptions about Books
 * which may be subsetted into other BookMetaDataSets.
 * Each set is naturally ordered.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BookSet extends SortedListSet
{

    public BookSet()
    {
        super();
    }

    public BookSet(Collection books)
    {
        super(books);
    }

    /**
     * Gets the sorted set of all keys which can be used for groupings.
     * These are all the property keys across the BookMetaDatas in this list.
     * @return the set of all keys which can be used for grouping.
     */
    public Set getGroups()
    {
        Set results = new TreeSet();
        Iterator bookIter = iterator();
        while (bookIter.hasNext())
        {
            Book book = (Book) bookIter.next();
            results.addAll(book.getProperties().keySet());
        }
        return results;
    }

    /**
     * Get the sorted set of all values for a particular key.
     * If there is a BookMetaData that does not have a value
     * for that key, then null will be in the set. This can be use
     * to categorize books that don't have that key.
     * For example, "Language" will return all the languages
     * for this BookMetaDataList and null for which the language
     * is unknown.
     * @param key
     * @return the values for a particular key.
     */
    public Set getGroup(String key)
    {
        Set results = new TreeSet();
        Iterator bookIter = iterator();
        while (bookIter.hasNext())
        {
            Book book = (Book) bookIter.next();
            Object property = book.getProperties().get(key);
            String propertyValue = property == null ? Msg.BOOK_METADATA_SET_OTHER.toString() : property.toString();
            results.add(propertyValue);
        }
        return results;
    }

    public BookSet filter(String key, String value)
    {
        return (BookSet) filter(new GroupFilter(key, value));
    }

    /**
     * GroupFilter does the SQL traditional group by.
     */
    private static final class GroupFilter implements Filter
    {
        public GroupFilter(String aKey, String aValue)
        {
            key = aKey;
            value = aValue;
        }

        public boolean test(Object obj)
        {
            Book book = (Book) obj;
            return book.getProperties().get(key) == value;
        }
        private String key;
        private String value;
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258688806185154867L;
}
