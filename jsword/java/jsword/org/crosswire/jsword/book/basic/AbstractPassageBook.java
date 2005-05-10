/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
package org.crosswire.jsword.book.basic;

import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom.Element;

/**
 * An abstract implementation of Book that lets implementors just concentrate
 * on reading book data.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractPassageBook extends AbstractBook
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.passage.Key)
     */
    public BookData getData(Key key) throws BookException
    {
        assert key != null;

        try
        {
            Element osis = OSISUtil.createOsisFramework(getBookMetaData());
            Element text = osis.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);

            // For all the ranges in this Passage
            Passage ref = KeyUtil.getPassage(key);
            Iterator rit = ref.rangeIterator(RestrictionType.CHAPTER);

            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();

                Element div = OSISUtil.factory().createDiv();
                text.addContent(div);

                Element title = OSISUtil.factory().createTitle();
                title.addContent(range.getName());
                div.addContent(title);

                // For all the verses in this range
                Iterator vit = range.verseIterator();
                while (vit.hasNext())
                {
                    Key verse = (Key) vit.next();
                    String txt = getText(verse);

                    // If the verse is empty then we shouldn't add the verse tag
                    if (txt.length() > 0)
                    {
                        List osisContent = getFilter().toOSIS(verse, txt);
                        addOSIS(verse, div, osisContent);

                    }
                }
            }

            BookData bdata = new BookData(osis, this, key);
            return bdata;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /**
     * Add the OSIS elements to the div element. Note, this assumes that
     * the data is fully marked up.
     * @param key The key being added
     * @param div The div element to which the key is being added
     * @param osisContent The OSIS representation of the key being added.
     */
    public void addOSIS(Key key, Element div, List osisContent)
    {
        assert key != null;
        div.addContent(osisContent);
    }

    /**
     * What filter should be used to filter data in the format produced by this
     * Book?.
     * In some ways this method is more suited to BookMetaData however we do not
     * have a specialization of BookMetaData to fit AbstractPassageBook and it
     * doesn't like any higher in the hierachy at the moment so I will leave
     * this here.
     */
    protected abstract Filter getFilter();

    /**
     * Read the unfiltered data for a given key
     */
    protected abstract String getText(Key key) throws BookException;

    /**
     * For when we want to add writing functionality
     */
    public void setDocument(Verse verse, BookData bdata) throws BookException
    {
        // For all of the sections
        Iterator sit = bdata.getOsis().getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT).getChildren(OSISUtil.OSIS_ELEMENT_DIV).iterator();
        while (sit.hasNext())
        {
            Element div = (Element) sit.next();

            // For all of the Verses in the section
            for (Iterator vit = div.getContent().iterator(); vit.hasNext(); )
            {
                Object data = vit.next();
                if (data instanceof Element)
                {
                    Element overse = (Element) data;
                    String text = OSISUtil.getPlainText(overse);

                    setText(verse, text);
                }
                else
                {
                    log.error("Ignoring non OSIS/Verse content of DIV."); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Set the unparsed text for a verse to permanent storage.
     */
    protected abstract void setText(Verse verse, String text) throws BookException;

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getEmptyKeyList()
     */
    public final Key createEmptyKeyList()
    {
        return keyf.createEmptyKeyList();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public final Key getGlobalKeyList()
    {
        return keyf.getGlobalKeyList();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public final Key getKey(String text) throws NoSuchKeyException
    {
        return keyf.getKey(text);
    }

    /**
     * Our key manager
     */
    private KeyFactory keyf = PassageKeyFactory.instance();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AbstractPassageBook.class);

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getRawData(org.crosswire.jsword.passage.Key)
     */
    public String getRawData(Key key) throws BookException
    {
        assert key != null;

        try
        {
            StringBuffer buffer = new StringBuffer();

            // For all the ranges in this Passage
            Passage ref = KeyUtil.getPassage(key);
            Iterator rit = ref.rangeIterator(RestrictionType.CHAPTER);

            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();

                // For all the verses in this range
                Iterator vit = range.verseIterator();
                while (vit.hasNext())
                {
                    Verse verse = (Verse) vit.next();
                    String txt = getText(verse);

                    // If the verse is empty then we shouldn't add the verse
                    if (txt.length() > 0)
                    {
                        buffer.append(txt);
                        buffer.append('\n');
                    }
                }
            }

            return buffer.toString();
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }
}
