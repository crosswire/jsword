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
 * This interface declares operations to be carried out after Raw Text
 *  has been read from a backend, before it is returned as OSIS to the caller.
 *
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public interface RawTextToXmlProcessor {
    /**
     * Runs before the processing starts.
     * 
     * @param partialDom the DOM, empty at this stage
     */
    void init(List<Content> partialDom);

    /**
     * Executes before a range is read from the raw data.
     * 
     * @param range the verse that is currently being examined
     * @param partialDom the DOM that is being built up as data is read
     */
    void preRange(VerseRange range, List<Content> partialDom);

    /**
     * Executes after a verse is read from the raw data.
     * 
     * @param verse the verse that is currently being examined
     * @param partialDom the DOM that is being built up as data is read
     * @param rawText the text that has been read, deciphered
     */
    void postVerse(Key verse, List<Content> partialDom, String rawText);
}
