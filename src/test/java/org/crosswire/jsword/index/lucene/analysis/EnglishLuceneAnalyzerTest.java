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

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the English Analyzer
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Sijo Cherian
 * @author DM Smith
 */
public class EnglishLuceneAnalyzerTest {

    @Before
    public void setUp() throws Exception {
        myAnalyzer = new EnglishLuceneAnalyzer();

        parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);
    }

    @Test
    public void testDefaultBehavior() throws ParseException {
        String testInput = "Surely will every man walketh";
        Query query = parser.parse(testInput);

        // stemming on
        Assert.assertTrue(query.toString().indexOf(FIELD + ":sure ") > -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":everi") > -1);
    }

    @Test
    public void testSetDoStopWords() throws ParseException {
        myAnalyzer = new EnglishLuceneAnalyzer();
        myAnalyzer.setDoStopWords(true);
        parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);
        String testInput = "Surely will every man walketh";
        Query query = parser.parse(testInput);

        // enable stop word
        Assert.assertTrue(query.toString().indexOf(FIELD + ":will") == -1);
    }

    @Test
    public void testCustomStopWords() throws Exception {
        myAnalyzer = new EnglishLuceneAnalyzer();
        // set custom stop word
        myAnalyzer.setDoStopWords(true);
        String[] stopWords = {
                "thy", "ye", "unto", "shalt"};
        myAnalyzer.setStopWords(new CharArraySet(Arrays.asList(stopWords), false));
        parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);
        String testInput = "Upon thy belly Shalt thou go";
        Query query = parser.parse(testInput);
        // System.out.println("ParsedQuery- "+ query.toString());

        Assert.assertTrue(query.toString().indexOf(FIELD + ":shalt") == -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":thy") == -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":upon") > -1);

    }

    @Test
    public void testSetDoStemming() throws ParseException {
        myAnalyzer = new EnglishLuceneAnalyzer();
        myAnalyzer.setDoStemming(false);
        parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);
        String testInput = "Surely will every man walketh";
        Query query = parser.parse(testInput);

        Assert.assertTrue(query.toString().indexOf(FIELD + ":surely") > -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":every") > -1);

    }

    protected static final String FIELD = "content";
    private AbstractBookAnalyzer myAnalyzer;
    private QueryParser parser;
}
