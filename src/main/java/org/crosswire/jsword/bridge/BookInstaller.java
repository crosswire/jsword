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

import java.util.List;
import java.util.Map;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;

/**
 * Exports the Book in SWORD's imp format. This is identical to SWORD's mod2imp.
 * Note: it does not work with GenBook.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BookInstaller {

    public BookInstaller() {
        installManager = new InstallManager();
    }

    /**
     * Uninstall a book.
     * 
     * @param book
     *            the book to delete
     * @throws BookException
     */
    public void deleteBook(Book book) throws BookException {
        // Actually do the delete
        // This should be a call on installer.
        // This will also remove the book from the list of installed books.
        book.getDriver().delete(book);
    }

    /**
     * Get a list of all known installers.
     * 
     * @return the list of installers
     */
    public Map<String, Installer> getInstallers() {
        // Ask the Install Manager for a map of all known remote repositories
        // sites
        return installManager.getInstallers();
    }

    /**
     * Get a list of all installed books.
     * 
     * @return the list of installed books
     */
    public static List<Book> getInstalledBooks() {
        return Books.installed().getBooks();
    }

    /**
     * Get a list of installed books by BookFilter.
     * 
     * @param filter
     *            The book filter
     * @return the list of matching books
     * @see BookFilter
     * @see Books
     */
    public static List<Book> getInstalledBooks(BookFilter filter) {
        return Books.installed().getBooks(filter);
    }

    /**
     * Get a list of books by CustomFilter specification
     * 
     * @param filterSpec
     *            The filter string
     * @return the list of matching books
     * @see BookFilters#getCustom(java.lang.String)
     * @see Books
     */
    public static List<Book> getInstalledBooks(String filterSpec) {
        return getInstalledBooks(BookFilters.getCustom(filterSpec));
    }

    /**
     * Get a particular installed book by initials.
     * 
     * @param bookInitials
     *            The book name to search for
     * @return The found book. Null otherwise.
     */
    public static Book getInstalledBook(String bookInitials) {
        return Books.installed().getBook(bookInitials);
    }

    /**
     * Get a list of all known books for an installer.
     * 
     * @param repositoryName
     * @return the list of books at that repository
     */
    public List<Book> getRepositoryBooks(String repositoryName) {
        return installManager.getInstaller(repositoryName).getBooks();
    }

    /**
     * Get a list of books in a repository by BookFilter.
     * 
     * @param repositoryName 
     *            The name of the repository
     * @param filter
     *            The book filter
     * @return the matching books
     * @see BookFilter
     * @see Books
     */
    public List<Book> getRepositoryBooks(String repositoryName, BookFilter filter) {
        return installManager.getInstaller(repositoryName).getBooks(filter);
    }

    /**
     * Get a list of books in a repository by CustomFilter specification
     * 
     * @param repositoryName 
     *            the name of the repository
     * @param filterSpec
     *            The filter string
     * @return the list of books
     * @see BookFilters#getCustom(java.lang.String)
     * @see Books
     */
    public List<Book> getRepositoryBooks(String repositoryName, String filterSpec) {
        return getRepositoryBooks(repositoryName, BookFilters.getCustom(filterSpec));
    }

    /**
     * Get a particular installed book by initials.
     * 
     * @param repositoryName 
     *            the name of the repository
     * @param bookInitials
     *            The book name to search for
     * @return The found book. Null otherwise.
     */
    public Book getRepositoryBook(String repositoryName, String bookInitials) {
        return installManager.getInstaller(repositoryName).getBook(bookInitials);
    }

    /**
     * Reload the local cache for a remote repository.
     * 
     * @param repositoryName
     * @throws InstallException
     */
    public void reloadBookList(String repositoryName) throws InstallException {
        installManager.getInstaller(repositoryName).reloadBookList();
    }

    /**
     * Get a Book from the repository. Note this does not install it.
     * 
     * @param repositoryName
     *            the repository from which to get the book
     * @param bookName
     *            the name of the book to get
     * @return the Book
     */
    public Book getBook(String repositoryName, String bookName) {
        return installManager.getInstaller(repositoryName).getBook(bookName);
    }

    /**
     * Install a book, overwriting it if the book to be installed is newer.
     * 
     * @param repositoryName
     *            the name of the repository from which to get the book
     * @param book
     *            the book to get
     * @throws BookException
     * @throws InstallException
     */
    public void installBook(String repositoryName, Book book) throws BookException, InstallException {
        // An installer knows how to install books
        Installer installer = installManager.getInstaller(repositoryName);

        // Delete the book, if present
        // At the moment, JSword will not re-install. Later it will, if the
        // remote version is greater.
        if (Books.installed().getBook(book.getInitials()) != null) {
            deleteBook(book);
        }

        // Now install it. Note this is a background task.
        installer.install(book);
    }

    private InstallManager installManager;

    /**
     * BookInstaller can manage the installation of books with the following
     * capabilities.
     * 
     * <p>Usage: BookInstaller [option]</p>
     * <table border="0">
     * <caption>Options</caption>
     * <tr>
     * <td>uninstall</td>
     * <td>bookName</td>
     * <td>Uninstall book</td>
     * </tr>
     * <tr>
     * <td>sources</td>
     * <td>&nbsp;</td>
     * <td>List source repositories</td>
     * </tr>
     * <tr>
     * <td>list</td>
     * <td>&nbsp;</td>
     * <td>List installed books</td>
     * </tr>
     * <tr>
     * <td>list</td>
     * <td>repositoryName</td>
     * <td>list available books from a repository</td>
     * </tr>
     * <tr>
     * <td>reload</td>
     * <td>repositoryName</td>
     * <td>Reload the local cache for a repository</td>
     * </tr>
     * <tr>
     * <td>install</td>
     * <td>repositoryName bookName</td>
     * <td>Install a book from a repository</td>
     * </tr>
     * </table>
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            usage();
            return;
        }

        System.err.print("BookInstaller");
        for (int i = 0; i < args.length; i++) {
            System.err.print(' ');
            System.err.print(args[i]);
        }
        System.err.print('\n');

        BookInstaller installer = new BookInstaller();

        String operation = args[0];
        if ("uninstall".equalsIgnoreCase(operation)) {
            if (args.length == 2) {
                Book b = Books.installed().getBook(args[1]);
                if (b == null) {
                    System.err.println("Book not found");
                    return;
                }
                try {
                    installer.deleteBook(b);
                } catch (BookException e) {
                    e.printStackTrace();
                }
            } else {
                usage();
            }
        } else if ("sources".equalsIgnoreCase(operation)) {
            // Get all the installers one after the other
            Map<String, Installer> installers = installer.getInstallers();
            for (String name : installers.keySet()) {
                System.out.println(name);
            }
        } else if ("list".equalsIgnoreCase(operation)) {
            if (args.length == 1) {
                for (Book book : BookInstaller.getInstalledBooks()) {
                    System.out.println(book.getInitials());
                }
            } else if (args.length == 2) {
                for (Book book : installer.getRepositoryBooks(args[1])) {
                    System.out.println(book.getInitials());
                }
            } else {
                usage();
            }
        } else if ("reload".equalsIgnoreCase(operation)) {
            if (args.length == 2) {
                try {
                    installer.reloadBookList(args[1]);
                } catch (InstallException e) {
                    e.printStackTrace();
                }
            } else {
                usage();
            }
        } else if ("install".equalsIgnoreCase(operation)) {
            if (args.length == 3) {
                Book b = installer.getBook(args[1], args[2]);
                if (b == null) {
                    System.err.println("Book not found");
                    return;
                }
                try {
                    installer.installBook(args[1], b);
                } catch (BookException e) {
                    e.printStackTrace();
                } catch (InstallException e) {
                    e.printStackTrace();
                }
            } else {
                usage();
            }
        } else {
            usage();
        }
    }

    public static void usage() {
        System.err.println("usage: BookInstaller <option>");
        System.err.println("Options:");
        System.err.println("    uninstall bookName                 Uninstall book");
        System.err.println("    sources                            List remote source repositories");
        System.err.println("    list                               List installed books");
        System.err.println("    list      repositoryName           List available books from a repository");
        System.err.println("    reload    repositoryName           Reload local cache for a repository");
        System.err.println("    install   repositoryName bookName  Install a book from a repository");
    }
}
