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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.Reader;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.el.GreekLowerCaseFilter;
import org.crosswire.jsword.index.lucene.IndexMetadata;

/**
 * Analyzer that removes the accents from the Hebrew text
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public class HebrewLuceneAnalyzer extends AbstractBookAnalyzer {
    public HebrewLuceneAnalyzer() {

    }

    /**
     * Creates a {@link TokenStream} which tokenizes all the text in the provided {@link Reader}.
     *
     * @return  A {@link TokenStream} built from a {@link StandardTokenizer} filtered with
     *                  {@link GreekLowerCaseFilter} and {@link StopFilter}
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new StandardTokenizer(matchVersion, reader);
        TokenStream result = source;
        result = new HebrewPointingFilter(result);

       return new TokenStreamComponents(source, result);
    }


    private final Version matchVersion = IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING;
}
