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
 * ID: $Id: SwordDictionary.java 1185 2006-11-13 13:32:18Z dmsmith $
 */
package org.crosswire.jsword.book.sword;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.filter.FilterException;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.ReadOnlyKeyList;
import org.jdom.Element;

/**
 * A Sword version of Dictionary.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SwordGenBook extends AbstractBook
{
    /**
     * Start and to as much checking as we can without using memory.
     * (i.e. actually reading the indexes)
     */
    protected SwordGenBook(SwordBookMetaData sbmd, AbstractBackend backend)
    {
        super(sbmd);

        this.sbmd = sbmd;
        this.backend = backend;
        map = null;
        set = null;
        global = null;
        active = false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    /* @Override */
    public final void activate(Lock lock)
    {
        super.activate(lock);

        set = backend.readIndex();

        map = new HashMap();
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            Key key = (Key) iter.next();
            map.put(key.getName(), key);
        }

        global = new ReadOnlyKeyList(set, false);

        active = true;

        // We don't need to activate the backend because it should be capable
        // of doing it for itself.
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    /* @Override */
    public final void deactivate(Lock lock)
    {
        super.deactivate(lock);

        map = null;
        set = null;
        global = null;

        Activator.deactivate(backend);

        active = false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getData(org.crosswire.jsword.passage.Key)
     */
    public BookData getText(Key key) throws BookException
    {
        checkActive();

        assert key != null;
        assert backend != null;

        try
        {
            Element osis = OSISUtil.createOsisFramework(getBookMetaData());
            Element text = osis.getChild(OSISUtil.OSIS_ELEMENT_OSISTEXT);

            Element div = OSISUtil.factory().createDiv();
            Element title = OSISUtil.factory().createTitle();
            title.addContent(key.getName());
            div.addContent(title);
            text.addContent(div);

            String txt = backend.getRawText(key);

            List osisContent = sbmd.getFilter().toOSIS(this, key, txt);
            div.addContent(osisContent);

            return new BookData(osis, this, key);
        }
        catch (FilterException ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key) throws BookException
    {
        checkActive();

        assert key != null;
        assert backend != null;

        return backend.getRawText(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#isWritable()
     */
    public boolean isWritable()
    {
        return backend.isWritable();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public void setRawText(Key key, String rawData) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(Key alias, Key source) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public Key getGlobalKeyList()
    {
        checkActive();

        return global;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#isValidKey(java.lang.String)
     */
    public Key getValidKey(String name)
    {
        try
        {
            return getKey(name);
        }
        catch (NoSuchKeyException e)
        {
            return createEmptyKeyList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public Key getKey(String text) throws NoSuchKeyException
    {
        checkActive();

        Key key = (Key) map.get(text);
        if (key != null)
        {
            return key;
        }

        // First check for keys that match ignoring case
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext())
        {
            String keyName = (String) iter.next();
            if (keyName.equalsIgnoreCase(text))
            {
                return (Key) map.get(keyName);
            }
        }

        // Next keys that start with the given text
        iter = map.keySet().iterator();
        while (iter.hasNext())
        {
            String keyName = (String) iter.next();
            if (keyName.startsWith(text))
            {
                return (Key) map.get(keyName);
            }
        }

        // Next try keys that contain the given text
        iter = map.keySet().iterator();
        while (iter.hasNext())
        {
            String keyName = (String) iter.next();
            if (keyName.indexOf(text) != -1)
            {
                return (Key) map.get(keyName);
            }
        }

        throw new NoSuchKeyException(Msg.NO_KEY, new Object[] { text, getInitials() });
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getEmptyKeyList()
     */
    public Key createEmptyKeyList()
    {
        return new DefaultKeyList();
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    private void checkActive()
    {
        if (!active)
        {
            Activator.activate(this);
        }
    }

    /**
     * The global key list
     */
    private Key global;

    /**
     * Are we active
     */
    private boolean active;

    /**
     * So we can quickly find a Key given the text for the key
     */
    private Map map;

    /**
     * So we can implement getIndex() easily
     */
    private Key set;

    /**
     * To read the data from the disk
     */
    private AbstractBackend backend;

    /**
     * The Sword configuration file
     */
    private SwordBookMetaData sbmd;
}
