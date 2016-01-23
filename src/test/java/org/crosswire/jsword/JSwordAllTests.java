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
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
@RunWith(Suite.class)
@SuiteClasses({
    org.crosswire.jsword.prerequisites.AllTests.class,
    org.crosswire.jsword.book.AllTests.class,
    org.crosswire.jsword.book.filter.thml.AllTests.class,
    org.crosswire.jsword.book.sword.AllTests.class,
    org.crosswire.jsword.bridge.AllTests.class,
    org.crosswire.jsword.index.lucene.analysis.AllTests.class,
    org.crosswire.jsword.passage.AllTests.class,
    org.crosswire.jsword.versification.AllTests.class,
    org.crosswire.jsword.versification.system.AllTests.class
})
public class JSwordAllTests {
}
