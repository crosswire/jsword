
package org.crosswire.jsword.book.basic;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.ArrayList;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.NetUtil;

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
public abstract class VersewiseBible extends AbstractWritableBible
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
     * Setup the Version information
     * @param version The version that this Bible is becoming
     */
    public abstract void setVersion(BookMetaData version);

    /**
     * Flush the data written to disk
     */
    public void flush() throws BookException
    {
    }

    /**
     * Get a URL in which we can save a generation report
     * @return A directory URL into which we can save files
     */
    public abstract URL getBaseURL();

    /**
     * Read from the given source version to generate ourselves
     * @param version The source
     */
    public void generate(Bible version) throws BookException
    {
        try
        {
            URL url = NetUtil.lengthenURL(getBaseURL(), "generate.log");
            PrintWriter out = new PrintWriter(NetUtil.getOutputStream(url));

            out.println("Generating Bible");
            out.println("Dest driver = "+getClass().getName());
            out.println("Dest url = "+getBaseURL());
            out.println("Source name = "+version.getMetaData().getName());
            out.println("Source driver = "+version.getClass().getName());
            out.flush();

            // Generate
            setVersion(version.getMetaData());
            generatePassages(version);
            generateText(version);
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
    protected void generateText(Bible source) throws IOException, NoSuchVerseException, BookException
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
            fireProgressMade("Writing Verses:", 100 * verse.getOrdinal() / Books.versesInBible());

            // Read the document from the original version
            BibleData doc = source.getData(temp);

            // Write the document to the mutable version
            setDocument(doc);

            // This could take a long time ...
            Thread.currentThread().yield();
            if (Thread.currentThread().isInterrupted())
                break;
        }
    }

    /**
     * Read from the given source version to generate ourselves
     * @param version The source
     */
    protected void generatePassages(Bible source) throws IOException, NoSuchVerseException, BookException
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
            fireProgressMade("Writing Words:", 100 * count++ / Verifier.GUESS_WORDS);

            // This could take a long time ...
            Thread.currentThread().yield();
            if (Thread.currentThread().isInterrupted())
                break;
        }
    }

    /** The Whole Bible */
    private static Passage whole = PassageFactory.getWholeBiblePassage();
}
