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
 * © CrossWire Bible Society, 2013 - 2016
 *
 */
package org.crosswire.jsword.versification.system;

import java.util.Iterator;

import org.crosswire.jsword.versification.Versification;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * JUnit test.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class VersificationsTest extends TestCase {
    @Test
    public void testFluff() {
        Versification v11n = null;
        Iterator<String> iter = Versifications.instance().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            v11n = Versifications.instance().getVersification(name);
            Assert.assertTrue("Predefined v11n is known", Versifications.instance().isDefined(name));
            Assert.assertEquals("Name matches", name, v11n.getName());
            Assert.assertTrue("v11n is not empty", v11n.maximumOrdinal() > 0);
        }
    }
}
