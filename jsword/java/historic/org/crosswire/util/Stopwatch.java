
package org.crosswire.util;

import java.util.*;

/**
 * A Little performance tool
 * @author Joe Walker
 * @version $Id$
 */
public class Stopwatch
{
    /**
     * Ensure we cant be instantiated
     */
    private Stopwatch()
    {
    }

    /**
     * Start the current thread on a timing run
     */
    public static void start() throws IllegalStateException
    {
        if (local.get() != null)
            throw new IllegalStateException("Already started");

        Long start = new Long(System.currentTimeMillis());
        origin.set(start);

        Map times = new HashMap();
        local.set(times);
    }

    /**
     * Marks a waypoint in the current timing run
     */
    public static void lap(String name)
    {
        Map times = (Map) local.get();
        long start = ((Long) origin.get()).longValue();

        if (times == null)
            throw new IllegalStateException("Not started");

        times.put(name, new Long(System.currentTimeMillis()-start));
    }

    /**
     * Ends the timing run on the current thread
     */
    public static void stop()
    {
        Map times = (Map) local.get();
        long start = ((Long) origin.get()).longValue();

        if (times == null)
            throw new IllegalStateException("Not started");

        times.put("STOP", new Long(System.currentTimeMillis()-start));

        local.set(null);
        origin.set(null);

        // We can either report now or store for later.
        // accumulated.add(times);
        report(Thread.currentThread().getName(), times);
    }

    /**
     * Report on the state of the completed timings so far
     */
    public static void report(boolean reset)
    {
        for (int i=0; i<accumulated.size(); i++)
        {
            Map times = (Map) accumulated.get(i);
            report(""+i, times);
        }

        if (reset)
        {
            accumulated.clear();
        }
    }

    /**
     * Produce a single report
     */
    private static void report(String id, Map times)
    {
        Iterator it = times.keySet().iterator();
        while (it.hasNext())
        {
            String name = (String) it.next();
            Long time = (Long) times.get(name);
            System.out.println(id+","+name+","+time);
        }
    }

    /**
     * Quick test
     */
    public static void main(String[] args)
    {
        for (int i=0; i<20; i++)
        {
            new Thread(new Runnable()
            {
                public void run()
                {
                    Stopwatch.start();

                    while (true) { if (Math.random() > 0.9999) break; }

                    Stopwatch.lap("first");

                    while (true) { if (Math.random() > 0.99999) break; }

                    Stopwatch.lap("second");

                    while (true) { if (Math.random() > 0.9999) break; }

                    Stopwatch.lap("third");

                    while (true) { if (Math.random() > 0.9999) break; }

                    Stopwatch.stop();
                }
            }).start();
        }

        // Stopwatch.report(false);
    }

    /**
     * When the program started
     */
    private static ThreadLocal origin = new ThreadLocal();

    /**
     * The accumulated data
     */
    private static ThreadLocal local = new ThreadLocal();

    /**
     * The accumulated data
     */
    private static List accumulated = new ArrayList();
}
