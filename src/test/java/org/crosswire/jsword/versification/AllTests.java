/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005 - 2012
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.versification;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test of Versification classes.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for org.crosswire.jsword.versification");
        // $JUnit-BEGIN$
        suite.addTestSuite(BookNameTest.class);
        suite.addTestSuite(BibleNamesTest.class);
        suite.addTestSuite(BibleBookTest.class);
        suite.addTestSuite(BibleBookListTest.class);
        suite.addTestSuite(VersificationTest.class);
        // $JUnit-END$
        return suite;
    }
}
