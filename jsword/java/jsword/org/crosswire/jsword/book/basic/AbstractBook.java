package org.crosswire.jsword.book.basic;

import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.ClassUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.IndexFactory;
import org.crosswire.jsword.book.search.Matcher;
import org.crosswire.jsword.book.search.MatcherFactory;
import org.crosswire.jsword.book.search.Searcher;
import org.crosswire.jsword.book.search.SearcherFactory;
import org.crosswire.jsword.book.search.Thesaurus;
import org.crosswire.jsword.book.search.ThesaurusFactory;
import org.crosswire.jsword.passage.Key;

/**
 * AbstractBook implements a few of the more generic methods of Book.
 * This class does a lot of work in helping make search easier, and implementing
 * some basic write methods. 
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
public abstract class AbstractBook implements Book
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getBookMetaData()
     */
    public final BookMetaData getBookMetaData()
    {
        return bmd;
    }

    /**
     * @see org.crosswire.jsword.book.Book#getBookMetaData()
     */
    public final void setBookMetaData(BookMetaData bmd)
    {
        this.bmd = bmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public void activate(Lock lock)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public void deactivate(Lock lock)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Searcher#search(org.crosswire.jsword.book.Search)
     */
    public Key find(Search search) throws BookException
    {
        try
        {
            if (index == null)
            {
                index = IndexFactory.getIndexForBook(this);
            }

            if (thesaurus == null)
            {
                thesaurus = ThesaurusFactory.createThesaurus();
            }

            if (search.isBestMatch())
            {
                if (matcher == null)
                {
                    matcher = MatcherFactory.createMatcher(index, thesaurus);
                }

                return matcher.bestMatch(search.getMatch(), search.getRestriction());
            }
            else
            {
                if (searcher == null)
                {
                    searcher = SearcherFactory.createSearcher(index);
                }

                return searcher.search(search.getMatch(), search.getRestriction());
            }
        }
        catch (InstantiationException ex)
        {
            throw new BookException(Msg.INDEX_FAIL);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public final String toString()
    {
        return ClassUtil.getShortClassName(getClass()) + ":" + getBookMetaData().toString(); //$NON-NLS-1$
    }

    /**
     * The global thesaurus
     */
    private Thesaurus thesaurus;

    /**
     * The search index for this book
     */
    private Index index;

    /**
     * How do we perform best matches
     */
    private Matcher matcher;

    /**
     * How do we perform searches
     */
    private Searcher searcher;

    /**
     * The meta data for this book
     */
    private BookMetaData bmd;
}
