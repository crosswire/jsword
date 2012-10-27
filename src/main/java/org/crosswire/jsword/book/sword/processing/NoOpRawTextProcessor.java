package org.crosswire.jsword.book.sword.processing;

import java.util.List;

import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom.Content;

public class NoOpRawTextProcessor implements RawTextToXmlProcessor {

    public void preRange(VerseRange range, List<Content> partialDom) {
        // No-Op
    }

    public void postVerse(Key verse, List<Content> partialDom, String rawText) {
        // No-Op
    }

    public void init(List<Content> partialDom) {
        // No-Op
    }
}
