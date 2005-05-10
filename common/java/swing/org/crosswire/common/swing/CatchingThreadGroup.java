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
package org.crosswire.common.swing;

/**
 * Another way to get ahold of missing exceptions.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class CatchingThreadGroup extends ThreadGroup
{
    /**
     * Simple ctor that names the threadgroup
     * @param name The name for this group
     */
    public CatchingThreadGroup(String name)
    {
        super(name);
    }

    /**
     * Simple ctor that names the threadgroup, and provides a parent group
     * @param group The parent ThreadGroup
     * @param name The name for this group
     */
    public CatchingThreadGroup(ThreadGroup group, String name)
    {
        super(group, name);
    }

    /* (non-Javadoc)
     * @see java.lang.ThreadGroup#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    public void uncaughtException(Thread t, Throwable ex)
    {
        ex.printStackTrace();
        ExceptionPane.showExceptionDialog(null, ex);
    }
}
