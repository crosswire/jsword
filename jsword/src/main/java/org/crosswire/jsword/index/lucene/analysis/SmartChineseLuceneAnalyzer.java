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

import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;

/**
 * A simple wrapper for {@link SmartChineseAnalyzer}, which takes overlapping
 * two character tokenization approach which leads to larger index size, like {@link CJKAnalyzer}.
 * This analyzer's stop list is merely of punctuation. It does stemming of English.
 *  
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SmartChineseLuceneAnalyzer extends AbstractBookAnalyzer
{
    public SmartChineseLuceneAnalyzer()
    {
        myAnalyzer = new SmartChineseAnalyzer();
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String, java.io.Reader)
     */
    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        return myAnalyzer.tokenStream(fieldName, reader);
    }

    private SmartChineseAnalyzer myAnalyzer;
}
