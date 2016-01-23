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
package org.crosswire.jsword.index.lucene;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test indexManager responsibilities
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Sijo Cherian
 */
public class LuceneIndexManagerTest {

    private LuceneIndexManager indexManager;
    private BookFilter filter;

    @Before
    public void setUp() throws Exception {
        indexManager = (LuceneIndexManager) IndexManagerFactory.getIndexManager();
        filter = BookFilters.either(BookFilters.getBibles(), BookFilters.getCommentaries());
    }

    //Sample usage:  create new index Or upgrade index if needed
    protected void createOrUpgradeIndex(Book book) throws BookException {

        if (!indexManager.isIndexed(book)) {
            System.out.println("Indexing book: " + book.getName());
            indexManager.scheduleIndexCreation(book);
        } else if (indexManager.needsReindexing(book)) {  //is already Indexed, but stale
            System.out.println("Re-indexing book: " + book.getName());
            indexManager.deleteIndex(book);
            indexManager.scheduleIndexCreation(book);
        }

    }

    @Test
    public void testInstalledVersionEqualToLatestVersion() throws Exception {

        Books myBooks = Books.installed();
        System.out.println(IndexMetadata.generateInstalledBooksIndexVersionReport(filter));

        Book reindexedBook = null;
        for (Book insBook : myBooks.getBooks(filter)) {
            createOrUpgradeIndex(insBook);

            Assert.assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) == InstalledIndex.instance().getInstalledIndexVersion(reindexedBook));
            Assert.assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) >= IndexMetadata.instance().getLatestIndexVersion());

            Assert.assertTrue(indexManager.isIndexed(insBook));
            Assert.assertFalse(indexManager.needsReindexing(insBook));


        } //for

    }



    /*  Test create/update of InstalledIndex.properties file  */
    @Test
    public void testInstalledVersionMetadataFileNotExisting() throws Exception {

        Books myBooks = Books.installed();
        boolean performedReindexing = false;
        Book reindexedBook = null;
        //delete InstalledIndex.properties , if it exists
        NetUtil.delete(InstalledIndex.instance().getPropertyFileURI());

        for (Book insBook : myBooks.getBooks(filter)) {

            if (indexManager.isIndexed(insBook)) {

                if (indexManager.needsReindexing(insBook)) {
                    System.out.println("Reindexing: " + insBook.getName());
                    performedReindexing = true;
                    reindexedBook = insBook;
                    indexManager.deleteIndex(insBook);
                    indexManager.scheduleIndexCreation(insBook);
                    break;
                }
            }
        } //for

        if (performedReindexing) {
            Assert.assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) >= IndexMetadata.instance().getLatestIndexVersion());

            Assert.assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) == InstalledIndex.instance().getInstalledIndexVersion(reindexedBook));
            //Can run queries
            // String myquery = VerseField + ":(john)";
            // Key key = reindexedBook.find(myquery);
            // System.out.println(myquery + " , ResultList: " + key.getName());

        }
    }


    //Test needsReindexing() method
    @Test
    public void testInstalledVersionLessThanLatestVersion() throws Exception {

        Books myBooks = Books.installed();
        boolean performedReindexing = false;
        Book reindexedBook = null;

        for (Book insBook : myBooks.getBooks(filter)) {

            //todo  update LatestVersion of one book, higher than its InstalledVersion


            if (indexManager.isIndexed(insBook)
                    && indexManager.needsReindexing(insBook))
            {
                System.out.println("Reindexing: " + insBook.getName());
                performedReindexing = true;
                reindexedBook = insBook;

                indexManager.deleteIndex(insBook);
                indexManager.scheduleIndexCreation(insBook);
                break;
            }


        } //for

        if (performedReindexing) {
            Assert.assertFalse(indexManager.needsReindexing(reindexedBook));
            Assert.assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) >= IndexMetadata.instance().getLatestIndexVersion());

            Assert.assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) == InstalledIndex.instance().getInstalledIndexVersion(reindexedBook));
            //A random query test
            //String myquery = VerseField + ":(john)";
            // Key key = reindexedBook.find(myquery);
            // System.out.println(myquery + " , ResultList: " + key.getName());

        }
    }


    protected static final String FIELD = "content";

}

