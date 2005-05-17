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
import org.crosswire.jsword.passage.RestrictionType;

/**
 * Alter the Passage by calling blur with a
 * number grabbed from the next word in the search string.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BlurCommandWord implements CommandWord
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.CommandWord#updatePassage(org.crosswire.jsword.book.search.parse.Searcher, org.crosswire.jsword.passage.Passage)
     */
    public void updatePassage(IndexSearcher engine, Key key) throws BookException
    {
        String word = engine.iterateWord();

        try
        {
            key.blur(Integer.parseInt(word), RestrictionType.getDefaultBlurRestriction());
        }
        catch (NumberFormatException ex)
        {
            throw new BookException(Msg.BLUR_FORMAT, ex, new Object[] { word });
        }
    }
}
