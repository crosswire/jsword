package org.crosswire.jsword.book.sword;

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
        TestSuite suite = new TestSuite("Test for org.crosswire.jsword.book.sword");
        // $JUnit-BEGIN$
        suite.addTest(new TestSuite(ConfigEntryTableTest.class));
        suite.addTest(new TestSuite(GenBookTest.class));
        suite.addTest(new TestSuite(RawFileBackendTest.class));
        suite.addTest(new TestSuite(SwordBookDriverTest.class));
        suite.addTest(new TestSuite(SwordBookMetaDataTest.class));
        suite.addTest(new TestSuite(SwordBookTest.class));
        // $JUnit-END$
        return suite;
    }
}
