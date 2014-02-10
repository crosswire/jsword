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
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2007 - 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.index.lucene.analysis;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Test;

/**
 * 
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author sijo cherian [sijocherian at yahoo dot com]
 * @author DM Smith
 */
public class AnalyzerFactoryTest {

    /**
     * Test method for
     * {@link org.crosswire.jsword.index.lucene.analysis.AnalyzerFactory#createAnalyzer(java.lang.String)}
     * .
     */
    @Test
    public void testCreateAnalyzer() {
        Analyzer myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer(null);
        assertTrue(myAnalyzer != null);

        myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer(null);
        assertTrue(myAnalyzer != null);
    }

    @Test
    public void testCustomStopWordFiltering() throws ParseException {
        AbstractBookAnalyzer myAnalyzer = new EnglishLuceneAnalyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_29, field, myAnalyzer);

        // set custom stop word
        myAnalyzer.setDoStopWords(true);
        String[] stopWords = {
                "thy", "ye", "unto", "shalt"};
        myAnalyzer.setStopWords(new CharArraySet(Arrays.asList(stopWords), false));
        String testInput = "Upon thy belly Shalt thou go";

        Query query = parser.parse(testInput);

        assertTrue(query.toString().indexOf(field + ":shalt") == -1);
        assertTrue(query.toString().indexOf(field + ":thy") == -1);
        assertTrue(query.toString().indexOf(field + ":upon") > -1);
    }

    @Test
    public void testDiacriticFiltering() throws Exception {
        AbstractBookAnalyzer myAnalyzer = new EnglishLuceneAnalyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_29, field, myAnalyzer);
        String testInput = "Surely will every man walketh";

        Query query = parser.parse(testInput);

        assertTrue(query.toString().indexOf(field + ":sure ") > -1);
        assertTrue(query.toString().indexOf(field + ":everi") > -1);
    }

    @Test
    public void testStopWordsFiltering() throws Exception {
        AbstractBookAnalyzer myAnalyzer = new EnglishLuceneAnalyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_29, field, myAnalyzer);
        String testInput = "Surely will every man walketh";
        // enable stop words
        myAnalyzer.setDoStopWords(true);
        Query query = parser.parse(testInput);

        assertTrue(query.toString().indexOf(field + ":will") == -1);
    }

    @Test
    public void testWithStemmingDisabled() throws Exception {
        AbstractBookAnalyzer myAnalyzer = new EnglishLuceneAnalyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_29, field, myAnalyzer);
        String testInput = "Surely will every man walketh";
        myAnalyzer.setDoStemming(false);
        Query query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field + ":surely") > -1);
        assertTrue(query.toString().indexOf(field + ":every") > -1);
    }

    /*
     * public void testLatin1Language() throws ParseException { Analyzer
     * myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer("Latin");
     *
     * 
     * QueryParser parser = new QueryParser(field, myAnalyzer);
     * 
     * String testInput = "test \u00D9\u00EB\u0153";
     * assertTrue(myAnalyzer instanceof SimpleLuceneAnalyzer); Query query =
     * parser.parse(testInput); //After Diacritic filtering
     * assertTrue(query.toString().indexOf(field+":ueoe") > -1);
     * 
     * testInput = "A\u00C1"; query = parser.parse(testInput);
     * //After Diacritic filtering
     * assertTrue(query.toString().indexOf(field+":aa") > -1);
     * 
     * 
     * }
     */
    protected static final String field = "content";
}
