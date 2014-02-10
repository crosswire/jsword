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
 * Copyright: 2007 - 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.common.icu;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.crosswire.jsword.internationalisation.DefaultLocaleProvider;
import org.crosswire.jsword.internationalisation.LocaleProvider;
import org.crosswire.jsword.internationalisation.LocaleProviderManager;
import org.junit.After;
import org.junit.Test;

/**
 * JUnit test of NumberShaper.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
public class NumberShaperTest {
    private static final String europeanDigits = "0123456789";
    private static final String easternArabicDigits = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";

    @After
    public void tearDown()  {
        LocaleProviderManager.setLocaleProvider(new DefaultLocaleProvider());
    }

    @Test
    public void testShape() {
        //This presumably prevents tests from running in parallel, but can't do much about this, given all the statics.
        LocaleProviderManager.setLocaleProvider(new LocaleProvider() {
            public Locale getUserLocale() {
                return new Locale("fa");
            }
        });
        
        //test the number shaper
        NumberShaper shaper = new NumberShaper();
        assertEquals(easternArabicDigits, shaper.shape(europeanDigits));
        // Note: the following depends upon whether icu is on the classpath
        if (shaper.canUnshape()) {
            assertEquals(europeanDigits, shaper.unshape(easternArabicDigits));
        } else {
            assertEquals(easternArabicDigits, shaper.unshape(easternArabicDigits));
        }
    }

}
