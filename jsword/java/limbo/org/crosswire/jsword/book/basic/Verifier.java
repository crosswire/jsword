package org.crosswire.jsword.book.basic;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.search.basic.DefaultSearchRequest;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;

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
     * @param book1 A Bible to check
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
     * @param book2 A Bible to check
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
    public void checkText(Key key, PrintWriter out)
    {
        Job job = JobManager.createJob(Msg.VERIFY_START.toString(), Thread.currentThread(), false);

        if (key == null)
        {
            key = book1.getGlobalKeyList();
        }

        // For every verse in the Bible
        int percent = 0;
        for (Iterator it = key.iterator(); it.hasNext(); )
        {
            Key subkey = (Key) it.next();

            if (subkey.canHaveChildren())
            {
                checkText(subkey, out);
            }
            else
            {
                try
                {
                    // Read the document from the first bible
                    BookData text1 = book1.getData(subkey);
                    BookData text2 = book2.getData(subkey);

                    // Check - this needs some work
                    if (!text1.equals(text2))
                    {
                        out.println(Msg.VERIFY_VERSE.toString() + subkey);
                        out.println(book1.getName() + ": " + text1); //$NON-NLS-1$
                        out.println(book2.getName() + ": " + text2); //$NON-NLS-1$
                        out.println();
                    }
                }
                catch (Exception ex)
                {
                    out.println(Msg.VERIFY_VERSE.toString() + subkey);
                    ex.printStackTrace(out);
                    out.println();
                }

                // Fire a progress event?
                if (subkey instanceof Passage)
                {
                    Verse verse = KeyUtil.getVerse(key);
                    percent = 100 * verse.getOrdinal() / BibleInfo.versesInBible();
                }

                job.setProgress(percent, Msg.VERIFY_VERSES.toString());

                // This could take a long time ...
                Thread.yield();
                if (Thread.currentThread().isInterrupted())
                {
                    break;
                }
            }
        }
    }

    /**
     * Read from the given source version to generate ourselves
     */
    public void checkPassage(PrintWriter out) throws BookException
    {
        Job job = JobManager.createJob(Msg.VERIFY_PASSAGES.toString(), Thread.currentThread(), false);
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
                job.setProgress(percent, Msg.VERIFY_WORDS.toString());
            }

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
            {
                break;
            }
        }

        job.done();
    }

    /**
     * Read from the given source version to generate ourselves
     */
    private void checkSinglePassage(String word, PrintWriter out) throws BookException
    {
        Key ref1 = book1.find(new DefaultSearchRequest(word, null));
        Key ref2 = book2.find(new DefaultSearchRequest(word, null));

        // Check
        if (!ref1.equals(ref2))
        {
            out.println(Msg.WORD.toString() + word);
            out.println(book1.getName() + ": " + ref1); //$NON-NLS-1$
            out.println(book2.getName() + ": " + ref2); //$NON-NLS-1$
            out.println();
        }
    }

    /**
     * We have no way of knowing exactly how many words there are in a Version ...
     */
    public static final int GUESS_WORDS = 18500;

    /**
     * The first Bible that we are checking
     */
    private Book book1;

    /**
     * The second Bible that we are checking
     */
    private Book book2;
}
