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

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.crosswire.jsword.index.lucene.IndexMetadata;

/**
 * Simple Analyzer providing same function as
 * org.apache.lucene.analysis.SimpleAnalyzer This is intended to be the default
 * analyzer for natural language fields. Additionally performs: Normalize
 * Diacritics (Changes Accented characters to their unaccented equivalent) for
 * ISO 8859-1 languages
 * 
 * Note: Next Lucene release (beyond 2.2.0) will have a major performance
 * enhancement using method - public TokenStream reusableTokenStream(String
 * fieldName, Reader reader) We should use that. Ref:
 * https://issues.apache.org/jira/browse/LUCENE-969
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public class SimpleLuceneAnalyzer extends AbstractBookAnalyzer {

    public SimpleLuceneAnalyzer() {
        doStemming = false;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {

        Tokenizer source = new LowerCaseTokenizer(IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING, reader) ;
        TokenStream result = new ASCIIFoldingFilter(source);

        return new TokenStreamComponents(source, result);

    }
}
