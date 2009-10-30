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
 * Copyright: 2009
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id:  $
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.ar.ArabicLetterTokenizer;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.ar.ArabicStemFilter;

/**
 * An Analyzer whose {@link TokenStream} is built from a
 * {@link ArabicLetterTokenizer} filtered with {@link LowerCaseFilter},
 * {@link ArabicNormalizationFilter}, {@link ArabicStemFilter} (optional) and
 * Arabic {@link StopFilter} (optional).
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ArabicLuceneAnalyzer extends AbstractBookAnalyzer {
    public ArabicLuceneAnalyzer() throws IOException {
        loadStopWords(ArabicAnalyzer.class, ArabicAnalyzer.DEFAULT_STOPWORD_FILE, ArabicAnalyzer.STOPWORDS_COMMENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String,
     * java.io.Reader)
     */
    public final TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream result = new ArabicLetterTokenizer(reader);
        result = new LowerCaseFilter(result);
        result = new ArabicNormalizationFilter(result);
        if (doStopWords && stopSet != null) {
            result = new StopFilter(false, result, stopSet);
        }

        if (doStemming) {
            result = new ArabicStemFilter(result);
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.lucene.analysis.Analyzer#reusableTokenStream(java.lang.String,
     * java.io.Reader)
     */
    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null) {
            streams = new SavedStreams();
            streams.setSource(new ArabicLetterTokenizer(reader));
            streams.setResult(new LowerCaseFilter(streams.getSource()));
            streams.setResult(new ArabicNormalizationFilter(streams.getResult()));
            if (doStopWords && stopSet != null) {
                streams.setResult(new StopFilter(false, streams.getResult(), stopSet));
            }

            if (doStemming) {
                streams.setResult(new ArabicStemFilter(streams.getResult()));
            }

            setPreviousTokenStream(streams);
        } else {
            streams.getSource().reset(reader);
        }
        return streams.getResult();
    }
}
