
package org.crosswire.io;

import java.io.IOException;
import java.io.Reader;

/**
 * TimeoutReader is a simple reader that unblocks (excepts)
 * after a specified timeout
 * @author Joe Walker
 */
public class TimeoutReader extends Reader implements Runnable
{
    /**
     * Use the specified Reader as the source
     * @param in The Stream to take input from
     * @param timeout The time (in ms) to wait for input
     * @throws IOException if hell breaks loose
     */
    public TimeoutReader(Reader in, int timeout)
    {
        this.in = in;
        this.timeout = timeout;
    }

    /**
     * Setup the timeout
     */
    public void run()
    {
        try
        {
            work.sleep(timeout);

            // Kill the thread that is blocked
            if (calling != null)
            {
                // This was borken at JDK 1.1.3
                calling.interrupt();

                // This is deprecated as of JDK 1.2
                // calling.stop(new InterruptedException("Timeout"));
            }
        }
        catch (Exception ex)
        {
            // for some reason we were prevented from killing
            // so we just do nothing
        }
    }

    /**
     * @return The byte to be read, as normal.
     */
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        calling = Thread.currentThread();

        work = new Thread(this);
        work.start();

        try
        {
            int read = in.read(cbuf, off, len);

            calling = null;
            work.stop();
            work = null;

            return read;
        }
        catch (Exception ex)
        {
            // I would like to catch InterruptedException however the stupid
            // compiler thinks that InterruptedException can never be
            // thrown in the preceeding block. Dumb compiler in my opinion.

            if (ex instanceof IOException)
                throw (IOException) ex;

            throw new IOException(ex.getMessage());
        }
    }

    /**
     * If someone closes the TimeoutReader then we close the
     * original, but leave the others, as they might not have finished
     * yet.
     */
    public void close() throws IOException
    {
        if (work == null)
        {
            // Kill Thread
        }

        in.close();
    }

    /**
     * Accessor for the timeout.
     * Works only for the next read. Does not change the current.
     */
    public void timeoutNow()
    {
        if (work == null)
        {
            // Kill Thread
        }
    }

    /**
     * Accessor for the timeout.
     * Works only for the next read. Does not change the current.
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    /**
     * Accessor for the timeout.
     */
    public int getTimeout()
    {
        return timeout;
    }

    private Reader in;
    private int timeout;
    private Thread work;
    private Thread calling;
}
