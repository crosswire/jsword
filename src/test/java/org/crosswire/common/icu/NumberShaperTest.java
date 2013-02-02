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
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */

package org.crosswire.common.icu;

import java.util.Locale;

import junit.framework.TestCase;

import org.crosswire.jsword.internationalisation.DefaultLocaleProvider;
import org.crosswire.jsword.internationalisation.LocaleProvider;
import org.crosswire.jsword.internationalisation.LocaleProviderManager;

/**
 * JUnit test of NumberShaper.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class NumberShaperTest extends TestCase {
    private static final String europeanDigits = "0123456789";
    private static final String easternArabicDigits = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9";

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

    @Override
    protected void tearDown()  {
        LocaleProviderManager.setLocaleProvider(new DefaultLocaleProvider());
    }
}
