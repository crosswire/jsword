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
 * © CrossWire Bible Society, 2008 - 2016
 *
 */
package org.crosswire.jsword.bridge;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.versification.BookName;

/**
 * Determines the scope of the Bible. That is, the verses that are in the Bible
 * and the verses that are not. This is based upon the KJV versification.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BibleScope {

    public BibleScope(Book book) {
        this.book = book;
    }

    /**
     * Get a key containing all the verses that are in this Bible.
     * 
     * @return verses that are in scope
     */
    public Key getInScope() {
        computeScope();
        return inScope;
    }

    /**
     * Get a key containing all the verses that are not in this Bible.
     * 
     * @return verses that are out of scope
     */
    public Key getOutOfScope() {
        computeScope();
        return outScope;
    }

    private void computeScope() {
        if (inScope == null) {
            Key all = book.getGlobalKeyList();
            inScope = book.createEmptyKeyList();
            outScope = book.createEmptyKeyList();
            for (Key key : all) {
                if (book.contains(key)) {
                    inScope.addAll(key);
                } else {
                    outScope.addAll(key);
                }
            }
        }
    }

    public static void report(Book b) {
        if (!b.getBookCategory().equals(BookCategory.BIBLE) && !b.getBookCategory().equals(BookCategory.COMMENTARY)) {
            System.err.println(b.getInitials() + " is not a Bible or Commentary");
            // System.exit(1);
        }

        BibleScope scope = new BibleScope(b);
        BookName.setFullBookName(false); // use short names
        System.out.println('[' + b.getInitials() + ']');
        System.out.println("InScope=" + scope.getInScope().getOsisRef());
        System.out.println("OutScope=" + scope.getOutOfScope().getOsisRef());
    }

    private Book book;

    /**
     * Call with &lt;operation&gt; book. Where operation can be one of:
     * <ul>
     * <li>check - returns "TRUE" or "FALSE" indicating whether the index exists
     * or not</li>
     * <li>create - (re)create the index</li>
     * <li>delete - delete the index if it exists</li>
     * </ul>
     * And book is the initials of a book, e.g. KJV.
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
            return;
        }

        System.err.println("BibleScope " + args[0]);

        Book b = Books.installed().getBook(args[0]);
        if (b == null) {
            System.err.println("Book not found");
            return;
        }

        report(b);

        // List books =
        // Books.installed().getBooks(BookFilters.getCommentaries());
        // Iterator iter = books.iterator();
        // while (iter.hasNext())
        // {
        // try {
        // report((Book) iter.next());
        // } catch (Exception e) {
        // System.out.println("exception " + e.toString());
        // }
        // System.out.println();
        // }
    }

    public static void usage() {
        System.err.println("Usage: BibleScope book");
    }

    private Key inScope;
    private Key outScope;
}
