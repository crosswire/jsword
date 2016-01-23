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
import org.junit.Test;

/**
 * Tokenization and query parsing test
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Sijo Cherian
 * @author DM Smith
 */
public class ChineseLuceneAnalyzerTest {

    @Test
    public void testTokenization() throws ParseException {
        myAnalyzer = new ChineseLuceneAnalyzer();
        parser = new QueryParser(Version.LUCENE_29, FIELD, myAnalyzer);

        String testInput = "\u795E\u7231\u4E16\u4EBA\uFF0C\u751A\u81F3\u628A\u4ED6\u7684\u72EC\u751F\u5B50\u8D50\u7ED9\u4ED6\u4EEC";

        Query query = parser.parse(testInput);
        Assert.assertTrue(query.toString().indexOf(FIELD + ":\"\u795E \u7231") > -1);
        Assert.assertTrue(query.toString().indexOf("\u4ED6 \u4EEC\"") > -1);
        // System.out.println(query.toString());
    }

    protected static final String FIELD = "content";
    private AbstractBookAnalyzer myAnalyzer;
    private QueryParser parser;
}
