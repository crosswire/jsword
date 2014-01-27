package org.crosswire.jsword.prerequisites;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.bridge.BookInstaller;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class BookTestPreRequisites {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookTestPreRequisites.class);
    private static final String[] books = new String[]{"KJV", "ESV"};
    private BookInstaller underTest = null;
    private Books installedBooks = Books.installed();

    @Test
    public void testInstallBook() {
        for (int ii = 0; ii < books.length; ii++) {
            try {
                if (installedBooks.getBook(books[ii]) == null) {
                    if(underTest == null) {
                        underTest = new BookInstaller();
                    }
                    LOGGER.info("Installing [{}]... Please wait...", books[ii]);
                    underTest.installBook("CrossWire", underTest.getRepositoryBook("CrossWire", books[ii]));
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }
}
