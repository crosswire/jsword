
// package default;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JSwordAllTests extends TestCase
{
    public JSwordAllTests(String s)
    {
        super(s);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(org.crosswire.jsword.book.jdbc.TestJDBCBible.class);
        suite.addTestSuite(org.crosswire.jsword.book.raw.TestRawBible.class);
        suite.addTestSuite(org.crosswire.jsword.book.ser.TestSerBible.class);
        suite.addTestSuite(org.crosswire.jsword.book.ser.TestSerBibleDriver.class);
        suite.addTestSuite(org.crosswire.jsword.book.TestBibles.class);
        suite.addTestSuite(org.crosswire.jsword.book.TestBookMetaData.class);
        suite.addTestSuite(org.crosswire.jsword.book.TestBookUtil.class);
        suite.addTestSuite(org.crosswire.jsword.book.TestDriverManager.class);
        suite.addTestSuite(org.crosswire.jsword.control.dictionary.TestDictionary.class);
        suite.addTestSuite(org.crosswire.jsword.control.search.TestCustomTokenizer.class);
        suite.addTestSuite(org.crosswire.jsword.control.search.TestEngine.class);
        suite.addTestSuite(org.crosswire.jsword.control.search.TestSearchWords.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestBooks.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageConstants.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageMix.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageSize.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageSpeed.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageSpeedOpt.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageTally.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageTally2.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageUtil.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestPassageWriteSpeed.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestVerse.class);
        suite.addTestSuite(org.crosswire.jsword.passage.TestVerseRange.class);
        suite.addTestSuite(org.crosswire.jsword.view.style.TestStyle.class);

        return suite;
    }
}
