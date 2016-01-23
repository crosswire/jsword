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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.SerializingContentHandler;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.sword.SwordBookPath;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.versification.BookName;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * The DWR DwrBridge adapts JSword to DWR. This is based upon APIExamples.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class DwrBridge {
    /**
     * Get a listing of all the available books.
     * 
     * @param filter
     *            The custom filter specification string
     * @return a list of (initial, name) string pairs
     * @see BookInstaller#getInstalledBook(String)
     */
    public String[][] getInstalledBooks(String filter) {
        List<String[]> reply = new ArrayList<String[]>();

        for (Book book : BookInstaller.getInstalledBooks(filter)) {
            String[] rbook = new String[] {
                    book.getInitials(), book.getName()
            };
            reply.add(rbook);
        }

        // If we can't find a book, indicate that.
        if (reply.isEmpty()) {
            reply.add(new String[] {
                    "", "No Books installed"});
        }

        return reply.toArray(new String[reply.size()][]);
    }

    /**
     * Determine whether the named book can be searched, that is, whether the
     * book is indexed.
     * 
     * @param bookInitials
     *            the named book to check.
     * @return true if searching can be performed
     */
    public boolean isIndexed(String bookInitials) {
        return isBookIndexed(BookInstaller.getInstalledBook(bookInitials));
    }

    /**
     * Determine the size of this reference.
     * 
     * @param bookInitials
     *            the book to which the reference applies.
     * @param reference
     *            the actual reference
     * @return the number of entries for this reference.
     * @throws NoSuchKeyException
     */
    public int getCardinality(String bookInitials, String reference) throws NoSuchKeyException {
        Book book = BookInstaller.getInstalledBook(bookInitials);
        if (book != null) {
            Key key = book.getKey(reference);
            return key.getCardinality();
        }
        return 0;
    }

    /**
     * Obtain the OSIS representation from a book for a reference, pruning a
     * reference to a limited number of keys.
     * 
     * @param bookInitials
     *            the book to use
     * @param reference
     *            a reference, appropriate for the book, for one or more keys
     * @param start 
     * @param count 
     * @return the OSIS as a string
     * @throws BookException 
     * @throws NoSuchKeyException 
     */
    public String getOSISString(String bookInitials, String reference, int start, int count) throws BookException, NoSuchKeyException {
        String result = "";
        try {
            SAXEventProvider sep = getOSISProvider(bookInitials, reference, start, count);
            if (sep != null) {
                ContentHandler ser = new SerializingContentHandler();
                sep.provideSAXEvents(ser);
                result = ser.toString();
            }
            return result;
        } catch (SAXException ex) {
            // This is allowed
            // throw new BookException(Msg.JSWORD_SAXPARSE, ex);
        }
        return result;
    }

    /**
     * Get a reference list for a search result against a book.
     * 
     * @param bookInitials
     * @param searchRequest
     * @return The reference for the matching.
     * @throws BookException
     */
    public String search(String bookInitials, String searchRequest) throws BookException {
        Book book = BookInstaller.getInstalledBook(bookInitials);
        if (isBookIndexed(book) && searchRequest != null) {
            if (BookCategory.BIBLE.equals(book.getBookCategory())) {
                BookName.setFullBookName(false);
            }
            return book.find(searchRequest).getName();
        }
        return "";
    }

    /**
     * Get close matches for a target in a book whose keys have a meaningful
     * sort. This is not true of keys that are numeric or contain numbers.
     * (unless the numbers are 0 filled.)
     * 
     * @param bookInitials 
     * @param searchRequest 
     * @param maxMatchCount 
     * @return the matches
     */
    public String[] match(String bookInitials, String searchRequest, int maxMatchCount) {
        Book book = BookInstaller.getInstalledBook(bookInitials);
        if (book == null || searchRequest == null || maxMatchCount < 1) {
            return new String[0];
        }

        // Need to use the locale of the book so that we can find stuff in the
        // proper order
        Locale sortLocale = new Locale(book.getLanguage().getCode());
        String target = searchRequest.toLowerCase(sortLocale);

        // Get everything with target as the prefix.
        // In Unicode \uFFFF is reserved for internal use
        // and is greater than every character defined in Unicode
        String endTarget = target + '\uffff';

        // This whole getGlobalKeyList is messy.
        // 1) Some drivers cache the list which is slow.
        // 2) Binary lookup would be much better.
        // 3) Caching the whole list here is dumb.
        // What is needed is that all this be pushed into JSword proper.
        // TODO(dms): Push this into Book interface.
        List<String> result = new ArrayList<String>();
        int count = 0;
        for (Key key : book.getGlobalKeyList()) {
            String entry = key.getName().toLowerCase(sortLocale);
            if (entry.compareTo(target) >= 0) {
                if (entry.compareTo(endTarget) < 0) {
                    result.add(entry);
                    count++;
                }

                // Have we seen enough?
                if (count >= maxMatchCount) {
                    break;
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * For the sake of diagnostics, return the locations that JSword will look
     * for books.
     * 
     * @return the SWORD path
     */
    public String[] getSwordPath() {
        File[] filePath = SwordBookPath.getSwordPath();
        if (filePath.length == 0) {
            return new String[] {
                "No path"};
        }
        String[] path = new String[filePath.length];
        for (int i = 0; i < filePath.length; i++) {
            path[i] = filePath[i].getAbsolutePath();
        }
        return path;
    }

    /**
     * Determine whether the book can be searched, that is, whether the book is
     * indexed.
     * 
     * @param book
     *            the book to check.
     * @return true if searching can be performed
     */
    public boolean isBookIndexed(Book book) {
        return book != null && IndexManagerFactory.getIndexManager().isIndexed(book);
    }

    /**
     * Get BookData representing one or more Book entries, but capped to a
     * maximum number of entries.
     * 
     * @param bookInitials
     *            the book to use
     * @param reference
     *            a reference, appropriate for the book, of one or more entries
     * @param start
     *            the starting point where 0 is the first.
     * @param count
     *            the maximum number of entries to use
     * 
     * @throws NoSuchKeyException
     */
    private BookData getBookData(String bookInitials, String reference, int start, int count) throws NoSuchKeyException {
        Book book = BookInstaller.getInstalledBook(bookInitials);
        if (book == null || reference == null || count < 1) {
            return null;
        }

        // TODO(dms): add trim(count) and trim(start, count) to the key
        // interface.
        Key key = null;
        if (BookCategory.BIBLE.equals(book.getBookCategory())) {
            key = book.getKey(reference);
            Passage remainder = (Passage) key;
            if (start > 0) {
                remainder = remainder.trimVerses(start);
            }
            remainder.trimVerses(count);
            key = remainder;
        } else if (BookCategory.GENERAL_BOOK.equals(book.getBookCategory())) {
            // At this time we cannot trim a General Book
            key = book.getKey(reference);
        } else {
            key = book.getKey(reference);

            // Do we need to trim?
            if (start > 0 || key.getCardinality() > count) {
                key = book.createEmptyKeyList();
                int i = 0;
                for (Key aKey : key) {
                    i++;
                    if (i <= start) {
                        continue;
                    }
                    if (i >= count) {
                        break;
                    }
                    key.addAll(aKey);
                }
            }
        }

        return new BookData(book, key);
    }

    /**
     * Obtain a SAX event provider for the OSIS document representation of one
     * or more book entries.
     * 
     * @param bookInitials
     *            the book to use
     * @param reference
     *            a reference, appropriate for the book, of one or more entries
     */
    private SAXEventProvider getOSISProvider(String bookInitials, String reference, int start, int count) throws BookException, NoSuchKeyException {
        BookData data = getBookData(bookInitials, reference, start, count);
        SAXEventProvider provider = null;
        if (data != null) {
            provider = data.getSAXEventProvider();
        }
        return provider;
    }

}
