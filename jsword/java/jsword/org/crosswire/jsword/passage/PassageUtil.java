package org.crosswire.jsword.passage;

/**
 * A Utility class containing various static methods.
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
public class PassageUtil
{
    /**
     * Prevent Instansiation
     */
    private PassageUtil()
    {
    }

    /**
     * The default Blur settings. This is not explicitly used by any of
     * the blur methods. It simply provides a convienient place to store
     * a default blur setting if desired.
     * @param value The new default blur setting
     */
    public static void setBlurRestriction(int value)
    {
        if (!PassageUtil.isValidBlurRestriction(value))
        {
            throw new IllegalArgumentException(Msg.ERROR_BLUR.toString());
        }

        blur = value;
    }

    /**
     * The default Blur settings. This is not explicitly used by any of the
     * blur methods. It simply provides a convienient place to store a default
     * blur setting if desired.
     * @return The current default blur setting
     */
    public static int getBlurRestriction()
    {
        return blur;
    }

    /**
     * Is the given restriction a valid one?
     * @param test The restriction to be tested for validity
     * @return True if the number is OK, False otherwise
     */
    public static final boolean isValidBlurRestriction(int test)
    {
        switch (test)
        {
        case PassageConstants.RESTRICT_BOOK:
        case PassageConstants.RESTRICT_CHAPTER:
        case PassageConstants.RESTRICT_NONE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Is the given accuracy a valid one?
     * @param test The accuracy to be tested for validity
     * @return True if the number is OK, False otherwise
     */
    public static final boolean isValidAccuracy(int test)
    {
        switch (test)
        {
        case PassageConstants.ACCURACY_BOOK_VERSE:
        case PassageConstants.ACCURACY_BOOK_CHAPTER:
        case PassageConstants.ACCURACY_BOOK_ONLY:
        case PassageConstants.ACCURACY_CHAPTER_VERSE:
        case PassageConstants.ACCURACY_NUMBER_ONLY:
        case PassageConstants.ACCURACY_NONE:
            return true;

        default:
            return false;
        }
    }

    /**
     * Do we remember the original string used to configure us?
     * @param persistentNaming True to keep the old string
     *        False (default) to generate a new better one
     */
    public static final void setPersistentNaming(boolean persistentNaming)
    {
        PassageUtil.persistentNaming = persistentNaming;
    }

    /**
     * Do we remember the original string used to configure us?
     * @return True if we keep the old string
     *         False (default) if we generate a new better one
     */
    public static final boolean isPersistentNaming()
    {
        return persistentNaming;
    }

    /**
     * By default do we remember the original string used to configure us?
     * @return false getDefaultPersistentNaming() is always false
     */
    public static final boolean getDefaultPersistentNaming()
    {
        return false;
    }
    /**
     * Do we store the original string?
     */
    private static boolean persistentNaming = getDefaultPersistentNaming();

    /**
     * The blur restriction
     */
    private static int blur = PassageConstants.RESTRICT_CHAPTER;
}
