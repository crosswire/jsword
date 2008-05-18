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
 * ID: $Id:  $
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.Reader;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Analyzer class to use as a Snowball Analyzer 
 * Default behavior: Stemming is done, Stop words not removed
 * A snowball stemmer can be configured by passing the stemmer name to setNaturalLanguage() method. 
 * Currently it takes following stemmer names (available stemmers in lucene snowball package net.sf.snowball.ext)
    Danish
    Dutch
    English
    Finnish
    French
    German2
    German
    Italian
    Kp
    Lovins
    Norwegian
    Porter
    Portuguese
    Russian
    Spanish
    Swedish

    This list is expected to expand, as and when Snowball project support more languages
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author sijo cherian [sijocherian at yahoo dot com]
 */
public class ConfigurableSnowballAnalyzer extends AbstractBookAnalyzer
{
    public ConfigurableSnowballAnalyzer()
    {
    }

    /**
     * Filters {@link StandardTokenizer} with {@link StandardFilter}, {@link
     * LowerCaseFilter}, {@link StopFilter} if enabled and
     * {@link SnowballFilter}.
     */
    public final TokenStream tokenStream(String fieldName, Reader reader)
    {
        TokenStream result = new StandardTokenizer(reader);
        result = new StandardFilter(result);
        result = new LowerCaseFilter(result);
        if (doStopWords && stopSet != null)
        {
            result = new StopFilter(result, stopSet);
        }

        // Configure Snowball filter based on language/stemmername
        if (doStemming)
        {
            result = new SnowballFilter(result, stemmerName);
        }

        return result;
    }

    public void setNaturalLanguage(String name)
    {
        naturalLanguage = name;
        // stemmer name are same as language name, in most cases
        stemmerName = name;

        // Check for allowed stemmers
        if (!allowedStemmers.matcher(stemmerName).matches())
        {
            throw new IllegalArgumentException("SnowballAnalyzer configured for unavailable stemmer " + stemmerName); //$NON-NLS-1$
        }

        // Initialize the default stop words
        if (defaultStopWordMap.containsKey(name))
        {
            stopSet = StopFilter.makeStopSet((String[]) defaultStopWordMap.get(name));
        }
    }

    private static Pattern allowedStemmers    = Pattern.compile("(Danish|Dutch|English|Finnish|French|German2|German|Italian|Kp|Lovins|Norwegian|Porter|Portuguese|Russian|Spanish|Swedish)"); //$NON-NLS-1$

    // Maps StemmerName > String array of standard stop words
    private static HashMap defaultStopWordMap = new HashMap();

    private String        stemmerName;

    static
    {
        defaultStopWordMap.put("French", FrenchAnalyzer.FRENCH_STOP_WORDS); //$NON-NLS-1$
        defaultStopWordMap.put("German", GermanAnalyzer.GERMAN_STOP_WORDS); //$NON-NLS-1$
        defaultStopWordMap.put("German2", GermanAnalyzer.GERMAN_STOP_WORDS); //$NON-NLS-1$
        defaultStopWordMap.put("Dutch", DutchAnalyzer.DUTCH_STOP_WORDS); //$NON-NLS-1$
        defaultStopWordMap.put("English", StopAnalyzer.ENGLISH_STOP_WORDS); //$NON-NLS-1$
        defaultStopWordMap.put("Porter", StopAnalyzer.ENGLISH_STOP_WORDS); //$NON-NLS-1$

    }
}
