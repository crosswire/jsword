
package org.crosswire.jsword.book.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.jdom.Document;
import org.jdom.input.SAXHandler;

/**
 * A Simple/Test implmentation of Remoter that doesn't do any remote access.
 * Calls to execute() are implemented by simply directly calling the local
 * methods.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class LocalRemoter implements Remoter
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.remote.Remoter#getRemoterName()
     */
    public String getRemoterName()
    {
        return "Local";
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
                List lbmds = Books.getBookMetaDatas(FILTER);
                BookMetaData[] bmds = (BookMetaData[]) lbmds.toArray(new BookMetaData[lbmds.size()]);

                String[] uids = getUIDs(bmds);
                return Converter.convertBookMetaDatasToDocument(bmds, uids);
            }
            else if (MethodName.GETDATA.equals(methodname))
            {
                String uid = method.getParameter(ParamName.PARAM_BIBLE);
                BookMetaData bmd = lookupBookMetaData(uid);
                Book bible = bmd.getBook();
                String refstr = method.getParameter(ParamName.PARAM_PASSAGE);
                Passage ref = PassageFactory.createPassage(refstr);
                BookData data = bible.getData(ref);

                SAXEventProvider provider = data.getSAXEventProvider();
                SAXHandler handler = new SAXHandler();
                provider.provideSAXEvents(handler);
                return handler.getDocument();
            }
            else if (MethodName.FINDPASSAGE.equals(methodname))
            {
                String uid = method.getParameter(ParamName.PARAM_BIBLE);
                BookMetaData bmd = lookupBookMetaData(uid);
                Book book = bmd.getBook();

                String word = method.getParameter(ParamName.PARAM_FINDSTRING);
                boolean match = Boolean.getBoolean(method.getParameter(ParamName.PARAM_FINDMATCH));
                String refstr = method.getParameter(ParamName.PARAM_FINDRANGE);
                Passage range = PassageFactory.createPassage(refstr);
                Search search = new Search(word, match);
                search.setRestriction(range);

                KeyList keylist = book.find(search);
                return Converter.convertKeyListToDocument(keylist);
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

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.remote.Remoter#getSpeed()
     */
    public int getSpeed()
    {
        return Books.SPEED_REMOTE_FASTEST;
    }

    /**
     * Lookup a BibleMetaData using the UID that we assigned to it earlier
     */
    private BookMetaData lookupBookMetaData(String uid)
    {
        return (BookMetaData) uid2bmd.get(uid);
    }

    /**
     * Create a list of UIDs for the given BibleMetaDatas.
     * This method does rely on the HashMap using equals() to deturmine
     * equality. I'm not sure if it does this.
     * @param bmds The array to create/retrieve UIDs for
     * @return String[] The new UID array
     */
    private String[] getUIDs(BookMetaData[] bmds)
    {
        String[] uids = new String[bmds.length];
        for (int i=0; i<bmds.length; i++)
        {
            BookMetaData bmd = bmds[i];
            String uid = (String) bmd2uid.get(bmd);
            
            if (uid == null)
            {
                uid = createUID();
                bmd2uid.put(bmd, uid);
                uid2bmd.put(uid, bmd);
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
        return "uid"+rand;
    }

    /**
     * The filter to select the bibles we are exporting
     */
    private static final BookFilter FILTER = BookFilters.getFaster(Books.SPEED_SLOWEST);

    /**
     * To help finding uids from bmds
     */
    private Map bmd2uid = new HashMap();

    /**
     * To help finding bmds from uids
     */
    private Map uid2bmd = new HashMap();
}
