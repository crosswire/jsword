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
package org.crosswire.jsword.book;

import org.jdom.Element;

/**
 * Strongs is a convenience way of recording a Strongs number instead of
 * using a String with a number in it.
 * 
 * <p>A Strongs number can not be a number because Hebrew and Greek numbers are
 * distinguished only by the Hebrew having a 0 at the start.
 * <p>The class is immutable.
 * <p>Numbers that exist:<ul>
 * <li>Hebrew: 1-8674
 * <li>Greek: 1-5624 (but not 1418, 2717, 3203-3302, 4452)
 * <li>Parsing: 0, 5625-5773, 8675-8809 (but not 5626, 5653, 5687, 5767, 8679)
 * </ul>
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Strongs
{
    /**
     * Create a Strongs number from an OLB descriptive string.
     * @param desc The OLB style descriptive string
     */
    public Strongs(String desc) throws BookException
    {
        // This is only the local copy.
        desc = desc.trim();

        try
        {
            if (desc.charAt(0) == '<')
            {
                // It's a Greek or Hebrew number
                if (desc.charAt(desc.length() - 1) != '>')
                {
                    throw new BookException(Msg.STRONGS_ERROR_PARSE, new Object[] { desc });
                }

                if (desc.charAt(1) == '0')
                {
                    set(HEBREW, Integer.parseInt(desc.substring(2, desc.length() - 1)));
                }
                else
                {
                    set(GREEK, Integer.parseInt(desc.substring(1, desc.length() - 1)));
                }
            }
            else if (desc.charAt(0) == '(')
            {
                // It's a parsing number
                if (desc.charAt(desc.length() - 1) != ')')
                {
                    throw new BookException(Msg.STRONGS_ERROR_PARSE, new Object[] { desc });
                }

                set(PARSING, Integer.parseInt(desc.substring(1, desc.length() - 1)));
            }

            throw new BookException(Msg.STRONGS_ERROR_PARSE, new Object[] { desc });
        }
        catch (NumberFormatException ex)
        {
            throw new BookException(Msg.STRONGS_ERROR_NUMBER, new Object[] { desc });
        }
    }

    /**
     * Create a Strongs number from a type and a number
     * @param type 0=HEBREW, 1=GREEK, 2=PARSING
     * @param number The strongs number
     */
    public Strongs(int type, int number) throws BookException
    {
        set(type, number);
    }

    /**
     * Work out what the Strongs number is from the W element
     * @param w The element to investigate
     */
    public Strongs(Element w) throws BookException
    {
        String lemma = w.getAttributeValue(OSISUtil.ATTRIBUTE_W_LEMMA);

        // LATER(joe): I think it goes x-study:[H|G]number, but this will need fixing...
        int colonpos = lemma.indexOf(":"); //$NON-NLS-1$
        if (colonpos != -1)
        {
            lemma = lemma.substring(colonpos + 1);
        }

        int newtype = -1;
        if (lemma.charAt(0) == 'H')
        {
            newtype = HEBREW;
            lemma = lemma.substring(1);
        }
        else if (lemma.charAt(0) == 'G')
        {
            newtype = GREEK;
            lemma = lemma.substring(1);
        }
        else
        {
            newtype = PARSING;
        }

        int newnum = Integer.parseInt(lemma);

        set(newtype, newnum);
    }

    /**
     * The string that would be used by the On-Line Bible to describe this
     * number
     * @return The OLB sytle string
     */
    public String getOLBName()
    {
        switch (type)
        {
        case GREEK:
            return "<" + number + '>'; //$NON-NLS-1$
        case HEBREW:
            return "<0" + number + '>'; //$NON-NLS-1$
        case PARSING:
            return "(" + number + ')'; //$NON-NLS-1$
        default:
            assert false : type;
            return "!Error!"; //$NON-NLS-1$
        }
    }

    /**
     * A very short description of the Strongs number
     * @return The short description
     */
    public String getDescription()
    {
        switch (type)
        {
        case GREEK:
            return Msg.STRONGS_GREEK.toString() + number;
        case HEBREW:
            return Msg.STRONGS_HEBREW.toString() + number;
        case PARSING:
            return Msg.STRONGS_PARSING.toString() + number;
        default:
            assert false : type;
            return "!Error!"; //$NON-NLS-1$
        }
    }

    /**
     * Default to returning the OLB name for this number
     * @return A descriptive String
     */
    public String toString()
    {
        return getOLBName();
    }

    /**
     * @return The type of this Strongs number
     */
    public int getType()
    {
        return type;
    }

    /**
     * @return The actual number that this represents
     */
    public int getNumber()
    {
        return 0;
    }

    /**
     * Is this number a Greek one?
     * @return true if the number is Greek
     */
    public boolean isGreek()
    {
        return type == GREEK;
    }

    /**
     * Is this number a Hebrew one?
     * @return true if the number is Hebrew
     */
    public boolean isHebrew()
    {
        return type == HEBREW;
    }

    /**
     * Is this number a Parsing one?
     * @return true if the number is Parsing
     */
    public boolean isParsing()
    {
        return type == PARSING;
    }

    /**
     * Create a Strongs number from a type and a number.
     * This is private since it should only be called from a constructor
     * to keep this class immutable.
     * @param type 0=HEBREW, 1=GREEK, 2=PARSING
     * @param number The strongs number
     */
    private void set(int type, int number) throws BookException
    {
        this.type = type;
        this.number = number;

        // Check validity
        switch (type)
        {
        case HEBREW:
            if (number > HEBREW_MAX || number < 1)
            {
                throw new BookException(Msg.STRONGS_ERROR_HEBREW, new Object[] { new Integer(HEBREW_MAX), new Integer(number) });
            }
            break;

        case GREEK:
            if (number > GREEK_MAX || number < 1)
            {
                throw new BookException(Msg.STRONGS_ERROR_GREEK, new Object[] { new Integer(GREEK_MAX), new Integer(number) });
            }
            // We have not checked for 1418, 2717, 3203-3302, 4452 which do not appear to
            // but legal numbers for Greek words. Should we do this?
            break;

        case PARSING:
            if (number < 1)
            {
                throw new BookException(Msg.STRONGS_ERROR_PARSING, new Object[] { new Integer(number) });
            }
            // The correct range seems to be: 0, 5625-5773, 8675-8809, but not 5626, 5653, 5687, 5767, 8679
            // I'm not sure if this is 100% correct so I'll not check it at the mo.
            break;

        default:
            throw new BookException(Msg.STRONGS_ERROR_TYPE, new Object[] { new Integer(number) });
        }
    }

    /**
     * This is a Hebrew word
     */
    public static final int HEBREW = 0;

    /**
     * This is a Greek word
     */
    public static final int GREEK = 1;

    /**
     * This is a Parsing note
     */
    public static final int PARSING = 2;

    /**
     * This largest legal value for a Greek number
     */
    public static final int GREEK_MAX = 5624;

    /**
     * This largest legal value for a Hebrew number
     */
    public static final int HEBREW_MAX = 8674;

    /**
     * The type of this Strongs number
     */
    private int type;

    /**
     * The actual number itself
     */
    private int number;
}
