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
package org.crosswire.jsword.book.search.parse;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;

/**
 * CommandWord extends Word to allow actions that alter a base
 * Passage. Implementations of this interface may use the Searcher to
 * get at a default Bible (or they may have one hard coded if necessary)
 * or to get at ParamWords that follow this command.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
*/
public interface CommandWord extends Word
{
    /**
     * Alter a Passage in whatever manner is appropriate for
     * this command.
     * For example the "~" command does something like this:
     * <code>ref.blur(engine.elements.next.getWord);</code>
     * The "&" command looks like this:
     * <code>ref.addAll(engine.elements.next.getPassage);</code>
     * @param engine The controller that can provide access to the search
     *               string or a default Bible.
     * @param ref The Passage to alter (if necessary)
     */
    public void updatePassage(IndexSearcher engine, Key ref) throws BookException;
}
