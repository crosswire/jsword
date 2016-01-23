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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.common.config;

import java.util.Map;

/**
 * MappedChoice is simply a Choice where there are a number of alternative
 * options where each entry is a Map.Entry.
 * 
 * @param <K> the key's type
 * @param <V> the value's type
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public interface MappedChoice<K, V> extends Choice {
    /**
     * The available alternative values to be presented as options to the user
     * where the user interface allows presentation of alternatives.
     * @return A string array of alternatives.
     */
    Map<K, V> getOptions();
}
