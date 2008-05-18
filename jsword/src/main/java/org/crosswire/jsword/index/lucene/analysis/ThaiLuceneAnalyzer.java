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

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.th.ThaiWordFilter;

/**
 * Tokenization using ThaiWordFilter. It uses java.text.BreakIterator to break words.
 * Stemming: Not implemented
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author sijo cherian [sijocherian at yahoo dot com]
 */
public class ThaiLuceneAnalyzer extends AbstractBookAnalyzer
{

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        TokenStream ts = new StandardTokenizer(reader);
        ts = new ThaiWordFilter(ts);
        if (doStopWords && stopSet != null)
        {
            ts = new StopFilter(ts, stopSet);
        }
        return ts;
    }

    public ThaiLuceneAnalyzer()
    {
        setNaturalLanguage("Thai"); //$NON-NLS-1$
    }
}
