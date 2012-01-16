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
 * Copyright: 2012
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.versification.system;

import java.util.HashMap;
import java.util.Map;

import org.crosswire.jsword.versification.ReferenceSystem;

/**
 * The ReferenceSystems class manages the creation of ReferenceSystems as needed.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ReferenceSystems {
    
    public synchronized ReferenceSystem getReferenceSystem(String name) {
        if (rsMap.containsKey(name)) {
            return rsMap.get(name);
        }
        return null;
    }

    private ReferenceSystems() {
        
    }

    private Map<String, ReferenceSystem> rsMap = new HashMap<String, ReferenceSystem>();
}
