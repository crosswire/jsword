
package org.crosswire.jsword.book.remote;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.jdom.Document;

/**
 * This represents all of the SerBibles.
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
public abstract class RemoteBookDriver extends AbstractBookDriver
{
    /**
     * Test the connection
     * @throws RemoterException if the ping fails
     */
    protected void ping() throws RemoterException
    {
        Remoter remoter = getRemoter();
        
        RemoteMethod method = new RemoteMethod(RemoteConstants.METHOD_GETBIBLES);
        remoter.execute(method);
    }

    /**
     * Accessor for the current remoter.
     * @return The remoter or null if none is available.
     */
    protected abstract Remoter getRemoter();

    /**
     * Get a list of the Books available from the name.
     * We cache the reply, for speed but we probably ought to have some way to
     * flush the cache because the list of Bibles on the server could change.
     * @return an array of book names
     */
    public BookMetaData[] getBooks()
    {
        synchronized (this)
        {
            if (rbmd == null)
            {
                try
                {
                    Remoter remoter = getRemoter();

                    RemoteMethod method = new RemoteMethod(RemoteConstants.METHOD_GETBIBLES);
                    Document doc = remoter.execute(method);

                    rbmd = Converter.convertDocumentToBibleMetaDatas(doc, remoter, remoter.getSpeed());
                    for (int i=0; i<rbmd.length; i++)
                    {
                        ids.put(rbmd[i].getID(), rbmd[i]);
                    }
                }
                catch (Exception ex)
                {
                    log.warn("failed to remote getBibleNames", ex);
                    rbmd = new RemoteBibleMetaData[0];
                }
            }
        }

        return rbmd;
    }

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(RemoteBookDriver.class);

    /**
     * The cache of Bible names.
     * At some stage it would be good to work out a way to clear the cache.
     */
    private RemoteBibleMetaData[] rbmd;

    /**
     * The id to metadata map
     */
    private Map ids = new HashMap();
}
