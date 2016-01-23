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
package org.crosswire.jsword.passage;

/**
 * A Factory for new Keys and KeyLists.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public interface KeyFactory {
    /**
     * Get a complete list of index entries. Create a Key that encompasses all
     * of the known valid keys for the given context. For a dictionary this will
     * include all of the entries in the dictionary, for a Bible this will
     * probably include all the verses in the Bible, but a commentary may well
     * miss some out.
     * 
     * @return A Key that includes all of the known Keys
     */
    Key getGlobalKeyList();

    /**
     * Get a Key for the name, if possible. Otherwise return an empty Key.
     * 
     * @param name
     * @return a valid key.
     */
    Key getValidKey(String name);

    /**
     * Someone has typed in a reference to find, but we need a Key to actually
     * look it up. So we create a Key from the string if such a translation is
     * possible. The returned Key may be a BranchKey if the string represents
     * more than one Key.
     * 
     * @param name
     *            The string to translate into a Key
     * @return The Key corresponding to the input text
     * @throws NoSuchKeyException
     *             If the name can not be parsed.
     */
    Key getKey(String name) throws NoSuchKeyException;

    /**
     * Fetch an empty Key to which we can add Keys. Not all implementations of
     * Key are able to hold any type of Key, It isn't reasonable to expect a Key
     * of Bible verses (=Passage) to hold a dictionary Key. So each KeyFactory
     * must be able to create you an empty Key to which you can safely add other
     * Keys it generates.
     * 
     * @return An empty Key that can hold other Keys from this factory.
     */
    Key createEmptyKeyList();
}
