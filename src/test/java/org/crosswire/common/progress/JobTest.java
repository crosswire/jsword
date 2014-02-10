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
 * Copyright: 2005 - 2014
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.common.progress;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith
 */
public class JobTest {

    private static final String WIBBLE = "wibble";

    @Test
    public void testJob() throws IOException {
        Progress job;
        File tempfile = File.createTempFile("jobtest", "tmp");
        URI uri = tempfile.toURI();

        job = JobManager.createJob(WIBBLE);
        job.beginJob(WIBBLE);

        assertEquals(WIBBLE, job.getJobName());
        assertFalse(job.isFinished());
        assertFalse(job.isCancelable());
        assertEquals(WIBBLE, job.getSectionName());
        assertEquals(0, job.getWork());
        job.done();
        assertTrue(job.isFinished());
        assertEquals(100, job.getWork());
        assertFalse(job.isCancelable());

        job = JobManager.createJob(UUID.randomUUID().toString(), WIBBLE, Thread.currentThread());
        job.beginJob(WIBBLE);
        assertEquals(WIBBLE, job.getJobName());
        assertFalse(job.isFinished());
        assertTrue(job.isCancelable());
        assertEquals(WIBBLE, job.getSectionName());
        assertEquals(0, job.getWork());
        job.done();
        assertTrue(job.isFinished());
        assertEquals(100, job.getWork());
        // assertEquals(job.isCancelable(), false);

        job = JobManager.createJob(WIBBLE);
        job.beginJob(WIBBLE, uri);
        job.setTotalWork(100);
        assertEquals(WIBBLE, job.getJobName());
        assertFalse(job.isFinished());
        assertFalse(job.isCancelable());
        assertEquals(WIBBLE, job.getSectionName());
        job.cancel();
        job.done();
        assertTrue(job.isFinished());
        assertEquals(100, job.getWork());
        // assertFalse(job.isCancelable());

        job = JobManager.createJob(UUID.randomUUID().toString(), WIBBLE, Thread.currentThread());
        job.beginJob(WIBBLE, uri);
        assertEquals(WIBBLE, job.getJobName());
        assertFalse(job.isFinished());
        assertTrue(job.isCancelable());
        assertEquals(WIBBLE, job.getSectionName());
        job.done();
        assertTrue(job.isFinished());
        assertEquals(100, job.getWork());
        // assertFalse(job.isCancelable());
    }
}
