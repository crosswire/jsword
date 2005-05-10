/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.jsword.book.raw;

import java.io.IOException;

import org.crosswire.jsword.passage.Verse;

/**
 * Insts is an interface that contains lists of numbers, generally
 * referring to members of an instance of an Items object.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
