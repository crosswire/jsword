
package org.crosswire.common.progress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;

/**
 * A Generic method of keeping track of Threads and monitoring their progress.
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
public class Job
{
    /**
     * Create a new Job
     * @param description Short description of this job
     * @param predicturl Optional URL to save/load prediction times from
     * @param work Optional thread to use in request to stop work
     */
    public Job(String description, URL predicturl, Thread work)
    {
        this.description = description;
        this.predicturl = predicturl;
        this.work = work;

        // Set-up the timings files. It's not a disaster if it doesn't load
        predicted = new Properties();
        try
        {
            if (predicturl != null)
            {
                InputStream in = predicturl.openStream();
                if (in != null)
                {
                    predicted.load(in);
                }
            }
        }
        catch (IOException ex)
        {
            log.debug("Failed to load prediction times - guessing");
        }
        
        // And the predictions for next time
        current = new Properties();
        current.setProperty(START, Long.toString(System.currentTimeMillis()));

        JobManager.setProgress(new WorkEvent(this, description, 0));
    }

    /**
     * Set progress bar.
     */
    public void setProgress(String progress)
    {
        current.setProperty(progress, Long.toString(System.currentTimeMillis()));

        int percent = (int) getTimeFromProperties(predicted, progress);
        JobManager.setProgress(new WorkEvent(this, progress, percent));
    }

    /**
     * Set progress bar.
     */
    public void setProgress(int percent, String progress)
    {
        current.setProperty(progress, Long.toString(System.currentTimeMillis()));

        JobManager.setProgress(new WorkEvent(this, progress, percent));
    }

    /**
     * Called to indicate that we are finished with the dialog
     */
    public void done()
    {
        if (predicturl != null)
        {
            // We need to create a new prediction file. Work out the start point
            long start = getTimeFromProperties(current, START);
    
            // And the end point
            long end = start;
            for (Iterator it = current.keySet().iterator(); it.hasNext();)
            {
                String message = (String) it.next();
                long time = getTimeFromProperties(current, message);
                if (time > end)
                    end = time;
            }
            long length = end - start;
    
            // Now we know the start and the end we can convert all times to percents
            Properties predictions = new Properties();
            for (Iterator it = current.keySet().iterator(); it.hasNext();)
            {
                String message = (String) it.next();
                long time = getTimeFromProperties(current, message);
                long offset = time - start;
                long percent = (100L * offset) / length;
                predictions.setProperty(message, Long.toString(percent));
            }
    
            // And save. It's not a disaster if this goes wrong
            try
            {
                OutputStream out = NetUtil.getOutputStream(predicturl);
                predictions.store(out, "Predicted Startup Percentages");
            }
            catch (IOException ex)
            {
                log.error("Failed to save prediction times", ex);
            }
        }

        JobManager.setProgress(new WorkEvent(this, "Done", true));
    }

    /**
     * Accessor for the job description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Accessor for the optional work Thread
     */
    public Thread getWork()
    {
        return work;
    }

    /**
     * Interrupt the job (if possible)
     */
    public void interrupt()
    {
        if (work != null)
        {
            work.interrupt();
        }
    }

    /**
     * Might the job be interruptable?
     */
    public boolean canInterrupt()
    {
        return work != null;
    }

    /**
     * Predict a percentage complete
     */
    private long getTimeFromProperties(Properties props, String message)
    {
        long reply = 0;

        String timestr = props.getProperty(message);
        if (timestr != null)
        {
            try
            {
                reply = Long.parseLong(timestr);
            }
            catch (NumberFormatException ex)
            {
                log.error("Time format error", ex);
            }
        }

        return reply;
    }

    /**
     * Optional thread to monitor progress
     */
    private Thread work;

    /**
     * Description of what we are doing
     */
    private String description;

    /**
     * The label under which we store the start point
     */
    private static final String START = "Start";

    /**
     * The timings loaded from where they were savcd after the last run
     */
    private Properties predicted;
    
    /**
     * The timings as measured this time
     */
    private Properties current;
    
    /**
     * The URL to which we load and save timings
     */
    private URL predicturl;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Job.class);
}
