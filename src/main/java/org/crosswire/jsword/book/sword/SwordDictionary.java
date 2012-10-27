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
package org.crosswire.jsword.book.sword;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.IOUtil;
import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.book.sword.state.OpenFileState;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom.Content;
import org.jdom.Element;

/**
 * A Sword version of Dictionary.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SwordDictionary extends AbstractBook {
    /**
     * Start and to as much checking as we can without using memory. (i.e.
     * actually reading the indexes)
     */
    protected SwordDictionary(SwordBookMetaData sbmd, AbstractBackend backend) {
        super(sbmd);

        this.backend = (AbstractKeyBackend) backend;
        this.filter = sbmd.getFilter();
        active = false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getOsisIterator(org.crosswire.jsword.passage.Key, boolean)
     */
    public Iterator<Content> getOsisIterator(final Key key, boolean allowEmpty) throws BookException {

        assert key != null;
        assert backend != null;

        List<Content> content = new ArrayList<Content>();
        Element title = OSISUtil.factory().createTitle();
        title.addContent(key.getName());
        content.add(title);
  
        
        OpenFileState state = null;
        String txt = null;
        try {
            state = backend.initState();
            txt = backend.readRawContent(state, key, key.getName());
        } catch (IOException e) {
            throw new BookException(e.getMessage(), e);
        } finally {
            IOUtil.close(state);
        }
  
        List<Content> osisContent = filter.toOSIS(this, key, txt);
        content.addAll(osisContent);
  
        return content.iterator();
    }



    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key) throws BookException {
        OpenFileState state = null;
        try {
            state = backend.initState();
            return backend.readRawContent(state, key, key.getName());
        } catch (IOException e) {
           throw new BookException("Unable to obtain raw content from backend", e);
        } finally {
            IOUtil.close(state);
        }
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
    public List<Content> getOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
        checkActive();

        assert key != null;
        assert backend != null;

        return backend.readToOsis(key, processor);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#isWritable()
     */
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
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only."));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getGlobalKeyList()
     */
    public Key getGlobalKeyList() {
        checkActive();

        return backend;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getValidKey(java.lang.String)
     */
    public Key getValidKey(String name) {
        try {
            return getKey(name);
        } catch (NoSuchKeyException e) {
            return createEmptyKeyList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getKey(java.lang.String)
     */
    public Key getKey(String text) throws NoSuchKeyException {
        checkActive();

        int pos = backend.indexOf(new DefaultLeafKeyList(text));
        if (pos < 0) {
            if (backend.getCardinality() > -pos - 1) {
                return backend.get(-pos - 1);
            }
            return backend.get(backend.getCardinality() - 1);
        }
        return backend.get(pos);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#createEmptyKeyList()
     */
    public Key createEmptyKeyList() {
        return new DefaultKeyList();
    }

    @Override
    public final void activate(Lock lock) {
        super.activate(lock);
        active = true;

        // We don't need to activate the backend because it should be capable
        // of doing it for itself.
    }

    @Override
    public final void deactivate(Lock lock) {
        super.deactivate(lock);
        active = false;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    private void checkActive() {
        if (!active) {
            Activator.activate(this);
        }
    }

    /**
     * Are we active
     */
    private boolean active;

    /**
     * To read the data from the disk
     */
    private AbstractKeyBackend backend;

    /**
     * The filter to use to convert to OSIS.
     */
    private Filter filter;

}
