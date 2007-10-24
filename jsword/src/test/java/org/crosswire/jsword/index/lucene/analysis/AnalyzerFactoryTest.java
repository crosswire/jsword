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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

/**
 *
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author sijo cherian [sijocherian at yahoo dot com]
 */
public class AnalyzerFactoryTest extends TestCase
{

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Test method for {@link org.crosswire.jsword.index.lucene.analysis.AnalyzerFactory#createAnalyzer(java.lang.String)}.
     */
    public void testCreateAnalyzer()
    {
        Analyzer myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer(""); //$NON-NLS-1$
        assertTrue(myAnalyzer!=null);
        
        myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer(null);
        assertTrue(myAnalyzer!=null);
        myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer("Unknown"); //$NON-NLS-1$
        assertTrue(myAnalyzer!=null);
    }

    public void testEngStemming() throws ParseException
    {
        AbstractAnalyzer myAnalyzer = new EnglishLuceneAnalyzer();
        
        QueryParser parser = new QueryParser(field, myAnalyzer);
        
        String testInput = "Surely will every man walketh"; //$NON-NLS-1$
        Query query = parser.parse(testInput);
        //assertTrue(myAnalyzer instanceof SimpleLuceneAnalyzer);
        
        //After Diacritic filtering
        assertTrue(query.toString().indexOf(field+":sure ") > -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field+":everi") > -1); //$NON-NLS-1$
        
        myAnalyzer.setDoStemming(false);
        query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field+":surely") > -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field+":every") > -1); //$NON-NLS-1$

        //enable stop word
        myAnalyzer.setDoStopWords(true);
        query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field+":will") == -1); //$NON-NLS-1$

        //set custom stop word
        myAnalyzer.setDoStopWords(true);
        String[] stopWords = {"thy", "ye","unto","shalt"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        myAnalyzer.setStopWords(stopWords);
        testInput = "Upon thy belly Shalt thou go"; //$NON-NLS-1$
        query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field+":shalt") == -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field+":thy") == -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field+":upon") > -1); //$NON-NLS-1$


        System.out.println(query.toString());
    }
    
    public void testLatin1Language() throws ParseException {
        Analyzer myAnalyzer = AnalyzerFactory.getInstance().createAnalyzer("Latin"); //$NON-NLS-1$
        
        QueryParser parser = new QueryParser(field, myAnalyzer);
        
        String testInput = "test \u00D9\u00EB\u0153"; //$NON-NLS-1$
        assertTrue(myAnalyzer instanceof SimpleLuceneAnalyzer);
        Query query = parser.parse(testInput);
        //After Diacritic filtering
        assertTrue(query.toString().indexOf(field+":ueoe") > -1); //$NON-NLS-1$
        
        testInput = "A\u00C1"; //$NON-NLS-1$
        query = parser.parse(testInput);
        //After Diacritic filtering
        assertTrue(query.toString().indexOf(field+":aa") > -1); //$NON-NLS-1$
        

    }
    
    protected static final String field = "content"; //$NON-NLS-1$
}
