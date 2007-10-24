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
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.de.GermanStemFilter;

/**
 * Based on Lucene's GermanAnalyzer
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public class GermanLuceneAnalyzer extends AbstractAnalyzer
{

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        TokenStream result = new LowerCaseTokenizer(reader);

        if (doStopWords && stopSet != null)
        {
            result = new StopFilter(result, stopSet);
        }

        if (doStemming)
        {
            result = new GermanStemFilter(result);
        }

        return result;
    }

    public GermanLuceneAnalyzer()
    {
        stopSet = StopFilter.makeStopSet(GermanAnalyzer.GERMAN_STOP_WORDS);
        setNaturalLanguage("German"); //$NON-NLS-1$
    }
}
