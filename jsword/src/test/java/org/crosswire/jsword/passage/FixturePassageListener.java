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
package org.crosswire.jsword.passage;

/**
 * To help us test the VerseCollectionListener interface.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class FixturePassageListener implements PassageListener {
    public int adds = 0;
    public int removals = 0;
    public int changes = 0;

    public FixturePassageListener() {
    }

    public void versesAdded(PassageEvent ev) {
        adds++;
    }

    public void versesRemoved(PassageEvent ev) {
        removals++;
    }

    public void versesChanged(PassageEvent ev) {
        changes++;
    }

    public boolean check(int addcheck, int removalcheck, int changecheck) throws Exception {
        if (this.adds != addcheck) {
            throw new Exception("ADD: should have: " + addcheck + ", noted " + this.adds); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (this.removals != removalcheck) {
            throw new Exception("REMOVALS: should have: " + removalcheck + ", noted " + this.removals); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (this.changes != changecheck) {
            throw new Exception("CHANGES: should have: " + changecheck + ", noted " + this.changes); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return true;
    }
}
