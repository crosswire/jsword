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
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.study.StrongsMapSet;
import org.crosswire.jsword.book.study.StrongsNumber;
import org.crosswire.jsword.passage.Key;
import org.jdom.Element;

public class StrongsAnalysis
{

    public StrongsAnalysis()
    {
        Book bible = Books.installed().getBook("KJV"); //$NON-NLS-1$
        if (!bible.hasFeature(FeatureType.STRONGS_NUMBERS))
        {
            bible = null;
            List bibles = Books.installed().getBooks(new BookFilters.BookFeatureFilter(FeatureType.STRONGS_NUMBERS));

            if (bibles.size() > 0)
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
                    osis = data.getOsis();
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
