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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.jsword.index.lucene;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.VerseFactory;

/**
 * A simple collector of verses that stores the verses in a Key.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class VerseCollector extends Collector {

    /**
     * Create a collector for the searcher that populates results.
     */
    public VerseCollector(Searcher searcher, Key results) {
        this.searcher = searcher;
        this.results = results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.search.Collector#acceptsDocsOutOfOrder()
     */
    public boolean acceptsDocsOutOfOrder() {
        // Order is unimportant
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.search.Collector#collect(int)
     */
    public void collect(int docId) throws IOException {
        Document doc = searcher.doc(docBase + docId);
        try {
            Key key = VerseFactory.fromString(doc.get(LuceneIndex.FIELD_KEY));
            results.addAll(key);
        } catch (NoSuchVerseException e) {
            // Wrap the NoSuchVerseException in an IOException so it can be
            // gotten.
            throw new IOException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.lucene.search.Collector#setNextReader(org.apache.lucene.index
     * .IndexReader, int)
     */
    public void setNextReader(IndexReader reader, int docBase) throws IOException {
        this.docBase = docBase;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.lucene.search.Collector#setScorer(org.apache.lucene.search
     * .Scorer)
     */
    public void setScorer(Scorer scorer) throws IOException {
        // This collector does no scoring. It collects all hits.
    }

    private int docBase;
    private Searcher searcher;
    private Key results;
}
