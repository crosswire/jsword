
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

/**
 * LinkArray contains a set of links for each verse in the Bible.
 * It is similar to a central margin reference data set, except that
 * every verse is linked to a constant number of others, and the links
 * have strengths.
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

        cacheAll();
    }

    /**
     * Fill up the link cache
     */
    public void cacheAll() throws NoSuchVerseException, BookException, SearchException
    {
        // Create the array of Nodes
        for (int i=1; i<=Books.versesInBible(); i++)
        {
            getLinks(i);
        }
    }

    /**
     * Turn a PassageTally into an array of links.
     * @param verse The verse to get a link for
     * @return The array of links for the specified verse
     */
    public Link[] getLinks(int ord)
    {
        int index = ord - 1;
        if (links[index] != null)
            return links[index];

        try
        {
            linked++;
            Verse verse = new Verse(ord);

            Passage ref = PassageFactory.createPassage();
            ref.add(verse);
            BibleData data = bible.getData(ref);

            String text = data.getPlainText();

            PassageTally tally = engine.bestMatch(text);

            tally.setOrdering(PassageTally.ORDER_TALLY);
            tally.trimVerses(LINKS_PER_VERSE);

            // Check that the tally contains the first verse as first match
            if (!tally.getVerseAt(0).equals(verse))
            {
                int miss_index = tally.getIndexOf(verse);
                if (miss_index == -1) miss_index = 50;
                miss_total += miss_index;
                log.info(""+verse+" missed by "+miss_index+" average index="+getMatchScore()+" text="+text);
            }

            // Remove the original wherever it was
            tally.remove(verse);

            // Create the links for the tally
            links[index] = new Link[LINKS_PER_VERSE];
            for (int i=0; i<LINKS_PER_VERSE; i++)
            {
                try
                {
                    Verse loop = tally.getVerseAt(i);
                    int strength = tally.getTallyOf(loop);

                    links[index][i] = new Link(loop.getOrdinal(), strength);
                }
                catch (ArrayIndexOutOfBoundsException ex)
                {
                    links[index][i] = new Link(verse.getOrdinal(), 0);
                }
            }

            return links[index];
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

    /** The number of links we record for each verse */
    private static final int LINKS_PER_VERSE = 40;

    /** The link data */
    private Link[][] links = new Link[Books.versesInBible()][];

    /** The log stream */
    protected static Logger log = Logger.getLogger(LinkArray.class);
}
