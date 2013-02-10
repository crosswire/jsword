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
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.index.lucene.IndexMetadata;
import org.crosswire.jsword.index.lucene.LuceneIndex;

/**
 * A specialized analyzer for Books that analyzes different fields differently.
 * This is book specific since it is possible that each book has specialized
 * search requirements.
 * 
 * Uses AnalyzerFactory for InstalledIndexVersion > 1.1
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class LuceneAnalyzer extends Analyzer {

    public LuceneAnalyzer(Book book) {
        // The default analysis
        analyzer = new PerFieldAnalyzerWrapper(new SimpleAnalyzer());

        if (IndexMetadata.instance().getInstalledIndexVersion() > IndexMetadata.INDEX_VERSION_1_1) {
            // Content is analyzed using natural language analyzer
            // (stemming, stopword etc)
            Analyzer myNaturalLanguageAnalyzer = AnalyzerFactory.getInstance().createAnalyzer(book);
            analyzer.addAnalyzer(LuceneIndex.FIELD_BODY, myNaturalLanguageAnalyzer);
        }

        // Keywords are normalized to osisIDs
        analyzer.addAnalyzer(LuceneIndex.FIELD_KEY, new KeyAnalyzer());

        // Strong's Numbers are normalized to a consistent representation
        analyzer.addAnalyzer(LuceneIndex.FIELD_STRONG, new StrongsNumberAnalyzer());

        // Strong's Numbers and Robinson's morphological codes are normalized to a consistent representation
        analyzer.addAnalyzer(LuceneIndex.FIELD_MORPHOLOGY, new MorphologyAnalyzer());

        // XRefs are normalized from ranges into a list of osisIDs
        analyzer.addAnalyzer(LuceneIndex.FIELD_XREF, new XRefAnalyzer());
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return analyzer.tokenStream(fieldName, reader);
    }

    private PerFieldAnalyzerWrapper analyzer;
}
