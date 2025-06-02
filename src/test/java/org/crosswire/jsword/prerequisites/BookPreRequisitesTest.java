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
 * © CrossWire Bible Society, 2013 - 2016
 *
 */
package org.crosswire.jsword.prerequisites;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.bridge.BookInstaller;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class BookPreRequisitesTest {
    @Before
    public void setUp() throws Exception {
        installedBooks = Books.installed();
    }

    @Test
    public void testInstallBook() {
        for (int ii = 0; ii < BOOKS.length; ii++) {
            try {
                if (installedBooks.getBook(BOOKS[ii]) == null) {
                    if (underTest == null) {
                        underTest = new BookInstaller();
                    }
                    LOGGER.info("Installing [{}]... Please wait...", BOOKS[ii]);
                    underTest.installBook("CrossWire", underTest.getRepositoryBook("CrossWire", BOOKS[ii]));
                }
            } catch (BookException ex) {
                ex.printStackTrace();
                Assert.fail(ex.getMessage());
            } catch (InstallException ex) {
                ex.printStackTrace();
                Assert.fail(ex.getMessage());
            }
        }
    }

    private BookInstaller underTest;
    private Books installedBooks;

    /**
     * All books referenced in these unit tests that can be retrieved from the Crosswire repository.
     * Currently missing : ESV2011, MHCC, ot1nt2
     * Tests that reference a currently missing book are automatically skipped unless the book was installed externally.
     */
    private static final String[] BOOKS = new String[]{"KJV", "StrongsHebrew", "StrongsGreek", "Josephus", "Nave", "Geneva", "TurNTB"};
    private static final Logger LOGGER = LoggerFactory.getLogger(BookPreRequisitesTest.class);
}
