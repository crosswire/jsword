package org.crosswire.jsword.index.lucene;

import org.crosswire.common.util.PropertyMap;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.passage.Key;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * Test indexManager responsibilities
 *
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

    /*  Test needsReindexing() method  */

    @Test
    public void testInstalledVersionMetadataFileNotExisting() throws Exception {

        Books myBooks = Books.installed();
        boolean performedReindexing = false;
        Book reindexedBook = null;

        for (Book insBook : myBooks.getBooks()) {

            if (indexManager.isIndexed(insBook)) {
                //todo if(InstalledIndex metadataFile exist) delete it for testing

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

            //todo After we implement Installed.Index.Version value update in metadata file after reindexing, then  assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) == InstalledIndex.instance().getInstalledIndexVersion(reindexedBook) );
            //Can run queries
            String myquery = VerseField + ":(john)";
            Key key = reindexedBook.find(myquery);
            System.out.println(myquery + " , ResultList: " + key.getName());

        }
    }

    @Test
    public void testInstalledVersionEqualToLatestVersion() throws Exception {

        Books myBooks = Books.installed();

        Book reindexedBook = null;
        for (Book insBook : myBooks.getBooks()) {
            if (indexManager.isIndexed(insBook)) {
                //todo explicitly add metadataFile with Version= LatestVersion value
                assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) == InstalledIndex.instance().getInstalledIndexVersion(reindexedBook));
                assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) >= IndexMetadata.instance().getLatestIndexVersion());

                assertTrue(indexManager.needsReindexing(insBook) == false);

            }
        } //for

    }

    //
    @Test
    public void testInstalledVersionLessThanLatestVersion() throws Exception {

        Books myBooks = Books.installed();
        boolean performedReindexing = false;
        Book reindexedBook = null;
        for (Book insBook : myBooks.getBooks()) {


            //todo if(metadataFile exist) update InstalledVersion to a older value
            //assertTrue(indexManager.needsReindexing(insBook) == true );
            if (indexManager.needsReindexing(insBook)) {
                System.out.println("Reindexing: " + insBook.getName());
                performedReindexing = true;
                reindexedBook = insBook;
                if (indexManager.isIndexed(insBook))
                    indexManager.deleteIndex(insBook);
                indexManager.scheduleIndexCreation(insBook);
                break;
            }


        } //for

        if (performedReindexing) {
            assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) >= IndexMetadata.instance().getLatestIndexVersion());

            //todo After we implement Installed.Index.Version stored in metadata file, then  assertTrue(IndexMetadata.instance().getLatestIndexVersion(reindexedBook) == IndexMetadata.instance().getInstalledIndexVersion(reindexedBook) );

            String myquery = VerseField + ":(john)";
            Key key = reindexedBook.find(myquery);
            System.out.println(myquery + " , ResultList: " + key.getName());

        }
    }


    protected static final String VerseField = "content";

}

