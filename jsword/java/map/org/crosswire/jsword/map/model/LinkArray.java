
package org.crosswire.jsword.map.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.control.search.Matcher;
import org.crosswire.jsword.control.search.SearchException;
import org.crosswire.jsword.passage.Books;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * LinkArray contains a set of link chapters for each chapter in the Bible.
 * It is similar to a central margin reference data set, except that it works
 * with chapters and not verses and every chapter is linked to a constant
 * number of others, and the links have strengths.
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
public class LinkArray implements Serializable
{
    /**
     * Basic constructor
     * @param bible The source of Bible data
     */
    public LinkArray(Bible bible) throws NoSuchVerseException, BookException, SearchException
    {
        this.bible = bible;
        engine = new Matcher(bible);

        links = new Link[Books.versesInBible()][][];

        for (int b=1; b<=Books.versesInBible(); b++)
        {
            links[b] = new Link[Books.chaptersInBook(b)][];
        }

        cacheAll();
    }

    /**
     * Fill up the link cache
     */
    public void cacheAll() throws NoSuchVerseException, BookException, SearchException
    {
        // Create the array of Nodes
        for (int b=1; b<=Books.versesInBible(); b++)
        {
            for (int c=1; c<=Books.chaptersInBook(b); c++)
            {
                getLinks(b, c);
            }
        }
    }

    /**
     * Turn a PassageTally into an array of links.
     * @param verse The verse to get a link for
     * @return The array of links for the specified verse
     */
    public Link[] getLinks(int b, int c)
    {
        if (links[b][c] != null)
            return links[b][c];

        try
        {
            PassageTally total = new PassageTally();
            total.setOrdering(PassageTally.ORDER_TALLY);
            
            for (int v=1; v<=Books.versesInChapter(b, c); v++)
            {
                Verse find = new Verse(b, c, v);
                Passage ref = PassageFactory.createPassage();
                ref.add(find);

                BibleData data = bible.getData(ref);
                String text = data.getPlainText();   
                PassageTally temp = engine.bestMatch(text);
                temp.setOrdering(PassageTally.ORDER_TALLY);
                total.addAll(temp);
            }

            int chff = Books.chaptersInBook(b);
            int vsff = Books.versesInChapter(b, chff);
            Verse start = new Verse(b, 1, 1);
            Verse end = new Verse(b, chff, vsff);
            VerseRange book = new VerseRange(start, end);

            total.remove(book);
            total.trimVerses(LINKS_PER_CHAPTER);
            scrunchTally(total);

            // Create the links for the tally
            links[b][c] = new Link[total.countVerses()];
            for (int i=0; i<links[b][c].length; i++)
            {
                Verse loop = total.getVerseAt(i);
                int strength = total.getTallyOf(loop);
                links[b][c][i] = new Link(loop.getBook(), loop.getChapter(), strength);
            }

            return links[b][c];
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * What is the average index for a given match. This is a measure of
     * how good the nest match algorithm is. The closer to zero the better
     * @return The average match index
     */
    public float getMatchScore()
    {
        if (linked == 0)
            return -1;

        return ((float) (100 * miss_total)) / linked;
    }

    /**
     * Take a tally and move all the link strengths in and chapter to the first
     * verse in the chapter.
     */
    public void scrunchTally(PassageTally tally) throws NoSuchVerseException
    {
        for (int b=1; b<=Books.booksInBible(); b++)
        {
            for (int c=1; c<=Books.chaptersInBook(b); c++)
            {
                Verse start = new Verse(b, c, 1);
                Verse end = new Verse(b, c, Books.versesInChapter(b, c));
                VerseRange chapter = new VerseRange(start, end);
                
                int chaptotal = 0;

                for (int v=1; v<=Books.versesInChapter(b, c); v++)
                {
                    chaptotal += tally.getTallyOf(new Verse(b, c, v));
                }
                
                tally.remove(chapter);
                tally.add(start, chaptotal);

                if (chaptotal > PassageTally.MAX_TALLY)
                    System.out.println("truncated chaptotal: "+chaptotal);
            }
        }        
    }

    /**
     * Debug for an array of Links
     */
    private static String debug(Link[] set)
    {
        StringBuffer buff = new StringBuffer();

        for (int i=0; i<set.length; i++)
        {
            if (i != 0)
                buff.append(", ");

            buff.append(set[i].toString());
        }

        return buff.toString();
    }

    /** To make serialization work across new versions */
    static final long serialVersionUID = -2354670272946948354L;

    /** The total miss mark */
    private transient int miss_total = 0;

    /** The number of verses checked */
    private transient int linked = 0;

    /** The Bible that we search in */
    private transient Bible bible;

    /** The thing we use to generate matches */
    private transient Matcher engine;

    /** The number of links we record for each chapter */
    public static final int LINKS_PER_CHAPTER = 200;

    /** The link data */
    private Link[][][] links;

    /** The log stream */
    protected static Logger log = Logger.getLogger(LinkArray.class);
}
