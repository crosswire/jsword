
package org.crosswire.jsword.book.remote;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Filters;
import org.crosswire.jsword.book.data.BibleData;
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class LocalRemoter implements Remoter
{
    /**
     * A simple name
     */
    public String getRemoterName()
    {
        return "Local";
    }

    /**
     * @see Remoter#execute(RemoteMethod)
     */
    public Document execute(RemoteMethod method) throws RemoterException
    {
        String methodname = method.getMethodName();

        if (RemoteConstants.METHOD_GETBIBLES.equals(methodname))
        {
            List lbmds = Books.getBooks(Filters.getFaster(Books.SPEED_SLOWEST));
            BibleMetaData[] bmds = (BibleMetaData[]) lbmds.toArray(new BibleMetaData[lbmds.size()]);

            String[] uids = getUIDs(bmds);
            return Converter.convertBibleMetaDatasToDocument(bmds, uids);
        }
        else if (RemoteConstants.METHOD_GETDATA.equals(methodname))
        {
            try
            {
                String uid = method.getParameter(RemoteConstants.PARAM_BIBLE);
                BibleMetaData bmd = lookupBibleMetaData(uid);
                Bible bible = bmd.getBible();
                String refstr = method.getParameter(RemoteConstants.PARAM_PASSAGE);
                Passage ref = PassageFactory.createPassage(refstr);
                BibleData data = bible.getData(ref);

                SAXEventProvider provider = data.getSAXEventProvider();
                SAXHandler handler = new SAXHandler();
                provider.provideSAXEvents(handler);
                return handler.getDocument();
            }
            catch (Exception ex)
            {
                throw new RemoterException("remote_getdata_fail", ex);
            }
        }
        else if (RemoteConstants.METHOD_FINDPASSAGE.equals(methodname))
        {
            try
            {
                String uid = method.getParameter(RemoteConstants.PARAM_BIBLE);
                BibleMetaData bmd = lookupBibleMetaData(uid);
                Bible bible = bmd.getBible();
                String word = method.getParameter(RemoteConstants.PARAM_WORD);
                Passage ref = bible.findPassage(word);
                return Converter.convertPassageToDocument(ref);
            }
            catch (BookException ex)
            {
                throw new RemoterException("remote_findpassage_fail", ex);
            }
        }
        else if (RemoteConstants.METHOD_STARTSWITH.equals(methodname))
        {
            try
            {
                String uid = method.getParameter(RemoteConstants.PARAM_BIBLE);
                BibleMetaData bmd = lookupBibleMetaData(uid);
                Bible bible = bmd.getBible();
                String word = method.getParameter(RemoteConstants.PARAM_WORD);
                Iterator it = bible.getStartsWith(word);
                return Converter.convertStartsWithToDocument(it);
            }
            catch (BookException ex)
            {
                throw new RemoterException("remote_startswith_fail", ex);
            }
        }
        else
        {
            throw new RemoterException("method not supported. given: "+methodname);
        }
    }

    /**
     * How fast are we?
     * @see org.crosswire.jsword.book.remote.Remoter#getSpeed()
     */
    public int getSpeed()
    {
        return Books.SPEED_REMOTE_FASTEST;
    }

    /**
     * Lookup a BibleMetaData using the UID that we assigned to it earlier
     * @param bmds
     * @return String[]
     */
    private BibleMetaData lookupBibleMetaData(String uid)
    {
        return (BibleMetaData) uid2bmd.get(uid);
    }

    /**
     * Create a list of UIDs for the given BibleMetaDatas.
     * This method does rely on the HashMap using equals() to deturmine
     * equality. I'm not sure if it does this.
     * @param bmds The array to create/retrieve UIDs for
     * @return String[] The new UID array
     */
    private String[] getUIDs(BibleMetaData[] bmds)
    {
        String[] uids = new String[bmds.length];
        for (int i=0; i<bmds.length; i++)
        {
            BibleMetaData bmd = bmds[i];
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

    private Map bmd2uid = new HashMap();
    private Map uid2bmd = new HashMap();
}
