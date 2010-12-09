/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
// package default;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JSwordAllTests extends TestCase {
    public JSwordAllTests(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest(org.crosswire.common.util.AllTests.suite());
        suite.addTest(org.crosswire.common.progress.AllTests.suite());
        suite.addTest(org.crosswire.common.diff.AllTests.suite());
        suite.addTest(org.crosswire.common.history.AllTests.suite());
        suite.addTest(org.crosswire.common.xml.AllTests.suite());
        suite.addTest(org.crosswire.common.icu.AllTests.suite());

        suite.addTestSuite(org.crosswire.jsword.passage.BibleInfoTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageConstantsTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageMixTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageSizeTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageSpeedTest.class);
        // commented out because it causes OutOfMemoryErrors.
        // suite.addTestSuite(org.crosswire.jsword.passage.PassageSpeedOptTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageTallyTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageTally2Test.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageUtilTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageWriteSpeedTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.VerseTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.VerseRangeTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.BooksTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.BookMetaDataTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.SentanceUtilTest.class);

        // run independently:
        // suite.addTestSuite(org.crosswire.jsword.book.ReadEverything.class);
        // commented out because the tests were very poor.
        // suite.addTestSuite(org.crosswire.jsword.book.OsisTest.class);

        // Not a JUnit test
        // suite.addTestSuite(org.crosswire.jsword.book.test.Speed.class);

        /*
         * FIXME
         * suite.addTestSuite(org.crosswire.jsword.index.search.parse.ParserTest
         * .class);
         * suite.addTestSuite(org.crosswire.jsword.index.search.parse.WordsTest
         * .class);
         */

        suite.addTestSuite(org.crosswire.jsword.book.sword.ConfigEntryTableTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.RawFileBackendTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.SwordBookDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.SwordBookMetaDataTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.SwordBookTest.class);

        suite.addTestSuite(org.crosswire.jsword.bridge.DwrBridgeTest.class);

        suite.addTestSuite(org.crosswire.jsword.index.lucene.analysis.AnalyzerFactoryTest.class);
        suite.addTestSuite(org.crosswire.jsword.index.lucene.analysis.ChineseLuceneAnalyzerTest.class);
        suite.addTestSuite(org.crosswire.jsword.index.lucene.analysis.ConfigurableSnowballAnalyzerTest.class);
        suite.addTestSuite(org.crosswire.jsword.index.lucene.analysis.EnglishLuceneAnalyzerTest.class);
        suite.addTestSuite(org.crosswire.jsword.index.lucene.analysis.GreekLuceneAnalyzerTest.class);
        suite.addTestSuite(org.crosswire.jsword.index.lucene.analysis.ThaiLuceneAnalyzerTest.class);
        return suite;
    }
}
