
package org.crosswire.jsword.book.basic;

import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.MsgBase;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Commentary;
import org.crosswire.jsword.book.Key;
import org.crosswire.jsword.book.PassageKey;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.DataException;
import org.crosswire.jsword.book.data.Filters;
import org.crosswire.jsword.book.data.JAXBUtil;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Header;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisText;
import org.crosswire.jsword.osis.Work;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * An AbstractCommentary implements a few of the more generic methods of Commentary.
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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public abstract class AbstractCommentary implements Commentary
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
        return getCommentaryMetaData();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Commentary#hasComments(org.crosswire.jsword.passage.Verse)
     */
    public boolean hasComments(Verse verse) throws BookException
    {
        Passage ref = PassageFactory.createPassage();
        ref.add(verse);
        BookData bdata = getComments(ref);

        return bdata != null;
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
            throw new BookException(Msg.NO_VERSE, ex);
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
            return getComments(ref);
        }
        else
        {
            return null;
        }
    }

    /**
     * For when we need to patch up for a getData() that has failed.
     * @see org.crosswire.jsword.book.Bible#getData(Passage)
     */
    protected BookData failedGetData(Passage ref, MsgBase message)
    {
        try
        {
            String osisid = getBookMetaData().getInitials();
            Osis osis = JAXBUtil.factory().createOsis();

            Work work = JAXBUtil.factory().createWork();
            work.setOsisWork(osisid);

            Header header = JAXBUtil.factory().createHeader();
            header.getWork().add(work);

            OsisText text = JAXBUtil.factory().createOsisText();
            text.setOsisIDWork("Bible." + osisid);
            text.setHeader(header);

            osis.setOsisText(text);

            // For all the ranges in this Passage
            Iterator rit = ref.rangeIterator(PassageConstants.RESTRICT_CHAPTER);
            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();
                Div div = JAXBUtil.factory().createDiv();
                div.setDivTitle(range.toString());

                text.getDiv().add(div);

                // For all the verses in this range
                Iterator vit = range.verseIterator();
                while (vit.hasNext())
                {
                    Verse verse = (Verse) vit.next();

                    org.crosswire.jsword.osis.Verse everse = JAXBUtil.factory().createVerse();
                    everse.setOsisID(verse.getBook() + "." + verse.getChapter() + "." + verse.getVerse());

                    div.getContent().add(everse);

                    try
                    {
                        Filters.PLAIN_TEXT.toOSIS(everse, message.getName());
                    }
                    catch (DataException ex)
                    {
                        // Ignore. There is not a lot we can do more.
                    }
                }
            }

            return new BookData(osis);
        }
        catch (JAXBException ex)
        {
            throw new LogicError();
        }
    }
}
