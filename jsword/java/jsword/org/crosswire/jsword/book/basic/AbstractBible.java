
package org.crosswire.jsword.book.basic;

import java.util.Iterator;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.PassageKey;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;

/**
 * An AbstractBible implements a few of the more generic methods of Bible.
 * This class does a lot of work in helping make search easier, and implementing
 * some basic write methods. 
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
public abstract class AbstractBible implements Bible
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#activate()
     */
    public void activate()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#deactivate()
     */
    public void deactivate()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getBookMetaData()
     */
    public BookMetaData getBookMetaData()
    {
        return getBibleMetaData();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getKey(java.lang.String)
     */
    public Key getKey(String text) throws BookException
    {
        try
        {
            return new PassageKey(text);
        }
        catch (NoSuchVerseException ex)
        {
            throw new BookException(Msg.NO_VERSE);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#find(org.crosswire.jsword.book.Search)
     */
    public Key find(Search search) throws BookException
    {
        Passage ref = findPassage(search);
        return new PassageKey(ref);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.book.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        if (key instanceof PassageKey)
        {
            Passage ref = ((PassageKey) key).getPassage();
            return getData(ref);
        }
        else
        {
            return null;
        }
    }

    /**
     * Read from the given source version to generate ourselves
     * @param source The Bible to read data from
     * @throws BookException If generation fails
     */
    public void generateText(Bible source) throws BookException
    {
        Passage temp = PassageFactory.createPassage(PassageFactory.SPEED);

        Job job = JobManager.createJob("Copying Bible data to new driver", Thread.currentThread(), false);
        int percent = -1;

        // For every verse in the Bible
        Iterator it = WHOLE.verseIterator();
        while (it.hasNext())
        {
            // Create a Passage containing that verse alone
            Verse verse = (Verse) it.next();
            temp.clear();
            temp.add(verse);

            // Fire a progress event?
            int newpercent = 100 * verse.getOrdinal() / BibleInfo.versesInBible(); 
            if (percent != newpercent)
            {
                percent = newpercent;
                job.setProgress(percent, "Writing Verses");
            }

            // Read the document from the original version
            BookData doc = source.getData(temp);

            // Write the document to the mutable version
            setDocument(verse, doc);

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
            {
                break;
            }
        }
    }

    /**
     * Write the XML to disk. Children will almost certainly want to
     * override this.
     * @param verse The verse to write
     * @param text The data to write
     */
    public void setDocument(Verse verse, BookData text) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY, new Object[] { verse.toString(), text.getPlainText() });
    }

    /**
     * The Whole Bible
     */
    private static final Passage WHOLE = PassageFactory.getWholeBiblePassage();
}
