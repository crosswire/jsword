/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
// package default;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JSwordLimboAllTests extends TestCase
{
    public JSwordLimboAllTests(String s)
    {
        super(s);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(org.crosswire.jsword.book.jdbc.JDBCBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.JDBCBookDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.JDBCBookUtilTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.raw.RawBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.raw.RawBookDriverTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.ser.SerBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.ser.SerBookDriverTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.stub.StubBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.stub.StubBookDriverTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.sword.SwordBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.SwordBookDriverTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.remote.ConverterTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoterTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.RemoteMethodTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoteBookTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoteBookDriverTest.class);

        return suite;
    }
}
