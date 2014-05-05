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
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.index.lucene.IndexMetadata;
import org.crosswire.jsword.index.lucene.InstalledIndex;
import org.crosswire.jsword.index.lucene.LuceneIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A specialized analyzer for Books that analyzes different fields differently.
 * This is book specific since it is possible that each book has specialized
 * search requirements.
 * 
 * Uses AnalyzerFactory for InstalledIndexVersion > 1.1
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith
 */
//todo rename this to LuceneAnalyzerWrapper
public class LuceneAnalyzer  {

    public LuceneAnalyzer(Book book) {
        Map<String,Analyzer> analyzerPerField = new HashMap<String,Analyzer>();
        if (InstalledIndex.instance().getInstalledIndexDefaultVersion() > IndexMetadata.INDEX_VERSION_1_1) {
            // Content is analyzed using natural language analyzer
            // (stemming, stopword etc)
            Analyzer myNaturalLanguageAnalyzer = AnalyzerFactory.getInstance().createAnalyzer(book);
            analyzerPerField.put(LuceneIndex.FIELD_BODY, myNaturalLanguageAnalyzer);
            //todo analyzerPerField.put(LuceneIndex.FIELD_HEADING, myNaturalLanguageAnalyzer);  //heading to use same analyzer as BODY
            //analyzerPerField.put(LuceneIndex.FIELD_INTRO, myNaturalLanguageAnalyzer);
            log.debug(book.getBookMetaData().getInitials()+" Using languageAnalyzer: "+ myNaturalLanguageAnalyzer.getClass().getName());
        }

        // Keywords are normalized to osisIDs
        analyzerPerField.put(LuceneIndex.FIELD_KEY, new KeyAnalyzer());

        // Strong's Numbers are normalized to a consistent representation
        analyzerPerField.put(LuceneIndex.FIELD_STRONG, new StrongsNumberAnalyzer());

        // Strong's Numbers and Robinson's morphological codes are normalized to a consistent representation
        analyzerPerField.put(LuceneIndex.FIELD_MORPHOLOGY, new MorphologyAnalyzer());

        // XRefs are normalized from ranges into a list of osisIDs
        analyzerPerField.put(LuceneIndex.FIELD_XREF, new XRefAnalyzer());

        // SimpleAnalyzer: default analyzer
        analyzer = new PerFieldAnalyzerWrapper(new SimpleAnalyzer(IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING),
                analyzerPerField);



    }

    public Analyzer getBookAnalyzer() {
        return analyzer;
    }


    /*@Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return analyzer.tokenStream(fieldName, reader);
    }*/

    private PerFieldAnalyzerWrapper analyzer;
    private static final Logger log = LoggerFactory.getLogger(LuceneAnalyzer.class);

}
