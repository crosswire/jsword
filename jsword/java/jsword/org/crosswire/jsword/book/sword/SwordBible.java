
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.basic.AbstractBible;
import org.crosswire.jsword.book.data.BibleData;
import org.crosswire.jsword.book.data.OsisUtil;
import org.crosswire.jsword.book.data.SectionData;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
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
public abstract class SwordBible extends AbstractBible
{
    /**
     * Constructor SwordBible.
     * @param swordConfig
     */
    public SwordBible(SwordBibleMetaData sbmd, SwordConfig config) throws BookException
    {
        this.sbmd = sbmd;
        this.config = config;

        if (ORDINAL_MAT11 == -1)
        {
            try
            {
                Verse mat11 = new Verse(BibleInfo.Names.Matthew, 1, 1);
                ORDINAL_MAT11 = mat11.getOrdinal();
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
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
    public BibleData getData(Passage ref) throws BookException
    {
        BibleData doc = OsisUtil.createBibleData(getBibleMetaData());

        try
        {
            // For all the ranges in this Passage
            Iterator rit = ref.rangeIterator();
            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();
                SectionData section = OsisUtil.createSectionData(doc, range.toString());

                // For all the verses in this range
                Iterator vit = range.verseIterator();
                while (vit.hasNext())
                {
                    Verse verse = (Verse) vit.next();
                    String text = getText(verse);
                    OsisUtil.createRefData(section, verse, text);
                }
            }

            return doc;
        }
        catch (Exception ex)
        {
            throw new BookException("ser_read", ex);
        }
    }

    /**
     * @param verse
     * @return String
     * @throws IOException
     */
    public abstract String getText(Verse verse) throws IOException;

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#findPassage(org.crosswire.jsword.book.Search)
     */
    public Passage findPassage(Search word) throws BookException
    {
        return PassageFactory.createPassage();
    }

    /**
     * Accessor for the SwordConfig
     */
    protected SwordConfig getConfig()
    {
        return config;
    }

    /**
     * Our meta data
     */
    private SwordBibleMetaData sbmd;

    /**
     * The start of the new testament
     */
    protected static int ORDINAL_MAT11 = -1;

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(SwordBible.class);

    /**
     * The configuration file
     */
    private SwordConfig config;
}
