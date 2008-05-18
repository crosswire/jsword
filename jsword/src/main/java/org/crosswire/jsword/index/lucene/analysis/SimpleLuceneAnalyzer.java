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
import java.util.regex.Pattern;

import org.apache.lucene.analysis.ISOLatin1AccentFilter;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;

/**
 * Simple Analyzer providing same function as org.apache.lucene.analysis.SimpleAnalyzer
 * This is intended to be the default analyzer for natural language fields.
 * Additionally performs:
 *   Normalize Diacritics (Changes Accented characters to their unaccented equivalent) for ISO 8859-1 languages
 *
 * Note: Next Lucene release (beyond 2.2.0) will have a major performance enhancement using method -
 *      public TokenStream reusableTokenStream(String fieldName, Reader reader)
 *      We should use that.
 *    Ref: https://issues.apache.org/jira/browse/LUCENE-969 
 *     
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public class SimpleLuceneAnalyzer extends AbstractBookAnalyzer
{

    public SimpleLuceneAnalyzer()
    {
        doStemming = false;
    }

    public TokenStream tokenStream(String fieldName, Reader reader)
    {

        TokenStream result = new LowerCaseTokenizer(reader);

        if (naturalLanguage != null && isoLatin1Langs.matcher(naturalLanguage).matches())
        {
            result = new ISOLatin1AccentFilter(result);
        }

        return result;
    }

    private static Pattern isoLatin1Langs = Pattern.compile("(Afrikaans|Albanian|Basque|Breton|Catalan|Danish|Dutch|English|Estonian|Faroese|French|Finnish|Galician|German|Icelandic|Irish|Italian|Latin|Luxembourgish|Norwegian|Occitan|Portuguese|Romansh|Scottish Gaelic|Spanish|Swahili|Swedish|Walloon)"); //$NON-NLS-1$
}
