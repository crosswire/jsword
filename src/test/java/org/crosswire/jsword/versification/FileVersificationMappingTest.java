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
 * © CrossWire Bible Society, 2014 - 2016
 *
 */
package org.crosswire.jsword.versification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;

import org.crosswire.common.config.ConfigException;
import org.crosswire.jsword.versification.system.Versifications;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * JUnit Test
 *
 * @author Chris Burrell
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.<br>
 * The copyright to this program is held by its authors.
 */
@RunWith(Parameterized.class)
public class FileVersificationMappingTest {
    private String v11nName;

    /**
     * @param v11nName the v11n name we are testing
     */
    public FileVersificationMappingTest(String v11nName) {
        this.v11nName = v11nName;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        Iterator<String> v11ns = Versifications.instance().iterator();
        List<Object[]> v11nsAsList = new ArrayList<Object[]>();
        while (v11ns.hasNext()) {
            v11nsAsList.add(new String[]{v11ns.next()});
        }

        return v11nsAsList;
    }

    @Test
    public void testVersifications() throws IOException, ConfigException {
        final Versification versification = Versifications.instance().getVersification(v11nName);
        try {
            FileVersificationMapping m = new FileVersificationMapping(versification);
            VersificationToKJVMapper mapper = new VersificationToKJVMapper(versification, m);
            Assert.assertFalse("Failed to parse " + this.v11nName, mapper.hasErrors());
        } catch (final MissingResourceException mre) {
            // This is allowed
            // ignore, as this basically means we don't have mappings...
        }
    }
}
