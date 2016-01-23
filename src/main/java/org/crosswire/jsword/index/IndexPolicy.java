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
package org.crosswire.jsword.index;

/**
 * An IndexPolicy allows for controlling the overall behavior
 * of Index creation. This class will change from time to time
 * so it is important to derive from IndexPolicyAdapter to
 * provide application resiliency.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public interface IndexPolicy {
    /**
     * Indicates whether Strong's Numbers should be included in the index.
     * Adapter default: true;
     * 
     * @return true if Strong's Numbers should be indexed
     */
    boolean isStrongsIndexed();

    /**
     * Indicates whether Strong's Numbers should be included in the index.
     * Adapter default: true;
     * 
     * @return true if Strong's Numbers should be indexed
     */
    boolean isMorphIndexed();

    /**
     * Indicates whether Strong's Numbers should be included in the index.
     * Adapter default: true;
     * 
     * @return true if Notes should be indexed
     */
    boolean isNoteIndexed();

    /**
     * Indicates whether Titles and Introductions should be included in the index.
     * Adapter default: true;
     * 
     * @return true if Titles and Introductions should be indexed
     */
    boolean isTitleIndexed();

    /**
     * Indicates whether Cross References should be included in the index.
     * Adapter default: true;
     * 
     * @return true if Cross References should be indexed
     */
    boolean isXrefIndexed();

    /**
     * The maximum size of the RAM Buffer in megabytes that
     * the underlying indexer may use during index creation.
     * Adapter default: 16.
     * 
     * @return the size of the RAM Buffer.
     */
    int getRAMBufferSize();

    /**
     * Indicates whether one Book can be indexed at a time.
     * When false, there is no upper bound on the number of
     * Books at any one time. Adapter default: false.
     * 
     * @return whether there can only be one Book indexed at a time.
     */
    boolean isSerial();
}
