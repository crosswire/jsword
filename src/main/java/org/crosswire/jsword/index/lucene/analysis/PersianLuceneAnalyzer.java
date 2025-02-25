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
 * © CrossWire Bible Society, 2009 - 2016
 *
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicLetterTokenizer;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fa.PersianNormalizationFilter;
import org.apache.lucene.util.Version;
import org.crosswire.jsword.index.lucene.IndexMetadata;

/**
 * An Analyzer whose {@link TokenStream} is built from a
 * {@link ArabicLetterTokenizer} filtered with {@link LowerCaseFilter},
 * {@link ArabicNormalizationFilter}, {@link PersianNormalizationFilter} and
 * Persian {@link StopFilter} (optional)
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
final public class PersianLuceneAnalyzer extends AbstractBookAnalyzer {
    public PersianLuceneAnalyzer() {
        stopSet = PersianAnalyzer.getDefaultStopSet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String,
     * java.io.Reader)
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new ArabicLetterTokenizer(matchVersion,reader);
        TokenStream result = new LowerCaseFilter(matchVersion, source);
        result = new ArabicNormalizationFilter(result);
        /* additional persian-specific normalization */
        result = new PersianNormalizationFilter(result);
        /*
         * the order here is important: the stop set is normalized with the
         * above!
         */
        if (doStopWords && stopSet != null) {
            result = new StopFilter((matchVersion), result, stopSet);
        }


        return new TokenStreamComponents(source, result);

    }

    private final Version matchVersion = IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING;
}
