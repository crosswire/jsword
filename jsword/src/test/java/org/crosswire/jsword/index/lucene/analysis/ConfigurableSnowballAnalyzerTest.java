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
 * Snowball Analyzer test for stemming, stop word
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Sijo Cherian [sijocherian at yahoo dot com]
 */
public class ConfigurableSnowballAnalyzerTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
        myAnalyzer = new ConfigurableSnowballAnalyzer();
        parser = new QueryParser(field, myAnalyzer);
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testStemmers()
    {
        
        //valid
        myAnalyzer.setNaturalLanguage("Finnish"); //$NON-NLS-1$
        //invalid stemmer
        try {
            myAnalyzer.setNaturalLanguage("test"); //$NON-NLS-1$
        }
        catch (IllegalArgumentException e) {
            assertTrue (e.getMessage().indexOf("SnowballAnalyzer") > -1); //$NON-NLS-1$
        }
    }
    
    public void testStemming() throws ParseException
    {
        
        myAnalyzer.setNaturalLanguage("French"); //$NON-NLS-1$
        
        String testInput=" tant aim� le monde qu 'il a donn� son"; //$NON-NLS-1$
      
        
        Query query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field+":aim ") > -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field+":mond ") > -1); //$NON-NLS-1$
        //System.out.println(query.toString());        
    }
    
    public void testStopwords() throws ParseException
    {
        
        myAnalyzer.setNaturalLanguage("French"); //$NON-NLS-1$
        myAnalyzer.setDoStopWords(true);        
        String testInput=" tant aim� le monde qu 'il a donn� son"; //$NON-NLS-1$
              
        Query query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field+":le") == -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field+":a ") == -1); //$NON-NLS-1$
              
    }  

    public void testStemmingOff() throws ParseException
    {
        
        myAnalyzer.setNaturalLanguage("French"); //$NON-NLS-1$
        myAnalyzer.setDoStemming(false);
        
        String testInput=" tant aim� le monde qu 'il a donn� son"; //$NON-NLS-1$
      
        
        Query query = parser.parse(testInput);
        //System.out.println(query.toString());
        assertTrue(query.toString().indexOf(field+":aim� ") > -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field+":donn� ") > -1); //$NON-NLS-1$
    }   

    public void testStemmerConfig() throws ParseException
    {
        
        myAnalyzer.setNaturalLanguage("French"); //$NON-NLS-1$
        myAnalyzer.setDoStemming(false);
        
        String testInput=" tant aim� le monde qu 'il a donn� son"; //$NON-NLS-1$
      
        
        Query query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field+":aim� ") > -1); //$NON-NLS-1$
        assertTrue(query.toString().indexOf(field+":donn� ") > -1); //$NON-NLS-1$
              
    }    
   
    public void testMultipleStemmers() throws ParseException
    {
        
        myAnalyzer.setNaturalLanguage("German"); //$NON-NLS-1$
               
        String testInput="Denn also hat Gott die Welt geliebt, da� er seinen eingeborenen Sohn gab, auf da� jeder, der an ihn glaubt, nicht verloren gehe, sondern ewiges Leben habe"; //$NON-NLS-1$
              
        Query query = parser.parse(testInput);
        assertTrue(query.toString().indexOf(field+":denn ") > -1); //$NON-NLS-1$
        
        //System.out.println(query.toString());  
        
        //Compare with custom analyzer
        Analyzer anal= new GermanLuceneAnalyzer();
        QueryParser gparser = new QueryParser(field, anal);
        query = gparser.parse(testInput);
        assertTrue(query.toString().indexOf(field+":denn ") > -1); //$NON-NLS-1$
        
    }      
    protected static final String field = "content"; //$NON-NLS-1$
    private AbstractBookAnalyzer myAnalyzer;
    private QueryParser parser;    
}
