package org.crosswire.common.xml;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.crosswire.common.xml"); //$NON-NLS-1$
        //$JUnit-BEGIN$
        suite.addTestSuite(XMLUtilTest.class);
        //$JUnit-END$
        return suite;
    }
}
