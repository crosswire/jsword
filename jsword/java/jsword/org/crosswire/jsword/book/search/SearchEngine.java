
package org.crosswire.jsword.book.search;

import java.net.URL;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.passage.Passage;

/**
 * An interface that Bibles can use for help in becoming searchable.
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
public interface SearchEngine
{
    /**
     * An initializer type method so we can configure the Search engine at
     * runtime. This method is run first of all, before anything else and should
     * do everything it can to ensure that future method calls will be error
     * free without consuming significant system resources.
     */
    public void init(Bible bible, URL url) throws BookException;

    /**
     * Called after init() but before findPassage() to load indexes or otherwise
     * consume resources that we avoided consuming when init() was called.
     */
    public void activate();

    /**
     * Called after when the system is short of resources. Calling deactivate()
     * places the SearchEngine in a dormant state where it will not be used
     * again until activate() is called.
     */
    public void deactivate();

    /**
     * For a given word find a list of references to it
     * @param search The text to search for
     * @return The references to the word
     */
    public Passage findPassage(Search search) throws BookException;

    /**
     * Tidy up after yourself and remove all the files that make up any indexes
     * you created.
     */
    public void delete() throws BookException;
}
