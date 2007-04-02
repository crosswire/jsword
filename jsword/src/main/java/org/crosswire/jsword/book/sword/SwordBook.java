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

import java.util.Iterator;
import java.util.List;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.passage.Key;
import org.jdom.Content;
import org.jdom.Element;

/**
 * SwordBook is a base class for all sword type books.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SwordBook extends AbstractPassageBook
{
    /**
     * Simple ctor
     */
    public SwordBook(SwordBookMetaData sbmd, AbstractBackend backend)
    {
        super(sbmd);

        this.sbmd = sbmd;
        this.backend = backend;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        super.activate(lock);

        // We don't need to activate the backend because it should be capable
        // of doing it for itself.
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
        super.deactivate(lock);

        Activator.deactivate(backend);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key) throws BookException
    {
        if (backend == null)
        {
            return ""; //$NON-NLS-1$
        }

        String result = backend.getRawText(key);

        assert result != null;
        return result;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#addOSIS(org.crosswire.jsword.passage.Key, org.jdom.Element, java.util.List)
     */
    public void addOSIS(Key key, Element div, List osisContent)
    {
        // See if the text is marked up with verses
        // If it is then just add it.
        Iterator iter = osisContent.iterator();
        while (iter.hasNext())
        {
            Content content = (Content) iter.next();
            if (content instanceof Element)
            {
                Element ele = (Element) content;
                if (ele.getName().equals(OSISUtil.OSIS_ELEMENT_VERSE))
                {
                    super.addOSIS(key, div, osisContent);
                    return;
                }
            }
        }

        // If we get here then the text is not marked up with verse
        // In this case we add the verse markup.
        Element everse = OSISUtil.factory().createVerse();
        everse.setAttribute(OSISUtil.OSIS_ATTR_OSISID, key.getOsisID());
        div.addContent(everse);
        super.addOSIS(key, everse, osisContent);
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
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getFilter()
     */
    protected Filter getFilter()
    {
        return sbmd.getFilter();
    }

    /**
     * To read the data from the disk
     */
    private AbstractBackend backend;

    /**
     * The Sword configuration file
     */
    private SwordBookMetaData sbmd;
}
