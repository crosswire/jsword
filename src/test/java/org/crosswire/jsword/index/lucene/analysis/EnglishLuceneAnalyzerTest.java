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

import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.crosswire.jsword.index.lucene.IndexMetadata;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the English Analyzer
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 * @author DM Smith
 */
public class EnglishLuceneAnalyzerTest {

    @Before
    public void setUp() throws Exception {
        myAnalyzer = new EnglishLuceneAnalyzer();

        parser = new QueryParser(IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING, field, myAnalyzer);
    }

    @Test
    public void testDefaultBehavior() throws ParseException {
        String testInput = "Surely will every man walketh";
        Query query = parser.parse(testInput);

        // stemming on
        assertTrue(query.toString().indexOf(field + ":sure ") > -1);
        assertTrue(query.toString().indexOf(field + ":everi") > -1);    
    }

    @Test
    public void testSetDoStopWords() throws ParseException {
        myAnalyzer = new EnglishLuceneAnalyzer();
        myAnalyzer.setDoStopWords(true);
        parser = new QueryParser(IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING, field, myAnalyzer);
        String testInput = "Surely will every man walketh";
        Query query = parser.parse(testInput);

        // enable stop word
        assertTrue(query.toString().indexOf(field + ":will") == -1);
    }

    @Test
    public void testCustomStopWords() throws Exception {
        myAnalyzer = new EnglishLuceneAnalyzer();
        // set custom stop word
        myAnalyzer.setDoStopWords(true);
        String[] stopWords = {
                "thy", "ye", "unto", "shalt"};
        myAnalyzer.setStopWords(new CharArraySet(IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING, Arrays.asList(stopWords), false));
        parser = new QueryParser(IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING, field, myAnalyzer);
        String testInput = "Upon thy belly Shalt thou go";
        Query query = parser.parse(testInput);
        // System.out.println("ParsedQuery- "+ query.toString());

        assertTrue(query.toString().indexOf(field + ":shalt") == -1);
        assertTrue(query.toString().indexOf(field + ":thy") == -1);
        assertTrue(query.toString().indexOf(field + ":upon") > -1);

    }

    @Test
    public void testSetDoStemming() throws ParseException {
        myAnalyzer = new EnglishLuceneAnalyzer();
        myAnalyzer.setDoStemming(false);
        parser = new QueryParser(IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING, field, myAnalyzer);
        String testInput = "Surely will every man walketh";
        Query query = parser.parse(testInput);

        assertTrue(query.toString().indexOf(field + ":surely") > -1);
        assertTrue(query.toString().indexOf(field + ":every") > -1);

    }

    protected static final String field = "content";
    private AbstractBookAnalyzer myAnalyzer;
    private QueryParser parser;
}
