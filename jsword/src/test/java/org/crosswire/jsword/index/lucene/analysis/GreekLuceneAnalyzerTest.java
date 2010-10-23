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

import junit.framework.TestCase;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

/**
 * Test the Greek Analyzer
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public class GreekLuceneAnalyzerTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        myAnalyzer = new GreekLuceneAnalyzer();

        parser = new QueryParser(Version.LUCENE_29, field, myAnalyzer);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testTokenization() throws ParseException {
        // From john 3:16

        String testInput = "\u0394\u03B9\u03BF\u03C4\u03B9 \u03C4\u03BF\u03C3\u03BF\u03BD \u03B7\u03B3\u03B1\u03C0\u03B7\u03C3\u03B5\u03BD \u03BF \u0398\u03B5\u03BF\u03C2 \u03C4\u03BF\u03BD \u03BA\u03BF\u03C3\u03BC\u03BF\u03BD\u002C \u03C9\u03C3\u03C4\u03B5 \u03B5\u03B4\u03C9\u03BA\u03B5 \u03C4\u03BF\u03BD \u03A5\u03B9\u03BF\u03BD \u03B1\u03C5\u03C4\u03BF\u03C5";
        Query query = parser.parse(testInput);
        // System.out.println(query.toString());
        // Lowercased test
        assertTrue(query.toString().indexOf(field + ":\u03B4\u03B9\u03BF\u03C4\u03B9 ") > -1);
        assertTrue(query.toString().indexOf(field + ":\u03B1\u03C5\u03C4\u03BF\u03C5") > -1);  

    }

    protected static final String field = "content";
    private AbstractBookAnalyzer myAnalyzer;
    private QueryParser parser;
}
