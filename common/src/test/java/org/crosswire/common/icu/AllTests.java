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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: AllTests.java 1405 2007-06-14 12:22:52 -0400 (Thu, 14 Jun 2007) dmsmith $
 */
package org.crosswire.common.icu;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class AllTests
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for org.crosswire.common.icu"); //$NON-NLS-1$
        //$JUnit-BEGIN$
        suite.addTest(new TestSuite(NumberShaperTest.class));
        //$JUnit-END$
        return suite;
    }
}