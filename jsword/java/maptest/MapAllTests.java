
// package default;

import org.crosswire.jsword.util.Project;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MapAllTests extends TestCase
{
    public MapAllTests(String s)
    {
        super(s);
    }

    public static Test suite()
    {
        Project.init();

        TestSuite suite = new TestSuite();

        suite.addTestSuite(org.crosswire.jsword.map.model.TestLinkArray.class);
        suite.addTestSuite(org.crosswire.jsword.map.model.TestMap.class);

        return suite;
    }
}
