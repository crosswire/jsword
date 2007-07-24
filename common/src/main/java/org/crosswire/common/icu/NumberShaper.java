/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the Internet at:
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

import java.awt.font.NumericShaper;
import java.util.Locale;

import org.crosswire.common.util.ReflectionUtil;

/**
 * NumberShaper changes numbers from one number system to another.
 * That is, the numbers 0-9 have different representations in some
 * locales. This means that they have different code points. For
 * example, Eastern Arabic numbers are from \u06f0 - \u06f9.
 * <p>
 * Internally, numbers will be represented with 0-9, but externally
 * they should show as a user wishes. Further user input may, optionally,
 * use the external form.
 * </p>
 * @see java.awt.font.NumericShaper
 * @see com.ibm.icu.text.ArabicShaping
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class NumberShaper
{
    /**
     * Create a shaper that is appropriate for the user's locale.
     */
    public NumberShaper()
    {
        this(Locale.getDefault());
    }

    /**
     * Create a shaper that is appropriate for the given locale.
     * 
     * @param the requested Locale
     */
    public NumberShaper(Locale locale)
    {
        this.locale = locale;
        this.nineShape = '\u0000';

        if (locale.getLanguage().equals("fa")) //$NON-NLS-1$
        {
            try
            {
                Class[] classTypes = { int.class };
                Object[] shape = { new Integer(ICU_DIGIT_TYPE_AN_EXTENDED | ICU_DIGITS_EN2AN) };
                Object[] unshape = { new Integer(ICU_DIGIT_TYPE_AN_EXTENDED | ICU_DIGITS_AN2EN) };
                arabicShaper = ReflectionUtil.construct("com.ibm.icu.text.ArabicShaping", shape, classTypes); //$NON-NLS-1$
                unArabicShaper = ReflectionUtil.construct("com.ibm.icu.text.ArabicShaping", unshape, classTypes); //$NON-NLS-1$
            }
            catch (Exception e)
            {
                // This is OK. The jar is not on the classpath
            }
            if (arabicShaper == null)
            {
                numericShaper = NumericShaper.getShaper(NumericShaper.EASTERN_ARABIC);
            }
        }

        if (locale.getLanguage().equals("ar")) //$NON-NLS-1$
        {
            try
            {
                Class[] classTypes = { int.class };
                Object[] shape = { new Integer(ICU_DIGIT_TYPE_AN | ICU_DIGITS_EN2AN) };
                Object[] unshape = { new Integer(ICU_DIGIT_TYPE_AN | ICU_DIGITS_AN2EN) };
                arabicShaper = ReflectionUtil.construct("com.ibm.icu.text.ArabicShaping", shape, classTypes); //$NON-NLS-1$
                unArabicShaper = ReflectionUtil.construct("com.ibm.icu.text.ArabicShaping", unshape, classTypes); //$NON-NLS-1$
            }
            catch (Exception e)
            {
                // This is OK. The jar is not on the classpath
            }
            if (arabicShaper == null)
            {
                numericShaper = NumericShaper.getShaper(NumericShaper.EASTERN_ARABIC);
            }
        }
    }

    /**
     * Determine whether shaping is possible.
     * 
     * @return whether shaping back to 0-9 is possible.
     */
    public boolean canShape()
    {
        return arabicShaper != null || numericShaper != null || getNine() != '9';
    }

    /**
     * Replace 0-9 in the input with representations appropriate for the script.
     * 
     * @param input the text to be transformed
     * @return the transformed text
     */
    public String shape(String input)
    {
        if (input == null)
        {
            return input;
        }

        if (arabicShaper != null)
        {
            Object[] params = { input };
            try
            {
                return (String) ReflectionUtil.invoke(arabicShaper, "shape", params); //$NON-NLS-1$
            }
            catch (Exception e)
            {
                // do nothing as it is OK for jar to not be present.
            }
        }

        if (numericShaper != null)
        {
            char[] src = input.toCharArray();
            numericShaper.shape(src, 0, src.length);
            return new String(src);
        }

        char[] src = input.toCharArray();
        if (shape(src, 0, src.length))
        {
            return new String(src);
        }

        return input;
    }

    /**
     * Determine whether shaping back to 0-9 is possible.
     * 
     * @return whether shaping back to 0-9 is possible.
     */
    public boolean canUnshape()
    {
        return getNine() != '9';
    }

    /**
     * Replace script representations of numbers with 0-9.
     * 
     * @param input the text to be transformed
     * @return the transformed text
     */
    public String unshape(String input)
    {
        if (unArabicShaper != null)
        {
            Object[] params = { input };
            try
            {
                return (String) ReflectionUtil.invoke(unArabicShaper, "shape", params); //$NON-NLS-1$
            }
            catch (Exception e)
            {
                // do nothing as it is OK for jar to not be present.
            }
        }

        char[] src = input.toCharArray();
        if (unshape(src, 0, src.length))
        {
            return new String(src);
        }
        
        return input;
    }

    /**
     * Perform shaping from 0-9 into target script.
     */
    private boolean shape(char[] src, int start, int count)
    {
        char nine = getNine();
        if (nine == '9')
        {
            return false;
        }

        return transform(src, start, count, '0', '9', nine - '9');
    }

    /**
     * Perform shaping back to 0-9.
     */
    private boolean unshape(char[] src, int start, int count)
    {
        int nine = getNine();
        if (nine == '9')
        {
            return false;
        }

        int zero = nine - 9;
        return transform(src, start, count, zero, nine, '9' - nine);
    }

    /**
     * Transform in place either to or from 0-9 and the script representation, returning true when at least one character is transformed.
     * 
     * @param src the text to transform
     * @param start the place in the string in which to start
     * @param count the number of characters to consume
     * @param zero zero in the source representation
     * @param nine nine in the source representation
     * @param offset the distance between zeros in the source and target representation 
     * @return
     */
    private boolean transform(char[] src, int start, int count, int zero, int nine, int offset)
    {
        char[] text = src;
        boolean transformed = false;
        for (int i = start, e = start + count; i < e; ++i)
        {
            char c = text[i];
            if (c >= zero && c <= nine)
            {
                text[i] = (char)(c + offset);
                transformed = true;
            }
        }
        return transformed;
    }

    /**
     * Establish nine for the language. There are languages that don't have zeroes.
     * 
     * @return
     */
    private char getNine()
    {
        if (nineShape == '\u0000')
        {
            nineShape = '9';
            if (locale.getLanguage().equals("fa")) //$NON-NLS-1$
            {
                nineShape = '\u06f9';
            }
            else if (locale.getLanguage().equals("ar")) //$NON-NLS-1$
            {
                nineShape = '\u0669';
            }
        }
        return nineShape;
    }

    // The following 4 values are replicated here from ArabicShaper.
    // This is needed for the sake of reflection.
    // If any of these change in ArabicShaper, they will need to be changed here as well.
    /**
     * Digit shaping option: Replace European digits (U+0030...U+0039) by Arabic-Indic digits.
     * @stable ICU 2.0
     */
    private static final int ICU_DIGITS_EN2AN = 0x20;

    /**
     * Digit shaping option: Replace Arabic-Indic digits by European digits (U+0030...U+0039).
     * @stable ICU 2.0
     */
    private static final int ICU_DIGITS_AN2EN = 0x40;

    /** 
     * Digit type option: Use Arabic-Indic digits (U+0660...U+0669). 
     * @stable ICU 2.0
     */
    private static final int ICU_DIGIT_TYPE_AN = 0;

    /** 
     * Digit type option: Use Eastern (Extended) Arabic-Indic digits (U+06f0...U+06f9). 
     * @stable ICU 2.0
     */
    private static final int ICU_DIGIT_TYPE_AN_EXTENDED = 0x100;

    /**
     * The locale for this shaper.
     */
    private Locale locale;

    /**
     * Nine for this shaper.
     */
    private char nineShape;

    /**
     * Convert 0-9 to \u06f0-\u06f9 for Persian, and \u0660-\u0669 for Arabic
     */
    private Object arabicShaper;
    /**
     * Revert \u06f0-\u06f9 for Persian, and \u0660-\u0669 for Arabic to 0-9
     */
    private Object unArabicShaper;
    private NumericShaper numericShaper;
}
