
package org.crosswire.common.swing;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.crosswire.common.util.LogicError;

/**
 * A DocumentWriter is-a Writer that uses a Document so all text printed
 * to the Writer ends up in the JTextArea.
 * A Document is a Container for text that supports editing and provides
 * notification of changes (serves as the model in an MVC relationship).
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
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
     * @return The new document
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

    /**
     * Write a portion of an array of characters.
     * @param cbuf Array of characters
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     * @exception IOException If an I/O error occurs
     */
    public void write(char[] cbuf, int off, int len) throws IOException
    {
        synchronized (lock)
        {
            queue = queue + new String(cbuf, off, len);
            update();
        }
    }

    /**
     * Write a single character.  The character to be written is contained in
     * the 16 low-order bits of the given integer value; the 16 high-order bits
     * are ignored.
     * <p> Subclasses that intend to support efficient single-character output
     * should override this method.
     * @exception  IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException
    {
        synchronized (lock)
        {
            queue = queue + (char) c;
            update();
        }
    }

    /**
     * Write an array of characters.
     * @param cbuf Array of characters to be written
     * @exception IOException If an I/O error occurs
     */
    public void write(char[] cbuf) throws IOException
    {
        synchronized (lock)
        {
            queue = queue + new String(cbuf);
            update();
        }
    }

    /**
     * Write a string.
     * @param str String to be written
     * @exception IOException If an I/O error occurs
     */
    public void write(String str) throws IOException
    {
        synchronized (lock)
        {
            queue = queue + str;
            update();
        }
    }

    /**
     * Write a portion of a string.
     * @param str A String
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     * @exception IOException If an I/O error occurs
     */
    public void write(String str, int off, int len) throws IOException
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

    /**
     * Flush the stream.  If the stream has saved any characters from the
     * various write() methods in a buffer, write them immediately to their
     * intended destination.  Then, if that destination is another character or
     * byte stream, flush it.  Thus one flush() invocation will flush all the
     * buffers in a chain of Writers and OutputStreams.
     * @exception  IOException  If an I/O error occurs
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
                throw new IOException(""+ex);
            }
            catch (InvocationTargetException ex)
            {
                throw new IOException(""+ex);
            }
        }
    }

    /**
     * Close the stream, flushing it first.  Once a stream has been closed,
     * further write() or flush() invocations will cause an IOException to be
     * thrown.  Closing a previously-closed stream, however, has no effect.
     * @exception IOException If an I/O error occurs
     */
    public void close() throws IOException
    {
        closed = true;
    }

    /** The object to lock on to read or write the queue or the updater */
    protected Object lock = new Object();

    /** The queue of strings to be added to the GUI */
    protected String queue = "";

    /** The destination Document */
    protected Document doc = null;

    /** The destination Document */
    protected boolean closed = false;

    /** The updater waiting to be run */
    protected Updater updater = null;

    /**
     * For Thread/Swing correctness we should only update in the GUI thread
     */
    class Updater implements Runnable
    {
        public void run()
        {
            synchronized (lock)
            {
                try
                {
                    doc.insertString(doc.getLength(), queue, null);
                }
                catch (BadLocationException ex)
                {
                    throw new LogicError();
                }

                queue = "";

                // This simply releases the pointer that our parent had
                // to us, it does not affect how this thread is being
                // executed. The practical effect is that any further
                // writes know to create a new updater
                updater = null;
            }
        }
    }
}
