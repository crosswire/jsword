
package org.crosswire.jsword.book.basic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.crosswire.common.util.EventListenerList;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.events.ProgressEvent;
import org.crosswire.jsword.book.events.ProgressListener;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Verifier
{
    /**
     * Constructor that sets up the Bibles as well.
     */
    public Verifier(Bible bible1, Bible bible2)
    {
        setBible1(bible1);
        setBible2(bible2);
    }

    /**
     * The first Bible that we are checking, this is supposed to be the
     * more accurate of the 2 Bibles, so we use this as a source of the
     * words to check.
     * @param bible1 A Bible to check
     */
    public void setBible1(Bible bible1)
    {
        this.bible1 = bible1;
    }

    /**
     * The first Bible that we are checking
     * @return A Bible to check
     */
    public Bible getBible1()
    {
        return bible1;
    }

    /**
     * The second Bible that we are checking, this is supposed to be the
     * less accurate, or more recent of the 2 Bibles, so we use this in
     * firing ProgressEvents.
     * @param bible2 A Bible to check
     */
    public void setBible2(Bible bible2)
    {
        this.bible2 = bible2;
    }

    /**
     * The second Bible that we are checking
     * @return A Bible to check
     */
    public Bible getBible2()
    {
        return bible2;
    }

    /**
     * Read from the given source version to generate ourselves
     * @param version The source
     */
    public void checkText(PrintWriter out) throws IOException, NoSuchVerseException, BookException
    {
        checkText(WHOLE, out);
    }

    /**
     * Read from the given source version to generate ourselves
     * @param version The source
     */
    public void checkText(Passage ref, PrintWriter out) throws IOException, NoSuchVerseException, BookException
    {
        int progress = 0;
        alive = true;

        // For every verse in the Bible
        Iterator it = ref.verseIterator();
        while (it.hasNext() && alive)
        {
            Verse verse = (Verse) it.next();
            VerseRange range = new VerseRange(verse);
            Passage ref2 = PassageFactory.createPassage();
            ref2.add(range);

            // Fire a progress event?
            int new_progress = 100 * verse.getOrdinal() / BibleInfo.versesInBible();
            if (progress != new_progress)
            {
                progress = new_progress;
                fireProgressMade("Checking Verses:", progress);
            }

            try
            {
                // Read the document from the first bible
                //BibleData text1 = bible1.getData(ref2);
                //BibleData text2 = bible2.getData(ref2);

                // NOTE(joe): Create some sort of BibleData compare system
                // Check

                /*
                if (!text1.equals(text2))
                {
                    out.println("Verse: "+range);
                    out.println(bible1.getName()+": "+text1);
                    out.println(bible2.getName()+": "+text2);
                    out.println();
                }
                */
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
                break;
        }
    }

    /**
     * Read from the given source version to generate ourselves
     * @param version The source
     */
    public void checkPassage(PrintWriter out) throws IOException, NoSuchVerseException, BookException
    {
        int count = 0;
        alive = true;

        // For every word in the word list
        //Iterator it = bible1.listWords();
        Iterator it = new ArrayList().iterator();
        while (it.hasNext() && alive)
        {
            String word = (String) it.next();
            checkSinglePassage(word, out);

            // Fire a progress event?
            fireProgressMade("Checking Words:", 100 * count++ / GUESS_WORDS);

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
    public void checkPassage(String starts, PrintWriter out) throws IOException, NoSuchVerseException, BookException
    {
        if (starts == null || starts.equals(""))
        {
            checkPassage(out);
            return;
        }

        int count = 0;
        alive = true;

        // For every word in the word list
        // NOTE(joe): think of a new way to do this
        Iterator it = null;//bible1.getSearcher().getStartsWith(starts);

        while (it.hasNext())
        {
            String s = (String) it.next();
            checkSinglePassage(s, out);

            // Fire a progress event?
            fireProgressMade("Checking Words:", 100 * count++ / GUESS_WORDS);

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
    private void checkSinglePassage(String word, PrintWriter out) throws IOException, NoSuchVerseException, BookException
    {
        Passage ref1 = bible1.findPassage(new Search(word, false));
        Passage ref2 = bible2.findPassage(new Search(word, false));

        // Check
        if (!ref1.equals(ref2))
        {
            out.println("Word:   " + word);
            out.println(bible1.getBookMetaData().getName() + ": " + ref1);
            out.println(bible2.getBookMetaData().getName() + ": " + ref2);
            out.println();
        }
    }

    /**
     * Since many of these operations take a long time, we may want to
     * kill them politely without killing any Threads
     */
    public void stopChecking()
    {
        alive = false;
    }

    /**
     * Add a progress listener to the list of things wanting
     * to know whenever we make some progress
     */
    public void addProgressListener(ProgressListener li)
    {
        listeners.add(ProgressListener.class, li);
    }

    /**
     * Remove a progress listener from the list of things wanting
     * to know whenever we make some progress
     */
    public void removeProgressListener(ProgressListener li)
    {
        listeners.remove(ProgressListener.class, li);
    }

    /**
     * Called to fire a ProgressEvent to all the Listeners, but only if
     * there is actual progress since last time.
     * @param percent The percentage of the way through that we are now
     */
    protected void fireProgressMade(String name, int percent)
    {
        if (this.percent == percent)
            return;

        this.percent = percent;

        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        ProgressEvent ev = null;
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == ProgressListener.class)
            {
                if (ev == null)
                    ev = new ProgressEvent(bible2, name, percent);

                ((ProgressListener) contents[i + 1]).progressMade(ev);
            }
        }
    }

    /**
     * We have no way of knowing exactly how many words there are in a Version ...
     */
    public static final int GUESS_WORDS = 18500;

    /**
     * The Whole Bible
     */
    public static Passage WHOLE = PassageFactory.getWholeBiblePassage();

    /**
     * The list of listeners
     */
    protected EventListenerList listeners = new EventListenerList();

    /**
     * The current progress
     */
    protected int percent = -1;

    /**
     * The first Bible that we are checking
     */
    private Bible bible1;

    /**
     * The second Bible that we are checking
     */
    private Bible bible2;

    /**
     * Is it OK to carry on
     */
    private boolean alive = true;
}
