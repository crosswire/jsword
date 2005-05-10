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
 * ID: $Id$
 */
package org.crosswire.jsword.book.search.parse;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;

/**
 * A ParamWord extends Word to provide more information
 * to a CommandWord. This will either be in the form of a String
 * or in the form of a Passage (from a search)
 * ParamWords are used by CommandWords that alter the final
 * Passage.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface ParamWord extends Word
{
    /**
     * Get a word for something else to word on.
     * @param engine The controller that can provide access to the search
     *               string or a default Bible.
     * @return The requested text
     * @exception BookException If this action is not appropriate
     */
    public String getWord(IndexSearcher engine) throws BookException;

    /**
     * Get a Passage or throw-up if that is not appropriate
     * for this Word.
     * @param engine The controller that can provide access to the search
     *               string or a default Bible.
     * @return A Passage relevant to this command
     * @exception BookException If this action is not appropriate
     */
    public Key getKeyList(IndexSearcher engine) throws BookException;
}
