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
package org.crosswire.jsword.book;

import java.io.IOException;

import org.crosswire.common.util.PluginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Factory class for Bookmarks.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public final class BookmarkFactory {
    /**
     * Prevent instantiation
     */
    private BookmarkFactory() {
    }

    /**
     * Create a new Bookmark.
     * 
     * @return the singleton
     */
    public static Bookmark getBookmark() {
        return instance.clone();
    }

    /**
     * The singleton
     */
    private static Bookmark instance;

    /**
     * The log stream
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkFactory.class);

    /**
     * Setup the instance
     */
    static {
        try {
            instance = PluginUtil.getImplementation(Bookmark.class);
        } catch (IOException e) {
            LOGGER.error("createBookmark failed", e);
        } catch (ClassCastException e) {
            LOGGER.error("createBookmark failed", e);
        } catch (ClassNotFoundException e) {
            LOGGER.error("createBookmark failed", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("createBookmark failed", e);
        } catch (InstantiationException e) {
            LOGGER.error("createBookmark failed", e);
        }
    }
}
