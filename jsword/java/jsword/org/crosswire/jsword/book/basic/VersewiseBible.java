
package org.crosswire.jsword.book.basic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.events.ProgressEvent;
import org.crosswire.jsword.book.events.ProgressListener;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;

/**
 * The VersewiseMutableBook class makes it easier to implement
 * MutableBook by splitting the job up into a Verse by Verse effort.
 * setDocument() is called once for every verse in the Bible in order,
 * and flush() is called when done.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class VersewiseBible extends AbstractBible
{
    /**
     * Write the XML to disk. Children will almost certainly want to
     * override this.
     * @param verse The verse to write
     * @param text The data to write
     */
    public abstract void setDocument(BibleData text) throws BookException;

    /**
     * Save a list of found words. Children will probably want to
     * override this.
     * @param word The word to write
     * @param ref The data to write
     */
    public abstract void foundPassage(String word, Passage ref) throws BookException;

    /**
     * Flush the data written to disk
     */
    public void flush() throws BookException
    {
    }

    /**
     * Read from the given source version to generate ourselves
     * @param version The source
     */
    public void generate(Bible source, ProgressListener li) throws BookException
    {
        try
        {
            log.debug("Generating Bible");
            log.debug("Dest driver = "+getClass().getName());
            log.debug("Source name = "+source.getBookMetaData().getName());
            log.debug("Source driver = "+source.getClass().getName());

            // Generate
            generatePassages(source, li);
            generateText(source, li);
            flush();
        }
        catch (IOException ex)
        {
            throw new BookException("book_gen", ex);
        }
        catch (NoSuchVerseException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Read from the given source version to generate ourselves
     * @param version The source
     */
    protected void generateText(Bible source, ProgressListener li) throws IOException, NoSuchVerseException, BookException
    {
        Passage temp = PassageFactory.createPassage(PassageFactory.SPEED);

        // For every verse in the Bible
        Iterator it = whole.verseIterator();
        while (it.hasNext())
        {
            // Create a Passage containing that verse alone
            Verse verse = (Verse) it.next();
            temp.clear();
            temp.add(verse);

            // Fire a progress event?
            li.progressMade(new ProgressEvent(this, "Writing Verses:", 100 * verse.getOrdinal() / Books.versesInBible()));

            // Read the document from the original version
            BibleData doc = source.getData(temp);

            // Write the document to the mutable version
            setDocument(doc);

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
                break;
        }
    }

    /**
     * Read from the given source version to generate ourselves
     * @param version The source
     */
    protected void generatePassages(Bible source, ProgressListener li) throws IOException, NoSuchVerseException, BookException
    {
        int count = 0;

        // @todo: reimplement this to take a note of the words as they flash
        // past while we are generating the text data.

        // For every word in the word list
        //Enumeration en = source.listWords();
        Iterator it = new ArrayList().iterator();
        while (it.hasNext())
        {
            // Read and write
            String word = (String) it.next();
            Passage ref_source = source.findPassage(word);
            foundPassage(word, ref_source);

            // Fire a progress event?
            li.progressMade(new ProgressEvent(this, "Writing Words:", 100 * count++ / Verifier.GUESS_WORDS));

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
                break;
        }
    }

    /** The log stream */
    protected static Logger log = Logger.getLogger(VersewiseBible.class);

    /** The Whole Bible */
    private static Passage whole = PassageFactory.getWholeBiblePassage();
}
