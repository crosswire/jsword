/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
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
 * ID: $Id$
 */
package org.crosswire.common.progress;

/**
 * A Generic way of keeping track of Threads and monitoring their progress.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public interface Progress {
    /**
     * Indicate that the total amount of work is unknown.
     */
    int UNKNOWN = -1;

    /**
     * Start the task.
     * 
     * @param name
     *            the initial name of the job.
     * @param totalWork
     *            the total amount that is to be worked. If UNKNOWN then the
     *            progress is to be guessed.
     */
    void beginJob(String name, int totalWork);

    /**
     * @return the job name
     */
    String getJobName();

    /**
     * @return the total amount of work to be done
     */
    int getTotalWork();

    /**
     * Called to indicate that we are finished doing work.
     */
    void done();

    /**
     * We have moved onto another section so update the section title.
     * 
     * @param name
     *            the name of the section
     */
    void setSectionName(String name);

    /**
     * We have moved onto another section so update the section title.
     */
    String getSectionName();

    /**
     * Indicate progress toward the whole.
     * 
     * @param progress
     *            a part of the whole.
     */
    void setWork(int progress);

    /**
     * @return the amount of work done so far, possibly estimated
     */
    int getWork();

    /**
     * Cancel the job (if possible). If isCancelable() is false, then the job
     * will be canceled if cancelable becomes true. There is no guarantee that
     * 
     */
    void cancel();

    /**
     * Used to determine whether job is done or canceled or reached totalWork.
     */
    boolean isFinished();

    /**
     * Might the job be cancelable?
     */
    boolean isCancelable();

    /**
     * Indicates whether the job is cancelable or not.
     * 
     * @param newCancelable
     *            The state to set.
     */
    void setCancelable(boolean newCancelable);

}
