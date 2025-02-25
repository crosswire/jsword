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
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.util.Version;
import org.crosswire.jsword.index.lucene.IndexMetadata;

/**
 * English Analyzer works like lucene SimpleAnalyzer + Stemming.
 * (LowerCaseTokenizer &gt; PorterStemFilter). Like the AbstractAnalyzer,
 * {@link StopFilter} is off by default.
 * 
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author sijo cherian
 */
final public class EnglishLuceneAnalyzer extends AbstractBookAnalyzer {

    public EnglishLuceneAnalyzer() {
        stopSet = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {

        Tokenizer source = new LowerCaseTokenizer(matchVersion, reader) ;
        TokenStream result = source;

        if (doStopWords && stopSet != null) {
            //result = new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(matchVersion), result, stopSet);
            result = new StopFilter(matchVersion, source,stopSet) ;
        }

        // Using Porter Stemmer
        if (doStemming) {
            result = new PorterStemFilter(result);
        }
        return new TokenStreamComponents(source, result);
    }

    private final Version matchVersion = IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING;
}
