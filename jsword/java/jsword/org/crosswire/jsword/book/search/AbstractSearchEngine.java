package org.crosswire.jsword.book.search;

import java.io.IOException;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.NoSuchKeyException;

/**
 * An implmentation of some of the basics of a search engine in terms of an
 * index that needs loading, on activation.
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
public abstract class AbstractSearchEngine implements SearchEngine
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#activate()
     */
    public final void activate(Lock lock)
    {
        // Load the ascii Passage index
        if (isIndexed())
        {
            try
            {
                loadIndexes();
            }
            catch (IOException ex)
            {
                log.warn("second load failure", ex); //$NON-NLS-1$
            }
        }
        else
        {
            Reporter.informUser(this, Msg.TYPE_INDEXGEN);

            // The index is usable but incomplete, so kick off a generation
            // thread if there is not one running already.
            if (!running)
            {
                Thread work = new Thread(new IndexerRunnable());
                work.start();
            }
            else
            {
                log.warn("activate() called while job in progress", new Exception()); //$NON-NLS-1$
            }
        }

        active = true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#deactivate()
     */
    public final void deactivate(Lock lock)
    {
        try
        {
            unloadIndexes();
        }
        catch (IOException ex)
        {
            Reporter.informUser(this, ex);
        }

        active = false;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive()
    {
        if (!active)
        {
            Activator.activate(this);
        }
    }

    /**
     * Are we active
     */
    private boolean active = false;

    /**
     * Detects if index data has been stored for this Bible already
     */
    protected abstract boolean isIndexed();

    /**
     * Loads the index files from disk ready for searching
     * @throws IOException if the load fails to read from disk
     */
    protected abstract void loadIndexes() throws IOException;

    /**
     * Frees the memory in a previously loaded index
     * @throws IOException if the load fails to read from disk
     */
    protected abstract void unloadIndexes() throws IOException;

    /**
     * Read from the given source version to generate ourselves. On completion
     * of this method the index should be usable. If this is not the natural
     * way this emthod finishes then it should be possible to call loadIndexes()
     * @param ajob The place to report progress
     * @throws IOException if the load fails to read from disk
     * @throws BookException if there is a problem reading from the Bible
     */
    protected abstract void generateSearchIndex(Job ajob) throws IOException, BookException, NoSuchKeyException;

    /**
     * Our children may discover that index files are present during index
     * creation, and wrongly report that the index is done, so we need to give
     * them a chance of getting the right answer.
     */
    protected boolean isRunning()
    {
        return running;
    }

    /**
     * Is there an index generation in progress?
     */
    protected boolean running = false;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AbstractSearchEngine.class);

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
            Job job = JobManager.createJob(Msg.INDEXING.toString(), Thread.currentThread(), false);

            try
            {
                generateSearchIndex(job);
            }
            catch (Exception ex)
            {
                Reporter.informUser(AbstractSearchEngine.this, ex);
                job.ignoreTimings();
            }
            finally
            {
                job.done();
                running = false;
            }
        }
    }
}
