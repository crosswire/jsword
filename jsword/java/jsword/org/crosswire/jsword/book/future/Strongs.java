
package org.crosswire.jsword.book.future;

import org.crosswire.common.util.LogicError;

/**
 * Strongs is a convenience way of recording a Strongs number instead of
 * using a String with a number in it. (A Strongs number can not be a number
 * because Hebrew and Greek numbers are distinguished only by the Hebrew
 * having a 0 at the start.) The class is immutable.
 * Numbers that exist:<ul>
 * <li>Hebrew: 1-8674
 * <li>Greek: 1-5624 (but not 1418, 2717, 3203-3302, 4452)
 * <li>Parsing: 0, 5625-5773, 8675-8809 (but not 5626, 5653, 5687, 5767, 8679)
 * </ul>
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Strongs
{
    /* From the properties file:

    # Strongs messages
    strongs_greek=Greek:
    strongs_hebrew=Hebrew:
    strongs_parsing=Parsing:
    
    # Strongs error messages
    strongs_error_parse=Strongs number must be of the form <n>, <0n> or (n) where n is a number. Given \'{0}\'
    strongs_error_number=Could not get a number from \'{0}\'
    strongs_error_hebrew=Hebrew numbers must be between 0 and {0,number,integer}. Given {1,number,integer}
    strongs_error_greek=Greek numbers must be between 0 and {0,number,integer}. Given {1,number,integer}
    strongs_error_parsing=Parsing numbers must be greater than 0. Given {0,number,integer}
    strongs_error_type=Strongs numbers must have a type in the range, 0-2. Given {0,number,integer}

     */

    /**
     * Create a Strongs number from an OLB descriptive string.
     * @param desc The OLB style descriptive string
     */
    public Strongs(String desc)
    {
        // This is only the local copy.
        desc = desc.trim();

        try
        {
            if (desc.charAt(0) == '<')
            {
                // It's a Greek or Hebrew number
                if (desc.charAt(desc.length()-1) != '>')
                    throw new IllegalArgumentException("PassageUtil.getResource(\"strongs_error_parse\", new Object[] { desc })");

                if (desc.charAt(1) == '0')
                {
                    set(HEBREW, Integer.parseInt(desc.substring(2, desc.length()-1)));
                }
                else
                {
                    set(GREEK, Integer.parseInt(desc.substring(1, desc.length()-1)));
                }
            }
            else if (desc.charAt(0) == '(')
            {
                // It's a parsing number
                if (desc.charAt(desc.length()-1) != ')')
                    throw new IllegalArgumentException("PassageUtil.getResource(\"strongs_error_parse\", new Object[] { desc })");

                set(PARSING, Integer.parseInt(desc.substring(1, desc.length()-1)));
            }
        }
        catch (NumberFormatException ex)
        {
            throw new IllegalArgumentException("PassageUtil.getResource(\"strongs_error_number\", new Object[] { desc })");
        }
    }

    /**
     * Create a Strongs number from a type and a number
     * @param type 0=HEBREW, 1=GREEK, 2=PARSING
     * @param number The strongs number
     */
    public Strongs(int type, int number)
    {
        set(type, number);
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
            return "<" + number + ">";
        case HEBREW:
            return "<0" + number + ">";
        case PARSING:
            return "(" + number + ")";
        default:
            throw new LogicError();
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
            return "PassageUtil.getResource(\"strongs_greek\")" + number;
        case HEBREW:
            return "PassageUtil.getResource(\"strongs_hebrew\")" + number;
        case PARSING:
            return "PassageUtil.getResource(\"strongs_parsing\")" + number;
        default:
            throw new LogicError();
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
    private void set(int type, int number)
    {
        this.type = type;
        this.number = number;

        // Check validity
        switch (type)
        {
        case HEBREW:
            if (number > HEBREW_MAX || number < 1)
            {
                // Object[] params = new Object[] { new Integer(HEBREW_MAX), new Integer(number) };
                throw new IllegalArgumentException("PassageUtil.getResource(\"strongs_error_hebrew\", params)");
            }
            break;

        case GREEK:
            if (number > GREEK_MAX || number < 1)
            {
                // Object[] params = new Object[] { new Integer(GREEK_MAX), new Integer(number) };
                throw new IllegalArgumentException("PassageUtil.getResource(\"strongs_error_greek\", params)");
            }
            // We have not checked for 1418, 2717, 3203-3302, 4452 which do not appear to
            // but legal numbers for Greek words. Should we do this?
            break;

        case PARSING:
            if (number < 1)
            {
                // Object[] params = new Object[] { new Integer(number) };
                throw new IllegalArgumentException("PassageUtil.getResource(\"strongs_error_parsing\", params)");
            }
            // The correct range seems to be: 0, 5625-5773, 8675-8809, but not 5626, 5653, 5687, 5767, 8679
            // I'm not sure if this is 100% correct so I'll not check it at the mo.
            break;

        default:
            throw new IllegalArgumentException("PassageUtil.getResource(\"strongs_error_type\")"+type);
        }
    }

    /** This is a Hebrew word */
    public static final int HEBREW = 0;

    /** This is a Greek word */
    public static final int GREEK = 1;

    /** This is a Parsing note */
    public static final int PARSING = 2;

    /** This largest legal value for a Greek number */
    public static final int GREEK_MAX = 5624;

    /** This largest legal value for a Hebrew number */
    public static final int HEBREW_MAX = 8674;

    /** The type of this Strongs number */
    private int type = 0;

    /** The actual number itself */
    private int number = 0;
}
