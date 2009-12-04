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
 * Test the Thai Analyzer
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public class ThaiLuceneAnalyzerTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        myAnalyzer = new ThaiLuceneAnalyzer();

        parser = new QueryParser(Version.LUCENE_29, field, myAnalyzer);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDefaultBehavior() throws ParseException {
        String testInput = "\u0E1A\u0E38\u0E15\u0E23\u0E21\u0E19\u0E38\u0E29\u0E22\u0E4C\u0E08\u0E30\u0E15\u0E49\u0E2D"; //$NON-NLS-1$

        Query query = parser.parse(testInput);
        // System.out.println(query.toString());
        assertTrue(query.toString().indexOf(field + ":\"\u0E1A\u0E38\u0E15\u0E23 \u0E21") > -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf("\u0E4C \u0E08\u0E30 \u0E15\u0E49\u0E2D") > -1); //$NON-NLS-1$ 
    }

    public void testWhitespaceQuery() throws ParseException {
        // From john 3:3
        String testInput = "\u0E40\u0E23\u0E32\u0E1A\u0E2D\u0E01\u0E04\u0E27\u0E32\u0E21\u0E08\u0E23\u0E34\u0E07\u0E41\u0E01\u0E48\u0E17\u0E48\u0E32\u0E19\u0E27\u0E48\u0E32 \u0E16\u0E49\u0E32\u0E1C\u0E39\u0E49\u0E43\u0E14\u0E44\u0E21\u0E48\u0E44\u0E14\u0E49\u0E1A\u0E31\u0E07\u0E40\u0E01\u0E34\u0E14\u0E43\u0E2B\u0E21\u0E48"; //$NON-NLS-1$

        Query query = parser.parse(testInput);
        // System.out.println(query.toString());
        assertTrue(query.toString().indexOf(field + ":\"\u0E40\u0E23\u0E32 \u0E1A") > -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field + ":\"\u0E16\u0E49\u0E32 \u0E1C") > -1); //$NON-NLS-1$ 
    }

    protected static final String field = "content"; //$NON-NLS-1$
    private AbstractBookAnalyzer myAnalyzer;
    private QueryParser parser;
}
