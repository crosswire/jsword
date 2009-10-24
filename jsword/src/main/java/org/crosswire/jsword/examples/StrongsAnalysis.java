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
 * ID: $Id: DictToOsis.java 1344 2007-05-23 21:50:52 -0400 (Wed, 23 May 2007) dmsmith $
 */
package org.crosswire.jsword.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.study.StrongsMapSet;
import org.crosswire.jsword.book.study.StrongsNumber;
import org.crosswire.jsword.passage.Key;
import org.jdom.Element;

/**
 * Analyze Strong's Numbers in a module.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class StrongsAnalysis
{
    /**
     *
     */
    public StrongsAnalysis()
    {
        Book bible = Books.installed().getBook("KJV"); //$NON-NLS-1$
        if (!bible.hasFeature(FeatureType.STRONGS_NUMBERS))
        {
            bible = null;
            List bibles = Books.installed().getBooks(new BookFilters.BookFeatureFilter(FeatureType.STRONGS_NUMBERS));

            if (!bibles.isEmpty())
            {
                bible = (Book) bibles.get(0);
            }
        }

        if (bible == null)
        {
            return;
        }

        List errors = new ArrayList();
        StrongsMapSet sms = new StrongsMapSet();
        analyze(sms, bible, errors, bible.getGlobalKeyList());
    }

    /**
     * @param sms
     * @param book
     * @param errors
     * @param wholeBible
     */
    public void analyze(StrongsMapSet sms, Book book, List errors, Key wholeBible)
    {
        Key subkey = null;
        BookData data = null;
        Element osis = null;
        StringBuffer buffer = new StringBuffer();
        for (Iterator it = wholeBible.iterator(); it.hasNext(); )
        {
            subkey = (Key) it.next();
            if (subkey.canHaveChildren())
            {
                analyze(sms, book, errors, subkey);
            }
            else
            {
                data = new BookData(book, subkey);
                osis = null;

                try
                {
                    osis = data.getOsisFragment();
                }
                catch (BookException e)
                {
                    errors.add(subkey);
                    continue;
                }

                // Do the actual indexing
                Collection allW = OSISUtil.getDeepContent(osis, OSISUtil.OSIS_ELEMENT_W);
                Iterator wIter = allW.iterator();
                while (wIter.hasNext())
                {
                    // Clear out the buffer for re-use
                    int len = buffer.length();
                    if (len > 0)
                    {
                        buffer.delete(0, len);
                    }

                    Element wElement = (Element) wIter.next();
                    String snAttr = wElement.getAttributeValue(OSISUtil.ATTRIBUTE_W_LEMMA);

                    String content = OSISUtil.getPlainText(wElement);

                    Matcher matcher = strongsNumberPattern.matcher(snAttr);
                    while (matcher.find())
                    {
                        try
                        {
                            StrongsNumber strongsNumber = new StrongsNumber(matcher.group(1));
                            if (buffer.length() > 0)
                            {
                                buffer.append(' ');
                            }
                            buffer.append(strongsNumber.getStrongsNumber());
                        }
                        catch (BookException e)
                        {
                            errors.add(subkey);
                            continue;
                        }
                    }

                    // now we can actually store the mapping
                    sms.add(buffer.toString(), content);
               }
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new StrongsAnalysis();
    }

    private static Pattern strongsNumberPattern = Pattern.compile("strong:([GH][0-9]+)"); //$NON-NLS-1$
}
