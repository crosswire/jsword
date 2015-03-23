package org.crosswire.jsword.index.lucene;

import static org.junit.Assert.assertTrue;

import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.junit.Before;
import org.junit.Test;


/**
 * Test indexManager responsibilities
 *
 * @author Sijo Cherian
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 */
public class LuceneIndexManagerTest {

    private LuceneIndexManager indexManager;

    @Before
    public void setUp() throws Exception {
        indexManager = (LuceneIndexManager) IndexManagerFactory.getIndexManager();
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
        System.out.println(IndexMetadata.generateInstalledBooksIndexVersionReport());

        Book reindexedBook = null;
        for (Book insBook : myBooks.getBooks()) {
            createOrUpgradeIndex(insBook);

            assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) == InstalledIndex.instance().getInstalledIndexVersion(reindexedBook));
            assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) >= IndexMetadata.instance().getLatestIndexVersion());

            assertTrue(indexManager.isIndexed(insBook));
            assertTrue(indexManager.needsReindexing(insBook) == false);


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

        for (Book insBook : myBooks.getBooks()) {

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
            assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) >= IndexMetadata.instance().getLatestIndexVersion());

            assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) == InstalledIndex.instance().getInstalledIndexVersion(reindexedBook));
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

        for (Book insBook : myBooks.getBooks()) {

            //todo  update LatestVersion of one book, higher than its InstalledVersion


            if (indexManager.isIndexed(insBook)
                    && indexManager.needsReindexing(insBook)) {
                System.out.println("Reindexing: " + insBook.getName());
                performedReindexing = true;
                reindexedBook = insBook;

                indexManager.deleteIndex(insBook);
                indexManager.scheduleIndexCreation(insBook);
                break;
            }


        } //for

        if (performedReindexing) {
            assertTrue(indexManager.needsReindexing(reindexedBook) == false );
            assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) >= IndexMetadata.instance().getLatestIndexVersion());

            assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) == InstalledIndex.instance().getInstalledIndexVersion(reindexedBook));
            //A random query test
            //String myquery = VerseField + ":(john)";
            // Key key = reindexedBook.find(myquery);
            // System.out.println(myquery + " , ResultList: " + key.getName());

        }
    }


    protected static final String VerseField = "content";

}

