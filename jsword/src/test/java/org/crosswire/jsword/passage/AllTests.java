package org.crosswire.jsword.passage;

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
        TestSuite suite = new TestSuite("Test for org.crosswire.jsword.passage");
        // $JUnit-BEGIN$
        suite.addTestSuite(AccuracyTypeTest.class);
        suite.addTestSuite(BibleInfoTest.class);
        suite.addTestSuite(PassageConstantsTest.class);
        suite.addTestSuite(PassageMixTest.class);
        suite.addTestSuite(PassageSizeTest.class);
        // commented out because it causes OutOfMemoryErrors.
        // suite.addTestSuite(PassageSpeedOptTest.class);
        suite.addTestSuite(PassageSpeedTest.class);
        suite.addTestSuite(PassageTallyTest.class);
        suite.addTestSuite(PassageTally2Test.class);
        suite.addTestSuite(PassageUtilTest.class);
        suite.addTestSuite(PassageWriteSpeedTest.class);
        suite.addTestSuite(VerseTest.class);
        suite.addTestSuite(VerseRangeTest.class);
        // $JUnit-END$
        return suite;
    }
}
