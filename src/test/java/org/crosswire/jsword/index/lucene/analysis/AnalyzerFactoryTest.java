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

import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author sijo cherian
 * @author DM Smith
 */
public class AnalyzerFactoryTest {

    /**
     * Test method for
     * {@link org.crosswire.jsword.index.lucene.analysis.AnalyzerFactory#createAnalyzer(org.crosswire.jsword.book.Book)}
     * .
     */
    @Test
    public void testCreateAnalyzer() {
        Analyzer myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer(null);
        Assert.assertTrue(myAnalyzer != null);

        myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer(null);
        Assert.assertTrue(myAnalyzer != null);
    }

    @Test
    public void testCustomStopWordFiltering() throws ParseException {
        AbstractBookAnalyzer myAnalyzer = new EnglishLuceneAnalyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);

        // set custom stop word
        myAnalyzer.setDoStopWords(true);
        String[] stopWords = {
                "thy", "ye", "unto", "shalt"};
        myAnalyzer.setStopWords(new CharArraySet(Arrays.asList(stopWords), false));
        String testInput = "Upon thy belly Shalt thou go";

        Query query = parser.parse(testInput);

        Assert.assertTrue(query.toString().indexOf(FIELD + ":shalt") == -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":thy") == -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":upon") > -1);
    }

    @Test
    public void testDiacriticFiltering() throws Exception {
        AbstractBookAnalyzer myAnalyzer = new EnglishLuceneAnalyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);
        String testInput = "Surely will every man walketh";

        Query query = parser.parse(testInput);

        Assert.assertTrue(query.toString().indexOf(FIELD + ":sure ") > -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":everi") > -1);
    }

    @Test
    public void testStopWordsFiltering() throws Exception {
        AbstractBookAnalyzer myAnalyzer = new EnglishLuceneAnalyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);
        String testInput = "Surely will every man walketh";
        // enable stop words
        myAnalyzer.setDoStopWords(true);
        Query query = parser.parse(testInput);

        Assert.assertTrue(query.toString().indexOf(FIELD + ":will") == -1);
    }

    @Test
    public void testWithStemmingDisabled() throws Exception {
        AbstractBookAnalyzer myAnalyzer = new EnglishLuceneAnalyzer();
        QueryParser parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);
        String testInput = "Surely will every man walketh";
        myAnalyzer.setDoStemming(false);
        Query query = parser.parse(testInput);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":surely") > -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":every") > -1);
    }

    /*
     * public void testLatin1Language() throws ParseException { Analyzer
     * myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer("Latin");
     *
     * 
     * QueryParser parser = new QueryParser(field, myAnalyzer);
     * 
     * String testInput = "test \u00D9\u00EB\u0153";
     * Assert.assertTrue(myAnalyzer instanceof SimpleLuceneAnalyzer); Query query =
     * parser.parse(testInput); //After Diacritic filtering
     * Assert.assertTrue(query.toString().indexOf(field+":ueoe") > -1);
     * 
     * testInput = "A\u00C1"; query = parser.parse(testInput);
     * //After Diacritic filtering
     * Assert.assertTrue(query.toString().indexOf(field+":aa") > -1);
     * 
     * 
     * }
     */
    protected static final String FIELD = "content";
}
