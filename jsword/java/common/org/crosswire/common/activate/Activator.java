package org.crosswire.common.activate;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Manager for instances of Activatable.
 * 
 * Activator should be used to manage all activate()ions and deactivate()ions
 * so that it can keep a track of exactly what is active and what can be
 * deactivate()d is save memory.
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
public class Activator
{
    /**
     * Prevent instansiation
     */
    private Activator()
    {
        // singleton - no set-up needed
    }

    /**
     * Check that a subject is activated and call activate() if not.
     * @param subject The thing to activate
     */
    public static void activate(Activatable subject)
    {
        if (!activated.contains(subject))
        {
            subject.activate(lock);
            activated.add(subject);
        }
    }

    /**
     * If we need to tighten things up a bit we can save memory with this
     */
    public static void reduceMemoryUsage(int amount)
    {
        switch (amount)
        {
        case KILL_EVERYTHING:
            for (Iterator it = activated.iterator(); it.hasNext();)
            {
                Activatable subject = (Activatable) it.next();
                deactivate(subject);
            }
            break;

        case KILL_LEAST_USED:
            // LATER(joe): implement
            throw new IllegalArgumentException("Not implemented");

        case KILL_ONLY_IF_TIGHT:
            // LATER(joe): implement
            throw new IllegalArgumentException("Not implemented");

        default:
            throw new IllegalArgumentException();
        }
    }

    /**
     * Try as hard as possible to conserve memory
     */
    public static final int KILL_EVERYTHING = 0;

    /**
     * Reduce memory usage, but only where sensible
     */
    public static final int KILL_LEAST_USED = 1;

    /**
     * Reduce memory usage, but only if we really need to
     */
    public static final int KILL_ONLY_IF_TIGHT = 2;

    /**
     * Deactivate an Activatable object.
     * It is safe to activate() something and then forget to deactivate() it
     * since we keep a track of activated objects and will automatically
     * deactivate() when needed, so this method should only be used when we are
     * sure that something will not be needed again.
     * @param subject The thing to de-activate
     */
    public static void deactivate(Activatable subject)
    {
        if (activated.contains(subject))
        {
            subject.deactivate(lock);
            activated.remove(subject);
        }
    }

    /**
     * The list of things that we have activated
     */
    private static Set activated = new HashSet();

    /**
     * The object we use to prevent others from
     */
    private static Lock lock = new Lock();
}
