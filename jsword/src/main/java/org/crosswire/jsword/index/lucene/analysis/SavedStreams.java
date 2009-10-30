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
 * Copyright: 2009
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.jsword.index.lucene.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

/**
 * SavedStreams is used to make reusable Lucene analyzers.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
/* package */class SavedStreams {
    /**
     * @return the source
     */
    public Tokenizer getSource() {
        return source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(Tokenizer source) {
        this.source = source;
    }

    /**
     * @return the result
     */
    public TokenStream getResult() {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(TokenStream result) {
        this.result = result;
    }

    private Tokenizer source;
    private TokenStream result;
}
