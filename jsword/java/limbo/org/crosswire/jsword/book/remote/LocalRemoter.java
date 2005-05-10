/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.jdom.Document;
import org.jdom.input.SAXHandler;

/**
 * A Simple/Test implmentation of Remoter that doesn't do any remote access.
 * Calls to execute() are implemented by simply directly calling the local
 * methods.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class LocalRemoter implements Remoter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.remote.Remoter#getRemoterName()
     */
    public String getRemoterName()
    {
        return "Local"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.remote.Remoter#execute(org.crosswire.jsword.book.remote.RemoteMethod)
     */
    public Document execute(RemoteMethod method) throws RemoterException
    {
        MethodName methodname = method.getMethodName();

        try
        {
            if (MethodName.GETBIBLES.equals(methodname))
            {
                List books = Books.installed().getBooks(FILTER);
                Book[] bookArray = (Book[]) books.toArray(new Book[books.size()]);

                String[] uids = getUIDs(bookArray);
                return Converter.convertBookToDocument(bookArray, uids);
            }
            else if (MethodName.GETDATA.equals(methodname))
            {
                String uid = method.getParameter(ParamName.PARAM_BIBLE);
                Book book = lookupBook(uid);
                String refstr = method.getParameter(ParamName.PARAM_PASSAGE);
                Key ref = book.getKey(refstr);
                BookData data = book.getData(ref);

                SAXEventProvider provider = data.getSAXEventProvider();
                SAXHandler handler = new SAXHandler();
                provider.provideSAXEvents(handler);
                return handler.getDocument();
            }
            else if (MethodName.FINDPASSAGE.equals(methodname))
            {
                String uid = method.getParameter(ParamName.PARAM_BIBLE);
                Book book = lookupBook(uid);

                String word = method.getParameter(ParamName.PARAM_FINDSTRING);
                Key key = book.find(word);
                return Converter.convertKeyListToDocument(key);
            }
            else
            {
                throw new RemoterException(Msg.REMOTE_NOSUPPORT, new Object[] {methodname});
            }
        }
        catch (RemoterException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new RemoterException(Msg.REMOTE_FAIL, ex);
        }
    }

    /**
     * Lookup a BibleMetaData using the UID that we assigned to it earlier
     */
    private Book lookupBook(String uid)
    {
        return (Book) uid2book.get(uid);
    }

    /**
     * Create a list of UIDs for the given BibleMetaDatas.
     * This method does rely on the HashMap using equals() to deturmine
     * equality. I'm not sure if it does this.
     * @param books The array to create/retrieve UIDs for
     * @return String[] The new UID array
     */
    private String[] getUIDs(Book[] books)
    {
        String[] uids = new String[books.length];
        for (int i=0; i<books.length; i++)
        {
            Book book = books[i];
            String uid = (String) book2uid.get(book);
            
            if (uid == null)
            {
                uid = createUID();
                book2uid.put(book, uid);
                uid2book.put(uid, book);
            }
            
            uids[i] = uid;
        }

        return uids;
    }

    /**
     * Create a new random UID.
     * This method should never return the same value twice.
     * @return String
     */
    private static String createUID()
    {
        int rand = (int) (Math.random() * Integer.MAX_VALUE);
        return "uid"+rand; //$NON-NLS-1$
    }

    /**
     * The filter to select the bibles we are exporting
     */
    private static final BookFilter FILTER = BookFilters.getAll();

    /**
     * To help finding uids from bmds
     */
    private Map book2uid = new HashMap();

    /**
     * To help finding bmds from uids
     */
    private Map uid2book = new HashMap();
}