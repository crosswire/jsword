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

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
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
      termAtt = (TermAttribute) addAttribute(TermAttribute.class);
    }

    /* (non-Javadoc)
     * @see org.apache.lucene.analysis.TokenStream#incrementToken()
     */
    public boolean incrementToken() throws IOException
    {
        // If the term is suffixed with '!a' or 'a', where 'a' is a sequence of 1 or more letters
        // then create a token without the suffix and also for the whole.
        if (number == null)
        {
            if (super.incrementToken())
            {
                try
                {
                    String tokenText = termAtt.term();

                    number = new StrongsNumber(tokenText);
                    String s = number.getStrongsNumber();

                    // Was it a Strong's Number? If so it transformed.
                    if (!s.equals(tokenText))
                    {
                        termAtt.setTermBuffer(s);

                        // If the number had a part keep it around for the next call
                        if (!number.isPart())
                        {
                            number = null;
                        }
                    }
                }
                catch (BookException e)
                {
                    DataPolice.report(e.getDetailedMessage());
                }

                // We are providing a term
                return true;
            }

            // There was no more input
            return false;
        }

        // Process the Strong's number with the !a
        termAtt.setTermBuffer(number.getFullStrongsNumber());
        // We are done with the Strong's Number so mark it as used
        number = null;
        // We are providing a term
        return true;
    }

    /* Define to quite FindBugs */
    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }

    /* Define to quite FindBugs */
    public int hashCode()
    {
        return super.hashCode();
    }

    private TermAttribute termAtt;
    private StrongsNumber number;
}
