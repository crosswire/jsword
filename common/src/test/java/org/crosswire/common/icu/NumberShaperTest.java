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

/**
 * JUnit test of NumberShaper.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class NumberShaperTest extends TestCase
{
    private static final String europeanDigits = "0123456789"; //$NON-NLS-1$
    private static final String easternArabicDigits = "\u06f0\u06f1\u06f2\u06f3\u06f4\u06f5\u06f6\u06f7\u06f8\u06f9"; //$NON-NLS-1$
    public void testShape()
    {
        NumberShaper shaper = new NumberShaper(new Locale("fa")); //$NON-NLS-1$
        assertEquals(easternArabicDigits, shaper.shape(europeanDigits));
        // Note: the following depends upon whether icu is on the classpath
        if (shaper.canUnshape())
        {
            assertEquals(europeanDigits, shaper.unshape(easternArabicDigits));
        }
        else
        {
            assertEquals(easternArabicDigits, shaper.unshape(easternArabicDigits));
        }
    }

}
