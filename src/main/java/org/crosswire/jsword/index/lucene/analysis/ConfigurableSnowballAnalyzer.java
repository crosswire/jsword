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
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.index.lucene.IndexMetadata;

/**
 * An Analyzer whose {@link TokenStream} is built from a
 * {@link LowerCaseTokenizer} filtered with {@link SnowballFilter} (optional)
 * and {@link StopFilter} (optional) Default behavior: Stemming is done, Stop
 * words not removed A snowball stemmer is configured according to the language
 * of the Book. Currently it takes following stemmer names (available stemmers
 * in lucene snowball package net.sf.snowball.ext)
 * 
 * <pre>
 *     Danish
 *     Dutch
 *     English
 *     Finnish
 *     French
 *     German2
 *     German
 *     Italian
 *     Kp
 *     Lovins
 *     Norwegian
 *     Porter
 *     Portuguese
 *     Russian
 *     Spanish
 *     Swedish
 * </pre>
 * 
 * This list is expected to expand, as and when Snowball project support more
 * languages
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author sijo cherian
 */
final public class ConfigurableSnowballAnalyzer extends AbstractBookAnalyzer {
    public ConfigurableSnowballAnalyzer() {
    }

    /**
     * Filters {@link LowerCaseTokenizer} with {@link StopFilter} if enabled and
     * {@link SnowballFilter}.
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new LowerCaseTokenizer(matchVersion, reader) ;
        TokenStream result = source;

        if (doStopWords && stopSet != null) {
            result = new StopFilter(matchVersion, result, stopSet);
        }

        // Configure Snowball filter based on language/stemmerName
        if (doStemming) {
            result = new SnowballFilter(result, stemmerName);
        }


        return new TokenStreamComponents(source, result);

    }

    @Override
    public void setBook(Book newBook) {
        book = newBook;
        stemmerName = null;
        if (book != null) {
            // stemmer name are same as language name, in most cases
            pickStemmer(book.getLanguage().getCode());
        }
    }

    /**
     * Given the name of a stemmer, use that one.
     * 
     * @param languageCode
     */
    public void pickStemmer(String languageCode) {
        if (languageCode != null) {
            // Check for allowed stemmers
            if (languageCodeToStemmerLanguageNameMap.containsKey(languageCode)) {
                stemmerName = languageCodeToStemmerLanguageNameMap.get(languageCode);
            } else {
                throw new IllegalArgumentException("SnowballAnalyzer configured for unavailable stemmer " + stemmerName);
            }

            // Initialize the default stop words
            if (defaultStopWordMap.containsKey(languageCode)) {
                stopSet = defaultStopWordMap.get(languageCode);
            }
        }
    }

    /**
     * The name of the stemmer to use.
     */
    private String stemmerName;

    private static Map<String, String> languageCodeToStemmerLanguageNameMap = new HashMap<>();
    static {
        languageCodeToStemmerLanguageNameMap.put("da", "Danish");
        languageCodeToStemmerLanguageNameMap.put("nl", "Dutch");
        languageCodeToStemmerLanguageNameMap.put("en", "English");
        languageCodeToStemmerLanguageNameMap.put("fi", "Finnish");
        languageCodeToStemmerLanguageNameMap.put("fr", "French");
        languageCodeToStemmerLanguageNameMap.put("de", "German");
        languageCodeToStemmerLanguageNameMap.put("it", "Italian");
        languageCodeToStemmerLanguageNameMap.put("no", "Norwegian");
        languageCodeToStemmerLanguageNameMap.put("pt", "Portuguese");
        languageCodeToStemmerLanguageNameMap.put("ru", "Russian");
        languageCodeToStemmerLanguageNameMap.put("es", "Spanish");
        languageCodeToStemmerLanguageNameMap.put("sv", "Swedish");
    }

    // Maps StemmerName > String array of standard stop words
    private static HashMap<String, CharArraySet> defaultStopWordMap = new HashMap<>();
    static {
        defaultStopWordMap.put("fr", FrenchAnalyzer.getDefaultStopSet());
        defaultStopWordMap.put("de", GermanAnalyzer.getDefaultStopSet());
        defaultStopWordMap.put("nl", DutchAnalyzer.getDefaultStopSet());
        defaultStopWordMap.put("en", StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }

    private final Version matchVersion = IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING;
}
