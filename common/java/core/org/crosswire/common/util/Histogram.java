package org.crosswire.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple implementation of a histogram. It would be nice to enhance
 * it to order on frequency.
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
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class Histogram
{
    /**
     * Create an empty histogram
     */
    public Histogram()
    {
        hist = new HashMap();
    }

    /**
     * note that this key has been seen one time more than before.
     * @param key
     */
    public void increment(String key)
    {
        Counter counter = (Counter) hist.get(key);
        if (counter == null)
        {
            counter = new Counter();
            hist.put(key, counter);
        }
        counter.increment();
    }

    public void clear()
    {
        hist.clear();
    }

    /**
     * The format of the histogram is an unordered list
     * of string and the counts of the number of times it has been seen.
     * @return the resultant histogram
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        Iterator iter = hist.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            buf.append(entry.getKey().toString());
            buf.append('\t');
            buf.append(entry.getValue().toString());
            buf.append('\n');
        }
        return buf.toString();
    }

    /**
     * Trivial mutable counting integer class.
     */
    private static class Counter
    {
        public Counter()
        {
        }

        public void increment()
        {
            counter++;
        }

        public String toString()
        {
            return Integer.toString(counter);
        }
        private int counter;
    }

    private Map hist;

}
