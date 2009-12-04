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

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import junit.framework.TestCase;

/**
 * Test the English Analyzer
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public class EnglishLuceneAnalyzerTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        myAnalyzer = new EnglishLuceneAnalyzer();

        parser = new QueryParser(Version.LUCENE_29, field, myAnalyzer);

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDefaultBehavior() throws ParseException {
        String testInput = "Surely will every man walketh"; //$NON-NLS-1$
        Query query = parser.parse(testInput);

        // stemming on
        assertTrue(query.toString().indexOf(field + ":sure ") > -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field + ":everi") > -1); //$NON-NLS-1$    
    }

    public void testSetDoStopWords() throws ParseException {
        String testInput = "Surely will every man walketh"; //$NON-NLS-1$
        Query query = parser.parse(testInput);

        // enable stop word
        myAnalyzer.setDoStopWords(true);
        query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field + ":will") == -1); //$NON-NLS-1$

        // set custom stop word
        myAnalyzer.setDoStopWords(true);
        String[] stopWords = {
                "thy", "ye", "unto", "shalt"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        myAnalyzer.setStopWords(stopWords);
        testInput = "Upon thy belly Shalt thou go"; //$NON-NLS-1$
        query = parser.parse(testInput);
        // System.out.println("ParsedQuery- "+ query.toString());

        assertTrue(query.toString().indexOf(field + ":shalt") == -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field + ":thy") == -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field + ":upon") > -1); //$NON-NLS-1$

    }

    public void testSetDoStemming() throws ParseException {
        String testInput = "Surely will every man walketh"; //$NON-NLS-1$
        Query query = parser.parse(testInput);

        myAnalyzer.setDoStemming(false);
        query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field + ":surely") > -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field + ":every") > -1); //$NON-NLS-1$

    }

    protected static final String field = "content"; //$NON-NLS-1$
    private AbstractBookAnalyzer myAnalyzer;
    private QueryParser parser;
}
