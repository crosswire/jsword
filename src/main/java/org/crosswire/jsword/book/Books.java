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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.util.CollectionUtil;
import org.crosswire.common.util.PluginUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.JSOtherMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Books class (along with Book) is the central point of contact between the
 * rest of the world and this set of packages.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 * @author DM Smith
 */
public final class Books extends AbstractBookList {
    /**
     * Create a singleton instance of the class. This is private to ensure that
     * only one can be created. This also makes the class final!
     */
    private Books() {
        super();
        initials = new HashMap<String, Book>();
        names = new HashMap<String, Book>();
        drivers = new HashSet<BookDriver>();
        books = new TreeSet();
    }

    /**
     * Accessor for the singleton instance
     * 
     * @return The singleton instance
     */
    public static Books installed() {
        return instance;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBooks()
     */
    public synchronized List<Book> getBooks() {
        return CollectionUtil.createList(books);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBooks(org.crosswire.jsword.book.BookFilter)
     */
    @Override
    public synchronized List<Book> getBooks(BookFilter filter) {
        return CollectionUtil.createList(new BookFilterIterator(books, filter));
    }

    /**
     * Search for the book by initials and name.
     * Looks for exact matches first, then searches case insensitive. 
     * In all cases the whole initials or the whole name has to match.
     * 
     * @param name The initials or name of the book to find
     * @return the book or null
     */
    public synchronized Book getBook(String name) {
        if (name == null) {
            return null;
        }

        Book book = initials.get(name);
        if (book != null) {
            return book;
        }

        book = names.get(name);
        if (book != null) {
            return book;
        }

        // Check for case-insensitive initial and name matches
        for (Book b : books) {
            if (name.equalsIgnoreCase(b.getInitials()) || name.equalsIgnoreCase(b.getName())) {
                return b;
            }
        }

        return null;
    }

    /**
     * Add a Book to the current list of Books. This method should only be
     * called by BibleDrivers, it is not a method for general consumption.
     * 
     * @param book the book to add to this book list
     */
    public synchronized void addBook(Book book) {
        if (book != null && books.add(book)) {
            initials.put(book.getInitials(), book);
            names.put(book.getName(), book);
            fireBooksChanged(instance, book, true);
        }
    }

    /**
     * Remove a Book from the current list of Books. This method should only be
     * called by BibleDrivers, it is not a method for general consumption.
     * 
     * @param book the book to be removed from this book list
     * @throws BookException when an error occurs when performing this method
     */
    public synchronized void removeBook(Book book) throws BookException {
        // log.debug("unregistering book: {}", bmd.getName());

        Activator.deactivate(book);

        boolean removed = books.remove(book);
        if (removed) {
            initials.remove(book.getInitials());
            names.remove(book.getName());
            fireBooksChanged(instance, book, false);
        } else {
            throw new BookException(JSOtherMsg.lookupText("Could not remove unregistered Book: {0}", book.getName()));
        }
    }

    /**
     * Register the driver, adding its books to the list. Any books that this
     * driver used, but not any more are removed. This can be called repeatedly
     * to re-register the driver.
     * 
     * @param driver
     *            The BookDriver to add
     * @throws BookException when an error occurs when performing this method
     */
    public synchronized void registerDriver(BookDriver driver) throws BookException {
        log.debug("begin registering driver: {}", driver.getClass().getName());

        drivers.add(driver);

        // Go through all the books and add all the new ones.
        // Remove those that are not known to the driver, but used to be.
        Book[] bookArray = driver.getBooks();
        Set<Book> current = CollectionUtil.createSet(new BookFilterIterator(books, BookFilters.getBooksByDriver(driver)));

        for (int j = 0; j < bookArray.length; j++) {
            Book b = bookArray[j];
            if (current.contains(b)) {
                // Since it was already in there, we don't add it.
                // By removing it from current we will be left with
                // what is not now known by the driver.
                current.remove(b);
            } else {
                addBook(bookArray[j]);
            }
        }

        // Remove the books from the previous version of the driver
        // that are not in this version.
        for (Book book : current) {
            removeBook(book);
        }

        log.debug("end registering driver: {}", driver.getClass().getName());
    }

    /**
     * Since Books keeps a track of drivers itself, including creating them when
     * registered it can be hard to get a hold of the current book driver. This
     * method gives access to the registered instances.
     * 
     * @param type the type of BookDriver
     * @return matching BookDrivers
     */
    public synchronized BookDriver[] getDriversByClass(Class<? extends BookDriver> type) {
        List<BookDriver> matches = new ArrayList<BookDriver>();
        for (BookDriver driver : drivers) {
            if (driver.getClass() == type) {
                matches.add(driver);
            }
        }

        return matches.toArray(new BookDriver[matches.size()]);
    }

    /**
     * Get an array of all the known drivers
     * 
     * @return Found int or the default value
     */
    public synchronized BookDriver[] getDrivers() {
        return drivers.toArray(new BookDriver[drivers.size()]);
    }

    /**
     * Registers all the drivers known to the program.
     */
    private void autoRegister() {
        // This will classload them all and they will register themselves.
        Class<? extends BookDriver>[] types = PluginUtil.getImplementors(BookDriver.class);

        log.debug("begin auto-registering {} drivers:", Integer.toString(types.length));

        for (int i = 0; i < types.length; i++) {
            // job.setProgress(Msg.JOB_DRIVER.toString() +
            // ClassUtils.getShortClassName(types[i]));

            try {
                Method driverInstance = types[i].getMethod("instance", new Class[0]);
                BookDriver driver = (BookDriver) driverInstance.invoke(null, new Object[0]); // types[i].newInstance();
                registerDriver(driver);
            } catch (NoSuchMethodException e) {
                Reporter.informUser(Books.class, e);
            } catch (IllegalArgumentException e) {
                Reporter.informUser(Books.class, e);
            } catch (IllegalAccessException e) {
                Reporter.informUser(Books.class, e);
            } catch (InvocationTargetException e) {
                Reporter.informUser(Books.class, e);
            } catch (BookException e) {
                Reporter.informUser(Books.class, e);
            }
        }
    }

    /**
     * The collection of Books
     */
    private Set<Book> books;

    /**
     * The map of book initials
     */
    private Map<String, Book> initials;

    /**
     * The map of book names
     */
    private Map<String, Book> names;

    /**
     * An array of BookDrivers
     */
    private Set<BookDriver> drivers;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(Books.class);

    /**
     * The singleton instance.
     * This needs to be declared after all other statics it uses.
     */
    private static final Books instance = new Books();
    // And it cannot register books until it is fully constructed
    // When this was the last call in the constructor it resulted
    // in "instance" being null in something it called.
    static {
        log.trace("Auto-registering start @ {}", Long.toString(System.currentTimeMillis()));
        instance.autoRegister();
        log.trace("Auto-registering stop @ {}", Long.toString(System.currentTimeMillis()));
    }
}
