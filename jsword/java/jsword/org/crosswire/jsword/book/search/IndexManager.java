package org.crosswire.jsword.book.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Reporter;

/**
 * .
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
public class IndexManager
{
    /**
     * 
     */
    public static IndexManager instance()
    {
        return instance;
    }

    /**
     * 
     */
    public void createIndex(Index index)
    {
        Reporter.informUser(this, Msg.TYPE_INDEXGEN);

        todo.add(index);

        Thread work = new Thread(runner);
        work.start();
    }

    /**
     * The index creation thread
     */
    private class IndexerRunnable implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            running = true;

            Iterator it = todo.iterator();
            while (it.hasNext())
            {
                Index index = (Index) it.next();
                Job job = JobManager.createJob(Msg.INDEXING.toString(), Thread.currentThread(), false);

                try
                {
                    index.generateSearchIndex(job);
                }
                catch (Exception ex)
                {
                    Reporter.informUser(IndexManager.class, ex);
                    job.ignoreTimings();
                }
                finally
                {
                    job.done();
                }                
            }

            running = false;
        }
    }

    /**
     * 
     */
    private static IndexManager instance = new IndexManager();

    /**
     * The thread worker that creates the indexes.
     */
    private Runnable runner = new IndexerRunnable();

    /**
     * The books to be indexed
     */
    protected Set todo = new HashSet();

    /**
     * Is there an index generation in progress?
     */
    protected boolean running = false;
}
