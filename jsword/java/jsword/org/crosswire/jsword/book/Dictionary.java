
package org.crosswire.jsword.book;

import java.util.SortedSet;

/**
 * Dictionary is an interface for all the Lexicon/Dictionary type works.
 * 
 * We include in this category all works where there are a number of entries
 * retrieved using a word (traditional dictionary or lexicon) or number (like
 * Strongs indexing System).
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
public interface Dictionary extends Book
{
    /**
     * The meta data about this Book.
     * @return A Version for this Dictionary
     * @see Book#getBookMetaData()
     */
    public DictionaryMetaData getDictionaryMetaData();

    /**
     * Get a list of index entries. If key is null or blank then the entire
     * index is retrieved. An empty array is returned if no entries could be
     * found.
     * I considered an interface like: <code>String[] getIndex();</code> in
     * place of this on the assumption that accessing a string array would be
     * faster (true but probably not significant) that it was more typesafe
     * (true but is it that relevant?) and that it is easy to wrap a List around
     * a String (true). However it is not easy to do the startswith thing for a
     * String[] without lots of work so we would need to stick to getIndex()
     * which <i>might</i> be a pain (?).
     * @param startswith The text to base replies on.
     * @return String[]
     */
    public SortedSet getIndex(String startswith) throws BookException;
}
