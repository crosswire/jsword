package org.crosswire.io;

import java.io.*;

/**
 * NullWriter allows you to write to /dev/null
 * @author Joe Walker
 */
public class NullWriter extends Writer
{
    /**
     * Override write to ask the listed Streams.
     * @param b The byte to be written, as normal.
     */
    public void write(char[] cbuf, int off, int len) throws IOException
    {
    }

    /**
     * Override write to ask the listed Streams.
     * @param b The byte to be written, as normal.
     */
    public void write(int b) throws IOException
    {
    }

    /**
     * Override flush to flush the listed Streams.
     */
    public void flush() throws IOException
    {
    }

    /**
     * If someone closes the TeeWriter then we go round
     * and close all the Streams on the stack.
     */
    public void close() throws IOException
    {
    }
}
