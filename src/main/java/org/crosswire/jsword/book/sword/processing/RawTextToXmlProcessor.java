package org.crosswire.jsword.book.sword.processing;

import java.util.List;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom.Content;

/**
 * This interface declares operations to be carried out after Raw Text has been read from a backend, before it is returned as OSIS to the caller
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public interface RawTextToXmlProcessor {
    /**
     * Executes before a range is read from the raw data
     * @param range the verse that is currently being examined
     * @param partialDom the DOM that is being built up as data is read
     */
    void preRange(VerseRange range, List<Content> partialDom);

    /**
     * Executes after a verse is read from the raw data
     * @param verse the verse that is currently being examined
     * @param partialDom the DOM that is being built up as data is read
     * @param rawText the text that has been read, deciphered
     */
    void postVerse(Key verse, List<Content> partialDom, String rawText);
}
