
package org.crosswire.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Various utilities for examining the running Threads and
 * controlling their execution.
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
public class ThreadUtil
{
    /**
     * Call a static method on a class given a sting
     * @param base The object to invoke a method on
     * @param called_method_name The text of the invocation eg "getName"
     * @param called_params For example new Object[] { ...}
     */
    public static Object invoke(Object base, String called_method_name, Object[] called_params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // Create a Class array describing the params
        Class[] called_types = new Class[called_params.length];
        for (int i=0; i<called_params.length; i++)
        {
            called_types[i] = called_params[i].getClass();
        }

        // Reflection
        Class called_class = base.getClass();

        // The bad news is that we can't use something like:
        // called_class.getMethod(called_method_name, called_types);
        // because it does not cope with inheritance (at least in the MVM)
        // so we have to search ourselves...
        Method[] test_methods = called_class.getMethods();
        for (int i=0; i<test_methods.length; i++)
        {
            // This this the right method name?
            if (!test_methods[i].getName().equals(called_method_name)) continue;

            // The right number of params
            Class[] test_types = test_methods[i].getParameterTypes();
            if (test_types.length != called_types.length) continue;

            // Of the right types?
            for (int j=0; j<test_types.length; j++)
            {
                if (!test_types[j].isAssignableFrom(called_types[j])) continue;
            }

            // So this is a match
            return test_methods[i].invoke(base, called_params);
        }

        throw new NoSuchMethodException(called_method_name);
    }

    /**
     * Call a static method on a class given a sting
     * @param call The text of the invocation eg "java.lang.String.getName"
     * @param called_params For example new Object[] { ...}
     */
    public static Object invoke(String call, Object[] called_params) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        // Spilt the call into class name and method name
        int last_dot = call.lastIndexOf('.');
        String called_class_name = call.substring(0, last_dot);
        String called_method_name = call.substring(last_dot+1);

        // Create a Class array describing the params
        Class[] called_types = new Class[called_params.length];
        for (int i=0; i<called_params.length; i++)
        {
            called_types[i] = called_params[i].getClass();
        }

        // Reflection
        Class called_class = Class.forName(called_class_name);

        // The bad news is that we can't use something like:
        // called_class.getMethod(called_method_name, called_types);
        // because it does not cope with inheritance (at least in the MVM)
        // so we have to search ourselves...
        Method[] test_methods = called_class.getMethods();
        for (int i=0; i<test_methods.length; i++)
        {
            // This this the right method name?
            if (!test_methods[i].getName().equals(called_method_name)) continue;

            // The right number of params
            Class[] test_types = test_methods[i].getParameterTypes();
            if (test_types.length != called_types.length) continue;

            // Of the right types?
            for (int j=0; j<test_types.length; j++)
            {
                if (!test_types[j].isAssignableFrom(called_types[j])) continue;
            }

            // So this is a match
            return test_methods[i].invoke(null, called_params);
        }

        throw new NoSuchMethodException(called_method_name);
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
    public synchronized static void soundSleep(long millis)
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
        List vec = new ArrayList();

        listThreads(vec, 0, base);

        return (String[]) vec.toArray(new String[vec.size()]);
    }

    /**
     * Private, used by getListing. Adds to a Vector the sub-threads
     * @param vec The Vector to add to.
     * @param depth The current recursion depth
     * @param group The ThreadGroup to detail
     */
    private static void listThreads(List vec, int depth, ThreadGroup group)
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
                addItem(vec, depth, group.getName());
            }
            catch (SecurityException ex)
            {
                addItem(vec, depth, "<Unavailable>");
            }

            for (int i=0; i<num_threads; i++)
            {
                listThread(vec, depth+1, threads[i]);
            }

            for (int i=0; i<num_groups; i++)
            {
                listThreads(vec, depth+1, groups[i]);
            }
        }
        catch (Exception ex)
        {
            addItem(vec, depth, ""+ex);
        }
    }

    /**
     * Private, used by getListing. Adds to a Vector the sub-threads
     * @param vec The Vector to add to.
     * @param depth The current recursion depth
     * @param group The ThreadGroup to detail
     */
    private static void listThread(List vec, int depth, Thread thread)
    {
        if (thread == null) return;

        try
        {
            addItem(vec, depth, thread.getName() + " (" + thread.getPriority() + ")");
        }
        catch (SecurityException ex)
        {
            addItem(vec, depth, "<Unavailable>");
        }
    }

    /**
     * Private, used by getListing. Adds to a Vector the sub-threads
     * @param vec The Vector to add to.
     * @param depth The current recursion depth
     * @param group The ThreadGroup to detail
     */
    private static void addItem(List vec, int depth, String item)
    {
        vec.add(StringUtils.leftPad("", depth*2) + item);
    }
}
