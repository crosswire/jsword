
package org.crosswire.jsword.book.search;

import java.io.IOException;
import java.net.URL;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.ProgressListener;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.util.Project;

/**
 * The idea behind this class is to gradually abstract out the search from the
 * data retrieval so that we can re-use the search technology in other Books.
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
public abstract class SearchableBible extends AbstractBible
{
    /**
     * Set ourselves up for searching. This will mean one of 2 things - either
     * loading a known index, or generating one by reading the whole Bible.
     * @param li Optional progress listener if you think this might take ages.
     */
    public void init(ProgressListener li) throws BookException
    {
        try
        {
            URL url = getIndexDirectory();

            Class impl = Project.resource().getImplementor(org.crosswire.jsword.book.search.Searcher.class);
            searcher = (Searcher) impl.newInstance();
            searcher.init(this, url, li);
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.SEARCH_INIT, ex);
        }
    }

    /**
     * This allows our children to decide to store indexes in a different place
     * so they can ship indexes with distributed Books.
     * @return A file: URL of a place to store indexes. 
     */
    protected URL getIndexDirectory() throws IOException
    {
        return Project.resource().getTempScratchSpace(getBibleMetaData().getFullName());
    }

    /**
     * For a given word find a list of references to it
     * @param word The text to search for
     * @return The references to the word
     */
    public Passage findPassage(Search match) throws BookException
    {
        return searcher.findPassage(match);
    }

    /**
     * The search implementation
     */
    protected Searcher searcher;
}
