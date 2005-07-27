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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.remote;

/**
 * A fullfilment of RemoteBibleDriver that uses a Local commection for test
 * purposes.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
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
