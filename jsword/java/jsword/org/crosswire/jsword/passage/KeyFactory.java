package org.crosswire.jsword.passage;

/**
 * A Factory for new Keys and KeyLists.
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
public interface KeyFactory
{
    /**
     * Get a complete list of index entries.
     * Create a Key that encompasses all of the known valid keys for the
     * given context. For a dictionary this will include all of the entries
     * in the dictionary, for a Bible this will probably include all the verses
     * in the Bible, but a commentary may well miss some out.
     * @return A Key that includes all of the known Keys
     */
    public Key getGlobalKeyList();

    /**
     * Someone has typed in a reference to find, but we need a Key to actually
     * look it up.
     * So we create a Key from the string if such a translation is possible.
     * The returned Key may be a BranchKey if the string represents more than
     * one Key.
     * @param name The string to translate into a Key
     * @return The Key corresponding to the input text
     * @throws NoSuchKeyException If the name can not be parsed.
     */
    public Key getKey(String name) throws NoSuchKeyException;

    /**
     * Fetch an empty Key to which we can add Keys.
     * Not all implementations of Key are able to hold any type of Key,
     * It isn't reasonable to expect a Key of Bible verses (=Passage) to
     * hold a dictionary Key. So each KeyFactory must be able to create you an
     * empty Key to which you can safely add other Keys it generates.
     * @return An empty Key that can hold other Keys from this factory.
     */
    public Key createEmptyKeyList();
}
