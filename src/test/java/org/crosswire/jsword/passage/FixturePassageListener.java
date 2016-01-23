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
package org.crosswire.jsword.passage;

/**
 * To help us test the VerseCollectionListener interface.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
class FixturePassageListener implements PassageListener {
    private int adds;
    private int removals;
    private int changes;

    FixturePassageListener() {
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
            throw new Exception("ADD: should have: " + addcheck + ", noted " + this.adds);
        }

        if (this.removals != removalcheck) {
            throw new Exception("REMOVALS: should have: " + removalcheck + ", noted " + this.removals);
        }

        if (this.changes != changecheck) {
            throw new Exception("CHANGES: should have: " + changecheck + ", noted " + this.changes);
        }

        return true;
    }
}
