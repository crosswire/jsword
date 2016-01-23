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

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the Thai Analyzer
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Sijo Cherian
 * @author DM Smith
 */
public class ThaiLuceneAnalyzerTest {

    @Before
    public void setUp() throws Exception {
        myAnalyzer = new ThaiLuceneAnalyzer();

        parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);
    }

    @Test
    public void testDefaultBehavior() throws ParseException {
        String testInput = "\u0E1A\u0E38\u0E15\u0E23\u0E21\u0E19\u0E38\u0E29\u0E22\u0E4C\u0E08\u0E30\u0E15\u0E49\u0E2D";

        Query query = parser.parse(testInput);
        // System.out.println(query.toString());
        Assert.assertTrue(query.toString().indexOf(FIELD + ":\"\u0E1A\u0E38\u0E15\u0E23 \u0E21") > -1);
        Assert.assertTrue(query.toString().indexOf("\u0E4C \u0E08\u0E30 \u0E15\u0E49\u0E2D") > -1);
    }

    @Test
    public void testWhitespaceQuery() throws ParseException {
        // From john 3:3
        String testInput = "\u0E40\u0E23\u0E32\u0E1A\u0E2D\u0E01\u0E04\u0E27\u0E32\u0E21\u0E08\u0E23\u0E34\u0E07\u0E41\u0E01\u0E48\u0E17\u0E48\u0E32\u0E19\u0E27\u0E48\u0E32 \u0E16\u0E49\u0E32\u0E1C\u0E39\u0E49\u0E43\u0E14\u0E44\u0E21\u0E48\u0E44\u0E14\u0E49\u0E1A\u0E31\u0E07\u0E40\u0E01\u0E34\u0E14\u0E43\u0E2B\u0E21\u0E48";

        Query query = parser.parse(testInput);
        // System.out.println(query.toString());
        Assert.assertTrue(query.toString().indexOf(FIELD + ":\"\u0E40\u0E23\u0E32 \u0E1A") > -1);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":\"\u0E16\u0E49\u0E32 \u0E1C") > -1);
    }

    protected static final String FIELD = "content";
    private AbstractBookAnalyzer myAnalyzer;
    private QueryParser parser;
}
