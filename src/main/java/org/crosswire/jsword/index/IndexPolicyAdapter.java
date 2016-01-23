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
 * © CrossWire Bible Society, 2013 - 2016
 */
package org.crosswire.jsword.index;

/**
 * The IndexPolicyAdapter provides for application resilience against
 * change to IndexPolicy. It defines a reasonable set of defaults for
 * a desktop application, but may not be appropriate for memory limited
 * devices, such as phones, tablets, pdas. The defaults are documented
 * in {@link IndexPolicy}.
 * 
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class IndexPolicyAdapter implements IndexPolicy {

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexPolicy#isStrongsIndexed()
     */
    public boolean isStrongsIndexed() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexPolicy#isMorphIndexed()
     */
    public boolean isMorphIndexed() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexPolicy#isNoteIndexed()
     */
    public boolean isNoteIndexed() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexPolicy#isTitleIndexed()
     */
    public boolean isTitleIndexed() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexPolicy#isXrefIndexed()
     */
    public boolean isXrefIndexed() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexPolicy#getRAMBufferSize()
     */
    public int getRAMBufferSize() {
        return 16;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.index.IndexPolicy#isSerial()
     */
    public boolean isSerial() {
        return false;
    }

}
