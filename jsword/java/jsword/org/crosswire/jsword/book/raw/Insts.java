
package org.crosswire.jsword.book.raw;

import java.io.IOException;

import org.crosswire.jsword.passage.Verse;

/**
 * Insts is an interface that contains lists of numbers, generally
 * referring to members of an instance of an Items object.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public interface Insts
{
    /**
     * Retrieve an ordered list of the words in a Verse
     * @param verse The Verse to retrieve words for
     * @return An array of word indexes
     */
    public int[] getIndexes(Verse verse);

    /**
     * Retrieve an ordered list of the words in a Verse
     * @param ordinal The Verse to retrieve words for
     * @return An array of word indexes
     */
    public int[] getIndexes(int ordinal);

    /**
     * Set a list of word indexes as the test to a Verse
     * @param verse The Verse to set the words for
     * @param indexes The array of word indexes
     */
    public void setIndexes(int[] indexes, Verse verse);

    /**
     * Ensure that all changes to the index of words are written to disk
     */
    public void save() throws IOException;
}
