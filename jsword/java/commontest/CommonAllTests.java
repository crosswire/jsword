
// package default;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CommonAllTests extends TestCase
{
    public CommonAllTests(String s)
    {
        super(s);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(org.crosswire.common.util.TestHelpDesk.class);
        suite.addTestSuite(org.crosswire.common.util.TestStringUtil.class);
        suite.addTestSuite(org.crosswire.common.util.TestThreadUtil.class);

        return suite;
    }
}
