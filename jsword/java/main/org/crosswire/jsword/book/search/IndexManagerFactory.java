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
package org.crosswire.jsword.book.search;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.Logger;

/**
 * A Factory class for IndexManagers.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]

 */
public final class IndexManagerFactory
{
    /**
     * Prevent Instansiation
     */
    private IndexManagerFactory()
    {
    }

    /**
     * Create a new IndexManager.
     */
    public static IndexManager getIndexManager()
    {
        return instance;
    }

    /**
     * The singleton
     */
    private static IndexManager instance;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(IndexManagerFactory.class);

    /**
     * Setup the instance
     */
    static
    {
        try
        {
            Class impl = ClassUtil.getImplementor(IndexManager.class);
            instance = (IndexManager) impl.newInstance();
        }
        catch (Exception ex)
        {
            log.error("createIndexManager failed", ex); //$NON-NLS-1$
        }
    }
}
