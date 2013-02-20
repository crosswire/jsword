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
 * Copyright: 2013
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */

package org.crosswire.jsword.book.sword;

import org.crosswire.jsword.book.BookException;

/**
 * Indicates that the files are missing, and therefore this book should be excluded
 */
public class MissingDataFilesException extends BookException {
    
    /**
     * Instantiates a new missing data files exception.
     *
     * @param msg the msg
     */
    public MissingDataFilesException(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = -130074367541462750L;
}
