package org.crosswire.jsword.book.basic;

import java.net.URL;

import org.apache.commons.lang.ClassUtils;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.search.SearchEngine;
import org.crosswire.jsword.book.search.SearchEngineFactory;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.util.Project;

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
     * @see org.crosswire.jsword.book.Book#hasData(org.crosswire.jsword.passage.Key)
     */
    public final boolean hasData(Key key) throws BookException
    {
        BookData bdata = getData(key);
        
        // PENDING(joe): this should not be possible - add a bdata.isEmpty() method

        return bdata != null;
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

    /**
     * Called by our children if we need to initialise a SearchEngine
     */
    protected void initSearchEngine()
    {
        try
        {
            URL url = Project.instance().getTempScratchSpace("sword-"+getBookMetaData().getInitials());
            searcher = SearchEngineFactory.createSearchEngine(this, url);
        }
        catch (Exception ex)
        {
            searcher = null;
            Reporter.informUser(this, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#find(org.crosswire.jsword.book.Search)
     */
    public KeyList find(Search match) throws BookException
    {
        if (searcher != null)
        {
            return searcher.findPassage(match);
        }
        else
        {
            return new DefaultKeyList();
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public final String toString()
    {
        return ClassUtils.getShortClassName(getClass())+":"+getBookMetaData().toString();
    }

    /**
     * The search implementation
     */
    private SearchEngine searcher;

    /**
     * The meta data for this book
     */
    private BookMetaData bmd;
}
