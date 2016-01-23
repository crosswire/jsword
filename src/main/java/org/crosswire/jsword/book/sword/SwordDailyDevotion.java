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
package org.crosswire.jsword.book.sword;

import java.util.Date;

import org.crosswire.common.icu.DateFormatter;
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.PreferredKey;

/**
 * A book that has a preferred key of today's date.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class SwordDailyDevotion extends SwordDictionary implements PreferredKey {
    /**
     * Construct an SwordDailyDevotion given the BookMetaData and the AbstractBackend.
     * 
     * @param sbmd the metadata that describes the book
     * @param backend the means by which the resource is accessed
     */
    public SwordDailyDevotion(SwordBookMetaData sbmd, Backend backend) {
        super(sbmd, backend);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.PreferredKey#getPreferred()
     */
    public Key getPreferred() {
        return new DefaultLeafKeyList(DateFormatter.getDateInstance().format(new Date()));
    }
}
