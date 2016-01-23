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
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.common.progress;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
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

        Assert.assertEquals(WIBBLE, job.getJobName());
        Assert.assertFalse(job.isFinished());
        Assert.assertFalse(job.isCancelable());
        Assert.assertEquals(WIBBLE, job.getSectionName());
        Assert.assertEquals(0, job.getWork());
        job.done();
        Assert.assertTrue(job.isFinished());
        Assert.assertEquals(100, job.getWork());
        Assert.assertFalse(job.isCancelable());

        job = JobManager.createJob(UUID.randomUUID().toString(), WIBBLE, Thread.currentThread());
        job.beginJob(WIBBLE);
        Assert.assertEquals(WIBBLE, job.getJobName());
        Assert.assertFalse(job.isFinished());
        Assert.assertTrue(job.isCancelable());
        Assert.assertEquals(WIBBLE, job.getSectionName());
        Assert.assertEquals(0, job.getWork());
        job.done();
        Assert.assertTrue(job.isFinished());
        Assert.assertEquals(100, job.getWork());
        // Assert.assertEquals(job.isCancelable(), false);

        job = JobManager.createJob(WIBBLE);
        job.beginJob(WIBBLE, uri);
        job.setTotalWork(100);
        Assert.assertEquals(WIBBLE, job.getJobName());
        Assert.assertFalse(job.isFinished());
        Assert.assertFalse(job.isCancelable());
        Assert.assertEquals(WIBBLE, job.getSectionName());
        job.cancel();
        job.done();
        Assert.assertTrue(job.isFinished());
        Assert.assertEquals(100, job.getWork());
        // Assert.assertFalse(job.isCancelable());

        job = JobManager.createJob(UUID.randomUUID().toString(), WIBBLE, Thread.currentThread());
        job.beginJob(WIBBLE, uri);
        Assert.assertEquals(WIBBLE, job.getJobName());
        Assert.assertFalse(job.isFinished());
        Assert.assertTrue(job.isCancelable());
        Assert.assertEquals(WIBBLE, job.getSectionName());
        job.done();
        Assert.assertTrue(job.isFinished());
        Assert.assertEquals(100, job.getWork());
        // Assert.assertFalse(job.isCancelable());
    }
}
