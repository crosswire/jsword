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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
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

    /**
     * Constructs a {@link LowerCaseTokenizer} filtered by a language filter
     * {@link StopFilter} and {@link PorterStemFilter} for English.
     */

    /*
    @Override
    public final TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream result = new LowerCaseTokenizer(reader);

        if (doStopWords && stopSet != null) {
            result = new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(matchVersion), result, stopSet);
        }

        // Using Porter Stemmer
        if (doStemming) {
            result = new PorterStemFilter(result);
        }

        return result;
    }*/

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.Analyzer#reusableTokenStream(java.lang.String, java.io.Reader)
     */

    /*public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null) {
            streams = new SavedStreams(new LowerCaseTokenizer(reader));
            if (doStopWords && stopSet != null) {
                streams.setResult(new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(matchVersion), streams.getResult(), stopSet));
            }

            if (doStemming) {
                streams.setResult(new PorterStemFilter(streams.getResult()));
            }

            setPreviousTokenStream(streams);
        } else {
            streams.getSource().reset(reader);
        }
        return streams.getResult();
    }*/

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
