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
package org.crosswire.jsword.book.basic;

import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;
import org.jdom.Content;
import org.jdom.Element;

/**
 * An abstract implementation of Book that lets implementors just concentrate on
 * reading book data.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractPassageBook extends AbstractBook {

    public AbstractPassageBook(BookMetaData bmd) {
        super(bmd);
        this.versification = (String) bmd.getProperty(BookMetaData.KEY_VERSIFICATION);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getOsisIterator(org.crosswire.jsword.passage.Key, boolean)
     */
    public Iterator<Content> getOsisIterator(Key key, final boolean allowEmpty) throws BookException {
        // Note: allowEmpty indicates parallel view
        // TODO(DMS): make the iterator be demand driven
        final Filter filter = getFilter();

        // For all the ranges in this Passage
        Passage ref = KeyUtil.getPassage(key, getVersification());
        final boolean showTitles = ref.hasRanges(RestrictionType.CHAPTER) || !allowEmpty;

        RawTextToXmlProcessor processor = new RawTextToXmlProcessor() {
            public void preRange(VerseRange range, List<Content> partialDom) {
                if (showTitles) {
                    Element title = OSISUtil.factory().createTitle();
                    title.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.GENERATED_CONTENT);
                    title.addContent(range.getName());
                    partialDom.add(title);
                }
            }

            public void postVerse(Key verse, List<Content> partialDom, String rawText) {
                // If the verse is empty then we shouldn't add the verse tag
                if (allowEmpty || rawText.length() > 0) {
                    List<Content> osisContent = filter.toOSIS(AbstractPassageBook.this, verse, rawText);
                    addOSIS(verse, partialDom, osisContent);
                }
            }

            public void init(List<Content> partialDom) {
                // no-op
            }
        };

        return getOsis(ref, processor).iterator();
    }

    /**
     * Add the OSIS elements to the div element. Note, this assumes that the
     * data is fully marked up.
     * 
     * @param key
     *            The key being added
     * @param div
     *            The div element to which the key's OSIS representation is
     *            being added
     * @param osisContent
     *            The OSIS representation of the key being added.
     */
    public void addOSIS(Key key, Element div, List<Content> osisContent) {
        assert key != null;
        div.addContent(osisContent);
    }

    /**
     * Add the OSIS elements to the div element. Note, this assumes that the
     * data is fully marked up.
     * 
     * @param key
     *            The key being added
     * @param content
     *            The list to which the key's OSIS representation is being added
     * @param osisContent
     *            The OSIS representation of the key being added.
     */
    public void addOSIS(Key key, List<Content> content, List<Content> osisContent) {
        assert key != null;
        content.addAll(osisContent);
    }

    /**
     * What filter should be used to filter data in the format produced by this
     * Book?. In some ways this method is more suited to BookMetaData however we
     * do not have a specialization of BookMetaData to fit AbstractPassageBook
     * and it doesn't like any higher in the hierarchy at the moment so I will
     * leave this here.
     */
    protected abstract Filter getFilter();

    /**
     * For when we want to add writing functionality. This does not work.
     * 
     * @param key
     * @param bdata
     * @throws BookException
     */
    public void setDocument(Key key, BookData bdata) throws BookException {
        // For all of the sections
        for (Content nextElem : OSISUtil.getFragment(bdata.getOsisFragment())) {
            if (nextElem instanceof Element) {
                Element div = (Element) nextElem;

                // For all of the Verses in the section
                for (Content data : (List<Content>) div.getContent()) {
                    if (data instanceof Element) {
                        Element overse = (Element) data;
                        String text = OSISUtil.getPlainText(overse);

                        setRawText(key, text);
                    } else {
                        log.error("Ignoring non OSIS/Verse content of DIV.");
                    }
                }
            } else {
                log.error("Ignoring non OSIS/Verse content of DIV.");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#isWritable()
     */
    public boolean isWritable() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#createEmptyKeyList()
     */
    public final Key createEmptyKeyList() {
        return keyf.createEmptyKeyList(Versifications.instance().getVersification(versification));
    }


    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getValidKey(java.lang.String)
     */
    public Key getValidKey(String name) {
        try {
            return getKey(name);
        } catch (NoSuchKeyException e) {
            return createEmptyKeyList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public final Key getKey(String text) throws NoSuchKeyException {
        return PassageKeyFactory.instance().getKey(Versifications.instance().getVersification(versification), text);
    }
    

    public Versification getVersification() {
        if (this.versificationSystem == null) {
            this.versificationSystem = Versifications.instance().getVersification((String) getBookMetaData().getProperty(BookMetaData.KEY_VERSIFICATION));
        }
        return versificationSystem;
    }

    /**
     * The name of the versification or null
     */
    private String versification;

    /**
     * Versification system, created lazily, so use getter
     */
    private Versification versificationSystem;

    /**
     * Our key manager
     */
    private PassageKeyFactory keyf = PassageKeyFactory.instance();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AbstractPassageBook.class);

}
