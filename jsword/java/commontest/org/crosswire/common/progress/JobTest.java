package org.crosswire.common.progress;

import java.net.MalformedURLException;
import java.net.URL;

import org.crosswire.jsword.util.Project;

import junit.framework.TestCase;

/**
 * JUnit Test.
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
public class JobTest extends TestCase
{

    /**
     * Constructor for JobTest.
     * @param arg0
     */
    public JobTest(String arg0)
    {
        super(arg0);
    }

    public void testJob() throws MalformedURLException
    {
        Job job;
        URL url = Project.instance().getWritablePropertiesURL("splash");

        job = JobManager.createJob("wibble", false);
        assertEquals(job.getJobDescription(), "wibble");
        assertEquals(job.isFinished(), false);
        assertEquals(job.canInterrupt(), false);
        assertEquals(job.getStateDescription(), "wibble");
        assertEquals(job.getPercent(), 0);
        assertEquals(job.getReportedPercent(), 0);
        job.done();
        assertEquals(job.isFinished(), true);
        assertEquals(job.getPercent(), 100);
        assertEquals(job.getReportedPercent(), 100);
        assertEquals(job.canInterrupt(), false);

        job = JobManager.createJob("wibble", Thread.currentThread(), false);
        assertEquals(job.getJobDescription(), "wibble");
        assertEquals(job.isFinished(), false);
        assertEquals(job.canInterrupt(), true);
        assertEquals(job.getStateDescription(), "wibble");
        assertEquals(job.getPercent(), 0);
        assertEquals(job.getReportedPercent(), 0);
        job.done();
        assertEquals(job.isFinished(), true);
        assertEquals(job.getPercent(), 100);
        assertEquals(job.getReportedPercent(), 100);
        assertEquals(job.canInterrupt(), false);

        job = JobManager.createJob("wibble", url, false);
        assertEquals(job.getJobDescription(), "wibble");
        assertEquals(job.isFinished(), false);
        assertEquals(job.canInterrupt(), false);
        assertEquals(job.getStateDescription(), "wibble");
        assertEquals(job.getPercent(), 0);
        assertEquals(job.getReportedPercent(), 0);
        job.done();
        assertEquals(job.isFinished(), true);
        assertEquals(job.getPercent(), 100);
        assertEquals(job.getReportedPercent(), 100);
        assertEquals(job.canInterrupt(), false);

        job = JobManager.createJob("wibble", url, Thread.currentThread(), false);
        assertEquals(job.getJobDescription(), "wibble");
        assertEquals(job.isFinished(), false);
        assertEquals(job.canInterrupt(), true);
        assertEquals(job.getStateDescription(), "wibble");
        assertEquals(job.getPercent(), 0);
        assertEquals(job.getReportedPercent(), 0);
        job.done();
        assertEquals(job.isFinished(), true);
        assertEquals(job.getPercent(), 100);
        assertEquals(job.getReportedPercent(), 100);
        assertEquals(job.canInterrupt(), false);
    }

    /*
     * Test for void setProgress(String)
     */
    public void testSetProgressString()
    {
        //NOTE: Implement setProgress().
    }

    /*
     * Test for void setProgress(int, String)
     */
    public void testSetProgressintString()
    {
        //NOTE: Implement setProgress().
    }

    public void testPredictProgress()
    {
        //NOTE: Implement predictProgress().
    }

    public void testDone()
    {
        //NOTE: Implement done().
    }

    public void testInterrupt()
    {
        //NOTE: Implement interrupt().
    }
}
