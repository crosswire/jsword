package org.crosswire.jsword.book.basic;

import java.util.Iterator;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom.Element;

/**
 * An abstract implementation of Book that lets implementors just concentrate
 * on reading book data.
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
public abstract class PassageAbstractBook extends AbstractBook
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
            KeyList keylist = DefaultKeyList.getKeyList(key);
            Passage ref = PassageUtil.getPassage(keylist);
            Iterator rit = ref.rangeIterator(PassageConstants.RESTRICT_CHAPTER);

            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();

                Element div = OSISUtil.factory().createDiv();
                Element title = OSISUtil.factory().createTitle();
                title.addContent(range.getName());
                div.addContent(title);
                text.addContent(div);

                // For all the verses in this range
                Iterator vit = range.verseIterator();
                while (vit.hasNext())
                {
                    Verse verse = (Verse) vit.next();
                    String txt = getText(verse);

                    // If the verse is empty then we shouldn't add the verse tag
                    if (txt.length() > 0)
                    {
                        Element everse = OSISUtil.factory().createVerse();
                        everse.setAttribute(OSISUtil.ATTRIBUTE_VERSE_OSISID, verse.getOSISName());

                        div.getContent().add(everse);

                        getFilter().toOSIS(everse, txt);
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
     * What filter should be used to filter data in the format produced by this
     * Book?.
     * In some ways this method is more suited to BookMetaData however we do not
     * have a specialization of BookMetaData to fit PassageAbstractBook and it
     * doesn't like any higher in the hierachy at the moment so I will leave
     * this here.
     */
    protected abstract Filter getFilter();

    /**
     * Read the unfiltered data for a given verse
     */
    protected abstract String getText(Verse verse) throws BookException;

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
    public KeyList createEmptyKeyList()
    {
        return keyf.createEmptyKeyList();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public final KeyList getGlobalKeyList()
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
    private KeyFactory keyf = new PassageKeyFactory();

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(PassageAbstractBook.class);

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
            KeyList keylist = DefaultKeyList.getKeyList(key);
            Passage ref = PassageUtil.getPassage(keylist);
            Iterator rit = ref.rangeIterator(PassageConstants.RESTRICT_CHAPTER);
    
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