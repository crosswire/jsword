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

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;

/**
 * Uses org.apache.lucene.analysis.cn.ChineseAnalyzer Analysis:
 * ChineseTokenizer, ChineseFilter StopFilter, Stemming not implemented yet
 * 
 * Note: org.apache.lucene.analysis.cn.CJKAnalyzer takes overlapping two
 * character tokenization approach which leads to larger index size.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Sijo Cherian
 */
public class ChineseLuceneAnalyzer extends AbstractBookAnalyzer {
    public ChineseLuceneAnalyzer() {
        myAnalyzer = new ChineseAnalyzer();
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String, java.io.Reader)
     */
    @Override
    public final TokenStream tokenStream(String fieldName, Reader reader) {
        return myAnalyzer.tokenStream(fieldName, reader);
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.Analyzer#reusableTokenStream(java.lang.String, java.io.Reader)
     */
    @Override
    public final TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        return myAnalyzer.reusableTokenStream(fieldName, reader);
    }

    private ChineseAnalyzer myAnalyzer;
}
