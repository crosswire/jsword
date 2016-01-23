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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Snowball Analyzer test for stemming, stop word
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Sijo Cherian
 * @author DM Smith
 */
public class ConfigurableSnowballAnalyzerTest {

    @Before
    public void setUp() throws Exception {
        myAnalyzer = new ConfigurableSnowballAnalyzer();
        parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);
    }

    @Test
    public void testStemmers() {

        // valid
        myAnalyzer.pickStemmer("fi");
        // invalid stemmer
        try {
            myAnalyzer.pickStemmer("test");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().indexOf("SnowballAnalyzer") > -1);
        }
    }

    @Test
    public void testStemming() throws ParseException {

        myAnalyzer.pickStemmer("fr");

        String testInput = " tant aimé le monde qu'il a donné son";

        Query query = parser.parse(testInput);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":aim ") > -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":mond ") > -1);
        // System.out.println(query.toString());
    }

    @Test
    public void testStopwords() throws ParseException {

        myAnalyzer.pickStemmer("fr");
        myAnalyzer.setDoStopWords(true);
        String testInput = " tant aimé le monde qu 'il a donné son";

        Query query = parser.parse(testInput);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":le") == -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":a ") == -1);

    }

    @Test
    public void testStemmingOff() throws ParseException {

        myAnalyzer.pickStemmer("fr");
        myAnalyzer.setDoStemming(false);

        String testInput = " tant aimé le monde qu'il a donné son";

        Query query = parser.parse(testInput);
        // System.out.println(query.toString());
        Assert.assertTrue(query.toString().indexOf(FIELD + ":aimé ") > -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":donné ") > -1);
    }

    @Test
    public void testStemmerConfig() throws ParseException {

        myAnalyzer.pickStemmer("fr");
        myAnalyzer.setDoStemming(false);

        String testInput = " tant aimé le monde qu'il a donné son";

        Query query = parser.parse(testInput);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":aimé ") > -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":donné ") > -1);

    }

    @Test
    public void testMultipleStemmers() throws ParseException {

        myAnalyzer.pickStemmer("de");

        String testInput = "Denn also hat Gott die Welt geliebt, daß er seinen eingeborenen Sohn gab, auf daß jeder, der an ihn glaubt, nicht verloren gehe, sondern ewiges Leben habe";

        Query query = parser.parse(testInput);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":denn ") > -1);

        // System.out.println(query.toString());

        // Compare with custom analyzer
        Analyzer anal = new GermanLuceneAnalyzer();
        QueryParser gparser = new QueryParser(Version.LUCENE_29, FIELD, anal);
        query = gparser.parse(testInput);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":denn ") > -1);

    }

    protected static final String FIELD = "content";
    private ConfigurableSnowballAnalyzer myAnalyzer;
    private QueryParser parser;
}
