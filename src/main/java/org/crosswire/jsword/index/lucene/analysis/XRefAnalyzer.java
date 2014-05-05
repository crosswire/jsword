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

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.index.lucene.IndexMetadata;

/**
 * A specialized analyzer that normalizes Cross References.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
final public class XRefAnalyzer extends AbstractBookAnalyzer {
    /**
     * Construct a default XRefAnalyzer.
     */
    public XRefAnalyzer() {
    }

    /**
     * Construct an XRefAnalyzer tied to a book.
     * 
     * @param book the book
     */
    public XRefAnalyzer(Book book) {
        setBook(book);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.Analyzer#tokenStream(java.lang.String,
     * java.io.Reader)
     */
    @Override

    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        //return new KeyFilter(getBook(), new WhitespaceTokenizer(reader));

        Tokenizer source = new WhitespaceTokenizer(IndexMetadata.LUCENE_IDXVERSION_FOR_INDEXING, reader) ;
        TokenStream result = new KeyFilter(getBook(), source);

        return new TokenStreamComponents(source, result);
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.Analyzer#reusableTokenStream(java.lang.String, java.io.Reader)
     */
    /*@Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null) {
            streams = new SavedStreams(new WhitespaceTokenizer(reader));
            streams.setResult(new KeyFilter(getBook(), streams.getResult()));
            setPreviousTokenStream(streams);
        } else {
            streams.getSource().reset(reader);
        }
        return streams.getResult();
    }*/
}
