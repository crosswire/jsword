package org.crosswire.jsword.book.basic;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * The Verifier check 2 versions for identical text.
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
public class Verifier
{
    /**
     * Constructor that sets up the Bibles as well.
     */
    public Verifier(Book book1, Book book2)
    {
        setBible1(book1);
        setBible2(book2);
    }

    /**
     * The first Bible that we are checking, this is supposed to be the
     * more accurate of the 2 Bibles, so we use this as a source of the
     * words to check.
     * @param bible1 A Bible to check
     */
    public final void setBible1(Book book1)
    {
        this.book1 = book1;
    }

    /**
     * The first Bible that we are checking
     * @return A Bible to check
     */
    public final Book getBible1()
    {
        return book1;
    }

    /**
     * The second Bible that we are checking, this is supposed to be the
     * less accurate, or more recent of the 2 Bibles, so we use this in
     * firing ProgressEvents.
     * @param bible2 A Bible to check
     */
    public final void setBible2(Book book2)
    {
        this.book2 = book2;
    }

    /**
     * The second Bible that we are checking
     * @return A Bible to check
     */
    public final Book getBible2()
    {
        return book2;
    }

    /**
     * Read from the given source version to generate ourselves
     */
    public void checkText(PrintWriter out)
    {
        checkText(WHOLE, out);
    }

    /**
     * Read from the given source version to generate ourselves
     */
    public void checkText(Passage ref, PrintWriter out)
    {
        Job job = JobManager.createJob("Copying Bible data to new driver", Thread.currentThread(), false);
        int percent = 0;

        // For every verse in the Bible
        Iterator it = ref.verseIterator();
        while (it.hasNext())
        {
            Verse verse = (Verse) it.next();
            VerseRange range = new VerseRange(verse);
            Passage ref2 = PassageFactory.createPassage();
            ref2.add(range);

            // Fire a progress event?
            int newpercent = 100 * verse.getOrdinal() / BibleInfo.versesInBible();
            if (percent != newpercent)
            {
                percent = newpercent;
                job.setProgress(percent, "Checking Verses");
            }

            try
            {
                // Read the document from the first bible
                BookData text1 = book1.getData(ref2);
                BookData text2 = book2.getData(ref2);

                // Check - this needs some work
                if (!text1.equals(text2))
                {
                    out.println("Verse: "+range);
                    out.println(book1.getBookMetaData().getName()+": "+text1);
                    out.println(book2.getBookMetaData().getName()+": "+text2);
                    out.println();
                }
            }
            catch (Exception ex)
            {
                out.println("Verse:  " + range);
                ex.printStackTrace(out);
                out.println();
            }

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
            {
                break;
            }
        }
    }

    /**
     * Read from the given source version to generate ourselves
     */
    public void checkPassage(PrintWriter out) throws BookException
    {
        Job job = JobManager.createJob("Copying Bible data to new driver", Thread.currentThread(), false);
        int count = 0;
        int percent = -1;

        // For every word in the word list
        //Iterator it = bible1.listWords();
        Iterator it = new ArrayList().iterator();
        while (it.hasNext())
        {
            String word = (String) it.next();
            checkSinglePassage(word, out);

            // Fire a progress event?
            int newpercent = 100 * count++ / GUESS_WORDS;
            if (percent != newpercent)
            {
                percent = newpercent;
                job.setProgress(percent, "Checking Words");
            }

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
            {
                break;
            }
        }
    }

    /**
     * Read from the given source version to generate ourselves
     */
    public void checkPassage(String starts, PrintWriter out) throws BookException
    {
        if (starts == null || starts.equals(""))
        {
            checkPassage(out);
            return;
        }

        if (!(book1 instanceof Index))
        {
            return;
        }

        Job job = JobManager.createJob("Copying Bible data to new driver", Thread.currentThread(), false);
        int count = 0;
        int percent = -1;

        // For every word in the word list
        Index index1 = (Index) book1;
        Iterator it = index1.getStartsWith(starts);
        while (it.hasNext())
        {
            String s = (String) it.next();
            checkSinglePassage(s, out);

            // Fire a progress event?
            int newpercent = 100 * count++ / GUESS_WORDS;
            if (percent != newpercent)
            {
                percent = newpercent;
                job.setProgress(percent, "Checking Words");
            }

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
            {
                break;
            }
        }
    }

    /**
     * Read from the given source version to generate ourselves
     */
    private void checkSinglePassage(String word, PrintWriter out) throws BookException
    {
        Key ref1 = book1.find(new Search(word, false));
        Key ref2 = book2.find(new Search(word, false));

        // Check
        if (!ref1.equals(ref2))
        {
            out.println("Word:   " + word);
            out.println(book1.getBookMetaData().getName() + ": " + ref1);
            out.println(book2.getBookMetaData().getName() + ": " + ref2);
            out.println();
        }
    }

    /**
     * We have no way of knowing exactly how many words there are in a Version ...
     */
    public static final int GUESS_WORDS = 18500;

    /**
     * The Whole Bible
     */
    public static final Passage WHOLE = PassageFactory.getWholeBiblePassage();

    /**
     * The first Bible that we are checking
     */
    private Book book1;

    /**
     * The second Bible that we are checking
     */
    private Book book2;
}
