
package org.crosswire.jsword.book.stub;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.book.basic.DefaultKey;
import org.crosswire.jsword.book.data.BookData;
import org.crosswire.jsword.book.data.Filters;
import org.crosswire.jsword.book.data.JAXBUtil;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Header;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisText;
import org.crosswire.jsword.osis.Work;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;

/**
 * StubBible is a simple stub implementation of Bible that is pretty much
 * always going to work because it has no dependancies on external files.
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
public class StubBible extends AbstractBible
{
    /**
     * Basic constructor for a StubBible
     */
    public StubBible(BibleMetaData bmd)
    {
        this.bmd = bmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#getBibleMetaData()
     */
    public BibleMetaData getBibleMetaData()
    {
        return bmd;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#getData(org.crosswire.jsword.passage.Passage)
     */
    public BookData getData(Passage ref) throws BookException
    {
        try
        {
            String osisid = getBibleMetaData().getInitials();
            Osis osis = JAXBUtil.factory().createOsis();

            Work work = JAXBUtil.factory().createWork();
            work.setOsisWork(osisid);
            
            Header header = JAXBUtil.factory().createHeader();
            header.getWork().add(work);
            
            OsisText text = JAXBUtil.factory().createOsisText();
            text.setOsisIDWork("Bible."+osisid);
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
                    everse.setOsisID(verse.getBook()+"."+verse.getChapter()+"."+verse.getVerse());
                    
                    div.getContent().add(everse);

                    Filters.PLAIN_TEXT.toOSIS(everse, "stub implementation");
                }
            }
            
            BookData bdata = new BookData(osis);
            return bdata;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Commentary#findPassage(org.crosswire.jsword.book.Search)
     */
    public Passage findPassage(Search search) throws BookException
    {
        try
        {
            return PassageFactory.createPassage("Gen 1:1-Rev 22:21");
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Dictionary#getIndex(java.lang.String)
     */
    public SortedSet getIndex(String base)
    {
        base = base.toLowerCase();
        
        SortedSet set = new TreeSet();

        if ("stub".startsWith(base))
        {
            set.add(new DefaultKey("stub"));
        }

        if ("implementation".startsWith(base))
        {
            set.add(new DefaultKey("implementation"));
        }

        return set;
    }

    /**
     * The name of this version
     */
    private BibleMetaData bmd;
}
