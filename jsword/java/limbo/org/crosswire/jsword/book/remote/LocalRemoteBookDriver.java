package org.crosswire.jsword.book.remote;

/**
 * A fullfilment of RemoteBibleDriver that uses a Local commection for test
 * purposes.
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
public class LocalRemoteBookDriver extends RemoteBookDriver
{
    /**
     * Pass on the exception because RemoteBibleDriver.ctor() could fail due to
     * its ping start-up operation.
     */
    public LocalRemoteBookDriver() throws RemoterException
    {
        ping();
    }

    /**
     * Accessor for the current remoter.
     * @return The remoter or null if none is available.
     * @see org.crosswire.jsword.book.remote.RemoteBookDriver#getRemoter()
     */
    protected Remoter getRemoter()
    {
        return remoter;
    }

    private static Remoter remoter = new LocalRemoter();
}
