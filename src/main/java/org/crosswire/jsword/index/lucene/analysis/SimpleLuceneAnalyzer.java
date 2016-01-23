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

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;

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
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Sijo Cherian
 */
public class SimpleLuceneAnalyzer extends AbstractBookAnalyzer {

    public SimpleLuceneAnalyzer() {
        doStemming = false;
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream result = new LowerCaseTokenizer(reader);
        result = new ASCIIFoldingFilter(result);
        return result;
    }
}
