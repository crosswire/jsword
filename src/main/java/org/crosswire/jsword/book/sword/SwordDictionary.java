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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.jsword.JSOtherMsg;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.filter.SourceFilter;
import org.crosswire.jsword.book.sword.processing.RawTextToXmlProcessor;
import org.crosswire.jsword.passage.DefaultKeyList;
import org.crosswire.jsword.passage.DefaultLeafKeyList;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom2.Content;
import org.jdom2.Element;

/**
 * A Sword version of Dictionary.
 * 
 * @see gnu.lgpl.License The GNU Lesser General Public License for details.
 * @author Joe Walker
 */
public class SwordDictionary extends AbstractBook {
    /**
     * Construct an SwordDictionary given the BookMetaData and the AbstractBackend.
     * 
     * @param sbmd the metadata that describes the book
     * @param backend the means by which the resource is accessed
     */
    protected SwordDictionary(SwordBookMetaData sbmd, Backend backend) {
        super(sbmd, backend);

        if (!(backend instanceof AbstractKeyBackend)) {
            throw new IllegalArgumentException("AbstractBackend must be an AbstractKeyBackened");
        }

        this.filter = sbmd.getFilter();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getOsisIterator(org.crosswire.jsword.passage.Key, boolean, boolean)
     */
    public Iterator<Content> getOsisIterator(final Key key, boolean allowEmpty, final boolean allowGenTitles) throws BookException {
        assert key != null;

        List<Content> content = new ArrayList<Content>();
        Element title = OSISUtil.factory().createGeneratedTitle();
        title.addContent(key.getName());
        content.add(title);

        String txt = getBackend().getRawText(key);
        List<Content> osisContent = filter.toOSIS(this, key, txt);
        content.addAll(osisContent);

        return content.iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key) throws BookException {
        return getBackend().getRawText(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        Backend backend = getBackend();
        return backend != null && backend.contains(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getRawText(org.crosswire.jsword.passage.Key)
     */
    @Override
    public List<Content> getOsis(Key key, RawTextToXmlProcessor processor) throws BookException {
        assert key != null;

        return getBackend().readToOsis(key, processor);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#isWritable()
     */
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
        throw new BookException(JSOtherMsg.lookupText("This Book is read-only."));
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getGlobalKeyList()
     */
    public Key getGlobalKeyList() {
        return (AbstractKeyBackend) getBackend();
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
        AbstractKeyBackend keyBackend = (AbstractKeyBackend) getBackend();

        int pos = keyBackend.indexOf(new DefaultLeafKeyList(text));
        if (pos < 0) {
            if (keyBackend.getCardinality() > -pos - 1) {
                return keyBackend.get(-pos - 1);
            }
            return keyBackend.get(keyBackend.getCardinality() - 1);
        }
        return keyBackend.get(pos);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#createEmptyKeyList()
     */
    public Key createEmptyKeyList() {
        return new DefaultKeyList();
    }

    /**
     * The filter to use to convert to OSIS.
     */
    private SourceFilter filter;

}
