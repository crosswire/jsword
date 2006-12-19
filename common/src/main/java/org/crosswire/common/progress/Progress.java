package org.crosswire.common.progress;

public interface Progress
{
    /**
     * Indicate that the total amount of work is unknown.
     */
    static int UNKNOWN = -1;

    /**
     * Start the task.
     *
     * @param name the initial name of the job.
     * @param totalWork the total amount that is to be worked. If UNKNOWN then the progress is to be guessed.
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
     * @param name the name of the section
     */
    void setSectionName(String name);

    /**
     * We have moved onto another section so update the section title.
     */
    String getSectionName();

    /**
     * Indicate progress toward the whole.
     * 
     * @param progress a part of the whole.
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
     * Used to determine whether job is done or cancelled or reached totalWork.
     */
    boolean isFinished();

    /**
     * Might the job be cancelable?
     */
    boolean isCancelable();

    /**
     * Indicates whether the job is cancelable or not.
     * 
     * @param newCancelable The state to set.
     */
    void setCancelable(boolean newCancelable);


}
