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
package org.crosswire.common.swing;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * A DocumentWriter is-a Writer that uses a Document so all text printed
 * to the Writer ends up in the JTextArea.
 * A Document is a Container for text that supports editing and provides
 * notification of changes (serves as the model in an MVC relationship).
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DocumentWriter extends Writer
{
    /**
     * Create the DocumentWriter with no Document, that just
     * dumps the text it get into the bin
     */
    public DocumentWriter()
    {
    }

    /**
     * Create the DocumentWriter with a Document to write to
     * @param doc The destination Document
     */
    public DocumentWriter(Document doc)
    {
        this.doc = doc;
    }

    /**
     * Accessor for the Document that we are updating
     */
    public Document getDocument()
    {
        return doc;
    }

    /**
     * Accessor for the Document that we are updating
     */
    public void setDocument(Document doc)
    {
        try
        {
            flush();
        }
        catch (IOException ex)
        {
            // we just wanted to make sure that updates to the old
            // Document didn't go to the new one, so dumping the
            // exception whilst not ideal seems like the best option.
        }

        synchronized (lock)
        {
            this.doc = doc;
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] cbuf, int off, int len)
    {
        synchronized (lock)
        {
            queue = queue + new String(cbuf, off, len);
            update();
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(int)
     */
    public void write(int c)
    {
        synchronized (lock)
        {
            queue = queue + (char) c;
            update();
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[])
     */
    public void write(char[] cbuf)
    {
        synchronized (lock)
        {
            queue = queue + new String(cbuf);
            update();
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(java.lang.String)
     */
    public void write(String str)
    {
        synchronized (lock)
        {
            queue = queue + str;
            update();
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(java.lang.String, int, int)
     */
    public void write(String str, int off, int len)
    {
        synchronized (lock)
        {
            queue = queue + str.substring(off, off+len);
            update();
        }
    }

    /**
     * Set up the gui to read an update. Note this must only be called
     * from within a synchronized (lock) section of code
     */
    private void update()
    {
        if (updater == null)
        {
            updater = new Updater();
            SwingUtilities.invokeLater(updater);
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#flush()
     */
    public void flush() throws IOException
    {
        if (updater != null)
        {
            // Changes are outstanding. It is OK to force an update using
            // this method because the scheduled update will kick in
            // later, find that the queue is empty, and do nothing. No
            // problem. It would be good to cancel an update but I dont
            // know of a way to do that.
            try
            {
                SwingUtilities.invokeAndWait(updater);
            }
            catch (InterruptedException ex)
            {
                throw new IOException(""+ex); //$NON-NLS-1$
            }
            catch (InvocationTargetException ex)
            {
                throw new IOException(""+ex); //$NON-NLS-1$
            }
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#close()
     */
    public void close()
    {
        closed = true;
    }

    /**
     * The object to lock on to read or write the queue or the updater
     */
    protected Object lock = new Object();

    /**
     * The queue of strings to be added to the GUI
     */
    protected String queue = ""; //$NON-NLS-1$

    /**
     * The destination Document
     */
    protected Document doc = null;

    /**
     * The destination Document
     */
    protected boolean closed = false;

    /**
     * The updater waiting to be run
     */
    protected Updater updater = null;

    /**
     * For Thread/Swing correctness we should only update in the GUI thread
     */
    private class Updater implements Runnable
    {
        public void run()
        {
            synchronized (lock)
            {
                try
                {
                    doc.insertString(doc.getLength(), queue, null);

                    queue = ""; //$NON-NLS-1$

                    // This simply releases the pointer that our parent had
                    // to us, it does not affect how this thread is being
                    // executed. The practical effect is that any further
                    // writes know to create a new updater
                    updater = null;
                }
                catch (BadLocationException ex)
                {
                    assert false : ex;
                }
            }
        }
    }
}
