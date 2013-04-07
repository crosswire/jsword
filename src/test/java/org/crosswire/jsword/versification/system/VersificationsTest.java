package org.crosswire.jsword.versification.system;

import java.util.Iterator;

import junit.framework.TestCase;

import org.crosswire.jsword.versification.Versification;

public class VersificationsTest extends TestCase {

    public VersificationsTest() {
    }

    public VersificationsTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() {
    }

    @Override
    protected void tearDown() {
    }

    public void testFluff() {
        Versification v11n = null;
        Iterator<String> iter = Versifications.instance().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            v11n = Versifications.instance().getVersification(name);
            assertTrue("Predefined v11n is known", Versifications.instance().isDefined(name));
            assertEquals("Name matches", name, v11n.getName());
            assertTrue("v11n is not empty", v11n.maximumOrdinal() > 0);
        }
    }
}
