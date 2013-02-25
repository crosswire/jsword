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
 * Copyright: 2005-2013
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.crosswire.common.util.IOUtil;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.versification.Versification;
import org.jdom.Content;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SwordBook is a base class for all verse based Sword type books.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SwordBook extends AbstractPassageBook {
    /**
     * Simple ctor
     */
    public SwordBook(SwordBookMetaData sbmd, AbstractBackend<?> backend) {
        super(sbmd);

        this.filter = sbmd.getFilter();
        this.backend = backend;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getGlobalKeyList()
     */
    public final Key getGlobalKeyList() {
        if (global == null) {
            try {
                global = this.backend.getGlobalKeyList();
                return global;
            } catch (UnsupportedOperationException ex) {
                // fail silently, operation not supported by the backend
                log.debug(ex.getMessage());
            } catch (BookException ex) {
                // failing silently, as previous behaviour was to attempt to
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
     * @see org.crosswire.jsword.book.Book#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        return backend != null && backend.contains(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key) throws BookException {

        OpenFileState state = null;
        try {
            state = backend.initState();
            return backend.readRawContent(state, key);
        } catch (IOException e) {
            throw new BookException("Unable to obtain raw content from backend", e);
        } finally {
            IOUtil.close(state);
        }
    }

    @Override
    protected List<Content> getOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
        if (backend == null) {
            return Collections.emptyList();
        }

        List<Content> result = backend.readToOsis(key, processor);
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
            Element everse = OSISUtil.factory().createVerse();
            everse.setAttribute(OSISUtil.OSIS_ATTR_OSISID, key.getOsisID());
            div.addContent(everse);
            super.addOSIS(key, everse, osisContent);
        }
    }

    @Override
    public void addOSIS(Key key, List<Content> contentList, List<Content> osisContent) {
        // See if the text is marked up with verses
        // If it is then just add it.
        for (Content content : osisContent) {
            if (content instanceof Element) {
                Element ele = (Element) content;
                if (ele.getName().equals(OSISUtil.OSIS_ELEMENT_VERSE)) {
                    super.addOSIS(key, contentList, osisContent);
                    return;
                }
            }
        }

        // If we get here then the text is not marked up with verse
        // In this case we add the verse markup, if the verse is not 0.
        if (KeyUtil.getVerse(key).getVerse() == 0) {
            super.addOSIS(key, contentList, osisContent);
        } else {
            Element everse = OSISUtil.factory().createVerse();
            everse.setAttribute(OSISUtil.OSIS_ATTR_OSISID, key.getOsisID());
            super.addOSIS(key, everse, osisContent);
            contentList.add(everse);
        }
    }

    @Override
    public boolean isWritable() {
        return backend.isWritable();
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
        OpenFileState state = null;
        try {
            state = backend.initState();

            backend.setAliasKey(state, alias, source);
        } catch (IOException e) {
            throw new BookException(JSOtherMsg.lookupText("Unable to save {0}.", alias.getOsisID()));
        } finally {
            IOUtil.close(state);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getFilter()
     */
    @Override
    protected Filter getFilter() {
        return filter;
    }

    /**
     * To read the data from the disk
     */
    private AbstractBackend backend;

    /**
     * The filter to use to convert to OSIS.
     */
    private Filter filter;

    /**
     * A cached representation of the global key list.
     */
    private Key global;

    /**
     * The log stream
     */
    private static final Logger log = LoggerFactory.getLogger(SwordBook.class);
}
