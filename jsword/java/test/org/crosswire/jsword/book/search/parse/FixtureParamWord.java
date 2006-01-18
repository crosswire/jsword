/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
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

import org.crosswire.common.util.MsgBase;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.passage.Key;

/**
 * A test SearchParamWord
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker
 */
class FixtureParamWord implements ParamWord
{
    /**
     * Setup the Passage to edit
     * @param ref The Passage to edit
     */
    public FixtureParamWord(String ref)
    {
        this.ref = ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getWord(org.crosswire.jsword.book.search.parse.Searcher)
     */
    public String getWord(IndexSearcher engine) throws BookException
    {
        throw new BookException(new MsgBase("Can't get a word from a startswith command"){}); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.ParamWord#getPassage(org.crosswire.jsword.book.search.parse.Searcher)
     */
    public Key getKeyList(IndexSearcher engine)
    {
        try
        {
            Index index = engine.getIndex();

            return index.getKey(ref);
        }
        catch (Exception ex)
        {
            assert false : ex;
            try
            {
                return engine.getIndex().find(null);
            }
            catch (BookException ex2)
            {
                assert false : ex2;
                return null;
            }
        }
    }

    /**
     * The editied up Passage
     */
    private String ref = null;
}