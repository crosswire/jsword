// package default;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit Test.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class JSwordAllTests extends TestCase
{
    public JSwordAllTests(String s)
    {
        super(s);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(org.crosswire.jsword.passage.BibleInfoTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageConstantsTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageMixTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageSizeTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageSpeedTest.class);
        // commented out because it causes OutOfMemoryErrors.
        //suite.addTestSuite(org.crosswire.jsword.passage.PassageSpeedOptTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageTallyTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageTally2Test.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageUtilTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageWriteSpeedTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.VerseTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.VerseRangeTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.BooksTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.BookMetaDataTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.SentanceUtilTest.class);
        // commented out because the tests were very poor.
        //suite.addTestSuite(org.crosswire.jsword.book.OsisTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.JDBCBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.JDBCBookDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.JDBCBookUtilTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.raw.RawBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.raw.RawBookDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoteBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoteBookDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.ser.SerBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.ser.SerBookDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.stub.StubBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.stub.StubBookDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.SwordBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.SwordBookDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.ConverterTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoterTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.RemoteMethodTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.search.parse.DictionaryTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.parse.CustomTokenizerTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.parse.ParserTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.parse.SearchWordsTest.class);

        return suite;
    }
}
