
package org.crosswire.jsword.book.basic;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.events.ProgressListener;
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
            searcher = new SerSearcher(this, url, li);
        }
        catch (IOException ex)
        {
            throw new BookException("ser_init", ex);
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
    public Passage findPassage(String word) throws BookException
    {
        return searcher.findPassage(word);
    }

    /**
     * Retrieval: Return an array of words that are used by this Bible
     * that start with the given string. For example calling:
     * <code>getStartsWith("love")</code> will return something like:
     * { "love", "loves", "lover", "lovely", ... }
     * @param base The word to base your word array on
     * @return An array of words starting with the base
     */
    public Iterator getStartsWith(String word) throws BookException
    {
        return searcher.getStartsWith(word);
    }

    /**
     * The search implementation
     */
    protected Searcher searcher;
}
