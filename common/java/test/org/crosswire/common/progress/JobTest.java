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
 * ID: $ID$
 */
package org.crosswire.common.progress;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.crosswire.common.util.NetUtil;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JobTest extends TestCase
{

    private static final String WIBBLE = "wibble"; //$NON-NLS-1$
    /**
     * Constructor for JobTest.
     * @param arg0
     */
    public JobTest(String arg0)
    {
        super(arg0);
    }

    public void testJob() throws IOException
    {
        Job job;
        File tempfile = File.createTempFile("jobtest", "tmp"); //$NON-NLS-1$ //$NON-NLS-2$
        URL url = new URL(NetUtil.PROTOCOL_FILE, null, tempfile.getAbsolutePath());

        job = JobManager.createJob(WIBBLE, false);
        assertEquals(job.getJobDescription(), WIBBLE);
        assertEquals(job.isFinished(), false);
        assertEquals(job.isInterruptable(), false);
        assertEquals(job.getStateDescription(), WIBBLE);
        assertEquals(job.getPercent(), 0);
        assertEquals(job.getReportedPercent(), 0);
        job.done();
        assertEquals(job.isFinished(), true);
        assertEquals(job.getPercent(), 100);
        assertEquals(job.getReportedPercent(), 100);
        assertEquals(job.isInterruptable(), false);

        job = JobManager.createJob(WIBBLE, Thread.currentThread(), false);
        assertEquals(job.getJobDescription(), WIBBLE);
        assertEquals(job.isFinished(), false);
        assertEquals(job.isInterruptable(), true);
        assertEquals(job.getStateDescription(), WIBBLE);
        assertEquals(job.getPercent(), 0);
        assertEquals(job.getReportedPercent(), 0);
        job.done();
        assertEquals(job.isFinished(), true);
        assertEquals(job.getPercent(), 100);
        assertEquals(job.getReportedPercent(), 100);
//        assertEquals(job.isInterruptable(), false);

        job = JobManager.createJob(WIBBLE, url, false);
        assertEquals(job.getJobDescription(), WIBBLE);
        assertEquals(job.isFinished(), false);
        assertEquals(job.isInterruptable(), false);
        assertEquals(job.getStateDescription(), WIBBLE);
        assertEquals(job.getPercent(), 0);
        assertEquals(job.getReportedPercent(), 0);
        job.done();
        assertEquals(job.isFinished(), true);
        assertEquals(job.getPercent(), 100);
        assertEquals(job.getReportedPercent(), 100);
//        assertEquals(job.isInterruptable(), false);

        job = JobManager.createJob(WIBBLE, url, Thread.currentThread(), false);
        assertEquals(job.getJobDescription(), WIBBLE);
        assertEquals(job.isFinished(), false);
        assertEquals(job.isInterruptable(), true);
        assertEquals(job.getStateDescription(), WIBBLE);
        assertEquals(job.getPercent(), 0);
        assertEquals(job.getReportedPercent(), 0);
        job.done();
        assertEquals(job.isFinished(), true);
        assertEquals(job.getPercent(), 100);
        assertEquals(job.getReportedPercent(), 100);
//        assertEquals(job.isInterruptable(), false);
    }
}
