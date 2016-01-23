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
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.activate;

import java.util.HashSet;
import java.util.Set;

/**
 * Manager for instances of Activatable.
 * 
 * Activator should be used to manage all activate()ions and deactivate()ions so
 * that it can keep a track of exactly what is active and what can be
 * deactivated is save memory.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class Activator {
    /**
     * Prevent instantiation
     */
    private Activator() {
        // singleton - no set-up needed
    }

    /**
     * Check that a subject is activated and call activate() if not.
     * 
     * @param subject
     *            The thing to activate
     */
    public static void activate(Activatable subject) {
        if (!activated.contains(subject) && subject != null) {
            subject.activate(lock);
            activated.add(subject);
        }
    }

    /**
     * If we need to tighten things up a bit we can save memory with this
     * 
     * @param amount the amount by which to to reduce memory
     */
    public static void reduceMemoryUsage(Kill amount) {
        amount.reduceMemoryUsage();
    }

    /**
     * Deactivate an Activatable object. It is safe to activate() something and
     * then forget to deactivate() it since we keep a track of activated objects
     * and will automatically deactivate() when needed, so this method should
     * only be used when we are sure that something will not be needed again.
     * 
     * @param subject
     *            The thing to deactivate
     */
    public static void deactivate(Activatable subject) {
        if (activated.contains(subject) && subject != null) {
            subject.deactivate(lock);
            activated.remove(subject);
        }
    }

    public static void deactivateAll() {
        for (Activatable item : activated) {
            deactivate(item);
        }
    }

    /**
     * The list of things that we have activated
     */
    private static Set<Activatable> activated = new HashSet<Activatable>();

    /**
     * The object we use to prevent others from
     */
    private static Lock lock = new Lock();
}
