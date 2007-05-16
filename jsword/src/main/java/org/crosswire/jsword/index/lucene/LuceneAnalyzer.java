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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id:LuceneIndex.java 984 2006-01-23 14:18:33 -0500 (Mon, 23 Jan 2006) dmsmith $
 */
package org.crosswire.jsword.index.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;

/**
 * A specialized analyzer for Books that analyzes different fields differently.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class LuceneAnalyzer extends Analyzer
{

    public LuceneAnalyzer()
    {
    }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        // do not tokenize keys
        if (LuceneIndex.FIELD_KEY.equals(fieldName))
        {
            return KEYWORD.tokenStream(fieldName, reader);
        }
        // Split Strong's Numbers on whitespace
        else if (LuceneIndex.FIELD_STRONG.equals(fieldName))
        {
            return WHITESPACE.tokenStream(fieldName, reader);
        }
        // Split xrefs's on whitespace
        else if (LuceneIndex.FIELD_XREF.equals(fieldName))
        {
            return WHITESPACE.tokenStream(fieldName, reader);
        }
        // just use the standard tokenizer
        else
        {
            return SIMPLE.tokenStream(fieldName, reader);
        }
    }

    private static final Analyzer KEYWORD = new KeywordAnalyzer();
    private static final Analyzer WHITESPACE = new WhitespaceAnalyzer();
    private static final Analyzer SIMPLE = new SimpleAnalyzer();
}
