package org.crosswire.jsword.passage;

/**
 * A PassageFactory is in charge of creating Passages. The point of
 * implementing it as a Factory is that the Passage interface may be
 * implemented in different ways eached optimized for a different
 * task. The user should not need to know which implementation is best
 * at each task, so it asks a PassageFactory, to create what it thinks
 * best.
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
public class PassageFactory
{
    /**
     * Prevent Instansiation
     */
    private PassageFactory()
    {
    }

    /**
     * Optimize the Passage for speed
     */
    public static final int SPEED = 0;

    /**
     * Optimize the Passage for speed
     */
    public static final int WRITE_SPEED = 1;

    /**
     * Optimize the Passage for size
     */
    public static final int SIZE = 2;

    /**
     * Optimize the Passage for a mix
     */
    public static final int MIX = 3;

    /**
     * Optimize the Passage for tally operations
     */
    public static final int TALLY = 4;

    /**
     * Set the default reference type. Must be one of:<ul>
     * <li>PassageFactory.SPEED
     * <li>PassageFactory.WRITE_SPEED
     * <li>PassageFactory.SIZE
     * <li>PassageFactory.MIX
     * <li>PassageFactory.TALLY
     * </ul>
     * @param defaultType The new default type.
     */
    public static void setDefaultPassage(int defaultType)
    {
        PassageFactory.defaultType = defaultType;
    }

    /**
     * Get the default reference type.
     * @return default_type The new default type.
     * @see PassageFactory#setDefaultPassage(int)
     */
    public static int getDefaultPassage()
    {
        return defaultType;
    }

    /**
     * Create an empty Passage using the default type.
     * @return The new Passage
     */
    protected static Passage createPassage()
    {
        return createPassage(defaultType);
    }

    /**
     * Create an empty Passage using the default type. And set the
     * contents of the Passage using a string.
     * @param name The Passage description.
     * @return The new Passage
     * @throws NoSuchVerseException if the name is invalid
     */
    public static Passage createPassage(String name) throws NoSuchVerseException
    {
        if (name == null)
        {
            createPassage(defaultType);
        }

        return createPassage(defaultType, name);
    }

    /**
     * Create an empty Passage using a specified type.
     * @param type The type of Passage to create.
     * @return The new Passage
     * @see #setDefaultPassage(int)
     */
    private static Passage createPassage(int type)
    {
        switch (type)
        {
        case MIX:
            return new RangedPassage();

        case WRITE_SPEED:
            return new BitwisePassage();

        case SPEED:
            return new RocketPassage();

        case SIZE:
            return new DistinctPassage();

        case TALLY:
            return new PassageTally();

        default:
            throw new IllegalArgumentException(Integer.toString(type));
        }
    }

    /**
     * Create an empty Passage using a specified type. And set the
     * contents of the Passage using a string.
     * @param type The type of Passage to create.
     * @param name The Passage description.
     * @return The new Passage
     * @throws NoSuchVerseException if the name is invalid
     * @see #setDefaultPassage(int)
     */
    private static Passage createPassage(int type, String name) throws NoSuchVerseException
    {
        if (name == null)
        {
            createPassage(type);
        }

        switch (type)
        {
        case MIX:
            return new RangedPassage(name);

        case WRITE_SPEED:
            return new BitwisePassage(name);

        case SPEED:
            return new RocketPassage(name);

        case SIZE:
            return new DistinctPassage(name);

        case TALLY:
            return new PassageTally(name);

        default:
            throw new IllegalArgumentException(Integer.toString(type));
        }
    }

    /**
     * Create a Passage with all bits of the Bible set.
     * @return The new Passage
     */
    public static Passage getWholeBiblePassage()
    {
        try
        {
            if (whole == null)
            {
                whole = new ReadOnlyPassage(PassageFactory.createPassage("Gen 1:1-Rev 22:21"), true); //$NON-NLS-1$
            }

            return whole;
        }
        catch (Exception ex)
        {
            assert false : ex;
            return PassageFactory.createPassage();
        }
    }

    /**
     * Get a new Passage based on another Passage that synchronizes all access
     * to its members.
     * @param ref The passage to synchronize
     * @return A new synchronized passage that proxies requests to the original
     */
    public static Passage getSynchronizedPassage(Passage ref)
    {
        return new SynchronizedPassage(ref);
    }

    /**
     * Get a new Passage based on another Passage that synchronizes all access
     * to its members.
     * @param ref The passage to synchronize
     * @param ignore Do we throw up if someone tries to change us
     * @return A new synchronized passage that proxies requests to the original
     */
    public static Passage getReadOnlyPassage(Passage ref, boolean ignore)
    {
        return new ReadOnlyPassage(ref, ignore);
    }

    /**
     * The cached whole Bible passage
     */
    private static Passage whole;

    /**
     * The default type
     */
    private static int defaultType = SPEED;
}