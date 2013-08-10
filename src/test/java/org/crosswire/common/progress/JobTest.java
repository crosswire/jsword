/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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
 */
package org.crosswire.common.progress;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JobTest extends TestCase {

    private static final String WIBBLE = "wibble";

    /**
     * Constructor for JobTest.
     * 
     * @param arg0
     */
    public JobTest(String arg0) {
        super(arg0);
    }

    public void testJob() throws IOException {
        Progress job;
        File tempfile = File.createTempFile("jobtest", "tmp");
        URI uri = tempfile.toURI();

        job = JobManager.createJob(WIBBLE);
        job.beginJob(WIBBLE);

        assertEquals(WIBBLE, job.getJobName());
        assertEquals(false, job.isFinished());
        assertEquals(false, job.isCancelable());
        assertEquals(WIBBLE, job.getSectionName());
        assertEquals(0, job.getWork());
        job.done();
        assertEquals(true, job.isFinished());
        assertEquals(100, job.getWork());
        assertEquals(job.isCancelable(), false);

        job = JobManager.createJob(UUID.randomUUID().toString(), WIBBLE, Thread.currentThread());
        job.beginJob(WIBBLE);
        assertEquals(WIBBLE, job.getJobName());
        assertEquals(false, job.isFinished());
        assertEquals(true, job.isCancelable());
        assertEquals(WIBBLE, job.getSectionName());
        assertEquals(0, job.getWork());
        job.done();
        assertEquals(true, job.isFinished());
        assertEquals(100, job.getWork());
        // assertEquals(job.isCancelable(), false);

        job = JobManager.createJob(WIBBLE);
        job.beginJob(WIBBLE, uri);
        job.setTotalWork(100);
        assertEquals(WIBBLE, job.getJobName());
        assertEquals(false, job.isFinished());
        assertEquals(false, job.isCancelable());
        assertEquals(WIBBLE, job.getSectionName());
        job.cancel();
        job.done();
        assertEquals(true, job.isFinished());
        assertEquals(100, job.getWork());
        // assertEquals(false, job.isCancelable());

        job = JobManager.createJob(UUID.randomUUID().toString(), WIBBLE, Thread.currentThread());
        job.beginJob(WIBBLE, uri);
        assertEquals(WIBBLE, job.getJobName());
        assertEquals(false, job.isFinished());
        assertEquals(true, job.isCancelable());
        assertEquals(WIBBLE, job.getSectionName());
        job.done();
        assertEquals(true, job.isFinished());
        assertEquals(100, job.getWork());
        // assertEquals(false, job.isCancelable());
    }
}
