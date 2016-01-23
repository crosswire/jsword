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
package org.crosswire.jsword.index.query;

import java.io.IOException;

import org.crosswire.common.util.PluginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Factory class for QueryBuilder.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public final class QueryBuilderFactory {
    /**
     * Prevent instantiation
     */
    private QueryBuilderFactory() {
    }

    /**
     * Create a new QueryBuilder.
     * 
     * @return the singleton query builder
     */
    public static QueryBuilder getQueryBuilder() {
        return instance;
    }

    /**
     * The singleton
     */
    private static QueryBuilder instance;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(QueryBuilderFactory.class);

    /**
     * Setup the instance
     */
    static {
        try {
            instance = PluginUtil.getImplementation(QueryBuilder.class);
        } catch (IOException e) {
            log.error("create QueryBuilder failed", e);
        } catch (ClassCastException e) {
            log.error("create QueryBuilder failed", e);
        } catch (ClassNotFoundException e) {
            log.error("create QueryBuilder failed", e);
        } catch (InstantiationException e) {
            log.error("create QueryBuilder failed", e);
        } catch (IllegalAccessException e) {
            log.error("create QueryBuilder failed", e);
        }
    }
}
