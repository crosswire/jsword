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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book;

import java.util.Comparator;

/**
 * Provides different ways to sort Books.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public final class BookComparators {
    /**
     * Ensure we can't be created
     */
    private BookComparators() {
    }

    /**
     * Order by default Book ordering
     */
    public static Comparator<Book> getDefault() {
        return new Comparator<Book>() {
            public int compare(Book o1, Book o2) {
                return o1.compareTo(o2);
            }
        };
    }

    /**
     * Order by Initials.
     */
    public static Comparator<Book> getInitialComparator() {
        return new Comparator<Book>() {
            public int compare(Book o1, Book o2) {
                return o1.getInitials().compareTo(o2.getInitials());
            }
        };
    }
}
