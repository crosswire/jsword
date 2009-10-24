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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: KeyAnalyzer.java 1376 2007-06-01 18:27:01Z dmsmith $
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * English Analyzer works like lucene SimpleAnalyzer + Stemming. 
 * (LowerCaseTokenizer  > PorterStemFilter). 
 * Like the AbstractAnalyzer, {@link StopFilter} is off by default. 
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author sijo cherian [sijocherian at yahoo dot com]
 */
public class EnglishLuceneAnalyzer extends AbstractBookAnalyzer
{

    public EnglishLuceneAnalyzer()
    {
        stopSet = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }

    /**
     * Constructs a {@link LowerCaseTokenizer} filtered by a
     * language filter {@link StopFilter} and {@link PorterStemFilter} for English.
     */
    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        TokenStream result = new LowerCaseTokenizer(reader);

        if (doStopWords && stopSet != null)
        {
            result = new StopFilter(false, result, stopSet);
        }

        // Using Porter Stemmer
        if (doStemming)
        {
            result = new PorterStemFilter(result);
        }

        return result;
    }

}
