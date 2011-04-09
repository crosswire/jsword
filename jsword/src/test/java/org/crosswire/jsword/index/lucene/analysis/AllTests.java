package org.crosswire.jsword.index.lucene.analysis;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.crosswire.jsword.index.lucene.analysis");
        // $JUnit-BEGIN$
        suite.addTestSuite(AnalyzerFactoryTest.class);
        suite.addTestSuite(ChineseLuceneAnalyzerTest.class);
        suite.addTestSuite(ConfigurableSnowballAnalyzerTest.class);
        suite.addTestSuite(EnglishLuceneAnalyzerTest.class);
        suite.addTestSuite(GreekLuceneAnalyzerTest.class);
        suite.addTestSuite(ThaiLuceneAnalyzerTest.class);
        // $JUnit-END$
        return suite;
    }
}
