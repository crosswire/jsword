
package org.crosswire.common.progress;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Timer;

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
    protected Job(String description, URL predicturl, Thread work, boolean fakeupdates)
    {
        this.statedesc = description;
        this.jobdesc = description;
        this.predicturl = predicturl;
        this.work = work;
        this.reportedpc = 0;
        this.finished = false;

        if (fakeupdates)
        {
            Action actupdate = new PredictAction();
            updater = new Timer(100, actupdate);
            updater.start();
        }

        // Set-up the timings files. It's not a disaster if it doesn't load
        if (predicturl != null)
        {
            loadPredictions();
        }
        
        // And the predictions for next time
        current = new Properties();
        start = System.currentTimeMillis();

        JobManager.fireWorkProgressed(this, false);
    }

    /**
     * We have moved onto another section so update the section title and if
     * available used previous runs to predict timings.
     */
    public void setProgress(String statedesc)
    {
        synchronized (this)
        {
            this.statedesc = statedesc;
            if (predictedlen != 0)
            {
                this.reportedpc = 100 * getAgeFromMap(predicted, statedesc) / predictedlen;
                this.guessedpc = reportedpc;
            }
            else
            {
                this.reportedpc = 0;
                this.guessedpc = 0;
            }

            predictSection(statedesc);

            current.put(statedesc, new Integer((int) (System.currentTimeMillis()-start)));
        }

        JobManager.fireWorkProgressed(this, true);
    }

    /**
     * We have moved onto another section so update the percentage complete
     * and the section title.
     */
    public synchronized void setProgress(int percent, String statedesc)
    {
        synchronized (this)
        {
            this.statedesc = statedesc;
            this.reportedpc = percent;
            this.guessedpc = percent;
    
            predictSection(statedesc);
    
            current.put(statedesc, new Integer((int) (System.currentTimeMillis()-start)));
        }

        JobManager.fireWorkProgressed(this, false);
    }

    /**
     * Called to indicate that we are finished with the dialog
     */
    public synchronized void done()
    {
        synchronized (this)
        {
            finished = true;
            statedesc = DONE;
            reportedpc = 100;
            guessedpc = 100;

            if (updater != null)
            {
                updater.stop();
            }
    
            current.put(statedesc, new Integer((int) (System.currentTimeMillis()-start)));
        }

        JobManager.fireWorkProgressed(this, false);

        if (predicturl != null)
        {
            savePredictions();
        }
    }

    /**
     * Accessor for the job description
     */
    public String getStateDescription()
    {
        return statedesc;
    }

    /**
     * Interrupt the job (if possible)
     */
    public void interrupt()
    {
        if (work != null && !finished)
        {
            work.interrupt();
        }
    }

    /**
     * Might the job be interruptable?
     */
    public boolean canInterrupt()
    {
        return work != null && !finished;
    }

    /**
     * Shortcut to check if percent == 100
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Get estimated the percent progress, extrapolating between sections
     * @return The estimated progress
     */
    public int getPercent()
    {
        return guessedpc;
    }

    /**
     * Get the last reported total percent progress
     * @return The last reported progress
     */
    public int getReportedPercent()
    {
        return reportedpc;
    }

    /**
     * Get a short descriptive phrase
     * @return The description
     */
    public String getJobDescription()
    {
        return jobdesc;
    }

    /**
     * Predict a percentage complete
     */
    private synchronized int getAgeFromMap(Map props, String message)
    {
        if (props == null)
        {
            return 0;
        }

        Integer time = (Integer) props.get(message);
        if (time != null)
        {
            return time.intValue();
        }

        return 0;
    }

    /**
     * Get estimated the percent progress, extrapolating between sections
     */
    protected synchronized void guessProgress()
    {
        long now = System.currentTimeMillis();

        if (now < sectionstart)
        {
            log.warn("now before started: now="+new Date(now)+" started="+new Date(sectionstart));
            guessedpc = reportedpc;
            return;
        }

        if (now == sectionstart)
        {
            guessedpc = reportedpc;
            return;
        }

        if (now > sectionend)
        {
            // the prediction went wrong and we are ahead of ourselves
            guessedpc = percentend;
            return;
        }

        // how long is this section
        int sectlen = (int) (sectionend - sectionstart);
        // what percent of the way through it are we?
        int sectpc = (int) (100 * (now - sectionstart) / sectlen);
        // so what do we need to add to the current percentage
        int boost = sectpc * (percentend - reportedpc) / 100;
        // so we guess at progress at:
        int total = reportedpc + boost;
        // but check this is not more than 100
        total = total <= 100 ? total : 100;

        guessedpc = total;
    }

    /**
     * Predict a percentage complete
     */
    private synchronized void predictSection(String message)
    {
        sectionstart = System.currentTimeMillis();

        // if we have nothing to go on assume 10 sections of 10 sec each.
        if (predicted == null || predictedlen == 0)
        {
            sectionend = 10000;
            percentend = 10;
            return;
        }

        // from the predictions get this section starts and ends and the final finish
        int predsectstart = getAgeFromMap(predicted, statedesc);
        int predsectend = Integer.MAX_VALUE;

        // find better values for predsectend and predallend
        for (Iterator it = predicted.keySet().iterator(); it.hasNext();)
        {
            String title = (String) it.next();
            int age = ((Integer) predicted.get(title)).intValue();

            // if this is a later section (than the current) but early than the current earliest
            if (age > predsectstart && age < predsectend)
            {
                predsectend = age;
            }
        }
        int predsecttime = predsectend - predsectstart;

        sectionend = sectionstart + predsecttime;

        // And what is the end percentage?
        int pcstart = 100 * predsectstart / predictedlen;
        int pcend = 100 * predsectend / predictedlen;
        int pcdiff = pcend - pcstart;

        percentend = reportedpc + pcdiff;

        log.debug("Predicting "+predsecttime+"ms ("+reportedpc+"-"+percentend+"%) for section "+message);
    }

    /**
     * Load the predictive timings if any
     */
    private synchronized void loadPredictions()
    {
        try
        {
            InputStream in = predicturl.openStream();
            if (in != null)
            {
                predicted = new Properties();
                Properties temp = new Properties();
                temp.load(in);

                for (Iterator it = temp.keySet().iterator(); it.hasNext();)
                {
                    String title = (String) it.next();
                    String timestr = temp.getProperty(title);

                    try
                    {
                        Integer time = new Integer(timestr);
                        predicted.put(title, time);

                        // if this time is later than the latest
                        int age = time.intValue();
                        if (age > predictedlen)
                        {
                            predictedlen = age;
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        log.error("Time format error", ex);
                    }
                }
            }
        }
        catch (IOException ex)
        {
            log.debug("Failed to load prediction times - guessing");
        }
    }

    /**
     * Save the known timings to a properties file.
     */
    private synchronized void savePredictions()
    {
        // We need to create a new prediction file. Work out the end point
        long end = start;
        for (Iterator it = current.keySet().iterator(); it.hasNext();)
        {
            String message = (String) it.next();
            int age = getAgeFromMap(current, message);
            if (age > end)
            {
                end = age;
            }
        }
        //long length = end - start;
        
        // Now we know the start and the end we can convert all times to percents
        Properties predictions = new Properties();
        for (Iterator it = current.keySet().iterator(); it.hasNext();)
        {
            String message = (String) it.next();
            int age = getAgeFromMap(current, message);
            predictions.setProperty(message, Integer.toString(age));
        }
        
        // And save. It's not a disaster if this goes wrong
        try
        {
            OutputStream out = NetUtil.getOutputStream(predicturl);
            predictions.store(out, "Predicted Startup Times");
        }
        catch (IOException ex)
        {
            log.error("Failed to save prediction times", ex);
        }
    }

    /**
     * Have we just finished?
     */
    private boolean finished;

    /**
     * The officially reported progress
     */
    private int reportedpc;

    /**
     * The guessed progress
     */
    private int guessedpc;

    /**
     * When do we expect this section to end
     */
    private long sectionend = 0;

    /**
     * When did this section start?
     */
    private long sectionstart = 0;

    /**
     * The percentage at the end of this section
     */
    private int percentend = 0;

    /**
     * A short descriptive phrase
     */
    private String jobdesc;

    /**
     * Optional thread to monitor progress
     */
    private Thread work;

    /**
     * Description of what we are doing
     */
    private String statedesc;

    /**
     * When did this job start?
     */
    private long start = -1;

    /**
     * The timings as measured this time
     */
    private Map current;

    /**
     * The timings loaded from where they were saved after the last run
     */
    private Map predicted;
    
    /**
     * How long to we predict this job is going to last?
     */
    private int predictedlen = -1;
    
    /**
     * The URL to which we load and save timings
     */
    private URL predicturl;

    /**
     * The swing timer that lets us post fake progress events
     */
    private Timer updater = null;

    /**
     * So we can fake progress for Jobs that don't tell us how they are doing
     */
    private final class PredictAction extends AbstractAction
    {
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent ev)
        {
            guessProgress();
            JobManager.fireWorkProgressed(Job.this, true);
        }
    }

    /**
     * What we use as a label for the end of the job
     */
    private static final String DONE = "Done";

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Job.class);
}
