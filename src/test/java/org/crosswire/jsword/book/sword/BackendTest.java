package org.crosswire.jsword.book.sword;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Test simple functionality across multiple backends, to ensure that all types
 * of backing drivers are working correctly
 * 
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BackendTest extends TestCase {
    private static final org.crosswire.common.util.Logger LOGGER = org.crosswire.common.util.Logger.getLogger(BackendTest.class);

    /**
     * Z Text
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendKJVTextZ() throws Exception {
        String version = "KJV";
        String reference = "Romans 1:1-3";
        String assertion = "<w lemma=\"strong:G3588 strong:G5207\" morph=\"robinson:T-GSM robinson:N-GSM\" src=\"2 3\">Son</w>";

        backendTest(version, reference, assertion);
    }

    /**
     * Z Text - cos it's important
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendESV() throws Exception {
        String version = "ESV";
        String reference = "Romans 1:1-3";
        String assertion = "set apart for the gospel of God,";

        backendTest(version, reference, assertion);
    }

    /**
     * Z Text - cos it's important
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendESVFullChapter() throws Exception {
        String version = "ESV";
        String reference = "Romans 1";

        backendTest(version, reference, " <title type=\"x-gen\">Romans 1</title>", "<verse osisID=\"Rom.1.1\">", "<verse osisID=\"Rom.1.32\">",
                "set apart for the gospel of God,", "give approval to those who practice them");
    }

    /**
     * Z Text - cos it's important
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendESV1AndFollowing() throws Exception {
        String version = "ESV";
        String reference = "Romans 1:1-ff";

        backendTest(version, reference, "<title type=\"x-gen\">Romans 1:1-32</title>", "<verse osisID=\"Rom.1.1\">", "<verse osisID=\"Rom.1.32\">",
                "set apart for the gospel of God,", "give approval to those who practice them");
    }

    /**
     * Z Text - cos it's important
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendCrossChapterBoundary() throws Exception {
        String version = "ESV";
        String reference = "Rom 1:32-Rom:2:2";

        String xml = backendTest(version, reference, "<verse osisID=\"Rom.1.32\">", "<title type=\"x-gen\">Romans 2:0-2</title>", "<verse osisID=\"Rom.2.1\">",
                "<verse osisID=\"Rom.2.2\">");

        assertFalse(xml.contains("<verse osisID=\"Rom.1.31\">"));
        assertFalse(xml.contains("<verse osisID=\"Rom.2.3\">"));

    }

    /**
     * Z Text - cos it's important
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendMiddleChapter() throws Exception {
        String version = "ESV";
        String reference = "Rom 1:2-1:31";

        String xml = backendTest(version, reference, "<verse osisID=\"Rom.1.2\">", "<verse osisID=\"Rom.1.31\">");

        assertFalse(xml.contains("<verse osisID=\"Rom.1.1\">"));
        assertFalse(xml.contains("<verse osisID=\"Rom.1.32\">"));

    }

    /**
     * Z Text - cos it's important
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendMiddleToEnd() throws Exception {
        String version = "ESV";
        String reference = "Rom 1:5-ff";

        String xml = backendTest(version, reference, "<verse osisID=\"Rom.1.5\">", "<verse osisID=\"Rom.1.31\">", "<verse osisID=\"Rom.1.32\">");

        assertFalse(xml.contains("<verse osisID=\"Rom.1.1\">"));
        assertFalse(xml.contains("<verse osisID=\"Rom.1.4\">"));
        assertFalse(xml.contains("<verse osisID=\"Rom.2.0\">"));
        assertFalse(xml.contains("<verse osisID=\"Rom.2.1\">"));
    }
    
    
    
    /**
     * Z Text - cos it's important
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackend2ChaptersStartMiddle() throws Exception {
        String version = "ESV";
        String reference = "Rom 1:5-3:14";

        String xml = backendTest(version, reference, "<verse osisID=\"Rom.1.5\">", 
                "<verse osisID=\"Rom.2.1\">","<verse osisID=\"Rom.2.10\">",  
                "<verse osisID=\"Rom.3.1\">","<verse osisID=\"Rom.3.10\">",
                "<verse osisID=\"Rom.3.14\">"
                );

        assertFalse(xml.contains("<verse osisID=\"Rom.1.4\">"));
        assertFalse(xml.contains("<verse osisID=\"Rom.3.15\">"));
    }
    
    /**
     * Z Text - cos it's important
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackend2ChaptersStartVerse1() throws Exception {
        String version = "ESV";
        String reference = "Rom 1:1-3:14";

        String xml = backendTest(version, reference, 
                "<verse osisID=\"Rom.1.1\">","<verse osisID=\"Rom.1.2\">",
                "<verse osisID=\"Rom.1.5\">", 
                "<verse osisID=\"Rom.2.1\">","<verse osisID=\"Rom.2.10\">",  
                "<verse osisID=\"Rom.3.1\">","<verse osisID=\"Rom.3.10\">",
                "<verse osisID=\"Rom.3.14\">"
                );

        assertFalse(xml.contains("<verse osisID=\"Rom.1.0\">"));
        assertFalse(xml.contains("<verse osisID=\"Rom.3.15\">"));
    }
    
    /**
     * Z Text - cos it's important
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackend2ChaptersStartVerse0() throws Exception {
        String version = "ESV";
        String reference = "Rom 1:0-3:14";

        String xml = backendTest(version, reference, 
                "<verse osisID=\"Rom.1.2\">","<verse osisID=\"Rom.1.2\">",
                "<verse osisID=\"Rom.1.5\">", 
                "<verse osisID=\"Rom.2.1\">","<verse osisID=\"Rom.2.10\">",  
                "<verse osisID=\"Rom.3.1\">","<verse osisID=\"Rom.3.10\">",
                "<verse osisID=\"Rom.3.14\">"
                );

        assertFalse(xml.contains("<verse osisID=\"Rom.3.15\">"));
    }

    /**
     * Commentariess Raw
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendMHCCCommentsRaw() throws Exception {
        String version = "MHCC";
        String reference = "Romans 1:1-3";
        String assertion = "Father of all believers, and coming to them through the Lord";

        backendTest(version, reference, assertion);
    }

    /**
     * Comments Z
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendGenevaCommentsZ() throws Exception {
        String version = "Geneva";
        String reference = "Romans 1:1-3";
        String assertion = "(3) By declaring the sum of the doctrine of the Gospel, he stirs up the Romans";

        backendTest(version, reference, assertion);
    }

    /**
     * Lexicon/Dictionary RawLd
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendStrongsGreekRawLd() throws Exception {
        String version = "StrongsGreek";
        String reference = "G3588";
        String assertion = "3588  ho   ho, including the feminine";

        backendTest(version, reference, assertion);
    }

    /**
     * Lexicon/Dictionary RawLd
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendNaveZld() throws Exception {
        String version = "Nave";
        String reference = "AARON";
        String assertion = "Marriage of";

        backendTest(version, reference, assertion);
    }

    /**
     * Lexicon/Dictionary RawLd
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendJosephus() throws Exception {
        String version = "Josephus";
        String assertion = "calamity of our whole nation; for those that were fond of";

        final Book currentBook = Books.installed().getBook(version);
        if (currentBook == null) {
            return;
        }

        Key key = currentBook.getKey("The Life of Flavius Josephus").get(5);
        backendTest(currentBook, key, assertion);
    }

    /**
     * Lexicon/Dictionary RawLd
     * 
     * @throws Exception
     *             an uncaught, failing, exception
     */
    public void testBackendOt1Nt2Devotional() throws Exception {
        String version = "ot1nt2";
        String assertion = "Genesis 1-2";

        Book book = Books.installed().getBook(version);
        if (book == null) {
            return;
        }
        backendTest(book, book.getGlobalKeyList().get(0), assertion);
    }

    private String backendTest(String version, String reference, String... assertion) throws NoSuchKeyException, BookException {
        final Book currentBook = Books.installed().getBook(version);

        if (currentBook == null) {
            return null;
        }

        return backendTest(currentBook, currentBook.getKey(reference), assertion);
    }

    private String backendTest(Book currentBook, Key key, String... assertions) throws NoSuchKeyException, BookException {
        final BookData bookData = new BookData(currentBook, key);
        final Element osisFragment = bookData.getOsisFragment();

        final XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        String xml = xmlOutputter.outputString(osisFragment);
        LOGGER.debug(xml);

        for (String s : assertions)
            Assert.assertTrue(s, xml.contains(s));
        return xml;
    }
}
