
package org.crosswire.jsword.book.sword;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Commentary;
import org.crosswire.jsword.book.CommentaryMetaData;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.basic.AbstractCommentary;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.FilterException;
import org.crosswire.jsword.book.data.OSISBookDataListnener;
import org.crosswire.jsword.book.data.BookDataListener;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A Sword version of a Commentary.
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
public class SwordCommentary extends AbstractCommentary implements Commentary
{
    /**
     * @param data
     */
    public SwordCommentary(SwordCommentaryMetaData scmd, SwordConfig config)
    {
        this.scmd = scmd;
        this.config = config;

        try
        {
            backend = config.getBackend();
            backend.init(config);
        }
        catch (BookException ex)
        {
            backend = null;
            log.error("Failed to init", ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Commentary#getCommentaryMetaData()
     */
    public CommentaryMetaData getCommentaryMetaData()
    {
        return scmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Commentary#getComments(org.crosswire.jsword.passage.Passage)
     */
    public BookData getComments(Passage ref) throws BookException
    {
        if (backend == null)
            failedGetData(ref, Msg.READ_FAIL);

        try
        {
            BookDataListener li = new OSISBookDataListnener();
            li.startDocument(getCommentaryMetaData().getInitials());
    
            // For all the ranges in this Passage
            Iterator rit = ref.rangeIterator();
            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();
                li.startSection(range.toString());
    
                // For all the verses in this range
                Iterator vit = range.verseIterator();
                while (vit.hasNext())
                {
                    Verse verse = (Verse) vit.next();
    
                    li.startVerse(verse);

                    // We should probably think about encodings here?
                    byte[] data = backend.getRawText(verse);
                    String text = new String(data);
                    config.getFilter().toOSIS(li, text);

                    li.endVerse();
                }
                
                li.endSection();
            }
    
            return li.endDocument();
        }
        catch (FilterException ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#findPassage(org.crosswire.jsword.book.Search)
     */
    public Passage findPassage(Search word) throws BookException
    {
        return PassageFactory.createPassage();
    }

    /**
     * To read the data from the disk
     */
    private Backend backend;

    /**
     * Our meta data
     */
    private SwordCommentaryMetaData scmd;

    /**
     * The configuration file
     */
    private SwordConfig config;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(SwordCommentary.class);
}
