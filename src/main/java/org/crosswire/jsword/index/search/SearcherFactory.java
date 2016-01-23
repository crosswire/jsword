/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.index.search;

import java.io.IOException;

import org.crosswire.common.util.PluginUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory method for creating a new Searcher.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class SearcherFactory {
    /**
     * Prevent instantiation
     */
    private SearcherFactory() {
    }

    /**
     * Create a new Searcher.
     * 
     * @param book the book
     * @return the searcher
     * @throws InstantiationException 
     */
    public static Searcher createSearcher(Book book) throws InstantiationException {
        try {
            IndexManager imanager = IndexManagerFactory.getIndexManager();
            Index index = imanager.getIndex(book);

            Searcher parser = PluginUtil.getImplementation(Searcher.class);
            parser.init(index);

            return parser;
        } catch (IOException e) {
            log.error("createSearcher failed", e);
            throw new InstantiationException();
        } catch (BookException e) {
            log.error("createSearcher failed", e);
            throw new InstantiationException();
        } catch (ClassCastException e) {
            log.error("createSearcher failed", e);
            throw new InstantiationException();
        } catch (ClassNotFoundException e) {
            log.error("createSearcher failed", e);
            throw new InstantiationException();
        } catch (IllegalAccessException e) {
            log.error("createSearcher failed", e);
            throw new InstantiationException();
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(SearcherFactory.class);
}
