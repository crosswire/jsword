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
package org.crosswire.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Various utilities for examining the running Threads and
 * controlling their execution.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class ThreadUtil
{
    /**
     * Prevent Instansiation
     */
    private ThreadUtil()
    {
    }

    /**
     * Sleep and don't think about throwing. Mostly when a thread calls
     * sleep you need to wrap it in a special try-catch block to get
     * hold of the InterruptedException - which is rarely called and
     * mostly ignored. This code takes care of the ignoring, and simply
     * logs some stuff if InterruptedException do happen.
     * @param millis The length of time to wait in milliseconds
     * @see java.lang.InterruptedException
     */
    public static synchronized void soundSleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException ex)
        {
            Reporter.informUser(ThreadUtil.class, ex);
        }
    }

    /**
     * Find the root ThreadGroup by ascending the Thread tree
     * @return The root ThreadGroup
     */
    public static ThreadGroup findRoot()
    {
        // Determine the current thread group
        ThreadGroup top = Thread.currentThread().getThreadGroup();

        // Proceed to the top ThreadGroup
        while (top.getParent() != null)
        {
            top = top.getParent();
        }

        return top;
    }

    /**
     * Create a StringArray (mostly for debugging) detailing the
     * current Threads, starting at the root ThreadGroup
     * @return The listing for all sub threads
     */
    public static String[] getListing()
    {
        return getListing(findRoot());
    }

    /**
     * Create a StringArray (mostly for debugging) detailing the
     * current Threads, starting at the specified ThreadGroup
     * @param base The ThreadGroup to detail
     * @return The listing for all sub threads
     */
    public static String[] getListing(ThreadGroup base)
    {
        List list = new ArrayList();

        listThreads(list, 0, base);

        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Private, used by getListing. Adds to a List the sub-threads
     * @param list The List to add to.
     * @param depth The current recursion depth
     * @param group The ThreadGroup to detail
     */
    private static void listThreads(List list, int depth, ThreadGroup group)
    {
        if (group == null)
        {
            return;
        }

        try
        {
            int num_threads = group.activeCount();
            int num_groups = group.activeGroupCount();

            Thread[] threads = new Thread[num_threads];
            ThreadGroup[] groups = new ThreadGroup[num_groups];

            group.enumerate(threads, false);
            group.enumerate(groups, false);

            try
            {
                addItem(list, depth, group.getName());
            }
            catch (SecurityException ex)
            {
                addItem(list, depth, Msg.UNAVILABLE.toString());
            }

            for (int i = 0; i < num_threads; i++)
            {
                listThread(list, depth + 1, threads[i]);
            }

            for (int i = 0; i < num_groups; i++)
            {
                listThreads(list, depth + 1, groups[i]);
            }
        }
        catch (Exception ex)
        {
            addItem(list, depth, ex.toString());
        }
    }

    /**
     * Private, used by getListing. Adds to a List the sub-threads
     * @param list The List to add to.
     * @param depth The current recursion depth
     * @param thread The Thread to detail
     */
    private static void listThread(List list, int depth, Thread thread)
    {
        if (thread == null)
        {
            return;
        }

        try
        {
            addItem(list, depth, thread.getName() + " (" + thread.getPriority() + ')'); //$NON-NLS-1$
        }
        catch (SecurityException ex)
        {
            addItem(list, depth, Msg.UNAVILABLE.toString());
        }
    }

    /**
     * Private, used by getListing. Adds to a List the sub-threads
     * @param list The List to add to.
     * @param depth The current recursion depth
     */
    private static void addItem(List list, int depth, String item)
    {
        list.add(PADDING.substring(0, depth * 2) + item); //$NON-NLS-1$
    }

    private static final String PADDING = "                                                                "; //$NON-NLS-1$

}
