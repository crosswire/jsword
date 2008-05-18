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

import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.crosswire.jsword.book.Book;

/**
 * Base class for Analyzers. 
 * Note: All analyzers configured in AnalyzerFactory.properties should be of this type
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author sijo cherian [sijocherian at yahoo dot com]
 */
public abstract class AbstractBookAnalyzer extends Analyzer
{

    public AbstractBookAnalyzer()
    {
        this(null);
    }

    public AbstractBookAnalyzer(Book book)
    {
        this.book = book;
        doStopWords = false;
        doStemming = true;
        naturalLanguage = null;
    }

    /**
     * The book for which analysis is being performed.
     * @param newBook
     */
    public void setBook(Book newBook)
    {
        book = newBook;
    }

    /**
     * @return the book for which analysis is being performed.
     */
    public Book getBook()
    {
        return book;
    }

    public void setDoStopWords(boolean doIt)
    {
        doStopWords = doIt;
    }

    public boolean getDoStopWords()
    {
        return doStopWords;
    }

    public void setStopWords(String[] stopWords)
    {
        stopSet = StopFilter.makeStopSet(stopWords);
    }

    public void setDoStemming(boolean stemming)
    {
        doStemming = stemming;
    }

    public void setNaturalLanguage(String lang)
    {
        naturalLanguage = lang;
    }

    public String getNaturalLanguage()
    {
        return naturalLanguage;
    }

    /**
     * The book against which analysis is performed.
     */
    protected Book    book;

    protected Set     stopSet;

    // for turning on/off stop word removal during analysis
    protected boolean doStopWords;

    // for turning on/off stemming
    protected boolean doStemming;

    // Natural language of text that is being analyzed (optional parameter)
    protected String  naturalLanguage;

}
