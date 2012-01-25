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
 * ID: $Id: JSwordAllTests.java 2114 2011-03-12 16:35:31Z dmsmith $
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
public class CommonAllTests extends TestCase {
    public CommonAllTests(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(org.crosswire.common.compress.AllTests.suite());
        suite.addTest(org.crosswire.common.diff.AllTests.suite());
        suite.addTest(org.crosswire.common.history.AllTests.suite());
        suite.addTest(org.crosswire.common.icu.AllTests.suite());
        suite.addTest(org.crosswire.common.progress.AllTests.suite());
        suite.addTest(org.crosswire.common.xml.AllTests.suite());
        suite.addTest(org.crosswire.common.util.AllTests.suite());
        return suite;
    }
}
