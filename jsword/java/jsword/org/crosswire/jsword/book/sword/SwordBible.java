
package org.crosswire.jsword.book.sword;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Iterator;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.JAXBUtil;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.book.search.SearchEngine;
import org.crosswire.jsword.book.search.SearchEngineFactory;
import org.crosswire.jsword.osis.Div;
import org.crosswire.jsword.osis.Header;
import org.crosswire.jsword.osis.Osis;
import org.crosswire.jsword.osis.OsisText;
import org.crosswire.jsword.osis.Work;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.util.Project;

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
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author Jacky Cheung
 * @version $Id$
 */
public class SwordBible extends AbstractBible
{
    /**
     * Constructor SwordBible.
     */
    public SwordBible(SwordBibleMetaData sbmd, SwordConfig config) throws BookException
    {
        this.sbmd = sbmd;
        this.config = config;

        backend = config.getBackend();
        backend.init(config);

        try
        {
            URL url = Project.instance().getTempScratchSpace("sword-"+sbmd.getInitials());
            searcher = SearchEngineFactory.createSearchEngine(this, url);
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.MISSING_SEARCHER, ex);
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
        {
            throw new BookException(Msg.MISSING_BACKEND);
        }

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

                    byte[] data = backend.getRawText(verse);
                    String charset = config.getModuleCharset();
                    String txt = null;
                    try
                    {
                        txt = new String(data, charset);
                    }
                    catch (UnsupportedEncodingException ex)
                    {
                        // It is impossible! In case, use system default...
                        log.error("Encoding: " + charset + " not supported", ex);
                        txt = new String(data);
                    }

                    // If the verse is empty then we shouldn't add the verse tag
                    if (txt.length() > 0)
                    {
                        org.crosswire.jsword.osis.Verse everse = JAXBUtil.factory().createVerse();
                        everse.setOsisID(verse.getBook()+"."+verse.getChapter()+"."+verse.getVerse());

                        div.getContent().add(everse);

                        config.getFilter().toOSIS(everse, txt);
                    }
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
     * @see org.crosswire.jsword.book.Bible#findPassage(org.crosswire.jsword.book.Search)
     */
    public Passage findPassage(Search match) throws BookException
    {
        return searcher.findPassage(match);
    }

    /**
     * The search implementation
     */
    private SearchEngine searcher;

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
    private static final Logger log = Logger.getLogger(SwordBible.class);
}
