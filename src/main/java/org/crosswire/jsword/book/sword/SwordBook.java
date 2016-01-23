/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2005 - 2016
 *
 */
package org.crosswire.jsword.book.sword;

import java.util.List;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.VerseKey;
import org.crosswire.jsword.versification.Versification;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SwordBook is a base class for all verse based Sword type books.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class SwordBook extends AbstractPassageBook {
    /**
     * Construct an SwordBook given the BookMetaData and the AbstractBackend.
     * 
     * @param sbmd the metadata that describes the book
     * @param backend the means by which the resource is accessed
     */
    public SwordBook(SwordBookMetaData sbmd, Backend<?> backend) {
        super(sbmd, backend);

        this.filter = sbmd.getFilter();

        if (backend == null) {
            throw new IllegalArgumentException("AbstractBackend must not be null.");
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getGlobalKeyList()
     */
    public final Key getGlobalKeyList() {
        if (global == null) {
            try {
                global = getBackend().getGlobalKeyList();
                return global;
            } catch (UnsupportedOperationException ex) {
                // fail silently, operation not supported by the backend
                log.debug(ex.getMessage());
            } catch (BookException ex) {
                // failing silently, as previous behavior was to attempt to
                // return as much as we can using the slower method
                log.debug(ex.getMessage());
            }

            Versification v11n = super.getVersification();

            global = super.createEmptyKeyList();
            Key all = PassageKeyFactory.instance().getGlobalKeyList(v11n);

            for (Key key : all) {
                if (contains(key)) {
                    global.addAll(key);
                }
            }
        }

        return global;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractBook#getScope()
     */
    @Override
    public Key getScope() {
        SwordBookMetaData sbmd = (SwordBookMetaData) getBookMetaData();
        //if the book type doesn't have verses, then leave it.
        if (sbmd.getProperty(BookMetaData.KEY_VERSIFICATION) == null) {
            //then we're not looking at a versified book
            return null;
        }

        Object keyString = sbmd.getProperty(BookMetaData.KEY_SCOPE);

        if (keyString != null) {
            try {
                return getKey((String) keyString);
            } catch (NoSuchKeyException ex) {
                //the scope defined is not correct
                log.error("Unable to parse scope from book", ex);
                return null;
            }
        }

        //need to calculate the scope
        //now comes the expensive part
        Key bookKeys = getGlobalKeyList();

        //this is practically impossible, but cater for it just in case.
        if (!(bookKeys instanceof VerseKey)) {
            log.error("Global key list isn't a verse key. A very expensive no-op has just occurred.");
            return null;
        }

        return bookKeys;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        return getBackend().contains(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key) throws BookException {
        return getBackend().getRawText(key);
    }

    @Override
    protected List<Content> getOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
        List<Content> result = getBackend().readToOsis(key, processor);
        assert result != null;
        return result;
    }

    @Override
    public void addOSIS(Key key, Element div, List<Content> osisContent) {
        // See if the text is marked up with verses
        // If it is then just add it.
        for (Content content : osisContent) {
            if (content instanceof Element) {
                Element ele = (Element) content;
                if (ele.getName().equals(OSISUtil.OSIS_ELEMENT_VERSE)) {
                    super.addOSIS(key, div, osisContent);
                    return;
                }
            }
        }

        // If we get here then the text is not marked up with verse
        // In this case we add the verse markup, if the verse is not 0.
        if (KeyUtil.getVerse(key).getVerse() == 0) {
            super.addOSIS(key, div, osisContent);
        } else {
            // In a SWORD module, the verse element is to be put
            // after the "preverse" material.
            Element everse = OSISUtil.factory().createVerse();
            everse.setAttribute(OSISUtil.OSIS_ATTR_OSISID, key.getOsisID());
            div.addContent(everse);
            super.addOSIS(key, everse, osisContent);
        }
    }

    @Override
    public void addOSIS(Key key, List<Content> contentList, List<Content> osisContent) {
        // If there is no content, then there is nothing to do.
        if (osisContent.size() == 0) {
            return;
        }

        // Note: Verse 0 is an introduction and not a verse so it never gets verse markup.
        // However, it should be wrapped by a div as it should be isolated as an intro.
        if (KeyUtil.getVerse(key).getVerse() == 0) {
            Element div = OSISUtil.factory().createDiv();
            div.setAttribute(OSISUtil.OSIS_ATTR_OSISID, key.getOsisID());
            // Mark it as generated
            div.setAttribute(OSISUtil.OSIS_ATTR_TYPE, OSISUtil.GENERATED_CONTENT);
            // Put the OSIS into the div
            div.addContent(osisContent);
            // Then put the div at the end of the contentList
            contentList.add(div);
            return;
        }

        // SWORD modules typically do not have the verse marker. When they don't
        // then the verse element needs to wrap the verse content.
        // However, the verse content may have "pre-verse" content.
        // This is marked up in one of two ways:
        // 1) old way
        //      <title subType="x-preverse">...</title>
        //    There may be more than one title marked as such.
        // 2) current way
        //      <div sID="xxx" type="x-milestone" subType="x-preverse"/>
        //         ...
        //      <div eID="xxx" type="x-milestone" subType="x-preverse"/>
        //      verse content
        //    In this we only need to look for the ending.
        // The critical observation is that the verse marker is to
        // follow the last element marked x-preverse.
        // Also, there are a good number of modules that have a title marked
        // type="psalm" and not canonical="true" which they should be.

        // See if the text is marked up with verse elements
        // If it is then just add it.
        int start = 0;
        int found = -1;
        boolean wrapped = false;
        Element preverse = null;
        for (Content content : osisContent) {
            if (content instanceof Element) {
                Element ele = (Element) content;
                String name = ele.getName();
                if (OSISUtil.OSIS_ELEMENT_VERSE.equals(name)) {
                    wrapped = true;
                    continue;
                }
                Attribute typeAttr = ele.getAttribute(OSISUtil.OSIS_ATTR_TYPE);
                Attribute subTypeAttr = ele.getAttribute(OSISUtil.OSIS_ATTR_SUBTYPE);
                if (subTypeAttr != null && "x-preverse".equals(subTypeAttr.getValue())) {
                    if (OSISUtil.OSIS_ELEMENT_DIV.equals(name) || OSISUtil.OSIS_ELEMENT_TITLE.equals(name)) {
                        preverse = ele;
                        found = start;
                    }
                } else if (typeAttr != null && "psalm".equals(typeAttr.getValue()) && OSISUtil.OSIS_ELEMENT_TITLE.equals(name)) {
                    // Psalm titles should be both canonical and preverse
                    // set the appropriate attributes if not already set.
                    Attribute canonicalAttr = ele.getAttribute(OSISUtil.OSIS_ATTR_CANONICAL);
                    if (canonicalAttr == null) {
                        ele.setAttribute(OSISUtil.OSIS_ATTR_CANONICAL, "true");
                    }
                    if (subTypeAttr == null) {
                        ele.setAttribute(OSISUtil.OSIS_ATTR_SUBTYPE, "x-preverse");
                        preverse = ele;
                        found = start;
                    }
                }
            }
            start++;
        }

        // Check to see whether the text is marked up with verse
        if (wrapped) {
            super.addOSIS(key, contentList, osisContent);
            return;
        }

        // If we get here then the text is not marked up with verse
        // In this case we add the verse markup, if the verse is not 0.
        Element everse = OSISUtil.factory().createVerse();
        everse.setAttribute(OSISUtil.OSIS_ATTR_OSISID, key.getOsisID());
        if (preverse == null) {
            everse.addContent(osisContent);
        } else {
            List<Content> sublist = osisContent.subList(found + 1, osisContent.size());
            everse.addContent(sublist);
            // a sub list is actually part of the original list
            // clearing it removes it from the original list
            sublist.clear();
            // Now append shortened list
            super.addOSIS(key, contentList, osisContent);
        }
        // Then put the verse at the end of the contentList
        contentList.add(everse);
    }

    @Override
    public boolean isWritable() {
        return getBackend().isWritable();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public void setRawText(Key key, String rawData) throws BookException {
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only."));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(Key alias, Key source) throws BookException {
        getBackend().setAliasKey(alias, source);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getFilter()
     */
    @Override
    protected SourceFilter getFilter() {
        return filter;
    }

    /**
     * The filter to use to convert to OSIS.
     */
    private SourceFilter filter;

    /**
     * A cached representation of the global key list.
     */
    private Key global;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(SwordBook.class);
}
