
package org.crosswire.jsword.book.raw;

import java.util.Iterator;
import java.io.IOException;

/**
 * Items is a list of words, puncuation marks or other bits of data that
 * can be indexed by number. 
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
public interface Items
{
    /**
     * Get an Enumeration through the words
     * @return An Enumeration
     */
    public Iterator iterator();

    /**
     * Fetch an item from the dictionary by an id.
     * @param index The id of the word to fetch
     * @exception NoSuchWordException
     */
    public String getItem(int index) throws NoSuchResourceException;

    /**
     * This method is called during the creation of the index to add a
     * word to the index or to get a current id. If the IndexedResource
     * was created without create=true then we do not create a new id
     * we just return -1
     * @param data The word to find/create an id for
     * @return The (new) id for the item, or -1
     */
    public int getIndex(String data);

    /**
     * Set a list of word indexes as the test to a Verse
     * @param data The array of wordd to be indexed
     */
    public int[] getIndex(String[] data);

    /**
     * How many items are there in the current dictionary
     * @return the Item count
     */
    public int size();

    /**
     * Ensure that all changes to the index of words are written to disk
     */
    public void save() throws IOException;
}
