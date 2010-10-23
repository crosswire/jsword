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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.WordlistLoader;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.book.Book;

/**
 * Base class for Analyzers. Note: All analyzers configured in
 * AnalyzerFactory.properties should be of this type
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author sijo cherian [sijocherian at yahoo dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public abstract class AbstractBookAnalyzer extends Analyzer {

    public AbstractBookAnalyzer() {
        this(null);
    }

    public AbstractBookAnalyzer(Book book) {
        this.book = book;
        doStopWords = false;
        doStemming = true;
    }

    /**
     * The book for which analysis is being performed.
     * 
     * @param newBook
     */
    public void setBook(Book newBook) {
        book = newBook;
    }

    /**
     * @return the book for which analysis is being performed.
     */
    public Book getBook() {
        return book;
    }

    public void setDoStopWords(boolean doIt) {
        doStopWords = doIt;
    }

    public boolean getDoStopWords() {
        return doStopWords;
    }

    public void setStopWords(String[] stopWords) {
        stopSet = StopFilter.makeStopSet(stopWords);
    }

    /**
     * Load a stop word list as a resource. The list needs to be in a form
     * described by {@link org.apache.lucene.analysis.WordListLoader}.
     * 
     * @param clazz
     *            the class that owns the resource
     * @param resourceName
     *            the name of the resource
     * @param commentChar
     *            The comment character in the stop word file.
     * @throws IOException
     */
    public void loadStopWords(Class clazz, String resourceName, String commentChar) throws IOException {
        InputStream stream = null;
        InputStreamReader reader = null;
        try {
            stream = ResourceUtil.getResourceAsStream(clazz, resourceName);
            reader = new InputStreamReader(stream, "UTF-8"); //
            stopSet = WordlistLoader.getWordSet(reader, commentChar);
        } finally {
            if (reader != null) {
                reader.close();
            }

            if (stream != null) {
                stream.close();
            }
        }
    }

    public void setDoStemming(boolean stemming) {
        doStemming = stemming;
    }

    /**
     * The book against which analysis is performed.
     */
    protected Book book;

    protected Set stopSet;

    // for turning on/off stop word removal during analysis
    protected boolean doStopWords;

    // for turning on/off stemming
    protected boolean doStemming;
}
