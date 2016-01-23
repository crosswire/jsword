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
 * © CrossWire Bible Society, 2012 - 2016
 *
 */
package org.crosswire.jsword.book.sword.processing;

import java.util.List;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom2.Content;

/**
 * A processor that does absolutely nothing.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class NoOpRawTextProcessor implements RawTextToXmlProcessor {

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor#preRange(org.crosswire.jsword.passage.VerseRange, java.util.List)
     */
    public void preRange(VerseRange range, List<Content> partialDom) {
        // No-Op
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor#postVerse(org.crosswire.jsword.passage.Key, java.util.List, java.lang.String)
     */
    public void postVerse(Key verse, List<Content> partialDom, String rawText) {
        // No-Op
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor#init(java.util.List)
     */
    public void init(List<Content> partialDom) {
        // No-Op
    }
}
