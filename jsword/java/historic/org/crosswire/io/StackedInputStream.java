
package org.crosswire.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.crosswire.util.Level;
import org.crosswire.util.Logger;

/**
 * This class allows you to have a stack of inputs.
 * When you try to read from the StackedInput you get
 * the input from the top of the stack. When that is
 * exausted, we move on to the next.
 * @author Joe Walker
 */
public class StackedInputStream extends InputStream
{
    /**
     * Add a new InputStream to the stack.
     * This Stream then becomes the current.
     * @param in The new Stream to be added.
     */
    public StackedInputStream push(InputStream in)
    {
        if (current != null)
        {
            list.addElement(current);
        }

        current = in;
        return this;
    }

    /**
     * Remove the bottom-most stream from the stack.
     * @return The Stream that has been removed.
     */
    public InputStream pop() throws IOException
    {
        InputStream dead = null;

        if (current == null)
        {
            throw new IOException();
        }

        if (list.size() == 0)
        {
            current = null;
        }
        else
        {
            dead = current;
            current = (InputStream) list.lastElement();
            list.removeElement(current);
        }

        return dead;
    }

    /**
     * Override to pass out to the current Stream.
     * @return The byte read, as normal.
     */
    public int read() throws IOException
    {
        while (true)
        {
            if (current == null) return -1;

            int retcode = current.read();

            if (retcode == -1)
            {
                InputStream dead = pop();
                dead.close();
            }
            else
            {
                return retcode;
            }
        }
    }

    /**
     * If someone closes the StackedInputStream then we go round
     * and close all the Streams on the stack.
     */
    public void close() throws IOException
    {
        // Close each InputStream catching and noting IOExceptions
        // Then rethrow at end if any failed.
        boolean failed = false;

        for (int i=0; i<list.size(); i++)
        {
            try
            {
                InputStream in = (InputStream) list.elementAt(i);
                in.close();
            }
            catch (Exception ex)
            {
                log.log(Level.INFO, "Failure during close", ex);
                failed = true;
            }
        }

        list.removeAllElements();

        if (failed) throw new IOException();
    }

    /**
     * @return The number of items on the stack
     */
    public int size()
    {
        if (current == null)	return 0;
        else					return list.size() + 1;
    }

    /**
     * Primarily for debugging. Reports on th state of the Stream.
     * @return A String containing the report.
     */
    public String toString()
    {
        String retcode = "";
        String NEWLINE = System.getProperty("line.separator", "\r\n");

        retcode += "There are " + list.size() + " input(s)" + NEWLINE;
        retcode += "Curr: " + current.toString() + NEWLINE;

        for (int i=list.size()-1; i>=0; i--)
        {
            InputStream in = (InputStream) list.elementAt(i);
            retcode += "Next: " + in.toString() + NEWLINE;
        }

        return retcode;
    }

    private Vector list = new Vector();
    private InputStream current;

    /** The log stream */
    protected static Logger log = Logger.getLogger("util.swing");
}
