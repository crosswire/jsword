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

import org.apache.lucene.analysis.TokenStream;
import org.crosswire.jsword.book.Book;

/**
 * A KeyFilter normalizes OSISrefs.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class XRefFilter extends AbstractBookTokenFilter {
    /**
     * Construct filtering <i>in</i>.
     * @param in 
     */
    public XRefFilter(TokenStream in) {
        this(null, in);
    }

    /**
     * Construct an XRefFilter tied to a Book.
     * 
     * @param book
     *            the book to which this TokenFilter is tied.
     * @param in
     *            the input TokenStream
     */
    public XRefFilter(Book book, TokenStream in) {
        super(book, in);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.TokenStream#incrementToken()
     */
    @Override
    public boolean incrementToken() throws IOException {
        // TODO(DMS): actually normalize
        return input.incrementToken();
    }

}
