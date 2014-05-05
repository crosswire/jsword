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

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.crosswire.jsword.index.lucene.IndexMetadata;
import org.junit.Test;

/**
 * Tokenization and query parsing test  : todo tests SmartChineseLuceneAnalyzer
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 * @author DM Smith
 */
public class ChineseLuceneAnalyzerTest {

    @Test
    public void testTokenization() throws ParseException {
        myAnalyzer = new SmartChineseLuceneAnalyzer();
        parser = new QueryParser(IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING, field, myAnalyzer);

        String testInput = "\u795E\u7231\u4E16\u4EBA\uFF0C\u751A\u81F3\u628A\u4ED6\u7684\u72EC\u751F\u5B50\u8D50\u7ED9\u4ED6\u4EEC";

        Query query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field + ":\"\u795E \u7231") > -1);
        assertTrue(query.toString().indexOf("\u4ED6 \u4EEC\"") > -1);
        // System.out.println(query.toString());
    }

    protected static final String field = "content";
    private AbstractBookAnalyzer myAnalyzer;
    private QueryParser parser;
}
