/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.ser;

import java.net.URL;
import java.util.Properties;

import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;

/**
 * A Biblical source that comes from files on the local file system.
 *
 * <p>This format is designed to be fast. At any cost. So disk space does
 * not matter, which is good because early versions used about 100Mb!</p>
 *
 * <p>This is a history of some of the design desisions that this class has
 * been through.</p>
 *
 * <h4>Searching</h4>
 * <p>I think that a Bible ought not to store anything other than Bible
 * text. I have experimented with a saerch mechanism that cached searches
 * in a very effective manner, however it took up a lot of disk space,
 * and only worked for one version. It might be good to have it work in a
 * more generic way, and an in-memory cache would also be a good idea. So
 * I am going to move the natty search bit into a caching class.
 *
 * <h4>Text Storage</h4>
 * It would be good to get a handle on the way the OLB and Sword and so on
 * work:<ul>
 * <li><b>OLB:</b> 2 core files: an index file that starts with text like:
 *     "AaronitesbaddonAbagthanarimabasedingtedAbbadaeelielonednegolbet"
 *     which is a strange sort of index. Possibly strings with start pos
 *     and length. Then data files, and plenty of other indexes.
 * <li><b>Theopholos:</b> Single data file that begins- "aaron aaronites
 *     aarons abaddon abagtha abana abarim abase abased abasing abated"
 *     This is again in index type affair.
 * <li><b>Sword:</b> All this VerseKey stuff ...
 * </ul>
 * I think the answer is that an word index is good. (Like this is news)
 * So we can map all the words to numbers and then encode the biblical
 * text as a series of numbers.
 *
 * <h4>Priorities</h4>
 * What factors affect our design the most?<ul>
 * <li><b>Search Speed:</b> Proably the biggest reason people will have to
 *     use this program initially will be the powerful search engine. This
 *     can be very demanding though, and every effort should be taken to
 *     make best match searches fast.
 * <li><b>Size:</b> Size is not a huge problem from a disk space point of
 *     view - the average hard disk is now about 10Gb. Looking at the
 *     various installations that I have, the average is a little short of
 *     20Mb each. Generally each version takes up 3-5Mb If we were to be
 *     over double this size and take up 50Mb total, I don't think there
 *     would be a huge problem.<br>
 *     However many people will first come to use this program from a net
 *     download - now size is a huge problem. Maybe we should have a
 *     very very compact download that on installation indexed itself.
 * <li><b>Text Retrieval Speed:</b> I do not see this as being a huge
 *     issue. The text generation time from reverse-engineering my
 *     concordance was acceptable if slow, so this should not be a big
 *     deal, and I guess it is very easily cacheable too.
 * </ul>
 *
 * <h4>Strategies</h4>
 * For a single verse we have 2 basic strategies. Have a single block of
 * data that specifies the words, punctuation, and markup, or for each set
 * of data we could have a separate source. Clearly there are also hybrid
 * versions. The pros and cons:<ul>
 * <li>Searches only have to read one file, and the information is more
 *     dense in that (less disk reads for wanted data) This also applies
 *     to the ability to ignore certain types of mark-up.
 * <li>It is easier to add/alter a single source of information - or even
 *     to share a source amongst versions. Maybe things like red lettering
 *     could benefit from this.
 * <li>Text display is slower because the information is spread over
 *     several files. But as mentioned above - who cares?
 * </ul>
 * So how far do we take this? The parts that we can split off from the
 * words are these:<ul>
 * <li>Markup: Most markup is tied to a particular word, so we would need
 *     some way of attaching markup to words.
 * <li>Inter-Word Punctuation: We could do for punctuation exactly what we
 *     do for the words. List the options in a dictionary, and then write
 *     out an index. I guess less than 255 different types of inter-word
 *     punctuation (1 byte per inter-word). (as opposed to 18360 different
 *     words 2 bytes per word)<br>
 *     There are 32k words in the Bible - this would make the central data
 *     file about 64k in size!
 * <li>Case: To get down to 18k words you need to make "Foo" the same as
 *     "foo" and "FOO", however I guess that even making words case
 *     sensative we would be under 65k words.
 *     Splitting case would not decrease file sizes (but may make it
 *     compress better) however it would introduce a new case file. Since
 *     there are only 4 cases (See PassageUtil) that is 0.25 bytes per
 *     word. (8k for the whole Bible)
 * <li>Intra-Word Punctuation: Examples "-',". Examples of words that use
 *     these punctuations: Maher-Shalal-Hash-Baz, Aaron's, 144,000. Watch
 *     out for --. The NIV uses it to join sentences together--Something
 *     like this. However there is no space between these words. This is
 *     closely linked to-
 * <li>Word Semantics: We could make the words "job", "jobs", and "job's"
 *     the same. Also "run", "runs", "running", "runned" and so on. Even
 *     "am", "are", "is". This would dramatically reduce the size of the
 *     dictionary, make the text re-generation quite complex and the data
 *     generation nigh on impossible. But it would make for some really
 *     powerful searches (although possibly nothing that a thesaurus would
 *     not help)
 * </ul>
 * I think the last 2 are hard to sus. However I am keen to work on them
 * next. So it looks like I sort out the first 3. Time to reasurect that
 * VB code. Now is it a port or a re-write?
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author Mark Goodwin [mark at thorubio dot org]
 */
public class SerBook extends AbstractPassageBook
{
    /**
     * Simple ctor
     */
    public SerBook(BookDriver driver, Properties prop, URL url) throws BookException
    {
        BookMetaData bmd = new DefaultBookMetaData(driver, this, prop);
        setBookMetaData(bmd);

        try
        {
            cache = new BookDataCache(url);
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.SER_INIT, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getFilter()
     */
    protected Filter getFilter()
    {
        return FilterFactory.getDefaultFilter();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getText(org.crosswire.jsword.passage.Verse)
     */
    protected String getText(Key key) throws BookException
    {
        Verse verse = KeyUtil.getVerse(key);

        return cache.getText(verse);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#setText(org.crosswire.jsword.passage.Verse, java.lang.String)
     */
    protected void setText(Verse verse, String text) throws BookException
    {
        cache.setText(verse, text);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBook#flush()
     *
    public void flush() throws BookException
    {
        super.flush();
    }
    */

    /**
     * The BookDataCache to which we delegate all questions
     */
    private BookDataCache cache;
}
