
package org.crosswire.jsword.book.sword;

import java.util.Iterator;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.BookDataListener;
import org.crosswire.jsword.book.data.FilterException;
import org.crosswire.jsword.book.data.OSISBookDataListnener;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A BibleDriver to read Sword format data.
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @version $Id$
 */
public class SwordBible extends AbstractBible
{
    /**
     * Constructor SwordBible.
     * @param swordConfig
     */
    public SwordBible(SwordBibleMetaData sbmd, SwordConfig config)
    {
        this.sbmd = sbmd;
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
     * @see org.crosswire.jsword.book.Bible#getBibleMetaData()
     */
    public BibleMetaData getBibleMetaData()
    {
        return sbmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#getData(org.crosswire.jsword.passage.Passage)
     */
    public BookData getData(Passage ref) throws BookException
    {
        if (backend == null)
            throw new BookException(Msg.READ_FAIL);

        try
        {
            BookDataListener li = new OSISBookDataListnener();
            li.startDocument(getBibleMetaData().getInitials());
    
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
    private SwordBibleMetaData sbmd;

    /**
     * The configuration file
     */
    private SwordConfig config;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(SwordBible.class);
}
