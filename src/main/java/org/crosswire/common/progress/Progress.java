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

import java.net.URI;

/**
 * A Generic way of keeping track of Threads and monitoring their progress.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author DM Smith
 */
public interface Progress {
    /**
     * Indicate that the total amount of work is unknown.
     */
    int UNKNOWN = -1;
    String INSTALL_BOOK = "INSTALL_BOOK-%s";
    String RELOAD_BOOK_LIST = "RELOAD_BOOK_LIST";
    String DOWNLOAD_SEARCH_INDEX = "DOWNLOAD_SEARCH_INDEX-%s";
    String CREATE_INDEX = "CREATE_INDEX-%s";

    /**
     * Start the task measured from 0 to 100. It is the caller's responsibility to compute percentages.
     * 
     * @param sectionName
     *            the initial name of the job.
     */
    void beginJob(String sectionName);

    /**
     * Start the task reporting progress toward total work. Percentages will be
     * computed on behalf of the caller.
     * 
     * @param sectionName
     *            the initial name of the job.
     * @param totalWork
     *            the total amount that is to be worked.
     */
    void beginJob(String sectionName, int totalWork);

    /**
     * Start the task using timings from a prior run as a guess for the current
     * run. If there are no predictions then progress is faked.
     * 
     * @param sectionName
     *            the initial name of the job.
     * @param predictURI
     *            the URI of a properties file from which past behavior is read
     *            and the results of the current run are stored.
     */
    void beginJob(String sectionName, URI predictURI);

    /**
     * @return the job name
     */
    String getJobName();

    /**
     * @return the unique ID of the job
     */
    String getJobID();

    /**
     * Gets the current ProgressMode. Builders of progress bars should use
     * an indeterminant progress bar for ProgressMode.UNKNOWN and ProgressMode.PREDICTIVE.
     * @return the current progress mode.
     */
    ProgressMode getProgressMode();

    /**
     * @return the total amount of work to be done, or UNKNOWN if it not known
     */
    int getTotalWork();

    /**
     * Set the total amount of work to be done. This can be called any time. It
     * is the responsibility of the caller for it to be meaningful. It is
     * ignored when the task is started with a prediction properties file.
     * 
     * @param totalWork
     *            the total amount of work to be done in units that make sense
     *            to the caller.
     */
    void setTotalWork(int totalWork);

    /**
     * Return the computed percentage as an integer, typically from 0 to 100.
     * 
     * @return the amount of work done as a percentage
     */
    int getWork();

    /**
     * Indicate progress toward 100%. Note this is just a call to setWorkDone.
     * 
     * @param progress
     *            a part of the whole.
     */
    void setWork(int progress);

    /**
     * @return the amount of work done so far as reported by the caller
     */
    int getWorkDone();

    /**
     * Indicate progress toward the whole. It is up to the caller to give a
     * value that makes sense. When using 100 as a basis, it is typically a
     * number from 0 to 100. In every case, it is a number from 0 to totalWork.
     * 
     * @param progress
     *            a part of the whole.
     */
    void setWorkDone(int progress);

    /**
     * Indicate progress toward the whole. It is up to the caller to give a
     * value that makes sense.
     * 
     * @param step
     *            the amount of work done since the last call.
     */
    void incrementWorkDone(int step);

    /**
     * The section name is used in reporting progress.
     * 
     * @return the current section name
     */
    String getSectionName();

    /**
     * We have moved onto another section so update the section title. The section name is used in reporting progress.
     * 
     * @param name
     *            the name of the section
     */
    void setSectionName(String name);

    /**
     * Called to indicate that we are finished doing work.
     */
    void done();

    /**
     * Cancel the job (if possible). If isCancelable() is false, then the job
     * will be canceled if cancelable becomes true. There is no guarantee that
     * 
     */
    void cancel();

    /**
     * Used to determine whether job is done or canceled or reached totalWork.
     * 
     * @return true if finished
     */
    boolean isFinished();

    /**
     * Might the job be cancelable?
     * 
     * @return true if the job can be cancelled
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
