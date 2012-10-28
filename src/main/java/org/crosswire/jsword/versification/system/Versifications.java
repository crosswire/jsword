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
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.versification.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.crosswire.jsword.versification.Versification;

/**
 * The Versifications class manages the creation of Versifications as needed.
 * It delays the construction of the Versification until getVersification(String name) is called.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Versifications {

    /**
     * The default Versification for JSword is the KJV.
     * This is subject to change at any time.
     */
    public static final String DEFAULT_V11N = SystemKJV.V11N_NAME;

    /**
     * Get the singleton instance of Versifications.
     *
     * @return the singleton
     */
    public static Versifications instance() {
        return instance;
    }

    /**
     * Get the default Versification.
     *
     * @return the default Versification.
     */
    public synchronized Versification getDefaultVersification() {
        return getVersification(DEFAULT_V11N);
    }

    /**
     * Get the Versification by its name. If name is null then return the default Versification.
     *
     * @param name the name of the Versification
     * @return the Versification or null if it is not known.
     */
    public synchronized Versification getVersification(String name) {
        String actual = name;
        if (actual == null) {
            actual = DEFAULT_V11N;
        }

        // This class delays the building of a Versification to when it is
        // actually needed.
        Versification rs = fluffed.get(actual);
        if (rs == null) {
            rs = fluff(actual);
            if (rs != null) {
                fluffed.put(actual, rs);
            }
        }

        return rs;
    }

    /**
     * Determine whether the named Versification is known.
     *
     * @param name the name of the Versification
     * @return true when the Versification is available for use
     */
    public synchronized boolean isDefined(String name) {
        return name == null || known.contains(name);
    }

    private Versification fluff(String name) {
        //keep KJV at the top as it is the most common
        if (SystemKJV.V11N_NAME.equals(name)) {
            return new SystemKJV();
        }
        
        //then in alphabetical order, to ease the developer checking we have them all
        if (SystemCatholic.V11N_NAME.equals(name)) {
            return new SystemCatholic();
        }
        if (SystemCatholic2.V11N_NAME.equals(name)) {
            return new SystemCatholic2();
        }
        if (SystemGerman.V11N_NAME.equals(name)) {
            return new SystemGerman();
        }
        if (SystemKJVA.V11N_NAME.equals(name)) {
            return new SystemKJVA();
        }
        if (SystemLeningrad.V11N_NAME.equals(name)) {
            return new SystemLeningrad();
        }
        if (SystemLuther.V11N_NAME.equals(name)) {
            return new SystemLuther();
        }
        if (SystemMT.V11N_NAME.equals(name)) {
            return new SystemMT();
        }
        if (SystemNRSV.V11N_NAME.equals(name)) {
            return new SystemNRSV();
        }
        if (SystemNRSVA.V11N_NAME.equals(name)) {
            return new SystemNRSVA();
        }
        if (SystemSynodal.V11N_NAME.equals(name)) {
            return new SystemSynodal();
        }
        if (SystemSynodalP.V11N_NAME.equals(name)) {
            return new SystemSynodalP();
        }
        if (SystemVulg.V11N_NAME.equals(name)) {
            return new SystemVulg();
        }
        
        
        return null;
    }

    /**
     * Add a Versification that is not predefined by JSword.
     *
     * @param rs the Versification to register
     */
    public synchronized void register(Versification rs) {
        fluffed.put(rs.getName(), rs);
        known.add(rs.getName());
    }

    /**
     * This class is a singleton, enforced by a private constructor.
     */
    private Versifications() {
        known = new HashSet<String>();
        known.add(SystemCatholic.V11N_NAME);
        known.add(SystemCatholic2.V11N_NAME);
        known.add(SystemGerman.V11N_NAME);
        known.add(SystemKJV.V11N_NAME);
        known.add(SystemKJVA.V11N_NAME);
        known.add(SystemLeningrad.V11N_NAME);
        known.add(SystemLuther.V11N_NAME);
        known.add(SystemMT.V11N_NAME);
        known.add(SystemNRSV.V11N_NAME);
        known.add(SystemNRSVA.V11N_NAME);
        known.add(SystemSynodal.V11N_NAME);
        known.add(SystemSynodalP.V11N_NAME);
        known.add(SystemVulg.V11N_NAME);
        fluffed = new HashMap<String, Versification>();
    }

    /**
     * The set of v11n names.
     */
    private Set<String> known;

    /**
     * The map of instantiated Versifications, given by their names.
     */
    private Map<String, Versification> fluffed;

    private static final Versifications instance = new Versifications();
}
