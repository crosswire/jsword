package org.crosswire.jsword.book;

import org.crosswire.common.activate.Activatable;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.KeyList;

/**
 * Book is the most basic store of textual data - It can retrieve data
 * either as an XML document or as plain text - It uses Keys to refer
 * to parts of itself, and can search for words (returning Keys).
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
public interface Book extends Activatable, KeyFactory
{
    /**
     * Meta-Information: What version of the Bible is this?
     * @return A Version for this Bible
     */
    public BookMetaData getBookMetaData();

    /**
     * Retrieval: Add to the given document some mark-up for the specified
     * Verses.
     * @param ref The verses to search for
     * @return The found Book data
     * @throws BookException If anything goes wrong with this method
     */
    public BookData getData(Key ref) throws BookException;

    /**
     * Does the specified verse have any data behind it.
     * @param verse The verse to query
     * @return The found BibleData document
     * @throws BookException If anything goes wrong with this method
     */
    public boolean hasData(Key key) throws BookException;

    /**
     * Retrieval: For a given search spec find a list of references to it.
     * If there are no matches then null should be returned, otherwise a valid
     * Key.
     * @param search The search spec.
     * @throws BookException If anything goes wrong with this method
     */
    public KeyList find(Search search) throws BookException;
}
