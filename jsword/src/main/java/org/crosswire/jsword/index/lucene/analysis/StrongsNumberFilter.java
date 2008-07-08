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
 * ID: $Id$
 */
package org.crosswire.jsword.index.lucene.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.DataPolice;
import org.crosswire.jsword.book.study.StrongsNumber;

/**
 * A StrongsNumberFilter normalizes Strong's Numbers.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class StrongsNumberFilter extends AbstractBookTokenFilter
{

    /**
     * Construct filtering <i>in</i>.
     */
    public StrongsNumberFilter(TokenStream in)
    {
      this(null, in);
    }

    /**
     * Construct filtering <i>in</i>.
     */
    public StrongsNumberFilter(Book book, TokenStream in)
    {
      super(book, in);
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.TokenStream#next(org.apache.lucene.analysis.Token)
     */
    public final Token next(Token result) throws IOException
    {
        // If the token is suffixed with '!a' or 'a', where 'a' is a sequence of 1 or more letters
        // then create a token without the suffix and also for the whole.
        Token token = result;
        if (lastToken == null)
        {
            token = input.next(token);
            if (token == null)
            {
                return null;
            }

            try
            {
                char[] buf = result.termBuffer();
                String tokenText = new String(buf, 0, result.termLength());

                number = new StrongsNumber(tokenText);
                String s = number.getStrongsNumber();

                if (!s.equals(tokenText))
                {
                    result.setTermBuffer(s.toCharArray(), 0, s.length());
                }

                if (number.isPart())
                {
                    lastToken = result;
                }
            }
            catch (BookException e)
            {
                DataPolice.report(e.getDetailedMessage());
            }
        }
        else
        {
            token = lastToken;
            lastToken = null;
            String s = number.getFullStrongsNumber();
            result.setTermBuffer(s.toCharArray(), 0, s.length());
        }
        return token;
    }

    private Token lastToken;
    private StrongsNumber number;
}
