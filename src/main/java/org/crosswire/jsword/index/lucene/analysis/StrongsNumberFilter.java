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
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.crosswire.jsword.JSMsg;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.study.StrongsNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A StrongsNumberFilter normalizes Strong's Numbers.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public class StrongsNumberFilter extends AbstractBookTokenFilter {

    /**
     * Construct filtering <i>in</i>.
     * 
     * @param in 
     */
    public StrongsNumberFilter(TokenStream in) {
        this(null, in);
    }

    /**
     * Construct filtering <i>in</i>.
     * 
     * @param book the book
     * @param in 
     */
    public StrongsNumberFilter(Book book, TokenStream in) {
        super(book, in);
        termAtt = addAttribute(TermAttribute.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.analysis.TokenStream#incrementToken()
     */
    @Override
    public boolean incrementToken() throws IOException {
        // If the term is suffixed with '!a' or 'a', where 'a' is a sequence of
        // 1 or more letters
        // then create a token without the suffix and also for the whole.
        if (number == null) {
            // Need to loop over invalid tokens
            while (input.incrementToken()) {
                String tokenText = termAtt.term();

                number = new StrongsNumber(tokenText);

                // Skip invalid Strong's Numbers.
                // Still need to return true as there may be more tokens to filter.
                if (!number.isValid()) {
                    // TRANSLATOR: User error condition: Indicates that what was given is not a Strong's Number. {0} is a placeholder for the bad Strong's Number.
                    log.warn(JSMsg.gettext("Not a valid Strong's Number \"{0}\"", tokenText));

                    // Go get the next token
                    continue;
                }

                String s = number.getStrongsNumber();
                termAtt.setTermBuffer(s);

                // If the number had a part keep it around for the next call
                // TODO(DMS): if there is a part, then treat as a synonym,
                //      setting the same position increment.
                if (!number.isPart()) {
                    number = null;
                }

                // incrementToken returned a value. There may be more input.
                return true;
            }

            // There was no more input
            return false;
        }

        // Process the Strong's number with the !a
        termAtt.setTermBuffer(number.getFullStrongsNumber());
        // We are done with the Strong's Number so mark it as used
        number = null;
        // We are working on a value returned by incrementToken.
        // There may be more input.
        return true;
    }

    /* Define to quite FindBugs */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /* Define to quite FindBugs */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private TermAttribute termAtt;
    private StrongsNumber number;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(StrongsNumberFilter.class);
}
